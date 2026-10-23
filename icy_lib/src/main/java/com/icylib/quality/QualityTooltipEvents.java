package com.icylib.quality;

import com.icylib.fancytext.TooltipBackgroundHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;

public final class QualityTooltipEvents {

    private static final int TRANSPARENT = 0x00000000;

    private QualityTooltipEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRenderTooltipColor(RenderTooltipEvent.Color event) {
        if (TooltipBackgroundHolder.get() != null) {
            return;
        }
        ItemStack stack = event.getItemStack();
        QualityTheme theme = ItemQualityRegistry.getTheme(stack);
        if (theme == null) {
            return;
        }

        event.setBackgroundStart(TRANSPARENT);
        event.setBackgroundEnd(TRANSPARENT);
        event.setBorderStart(TRANSPARENT);
        event.setBorderEnd(TRANSPARENT);

        Font font = event.getFont();
        List<ClientTooltipComponent> components = event.getComponents();
        int width = 0;
        int height = components.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent component : components) {
            width = Math.max(width, component.getWidth(font));
            height += component.getHeight();
        }

        QualityRenderHelper.renderTooltipBackground(event.getGraphics(), event.getX(), event.getY(), width, height, theme);
    }
}
