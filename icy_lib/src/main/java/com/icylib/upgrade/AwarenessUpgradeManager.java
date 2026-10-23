package com.icylib.upgrade;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AwarenessUpgradeManager {
    private static final int BASE_EXP_COST = 1;
    private static final int EXP_LEVEL_INCREMENT = 1;
    private static final int EXP_RANK_MULTIPLIER = 2;
    private static final int BASE_EXP_CONVERSION = 5;
    private static final double EXP_CONVERSION_RANK_FACTOR = 2.0;

    private static Function<Player, Integer> expProvider = player -> player.experienceLevel;
    private static BiFunction<AwarenessUpgradeStats, Integer, Integer> expCostCalculator = (stats, level) ->
            BASE_EXP_COST + level * EXP_LEVEL_INCREMENT + (stats.rank - 1) * EXP_RANK_MULTIPLIER;

    public static void setExpProvider(Function<Player, Integer> provider) {
        expProvider = provider;
    }

    public static void setExpCostCalculator(BiFunction<AwarenessUpgradeStats, Integer, Integer> calculator) {
        expCostCalculator = calculator;
    }

    public static int getPlayerExp(Player player) {
        return expProvider.apply(player);
    }

    public static int getExpForLevel(AwarenessUpgradeStats stats, int level) {
        if (stats == null || stats.rank <= 0) {
            return BASE_EXP_COST + level * EXP_LEVEL_INCREMENT;
        }
        return expCostCalculator.apply(stats, level);
    }

    public static int getExpForConversion(int rank, AwarenessUpgradeStats stats) {
        double baseExp = BASE_EXP_CONVERSION * Math.pow(EXP_CONVERSION_RANK_FACTOR, rank - 1);
        if (stats == null) {
            return (int) Math.round(baseExp);
        }
        return (int) Math.round(baseExp + stats.totalExp);
    }

    public static int getMaxStage(int rank) {
        return switch (rank) {
            case 2 -> 1;
            case 3 -> 3;
            case 4 -> 5;
            case 5 -> 5;
            case 6 -> 5;
            default -> 10;
        };
    }

    public static int getMaxLevel(int stage, int rank) {
        return switch (rank) {
            case 2 -> switch (stage) {
                case 1 -> 10;
                default -> 10;
            };
            case 3 -> switch (stage) {
                case 1 -> 10;
                case 2 -> 20;
                case 3 -> 25;
                default -> 25;
            };
            case 4 -> switch (stage) {
                case 1 -> 10;
                case 2 -> 20;
                case 3 -> 25;
                case 4 -> 30;
                case 5 -> 35;
                default -> 35;
            };
            case 5 -> switch (stage) {
                case 1 -> 10;
                case 2 -> 20;
                case 3 -> 30;
                case 4 -> 40;
                case 5 -> 45;
                default -> 45;
            };
            case 6 -> switch (stage) {
                case 1 -> 10;
                case 2 -> 20;
                case 3 -> 30;
                case 4 -> 40;
                case 5 -> 45;
                default -> 45;
            };
            default -> 5 + stage * 5;
        };
    }

    public static int getMaxLevel(int stage) {
        return 5 + stage * 5;
    }

    public static boolean canUpgrade(AwarenessUpgradeStats stats) {
        return stats != null && stats.level < getMaxLevel(stats.stage, stats.rank);
    }

    public static boolean canBreakthrough(AwarenessUpgradeStats stats) {
        return stats != null && stats.level >= getMaxLevel(stats.stage, stats.rank) && stats.stage < getMaxStage(stats.rank);
    }

    public static boolean tryUpgrade(AwarenessUpgradeStats stats, Player player,
            BiFunction<AwarenessUpgradeStats, Integer, Double>[] attributeGrowthFunctions) {
        if (!canUpgrade(stats)) {
            return false;
        }

        int cost = getExpForLevel(stats, stats.level);
        int playerExp = getPlayerExp(player);

        if (playerExp < cost) {
            return false;
        }

        player.giveExperienceLevels(-cost);
        stats.exp += cost;

        int maxLevel = getMaxLevel(stats.stage, stats.rank);
        while (stats.level < maxLevel) {
            int expNeeded = getExpForLevel(stats, stats.level);
            if (stats.exp < expNeeded) {
                break;
            }
            stats.exp -= expNeeded;
            stats.totalExp += expNeeded;
            stats.level++;

            if (attributeGrowthFunctions != null) {
                for (int i = 0; i < stats.attributes.size() && i < attributeGrowthFunctions.length; i++) {
                    double growth = attributeGrowthFunctions[i].apply(stats, stats.level);
                    stats.attributes.get(i).addValue(growth);
                }
            }
        }

        if (stats.level >= maxLevel) {
            stats.exp = 0;
        }

        return true;
    }

    public static boolean tryBreakthrough(AwarenessUpgradeStats stats, Player player,
            List<ItemStack> materials,
            BiFunction<AwarenessUpgradeStats, Integer, Double>[] attributeGrowthFunctions) {
        if (!canBreakthrough(stats)) {
            return false;
        }

        stats.stage++;
        stats.level = 0;
        stats.exp = 0;

        if (attributeGrowthFunctions != null) {
            for (int i = 0; i < stats.attributes.size() && i < attributeGrowthFunctions.length; i++) {
                double growth = attributeGrowthFunctions[i].apply(stats, 0);
                stats.attributes.get(i).addValue(growth);
            }
        }

        return true;
    }

    public static int getTotalLevels(int stage) {
        int total = 0;
        for (int i = 1; i <= stage; i++) {
            total += getMaxLevel(i);
        }
        return total;
    }
}
