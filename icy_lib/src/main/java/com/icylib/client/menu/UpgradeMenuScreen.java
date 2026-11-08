package com.icylib.client.menu;

import com.icylib.client.gui.IcyGuiStyle;
import com.icylib.client.gui.PgrButton;
import com.icylib.client.upgrade.IUpgradeScreenRenderer;
import com.icylib.client.upgrade.UpgradeScreenRendererManager;
import com.icylib.menu.UpgradeMenu;
import com.icylib.network.ClientUpgradeCache;
import com.icylib.network.PlayerUpgradeNetworkHandler;
import com.icylib.upgrade.AwarenessUpgradeManager;
import com.icylib.upgrade.AwarenessUpgradeStats;
import com.icylib.upgrade.ISkillWeapon;
import com.icylib.upgrade.IUpgradeableItem;
import com.icylib.upgrade.PlayerUpgradeData;
import com.icylib.upgrade.UpgradeLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

// 升级与突破全屏界面：布局由 UpgradeLayout 计算，槽位坐标在菜单创建时固定后整块画布居中
public class UpgradeMenuScreen extends AbstractContainerScreen<UpgradeMenu> {
    private static final int LINE_HEIGHT = 14;

    private PgrButton upgradeButton;
    private PgrButton breakthroughButton;
    private PgrButton levelUpButton;
    private PgrButton promoteButton;
    private final PgrButton[] skillButtons = new PgrButton[ISkillWeapon.SKILL_COUNT];
    private PgrButton skillUpgradeButton;
    private PgrButton ascendButton;
    private int selectedSkill = 0;
    private IUpgradeScreenRenderer renderer;
    private UpgradeLayout layout;

    public UpgradeMenuScreen(UpgradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 200;
        this.imageHeight = 248;
    }

    @Override
    protected void init() {
        this.imageWidth = this.width;
        this.imageHeight = this.height;
        super.init();

        this.layout = menu.getLayout();

        this.leftPos = (this.width - layout.width) / 2;
        this.topPos = (this.height - layout.height) / 2;

        renderer = UpgradeScreenRendererManager.getRenderer();
        if (renderer != null) {
            renderer.init(this);
        }

        int bx = leftPos + 24;
        int by = topPos + 9;
        upgradeButton = new PgrButton(bx, by, 80, 22, Component.literal("强化"), button -> {
            if (minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            }
        });
        breakthroughButton = new PgrButton(bx + 88, by, 80, 22, Component.literal("突破"), button -> {
            if (minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
            }
        });

        int panelBtnY = topPos + layout.panelY + layout.panelH - 28;
        levelUpButton = new PgrButton(leftPos + layout.panelX + 4, panelBtnY, 64, 20, Component.literal("升级"), button -> {
            PlayerUpgradeNetworkHandler.sendUpgrade();
        });
        promoteButton = new PgrButton(leftPos + layout.panelX + layout.panelW - 68, panelBtnY, 64, 20, Component.literal("晋升"), button -> {
            PlayerUpgradeNetworkHandler.sendPromote();
        });

        for (int i = 0; i < ISkillWeapon.SKILL_COUNT; i++) {
            final int index = i;
            int row = i <= 2 ? 0 : (i <= 4 ? 1 : 2);
            int col = i <= 2 ? i : (i <= 4 ? i - 2 : i - 5);
            PgrButton skillBtn = new PgrButton(leftPos + layout.skillX + col * 60, topPos + layout.skillY + row * 26,
                    54, 20, Component.empty(), button -> selectedSkill = index);
            skillBtn.visible = false;
            skillButtons[i] = skillBtn;
            addRenderableWidget(skillBtn);
        }
        skillUpgradeButton = new PgrButton(
                leftPos + layout.skillX,
                topPos + layout.skillY + 78,
                174, 20, Component.literal("升级技能"), button -> {
            if (minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 100 + selectedSkill);
            }
        });
        skillUpgradeButton.visible = false;
        int ascEffectW = layout.skillX - 24 - layout.weaponAscX;
        ascendButton = new PgrButton(leftPos + layout.weaponAscX + Math.max(0, ascEffectW - 56),
                topPos + layout.weaponAscY, 56, 20, Component.literal("进阶"), button -> {
            if (minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 2);
            }
        });
        ascendButton.visible = false;

        addRenderableWidget(upgradeButton);
        addRenderableWidget(breakthroughButton);
        addRenderableWidget(levelUpButton);
        addRenderableWidget(promoteButton);
        addRenderableWidget(skillUpgradeButton);
        addRenderableWidget(ascendButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        if (renderer != null) {
            renderer.renderBackground(guiGraphics, 0, 0, this.width, this.height, partialTick);
        } else {
            guiGraphics.fill(0, 0, this.width, this.height, 0xE0101418);
        }

        int canvasRight = leftPos + layout.width;
        int canvasBottom = topPos + layout.height;

        int panelX = leftPos + 12;
        int panelY = topPos + UpgradeLayout.HEADER_HEIGHT + 6;
        int panelW = layout.width - 24;
        int panelH = layout.panelY - 4 - UpgradeLayout.HEADER_HEIGHT - 6;
        if (panelH > 8) {
            IcyGuiStyle.panel(guiGraphics, panelX, panelY, panelW, panelH);
        }

        guiGraphics.fill(leftPos, topPos, canvasRight, topPos + UpgradeLayout.HEADER_HEIGHT, IcyGuiStyle.PANEL);
        guiGraphics.fill(leftPos, topPos, canvasRight, topPos + 2, IcyGuiStyle.ACCENT);
        guiGraphics.fill(leftPos, topPos + UpgradeLayout.HEADER_HEIGHT,
                canvasRight, topPos + UpgradeLayout.HEADER_HEIGHT + 1, IcyGuiStyle.BORDER_DIM);

        guiGraphics.fill(leftPos, topPos + layout.bottomBarTop, canvasRight, canvasBottom, IcyGuiStyle.PANEL);
        guiGraphics.fill(leftPos + layout.panelX + layout.panelW + 4, topPos + layout.bottomBarTop,
                canvasRight, topPos + layout.bottomBarTop + 1, IcyGuiStyle.BORDER_DIM);
        guiGraphics.fill(leftPos, canvasBottom - 2, canvasRight, canvasBottom, IcyGuiStyle.ACCENT_DIM);

        int pX = leftPos + layout.panelX;
        int pY = topPos + layout.panelY;
        IcyGuiStyle.panel(guiGraphics, pX, pY, layout.panelW, layout.panelH);

        int nutPanelW = 70;
        int nutPanelH = 20;
        int nutPanelX = leftPos + layout.width - nutPanelW - 12;
        int nutPanelY = topPos + 44;
        IcyGuiStyle.panel(guiGraphics, nutPanelX, nutPanelY, nutPanelW, nutPanelH);

        IcyGuiStyle.panel(guiGraphics, nutPanelX, nutPanelY + 24, nutPanelW, nutPanelH);

        ItemStack centerStack = this.menu.slots.get(0).getItem();
        if (!centerStack.isEmpty() && centerStack.getItem() instanceof ISkillWeapon w
                && w.supportsSkillUpgrade(centerStack)) {
            if (layout.skillDescH >= 24) {
                IcyGuiStyle.frame(guiGraphics, leftPos + layout.skillDescX - 4, topPos + layout.skillDescY - 4,
                        layout.skillDescW, layout.skillDescH, IcyGuiStyle.BORDER_DIM);
            }
            guiGraphics.fill(leftPos + layout.skillX - 12, topPos + layout.centerY + 8,
                    leftPos + layout.skillX - 11, topPos + layout.centerY + layout.centerH - 8,
                    IcyGuiStyle.BORDER_DIM);
        }

        for (Slot slot : this.menu.slots) {
            IcyGuiStyle.slot(guiGraphics, leftPos + slot.x, topPos + slot.y, slot.index == 0);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int titleCenter = layout.width / 2;
        guiGraphics.drawCenteredString(font, title, titleCenter, 15, IcyGuiStyle.TEXT);
        int titleHalf = font.width(title) / 2 + 12;
        guiGraphics.fill(titleCenter - titleHalf, 25, titleCenter + titleHalf, 27, IcyGuiStyle.ACCENT);
        guiGraphics.drawString(font, playerInventoryTitle,
                layout.invX, layout.invMainY - 12, IcyGuiStyle.TEXT_DIM, false);

        PlayerUpgradeData data = ClientUpgradeCache.get();
        int px = layout.panelX + 6;
        int py = layout.panelY + 6;

        guiGraphics.drawString(font, Component.literal("Lv." + data.level + "/" + PlayerUpgradeData.MAX_LEVEL)
                .withStyle(ChatFormatting.WHITE), px, py, IcyGuiStyle.TEXT, false);
        py += 12;

        int expForLevel = PlayerUpgradeData.getExpForLevel(data.level);
        String expText = data.level >= PlayerUpgradeData.MAX_LEVEL ? "MAX" : data.exp + "/" + expForLevel;
        guiGraphics.drawString(font, Component.literal("经验: " + expText)
                .withStyle(ChatFormatting.GRAY), px, py, IcyGuiStyle.TEXT_DIM, false);
        py += 12;

        String totalText = "属性: +" + (int) data.getMaxHealthBonus() + "生 +" + (int) data.getAttackDamageBonus()
                + "攻 +" + (int) data.getDefenseBonus() + "防 +" + (int) data.getCriticalBonus() + "会";
        guiGraphics.drawString(font, Component.literal(totalText)
                .withStyle(ChatFormatting.AQUA), px, py, IcyGuiStyle.TEXT_DIM, false);
        py += 12;

        PlayerUpgradeData.Promotion current = data.getCurrentPromotion();
        guiGraphics.drawString(font, "阶级: ", px, py, IcyGuiStyle.TEXT_DIM, false);
        int rx = px + font.width("阶级: ");
        guiGraphics.drawString(font, Component.literal(current.tierName())
                .withStyle(ChatFormatting.BOLD), rx, py, current.tierColor(), false);
        rx += font.width(Component.literal(current.tierName()).withStyle(ChatFormatting.BOLD)) + 4;
        for (int i = 0; i < current.pipsTotal(); i++) {
            IcyGuiStyle.rankBadge(guiGraphics, rx, py + 1, i < current.pipsLit(), current.tierColor());
            rx += 9;
        }
        py += 12;

        if (data.promotionStage < PlayerUpgradeData.Promotion.maxIndex()) {
            PlayerUpgradeData.Promotion next = data.getNextPromotion();
            String reqText = "晋升: Lv." + next.requiredLevel + " 螺母:" + next.nutCost;
            guiGraphics.drawString(font, Component.literal(reqText)
                    .withStyle(ChatFormatting.YELLOW), px, py, IcyGuiStyle.TEXT_DIM, false);
            py += 12;

            String attrText = "+" + next.maxHealth + "生 +" + next.attackDamage + "攻 +" + next.defense
                    + "防 +" + next.getCritical() + "会";
            guiGraphics.drawString(font, Component.literal(attrText)
                    .withStyle(ChatFormatting.GREEN), px, py, IcyGuiStyle.TEXT_DIM, false);
        } else {
            guiGraphics.drawString(font, Component.literal("已是最高阶级")
                    .withStyle(ChatFormatting.GOLD), px, py, IcyGuiStyle.TEXT_DIM, false);
        }

        levelUpButton.active = data.canLevelUp();
        promoteButton.active = data.canPromote();

        String nutText = "螺母: " + data.nuts;
        guiGraphics.drawCenteredString(font, Component.literal(nutText).withStyle(ChatFormatting.GOLD),
                layout.width - 12 - 35, 50, IcyGuiStyle.TEXT);

        String spText = "技能点: " + data.skillPoints;
        guiGraphics.drawCenteredString(font, Component.literal(spText).withStyle(ChatFormatting.GREEN),
                layout.width - 12 - 35, layout.spY, IcyGuiStyle.TEXT);

        ItemStack stack = menu.slots.get(0).getItem();
        boolean canUpgrade = false;
        boolean canBreakthrough = false;
        boolean materialMode = false;

        int expCost = menu.getExpCost();
        int currentPlayerExp = minecraft.player != null ? minecraft.player.experienceLevel : 0;

        int textX = layout.infoX;
        int textY = layout.infoY;

        if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeableItem upgradeable) {
            materialMode = upgradeable.upgradeWithMaterials();
            var statsOpt = upgradeable.getUpgradeStats(stack);
            if (statsOpt.isPresent()) {
                AwarenessUpgradeStats stats = statsOpt.get();
                int maxLevel = AwarenessUpgradeManager.getMaxLevel(stats.stage, stats.rank);
                int maxStage = AwarenessUpgradeManager.getMaxStage(stats.rank);
                boolean maxed = stats.level >= maxLevel && stats.stage >= maxStage;

                String levelText = "阶段" + stats.stage + "/" + maxStage + "-Lv." + stats.level + "/" + maxLevel;
                guiGraphics.drawString(font, Component.literal(levelText)
                        .withStyle(maxed ? ChatFormatting.GOLD : ChatFormatting.WHITE), textX, textY, IcyGuiStyle.TEXT, false);
                textY += LINE_HEIGHT;

                if (materialMode) {
                    if (stats.level < maxLevel) {
                        for (ItemStack material : upgradeable.getUpgradeMaterials(stack)) {
                            String materialName = material.getItem().getDescriptionId();
                            String materialText = "升级材料: " + Component.translatable(materialName).getString() + " x" + material.getCount();
                            guiGraphics.drawString(font, Component.literal(materialText)
                                    .withStyle(ChatFormatting.YELLOW), textX, textY, IcyGuiStyle.TEXT, false);
                            textY += LINE_HEIGHT;
                        }
                    }
                } else {
                    String expCostText = "所需经验: " + expCost;
                    guiGraphics.drawString(font, Component.literal(expCostText)
                            .withStyle(expCost <= currentPlayerExp ? ChatFormatting.GREEN : ChatFormatting.RED), textX, textY, IcyGuiStyle.TEXT, false);
                    textY += LINE_HEIGHT;

                    String playerExpText = "当前经验: " + currentPlayerExp;
                    guiGraphics.drawString(font, Component.literal(playerExpText)
                            .withStyle(ChatFormatting.BLUE), textX, textY, IcyGuiStyle.TEXT, false);
                    textY += LINE_HEIGHT;
                }

                if (stats.level < maxLevel) {
                    String nextLevelText = "升级后阶段" + stats.stage + "-Lv." + (stats.level + 1);
                    guiGraphics.drawString(font, Component.literal(nextLevelText)
                            .withStyle(ChatFormatting.GRAY), textX, textY, IcyGuiStyle.TEXT, false);
                } else if (stats.stage < maxStage) {
                    String breakthroughText = "已满级，可突破至阶段" + (stats.stage + 1);
                    guiGraphics.drawString(font, Component.literal(breakthroughText)
                            .withStyle(ChatFormatting.GOLD), textX, textY, IcyGuiStyle.TEXT, false);
                    textY += LINE_HEIGHT;

                    List<ItemStack> materials = upgradeable.getBreakthroughMaterials(stack, stats.stage);
                    for (ItemStack material : materials) {
                        String materialName = material.getItem().getDescriptionId();
                        String materialText = "突破材料: " + Component.translatable(materialName).getString() + " x" + material.getCount();
                        guiGraphics.drawString(font, Component.literal(materialText)
                                .withStyle(ChatFormatting.YELLOW), textX, textY, IcyGuiStyle.TEXT, false);
                        textY += LINE_HEIGHT;
                    }
                } else {
                    String maxText = "已满阶满级";
                    guiGraphics.drawString(font, Component.literal(maxText)
                            .withStyle(ChatFormatting.GOLD), textX, textY, IcyGuiStyle.TEXT, false);
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

        boolean weaponMode = !stack.isEmpty() && stack.getItem() instanceof ISkillWeapon;
        ISkillWeapon weapon = weaponMode ? (ISkillWeapon) stack.getItem() : null;
        boolean skillMode = weaponMode && weapon.supportsSkillUpgrade(stack);

        for (int i = 0; i < ISkillWeapon.SKILL_COUNT; i++) {
            PgrButton b = skillButtons[i];
            b.visible = skillMode;
            if (skillMode) {
                b.setMessage(Component.literal(weapon.skillName(i) + " " + weapon.getSkillLevel(stack, i)));
            }
        }
        skillUpgradeButton.visible = skillMode && weapon.skillUpgradable(selectedSkill);
        ascendButton.visible = weaponMode;
        if (!weaponMode) {
            return;
        }

        if (skillMode) {
            int selRow = selectedSkill <= 2 ? 0 : (selectedSkill <= 4 ? 1 : 2);
            int selCol = selectedSkill <= 2 ? selectedSkill : (selectedSkill <= 4 ? selectedSkill - 2 : selectedSkill - 5);
            int selX = layout.skillX + selCol * 60;
            int selY = layout.skillY + selRow * 26;
            IcyGuiStyle.frame(guiGraphics, selX - 2, selY - 2, 58, 24, IcyGuiStyle.ACCENT);

            int skillLv = weapon.getSkillLevel(stack, selectedSkill);
            boolean upgradable = weapon.skillUpgradable(selectedSkill);
            int dx = layout.skillDescX;
            int dy = layout.skillDescY;
            Component header = Component.literal(weapon.skillName(selectedSkill)
                    + "  Lv." + skillLv + "/" + ISkillWeapon.MAX_SKILL_LEVEL
                    + (upgradable ? "  倍率" + Math.round(weapon.skillMultiplier(stack, selectedSkill) * 100) + "%" : ""));
            guiGraphics.drawString(font, header, dx, dy, IcyGuiStyle.TEXT, false);
            dy += 13;
            String desc = weapon.skillDesc(selectedSkill, skillLv);
            int maxTextY = layout.skillDescY + layout.skillDescH - 4;
            for (net.minecraft.util.FormattedCharSequence line
                    : font.split(Component.literal(desc), Math.max(40, layout.skillDescW - 12))) {
                if (dy > maxTextY) {
                    break;
                }
                guiGraphics.drawString(font, line, dx, dy, IcyGuiStyle.TEXT_DIM, false);
                dy += 11;
            }
            if (upgradable) {
                skillUpgradeButton.active = skillLv < ISkillWeapon.MAX_SKILL_LEVEL && data.skillPoints >= 1;
                skillUpgradeButton.setMessage(Component.literal(skillLv >= ISkillWeapon.MAX_SKILL_LEVEL
                        ? "已满级" : "升级技能 (技能点×1)"));
            }
        }

        int asc = weapon.getAscension(stack);
        int ax = layout.weaponAscX;
        int ay = layout.weaponAscY;
        guiGraphics.drawString(font, "阶级: ", ax, ay + 4, IcyGuiStyle.TEXT_DIM, false);
        int arx = ax + font.width("阶级: ");
        ChatFormatting ascColor = switch (asc) {
            case 1 -> ChatFormatting.BLUE;
            case 2 -> ChatFormatting.GOLD;
            case 3 -> ChatFormatting.RED;
            default -> ChatFormatting.WHITE;
        };
        Component ascName = Component.translatable("ascension.icy_punishing." + asc)
                .withStyle(ascColor, ChatFormatting.BOLD);
        guiGraphics.drawString(font, ascName, arx, ay + 4, IcyGuiStyle.TEXT, false);
        arx += font.width(ascName) + 6;
        for (int i = 0; i < weapon.maxAscension(); i++) {
            IcyGuiStyle.rankBadge(guiGraphics, arx, ay + 5, i < asc, IcyGuiStyle.ACCENT);
            arx += 9;
        }

        List<ItemStack> ascMats = weapon.getAscensionMaterials(stack);
        if (ascMats.isEmpty()) {
            guiGraphics.drawString(font, Component.literal("已是最高阶级")
                    .withStyle(ChatFormatting.GOLD), ax, ay + 18, IcyGuiStyle.TEXT_DIM, false);
            ascendButton.active = false;
        } else {
            ItemStack need = ascMats.get(0);
            guiGraphics.drawString(font, Component.literal("进阶消耗: "
                            + need.getHoverName().getString() + " ×" + need.getCount())
                    .withStyle(ChatFormatting.YELLOW), ax, ay + 18, IcyGuiStyle.TEXT_DIM, false);
            ascendButton.active = menu.hasEnoughAscensionMaterials();
        }

        int effectWidth = Math.max(120, layout.skillX - 24 - ax);
        int ey = ay + 32;
        String curEffect = asc > 0 ? weapon.ascensionEffect(asc) : null;
        String nextEffect = asc < weapon.maxAscension() ? weapon.ascensionEffect(asc + 1) : null;
        ey = drawWrapped(guiGraphics, "当前进阶效果: " + (curEffect != null ? curEffect : "无"),
                ax, ey, effectWidth, curEffect != null ? IcyGuiStyle.TEXT : IcyGuiStyle.TEXT_DIM);
        if (asc < weapon.maxAscension()) {
            drawWrapped(guiGraphics, "下一阶效果: " + (nextEffect != null ? nextEffect : "无"),
                    ax, ey, effectWidth, IcyGuiStyle.TEXT_DIM);
        }
    }

    private int drawWrapped(GuiGraphics guiGraphics, String text, int x, int y, int width, int color) {
        for (net.minecraft.util.FormattedCharSequence line : font.split(Component.literal(text), width)) {
            guiGraphics.drawString(font, line, x, y, color, false);
            y += 11;
        }
        return y + 1;
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
