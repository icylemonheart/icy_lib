package com.icylib.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * icy_lib 通用配置（防御 / 移动）。
 */
public class IcyLibConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue CANCEL_HIT_ANIMATION_WHEN_NO_DAMAGE;

    public static final ModConfigSpec.DoubleValue MONSTER_DEFENSE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue MONSTER_CRITICAL_DEFENSE_MULTIPLIER;

    public static final ModConfigSpec.BooleanValue SET_HEALTH_DEFENSE_ENABLED;

    public static final ModConfigSpec.DoubleValue PLAYER_SPEED_CAP;

    static {
        BUILDER.comment(
                "防御相关配置",
                "Defense-related settings."
        ).push("defense");

        CANCEL_HIT_ANIMATION_WHEN_NO_DAMAGE = BUILDER
                .comment(
                        "当防御导致实际伤害≤0时，是否取消受击动画。设为 true 时，完全抵御伤害不会显示受击动画。",
                        "Whether to cancel the hurt animation when defense reduces actual damage to ≤0. When true, fully negated damage shows no hurt animation."
                )
                .define("cancelHitAnimationWhenNoDamage", false);

        MONSTER_DEFENSE_MULTIPLIER = BUILDER
                .comment(
                        "怪物默认防御值 = 最大生命值 × 此值。默认为 0.1（即 1/10）。设为 0 则怪物不获取额外防御。",
                        "Monster default defense = max health × this value. Default 0.1 (1/10). Set to 0 to give monsters no extra defense."
                )
                .defineInRange("monsterDefenseMultiplier", 0.1, 0.0, 1.0);

        MONSTER_CRITICAL_DEFENSE_MULTIPLIER = BUILDER
                .comment(
                        "怪物默认会心防御值 = 护甲值 × 此值。默认为 0.1（即 1/10）。设为 0 则怪物不获取额外会心防御。",
                        "Monster default critical defense = armor × this value. Default 0.1 (1/10). Set to 0 to give monsters no extra critical defense."
                )
                .defineInRange("monsterCriticalDefenseMultiplier", 0.1, 0.0, 1.0);

        SET_HEALTH_DEFENSE_ENABLED = BUILDER
                .comment(
                        "是否让 icy_lib 的通用抗性与防御对【绕过 hurt 直接调用 setHealth 扣血】的穿透伤害生效。",
                        "Whether icy_lib universal resistance & defense apply to bypass damage that skips hurt and calls setHealth directly.",
                        "默认 false。启用后：",
                        "Default false. When enabled:",
                        "  - hurt 管线内的 setHealth（已被 LivingDamageEvent 减伤过）会被跳过，避免重复结算；",
                        "  - setHealth inside the hurt pipeline (already reduced by LivingDamageEvent) is skipped to avoid double settlement;",
                        "  - hurt 管线外的 setHealth（直接改血）将应用【通用抗性除法 + 防御削减】减伤。",
                        "  - setHealth outside the hurt pipeline (direct health change) applies [universal resistance division + defense reduction]."
                )
                .define("setHealthDefenseEnabled", false);

        BUILDER.pop();

        BUILDER.comment(
                "移动相关配置",
                "Movement-related settings."
        ).push("movement");

        PLAYER_SPEED_CAP = BUILDER
                .comment(
                        "玩家移动速度表现上限。玩家速度属性默认为 0.1，此值钳制的是实际移动表现而非属性本身：无论速度属性堆得多高，行走/游泳移动最多表现为速度属性等于此值时的样子。设为 0 则禁用限制。默认 0.5。",
                        "Player movement speed display cap. The player speed attribute defaults to 0.1; this caps actual movement performance, not the attribute itself: no matter how high speed is stacked, walking/swimming performs at most as if the speed attribute equaled this value. Set to 0 to disable. Default 0.5."
                )
                .defineInRange("playerSpeedCap", 0.5, 0.0, 10.0);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
