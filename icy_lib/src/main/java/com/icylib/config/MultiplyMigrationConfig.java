package com.icylib.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * 独立乘区 → 乘法乘区 迁移配置。
 * Independent multiply zone (ADD_MULTIPLIED_TOTAL) → multiply-base zone (ADD_MULTIPLIED_BASE) migration config.
 *
 * 对配置中列出的属性，每 tick 扫描其 ADD_MULTIPLIED_TOTAL 修饰符，
 * 用 counteract (1/P - 1) 抵消独立乘区乘积 P，
 * 用 migration (∑mi) 加到 ADD_MULTIPLIED_BASE 乘区，
 * 等价于：把所有 ADD_MULTIPLIED_TOTAL modifier 按加法语义迁移到 ADD_MULTIPLIED_BASE。
 *
 * For the configured attributes, each tick scans ADD_MULTIPLIED_TOTAL modifiers,
 * cancels the independent-zone product P with a counteract (1/P - 1) modifier,
 * and adds migration (∑mi) to ADD_MULTIPLIED_BASE, i.e. every ADD_MULTIPLIED_TOTAL
 * modifier is migrated to ADD_MULTIPLIED_BASE using additive semantics.
 *
 * id 白名单：列出的 modifier id (ResourceLocation) 保留在独立乘区，不参与迁移。默认为空 = 迁移全部。
 * id whitelist: listed modifier ids stay in the independent zone. Empty = migrate all.
 */
public class MultiplyMigrationConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    /** 要实施迁移的属性注册名列表（简写如 max_health 会自动补全 minecraft:generic. 前缀） / Registry names of attributes to migrate (shorthand like max_health expands to minecraft:generic.max_health). */
    public static final ModConfigSpec.ConfigValue<List<? extends String>> TARGET_ATTRIBUTES;

    /** 保留在独立乘区的 modifier id (ResourceLocation) 白名单（下游模组也可通过 com.icylib.api.MultiplyMigrationProtection 注册） / Whitelist of modifier ids kept in the independent zone (downstream mods may also register via com.icylib.api.MultiplyMigrationProtection). */
    public static final ModConfigSpec.ConfigValue<List<? extends String>> PROTECTED_IDS;

    static {
        BUILDER.comment(
                "独立乘区 → 乘法乘区 迁移配置",
                "Independent multiply zone (ADD_MULTIPLIED_TOTAL) → multiply-base zone (ADD_MULTIPLIED_BASE) migration config.",
                "对下列属性，扫描其 ADD_MULTIPLIED_TOTAL（独立乘区）修饰符，全部按加法语义迁移到 ADD_MULTIPLIED_BASE（乘法乘区）。",
                "For the attributes listed below, their ADD_MULTIPLIED_TOTAL modifiers are migrated to ADD_MULTIPLIED_BASE using additive semantics.",
                "迁移算法：设独立乘区各个数值为 m1, m2, ...；",
                "Algorithm: given the independent-zone amounts m1, m2, ...;",
                "  counteract (ADD_MULTIPLIED_TOTAL) amount = 1/∏(1+mi) - 1  → 抵消独立乘区乘积 / cancels the independent-zone product",
                "  migration (ADD_MULTIPLIED_BASE) amount = ∑mi             → 加法语义迁移到乘法乘区 / migrates to the multiply-base zone additively",
                "最终：独立乘区 = 1（净空），乘法乘区累加 ∑mi。",
                "Result: independent zone = 1 (cleared), multiply-base zone gains ∑mi.",
                "id 白名单内的 modifier 保留在独立乘区，不参与迁移。",
                "Modifiers whose id is in the whitelist stay in the independent zone and are not migrated.",
                "比如：独立乘区为 ×1.5 ×2 ×1.2，迁移后为 +50% +100% +20%（加法语义）。",
                "Example: independent zone ×1.5 ×2 ×1.2 becomes +50% +100% +20% (additive)."
        ).push("multiply_migration");

        TARGET_ATTRIBUTES = BUILDER
                .comment(
                        "要实施迁移的属性注册名列表。",
                        "Registry names of the attributes to migrate.",
                        "支持两种写法 / Two formats are supported:",
                        "  简写 / shorthand：max_health → 自动补全为 minecraft:generic.max_health / expands to minecraft:generic.max_health",
                        "  全写 / full：minecraft:generic.max_health / icy_lib:defense",
                        "注意：TOML 列表里的字符串必须用英文双引号包裹，多个用英文逗号分隔。",
                        "Note: strings in the TOML list MUST be wrapped in double quotes; separate multiple entries with commas.",
                        "示例 / Example：targetAttributes = [\"minecraft:generic.max_health\", \"icy_lib:defense\"]",
                        "留空则不启用迁移。",
                        "Leave empty to disable migration."
                )
                .defineList("targetAttributes", List.of(),
                        obj -> obj instanceof String s && !s.isBlank());

        PROTECTED_IDS = BUILDER
                .comment(
                        "id 白名单：列出的 modifier 保留在独立乘区，不被迁移。",
                        "id whitelist: listed modifiers stay in the independent zone and are not migrated.",
                        "格式：标准 ResourceLocation 字符串，如 icy_lib:my_special_modifier",
                        "Format: standard ResourceLocation string, e.g. icy_lib:my_special_modifier",
                        "注意：TOML 列表里的字符串必须用英文双引号包裹。",
                        "Note: strings in the TOML list MUST be wrapped in double quotes.",
                        "示例 / Example：protectedIds = [\"icy_lib:my_special_modifier\"]",
                        "默认为空 = 迁移所有未被模组自行登记保护的 ADD_MULTIPLIED_TOTAL modifier。",
                        "Empty by default = migrate every ADD_MULTIPLIED_TOTAL modifier that no mod has registered for protection.",
                        "其它模组可通过 icy_lib API（com.icylib.api.MultiplyMigrationProtection）自行登记受保护 id，无需在此手写。",
                        "Other mods can register protected ids themselves via the icy_lib API (com.icylib.api.MultiplyMigrationProtection)."
                )
                .defineList("protectedIds", List.of(),
                        obj -> obj instanceof String s && isValidResourceLocation(s));

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private static boolean isValidResourceLocation(String s) {
        try {
            return ResourceLocation.tryParse(s) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
