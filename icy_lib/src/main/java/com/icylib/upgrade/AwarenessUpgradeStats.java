package com.icylib.upgrade;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwarenessUpgradeStats {
    public int rank;
    public int stage;
    public int level;
    public int exp;
    public int totalExp;

    public List<StatEntry> attributes = new ArrayList<>();
    public Map<String, StatEntry> statMap = new HashMap<>();

    public AwarenessUpgradeStats() {}

    public AwarenessUpgradeStats(int rank) {
        this.rank = rank;
        this.stage = 1;
        this.level = 0;
        this.exp = 0;
        this.totalExp = 0;
    }

    public void addAttribute(String type, double value) {
        if (statMap.containsKey(type)) {
            statMap.get(type).addValue(value);
        } else {
            StatEntry entry = new StatEntry(type, value);
            attributes.add(entry);
            statMap.put(type, entry);
        }
    }

    public void addAttributeValue(String type, double value) {
        if (statMap.containsKey(type)) {
            statMap.get(type).addValue(value);
        }
    }

    public int getAttributeIndex(String type) {
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).type.equals(type)) {
                return i;
            }
        }
        return -1;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("rank", rank);
        tag.putInt("stage", stage);
        tag.putInt("level", level);
        tag.putInt("exp", exp);
        tag.putInt("totalExp", totalExp);

        tag.putInt("attributesCount", attributes.size());
        for (int i = 0; i < attributes.size(); i++) {
            tag.put("attribute_" + i, attributes.get(i).toTag());
        }

        return tag;
    }

    public static AwarenessUpgradeStats fromTag(CompoundTag tag) {
        AwarenessUpgradeStats stats = new AwarenessUpgradeStats();
        stats.rank = tag.getInt("rank");
        stats.stage = tag.getInt("stage");
        stats.level = tag.getInt("level");
        stats.exp = tag.getInt("exp");
        stats.totalExp = tag.getInt("totalExp");

        int count = tag.getInt("attributesCount");
        for (int i = 0; i < count; i++) {
            StatEntry entry = StatEntry.fromTag(tag.getCompound("attribute_" + i));
            stats.attributes.add(entry);
            stats.statMap.put(entry.type, entry);
        }

        return stats;
    }

    public static class StatEntry {
        public String type;
        public double value;

        public StatEntry() {}

        public StatEntry(String type, double value) {
            this.type = type;
            this.value = value;
        }

        public void addValue(double value) {
            this.value += value;
        }

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", type);
            tag.putDouble("value", value);
            return tag;
        }

        public static StatEntry fromTag(CompoundTag tag) {
            StatEntry entry = new StatEntry();
            entry.type = tag.getString("type");
            entry.value = tag.getDouble("value");
            return entry;
        }
    }
}
