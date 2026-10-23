package com.icylib.quality;

public record QualityTheme(int rank, int primary, int light) {

    private static final QualityTheme[] THEMES = new QualityTheme[7];

    static {
        THEMES[2] = new QualityTheme(2, 0x54D66A, 0xA6F2B4);
        THEMES[3] = new QualityTheme(3, 0x3E8FFF, 0x9DC6FF);
        THEMES[4] = new QualityTheme(4, 0xB45CFF, 0xDCB0FF);
        THEMES[5] = new QualityTheme(5, 0xFFA726, 0xFFD59A);
        THEMES[6] = new QualityTheme(6, 0xFF5252, 0xFFA3A3);
    }

    public static QualityTheme of(int rank) {
        return rank >= 2 && rank < THEMES.length ? THEMES[rank] : null;
    }
}
