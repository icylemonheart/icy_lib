package com.icylib.dodge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 闪避判定：在 LivingIncomingDamageEvent 中消耗完美闪避窗口并取消伤害。
 */
public class DodgeEventHandler {

    @SubscribeEvent
    public void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        DodgeSystem.DodgeState state = DodgeSystem.STATES.get(player.getUUID());
        if (state == null || state.consumed) {
            return;
        }
        long now = player.level().getGameTime();
        if (state.isExpired(now)) {
            DodgeSystem.STATES.remove(player.getUUID());
            return;
        }
        if (!(event.getSource().getEntity() instanceof LivingEntity)) {
            return;
        }
        state.consumed = true;
        event.setCanceled(true);
        state.callback.onPerfectDodge(player, event.getSource());
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DodgeSystem.clear(player);
        }
    }
}
