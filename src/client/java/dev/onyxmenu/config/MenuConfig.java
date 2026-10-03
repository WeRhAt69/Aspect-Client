package dev.onyxmenu.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.onyxmenu.model.Category;
import dev.onyxmenu.model.ModuleRegistry;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MenuConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("aspect-client.json");

    private MenuConfig() {}

    public static void load(ModuleRegistry registry) {
        if (!Files.exists(PATH)) return;
        try {
            JsonObject root = GSON.fromJson(Files.readString(PATH), JsonObject.class);
            if (root == null || !root.has("modules")) return;
            JsonObject states = root.getAsJsonObject("modules");
            registry.all().values().stream().flatMap(java.util.Collection::stream).forEach(module -> {
                if (states.has(module.id())) module.setEnabled(states.get(module.id()).getAsBoolean());
            });
        } catch (Exception ignored) {
            // Invalid configuration falls back to safe defaults.
        }
    }

    public static void save(ModuleRegistry registry) {
        JsonObject root = new JsonObject();
        JsonObject states = new JsonObject();
        registry.all().values().stream().flatMap(java.util.Collection::stream)
            .forEach(module -> states.addProperty(module.id(), module.enabled()));
        root.add("modules", states);
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(root));
        } catch (IOException ignored) {
            // The menu remains usable if configuration cannot be written.
        }
    }
}
