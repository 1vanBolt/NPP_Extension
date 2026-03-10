package ru.npp.extension.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import ru.npp.extension.BattlePrepMod;
import ru.npp.extension.network.PacketActionRequest;

public class PvPControlGui extends GuiScreen {
    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        int startY = this.height / 2 - 50;

        this.buttonList.add(new GuiButton(0, centerX - 110, startY, 220, 20, "1) Время: День / Ночь"));
        this.buttonList.add(new GuiButton(1, centerX - 110, startY + 24, 220, 20, "2) Режим: Выживание / Творч"));
        this.buttonList.add(new GuiButton(2, centerX - 110, startY + 48, 220, 20, "3) Сложность: Мирный / Сложный"));
        this.buttonList.add(new GuiButton(3, centerX - 110, startY + 72, 220, 20, "4) Таймер 30 мин: Старт / Стоп"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        BattlePrepMod.NETWORK.sendToServer(new PacketActionRequest(button.id));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Управление подготовкой PvP", this.width / 2, this.height / 2 - 70, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
