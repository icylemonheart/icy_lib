package com.icylib.upgrade;

/** 升级/突破界面的全屏布局；槽位坐标须在创建 UpgradeMenu 时确定，菜单与界面共用此结果。 */
public final class UpgradeLayout {
    public static final int SLOT = 18;
    public static final int HEADER_HEIGHT = 40;

    public final int width;
    public final int height;

    public final int itemX;
    public final int itemY;
    public final int infoX;
    public final int infoY;
    public final int invX;
    public final int invMainY;
    public final int hotbarY;
    public final int bottomBarTop;
    public final int panelX;
    public final int panelY;
    public final int panelW;
    public final int panelH;
    public final int nutX;
    public final int nutY;
    public final int spX;
    public final int spY;

    public final int centerX;
    public final int centerY;
    public final int centerW;
    public final int centerH;
    public final int weaponAscX;
    public final int weaponAscY;
    public final int skillX;
    public final int skillY;
    public final int skillDescX;
    public final int skillDescY;
    public final int skillDescW;
    public final int skillDescH;

    private UpgradeLayout(int width, int height) {
        this.width = width;
        this.height = height;

        int cx = width / 2;

        this.invX = cx - 9 * SLOT / 2;
        this.hotbarY = height - 26;
        this.invMainY = hotbarY - 58;
        this.bottomBarTop = invMainY - 22;

        this.panelW = 140;
        this.panelH = 116;
        this.panelX = 12;
        this.panelY = height - panelH - 12;

        this.nutX = width - 12;
        this.nutY = 50;
        this.spX = width - 12;
        this.spY = 74;

        this.centerX = 12;
        this.centerY = HEADER_HEIGHT + 6;
        this.centerW = width - 24;
        this.centerH = panelY - 4 - centerY;

        this.itemX = centerX + 12;
        this.itemY = centerY + 12;
        this.infoX = itemX + SLOT + 14;
        this.infoY = itemY + 2;

        this.weaponAscX = centerX + 12;
        this.weaponAscY = Math.max(centerY + 112, centerY + centerH - 58);

        this.skillX = centerX + centerW - 174 - 16;
        this.skillY = centerY + 50;
        this.skillDescX = centerX + 12;
        this.skillDescY = centerY + 72;
        this.skillDescW = skillX - 12 - skillDescX;
        this.skillDescH = Math.max(0, weaponAscY - 8 - skillDescY);
    }

    public static UpgradeLayout compute(int width, int height) {
        return new UpgradeLayout(Math.max(width, 320), Math.max(height, 240));
    }
}
