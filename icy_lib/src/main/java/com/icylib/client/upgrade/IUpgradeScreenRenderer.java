package com.icylib.client.upgrade;

import com.icylib.menu.UpgradeMenu;
import com.icylib.upgrade.IUpgradeableItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;

public interface IUpgradeScreenRenderer {
    void renderBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, float partialTick);
    
    void renderAdditional(GuiGraphics guiGraphics, AbstractContainerScreen<UpgradeMenu> screen, float partialTick, int mouseX, int mouseY);
    
    default void init(AbstractContainerScreen<UpgradeMenu> screen) {}
}
