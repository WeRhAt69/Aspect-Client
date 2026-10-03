package dev.onyxmenu.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ModuleRegistry {
    private final Map<Category, List<MenuModule>> modules = new EnumMap<>(Category.class);

    public ModuleRegistry() {
        modules.put(Category.COMBAT, list("Aim Assist", "Auto Clicker", "Block Hit", "Hit Select", "Reach Display", "Sprint", "Velocity HUD", "W-Tap Trainer"));
        modules.put(Category.RENDER, list("Animations", "Armor HUD", "Block Overlay", "Full Bright", "Hit Color", "Name Tags", "Particles"));
        modules.put(Category.UTILITY, list("Auto GG", "Chat Filter", "Clock", "Coordinates", "FPS Display", "Ping Display"));
        modules.put(Category.WORLD, list("Biome Info", "Chunk Borders", "Light Level", "Time Display", "Waypoints"));
        modules.put(Category.INVENTORY, list("Armor Status", "Item Counter", "Potion Status", "Quick Drop"));
        modules.put(Category.MINIGAMES, list("Bed Counter", "Game Timer", "Team Display"));
        modules.put(Category.OTHER, list("CPS Display", "Keystrokes", "Memory Usage", "Session Info"));
    }

    private static List<MenuModule> list(String... names) {
        return java.util.Arrays.stream(names)
            .map(name -> new MenuModule(name.toLowerCase().replace(" ", "_").replace("-", "_"), name))
            .toList();
    }

    public List<MenuModule> get(Category category) { return modules.get(category); }
    public Map<Category, List<MenuModule>> all() { return modules; }
}
