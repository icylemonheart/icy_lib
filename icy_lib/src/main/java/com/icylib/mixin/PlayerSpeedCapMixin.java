package com.icylib.mixin;

import com.icylib.config.IcyLibConfig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 玩家移动速度表现上限（通用，客户端与服务端均生效）。
 *
 * {@link Player#getSpeed()} 直接返回移动速度属性值，地面行走（摩擦力影响速度）、
 * 深海探索者游泳推进均以它为基准。在此返回处钳制：
 * <ul>
 *   <li>属性本身与属性面板显示不变（不是属性上限）；</li>
 *   <li>实际自走移动表现不超过配置值（默认0.5）；</li>
 *   <li>击退、爆炸、直接修改速度向量的位移不受影响（那些不走 getSpeed）。</li>
 * </ul>
 *
 * 创造模式飞行使用独立的 abilities.flyingSpeed（getFlyingSpeed），不受速度属性影响，因此不在此钳制。
 */
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
