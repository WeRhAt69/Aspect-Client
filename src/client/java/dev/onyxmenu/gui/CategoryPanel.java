package dev.onyxmenu.gui;

import dev.onyxmenu.model.Category;
import dev.onyxmenu.model.MenuModule;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public final class CategoryPanel {
    public static final int WIDTH = 222;
    private static final int HEADER = 38;
    private static final int ROW = 40;
    private final Category category;
    private final List<MenuModule> modules;
    private int x;
    private int y;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;
    private float openProgress;
    private boolean closing;

    public CategoryPanel(Category category, List<MenuModule> modules, int x, int y) {
        this.category = category;
        this.modules = modules;
        this.x = x;
        this.y = y;
    }

    public void render(DrawContext context, TextRenderer text, int mouseX, int mouseY, float delta) {
        float target = closing ? 0.0F : 1.0F;
        openProgress += (target - openProgress) * Math.min(1.0F, delta * 0.24F);
        float eased = 1.0F - (1.0F - openProgress) * (1.0F - openProgress) * (1.0F - openProgress);
        int height = HEADER + modules.size() * ROW;
        int visibleHeight = Math.max(1, Math.round(height * eased));
        int slideX = Math.round((1.0F - eased) * -10.0F);
        int renderX = x + slideX;
        context.enableScissor(renderX - 8, y - 8, renderX + WIDTH + 10, y + visibleHeight + 8);
        context.fill(renderX + 4, y + 5, renderX + WIDTH + 4, y + height + 5, 0x66000000);
        context.fill(renderX, y, renderX + WIDTH, y + height, 0xF61A1A1C);
        context.fill(renderX, y, renderX + WIDTH, y + HEADER, 0xFF171719);
        context.fill(renderX, y + HEADER - 1, renderX + WIDTH, y + HEADER, 0xFF27272A);
        context.drawTextWithShadow(text, category.icon(), renderX + 12, y + 14, 0xFFE0E0E2);
        context.drawTextWithShadow(text, category.label(), renderX + 32, y + 14, 0xFFD4D4D6);
        context.drawTextWithShadow(text, "⌃", renderX + WIDTH - 19, y + 14, 0xFF6F6F73);

        for (int i = 0; i < modules.size(); i++) {
            MenuModule module = modules.get(i);
            int rowY = y + HEADER + i * ROW;
            boolean hovered = inside(mouseX, mouseY, renderX, rowY, WIDTH, ROW);
            if (hovered) context.fill(renderX, rowY, renderX + WIDTH, rowY + ROW, 0xFF202023);
            float toggle = module.animateToggle(delta);
            if (toggle > 0.02F) context.fill(renderX, rowY, renderX + Math.max(1, Math.round(3 * toggle)), rowY + ROW, 0xFF00A991);
            context.drawTextWithShadow(text, module.name(), renderX + 12, rowY + 16, module.enabled() ? 0xFFE4F8F4 : 0xFFA5A5A9);
            drawToggle(context, renderX + WIDTH - 53, rowY + 14, toggle);
            context.drawTextWithShadow(text, "⋮", renderX + WIDTH - 16, rowY + 15, 0xFF6E6E72);
        }
        context.disableScissor();
    }

    private static void drawToggle(DrawContext context, int x, int y, float progress) {
        int color = blend(0xFF343438, 0xFF00A991, progress);
        context.fill(x + 3, y, x + 20, y + 12, color);
        context.fill(x, y + 3, x + 23, y + 9, color);
        int knobX = x + 2 + Math.round(11 * progress);
        context.fill(knobX, y + 2, knobX + 9, y + 10, blend(0xFF151517, 0xFFF0F7F5, progress));
    }

    private static int blend(int from, int to, float amount) {
        int r = Math.round(((from >> 16) & 255) + (((to >> 16) & 255) - ((from >> 16) & 255)) * amount);
        int g = Math.round(((from >> 8) & 255) + (((to >> 8) & 255) - ((from >> 8) & 255)) * amount);
        int b = Math.round((from & 255) + ((to & 255) - (from & 255)) * amount);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !contains(mouseX, mouseY)) return false;
        if (inside(mouseX, mouseY, x, y, WIDTH, HEADER)) {
            dragging = true;
            dragOffsetX = (int) mouseX - x;
            dragOffsetY = (int) mouseY - y;
            return true;
        }
        int index = ((int) mouseY - y - HEADER) / ROW;
        if (index >= 0 && index < modules.size()) modules.get(index).toggle();
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int screenWidth, int screenHeight) {
        if (!dragging) return false;
        int height = HEADER + modules.size() * ROW;
        x = Math.max(4, Math.min((int) mouseX - dragOffsetX, screenWidth - WIDTH - 4));
        y = Math.max(4, Math.min((int) mouseY - dragOffsetY, screenHeight - height - 4));
        return true;
    }

    public void mouseReleased() { dragging = false; }
    public void closeAnimated() { closing = true; }
    public boolean finishedClosing() { return closing && openProgress < 0.025F; }
    public boolean closing() { return closing; }
    public Category category() { return category; }
    private boolean contains(double mx, double my) { return inside(mx, my, x, y, WIDTH, HEADER + modules.size() * ROW); }
    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
