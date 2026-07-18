package com.icylib.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class IcyLibConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue CANCEL_HIT_ANIMATION_WHEN_NO_DAMAGE;
    
    public static final ForgeConfigSpec.DoubleValue MONSTER_DEFENSE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MONSTER_CRITICAL_DEFENSE_MULTIPLIER;

    static {
        BUILDER.comment("Defense-related configuration").push("defense");
        
        CANCEL_HIT_ANIMATION_WHEN_NO_DAMAGE = BUILDER
                .comment("Whether to cancel hit animation when defense reduces damage to 0 or below. When set to true, fully blocked damage will not show hit animation.")
                .define("cancelHitAnimationWhenNoDamage", false);
        
        MONSTER_DEFENSE_MULTIPLIER = BUILDER
                .comment("Monster default defense = max health × this value. Default is 0.1 (1/10). Set to 0 to disable extra defense for monsters.")
                .defineInRange("monsterDefenseMultiplier", 0.1, 0.0, 1.0);
        
        MONSTER_CRITICAL_DEFENSE_MULTIPLIER = BUILDER
                .comment("Monster default critical defense = armor × this value. Default is 0.1 (1/10). Set to 0 to disable extra critical defense for monsters.")
                .defineInRange("monsterCriticalDefenseMultiplier", 0.1, 0.0, 1.0);
        
        BUILDER.pop();
        
        SPEC = BUILDER.build();
    }
}