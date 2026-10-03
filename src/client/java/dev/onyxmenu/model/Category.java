package dev.onyxmenu.model;

public enum Category {
    COMBAT("Combat", "⚔"),
    RENDER("Render", "◉"),
    UTILITY("Utility", "◆"),
    WORLD("World", "●"),
    INVENTORY("Inventory", "▣"),
    MINIGAMES("Minigames", "♢"),
    OTHER("Other", "≡");

    private final String label;
    private final String icon;

    Category(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String label() { return label; }
    public String icon() { return icon; }
}
