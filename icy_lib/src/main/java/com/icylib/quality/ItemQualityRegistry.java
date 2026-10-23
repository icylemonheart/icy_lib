package com.icylib.quality;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemQualityRegistry {

    private static final Map<Item, Integer> RANKS_BY_ITEM = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Integer> RANKS_BY_ID = new ConcurrentHashMap<>();

    private ItemQualityRegistry() {
    }

    public static void register(Item item, int rank) {
        if (item != null) {
            RANKS_BY_ITEM.put(item, rank);
        }
    }

    public static void register(ResourceLocation itemId, int rank) {
        if (itemId != null) {
            RANKS_BY_ID.put(itemId, rank);
        }
    }

    public static void unregister(Item item) {
        if (item != null) {
            RANKS_BY_ITEM.remove(item);
        }
    }

    public static void unregister(ResourceLocation itemId) {
        if (itemId != null) {
            RANKS_BY_ID.remove(itemId);
        }
    }

    public static int getRank(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        Item item = stack.getItem();
        if (item instanceof IQualityItem qualityItem) {
            return qualityItem.getQualityRank(stack);
        }
        Integer rank = RANKS_BY_ITEM.get(item);
        if (rank != null) {
            return rank;
        }
        if (!RANKS_BY_ID.isEmpty()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            Integer byId = RANKS_BY_ID.get(id);
            if (byId != null) {
                return byId;
            }
        }
        return 0;
    }

    public static QualityTheme getTheme(ItemStack stack) {
        return QualityTheme.of(getRank(stack));
    }
}
