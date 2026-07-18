package com.icylib.client.upgrade;

import com.icylib.menu.UpgradeMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

public class DefaultUpgradeScreenRenderer implements IUpgradeScreenRenderer {
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, float partialTick) {
        guiGraphics.fill(x, y, x + width, y + height, 0x80000000);
        guiGraphics.fill(x + 54, y + 80, x + 122, y + 134, 0x40404040);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("Breakthrough Materials").withStyle(ChatFormatting.GOLD), x + 70, y + 72, 4210752, false);
    }

    @Override
    public void renderAdditional(GuiGraphics guiGraphics, AbstractContainerScreen<UpgradeMenu> screen, float partialTick, int mouseX, int mouseY) {}
}
