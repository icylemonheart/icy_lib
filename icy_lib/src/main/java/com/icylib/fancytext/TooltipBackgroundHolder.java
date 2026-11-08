package com.icylib.fancytext;

/** Tooltip 背景线程持有者：ItemTooltipEvent 写入、渲染时读取、RETURN 时清除。 */
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
