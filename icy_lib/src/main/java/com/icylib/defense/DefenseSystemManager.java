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
 * 防御系统调度器：在 LivingDamageEvent.Pre（HIGHEST）按 priority 顺序应用各防御层。
 */
public class DefenseSystemManager {

    private static final List<DefenseLayer> LAYERS = new ArrayList<>();

    static {
        registerLayer(ShieldSystem.INSTANCE);
        registerLayer(ArmorSystem.INSTANCE);
    }

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

        DefenseApplyEvent applyEvent = new DefenseApplyEvent(player, source, context);
        NeoForge.EVENT_BUS.post(applyEvent);
        if (applyEvent.isCanceled()) {
            return;
        }
        context = applyEvent.getContext();

        float remaining = applyDefense(player, context);
        container.setNewDamage(Math.max(0.0F, remaining));
    }

    private static float applyDefense(Player player, DefenseDamageContext context) {
        if (context.getMode() == DefenseDamageContext.Mode.SEQUENTIAL) {
            return applySequential(player, context);
        } else {
            return applySimultaneous(player, context);
        }
    }

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
        return context.getOriginalDamage();
    }
}
