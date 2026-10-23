package com.icylib.defense;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * 防御层应用前事件。
 *
 * 在 {@link DefenseSystemManager} 真正将伤害作用于各防御层之前触发，
 * 第三方模组可订阅此事件以：
 * - 修改伤害应用模式（如将某伤害标记为 SIMULTANEOUS 同时扣血）
 * - 添加穿透标记（如该伤害仅作用于护盾，绕过护甲）
 * - 调整分流比例
 * - 直接取消（跳过防御系统，伤害直接作用于生命值）
 *
 * 该事件是实现"事件驱动架构"的核心扩展点，
 * 允许在不修改核心代码的前提下注册新的伤害行为。
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
