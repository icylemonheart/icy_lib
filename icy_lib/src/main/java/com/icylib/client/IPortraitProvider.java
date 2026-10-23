package com.icylib.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface IPortraitProvider {
    ResourceLocation getPortraitLocation(ItemStack stack);

    int getPortraitWidth(ItemStack stack);

    int getPortraitHeight(ItemStack stack);
}
