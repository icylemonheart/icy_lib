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
 * 全属性加成工具类：modifier id 由属性注册名生成，黑名单可排除属性或命名空间。
 */
public class AllAttributesBonusHelper {

    private static final String MODIFIER_NAMESPACE = "icy_lib";
    private static final String MODIFIER_PREFIX_BASE = "all_attributes_bonus_";

    private static final Set<String> BLACKLISTED_ATTRIBUTES = new HashSet<>();
    private static final Set<String> BLACKLISTED_NAMESPACES = new HashSet<>();

    private static ResourceLocation getModifierIdForAttribute(Attribute attribute) {
        ResourceLocation key = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        String path = key == null ? attribute.toString().replace(':', '_') : key.getPath();
        return ResourceLocation.fromNamespaceAndPath(MODIFIER_NAMESPACE, MODIFIER_PREFIX_BASE + path);
    }

    public static void registerBlacklistedAttribute(String attributeId) {
        BLACKLISTED_ATTRIBUTES.add(attributeId);
    }

    public static void registerBlacklistedNamespace(String namespace) {
        BLACKLISTED_NAMESPACES.add(namespace);
    }

    private static boolean isBlacklisted(Attribute attribute) {
        ResourceLocation key = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        if (key == null) return false;
        String fullId = key.toString();
        return BLACKLISTED_ATTRIBUTES.contains(fullId) || BLACKLISTED_NAMESPACES.contains(key.getNamespace());
    }

    /**
     * @param bonus 0.02 表示所有属性 ×1.02
     */
    public static Map<Holder<Attribute>, AttributeModifier> createAllAttributesBonus(
            LivingEntity entity, double bonus, String namePrefix) {
        Map<Holder<Attribute>, AttributeModifier> modifiers = new HashMap<>();
        if (bonus == 0) return modifiers;

        BuiltInRegistries.ATTRIBUTE.holders().forEach(attributeHolder -> {
            AttributeInstance instance = entity.getAttribute(attributeHolder);
            if (instance == null) return;
            if (isBlacklisted(attributeHolder.value())) return;

            ResourceLocation modifierId = getModifierIdForAttribute(attributeHolder.value());
            ResourceLocation namespacedId = ResourceLocation.fromNamespaceAndPath(
                    MODIFIER_NAMESPACE, namePrefix + "_" + MODIFIER_PREFIX_BASE + modifierId.getPath());

            modifiers.put(attributeHolder, new AttributeModifier(
                    namespacedId,
                    bonus,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        });

        return modifiers;
    }

    public static void applyAllAttributesBonus(LivingEntity entity, double bonus, String namePrefix) {
        if (bonus == 0) return;
        BuiltInRegistries.ATTRIBUTE.holders().forEach(attributeHolder -> {
            AttributeInstance instance = entity.getAttribute(attributeHolder);
            if (instance == null) return;
            if (isBlacklisted(attributeHolder.value())) return;

            ResourceLocation modifierId = getModifierIdForAttribute(attributeHolder.value());
            ResourceLocation namespacedId = ResourceLocation.fromNamespaceAndPath(
                    MODIFIER_NAMESPACE, namePrefix + "_" + MODIFIER_PREFIX_BASE + modifierId.getPath());

            instance.addTransientModifier(new AttributeModifier(
                    namespacedId,
                    bonus,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        });
    }

    public static void removeAllAttributesBonus(LivingEntity entity) {
        BuiltInRegistries.ATTRIBUTE.holders().forEach(attributeHolder -> {
            AttributeInstance instance = entity.getAttribute(attributeHolder);
            if (instance == null) return;
            if (isBlacklisted(attributeHolder.value())) return;

            ResourceLocation modifierId = getModifierIdForAttribute(attributeHolder.value());
            try {
                instance.removeModifier(modifierId);
            } catch (Exception ignored) {
            }
        });
    }
}
