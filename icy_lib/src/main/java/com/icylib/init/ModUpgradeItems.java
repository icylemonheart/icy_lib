package com.icylib.init;

import com.icylib.IcyLib;
import com.icylib.upgrade.ExpItem;
import com.icylib.upgrade.UpgradeBoostItem;
import com.icylib.upgrade.UpgradeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModUpgradeItems {
    public static final DeferredRegister<Item> UPGRADE_ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, IcyLib.MODID);

    public static final DeferredHolder<Item, ExpItem> EXP_ITEM_1 =
            UPGRADE_ITEMS.register("exp_item_1", () -> new ExpItem(new Item.Properties(), 1));
    public static final DeferredHolder<Item, ExpItem> EXP_ITEM_2 =
            UPGRADE_ITEMS.register("exp_item_2", () -> new ExpItem(new Item.Properties(), 2));
    public static final DeferredHolder<Item, ExpItem> EXP_ITEM_3 =
            UPGRADE_ITEMS.register("exp_item_3", () -> new ExpItem(new Item.Properties(), 3));
    public static final DeferredHolder<Item, ExpItem> EXP_ITEM_4 =
            UPGRADE_ITEMS.register("exp_item_4", () -> new ExpItem(new Item.Properties(), 4));
    public static final DeferredHolder<Item, ExpItem> EXP_ITEM_5 =
            UPGRADE_ITEMS.register("exp_item_5", () -> new ExpItem(new Item.Properties(), 5));

    public static final DeferredHolder<Item, UpgradeBoostItem> BOOST_ATTRIBUTE_1_1 =
            UPGRADE_ITEMS.register("boost_attribute_1_1", () -> new UpgradeBoostItem(new Item.Properties(), 1, UpgradeType.attribute(0, "health")));
    public static final DeferredHolder<Item, UpgradeBoostItem> BOOST_ATTRIBUTE_1_2 =
            UPGRADE_ITEMS.register("boost_attribute_1_2", () -> new UpgradeBoostItem(new Item.Properties(), 2, UpgradeType.attribute(0, "health")));
    public static final DeferredHolder<Item, UpgradeBoostItem> BOOST_ATTRIBUTE_1_3 =
            UPGRADE_ITEMS.register("boost_attribute_1_3", () -> new UpgradeBoostItem(new Item.Properties(), 3, UpgradeType.attribute(0, "health")));
    public static final DeferredHolder<Item, UpgradeBoostItem> BOOST_ATTRIBUTE_2_1 =
            UPGRADE_ITEMS.register("boost_attribute_2_1", () -> new UpgradeBoostItem(new Item.Properties(), 1, UpgradeType.attribute(1, "attack")));
    public static final DeferredHolder<Item, UpgradeBoostItem> BOOST_ATTRIBUTE_2_2 =
            UPGRADE_ITEMS.register("boost_attribute_2_2", () -> new UpgradeBoostItem(new Item.Properties(), 2, UpgradeType.attribute(1, "attack")));
    public static final DeferredHolder<Item, UpgradeBoostItem> BOOST_ATTRIBUTE_2_3 =
            UPGRADE_ITEMS.register("boost_attribute_2_3", () -> new UpgradeBoostItem(new Item.Properties(), 3, UpgradeType.attribute(1, "attack")));
    public static final DeferredHolder<Item, UpgradeBoostItem> BOOST_ATTRIBUTE_3_1 =
            UPGRADE_ITEMS.register("boost_attribute_3_1", () -> new UpgradeBoostItem(new Item.Properties(), 1, UpgradeType.attribute(2, "defense")));
    public static final DeferredHolder<Item, UpgradeBoostItem> BOOST_ATTRIBUTE_3_2 =
            UPGRADE_ITEMS.register("boost_attribute_3_2", () -> new UpgradeBoostItem(new Item.Properties(), 2, UpgradeType.attribute(2, "defense")));
    public static final DeferredHolder<Item, UpgradeBoostItem> BOOST_ATTRIBUTE_3_3 =
            UPGRADE_ITEMS.register("boost_attribute_3_3", () -> new UpgradeBoostItem(new Item.Properties(), 3, UpgradeType.attribute(2, "defense")));
}
