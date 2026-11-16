package studio.vdev.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClientScreen extends Screen {

    private static final int PW = 420, PH = 255;
    private static final int BLUE1 = 0xFF2563EB;
    private static final int BLUE2 = 0xFF38BDF8;
    private static final int GRAY = 0xFF8FA3C0;

    private static final String[] NAMES = {
            "FPS Counter", "CPS Counter", "Keystrokes", "RGB Hotbar",
            "Server IP", "TPS Counter", "Player Counter"};
    private static final String[] DESCS = {
            "Shows your frames per second",
            "Clicks per second (left | right)",
            "WASD, mouse and space overlay",
            "Rainbow glow around your hotbar",
            "Shows the server you are on",
            "Estimated server ticks per second",
            "Players online in the tab list"};

    private int px, py;

    public ClientScreen() {
        super(Text.literal("VDEV Client"));
    }

    @Override
    protected void init() {
        px = (width - PW) / 2;
        py = (height - PH) / 2;
    }

    private int rowX() { return px + 128; }
    private int rowW() { return PW - 128 - 10; }
    private int rowY(int i) { return py + 50 + i * 27; }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);

        // panel + glow
        rr(ctx, px - 2, py - 2, PW + 4, PH + 4, 10, 0x3038BDF8, 0x302563EB, false);
        rr(ctx, px, py, PW, PH, 8, 0xF0101A2E, 0xF00A1020, false);

        // sidebar
        int sx = px + 8, sy = py + 8, sw = 110, sh = PH - 16;
        rr(ctx, sx, sy, sw, sh, 6, 0xCC0C1426, 0xCC0A101D, false);
        rr(ctx, sx + 8, sy + 8, 20, 20, 5, BLUE1, BLUE2, false);
        ctx.drawText(textRenderer, "V", sx + 15, sy + 14, 0xFFFFFFFF, false);
        ctx.drawText(textRenderer, "VDEV", sx + 34, sy + 9, 0xFFFFFFFF, true);
        scaled(ctx, "CLIENT", sx + 34, sy + 20, 0.7f, GRAY);

        scaled(ctx, "MODULES", sx + 10, sy + 40, 0.65f, 0xFF6B7FA0);
        rr(ctx, sx + 6, sy + 52, sw - 12, 20, 5, BLUE1, BLUE2, true);
        ctx.drawText(textRenderer, "Visuals", sx + 16, sy + 58, 0xFFFFFFFF, true);

        // bottom status card
        rr(ctx, sx + 6, sy + sh - 34, sw - 12, 28, 5, 0xFF111C33, 0xFF111C33, false);
        rr(ctx, sx + 13, sy + sh - 27, 5, 5, 2, 0xFF4
