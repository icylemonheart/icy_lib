package com.icylib.upgrade;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;

import java.util.List;

public class UpgradeBoostItem extends RankedItem {
    public final UpgradeType type;

    public UpgradeBoostItem(Properties props, int rank, UpgradeType type) {
        super(props, rank);
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("item.icy_lib.boost_attribute.desc", type.attributeIndex + 1, type.attributeType));
        super.appendHoverText(stack, context, list, flag);
    }
}
