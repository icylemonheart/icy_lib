package com.icylib.fancytext;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Random;

/** 程序化星空背景渲染器：星点布局由 seed 固定，闪烁与漂移随时间变化。 */
public final class StarfieldRenderer {

    private StarfieldRenderer() {
    }

    /** 渲染星空背景；z 为绘制层级（原版 tooltip 背景传 400）。 */
    public static void render(GuiGraphics graphics, int x1, int y1, int x2, int y2, int z,
                              TooltipBackground background) {
        StarfieldConfig cfg = background.starfield();
        if (cfg == null) {
            cfg = StarfieldConfig.DEFAULT;
        }

        int w = x2 - x1;
        int h = y2 - y1;
        if (w <= 0 || h <= 0) {
            return;
        }

        graphics.fillGradient(x1, y1, x2, y2, z, background.bgTop(), background.bgBottom());

        double time = Util.getMillis() / 1000.0;
        Random random = new Random(cfg.seed());
        int count = Math.max(1, cfg.starCount());
        float minSize = Math.max(1, cfg.minSize());
        float sizeRange = Math.max(0, cfg.maxSize() - cfg.minSize());
        double twinkleRad = Math.PI * 2.0 * cfg.twinkleSpeed();

        for (int i = 0; i < count; i++) {
            float baseX = random.nextFloat() * w;
            float baseY = random.nextFloat() * h;
            float size = minSize + random.nextFloat() * sizeRange;
            float phase = random.nextFloat() * (float) Math.PI * 2.0F;
            float speedFactor = 0.5F + random.nextFloat();

            float drift = (float) ((time * cfg.driftSpeed() * speedFactor) % w);
            if (drift < 0) {
                drift += w;
            }
            float sx = baseX + drift;
            sx -= (float) Math.floor(sx / w) * w;

            int px = x1 + (int) sx;
            int py = y1 + (int) baseY;
            int s = Math.max(1, (int) size);

            double tw = Math.sin(time * twinkleRad * speedFactor + phase);
            float brightness = 1.0F - cfg.twinkleAmount() * (float) (tw * 0.5 + 0.5);
            if (brightness <= 0.02F) {
                continue;
            }
            int alpha = Math.min(255, (int) (brightness * 255.0F));
            int color = (alpha << 24) | (cfg.starColor() & 0xFFFFFF);

            graphics.fill(px, py, px + s, py + s, z, color);
        }
    }
}
