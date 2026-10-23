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
 * 独立乘区 → 乘法乘区 通用迁移处理器。
 *
 * 对配置中列出的属性，每 20 tick 扫描 ADD_MULTIPLIED_TOTAL modifier，
 * 用 counteract (1/P-1) 抵消独立乘区乘积 P，
 * 用 migration (∑mi) 加到 ADD_MULTIPLIED_BASE，等价于把所有 ADD_MULTIPLIED_TOTAL modifier 加法语义迁移到乘法乘区。
 *
 * 最终受保护集合 = 玩家配置 protectedIds 白名单 ∪ 下游模组通过
 * com.icylib.api.MultiplyMigrationProtection 注册的 id；这些 modifier 保留在独立乘区。
 * icy_lib 自己的 counteract/migration modifier 通过"扫描前先 remove 自身"实现隔离，不依赖白名单。
 *
 * Effective protected set = configured protectedIds whitelist ∪ ids registered by
 * downstream mods via com.icylib.api.MultiplyMigrationProtection.
 *
 * 迁移算法（继承自原 LifeThread 索取1的设计）：
 *   独立乘区 foreign 每条 ×(1+mi) → 乘法乘区 +mi 相加（加法语义）
 *   例：×1.2 ×1.5（两条独立）→ +20% +50% = 合计 +70%
 *      而不是 (1.2 × 1.5) - 1 = +80%
 */
public class MultiplyTotalMigrationHandler {

    // icy_lib 自己添加的迁移用 id（所有属性共用）
    // 1.21.1 中 AttributeModifier 的 id 由 UUID 改为 ResourceLocation
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

        // 目标属性 = 配置 targetAttributes ∪ 下游模组通过 MultiplyMigrationProtection 注册的属性
        Set<String> targetAttrNames = new HashSet<>(MultiplyMigrationConfig.TARGET_ATTRIBUTES.get());
        targetAttrNames.addAll(com.icylib.api.MultiplyMigrationProtection.getRegisteredTargetAttributes());
        if (targetAttrNames.isEmpty()) return;

        Set<ResourceLocation> protectedIds = resolveProtectedIds();

        for (String attrName : targetAttrNames) {
            // 检查该属性对当前玩家是否应进行迁移
            // 配置中的属性始终生效；API注册的属性按玩家条件过滤
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

    /**
     * 解析最终生效的保护 id 集合 =
     * 玩家配置白名单 ∪ 下游模组通过 MultiplyMigrationProtection API 注册的 id。
     * icy_lib 自己的 counteract/migration id 不在此处加入，
     * 而是由 migrateAttribute 在扫描前先 remove 自身 modifier 来实现隔离。
     */
    private Set<ResourceLocation> resolveProtectedIds() {
        Set<ResourceLocation> set = new HashSet<>();
        for (String s : MultiplyMigrationConfig.PROTECTED_IDS.get()) {
            try {
                ResourceLocation rl = ResourceLocation.tryParse(s);
                if (rl != null) set.add(rl);
            } catch (Exception ignored) {
                // 配置校验阶段已过滤，此处防御性忽略
            }
        }
        set.addAll(com.icylib.api.MultiplyMigrationProtection.getRegisteredIds());
        return set;
    }

    /**
     * 解析属性 Holder。支持：
     *   简写：max_health → minecraft:generic.max_health
     *   全写：minecraft:generic.max_health / icy_lib:defense
     */
    private Holder<Attribute> resolveAttributeHolder(String name) {
        String normalized = name.indexOf(':') >= 0 ? name : "minecraft:generic." + name;
        ResourceLocation rl = ResourceLocation.tryParse(normalized);
        if (rl == null) return null;
        return BuiltInRegistries.ATTRIBUTE.getHolder(rl).orElse(null);
    }

    /**
     * 对单个属性实例实施迁移。
     * 设 foreign modifier 在独立乘区的乘积 P = ∏(1+mi)，加法语义之和 sumM = ∑mi。
     *   counteract (ADD_MULTIPLIED_TOTAL, 1/P - 1) → 抵消独立乘区，让独立乘区净效果为 1
     *   migration (ADD_MULTIPLIED_BASE, sumM)       → 按加法语义把 ∑mi 迁移到乘法乘区
     *
     * 自身隔离：扫描前先 remove 自己上一 tick 添加的 counteract/migration，
     * 这样扫描时不会看到自己的 modifier，避免反馈循环，无需在白名单中特殊处理。
     */
    private void migrateAttribute(AttributeInstance attrInst, Set<ResourceLocation> protectedIds) {
        // 先移除自身上一 tick 添加的迁移修饰符，确保扫描只看到真正的 foreign modifier
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

        // 无 foreign，或乘积非正（无法取倒数）→ 不添加迁移修饰符（自身已 remove）
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
