package com.icylib.shield;

import com.icylib.defense.DefenseLayer;
import com.icylib.defense.DefenseLayerType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 护盾层：最先被消耗的防御层，本类只负责数据存储与生命周期。
 */
public class ShieldSystem implements DefenseLayer {

    public static final ShieldSystem INSTANCE = new ShieldSystem();

    public static final Map<UUID, Float> playerShields = new HashMap<>();
    public static final Map<UUID, Float> playerMaxShields = new HashMap<>();

    private ShieldSystem() {}

    public static void addShield(Player player, float amount) {
        UUID uuid = player.getUUID();
        float current = playerShields.getOrDefault(uuid, 0.0f);
        float max = playerMaxShields.getOrDefault(uuid, 0.0f);
        float newShield = current + amount;
        if (newShield > max) {
            playerMaxShields.put(uuid, newShield);
            max = newShield;
        }
        float clamped = Math.min(newShield, max);
        playerShields.put(uuid, clamped);
    }

    public static void setShield(Player player, float amount) {
        UUID uuid = player.getUUID();
        float max = playerMaxShields.getOrDefault(uuid, 0.0f);
        if (amount > max) {
            playerMaxShields.put(uuid, amount);
            max = amount;
        }
        float clamped = Math.min(amount, max);
        playerShields.put(uuid, clamped);
    }

    public static float getShield(Player player) {
        return playerShields.getOrDefault(player.getUUID(), 0.0f);
    }

    public static float getMaxShield(Player player) {
        return playerMaxShields.getOrDefault(player.getUUID(), 0.0f);
    }

    public static void clearShield(Player player) {
        UUID uuid = player.getUUID();
        playerShields.remove(uuid);
        playerMaxShields.remove(uuid);
    }

    public static void setMaxShield(Player player, float max) {
        UUID uuid = player.getUUID();
        playerMaxShields.put(uuid, max);
        float current = playerShields.getOrDefault(uuid, 0.0f);
        if (current > max) {
            playerShields.put(uuid, max);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player newPlayer = event.getEntity();
        UUID origUUID = original.getUUID();
        UUID newUUID = newPlayer.getUUID();

        Float shield = playerShields.get(origUUID);
        Float maxShield = playerMaxShields.get(origUUID);

        if (shield != null) {
            playerShields.put(newUUID, shield);
        }
        if (maxShield != null) {
            playerMaxShields.put(newUUID, maxShield);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        playerShields.remove(uuid);
        playerMaxShields.remove(uuid);
    }

    @Override
    public DefenseLayerType getType() {
        return DefenseLayerType.SHIELD;
    }

    @Override
    public float getCurrent(Player player) {
        return playerShields.getOrDefault(player.getUUID(), 0.0f);
    }

    @Override
    public float getMax(Player player) {
        return playerMaxShields.getOrDefault(player.getUUID(), 0.0f);
    }

    @Override
    public float applyDamage(Player player, float damage) {
        UUID uuid = player.getUUID();
        float shieldAmount = playerShields.getOrDefault(uuid, 0.0f);

        if (shieldAmount <= 0.0f) {
            return damage;
        }

        if (shieldAmount >= damage) {
            playerShields.put(uuid, shieldAmount - damage);
            return 0.0f;
        } else {
            float remaining = damage - shieldAmount;
            playerShields.put(uuid, 0.0f);
            playerMaxShields.put(uuid, 0.0f);
            return remaining;
        }
    }

    @Override
    public boolean isActive(Player player) {
        return playerShields.getOrDefault(player.getUUID(), 0.0f) > 0.0f;
    }
}
