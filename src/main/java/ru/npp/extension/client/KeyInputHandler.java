package ru.npp.extension.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyInputHandler {
    private static final KeyBinding OPEN_GUI_KEY = new KeyBinding("key.npp_extension.open_gui", Keyboard.KEY_GRAVE, "key.categories.gameplay");

    public static void register() {
        ClientRegistry.registerKeyBinding(OPEN_GUI_KEY);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (OPEN_GUI_KEY.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new PvPControlGui());
        }
    }
}
