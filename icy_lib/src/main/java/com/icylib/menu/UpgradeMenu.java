package com.icylib.menu;

import com.icylib.init.ModMenuTypes;
import com.icylib.network.PlayerUpgradeNetworkHandler;
import com.icylib.upgrade.AwarenessUpgradeManager;
import com.icylib.upgrade.IUpgradeableItem;
import com.icylib.upgrade.ISkillWeapon;
import com.icylib.upgrade.PlayerUpgradeAttributes;
import com.icylib.upgrade.PlayerUpgradeData;
import com.icylib.upgrade.PlayerUpgradeItems;
import com.icylib.upgrade.UpgradeLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** 升级/突破菜单（全屏）：材料直接从背包读取消耗，中央槽（索引 0）放待升级物品或可提交资源。 */
public class UpgradeMenu extends AbstractContainerMenu {
    public final Player player;
    public final SimpleContainer upgradeContainer;
    private final DataSlot experience;
    private final DataSlot expCost;
    private final DataSlot breakthroughCost;
    private boolean isProcessingSlotsChanged = false;

    private final UpgradeLayout layout;

    private static int clientGuiWidth = 320;
    private static int clientGuiHeight = 240;

    public static void setClientScreenSize(int width, int height) {
        clientGuiWidth = width;
        clientGuiHeight = height;
    }

    public int getExpCost() {
        return expCost.get();
    }

    public int getBreakthroughCost() {
        return breakthroughCost.get();
    }

    public UpgradeLayout getLayout() {
        return layout;
    }

    public UpgradeMenu(int id, Inventory playerInventory) {
        super(ModMenuTypes.UPGRADE_MENU.get(), id);
        this.player = playerInventory.player;
        // 必须重写 setChanged，否则中央槽自动结算不触发
        this.upgradeContainer = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                UpgradeMenu.this.slotsChanged(this);
            }
        };

        boolean clientSide = playerInventory.player.level().isClientSide;
        this.layout = UpgradeLayout.compute(clientSide ? clientGuiWidth : 320, clientSide ? clientGuiHeight : 240);

        addSlot(new Slot(upgradeContainer, 0, layout.itemX, layout.itemY) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof IUpgradeableItem || PlayerUpgradeItems.isSubmittable(stack);
            }

            @Override
            public boolean mayPickup(Player player) {
                return true;
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9,
                        layout.invX + j * UpgradeLayout.SLOT, layout.invMainY + i * UpgradeLayout.SLOT));
            }
        }
        for (int k = 0; k < 9; k++) {
            addSlot(new Slot(playerInventory, k, layout.invX + k * UpgradeLayout.SLOT, layout.hotbarY));
        }

        experience = DataSlot.standalone();
        expCost = DataSlot.standalone();
        breakthroughCost = DataSlot.standalone();
        addDataSlot(experience);
        addDataSlot(expCost);
        addDataSlot(breakthroughCost);

        slotsChanged(upgradeContainer);
    }

    @Override
    public void slotsChanged(Container container) {
        if (isProcessingSlotsChanged) {
            super.slotsChanged(container);
            return;
        }
        isProcessingSlotsChanged = true;
        try {
            super.slotsChanged(container);
            if (container == upgradeContainer && player instanceof ServerPlayer sp) {
                ItemStack stack = upgradeContainer.getItem(0);
                experience.set(sp.experienceLevel);

                int nutValue = PlayerUpgradeItems.getNutValue(stack);
                int expValue = PlayerUpgradeItems.getExpValue(stack);
                int skillPointValue = PlayerUpgradeItems.getSkillPointValue(stack);
                if (!stack.isEmpty() && (nutValue > 0 || expValue > 0 || skillPointValue > 0)) {
                    PlayerUpgradeData data = PlayerUpgradeData.get(sp);
                    if (nutValue > 0) {
                        data.addNuts(nutValue * stack.getCount());
                        sp.displayClientMessage(Component.literal("提交了 " + (nutValue * stack.getCount()) + " 螺母")
                                .withStyle(ChatFormatting.GOLD), true);
                    } else if (expValue > 0) {
                        data.addExp(expValue * stack.getCount());
                        sp.displayClientMessage(Component.literal("提交了 " + (expValue * stack.getCount()) + " 点经验")
                                .withStyle(ChatFormatting.AQUA), true);
                    } else {
                        data.addSkillPoints(skillPointValue * stack.getCount());
                        sp.displayClientMessage(Component.literal("提交了 " + (skillPointValue * stack.getCount()) + " 技能点")
                                .withStyle(ChatFormatting.GREEN), true);
                    }
                    upgradeContainer.setItem(0, ItemStack.EMPTY);
                    PlayerUpgradeData.set(sp, data);
                    PlayerUpgradeAttributes.apply(sp, data);
                    PlayerUpgradeNetworkHandler.syncToClient(sp, data);
                    expCost.set(0);
                    breakthroughCost.set(0);
                    return;
                }

                if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeableItem upgradeable) {
                    if (upgradeable.upgradeWithMaterials()) {
                        expCost.set(0);
                    } else {
                        expCost.set(upgradeable.getUpgradeStats(stack)
                                .map(s -> AwarenessUpgradeManager.getExpForLevel(s, s.level))
                                .orElse(0));
                    }
                    List<ItemStack> materials = upgradeable.getBreakthroughMaterials(stack,
                            upgradeable.getUpgradeStats(stack).map(s -> s.stage).orElse(1));
                    breakthroughCost.set(materials.isEmpty() ? 0 : materials.get(0).getCount());
                } else {
                    expCost.set(0);
                    breakthroughCost.set(0);
                }
            }
        } finally {
            isProcessingSlotsChanged = false;
        }
    }

    public boolean hasEnoughUpgradeMaterials() {
        ItemStack stack = upgradeContainer.getItem(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof IUpgradeableItem upgradeable)) {
            return false;
        }
        List<ItemStack> required = upgradeable.getUpgradeMaterials(stack);
        if (required.isEmpty()) return false;
        for (ItemStack need : required) {
            int count = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack inv = player.getInventory().getItem(i);
                if (!inv.isEmpty() && ItemStack.isSameItem(inv, need)) {
                    count += inv.getCount();
                }
            }
            if (count < need.getCount()) return false;
        }
        return true;
    }

    public boolean hasEnoughMaterials() {
        ItemStack stack = upgradeContainer.getItem(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof IUpgradeableItem upgradeable)) {
            return false;
        }
        var statsOpt = upgradeable.getUpgradeStats(stack);
        if (statsOpt.isEmpty()) return false;
        List<ItemStack> required = upgradeable.getBreakthroughMaterials(stack, statsOpt.get().stage);
        if (required.isEmpty()) return false;
        for (ItemStack need : required) {
            int count = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack inv = player.getInventory().getItem(i);
                if (!inv.isEmpty() && ItemStack.isSameItem(inv, need)) {
                    count += inv.getCount();
                }
            }
            if (count < need.getCount()) return false;
        }
        return true;
    }

    private boolean hasEnoughItems(List<ItemStack> required) {
        for (ItemStack need : required) {
            int count = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack inv = player.getInventory().getItem(i);
                if (!inv.isEmpty() && ItemStack.isSameItem(inv, need)) {
                    count += inv.getCount();
                }
            }
            if (count < need.getCount()) return false;
        }
        return true;
    }

    public boolean hasEnoughAscensionMaterials() {
        ItemStack stack = upgradeContainer.getItem(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof ISkillWeapon weapon)) {
            return false;
        }
        List<ItemStack> required = weapon.getAscensionMaterials(stack);
        return !required.isEmpty() && hasEnoughItems(required);
    }

    private void consumeMaterials(List<ItemStack> required) {
        for (ItemStack need : required) {
            int remaining = need.getCount();
            for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
                ItemStack inv = player.getInventory().getItem(i);
                if (!inv.isEmpty() && ItemStack.isSameItem(inv, need)) {
                    int take = Math.min(remaining, inv.getCount());
                    inv.shrink(take);
                    remaining -= take;
                }
            }
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!(player instanceof ServerPlayer sp)) return false;
        ItemStack stack = upgradeContainer.getItem(0);

        // 专属武器：进阶（id=2）与技能升级（id=100+技能索引）
        if (!stack.isEmpty() && stack.getItem() instanceof ISkillWeapon weapon) {
            if (id == 2) {
                List<ItemStack> materials = weapon.getAscensionMaterials(stack);
                if (materials.isEmpty() || !hasEnoughItems(materials)) return false;
                consumeMaterials(materials);
                weapon.ascend(stack);
                sp.level().playSound(null, sp.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                        SoundSource.PLAYERS, 1.0f, 1.2f);
                slotsChanged(upgradeContainer);
                broadcastChanges();
                return true;
            }
            if (id >= 100 && id < 100 + ISkillWeapon.SKILL_COUNT) {
                int skillIndex = id - 100;
                if (!weapon.skillUpgradable(skillIndex)) return false;
                int lv = weapon.getSkillLevel(stack, skillIndex);
                if (lv >= ISkillWeapon.MAX_SKILL_LEVEL) return false;
                PlayerUpgradeData data = PlayerUpgradeData.get(sp);
                if (!data.tryConsumeSkillPoint(1)) return false;
                weapon.setSkillLevel(stack, skillIndex, lv + 1);
                PlayerUpgradeData.set(sp, data);
                PlayerUpgradeAttributes.apply(sp, data);
                PlayerUpgradeNetworkHandler.syncToClient(sp, data);
                sp.level().playSound(null, sp.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.PLAYERS, 1.0f, 1.5f);
                slotsChanged(upgradeContainer);
                broadcastChanges();
                return true;
            }
        }

        if (stack.isEmpty() || !(stack.getItem() instanceof IUpgradeableItem upgradeable)) {
            return false;
        }

        if (id == 0) {
            if (upgradeable.upgradeWithMaterials()) {
                List<ItemStack> required = upgradeable.getUpgradeMaterials(stack);
                if (required.isEmpty() || !hasEnoughUpgradeMaterials()) return false;
                if (!upgradeable.canUpgrade(stack)) return false;
                consumeMaterials(required);
            } else {
                if (!upgradeable.canUpgrade(stack)) return false;
            }
            boolean success = upgradeable.tryUpgrade(stack, sp);
            if (success) {
                slotsChanged(upgradeContainer);
                broadcastChanges();
            }
            return success;
        } else if (id == 1) {
            if (!upgradeable.canBreakthrough(stack)) return false;
            var statsOpt = upgradeable.getUpgradeStats(stack);
            if (statsOpt.isEmpty()) return false;
            List<ItemStack> materials = upgradeable.getBreakthroughMaterials(stack, statsOpt.get().stage);
            if (materials.isEmpty()) return false;
            if (!hasEnoughMaterials()) return false;
            consumeMaterials(materials);
            boolean success = upgradeable.tryBreakthrough(stack, sp, materials);
            if (success) {
                slotsChanged(upgradeContainer);
                broadcastChanges();
            }
            return success;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();

        if (index == 0) {
            if (!moveItemStackTo(stack, 1, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (stack.getItem() instanceof IUpgradeableItem || PlayerUpgradeItems.isSubmittable(stack)) {
                if (!moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            ItemStack stack = upgradeContainer.getItem(0);
            if (!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack);
                upgradeContainer.setItem(0, ItemStack.EMPTY);
            }
        }
    }
}
