package com.icylib.upgrade;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class Upgrade {
    public List<Integer> boostCounts = new ArrayList<>();

    public Upgrade() {}

    public boolean removeBoost(int index) {
        if (index < boostCounts.size() && boostCounts.get(index) > 0) {
            boostCounts.set(index, boostCounts.get(index) - 1);
            return true;
        }
        return false;
    }

    public void add(UpgradeType type) {
        while (boostCounts.size() <= type.attributeIndex) {
            boostCounts.add(0);
        }
        boostCounts.set(type.attributeIndex, boostCounts.get(type.attributeIndex) + 1);
    }

    public int getBoostCount(int index) {
        if (index < boostCounts.size()) {
            return boostCounts.get(index);
        }
        return 0;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("boostCountsSize", boostCounts.size());
        for (int i = 0; i < boostCounts.size(); i++) {
            tag.putInt("boost_" + i, boostCounts.get(i));
        }
        return tag;
    }

    public static Upgrade fromTag(CompoundTag tag) {
        Upgrade upgrade = new Upgrade();
        int boostSize = tag.getInt("boostCountsSize");
        for (int i = 0; i < boostSize; i++) {
            upgrade.boostCounts.add(tag.getInt("boost_" + i));
        }
        return upgrade;
    }
}
