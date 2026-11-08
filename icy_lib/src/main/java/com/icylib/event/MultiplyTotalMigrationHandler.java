package com.icylib.event;

import com.icylib.config.MultiplyMigrationConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 把 ADD_MULTIPLIED_TOTAL 按加法语义迁移到 ADD_MULTIPLIED_BASE：counteract 抵消原乘积，migration 加和。
 */
public class MultiplyTotalMigrationHandler {

    public static final ResourceLocation COUNTERACT_ID =
            ResourceLocation.fromNamespaceAndPath("icy_lib", "multiply_total_counteract");
    public static final ResourceLocation MIGRATION_ID =
            ResourceLocation.fromNamespaceAndPath("icy_lib", "multiply_base_migration");

    @SubscribeEvent
    public void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (player.isDeadOrDying() || !player.isAlive() || player.getHealth() <= 0.0F) return;
        if (player.tickCount % 20 != 0) return;

        Set<String> targetAttrNames = new HashSet<>(MultiplyMigrationConfig.TARGET_ATTRIBUTES.get());
        targetAttrNames.addAll(com.icylib.api.MultiplyMigrationProtection.getRegisteredTargetAttributes());
        if (targetAttrNames.isEmpty()) return;

        Set<ResourceLocation> protectedIds = resolveProtectedIds();

        for (String attrName : targetAttrNames) {
            boolean fromConfig = MultiplyMigrationConfig.TARGET_ATTRIBUTES.get().contains(attrName);
            if (!fromConfig && !com.icylib.api.MultiplyMigrationProtection.isTargetActive(attrName, player)) {
                continue;
            }

            Holder<Attribute> attrHolder = resolveAttributeHolder(attrName);
            if (attrHolder == null) continue;

            AttributeInstance attrInst = player.getAttribute(attrHolder);
            if (attrInst == null) continue;

            migrateAttribute(attrInst, protectedIds);
        }
    }

    private Set<ResourceLocation> resolveProtectedIds() {
        Set<ResourceLocation> set = new HashSet<>();
        for (String s : MultiplyMigrationConfig.PROTECTED_IDS.get()) {
            try {
                ResourceLocation rl = ResourceLocation.tryParse(s);
                if (rl != null) set.add(rl);
            } catch (Exception ignored) {
            }
        }
        set.addAll(com.icylib.api.MultiplyMigrationProtection.getRegisteredIds());
        return set;
    }

    private Holder<Attribute> resolveAttributeHolder(String name) {
        String normalized = name.indexOf(':') >= 0 ? name : "minecraft:generic." + name;
        ResourceLocation rl = ResourceLocation.tryParse(normalized);
        if (rl == null) return null;
        return BuiltInRegistries.ATTRIBUTE.getHolder(rl).orElse(null);
    }

    private void migrateAttribute(AttributeInstance attrInst, Set<ResourceLocation> protectedIds) {
        attrInst.removeModifier(COUNTERACT_ID);
        attrInst.removeModifier(MIGRATION_ID);

        double product = 1.0D;
        double sumM = 0.0D;
        boolean hasOther = false;

        for (AttributeModifier mod : attrInst.getModifiers()) {
            if (mod.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) continue;
            if (protectedIds.contains(mod.id())) continue;

            double m = mod.amount();
            product *= (1.0D + m);
            sumM += m;
            hasOther = true;
        }

        if (!hasOther || product <= 0.0D || Double.isNaN(product) || Double.isInfinite(product)) {
            return;
        }

        double counteractAmount = (1.0D / product) - 1.0D;
        double migrationAmount = sumM;

        if (Math.abs(counteractAmount) > 1e-9 && Math.abs(migrationAmount) > 1e-9) {
            attrInst.addTransientModifier(new AttributeModifier(
                    COUNTERACT_ID, counteractAmount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            attrInst.addTransientModifier(new AttributeModifier(
                    MIGRATION_ID, migrationAmount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }
}
