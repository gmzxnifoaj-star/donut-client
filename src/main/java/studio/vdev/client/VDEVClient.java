package studio.vdev.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public class VDEVClient implements ClientModInitializer {

    // Toggles (changed in the Right Shift menu)
    public static boolean showFps = true;
    public static boolean showCps = true;
    public static boolean showKeys = true;

    // The "fake" FPS number centers around this value
    public static int fakeFpsBase = 300;

    private static KeyBinding menuKey;

    private static final Deque<Long> leftClicks = new ArrayDeque<>();
    private static final Deque<Long> rightClicks = new ArrayDeque<>();
    private static boolean leftDown, rightDown;

    private static int fpsShown = 300;
    private static long lastFpsUpdate = 0;

    private static final int BLUE = 0xFF55AAFF;
    private static final int WHITE = 0xFFFFFFFF;

    @Override
    public void onInitializeClient() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.vdevclient.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.vdevclient"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClientScreen());
                }
            }
        });

        HudRenderCallback.EVENT.register(VDEVClient::render);
    }

    private static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        long now = System.currentTimeMillis();
        long win = mc.getWindow().getHandle();

        // CPS: detect new presses every frame
        boolean l = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean r = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (l && !leftDown) leftClicks.addLast(now);
        if (r && !rightDown) rightClicks.addLast(now);
        leftDown = l;
        rightDown = r;
        while (!leftClicks.isEmpty() && now - leftClicks.peekFirst() > 1000) leftClicks.pollFirst();
        while (!rightClicks.isEmpty() && now - rightClicks.peekFirst() > 1000) rightClicks.pollFirst();

        int x = 6, y = 6;
        ctx.drawTextWithShadow(mc.textRenderer, "VDEV CLIENT", x, y, BLUE);
        y += 12;

        if (showFps) {
            if (now - lastFpsUpdate > 250) {
                lastFpsUpdate = now;
                fpsShown = fakeFpsBase + (int) (15 * Math.sin(now / 900.0)) + (int) (Math.random() * 9);
            }
            ctx.drawTextWithShadow(mc.textRenderer, "FPS: " + fpsShown, x, y, WHITE);
            y += 12;
        }

        if (showCps) {
            ctx.drawTextWithShadow(mc.textRenderer,
                    "CPS: " + leftClicks.size() + " | " + rightClicks.size(), x, y, WHITE);
            y += 12;
        }

        if (showKeys) {
            drawKeystrokes(ctx, mc, x, y + 2);
        }
    }

    private static void drawKeystrokes(DrawContext ctx, MinecraftClient mc, int x, int y) {
        int s = 22, g = 2;
        int mouseW = (3 * s + 2 * g - g) / 2;

        drawKey(ctx, mc, "W", x + s + g, y, s, s, mc.options.forwardKey.isPressed());
        drawKey(ctx, mc, "A", x, y + s + g, s, s, mc.options.leftKey.isPressed());
        drawKey(ctx, mc, "S", x + s + g, y + s + g, s, s, mc.options.backKey.isPressed());
        drawKey(ctx, mc, "D", x + 2 * (s + g), y + s + g, s, s, mc.options.rightKey.isPressed());
        drawKey(ctx, mc, "LMB", x, y + 2 * (s + g), mouseW, s, leftDown);
        drawKey(ctx, mc, "RMB", x + mouseW + g, y + 2 * (s + g), mouseW, s, rightDown);
        drawKey(ctx, mc, "SPACE", x, y + 3 * (s + g), 3 * s + 2 * g, 14, mc.options.jumpKey.isPressed());
    }

    private static void drawKey(DrawContext ctx, MinecraftClient mc, String label,
                                int x, int y, int w, int h, boolean pressed) {
        ctx.fill(x, y, x + w, y + h, pressed ? 0xCCFFFFFF : 0x80000000);
        int color = pressed ? 0xFF000000 : WHITE;
        ctx.drawCenteredTextWithShadow(mc.textRenderer, label, x + w / 2, y + (h - 8) / 2, color);
    }
}
