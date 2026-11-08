package com.icylib.upgrade;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/** 专属武器技能培养接口；技能索引 0红球/1黄球/2蓝球/3必杀/4被动/5~7跃升Ⅰ~Ⅲ，等级上限 18，数据存于物品 NBT。 */
public interface ISkillWeapon {
    int SKILL_RED = 0;
    int SKILL_YELLOW = 1;
    int SKILL_BLUE = 2;
    int SKILL_ULT = 3;
    int SKILL_PASSIVE = 4;
    int SKILL_LEAP_1 = 5;
    int SKILL_LEAP_2 = 6;
    int SKILL_LEAP_3 = 7;
    int SKILL_COUNT = 8;
    int MAX_SKILL_LEVEL = 18;

    String skillName(int index);

    String skillDesc(int index, int level);

    default boolean supportsSkillUpgrade(ItemStack stack) {
        return true;
    }

    default boolean skillUpgradable(int index) {
        return index != SKILL_PASSIVE;
    }

    int getSkillLevel(ItemStack stack, int index);

    void setSkillLevel(ItemStack stack, int index, int level);

    default float skillMultiplier(ItemStack stack, int index) {
        int level = Math.max(1, Math.min(MAX_SKILL_LEVEL, getSkillLevel(stack, index)));
        return 0.5f + 0.5f * (level - 1) / (MAX_SKILL_LEVEL - 1);
    }

    int getAscension(ItemStack stack);

    default int maxAscension() {
        return 3;
    }

    String ascensionEffect(int stage);

    List<ItemStack> getAscensionMaterials(ItemStack stack);

    void ascend(ItemStack stack);
}
