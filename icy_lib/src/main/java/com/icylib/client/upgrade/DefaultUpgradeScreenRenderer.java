package com.icylib.client.upgrade;

import com.icylib.menu.UpgradeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

/** 默认升级界面背景（全屏竖直渐变），可通过 UpgradeScreenRendererManager 替换。 */
public class DefaultUpgradeScreenRenderer implements IUpgradeScreenRenderer {
    private static final int COLOR_TOP = 0xCC0A0F14;
    private static final int COLOR_BOTTOM = 0xE0141A21;

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, float partialTick) {
        guiGraphics.fillGradient(x, y, x + width, y + height, COLOR_TOP, COLOR_BOTTOM);
    }

    @Override
    public void renderAdditional(GuiGraphics guiGraphics, AbstractContainerScreen<UpgradeMenu> screen, float partialTick, int mouseX, int mouseY) {}
}
