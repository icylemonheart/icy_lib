package com.icylib.defense;

import com.icylib.armor.ArmorSystem;
import com.icylib.shield.ShieldSystem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 防御系统调度器。
 *
 * 作为所有防御层（护盾、护甲及未来扩展）的统一入口，
 * 订阅 {@link LivingDamageEvent.Pre}（HIGHEST 优先级），
 * 按层级 priority 顺序应用伤害。
 *
 * 设计目标：
 * 1. 单一伤害入口，避免多个子系统各自订阅事件导致的顺序混乱
 * 2. 通过 {@link DefenseApplyEvent} 提供事件驱动扩展点
 * 3. 支持未来扩展的穿透与同时作用伤害类型，无需重构核心逻辑
 */
public class DefenseSystemManager {

    private static final List<DefenseLayer> LAYERS = new ArrayList<>();

    static {
        // 注册默认防御层：护盾优先于护甲
        registerLayer(ShieldSystem.INSTANCE);
        registerLayer(ArmorSystem.INSTANCE);
    }

    /**
     * 注册新的防御层。第三方模组可在 setup 阶段调用以扩展系统。
     * 注册后会按 priority 重新排序。
     */
    public static synchronized void registerLayer(DefenseLayer layer) {
        LAYERS.add(layer);
        LAYERS.sort(Comparator.comparingInt(l -> l.getType().getPriority()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        DamageContainer container = event.getContainer();
        float damage = container.getNewDamage();
        if (damage <= 0.0F) {
            return;
        }

        DamageSource source = container.getSource();
        DefenseDamageContext context = DefenseDamageContext.forSource(source, damage);

        // 触发扩展事件，允许第三方模组修改应用方式
        DefenseApplyEvent applyEvent = new DefenseApplyEvent(player, source, context);
        NeoForge.EVENT_BUS.post(applyEvent);
        if (applyEvent.isCanceled()) {
            // 取消意味着绕过所有防御层，伤害直接作用于生命值
            return;
        }
        context = applyEvent.getContext();

        float remaining = applyDefense(player, context);
        container.setNewDamage(Math.max(0.0F, remaining));
    }

    /**
     * 根据 context 模式将伤害分配到各防御层。
     * 返回最终需要作用于玩家生命值的伤害。
     */
    private static float applyDefense(Player player, DefenseDamageContext context) {
        if (context.getMode() == DefenseDamageContext.Mode.SEQUENTIAL) {
            return applySequential(player, context);
        } else {
            return applySimultaneous(player, context);
        }
    }

    /**
     * 顺序模式：按 priority 从小到大依次吸收。
     * shield 先消耗，耗尽后剩余伤害传递给 armor，以此类推。
     */
    private static float applySequential(Player player, DefenseDamageContext context) {
        float remaining = context.getOriginalDamage();
        for (DefenseLayer layer : LAYERS) {
            if (context.bypasses(layer.getType())) {
                continue;
            }
            if (!layer.isActive(player)) {
                continue;
            }
            remaining = layer.applyDamage(player, remaining);
            if (remaining <= 0.0F) {
                return 0.0F;
            }
        }
        return Math.max(0.0F, remaining);
    }

    /**
     * 同时模式：按比例将原始伤害同时作用于多个层级。
     * 各层独立吸收自身分配到的伤害，玩家生命值仍承受原始伤害（按 health 比例可削减）。
     */
    private static float applySimultaneous(Player player, DefenseDamageContext context) {
        Map<DefenseLayerType, Float> ratios = context.getSplitRatios();
        for (DefenseLayer layer : LAYERS) {
            if (context.bypasses(layer.getType())) {
                continue;
            }
            if (!layer.isActive(player)) {
                continue;
            }
            float ratio = ratios.getOrDefault(layer.getType(), 0.0F);
            float splitDamage = context.getOriginalDamage() * ratio;
            if (splitDamage > 0.0F) {
                layer.applyDamage(player, splitDamage);
            }
        }
        // 同时模式下：生命值承受原始伤害（按未在 ratios 中显式削减的部分）
        // 默认设计为 health 仍承受 100% 原始伤害，第三方可通过 DefenseApplyEvent 自定义
        return context.getOriginalDamage();
    }
}
