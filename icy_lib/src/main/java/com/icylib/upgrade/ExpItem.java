package com.icylib.upgrade;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExpItem extends RankedItem {
    public ExpItem(Properties properties, int rank) {
        super(properties, rank);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("item.icy_lib.exp_item.desc", AwarenessUpgradeManager.getExpForConversion(rank, null)));
        super.appendHoverText(stack, level, list, flag);
    }
}
