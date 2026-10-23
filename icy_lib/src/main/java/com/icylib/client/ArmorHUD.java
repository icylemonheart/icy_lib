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

/**
 * 护甲 HUD 渲染器。
 *
 * 显示规则：
 * - 深黄色字体（#996600, RGB 153,102,0）
 * - 位于 {@link ShieldHUD} 上方（ShieldHUD y=screenHeight-60，本 HUD y=screenHeight-84）
 * - 与 ShieldHUD 视觉层级保持一致：文字 + 进度条
 *
 * 仅在客户端渲染，仅当护甲值 > 0 且上限 > 0 时显示。
 */
@EventBusSubscriber(modid = "icy_lib", value = Dist.CLIENT)
public class ArmorHUD {

    /** 深黄色 RGB（153,102,0），用于文字与进度条 */
    private static final int COLOR_DARK_YELLOW = 0x996600;
    /** 半透明背景色：80 alpha + 深黄 RGB */
    private static final int COLOR_BG = 0x80996600;

    /** 与 ShieldHUD 的垂直间距，确保不重叠 */
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
        // 文字背景
        guiGraphics.fill(x - 2, y - 2, x + textWidth + 2, y + textHeight + 2, COLOR_BG);
        guiGraphics.drawString(font, armorText, x, y, COLOR_DARK_YELLOW, false);

        // 进度条
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
