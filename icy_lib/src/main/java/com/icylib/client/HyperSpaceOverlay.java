package com.icylib.client;

import com.icylib.init.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

// 超算空间效果：青色全屏滤镜 + 上下暗角 + 扫描光带
@OnlyIn(Dist.CLIENT)
public final class HyperSpaceOverlay {

    private static final int TINT_RGB = 0x4AD8FF;
    private static final int TINT_ALPHA = 0x2A;
    private static final int EDGE_RGB = 0x000A1A;
    private static final int EDGE_ALPHA = 0xB4;

    private HyperSpaceOverlay() {
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui) {
            return;
        }
        MobEffectInstance effect = player.getEffect(ModEffects.HYPER_CALCULATION_SPACE);
        if (effect == null) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        float fade = Math.min(1.0f, effect.getDuration() / 20.0f);

        int tintAlpha = (int) (TINT_ALPHA * fade);
        graphics.fill(0, 0, w, h, (tintAlpha << 24) | TINT_RGB);

        int edgeAlpha = (int) (EDGE_ALPHA * fade);
        int edge = (edgeAlpha << 24) | EDGE_RGB;
        int clear = EDGE_RGB;
        int band = Math.max(1, h / 4);
        graphics.fillGradient(0, 0, w, band, edge, clear);
        graphics.fillGradient(0, h - band, w, h, clear, edge);

        long ticks = player.level().getGameTime();
        int bandHeight = 48;
        int y = (int) ((ticks * 3) % (h + bandHeight)) - bandHeight;
        int scanAlpha = (int) (0x30 * fade);
        int scan = (scanAlpha << 24) | TINT_RGB;
        graphics.fillGradient(0, y, w, y + bandHeight / 2, 0x00000000, scan);
        graphics.fillGradient(0, y + bandHeight / 2, w, y + bandHeight, scan, 0x00000000);
    }
}
