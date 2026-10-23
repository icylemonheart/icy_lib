package com.icylib.client.upgrade;

import com.icylib.menu.UpgradeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class DefaultUpgradeScreenRenderer implements IUpgradeScreenRenderer {
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, float partialTick) {
        guiGraphics.fill(x, y, x + width, y + height, 0x80000000);
        guiGraphics.fill(x + 54, y + 50, x + width - 8, y + height - 60, 0x40404040);
    }

    @Override
    public void renderAdditional(GuiGraphics guiGraphics, AbstractContainerScreen<UpgradeMenu> screen, float partialTick, int mouseX, int mouseY) {}
}
