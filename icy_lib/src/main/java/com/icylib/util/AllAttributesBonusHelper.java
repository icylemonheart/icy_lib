package com.icylib.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 全属性加成工具类。
 *
 * 注意：1.21.1 中 AttributeModifier 的 id 从 UUID 改为 ResourceLocation，
 * 且 getAttribute/addTransientAttributeModifiers 改为接收 Holder<Attribute>。
 *
 * 黑名单系统：可通过 {@link #registerBlacklistedAttribute(String)} 和
 * {@link #registerBlacklistedNamespace(String)} 将特定属性或整个命名空间
 * 加入黑名单，这些属性不会被加成修饰符影响。
 */
public class AllAttributesBonusHelper {

    private static final String MODIFIER_NAMESPACE = "icy_lib";
    private static final String MODIFIER_PREFIX_BASE = "all_attributes_bonus_";

    // 黑名单集合：存放不应该受到加成的属性ID和命名空间
    private static final Set<String> BLACKLISTED_ATTRIBUTES = new HashSet<>();
    private static final Set<String> BLACKLISTED_NAMESPACES = new HashSet<>();

    /**
     * 通过属性的注册名生成稳定唯一的 Modifier ResourceLocation。
     */
    private static ResourceLocation getModifierIdForAttribute(Attribute attribute) {
        ResourceLocation key = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        String path = key == null ? attribute.toString().replace(':', '_') : key.getPath();
        return ResourceLocation.fromNamespaceAndPath(MODIFIER_NAMESPACE, MODIFIER_PREFIX_BASE + path);
    }

    /**
     * 注册黑名单属性，使其不被加成修饰符影响。
     *
     * @param attributeId 属性ID，格式：modid:attribute_name
     */
    public static void registerBlacklistedAttribute(String attributeId) {
        BLACKLISTED_ATTRIBUTES.add(attributeId);
    }

    /**
     * 注册黑名单命名空间，使整个命名空间下的属性不被加成修饰符影响。
     *
     * @param namespace 命名空间，例如 "minecraft" 或自定义模组ID
     */
    public static void registerBlacklistedNamespace(String namespace) {
        BLACKLISTED_NAMESPACES.add(namespace);
    }

    /**
     * 判断属性是否在黑名单中。
     */
    private static boolean isBlacklisted(Attribute attribute) {
        ResourceLocation key = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        if (key == null) return false;
        String fullId = key.toString();
        return BLACKLISTED_ATTRIBUTES.contains(fullId) || BLACKLISTED_NAMESPACES.contains(key.getNamespace());
    }

    /**
     * 遍历所有注册属性，为实体拥有的属性生成乘算修饰符。
     * @param entity 目标实体
     * @param bonus 加成值（如0.02表示所有属性×1.02）
     * @param namePrefix 修饰符名称前缀（用于生成 path，区分不同来源）
     * @return 包含所有属性乘算修饰符的 Map
     */
    public static Map<Holder<Attribute>, AttributeModifier> createAllAttributesBonus(
            LivingEntity entity, double bonus, String namePrefix) {
        Map<Holder<Attribute>, AttributeModifier> modifiers = new HashMap<>();
        if (bonus == 0) return modifiers;

        BuiltInRegistries.ATTRIBUTE.holders().forEach(attributeHolder -> {
            AttributeInstance instance = entity.getAttribute(attributeHolder);
            if (instance == null) return;
            if (isBlacklisted(attributeHolder.value())) return; // 跳过黑名单属性

            ResourceLocation modifierId = getModifierIdForAttribute(attributeHolder.value());
            // 将 namePrefix 加入 path 以区分不同来源的加成
            ResourceLocation namespacedId = ResourceLocation.fromNamespaceAndPath(
                    MODIFIER_NAMESPACE, namePrefix + "_" + MODIFIER_PREFIX_BASE + modifierId.getPath());

            modifiers.put(attributeHolder, new AttributeModifier(
                    namespacedId,
                    bonus,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        });

        return modifiers;
    }

    /**
     * 将全属性加成修饰符直接应用到实体上（临时修饰符）。
     */
    public static void applyAllAttributesBonus(LivingEntity entity, double bonus, String namePrefix) {
        if (bonus == 0) return;
        BuiltInRegistries.ATTRIBUTE.holders().forEach(attributeHolder -> {
            AttributeInstance instance = entity.getAttribute(attributeHolder);
            if (instance == null) return;
            if (isBlacklisted(attributeHolder.value())) return; // 跳过黑名单属性

            ResourceLocation modifierId = getModifierIdForAttribute(attributeHolder.value());
            ResourceLocation namespacedId = ResourceLocation.fromNamespaceAndPath(
                    MODIFIER_NAMESPACE, namePrefix + "_" + MODIFIER_PREFIX_BASE + modifierId.getPath());

            instance.addTransientModifier(new AttributeModifier(
                    namespacedId,
                    bonus,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        });
    }

    /**
     * 移除之前应用的全属性加成修饰符（遍历所有已注册属性逐一清理）。
     */
    public static void removeAllAttributesBonus(LivingEntity entity) {
        BuiltInRegistries.ATTRIBUTE.holders().forEach(attributeHolder -> {
            AttributeInstance instance = entity.getAttribute(attributeHolder);
            if (instance == null) return;
            if (isBlacklisted(attributeHolder.value())) return; // 跳过黑名单属性

            ResourceLocation modifierId = getModifierIdForAttribute(attributeHolder.value());
            try {
                instance.removeModifier(modifierId);
            } catch (Exception ignored) {
            }
        });
    }
}
