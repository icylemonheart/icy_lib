package com.icylib.fancytext;

import net.minecraft.util.Mth;

/** 内置动态效果集合：无状态纯函数，按添加顺序逐字符应用。 */
public final class FancyTextEffects {

    private FancyTextEffects() {
    }

    public enum FloatMode {
        ALL,
        HIGHLIGHT_ONLY
    }

    public static TextEffect stripeSweep(int accentColor, float stripeWidth, float gradientWidth,
                                         float speedPxPerSec, boolean rightToLeft) {
        return stripeSweep(accentColor, stripeWidth, gradientWidth, speedPxPerSec, rightToLeft, 0.0F);
    }

    public static TextEffect stripeSweep(int accentColor, float stripeWidth, float gradientWidth,
                                         float speedPxPerSec, boolean rightToLeft, float phaseOffset) {
        int accent = ColorUtil.opaque(accentColor);
        return (ctx, env) -> {
            float inner = stripeWidth / 2.0F;
            float outer = inner + Math.max(gradientWidth, 0.01F);
            float period = Math.max(env.textWidth(), 1.0F) + outer * 2.0F;
            float traveled = (float) ((env.time() * speedPxPerSec + phaseOffset) % period);
            if (traveled < 0) {
                traveled += period;
            }
            float center = rightToLeft ? (env.textWidth() + outer) - traveled : -outer + traveled;
            float dist = Math.abs(ctx.charX + ctx.charWidth / 2.0F - center);
            float weight;
            if (dist <= inner) {
                weight = 1.0F;
            } else if (dist >= outer) {
                weight = 0.0F;
            } else {
                weight = ColorUtil.smoothstep(1.0F - (dist - inner) / (outer - inner));
            }
            ctx.highlight = Math.max(ctx.highlight, weight);
            ctx.color = ColorUtil.lerpColor(ctx.color, accent, weight);
        };
    }

    public static TextEffect floatEffect(float amplitudePx, double speedHz, FloatMode mode) {
        return (ctx, env) -> {
            float wave = (float) (0.5 - 0.5 * Math.cos(env.time() * Math.PI * 2.0 * speedHz));
            float amount = amplitudePx * wave;
            if (mode == FloatMode.HIGHLIGHT_ONLY) {
                amount *= ctx.highlight;
            }
            ctx.yOffset -= amount;
        };
    }

    public static TextEffect wave(float amplitudePx, double speedHz, float wavelengthPx) {
        return (ctx, env) -> {
            double phase = (ctx.charX / Math.max(wavelengthPx, 0.01F)) * Math.PI * 2.0
                    + env.time() * Math.PI * 2.0 * speedHz;
            ctx.yOffset -= (float) (amplitudePx * Math.sin(phase));
        };
    }

    public static TextEffect pulse(int targetColor, double speedHz, float minWeight) {
        int target = ColorUtil.opaque(targetColor);
        return (ctx, env) -> {
            float wave = (float) (0.5 - 0.5 * Math.cos(env.time() * Math.PI * 2.0 * speedHz));
            float weight = minWeight + (1.0F - minWeight) * wave;
            ctx.color = ColorUtil.lerpColor(ctx.color, target, weight);
        };
    }

    public static TextEffect flowingGradient(int[] colors, double charsPerCycle, double cyclesPerSec) {
        int[] palette = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            palette[i] = ColorUtil.opaque(colors[i]);
        }
        return (ctx, env) -> {
            int n = palette.length;
            if (n == 0) {
                return;
            }
            if (n == 1) {
                ctx.color = palette[0];
                return;
            }
            double phase = ctx.index / Math.max(charsPerCycle, 0.5) + env.time() * cyclesPerSec;
            phase -= Math.floor(phase);
            double scaled = phase * n;
            int first = (int) Math.floor(scaled) % n;
            int second = (first + 1) % n;
            float amount = (float) (scaled - Math.floor(scaled));
            ctx.color = ColorUtil.lerpColor(palette[first], palette[second], amount);
        };
    }

    public static TextEffect gradient(int[] colors, boolean scroll, double scrollCyclesPerSec) {
        int[] palette = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            palette[i] = ColorUtil.opaque(colors[i]);
        }
        return (ctx, env) -> {
            int n = palette.length;
            if (n == 0) {
                return;
            }
            if (n == 1) {
                ctx.color = ColorUtil.lerpColor(palette[0], palette[0], 0.0F);
                return;
            }
            float t = ctx.charX / Math.max(env.textWidth(), 1.0F);
            if (scroll) {
                t += (float) (env.time() * scrollCyclesPerSec);
            }
            t = t - (float) Math.floor(t);
            float scaled = t * n;
            int seg = (int) scaled % n;
            float f = ColorUtil.smoothstep(scaled - (int) scaled);
            ctx.color = ColorUtil.lerpColor(palette[seg], palette[(seg + 1) % n], f);
        };
    }

    public static TextEffect rainbow(float saturation, float brightness, double hueCyclesPerSec, float hueSpan) {
        return (ctx, env) -> {
            float t = ctx.charX / Math.max(env.textWidth(), 1.0F);
            float hue = t * hueSpan + (float) (env.time() * hueCyclesPerSec);
            hue = hue - (float) Math.floor(hue);
            int rgb = Mth.hsvToRgb(hue, saturation, brightness);
            ctx.color = (ctx.color & 0xFF000000) | rgb;
        };
    }

    public static TextEffect flicker(long seed, float intensity, double speed) {
        return (ctx, env) -> {
            double t = env.time() * speed + ctx.index * 0.6180339887498949 + seed * 0.000173;
            double n = Math.sin(t * 12.9898) * Math.sin(t * 4.1414) * Math.sin(t * 7.7777);
            float s = (float) (n * 0.5 + 0.5);
            float weight = ColorUtil.smoothstep((s - 0.62F) / 0.38F) * intensity;
            if (weight > 0.001F) {
                ctx.color = ColorUtil.lerpColor(ctx.color, (ctx.color & 0xFF000000) | 0xFFFFFF, weight);
            }
        };
    }

    public static TextEffect alphaPulse(double speedHz, float minAlpha) {
        return (ctx, env) -> {
            float wave = (float) (0.5 - 0.5 * Math.cos(env.time() * Math.PI * 2.0 * speedHz));
            ctx.alpha *= minAlpha + (1.0F - minAlpha) * wave;
        };
    }
}
