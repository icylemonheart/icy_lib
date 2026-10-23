package com.icylib.util;

/**
 * 伤害管线跟踪器 —— 用 ThreadLocal 计数标记当前线程是否处于 LivingEntity.hurt() 管线中。
 *
 * 用于区分两种 setHealth 调用：
 *  - 管线内调用（在 hurt → actuallyHurt → setHealth 链路中）：
 *    已被 icy_lib 在 LivingDamageEvent 阶段减伤过，无需重复结算
 *  - 管线外调用（外部模组绕过 hurt 直接调用 setHealth 扣血）：
 *    需要在 LivingEntityDefenseMixin 中补上 icy_lib 通用抗性 + 防御减伤
 *
 * 计数设计以支持嵌套 hurt 调用（递归/重入），到 0 时清理 ThreadLocal 避免内存泄漏。
 */
public final class DamagePipelineTracker {

    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

    private DamagePipelineTracker() {}

    /** 进入 hurt 管线（嵌套可重入，计数 +1） */
    public static void enter() {
        DEPTH.set(DEPTH.get() + 1);
    }

    /** 退出 hurt 管线（计数 -1，到 0 时移除 ThreadLocal） */
    public static void exit() {
        int v = DEPTH.get();
        if (v <= 0) {
            DEPTH.remove();
            return;
        }
        if (v == 1) {
            DEPTH.remove();
        } else {
            DEPTH.set(v - 1);
        }
    }

    /** 当前线程是否处于 hurt 管线内 */
    public static boolean inPipeline() {
        return DEPTH.get() > 0;
    }
}
