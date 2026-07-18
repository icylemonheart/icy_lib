package com.icylib.shield;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = "icy_lib")
public class ShieldSystem {
    
    public static final Map<Player, Float> playerShields = new WeakHashMap<>();
    public static final Map<Player, Float> playerMaxShields = new WeakHashMap<>();
    
    public static void addShield(Player player, float amount) {
        float current = playerShields.getOrDefault(player, 0.0f);
        float max = playerMaxShields.getOrDefault(player, 0.0f);
        playerShields.put(player, current + amount);
        playerMaxShields.put(player, Math.max(max, current + amount));
    }
    
    public static void setShield(Player player, float amount) {
        playerShields.put(player, amount);
        playerMaxShields.put(player, amount);
    }
    
    public static float getShield(Player player) {
        return playerShields.getOrDefault(player, 0.0f);
    }
    
    public static float getMaxShield(Player player) {
        return playerMaxShields.getOrDefault(player, 0.0f);
    }
    
    public static void clearShield(Player player) {
        playerShields.remove(player);
        playerMaxShields.remove(player);
    }
    
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            float shieldAmount = playerShields.getOrDefault(player, 0.0f);
            
            if (shieldAmount > 0) {
                float damage = event.getAmount();
                if (shieldAmount >= damage) {
                    playerShields.put(player, shieldAmount - damage);
                    event.setAmount(0.0f);
                } else {
                    float remainingDamage = damage - shieldAmount;
                    playerShields.put(player, 0.0f);
                    playerMaxShields.put(player, 0.0f);
                    event.setAmount(remainingDamage);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player newPlayer = event.getEntity();
        
        Float shield = playerShields.get(original);
        Float maxShield = playerMaxShields.get(original);
        
        if (shield != null) {
            playerShields.put(newPlayer, shield);
        }
        if (maxShield != null) {
            playerMaxShields.put(newPlayer, maxShield);
        }
    }
}