package com.icylib.fancytext;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Random;

/**
 * 程序化星空背景渲染器。
 *
 * 在指定矩形内绘制：深色渐变底色 + 确定性分布的星点（闪烁 + 横向漂移）。
 * 星点位置由 {@link StarfieldConfig#seed} 固定，保证每帧布局一致、仅动画变化。
 *
 * 所有绘制使用带 z 参数的 fill/fillGradient，与原版 tooltip 背景的 z 层级一致。
 */
public final class StarfieldRenderer {

    private StarfieldRenderer() {
    }

    /**
     * 渲染星空背景（含底色渐变与星点）。
     *
     * @param z 绘制层级（原版 tooltip 背景传 400）
     */
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

        // 1. 底色垂直渐变（带 z）
        graphics.fillGradient(x1, y1, x2, y2, z, background.bgTop(), background.bgBottom());

        // 2. 星点
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
            float speedFactor = 0.5F + random.nextFloat(); // 每颗星速度/相位略有差异

            // 横向漂移（循环回绕）
            float drift = (float) ((time * cfg.driftSpeed() * speedFactor) % w);
            if (drift < 0) {
                drift += w;
            }
            float sx = baseX + drift;
            sx -= (float) Math.floor(sx / w) * w;

            int px = x1 + (int) sx;
            int py = y1 + (int) baseY;
            int s = Math.max(1, (int) size);

            // 闪烁：tw 在 [-1, 1]，brightness 在 [1-amount, 1]
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
