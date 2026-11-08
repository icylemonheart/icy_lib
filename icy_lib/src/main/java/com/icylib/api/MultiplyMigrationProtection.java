package com.icylib.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * 独立乘区迁移保护 API：登记保留在独立乘区的 modifier id 与需要迁移的目标属性。
 */
public final class MultiplyMigrationProtection {

    private static final Set<ResourceLocation> PROTECTED = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final Map<String, Set<Predicate<Player>>> TARGET_ATTRIBUTES = new ConcurrentHashMap<>();

    private MultiplyMigrationProtection() {
    }

    public static void registerProtectedId(ResourceLocation id) {
        if (id != null) {
            PROTECTED.add(id);
        }
    }

    public static void unregisterProtectedId(ResourceLocation id) {
        if (id != null) {
            PROTECTED.remove(id);
        }
    }

    public static Set<ResourceLocation> getRegisteredIds() {
        return Collections.unmodifiableSet(PROTECTED);
    }

    public static void registerTargetAttribute(String attributeName) {
        registerTargetAttribute(attributeName, player -> true);
    }

    public static void registerTargetAttribute(String attributeName, Predicate<Player> playerCondition) {
        if (attributeName != null && !attributeName.isEmpty() && playerCondition != null) {
            TARGET_ATTRIBUTES.computeIfAbsent(attributeName, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                    .add(playerCondition);
        }
    }

    public static boolean isTargetActive(String attributeName, Player player) {
        Set<Predicate<Player>> conditions = TARGET_ATTRIBUTES.get(attributeName);
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }
        for (Predicate<Player> condition : conditions) {
            if (condition.test(player)) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> getRegisteredTargetAttributes() {
        return Collections.unmodifiableSet(TARGET_ATTRIBUTES.keySet());
    }
}
