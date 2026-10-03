package dev.onyxmenu.gui;

import dev.onyxmenu.config.MenuConfig;
import dev.onyxmenu.model.Category;
import dev.onyxmenu.model.ModuleRegistry;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class OnyxMenuScreen extends Screen {
    private static final int MAIN_WIDTH = 220;
    private static final int HEADER = 38;
    private static final int ROW = 36;
    private final ModuleRegistry registry;
    private final List<CategoryPanel> openPanels = new ArrayList<>();
    private int mainX = 24;
    private int mainY = 24;
    private boolean draggingMain;
    private int dragOffsetX;
    private int dragOffsetY;
    private float openProgress;

    public OnyxMenuScreen(ModuleRegistry registry) {
        super(Text.literal("Aspect Client"));
        this.registry = registry;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        openProgress += (1.0F - openProgress) * Math.min(1.0F, delta * 0.22F);
        renderMain(context, mouseX, mouseY, openProgress);
        for (CategoryPanel panel : openPanels) panel.render(context, textRenderer, mouseX, mouseY, delta);
        openPanels.removeIf(CategoryPanel::finishedClosing);
    }

    private void renderMain(DrawContext context, int mouseX, int mouseY, float progress) {
        float eased = 1.0F - (1.0F - progress) * (1.0F - progress) * (1.0F - progress);
        int height = HEADER + Category.values().length * ROW + 146;
        int visibleHeight = Math.max(1, Math.round(height * eased));
        context.enableScissor(mainX - 8, mainY - 8, mainX + MAIN_WIDTH + 10, mainY + visibleHeight + 8);
        context.fill(mainX + 4, mainY + 5, mainX + MAIN_WIDTH + 4, mainY + height + 5, 0x66000000);
        context.fill(mainX, mainY, mainX + MAIN_WIDTH, mainY + height, 0xF61A1A1C);
        context.fill(mainX, mainY, mainX + MAIN_WIDTH, mainY + HEADER, 0xFF151517);
        context.drawTextWithShadow(textRenderer, "ASPECT", mainX + 13, mainY + 14, 0xFFF2F2F3);
        context.drawTextWithShadow(textRenderer, "CLIENT", mainX + 57, mainY + 14, 0xFF00A991);
        context.drawTextWithShadow(textRenderer, "⚙", mainX + MAIN_WIDTH - 20, mainY + 14, 0xFF858589);

        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            int rowY = mainY + HEADER + i * ROW;
            boolean open = isOpen(category);
            boolean hovered = inside(mouseX, mouseY, mainX, rowY, MAIN_WIDTH, ROW);
            if (open || hovered) context.fill(mainX, rowY, mainX + MAIN_WIDTH, rowY + ROW, open ? 0xFF202521 : 0xFF202023);
            context.drawTextWithShadow(textRenderer, category.icon(), mainX + 14, rowY + 14, open ? 0xFF00A991 : 0xFF9A9A9E);
            context.drawTextWithShadow(textRenderer, category.label(), mainX + 38, rowY + 14, open ? 0xFF00B89D : 0xFFAAAAAE);
            context.drawTextWithShadow(textRenderer, open ? "‹" : "›", mainX + MAIN_WIDTH - 17, rowY + 14, 0xFF717175);
        }
        int footerY = mainY + HEADER + categories.length * ROW;
        context.fill(mainX, footerY, mainX + MAIN_WIDTH, footerY + 25, 0xFF151517);
        context.drawTextWithShadow(textRenderer, "MISC", mainX + 14, footerY + 9, 0xFF656569);
        String[] misc = {"Friends", "Profiles", "Macros"};
        for (int i = 0; i < misc.length; i++) {
            int rowY = footerY + 25 + i * 34;
            boolean hovered = inside(mouseX, mouseY, mainX, rowY, MAIN_WIDTH, 34);
            if (hovered) context.fill(mainX, rowY, mainX + MAIN_WIDTH, rowY + 34, 0xFF202023);
            context.drawTextWithShadow(textRenderer, misc[i], mainX + 14, rowY + 13, 0xFFA0A0A4);
            context.drawTextWithShadow(textRenderer, "›", mainX + MAIN_WIDTH - 17, rowY + 13, 0xFF67676B);
        }
        int bottomY = footerY + 127;
        context.fill(mainX, bottomY, mainX + MAIN_WIDTH, bottomY + 1, 0xFF262629);
        context.drawTextWithShadow(textRenderer, "●", mainX + 14, bottomY + 10, 0xFF858589);
        context.drawTextWithShadow(textRenderer, "ASPECT  1.0", mainX + MAIN_WIDTH - 76, bottomY + 10, 0xFF5F5F63);
        context.disableScissor();
    }

    private boolean isOpen(Category category) {
        return openPanels.stream().anyMatch(panel -> panel.category() == category);
    }

    private void toggle(Category category) {
        CategoryPanel existing = openPanels.stream().filter(panel -> panel.category() == category).findFirst().orElse(null);
        if (existing != null) {
            existing.closeAnimated();
            return;
        }
        int x = Math.min(mainX + MAIN_WIDTH + 16 + openPanels.size() * 18, Math.max(4, width - CategoryPanel.WIDTH - 4));
        int y = Math.min(mainY + openPanels.size() * 18, Math.max(4, height - 380));
        openPanels.add(new CategoryPanel(category, registry.get(category), x, y));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        for (int i = openPanels.size() - 1; i >= 0; i--) {
            CategoryPanel panel = openPanels.get(i);
            if (!panel.closing() && panel.mouseClicked(mouseX, mouseY, button)) return true;
        }
        if (button == 0 && inside(mouseX, mouseY, mainX, mainY, MAIN_WIDTH, HEADER)) {
            draggingMain = true;
            dragOffsetX = (int) mouseX - mainX;
            dragOffsetY = (int) mouseY - mainY;
            return true;
        }
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            int rowY = mainY + HEADER + i * ROW;
            if (button == 0 && inside(mouseX, mouseY, mainX, rowY, MAIN_WIDTH, ROW)) {
                toggle(categories[i]);
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (draggingMain) {
            int menuHeight = HEADER + Category.values().length * ROW + 146;
            mainX = Math.max(4, Math.min((int) mouseX - dragOffsetX, width - MAIN_WIDTH - 4));
            mainY = Math.max(4, Math.min((int) mouseY - dragOffsetY, height - menuHeight - 4));
            return true;
        }
        for (CategoryPanel panel : openPanels) {
            if (panel.mouseDragged(mouseX, mouseY, width, height)) return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        draggingMain = false;
        openPanels.forEach(CategoryPanel::mouseReleased);
        return super.mouseReleased(click);
    }

    @Override
    public void close() {
        MenuConfig.save(registry);
        super.close();
    }

    @Override
    public boolean shouldPause() { return false; }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
