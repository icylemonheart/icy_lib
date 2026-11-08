package com.icylib.mixin;

import com.icylib.config.IcyLibConfig;
import com.icylib.init.ModAttributes;
import com.icylib.util.DamagePipelineTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** setHealth 穿透拦截：hurt 管线外的扣血量应用通用抗性除法 + 防御减伤（默认关闭）。 */
@Mixin(value = LivingEntity.class, remap = false)
public abstract class LivingEntityDefenseMixin {

    @Inject(method = "hurt", at = @At("HEAD"))
    private void icylib$enterHurtPipeline(DamageSource source, float amount,
                                          CallbackInfoReturnable<Boolean> cir) {
        DamagePipelineTracker.enter();
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void icylib$exitHurtPipeline(DamageSource source, float amount,
                                         CallbackInfoReturnable<Boolean> cir) {
        DamagePipelineTracker.exit();
    }

    @ModifyVariable(method = "setHealth", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float icylib$interceptBypassSetHealth(float health) {
        if (!IcyLibConfig.SET_HEALTH_DEFENSE_ENABLED.get()) return health;
        if (DamagePipelineTracker.inPipeline()) return health;

        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide()) return health;

        float current = self.getHealth();
        if (health >= current) return health;

        float rawDamage = current - health;

        AttributeInstance uniInst = self.getAttribute(ModAttributes.UNIVERSAL_RESISTANCE);
        if (uniInst == null) return health;
        double universalResistance = uniInst.getValue();
        if (universalResistance <= 0.0) universalResistance = 1.0;

        AttributeInstance defInst = self.getAttribute(ModAttributes.DEFENSE);
        double defense = defInst != null ? defInst.getValue() : 0.0;

        if (!(self instanceof Player)) {
            defense += self.getMaxHealth() * IcyLibConfig.MONSTER_DEFENSE_MULTIPLIER.get();
        }

        double reduced = rawDamage / universalResistance - defense;
        if (reduced < 0.0) reduced = 0.0;

        float newHealth = (float) (current - reduced);
        newHealth = Math.max(0.0F, Math.min(self.getMaxHealth(), newHealth));
        return newHealth;
    }
}
