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

/**
 * setHealth 穿透拦截 Mixin。
 *
 * 受 {@link IcyLibConfig#SET_HEALTH_DEFENSE_ENABLED} 配置项控制，默认关闭。
 * 启用后：在 LivingEntity.hurt() 入口/出口维护 ThreadLocal 标记（{@link DamagePipelineTracker}）。
 * 当 setHealth 被调用时：
 *  - 标记处于 hurt 管线内 → 跳过（已被 LivingDamageEvent 减伤，避免重复结算）
 *  - 标记不在管线内 → 外部模组直接 setHealth 穿透，对扣血量应用 icy_lib
 *    通用抗性 + 防御减伤
 *
 * 由于 setHealth 无 DamageSource 参数，无法判定伤害类型，
 * 故统一应用通用抗性除法 + 防御削减（不区分元素类型）。
 * 帕弥什伤害无法在此处识别，会一并被减伤——这是 setHealth 路径的固有限制。
 */
@Mixin(value = LivingEntity.class, remap = false)
public abstract class LivingEntityDefenseMixin {

    /** hurt 入口：标记进入伤害管线（嵌套可重入） */
    @Inject(method = "hurt", at = @At("HEAD"))
    private void icylib$enterHurtPipeline(DamageSource source, float amount,
                                          CallbackInfoReturnable<Boolean> cir) {
        DamagePipelineTracker.enter();
    }

    /** hurt 出口：清除伤害管线标记（捕获所有 return 路径） */
    @Inject(method = "hurt", at = @At("RETURN"))
    private void icylib$exitHurtPipeline(DamageSource source, float amount,
                                         CallbackInfoReturnable<Boolean> cir) {
        DamagePipelineTracker.exit();
    }

    /**
     * setHealth 参数拦截：
     * - 配置关闭：直通
     * - hurt 管线内：直通（icy_lib LivingDamageEvent 已减伤）
     * - hurt 管线外（外部模组穿透）：对扣血量应用 icy_lib 通用抗性 + 防御减伤
     */
    @ModifyVariable(method = "setHealth", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float icylib$interceptBypassSetHealth(float health) {
        if (!IcyLibConfig.SET_HEALTH_DEFENSE_ENABLED.get()) return health;
        // 在 hurt 管线内：icy_lib LivingDamageEvent 已减伤，直通
        if (DamagePipelineTracker.inPipeline()) return health;

        LivingEntity self = (LivingEntity) (Object) this;
        // 仅服务端处理
        if (self.level().isClientSide()) return health;

        float current = self.getHealth();
        // 加血/无变化直通，仅拦截扣血
        if (health >= current) return health;

        float rawDamage = current - health;

        // 取通用抗性（默认 1.0，除法式减伤）
        AttributeInstance uniInst = self.getAttribute(ModAttributes.UNIVERSAL_RESISTANCE);
        if (uniInst == null) return health; // 实体未注册该属性
        double universalResistance = uniInst.getValue();
        if (universalResistance <= 0.0) universalResistance = 1.0;

        // 取防御（加算式减伤，最后一步扣除）
        AttributeInstance defInst = self.getAttribute(ModAttributes.DEFENSE);
        double defense = defInst != null ? defInst.getValue() : 0.0;

        // 怪物默认防御 = maxHealth × 0.1（与 DefenseEventHandler 一致）
        if (!(self instanceof Player)) {
            defense += self.getMaxHealth() * IcyLibConfig.MONSTER_DEFENSE_MULTIPLIER.get();
        }

        // 应用减伤公式：raw → raw / universal - defense
        double reduced = rawDamage / universalResistance - defense;
        if (reduced < 0.0) reduced = 0.0;

        // 新血量 = 当前血量 - 减伤后伤害（夹在 [0, maxHealth] 区间）
        float newHealth = (float) (current - reduced);
        newHealth = Math.max(0.0F, Math.min(self.getMaxHealth(), newHealth));
        return newHealth;
    }
}
