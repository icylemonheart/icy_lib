package com.icylib.client.upgrade;

public class UpgradeScreenRendererManager {
    private static IUpgradeScreenRenderer renderer = new DefaultUpgradeScreenRenderer();

    public static void setRenderer(IUpgradeScreenRenderer newRenderer) {
        renderer = newRenderer;
    }

    public static IUpgradeScreenRenderer getRenderer() {
        return renderer;
    }

    public static void resetToDefault() {
        renderer = new DefaultUpgradeScreenRenderer();
    }
}
