package com.icylib.upgrade;

import com.icylib.menu.UpgradeMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;

public class UpgradeUIHelper {
    public static void openUpgradeMenu(Player player) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("Upgrade");
            }

            @Override
            public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int windowId, net.minecraft.world.entity.player.Inventory inventory, Player player) {
                return new UpgradeMenu(windowId, inventory);
            }
        });
    }
}
