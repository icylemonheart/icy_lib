package com.icylib.fancytext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 艺术字样式：基础颜色 + 有序效果链 + 渲染参数。
 *
 * 通过 Builder 构建，效果按添加顺序依次应用（顺序影响叠加结果）。
 *
 * 示例（天蓝色主体 + 黄色条带右向左扫过 + 条带处向上浮动2像素）：
 * <pre>{@code
 * FancyTextStyle style = FancyTextStyle.builder(0x55AAFF)
 *         .effect(FancyTextEffects.stripeSweep(0xFFDD44, 14F, 8F, 35F, true))
 *         .effect(FancyTextEffects.floatEffect(2.0F, 0.8, FancyTextEffects.FloatMode.HIGHLIGHT_ONLY))
 *         .build();
 * }</pre>
 *
 * 若设置了 {@link TooltipBackground}，则当 tooltip 文本中包含本样式的 {{...}} 标记时，
 * 整个 tooltip 面板背景会被替换为该背景（由 {@link com.icylib.mixin.TooltipBackgroundMixin} 注入）。
 */
public record FancyTextStyle(int baseColor, boolean shadow, float alpha, List<TextEffect> effects,
                             TooltipBackground tooltipBackground) {

    public FancyTextStyle {
        effects = List.copyOf(effects);
    }

    public static Builder builder(int baseColor) {
        return new Builder(baseColor);
    }

    public static final class Builder {
        private final List<TextEffect> effects = new ArrayList<>();
        private final int baseColor;
        private boolean shadow = true;
        private float alpha = 1.0F;
        private TooltipBackground tooltipBackground;

        private Builder(int baseColor) {
            // 6位颜色输入（alpha=0）时自动视为不透明
            this.baseColor = (baseColor & 0xFF000000) == 0 ? 0xFF000000 | baseColor : baseColor;
        }

        /** 追加一个效果（按顺序应用） */
        public Builder effect(TextEffect effect) {
            this.effects.add(effect);
            return this;
        }

        /** 是否带阴影 */
        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        /** 全局透明度 0~1 */
        public Builder alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * 设置该样式对应的 tooltip 面板背景。
         * 当 tooltip 文本中包含本样式的 {{styleId}} 标记时，背景会被自动替换。
         * 传 null 表示使用原版默认背景。
         */
        public Builder tooltipBackground(TooltipBackground tooltipBackground) {
            this.tooltipBackground = tooltipBackground;
            return this;
        }

        public FancyTextStyle build() {
            return new FancyTextStyle(baseColor, shadow, alpha, Collections.unmodifiableList(effects), tooltipBackground);
        }
    }
}
