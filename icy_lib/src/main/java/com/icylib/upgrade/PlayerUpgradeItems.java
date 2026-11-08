package com.icylib.upgrade;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/** 可提交为升级资源的物品注册表（螺母/升级经验/技能点），由内容模组在 commonSetup 注册。 */
public class PlayerUpgradeItems {
    private static final Map<Item, Integer> NUT_VALUES = new HashMap<>();
    private static final Map<Item, Integer> EXP_VALUES = new HashMap<>();
    private static final Map<Item, Integer> SKILL_POINT_VALUES = new HashMap<>();

    public static void registerNutItem(Item item, int value) {
        NUT_VALUES.put(item, value);
    }

    public static void registerExpItem(Item item, int value) {
        EXP_VALUES.put(item, value);
    }

    public static void registerSkillPointItem(Item item, int value) {
        SKILL_POINT_VALUES.put(item, value);
    }

    public static int getNutValue(ItemStack stack) {
        return NUT_VALUES.getOrDefault(stack.getItem(), 0);
    }

    public static int getExpValue(ItemStack stack) {
        return EXP_VALUES.getOrDefault(stack.getItem(), 0);
    }

    public static int getSkillPointValue(ItemStack stack) {
        return SKILL_POINT_VALUES.getOrDefault(stack.getItem(), 0);
    }

    public static boolean isSubmittable(ItemStack stack) {
        return getNutValue(stack) > 0 || getExpValue(stack) > 0 || getSkillPointValue(stack) > 0;
    }
}
