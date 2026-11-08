package com.icylib.client;

import com.icylib.upgrade.PlayerUpgradeItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

// 客户端：为可提交物品（螺母包/经验仓）添加 tooltip
@OnlyIn(Dist.CLIENT)
public class PlayerUpgradeSubmitHandler {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        int nut = PlayerUpgradeItems.getNutValue(stack);
        int exp = PlayerUpgradeItems.getExpValue(stack);
        if (nut > 0) {
            event.getToolTip().add(Component.literal("放入强化界面中央槽位提交：" + nut + " 螺母/个")
                    .withStyle(ChatFormatting.GOLD));
        } else if (exp > 0) {
            event.getToolTip().add(Component.literal("放入强化界面中央槽位提交：" + exp + " 升级经验/个")
                    .withStyle(ChatFormatting.AQUA));
        }
    }
}
