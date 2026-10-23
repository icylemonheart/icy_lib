package com.icylib.client.upgrade;

import com.icylib.menu.UpgradeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public interface IUpgradeScreenRenderer {
    void renderBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, float partialTick);

    void renderAdditional(GuiGraphics guiGraphics, AbstractContainerScreen<UpgradeMenu> screen, float partialTick, int mouseX, int mouseY);

    default void init(AbstractContainerScreen<UpgradeMenu> screen) {}
}
