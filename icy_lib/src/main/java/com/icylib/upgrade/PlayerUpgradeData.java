package com.icylib.upgrade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/** 玩家升级/晋升数据（NBT key IcyLibPlayerUpgradeData）；晋升数值取自战双参考图，各阶段属性直接替换、不叠加。 */
public class PlayerUpgradeData {
    public static final int MAX_LEVEL = 80;
    public static final int TOTAL_EXP = 500000;

    public int level = 0;
    public int exp = 0;
    public int totalExp = 0;
    public int nuts = 0;
    public int skillPoints = 0;
    public int promotionStage = 0;

    public enum Promotion {
        PRIVATE("列兵", 0, 0, 0, 0, 0, 0),
        SERGEANT_1("军士1", 2, 5000, 40, 8, 5, 3),
        SERGEANT_2("军士2", 10, 10000, 85, 18, 12, 7),
        ELITE_1("精锐1", 20, 15000, 137, 30, 20, 12),
        ELITE_2("精锐2", 30, 20000, 196, 43, 28, 18),
        ELITE_3("精锐3", 40, 25000, 261, 58, 38, 24),
        SPECIAL_1("特种1", 45, 30000, 340, 75, 50, 31),
        SPECIAL_2("特种2", 50, 35000, 432, 96, 63, 40),
        SPECIAL_3("特种3", 55, 42500, 536, 119, 78, 49),
        ACE_1("王牌1", 60, 50000, 654, 145, 96, 60),
        ACE_2("王牌2", 65, 60000, 785, 174, 115, 72),
        ACE_3("王牌3", 70, 70000, 929, 207, 136, 86),
        ACE_4("王牌4", 75, 80000, 1099, 244, 161, 102),
        HERO("英雄", 80, 100000, 1309, 291, 192, 121);

        public final String displayName;
        public final int requiredLevel;
        public final int nutCost;
        public final int maxHealth;
        public final int attackDamage;
        public final int defense;
        public final int critical;

        Promotion(String displayName, int requiredLevel, int nutCost, int maxHealth, int attackDamage, int defense, int critical) {
            this.displayName = displayName;
            this.requiredLevel = requiredLevel;
            this.nutCost = nutCost;
            this.maxHealth = maxHealth;
            this.attackDamage = attackDamage;
            this.defense = defense;
            this.critical = critical;
        }

        public int getCritical() {
            return critical;
        }

        public String tierName() {
            int i = ordinal();
            if (i == 0) return "列兵";
            if (i <= 2) return "军士";
            if (i <= 5) return "精锐";
            if (i <= 8) return "特种";
            if (i <= 12) return "王牌";
            return "英雄";
        }

        public int tierColor() {
            int i = ordinal();
            if (i == 0) return 0xFF9AA0A6;
            if (i <= 2) return 0xFF4C8DFF;
            if (i <= 5) return 0xFFB066FF;
            if (i <= 8) return 0xFFFF7A45;
            if (i <= 12) return 0xFFD23A3A;
            return 0xFFFFC53D;
        }

        public int pipsTotal() {
            int i = ordinal();
            if (i == 0) return 0;
            if (i <= 2) return 2;
            if (i <= 5) return 3;
            if (i <= 8) return 3;
            return 4;
        }

        public int pipsLit() {
            int i = ordinal();
            if (i == 0) return 0;
            if (i <= 2) return i;
            if (i <= 5) return i - 2;
            if (i <= 8) return i - 5;
            if (i <= 12) return i - 8;
            return 4;
        }

        public static Promotion byIndex(int index) {
            Promotion[] values = values();
            if (index < 0 || index >= values.length) {
                return PRIVATE;
            }
            return values[index];
        }

        public static int maxIndex() {
            return values().length - 1;
        }
    }

    public static final int BASE_EXP_PER_LEVEL = 88;
    public static final int EXP_STEP_PER_LEVEL = 156;

    public static int getExpForLevel(int level) {
        if (level >= MAX_LEVEL) return 0;
        return BASE_EXP_PER_LEVEL + level * EXP_STEP_PER_LEVEL;
    }

    public boolean canLevelUp() {
        return level < MAX_LEVEL && exp >= getExpForLevel(level);
    }

    public boolean canPromote() {
        Promotion next = Promotion.byIndex(promotionStage + 1);
        return promotionStage < Promotion.maxIndex()
                && level >= next.requiredLevel
                && nuts >= next.nutCost;
    }

    public Promotion getCurrentPromotion() {
        return Promotion.byIndex(promotionStage);
    }

    public Promotion getNextPromotion() {
        return Promotion.byIndex(promotionStage + 1);
    }

    public void addExp(int amount) {
        if (level >= MAX_LEVEL) return;
        this.exp += amount;
        this.totalExp += amount;
        while (level < MAX_LEVEL && this.exp >= getExpForLevel(level)) {
            this.exp -= getExpForLevel(level);
            this.level++;
        }
        if (level >= MAX_LEVEL) {
            this.exp = 0;
        }
    }

    public boolean tryPromote() {
        if (!canPromote()) return false;
        Promotion next = Promotion.byIndex(promotionStage + 1);
        this.nuts -= next.nutCost;
        this.promotionStage++;
        return true;
    }

    public void addNuts(int amount) {
        this.nuts += amount;
    }

    public void addSkillPoints(int amount) {
        this.skillPoints += amount;
    }

    public boolean tryConsumeSkillPoint(int n) {
        if (this.skillPoints < n) return false;
        this.skillPoints -= n;
        return true;
    }

    public static final double HEALTH_PER_LEVEL = 2000.0 / MAX_LEVEL;
    public static final double ATTACK_PER_LEVEL = 500.0 / MAX_LEVEL;
    public static final double DEFENSE_PER_LEVEL = 300.0 / MAX_LEVEL;
    public static final double CRITICAL_PER_LEVEL = 200.0 / MAX_LEVEL;

    public double getMaxHealthBonus() {
        return getCurrentPromotion().maxHealth + level * HEALTH_PER_LEVEL;
    }

    public double getAttackDamageBonus() {
        return getCurrentPromotion().attackDamage + level * ATTACK_PER_LEVEL;
    }

    public double getDefenseBonus() {
        return getCurrentPromotion().defense + level * DEFENSE_PER_LEVEL;
    }

    public double getCriticalBonus() {
        return getCurrentPromotion().critical + level * CRITICAL_PER_LEVEL;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("level", level);
        tag.putInt("exp", exp);
        tag.putInt("totalExp", totalExp);
        tag.putInt("nuts", nuts);
        tag.putInt("skillPoints", skillPoints);
        tag.putInt("promotionStage", promotionStage);
        return tag;
    }

    public static PlayerUpgradeData fromTag(CompoundTag tag) {
        PlayerUpgradeData data = new PlayerUpgradeData();
        data.level = tag.getInt("level");
        data.exp = tag.getInt("exp");
        data.totalExp = tag.getInt("totalExp");
        data.nuts = tag.getInt("nuts");
        data.skillPoints = tag.getInt("skillPoints");
        data.promotionStage = tag.getInt("promotionStage");
        return data;
    }

    public static PlayerUpgradeData get(Player player) {
        CompoundTag persistent = player.getPersistentData();
        if (!persistent.contains("IcyLibPlayerUpgradeData")) {
            return new PlayerUpgradeData();
        }
        return fromTag(persistent.getCompound("IcyLibPlayerUpgradeData"));
    }

    public static void set(Player player, PlayerUpgradeData data) {
        player.getPersistentData().put("IcyLibPlayerUpgradeData", data.toTag());
    }
}
