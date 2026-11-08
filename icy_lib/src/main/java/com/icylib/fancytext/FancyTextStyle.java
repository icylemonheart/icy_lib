package com.icylib.fancytext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 艺术字样式：基础颜色 + 有序效果链 + 可选 tooltip 背景。 */
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
            this.baseColor = (baseColor & 0xFF000000) == 0 ? 0xFF000000 | baseColor : baseColor;
        }

        public Builder effect(TextEffect effect) {
            this.effects.add(effect);
            return this;
        }

        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public Builder tooltipBackground(TooltipBackground tooltipBackground) {
            this.tooltipBackground = tooltipBackground;
            return this;
        }

        public FancyTextStyle build() {
            return new FancyTextStyle(baseColor, shadow, alpha, Collections.unmodifiableList(effects), tooltipBackground);
        }
    }
}
