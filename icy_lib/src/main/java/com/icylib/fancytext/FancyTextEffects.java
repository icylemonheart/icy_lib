package com.icylib.fancytext;

import net.minecraft.util.Mth;

/**
 * 内置动态效果集合（静态工厂方法）。
 *
 * 所有效果均为无状态纯函数，可任意组合叠加。
 * 效果在渲染时按添加顺序逐字符应用，先添加的效果先执行。
 */
public final class FancyTextEffects {

    private FancyTextEffects() {
    }

    /** 浮动模式 */
    public enum FloatMode {
        /** 全部字符一起上下浮动 */
        ALL,
        /** 只有被高亮的部分（如条带覆盖处）浮动，实现"高亮部分浮在主体之上" */
        HIGHLIGHT_ONLY
    }

    // ==================== 条带扫过 ====================

    /**
     * 亮色条带扫过效果：一条窄条带沿文本水平移动，条带与底色之间通过渐变平滑过渡。
     *
     * @param accentColor    条带颜色（如黄色）
     * @param stripeWidth    条带宽度（像素，条带内为纯强调色）
     * @param gradientWidth  渐变过渡宽度（像素，两侧各一份）
     * @param speedPxPerSec  移动速度（像素/秒）
     * @param rightToLeft    true=从右向左移动，false=从左向右
     */
    public static TextEffect stripeSweep(int accentColor, float stripeWidth, float gradientWidth,
                                         float speedPxPerSec, boolean rightToLeft) {
        return stripeSweep(accentColor, stripeWidth, gradientWidth, speedPxPerSec, rightToLeft, 0.0F);
    }

    /** 同上，带初始相位偏移（像素），用于错开多个条带 */
    public static TextEffect stripeSweep(int accentColor, float stripeWidth, float gradientWidth,
                                         float speedPxPerSec, boolean rightToLeft, float phaseOffset) {
        int accent = ColorUtil.opaque(accentColor);
        return (ctx, env) -> {
            float inner = stripeWidth / 2.0F;
            float outer = inner + Math.max(gradientWidth, 0.01F);
            // 循环周期保证条带完整移出文本后再从另一端进入，无跳变
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
            // 记录高亮权重，供后续效果（如 HIGHLIGHT_ONLY 浮动）参考
            ctx.highlight = Math.max(ctx.highlight, weight);
            ctx.color = ColorUtil.lerpColor(ctx.color, accent, weight);
        };
    }

    // ==================== 浮动 ====================

    /**
     * 浮动效果：字符在 0 ~ -amplitude 范围内往复浮动（向上浮起再回落）。
     *
     * @param amplitudePx 浮动幅度（像素，如 2.0 即上下浮动2像素）
     * @param speedHz     浮动频率（次/秒）
     * @param mode        ALL=全部字符浮动；HIGHLIGHT_ONLY=仅高亮部分浮动（高于其余部分）
     */
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

    /**
     * 字符波浪效果：字符按位置产生正弦相位差的上下起伏（类似横幅飘动）。
     *
     * @param amplitudePx  起伏幅度（像素，上下各 amplitude）
     * @param speedHz      波浪推进频率（次/秒）
     * @param wavelengthPx 波长（像素，越大波动越平缓）
     */
    public static TextEffect wave(float amplitudePx, double speedHz, float wavelengthPx) {
        return (ctx, env) -> {
            double phase = (ctx.charX / Math.max(wavelengthPx, 0.01F)) * Math.PI * 2.0
                    + env.time() * Math.PI * 2.0 * speedHz;
            ctx.yOffset -= (float) (amplitudePx * Math.sin(phase));
        };
    }

    // ==================== 颜色脉冲 ====================

    /**
     * 颜色脉冲效果：整体颜色在底色与目标色之间往复呼吸。
     *
     * @param targetColor 峰值颜色（如白色高光）
     * @param speedHz     脉冲频率（次/秒）
     * @param minWeight   谷值时向目标色混合的最低比例（0~1，越大脉冲越弱）
     */
    public static TextEffect pulse(int targetColor, double speedHz, float minWeight) {
        int target = ColorUtil.opaque(targetColor);
        return (ctx, env) -> {
            float wave = (float) (0.5 - 0.5 * Math.cos(env.time() * Math.PI * 2.0 * speedHz));
            float weight = minWeight + (1.0F - minWeight) * wave;
            ctx.color = ColorUtil.lerpColor(ctx.color, target, weight);
        };
    }

    // ==================== 渐变 ====================

    /**
     * 字内流动渐变（参考特效文本模组算法）：相邻字符相位差 1/颜色数，
     * 每约 charsPerCycle 个字符完成一次色彩循环，整条文本连成一个流动色环。
     * 单个字符自身颜色随时间流动变化，视觉上"每个字都有渐变、整行连成一体"。
     *
     * @param colors       颜色环序列（首尾自动相接循环，至少2个）
     * @param charsPerCycle 每多少个字符完成一次完整色彩循环（推荐 2.5~4，越小单字渐变越明显）
     * @param cyclesPerSec 时间流动速度（每秒相位圈数，0.15~1.0；0 为静止的错落渐变）
     */
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

    /**
     * 多段渐变效果：沿文本方向在多个颜色间平滑过渡。
     *
     * @param colors             渐变色序列（至少1个），scroll=true 时首尾相接循环
     * @param scroll             是否滚动（渐变随时间横向流动）
     * @param scrollCyclesPerSec 滚动速度（每秒滚动完整渐变多少圈）
     */
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

    // ==================== 彩虹 ====================

    /**
     * HSV 彩虹循环：色相沿文本分布并随时间流动。
     *
     * @param saturation      饱和度 0~1
     * @param brightness      亮度 0~1
     * @param hueCyclesPerSec 色相流动速度（圈/秒，负值反向）
     * @param hueSpan         文本上色相跨度（1.0=完整彩虹，0.3=局部色域）
     */
    public static TextEffect rainbow(float saturation, float brightness, double hueCyclesPerSec, float hueSpan) {
        return (ctx, env) -> {
            float t = ctx.charX / Math.max(env.textWidth(), 1.0F);
            float hue = t * hueSpan + (float) (env.time() * hueCyclesPerSec);
            hue = hue - (float) Math.floor(hue);
            int rgb = Mth.hsvToRgb(hue, saturation, brightness);
            ctx.color = (ctx.color & 0xFF000000) | rgb;
        };
    }

    // ==================== 闪烁 ====================

    /**
     * 随机闪烁效果：每个字符独立、无规律地闪向白色（类似附魔物品的闪烁感）。
     *
     * @param seed      随机种子（不同种子闪烁节奏不同）
     * @param intensity 闪烁强度 0~1（1=完全变白）
     * @param speed     闪烁速度
     */
    public static TextEffect flicker(long seed, float intensity, double speed) {
        return (ctx, env) -> {
            double t = env.time() * speed + ctx.index * 0.6180339887498949 + seed * 0.000173;
            // 多重正弦叠加产生伪随机波形
            double n = Math.sin(t * 12.9898) * Math.sin(t * 4.1414) * Math.sin(t * 7.7777);
            float s = (float) (n * 0.5 + 0.5);
            float weight = ColorUtil.smoothstep((s - 0.62F) / 0.38F) * intensity;
            if (weight > 0.001F) {
                ctx.color = ColorUtil.lerpColor(ctx.color, (ctx.color & 0xFF000000) | 0xFFFFFF, weight);
            }
        };
    }

    // ==================== 透明度脉冲 ====================

    /**
     * 透明度脉冲效果：字符透明度往复呼吸（幽灵/若隐若现感）。
     *
     * @param speedHz  脉冲频率（次/秒）
     * @param minAlpha 谷值透明度 0~1
     */
    public static TextEffect alphaPulse(double speedHz, float minAlpha) {
        return (ctx, env) -> {
            float wave = (float) (0.5 - 0.5 * Math.cos(env.time() * Math.PI * 2.0 * speedHz));
            ctx.alpha *= minAlpha + (1.0F - minAlpha) * wave;
        };
    }
}
