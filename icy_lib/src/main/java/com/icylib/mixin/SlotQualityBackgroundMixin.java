package com.icylib.mixin;

import com.icylib.quality.ItemQualityRegistry;
import com.icylib.quality.QualityRenderHelper;
import com.icylib.quality.QualityTheme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContainerScreen.class, remap = false)
public class SlotQualityBackgroundMixin {

    @Inject(method = "renderSlot(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;)V",
            at = @At("HEAD"), remap = false)
    private void icylib$renderQualityBackground(GuiGraphics graphics, Slot slot, CallbackInfo ci) {
        ItemStack stack = slot.getItem();
        if (stack.isEmpty()) {
            return;
        }
        QualityTheme theme = ItemQualityRegistry.getTheme(stack);
        if (theme == null) {
            return;
        }
        QualityRenderHelper.renderSlotBackground(graphics, slot.x, slot.y, theme);
    }
}
