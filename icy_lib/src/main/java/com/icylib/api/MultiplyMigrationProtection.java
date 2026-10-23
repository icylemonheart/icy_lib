package com.icylib.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * 独立乘区迁移保护 —— 下游模组公共 API。
 * Independent-zone migration protection — public API for downstream mods.
 *
 * 下游模组（如契约饰品模组）若希望自己的 ADD_MULTIPLIED_TOTAL modifier 保留在独立乘区、
 * 不被 icy_lib 的"独立乘区 → 乘法乘区"通用迁移影响，应在自己的类加载/初始化阶段
 * 调用 {@link #registerProtectedId(ResourceLocation)} 注册该 modifier 的 ResourceLocation id。
 *
 * Downstream mods (e.g. contract-accessory mods) that want their ADD_MULTIPLIED_TOTAL
 * modifiers to stay in the independent multiply zone should register the modifier
 * ResourceLocation id here during class-load/initialization.
 *
 * 注册即生效，无需玩家在配置文件中手写 id；注册集合与玩家配置的 protectedIds
 * 白名单取并集。本集合仅保存 ResourceLocation，不依赖下游模组的类，icy_lib 不需要知道模组身份。
 *
 * Registration takes effect immediately; the registered set is unioned with the
 * player-configured protectedIds whitelist.
 */
public final class MultiplyMigrationProtection {

    private static final Set<ResourceLocation> PROTECTED = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * 目标属性注册表：key 为属性名称（如 "max_health" 或 "minecraft:generic.max_health"），
     * value 为玩家条件集合（空集合表示对所有玩家生效）。
     */
    private static final Map<String, Set<Predicate<Player>>> TARGET_ATTRIBUTES = new ConcurrentHashMap<>();

    private MultiplyMigrationProtection() {
    }

    /**
     * 注册一个受保护的 modifier ResourceLocation id：该 modifier 永远保留在独立乘区，不参与迁移。
     * Register a protected modifier ResourceLocation id; it always stays in the independent zone.
     *
     * 可在模组构造器、静态初始化块等任意早期阶段调用，线程安全。
     * Safe to call from any early stage (mod constructor, static initializer); thread-safe.
     */
    public static void registerProtectedId(ResourceLocation id) {
        if (id != null) {
            PROTECTED.add(id);
        }
    }

    /**
     * 注销保护（一般用于配置开关关闭时）。
     * Unregister protection (typically when a config toggle is disabled).
     */
    public static void unregisterProtectedId(ResourceLocation id) {
        if (id != null) {
            PROTECTED.remove(id);
        }
    }

    /**
     * icy_lib 内部使用：获取已注册的受保护 id 集合（只读视图）。
     * Internal: read-only view of registered protected ids.
     */
    public static Set<ResourceLocation> getRegisteredIds() {
        return Collections.unmodifiableSet(PROTECTED);
    }

    /**
     * 注册需要进行独立乘区迁移的目标属性（对所有玩家生效）。
     * Register a target attribute for independent-zone migration (applies to all players).
     *
     * @param attributeName 属性名称，支持简写（max_health）或全写（minecraft:generic.max_health）
     */
    public static void registerTargetAttribute(String attributeName) {
        registerTargetAttribute(attributeName, player -> true);
    }

    /**
     * 注册需要进行独立乘区迁移的目标属性（按玩家条件过滤）。
     * Register a target attribute for independent-zone migration (filtered by player condition).
     *
     * @param attributeName 属性名称，支持简写（max_health）或全写（minecraft:generic.max_health）
     * @param playerCondition 玩家条件，返回 true 时对该玩家启用迁移
     */
    public static void registerTargetAttribute(String attributeName, Predicate<Player> playerCondition) {
        if (attributeName != null && !attributeName.isEmpty() && playerCondition != null) {
            TARGET_ATTRIBUTES.computeIfAbsent(attributeName, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                    .add(playerCondition);
        }
    }

    /**
     * 检查指定属性对特定玩家是否应进行迁移。
     * Check if the specified attribute should be migrated for the given player.
     *
     * @param attributeName 属性名称
     * @param player 目标玩家
     * @return true 如果该属性已注册且玩家满足条件
     */
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

    /**
     * 获取已注册的目标属性集合（只读视图）。
     * Get read-only view of registered target attributes.
     */
    public static Set<String> getRegisteredTargetAttributes() {
        return Collections.unmodifiableSet(TARGET_ATTRIBUTES.keySet());
    }
}
