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

/**
 * Font 全局拦截（客户端）：文本含 {{...}} 艺术字标记时接管渲染与测量。
 *
 * - drawInBatch(String)：GUI/HUD/聊天等 String 入口
 * - drawInBatch(FormattedCharSequence)：tooltip 入口，Component 版内部转发到这里
 * - width ×3：剔除标记字符，保证 tooltip 布局正确
 *
 * 不拦截 drawInBatch8xOutline（文本展示实体等描边路径），此类文本不支持特效标记。
 *
 * NeoForge 1.21.1 在 moddev 插件 + Mojang mappings 下，所有 @Mixin/@Inject 必须 remap = false，
 * 否则会因找不到 SRG 混淆映射而启动失败。
 */
@Mixin(value = Font.class, remap = false)
public abstract class FontEffectMixin {

    // ==================== 渲染拦截 ====================

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

    // ==================== 宽度拦截（剔除标记字符） ====================

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

    // ==================== 工具 ====================

    private static String collect(FormattedCharSequence sequence) {
        StringBuilder sb = new StringBuilder(64);
        sequence.accept((index, style, codePoint) -> {
            sb.appendCodePoint(codePoint);
            return true;
        });
        return sb.toString();
    }
}
