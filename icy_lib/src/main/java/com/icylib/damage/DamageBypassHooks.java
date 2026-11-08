package com.icylib.damage;

import net.minecraft.world.damagesource.DamageSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 穿透钩子：附属模组注册"无视护甲/无视魔法防御"判定，命中时跳过本库抗性除法与防御削减。
 */
public final class DamageBypassHooks {

    private DamageBypassHooks() {
    }

    @FunctionalInterface
    public interface BypassCheck {
        boolean shouldBypass(DamageSource source);
    }

    private static final List<BypassCheck> ARMOR_CHECKS = new ArrayList<>();
    private static final List<BypassCheck> MAGIC_CHECKS = new ArrayList<>();

    public static synchronized void registerArmorBypass(BypassCheck check) {
        ARMOR_CHECKS.add(check);
    }

    public static synchronized void registerMagicBypass(BypassCheck check) {
        MAGIC_CHECKS.add(check);
    }

    public static boolean bypassArmor(DamageSource source) {
        for (BypassCheck check : ARMOR_CHECKS) {
            if (check.shouldBypass(source)) return true;
        }
        return false;
    }

    public static boolean bypassMagic(DamageSource source) {
        for (BypassCheck check : MAGIC_CHECKS) {
            if (check.shouldBypass(source)) return true;
        }
        return false;
    }
}
