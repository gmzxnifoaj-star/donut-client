package studio.vdev.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ClientScreen extends Screen {

    public ClientScreen() {
        super(Text.literal("VDEV Client"));
    }

    @Override
    protected void init() {
        int w = 160, h = 20;
        int x = width / 2 - w / 2;
        int y = height / 2 - 45;

        addDrawableChild(toggle("FPS Counter", () -> VDEVClient.showFps, v -> VDEVClient.showFps = v, x, y, w, h));
        addDrawableChild(toggle("CPS Counter", () -> VDEVClient.showCps, v -> VDEVClient.showCps = v, x, y + 25, w, h));
        addDrawableChild(toggle("Keystrokes", () -> VDEVClient.showKeys, v -> VDEVClient.showKeys = v, x, y + 50, w, h));
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(x, y + 75, w, h).build());
    }

    private ButtonWidget toggle(String name, BooleanSupplier get, Consumer<Boolean> set,
                                int x, int y, int w, int h) {
        return ButtonWidget.builder(label(name, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            b.setMessage(label(name, get.getAsBoolean()));
        }).dimensions(x, y, w, h).build();
    }

    private static Text label(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "ON" : "OFF"));
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.renderBackground(ctx, mouseX, mouseY, delta);
        int pw = 190, ph = 150;
        int x0 = width / 2 - pw / 2;
        int y0 = height / 2 - 70;
        ctx.fill(x0, y0, x0 + pw, y0 + ph, 0xCC0B1020);
        ctx.fill(x0, y0, x0 + pw, y0 + 2, 0xFF55AAFF);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "VDEV CLIENT", width / 2, height / 2 - 62, 0xFF55AAFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "Protected by VDEV Studios", width / 2, height / 2 + 62, 0xFFAAAAAA);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
