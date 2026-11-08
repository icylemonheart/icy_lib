package com.icylib.util;

/**
 * 用 ThreadLocal 计数标记当前线程是否处于 LivingEntity.hurt() 管线内。
 */
public final class DamagePipelineTracker {

    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

    private DamagePipelineTracker() {}

    public static void enter() {
        DEPTH.set(DEPTH.get() + 1);
    }

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

    public static boolean inPipeline() {
        return DEPTH.get() > 0;
    }
}
