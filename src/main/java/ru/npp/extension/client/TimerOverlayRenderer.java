package ru.npp.extension.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class TimerOverlayRenderer {
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!ClientTimerState.isRunning()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;

        int totalSeconds = ClientTimerState.getRemainingSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String text = String.format("Подготовка: %02d:%02d", minutes, seconds);
        int x = (event.resolution.getScaledWidth() - font.getStringWidth(text)) / 2;
        int y = 10;
        font.drawStringWithShadow(text, x, y, 0xFFFFFF);
    }
}
