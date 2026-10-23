package com.icylib.quality;

import net.minecraft.world.item.ItemStack;

public interface IQualityItem {

    int getQualityRank(ItemStack stack);
}
