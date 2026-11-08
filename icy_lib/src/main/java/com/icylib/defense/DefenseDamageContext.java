package com.icylib.defense;

import net.minecraft.world.damagesource.DamageSource;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 防御伤害上下文：SEQUENTIAL 按 priority 顺序吸收，SIMULTANEOUS 按比例分流。
 */
public class DefenseDamageContext {

    public enum Mode {
        SEQUENTIAL,
        SIMULTANEOUS
    }

    private final DamageSource source;
    private final float originalDamage;
    private final Set<DefenseLayerType> bypassedLayers;
    private final Mode mode;
    private final Map<DefenseLayerType, Float> splitRatios;

    private DefenseDamageContext(Builder builder) {
        this.source = builder.source;
        this.originalDamage = builder.originalDamage;
        this.bypassedLayers = Collections.unmodifiableSet(EnumSet.copyOf(builder.bypassedLayers));
        this.mode = builder.mode;
        this.splitRatios = Collections.unmodifiableMap(new EnumMap<>(builder.splitRatios));
    }

    public DamageSource getSource() {
        return source;
    }

    public float getOriginalDamage() {
        return originalDamage;
    }

    public Set<DefenseLayerType> getBypassedLayers() {
        return bypassedLayers;
    }

    public boolean bypasses(DefenseLayerType type) {
        return bypassedLayers.contains(type);
    }

    public Mode getMode() {
        return mode;
    }

    public Map<DefenseLayerType, Float> getSplitRatios() {
        return splitRatios;
    }

    public static DefenseDamageContext forSource(DamageSource source, float damage) {
        return new Builder(source, damage).build();
    }

    public Builder toBuilder() {
        return new Builder(source, originalDamage)
                .bypass(bypassedLayers)
                .mode(mode)
                .splitRatios(splitRatios);
    }

    public static class Builder {
        private final DamageSource source;
        private final float originalDamage;
        private final Set<DefenseLayerType> bypassedLayers = EnumSet.noneOf(DefenseLayerType.class);
        private Mode mode = Mode.SEQUENTIAL;
        private final Map<DefenseLayerType, Float> splitRatios = new EnumMap<>(DefenseLayerType.class);

        public Builder(DamageSource source, float originalDamage) {
            this.source = source;
            this.originalDamage = originalDamage;
        }

        public Builder bypass(DefenseLayerType layer) {
            this.bypassedLayers.add(layer);
            return this;
        }

        public Builder bypass(Set<DefenseLayerType> layers) {
            this.bypassedLayers.addAll(layers);
            return this;
        }

        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder splitRatio(DefenseLayerType layer, float ratio) {
            this.splitRatios.put(layer, ratio);
            return this;
        }

        private Builder splitRatios(Map<DefenseLayerType, Float> ratios) {
            this.splitRatios.putAll(ratios);
            return this;
        }

        public DefenseDamageContext build() {
            return new DefenseDamageContext(this);
        }
    }
}
