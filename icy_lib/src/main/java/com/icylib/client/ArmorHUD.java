package com.icylib.client;

import com.icylib.armor.ArmorSystem;
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

// 装甲 HUD：深黄色（#996600），位于 ShieldHUD 上方 24px（y = screenHeight-84）
@EventBusSubscriber(modid = "icy_lib", value = Dist.CLIENT)
public class ArmorHUD {

    private static final int COLOR_DARK_YELLOW = 0x996600;
    private static final int COLOR_BG = 0x80996600;

    private static final int SHIELD_HUD_Y = 60;
    private static final int VERTICAL_GAP = 24;
    private static final int Y_OFFSET_FROM_BOTTOM = SHIELD_HUD_Y + VERTICAL_GAP;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        float armorAmount = ArmorSystem.getArmor(player);
        float maxArmor = ArmorSystem.getMaxArmor(player);

        if (armorAmount <= 0 || maxArmor <= 0) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Window window = minecraft.getWindow();
        Font font = minecraft.font;

        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        String armorText = "护甲: " + String.format("%.0f", armorAmount) + " / " + String.format("%.0f", maxArmor);

        int textWidth = font.width(armorText);
        int textHeight = font.lineHeight;

        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight - Y_OFFSET_FROM_BOTTOM;

        RenderSystem.enableBlend();
        guiGraphics.fill(x - 2, y - 2, x + textWidth + 2, y + textHeight + 2, COLOR_BG);
        guiGraphics.drawString(font, armorText, x, y, COLOR_DARK_YELLOW, false);

        float armorPercent = armorAmount / maxArmor;
        int barWidth = 100;
        int barHeight = 6;
        int barX = (screenWidth - barWidth) / 2;
        int barY = y + textHeight + 4;

        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0x80000000);
        guiGraphics.fill(barX, barY, barX + (int) (barWidth * armorPercent), barY + barHeight, COLOR_DARK_YELLOW);

        RenderSystem.disableBlend();
    }
}
