package com.icylib.damage;

import net.minecraft.world.damagesource.DamageSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 伤害穿透钩子：供附属模组注册"无视护甲 / 无视魔法防御"判定。
 *
 * 两种穿透版本的区别：
 *  - 穿透 icy_lib 防御与抗性版本：附属模组调用 registerArmorBypass/registerMagicBypass 注册判定，
 *    本库 DefenseEventHandler 会跳过对应伤害类型的抗性除法与防御削减，
 *    同时原版护甲/魔法吸收 Mixin（LivingEntityDamageAbsorbMixin）也会短路。
 *  - 不穿透 icy_lib 防御与抗性版本：附属模组不注册本钩子，仅通过伤害源标签 Mixin
 *    （DamageSourceBypassTagsMixin）使 BYPASSES_ARMOR/EFFECTS 等标签返回 true，
 *    让原版护甲/魔法吸收方法自行短路，并让 L2 词条减伤自行跳过；
 *    本库的抗性除法与防御削减正常生效。
 *
 * 本钩子仅在"穿透 icy_lib 版本"中被注册和调用。
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

    /** 注册"无视护甲"判定（对应原版 BYPASSES_ARMOR 语义）。 */
    public static synchronized void registerArmorBypass(BypassCheck check) {
        ARMOR_CHECKS.add(check);
    }

    /** 注册"无视魔法防御"判定（对应原版 BYPASSES_ENCHANTMENTS/RESISTANCE/EFFECTS 语义）。 */
    public static synchronized void registerMagicBypass(BypassCheck check) {
        MAGIC_CHECKS.add(check);
    }

    /** 是否无视护甲（仅穿透 icy_lib 版本生效：跳过本库物理抗性除法与防御削减）。 */
    public static boolean bypassArmor(DamageSource source) {
        for (BypassCheck check : ARMOR_CHECKS) {
            if (check.shouldBypass(source)) return true;
        }
        return false;
    }

    /** 是否无视魔法防御（仅穿透 icy_lib 版本生效：跳过本库魔法/元素抗性除法与防御削减）。 */
    public static boolean bypassMagic(DamageSource source) {
        for (BypassCheck check : MAGIC_CHECKS) {
            if (check.shouldBypass(source)) return true;
        }
        return false;
    }
}
