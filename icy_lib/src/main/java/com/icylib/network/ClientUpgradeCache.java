package com.icylib.network;

import com.icylib.upgrade.PlayerUpgradeData;

/** 客户端缓存的玩家升级数据。 */
public class ClientUpgradeCache {
    private static PlayerUpgradeData data = new PlayerUpgradeData();

    public static PlayerUpgradeData get() {
        return data;
    }

    public static void set(PlayerUpgradeData newData) {
        data = newData;
    }
}
