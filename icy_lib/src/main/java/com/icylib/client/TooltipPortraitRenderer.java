package com.icylib.client;

import com.icylib.IcyLib;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = IcyLib.MODID, value = Dist.CLIENT)
public class TooltipPortraitRenderer {

    private static ItemStack pendingStack = ItemStack.EMPTY;
    private static int pendingX = 0;
    private static int pendingY = 0;
    private static Function<ItemStack, IPortraitProvider> portraitProviderGetter = null;

    public static void registerPortraitProvider(Function<ItemStack, IPortraitProvider> getter) {
        portraitProviderGetter = getter;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderTooltipPre(RenderTooltipEvent.Pre event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && portraitProviderGetter != null) {
            IPortraitProvider provider = portraitProviderGetter.apply(stack);
            if (provider != null && provider.getPortraitLocation(stack) != null) {
                pendingStack = stack;
                pendingX = event.getX();
                pendingY = event.getY();
                return;
            }
        }
        pendingStack = ItemStack.EMPTY;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (pendingStack.isEmpty() || portraitProviderGetter == null) {
            return;
        }
        ItemStack stack = pendingStack;
        pendingStack = ItemStack.EMPTY;

        IPortraitProvider provider = portraitProviderGetter.apply(stack);
        if (provider == null) {
            return;
        }
        ResourceLocation portrait = provider.getPortraitLocation(stack);
        if (portrait == null) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int x = pendingX;
        int y = pendingY;

        int portraitWidth = provider.getPortraitWidth(stack);
        int portraitHeight = provider.getPortraitHeight(stack);
        int portraitX = x - portraitWidth - 4;
        int portraitY = y;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 500);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        try {
            guiGraphics.blit(portrait, portraitX, portraitY, 0, 0, portraitWidth, portraitHeight, portraitWidth, portraitHeight);
        } catch (Exception ignored) {
        }
        guiGraphics.flush();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();

        guiGraphics.pose().popPose();
    }
}
