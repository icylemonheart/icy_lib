package com.icylib.menu;

import com.icylib.IcyLib;
import com.icylib.init.ModMenuTypes;
import com.icylib.upgrade.IUpgradeableItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecycleMenu extends AbstractContainerMenu {
    private final Container inputContainer;
    private final Container outputContainer;
    private final Player player;
    private final List<ItemStack> recycleItems = new ArrayList<>();
    public int totalExp;

    public RecycleMenu(int id, Inventory playerInventory) {
        super(ModMenuTypes.RECYCLE_MENU.get(), id);
        this.player = playerInventory.player;
        this.inputContainer = new SimpleContainer(9);
        this.outputContainer = new SimpleContainer(1);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                addSlot(new Slot(inputContainer, index, 8 + j * 18, 18 + i * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.getItem() instanceof IUpgradeableItem;
                    }
                });
            }
        }

        addSlot(new Slot(outputContainer, 0, 134, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.totalExp = 0;
    }

    @Override
    public void slotsChanged(Container container) {
        if (player.level().isClientSide) return;

        totalExp = 0;
        recycleItems.clear();

        for (int i = 0; i < inputContainer.getContainerSize(); i++) {
            ItemStack stack = inputContainer.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeableItem upgradeable) {
                recycleItems.add(stack);
                totalExp += upgradeable.getExpForConversion(stack);
            }
        }

        updateOutput();
        super.slotsChanged(container);
    }

    private void updateOutput() {
        if (totalExp > 0) {
            int rank = Math.min(totalExp / 100 + 1, 5);
            ItemStack expItem = new ItemStack(IcyLib.EXP_ITEMS.get(rank - 1), totalExp / (rank * 100));
            outputContainer.setItem(0, expItem);
        } else {
            outputContainer.setItem(0, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (button == 0 && totalExp > 0 && !player.level().isClientSide) {
            for (int i = 0; i < inputContainer.getContainerSize(); i++) {
                inputContainer.setItem(i, ItemStack.EMPTY);
            }
            totalExp = 0;
            recycleItems.clear();
            updateOutput();
            return true;
        }
        return super.clickMenuButton(player, button);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < 9) {
            if (!moveItemStackTo(stack, 10, 46, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index == 9) {
            if (!moveItemStackTo(stack, 10, 46, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (stack.getItem() instanceof IUpgradeableItem) {
                for (int i = 0; i < 9; i++) {
                    if (inputContainer.getItem(i).isEmpty()) {
                        inputContainer.setItem(i, stack.copy());
                        stack.setCount(0);
                        slot.set(stack);
                        return copy;
                    }
                }
            }
            if (index < 36) {
                if (!moveItemStackTo(stack, 36, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 10, 36, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
