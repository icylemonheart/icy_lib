package com.icylib.upgrade;

import com.icylib.quality.IQualityItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class RankedItem extends Item implements IQualityItem {
    protected static Rarity getRarity(int rank) {
        return rank <= 2 ? Rarity.UNCOMMON : rank <= 4 ? Rarity.RARE : Rarity.EPIC;
    }

    public final int rank;

    public RankedItem(Properties props, int rank) {
        super(props.rarity(getRarity(rank)));
        this.rank = rank;
    }

    @Override
    public int getQualityRank(ItemStack stack) {
        return rank;
    }
}
