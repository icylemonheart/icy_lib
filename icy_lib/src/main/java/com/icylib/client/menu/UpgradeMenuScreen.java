package com.icylib.client.menu;

import com.icylib.client.upgrade.IUpgradeScreenRenderer;
import com.icylib.client.upgrade.UpgradeScreenRendererManager;
import com.icylib.menu.UpgradeMenu;
import com.icylib.upgrade.AwarenessUpgradeManager;
import com.icylib.upgrade.AwarenessUpgradeStats;
import com.icylib.upgrade.IUpgradeableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class UpgradeMenuScreen extends AbstractContainerScreen<UpgradeMenu> {
    private Button upgradeButton;
    private Button breakthroughButton;
    private IUpgradeScreenRenderer renderer;

    public UpgradeMenuScreen(UpgradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 200;
        this.imageHeight = 248;
    }

    @Override
    protected void init() {
        super.init();
        renderer = UpgradeScreenRendererManager.getRenderer();
        renderer.init(this);

        upgradeButton = Button.builder(Component.literal("升级"), button -> {
            if (minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            }
        })
                .bounds(leftPos + 8, topPos + 8, 70, 22)
                .build();
        breakthroughButton = Button.builder(Component.literal("突破"), button -> {
            if (minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
            }
        })
                .bounds(leftPos + 86, topPos + 8, 70, 22)
                .build();
        addRenderableWidget(upgradeButton);
        addRenderableWidget(breakthroughButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (renderer != null) {
            renderer.renderBackground(guiGraphics, x, y, imageWidth, imageHeight, partialTick);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 4210752, false);
        guiGraphics.drawString(font, playerInventoryTitle, 8, imageHeight - 94, 4210752, false);

        ItemStack stack = menu.slots.get(0).getItem();
        boolean canUpgrade = false;
        boolean canBreakthrough = false;
        boolean materialMode = false;
        AwarenessUpgradeStats stats = null;

        int expCost = menu.getExpCost();
        int currentPlayerExp = minecraft.player != null ? minecraft.player.experienceLevel : 0;

        if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeableItem upgradeable) {
            materialMode = upgradeable.upgradeWithMaterials();
            var statsOpt = upgradeable.getUpgradeStats(stack);
            if (statsOpt.isPresent()) {
                stats = statsOpt.get();
                int maxLevel = AwarenessUpgradeManager.getMaxLevel(stats.stage, stats.rank);
                int maxStage = AwarenessUpgradeManager.getMaxStage(stats.rank);
                String levelText = "阶段" + stats.stage + "/" + maxStage + "-Lv." + stats.level + "/" + maxLevel;
                guiGraphics.drawString(font, Component.literal(levelText).withStyle(stats.level >= maxLevel && stats.stage >= maxStage ? ChatFormatting.GOLD : ChatFormatting.WHITE), 100, 52, 4210752, false);

                if (materialMode) {
                    if (stats.level < maxLevel) {
                        List<ItemStack> upgradeMaterials = upgradeable.getUpgradeMaterials(stack);
                        int yPos = 68;
                        for (ItemStack material : upgradeMaterials) {
                            String materialName = material.getItem().getDescriptionId();
                            String materialText = "升级材料: " + Component.translatable(materialName).getString() + " x" + material.getCount();
                            guiGraphics.drawString(font, Component.literal(materialText).withStyle(ChatFormatting.YELLOW), 100, yPos, 4210752, false);
                            yPos += 14;
                        }
                    }
                } else {
                    String expCostText = "所需经验: " + expCost;
                    guiGraphics.drawString(font, Component.literal(expCostText).withStyle(expCost <= currentPlayerExp ? ChatFormatting.GREEN : ChatFormatting.RED), 100, 68, 4210752, false);

                    String playerExpText = "当前经验: " + currentPlayerExp;
                    guiGraphics.drawString(font, Component.literal(playerExpText).withStyle(ChatFormatting.BLUE), 100, 84, 4210752, false);
                }

                if (stats.level < maxLevel) {
                    String nextLevelText = "升级后阶段" + stats.stage + "-Lv." + (stats.level + 1);
                    guiGraphics.drawString(font, Component.literal(nextLevelText).withStyle(ChatFormatting.GRAY), 100, 100, 4210752, false);
                } else if (stats.stage < maxStage) {
                    String breakthroughText = "已满级，可突破至阶段" + (stats.stage + 1);
                    guiGraphics.drawString(font, Component.literal(breakthroughText).withStyle(ChatFormatting.GOLD), 100, 100, 4210752, false);

                    List<ItemStack> materials = upgradeable.getBreakthroughMaterials(stack, stats.stage);
                    if (!materials.isEmpty()) {
                        int yPos = 116;
                        for (ItemStack material : materials) {
                            String materialName = material.getItem().getDescriptionId();
                            String materialText = "突破材料: " + Component.translatable(materialName).getString() + " x" + material.getCount();
                            guiGraphics.drawString(font, Component.literal(materialText).withStyle(ChatFormatting.YELLOW), 100, yPos, 4210752, false);
                            yPos += 14;
                        }
                    }
                } else {
                    String maxText = "已满阶满级";
                    guiGraphics.drawString(font, Component.literal(maxText).withStyle(ChatFormatting.GOLD), 100, 100, 4210752, false);
                }
            }

            canUpgrade = upgradeable.canUpgrade(stack);
            canBreakthrough = upgradeable.canBreakthrough(stack);
        }

        if (materialMode) {
            upgradeButton.active = canUpgrade && menu.hasEnoughUpgradeMaterials();
        } else {
            upgradeButton.active = expCost > 0 && expCost <= currentPlayerExp && canUpgrade;
        }
        breakthroughButton.active = menu.getBreakthroughCost() > 0 && menu.hasEnoughMaterials() && canBreakthrough;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (renderer != null) {
            renderer.renderAdditional(guiGraphics, this, partialTick, mouseX, mouseY);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
