package com.icylib.mixin;

import com.icylib.fancytext.FancyTextMarkup;
import com.icylib.fancytext.GlobalFancyTextRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Font 全局拦截：文本含 {{...}} 标记时接管渲染与测量（1.21.1 下必须 remap = false）。 */
@Mixin(value = Font.class, remap = false)
public abstract class FontEffectMixin {

    @Inject(method = "drawInBatch(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void icylib$renderEffectString(String text, float x, float y, int color, boolean dropShadow,
                                           Matrix4f matrix, MultiBufferSource buffer, Font.DisplayMode displayMode,
                                           int backgroundColor, int packedLight,
                                           CallbackInfoReturnable<Integer> cir) {
        if (GlobalFancyTextRenderer.isRendering() || !FancyTextMarkup.hasMarkup(text)) {
            return;
        }
        Integer result = GlobalFancyTextRenderer.renderString((Font) (Object) this, text, x, y, color, dropShadow,
                matrix, buffer, displayMode, backgroundColor, packedLight);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }

    @Inject(method = "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void icylib$renderEffectSequence(FormattedCharSequence sequence, float x, float y, int color,
                                             boolean dropShadow, Matrix4f matrix, MultiBufferSource buffer,
                                             Font.DisplayMode displayMode, int backgroundColor, int packedLight,
                                             CallbackInfoReturnable<Integer> cir) {
        if (GlobalFancyTextRenderer.isRendering() || sequence == null) {
            return;
        }
        String plain = collect(sequence);
        if (!FancyTextMarkup.hasMarkup(plain)) {
            return;
        }
        Integer result = GlobalFancyTextRenderer.renderSequence((Font) (Object) this, sequence, x, y, color,
                dropShadow, matrix, buffer, displayMode, backgroundColor, packedLight);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }

    @Inject(method = "width(Ljava/lang/String;)I", at = @At("HEAD"), cancellable = true, remap = false)
    private void icylib$widthString(String text, CallbackInfoReturnable<Integer> cir) {
        if (!FancyTextMarkup.hasMarkup(text)) {
            return;
        }
        cir.setReturnValue(((Font) (Object) this).width(FancyTextMarkup.strip(text)));
    }

    @Inject(method = "width(Lnet/minecraft/network/chat/FormattedText;)I", at = @At("HEAD"), cancellable = true, remap = false)
    private void icylib$widthFormattedText(FormattedText text, CallbackInfoReturnable<Integer> cir) {
        if (text == null || GlobalFancyTextRenderer.isRendering()) {
            return;
        }
        String plain = text.getString();
        if (!FancyTextMarkup.hasMarkup(plain)) {
            return;
        }
        cir.setReturnValue(((Font) (Object) this).width(FancyTextMarkup.strip(plain)));
    }

    @Inject(method = "width(Lnet/minecraft/util/FormattedCharSequence;)I", at = @At("HEAD"), cancellable = true, remap = false)
    private void icylib$widthSequence(FormattedCharSequence sequence, CallbackInfoReturnable<Integer> cir) {
        if (sequence == null || GlobalFancyTextRenderer.isRendering()) {
            return;
        }
        String plain = collect(sequence);
        if (!FancyTextMarkup.hasMarkup(plain)) {
            return;
        }
        cir.setReturnValue(((Font) (Object) this).width(FancyTextMarkup.strip(plain)));
    }

    private static String collect(FormattedCharSequence sequence) {
        StringBuilder sb = new StringBuilder(64);
        sequence.accept((index, style, codePoint) -> {
            sb.appendCodePoint(codePoint);
            return true;
        });
        return sb.toString();
    }
}
