package com.icylib.defense;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * 防御层应用前事件：可修改伤害分流上下文，取消则跳过全部防御层。
 */
public class DefenseApplyEvent extends Event implements ICancellableEvent {

    private final Player player;
    private final DamageSource source;
    private DefenseDamageContext context;

    public DefenseApplyEvent(Player player, DamageSource source, DefenseDamageContext context) {
        this.player = player;
        this.source = source;
        this.context = context;
    }

    public Player getPlayer() {
        return player;
    }

    public DamageSource getSource() {
        return source;
    }

    public DefenseDamageContext getContext() {
        return context;
    }

    public void setContext(DefenseDamageContext context) {
        this.context = context;
    }
}
