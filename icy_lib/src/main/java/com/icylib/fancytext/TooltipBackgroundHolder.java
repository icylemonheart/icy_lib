package com.icylib.fancytext;

/**
 * Tooltip 背景线程持有者（渲染线程内传递）。
 *
 * 在 {@link FancyTextTooltipEvents#onItemTooltip} 中设置，
 * 在 {@link com.icylib.mixin.TooltipBackgroundMixin} 渲染时读取，
 * 渲染结束后由 mixin 的 RETURN 注入清除。
 */
public final class TooltipBackgroundHolder {

    private static final ThreadLocal<TooltipBackground> HOLDER = new ThreadLocal<>();

    private TooltipBackgroundHolder() {
    }

    public static void set(TooltipBackground background) {
        if (background == null) {
            HOLDER.remove();
        } else {
            HOLDER.set(background);
        }
    }

    public static TooltipBackground get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
