package com.icylib.client;

import com.icylib.shield.ShieldSystem;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "icy_lib", value = Dist.CLIENT)
public class ShieldHUD {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        float shieldAmount = ShieldSystem.getShield(player);
        float maxShield = ShieldSystem.getMaxShield(player);

        if (shieldAmount > 0 && maxShield > 0) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            Window window = minecraft.getWindow();
            Font font = minecraft.font;

            int screenWidth = window.getGuiScaledWidth();
            int screenHeight = window.getGuiScaledHeight();

            String shieldText = "护盾: " + String.format("%.0f", shieldAmount) + " / " + String.format("%.0f", maxShield);

            int textWidth = font.width(shieldText);
            int textHeight = font.lineHeight;

            int x = (screenWidth - textWidth) / 2;
            int y = screenHeight - 60;

            RenderSystem.enableBlend();
            guiGraphics.fill(x - 2, y - 2, x + textWidth + 2, y + textHeight + 2, 0x80000000);
            guiGraphics.drawString(font, shieldText, x, y, 0x00BFFF, false);

            float shieldPercent = shieldAmount / maxShield;
            int barWidth = 100;
            int barHeight = 6;
            int barX = (screenWidth - barWidth) / 2;
            int barY = y + textHeight + 4;

            guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0x80000000);
            guiGraphics.fill(barX, barY, barX + (int) (barWidth * shieldPercent), barY + barHeight, 0x00BFFF);

            RenderSystem.disableBlend();
        }
    }
}
