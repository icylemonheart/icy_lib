package com.icylib.upgrade;

import com.icylib.menu.UpgradeMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class UpgradeUIHelper {
    public static void openUpgradeMenu(Player player) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("升级");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, net.minecraft.world.entity.player.Inventory inventory, Player player) {
                return new UpgradeMenu(windowId, inventory);
            }
        });
    }
}
