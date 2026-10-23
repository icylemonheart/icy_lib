package com.icylib.armor;

import com.icylib.defense.DefenseLayer;
import com.icylib.defense.DefenseLayerType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 护甲系统 —— icy_lib 通用护甲层。
 *
 * 伤害优先级：由 {@link com.icylib.defense.DefenseSystemManager} 调度，
 * 护盾（SHIELD）优先被消耗，护盾耗尽后护甲（ARMOR）承受剩余伤害。
 */
public class ArmorSystem implements DefenseLayer {

    public static final ArmorSystem INSTANCE = new ArmorSystem();

    public static final Map<UUID, Float> playerArmors = new HashMap<>();
    public static final Map<UUID, Float> playerMaxArmors = new HashMap<>();

    private ArmorSystem() {}

    public static void addArmor(Player player, float amount) {
        UUID uuid = player.getUUID();
        float current = playerArmors.getOrDefault(uuid, 0.0f);
        float max = playerMaxArmors.getOrDefault(uuid, 0.0f);
        float newArmor = current + amount;
        // 若新增护甲超过当前 max，更新 max
        if (newArmor > max) {
            playerMaxArmors.put(uuid, newArmor);
            max = newArmor;
        }
        float clamped = Math.min(newArmor, max);
        playerArmors.put(uuid, clamped);
    }

    public static void setArmor(Player player, float amount) {
        UUID uuid = player.getUUID();
        float max = playerMaxArmors.getOrDefault(uuid, 0.0f);
        // 先更新最大值（若 amount 超过当前 max），再钳制护甲值
        if (amount > max) {
            playerMaxArmors.put(uuid, amount);
            max = amount;
        }
        float clamped = Math.min(amount, max);
        playerArmors.put(uuid, clamped);
    }

    public static float getArmor(Player player) {
        return playerArmors.getOrDefault(player.getUUID(), 0.0f);
    }

    public static float getMaxArmor(Player player) {
        return playerMaxArmors.getOrDefault(player.getUUID(), 0.0f);
    }

    public static void clearArmor(Player player) {
        UUID uuid = player.getUUID();
        playerArmors.remove(uuid);
        playerMaxArmors.remove(uuid);
    }

    public static void setMaxArmor(Player player, float max) {
        UUID uuid = player.getUUID();
        playerMaxArmors.put(uuid, max);
        float current = playerArmors.getOrDefault(uuid, 0.0f);
        if (current > max) {
            playerArmors.put(uuid, max);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player newPlayer = event.getEntity();
        UUID origUUID = original.getUUID();
        UUID newUUID = newPlayer.getUUID();

        Float armor = playerArmors.get(origUUID);
        Float maxArmor = playerMaxArmors.get(origUUID);

        if (armor != null) {
            playerArmors.put(newUUID, armor);
        }
        if (maxArmor != null) {
            playerMaxArmors.put(newUUID, maxArmor);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        playerArmors.remove(uuid);
        playerMaxArmors.remove(uuid);
    }

    // ===== DefenseLayer 接口实现 =====

    @Override
    public DefenseLayerType getType() {
        return DefenseLayerType.ARMOR;
    }

    @Override
    public float getCurrent(Player player) {
        return playerArmors.getOrDefault(player.getUUID(), 0.0f);
    }

    @Override
    public float getMax(Player player) {
        return playerMaxArmors.getOrDefault(player.getUUID(), 0.0f);
    }

    @Override
    public float applyDamage(Player player, float damage) {
        UUID uuid = player.getUUID();
        float armorAmount = playerArmors.getOrDefault(uuid, 0.0f);

        if (armorAmount <= 0.0f) {
            return damage;
        }

        if (armorAmount >= damage) {
            playerArmors.put(uuid, armorAmount - damage);
            return 0.0f;
        } else {
            float remaining = damage - armorAmount;
            playerArmors.put(uuid, 0.0f);
            playerMaxArmors.put(uuid, 0.0f);
            return remaining;
        }
    }

    @Override
    public boolean isActive(Player player) {
        return playerArmors.getOrDefault(player.getUUID(), 0.0f) > 0.0f;
    }
}
