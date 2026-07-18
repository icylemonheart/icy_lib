package com.icylib.upgrade;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public interface IUpgradeableItem {
    Optional<AwarenessUpgradeStats> getUpgradeStats(ItemStack stack);

    void setUpgradeStats(ItemStack stack, AwarenessUpgradeStats stats);

    Optional<Upgrade> getUpgrade(ItemStack stack);

    void setUpgrade(ItemStack stack, Upgrade upgrade);

    int getRank();

    boolean canUpgrade(ItemStack stack);

    boolean canBreakthrough(ItemStack stack);

    boolean tryUpgrade(ItemStack stack, Player player);

    boolean tryBreakthrough(ItemStack stack, Player player, List<ItemStack> materials);

    int getExpForConversion(ItemStack stack);

    BiFunction<AwarenessUpgradeStats, Integer, Double>[] getAttributeGrowthFunctions();

    List<ItemStack> getBreakthroughMaterials(ItemStack stack, int stage);
}
