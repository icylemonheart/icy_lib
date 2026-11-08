package com.icylib.dodge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用闪避判定系统：窗口期内受到活体攻击者的伤害时取消伤害并触发回调。
 */
public final class DodgeSystem {

    static final Map<UUID, DodgeState> STATES = new ConcurrentHashMap<>();

    private DodgeSystem() {
    }

    /**
     * @param windowTicks 窗口时长，单位 tick
     */
    public static void activate(ServerPlayer player, long windowTicks, DodgeCallback callback) {
        STATES.put(player.getUUID(), new DodgeState(player.level().getGameTime(), windowTicks, callback));
    }

    public static boolean isActive(ServerPlayer player) {
        DodgeState s = STATES.get(player.getUUID());
        return s != null && !s.consumed && !s.isExpired(player.level().getGameTime());
    }

    public static void clear(ServerPlayer player) {
        STATES.remove(player.getUUID());
    }

    @FunctionalInterface
    public interface DodgeCallback {
        void onPerfectDodge(ServerPlayer player, DamageSource source);
    }

    static final class DodgeState {
        final long startTime;
        final long windowTicks;
        final DodgeCallback callback;
        boolean consumed;

        DodgeState(long startTime, long windowTicks, DodgeCallback callback) {
            this.startTime = startTime;
            this.windowTicks = windowTicks;
            this.callback = callback;
        }

        boolean isExpired(long now) {
            return now - startTime > windowTicks;
        }
    }
}
