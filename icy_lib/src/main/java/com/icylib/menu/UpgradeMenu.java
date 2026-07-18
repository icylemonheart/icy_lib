package com.icylib.menu;

import com.icylib.init.ModMenuTypes;
import com.icylib.upgrade.AwarenessUpgradeManager;
import com.icylib.upgrade.IUpgradeableItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class UpgradeMenu extends AbstractContainerMenu {
    private final SimpleContainer upgradeContainer;
    private final SimpleContainer materialsContainer;
    private final Player player;
    
    private final DataSlot experience;
    private final DataSlot expCost;
    private final DataSlot breakthroughCost;
    private boolean isProcessingSlotsChanged = false;
    
    public int getExpCost() {
        return expCost.get();
    }
    
    public int getBreakthroughCost() {
        return breakthroughCost.get();
    }

    public UpgradeMenu(int id, Inventory playerInventory) {
        super(ModMenuTypes.UPGRADE_MENU.get(), id);
        this.player = playerInventory.player;
        this.upgradeContainer = new SimpleContainer(1);
        this.materialsContainer = new SimpleContainer(9);

        addSlot(new Slot(upgradeContainer, 0, 80, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof IUpgradeableItem;
            }
            
            @Override
            public void set(ItemStack stack) {
                super.set(stack);
                if (!player.level().isClientSide) {
                    updateCosts();
                }
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int idx = i * 3 + j;
                addSlot(new Slot(materialsContainer, idx, 58 + j * 18, 84 + i * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return isValidBreakthroughMaterial(stack);
                    }

                    @Override
                    public int getMaxStackSize() {
                        return 64;
                    }
                });
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 142 + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            addSlot(new Slot(playerInventory, k, 8 + k * 18, 200));
        }

        this.experience = DataSlot.standalone();
        this.expCost = DataSlot.standalone();
        this.breakthroughCost = DataSlot.standalone();
        addDataSlot(this.experience);
        addDataSlot(this.expCost);
        addDataSlot(this.breakthroughCost);
        
        this.experience.set(getPlayerExp());
        updateCosts();
    }
    
    private void updateCosts() {
        ItemStack stack = upgradeContainer.getItem(0);
        int ec = 0;
        int breakthroughCost = 0;
        
        if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeableItem upgradeable) {
            var opt = upgradeable.getUpgradeStats(stack);
            if (opt.isPresent()) {
                var stats = opt.get();
                if (AwarenessUpgradeManager.canUpgrade(stats)) {
                    ec = AwarenessUpgradeManager.getExpForLevel(stats, stats.level);
                }
                if (AwarenessUpgradeManager.canBreakthrough(stats)) {
                    breakthroughCost = 1;
                }
            }
        }
        
        this.expCost.set(ec);
        this.breakthroughCost.set(breakthroughCost);
    }

    private boolean isValidBreakthroughMaterial(ItemStack stack) {
        ItemStack upgradeItem = upgradeContainer.getItem(0);
        if (upgradeItem.isEmpty() || !(upgradeItem.getItem() instanceof IUpgradeableItem upgradeable)) {
            return false;
        }
        return upgradeable.getUpgradeStats(upgradeItem).map(stats -> {
            List<ItemStack> required = upgradeable.getBreakthroughMaterials(upgradeItem, stats.stage);
            for (ItemStack requiredStack : required) {
                if (ItemStack.isSameItemSameTags(stack, requiredStack)) {
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }

    private int getPlayerExp() {
        return player.experienceLevel;
    }

    @Override
    public void slotsChanged(Container container) {
        if (player.level().isClientSide || isProcessingSlotsChanged) return;

        isProcessingSlotsChanged = true;
        
        updateCosts();
        
        isProcessingSlotsChanged = false;
        
        super.slotsChanged(container);
    }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (button == 0) {
            int cost = expCost.get();
            int playerExp = getPlayerExp();
            
            if (cost <= 0 || cost > playerExp) {
                return false;
            }
            
            ItemStack stack = upgradeContainer.getItem(0);
            if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeableItem upgradeable) {
                boolean success = upgradeable.tryUpgrade(stack, player);
                if (success) {
                    upgradeContainer.setItem(0, stack.copy());
                    experience.set(getPlayerExp());
                    updateCosts();
                }
            }
            return true;
        } else if (button == 1) {
            int breakthroughCostVal = breakthroughCost.get();
            
            if (breakthroughCostVal <= 0 || !hasEnoughMaterials()) {
                return false;
            }
            
            ItemStack stack = upgradeContainer.getItem(0);
            if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeableItem upgradeable) {
                List<ItemStack> materials = collectMaterials();
                boolean success = upgradeable.tryBreakthrough(stack, player, materials);
                if (success) {
                    upgradeContainer.setItem(0, stack.copy());
                    consumeMaterials();
                    updateCosts();
                }
            }
            return true;
        }
        return super.clickMenuButton(player, button);
    }

    private int recalculateExpCost() {
        ItemStack stack = upgradeContainer.getItem(0);
        if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeableItem upgradeable) {
            var opt = upgradeable.getUpgradeStats(stack);
            if (opt.isPresent()) {
                var stats = opt.get();
                if (AwarenessUpgradeManager.canUpgrade(stats)) {
                    return AwarenessUpgradeManager.getExpForLevel(stats, stats.level);
                }
            }
        }
        return 0;
    }
    
    private int recalculateBreakthroughCost() {
        ItemStack stack = upgradeContainer.getItem(0);
        if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeableItem upgradeable) {
            var opt = upgradeable.getUpgradeStats(stack);
            if (opt.isPresent()) {
                var stats = opt.get();
                if (AwarenessUpgradeManager.canBreakthrough(stats)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    private List<ItemStack> collectMaterials() {
        List<ItemStack> materials = new ArrayList<>();
        for (int i = 0; i < materialsContainer.getContainerSize(); i++) {
            ItemStack stack = materialsContainer.getItem(i);
            if (!stack.isEmpty()) {
                materials.add(stack.copy());
            }
        }
        return materials;
    }

    private void consumeMaterials() {
        ItemStack stack = upgradeContainer.getItem(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof IUpgradeableItem upgradeable)) {
            return;
        }
        upgradeable.getUpgradeStats(stack).ifPresent(stats -> {
            List<ItemStack> required = upgradeable.getBreakthroughMaterials(stack, stats.stage);
            for (ItemStack requiredStack : required) {
                int needed = requiredStack.getCount();
                for (int i = 0; i < materialsContainer.getContainerSize() && needed > 0; i++) {
                    ItemStack material = materialsContainer.getItem(i);
                    if (ItemStack.isSameItemSameTags(material, requiredStack)) {
                        int take = Math.min(needed, material.getCount());
                        material.shrink(take);
                        needed -= take;
                        if (material.isEmpty()) {
                            materialsContainer.setItem(i, ItemStack.EMPTY);
                        }
                    }
                }
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index == 0) {
            if (!moveItemStackTo(stack, 10, 46, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= 1 && index < 10) {
            if (!moveItemStackTo(stack, 10, 46, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (stack.getItem() instanceof IUpgradeableItem && !moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            if (isValidBreakthroughMaterial(stack) && !moveItemStackTo(stack, 1, 10, false)) {
                return ItemStack.EMPTY;
            }
            if (index < 38) {
                if (!moveItemStackTo(stack, 38, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 10, 38, false)) {
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
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            for (int i = 0; i < upgradeContainer.getContainerSize(); i++) {
                ItemStack stack = upgradeContainer.getItem(i);
                if (!stack.isEmpty()) {
                    player.drop(stack, false);
                    upgradeContainer.setItem(i, ItemStack.EMPTY);
                }
            }
            for (int i = 0; i < materialsContainer.getContainerSize(); i++) {
                ItemStack stack = materialsContainer.getItem(i);
                if (!stack.isEmpty()) {
                    player.drop(stack, false);
                    materialsContainer.setItem(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public boolean hasEnoughMaterials() {
        ItemStack stack = upgradeContainer.getItem(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof IUpgradeableItem upgradeable)) {
            return false;
        }
        return upgradeable.getUpgradeStats(stack).map(stats -> {
            List<ItemStack> required = upgradeable.getBreakthroughMaterials(stack, stats.stage);
            for (ItemStack requiredStack : required) {
                int found = 0;
                for (int i = 0; i < materialsContainer.getContainerSize(); i++) {
                    ItemStack material = materialsContainer.getItem(i);
                    if (ItemStack.isSameItemSameTags(material, requiredStack)) {
                        found += material.getCount();
                    }
                }
                if (found < requiredStack.getCount()) {
                    return false;
                }
            }
            return true;
        }).orElse(false);
    }
}
