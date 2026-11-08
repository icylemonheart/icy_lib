package com.icylib.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

// 界面绘制工具：所有颜色均为 ARGB（0xAARRGGBB）
public final class IcyGuiStyle {
    public static final int ACCENT = 0xFFD23A3A;
    public static final int ACCENT_DIM = 0x66D23A3A;
    public static final int ACCENT_SOFT = 0x33D23A3A;

    public static final int BORDER = 0xCCE6EBF0;
    public static final int BORDER_DIM = 0x55E6EBF0;

    public static final int PANEL = 0xC00A0E12;
    public static final int PANEL_SOFT = 0x70141A21;
    public static final int SLOT_FILL = 0x99101820;

    public static final int TEXT = 0xFFF2F5F7;
    public static final int TEXT_DIM = 0xFF97A3AE;

    private IcyGuiStyle() {
    }

    public static void frame(GuiGraphics guiGraphics, int x, int y, int width, int height, int color) {
        guiGraphics.fill(x, y, x + width, y + 1, color);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, color);
        guiGraphics.fill(x, y + 1, x + 1, y + height - 1, color);
        guiGraphics.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    public static void brackets(GuiGraphics guiGraphics, int x, int y, int width, int height, int color, int length) {
        guiGraphics.fill(x, y, x + length, y + 1, color);
        guiGraphics.fill(x, y, x + 1, y + length, color);

        guiGraphics.fill(x + width - length, y, x + width, y + 1, color);
        guiGraphics.fill(x + width - 1, y, x + width, y + length, color);

        guiGraphics.fill(x, y + height - 1, x + length, y + height, color);
        guiGraphics.fill(x, y + height - length, x + 1, y + height, color);

        guiGraphics.fill(x + width - length, y + height - 1, x + width, y + height, color);
        guiGraphics.fill(x + width - 1, y + height - length, x + width, y + height, color);
    }

    public static void panel(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, PANEL_SOFT);
        frame(guiGraphics, x, y, width, height, BORDER_DIM);
        brackets(guiGraphics, x, y, width, height, ACCENT_DIM, 8);
    }

    public static void rankBadge(GuiGraphics guiGraphics, int x, int y, boolean lit, int color) {
        int[] widths = {1, 3, 5, 7, 5, 3, 1};
        for (int i = 0; i < 7; i++) {
            int w = widths[i];
            int dx = (7 - w) / 2;
            if (lit) {
                guiGraphics.fill(x + dx, y + i, x + dx + w, y + i + 1, color);
            } else {
                guiGraphics.fill(x + dx, y + i, x + dx + 1, y + i + 1, BORDER_DIM);
                guiGraphics.fill(x + dx + w - 1, y + i, x + dx + w, y + i + 1, BORDER_DIM);
            }
        }
        if (lit) {
            guiGraphics.fill(x + 3, y + 3, x + 4, y + 4, 0xFFFFFFFF);
        }
    }

    public static void slot(GuiGraphics guiGraphics, int x, int y, boolean highlight) {
        guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, SLOT_FILL);
        frame(guiGraphics, x - 1, y - 1, 18, 18, highlight ? ACCENT : BORDER_DIM);
        if (highlight) {
            brackets(guiGraphics, x - 1, y - 1, 18, 18, ACCENT, 3);
        }
    }

    public static void button(GuiGraphics guiGraphics, Font font, Component message,
                              int x, int y, int width, int height, boolean hovered, boolean active) {
        int fill;
        int border;
        int textColor;
        if (!active) {
            fill = 0x660A0C0F;
            border = BORDER_DIM;
            textColor = TEXT_DIM;
        } else if (hovered) {
            fill = 0xD9231116;
            border = ACCENT;
            textColor = TEXT;
        } else {
            fill = 0xB80E1218;
            border = BORDER;
            textColor = TEXT;
        }

        guiGraphics.fill(x, y, x + width, y + height, fill);
        frame(guiGraphics, x, y, width, height, border);

        guiGraphics.fill(x + 1, y + 1, x + 3, y + height - 1, active ? ACCENT : ACCENT_DIM);

        brackets(guiGraphics, x - 1, y - 1, width + 2, height + 2,
                active ? (hovered ? ACCENT : BORDER_DIM) : 0x33E6EBF0, 4);

        if (active && hovered) {
            frame(guiGraphics, x + 1, y + 1, width - 2, height - 2, ACCENT_SOFT);
        }

        guiGraphics.drawCenteredString(font, message, x + width / 2, y + (height - 8) / 2, textColor);
    }
}
