package com.icylib.mixin;

import com.icylib.config.IcyLibConfig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 玩家移动速度表现上限：在 Player#getSpeed 返回值处钳制，不改属性面板、不影响击退与爆炸位移。 */
@Mixin(value = Player.class, remap = false)
public class PlayerSpeedCapMixin {

    @Inject(method = "getSpeed", at = @At("RETURN"), cancellable = true)
    private void icylib$capSpeed(CallbackInfoReturnable<Float> cir) {
        double cap = IcyLibConfig.PLAYER_SPEED_CAP.get();
        if (cap > 0.0) {
            float speed = cir.getReturnValueF();
            if (speed > (float) cap) {
                cir.setReturnValue((float) cap);
            }
        }
    }
}
