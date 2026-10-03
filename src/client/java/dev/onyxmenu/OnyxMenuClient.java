package dev.onyxmenu;

import dev.onyxmenu.config.MenuConfig;
import dev.onyxmenu.gui.OnyxMenuScreen;
import dev.onyxmenu.model.ModuleRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class OnyxMenuClient implements ClientModInitializer {
    private static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(Identifier.of("onyxmenu", "controls"));
    private static final ModuleRegistry REGISTRY = new ModuleRegistry();

    @Override
    public void onInitializeClient() {
        MenuConfig.load(REGISTRY);
        KeyBinding openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.onyxmenu.open_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) {
                if (client.currentScreen instanceof OnyxMenuScreen) client.setScreen(null);
                else client.setScreen(new OnyxMenuScreen(REGISTRY));
            }
        });
    }
}
