package br.com.zenix.hungergames.manager.managers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.zenix.core.spigot.player.events.ServerTimeEvent;
import br.com.zenix.hungergames.game.custom.HungerListener;
import br.com.zenix.hungergames.game.event.GamerHitEntityEvent;
import br.com.zenix.hungergames.player.gamer.Gamer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class DamageManager extends HungerListener {

	public static final HashMap<Material, Double> damageMaterial = new HashMap<>();

	public DamageManager() {

		damageMaterial.put(Material.DIAMOND_SWORD, 4.5D);
		damageMaterial.put(Material.IRON_SWORD, 4.0D);
		damageMaterial.put(Material.STONE_SWORD, 3.0D);
		damageMaterial.put(Material.WOOD_SWORD, 2.0D);
		damageMaterial.put(Material.GOLD_SWORD, 2.0D);

		damageMaterial.put(Material.DIAMOND_AXE, 5.0D);
		damageMaterial.put(Material.IRON_AXE, 4.0D);
		damageMaterial.put(Material.STONE_AXE, 3.0D);
		damageMaterial.put(Material.WOOD_AXE, 2.0D);
		damageMaterial.put(Material.GOLD_AXE, 2.0D);

		damageMaterial.put(Material.DIAMOND_PICKAXE, 4.0D);
		damageMaterial.put(Material.IRON_PICKAXE, 3.0D);
		damageMaterial.put(Material.STONE_PICKAXE, 2.0D);
		damageMaterial.put(Material.WOOD_PICKAXE, 1.0D);
		damageMaterial.put(Material.GOLD_PICKAXE, 1.0D);

		for (Material mat : Material.values()) {
			if (damageMaterial.containsKey(mat)) {
				continue;
			}
			damageMaterial.put(mat, 1.0D);
		}
	}

	@EventHandler
	public void onLava(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && e.getCause() == DamageCause.LAVA) {
			e.setDamage(4.0D);
		}
	}

	@EventHandler
	public void onAsyncPreDamageEvent(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getDamager();

		if (player.hasMetadata("custom")) {
			player.removeMetadata("custom", getManager().getPlugin());
			return;
		}

		double damage = 1.0D;

		ItemStack itemStack = player.getItemInHand();

		if (itemStack != null) {
			damage = damageMaterial.get(itemStack.getType());
			if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL)) {
				damage += itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
			}
		}

		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
				int amplifier = effect.getAmplifier() + 1;
				damage += (amplifier * 2);
			} else if (effect.getType().equals(PotionEffectType.WEAKNESS)) {
				damage -= (effect.getAmplifier() + 1);
			}
		}

		if (event.getEntity() instanceof LivingEntity) {
			GamerHitEntityEvent gamerEvent = new GamerHitEntityEvent(player, (LivingEntity) event.getEntity(), damage);
			Bukkit.getPluginManager().callEvent(gamerEvent);
			if (gamerEvent.isCancelled()) {
				event.setCancelled(true);
				return;
			} else {
				LivingEntity le = (LivingEntity) event.getEntity();
				if (le.hasPotionEffect(PotionEffectType.WEAKNESS)) {
					for (PotionEffect effect : le.getActivePotionEffects()) {
						if (!effect.getType().equals(PotionEffectType.WEAKNESS)) {
							continue;
						}
						gamerEvent.setDamage(gamerEvent.getDamage() + (effect.getAmplifier() + 1));
					}
				}
				if (player.hasPermission("*")) {
					damage = gamerEvent.getDamage() + 1;
				} else {
					damage = gamerEvent.getDamage();
				}

			}
		}
		event.setDamage(damage);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHitEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getDamager();

		if (player.hasMetadata("custom")) {
			player.removeMetadata("custom", getManager().getPlugin());
			return;
		}

		double damage = 1.0D;

		ItemStack itemStack = player.getItemInHand();

		if (itemStack != null) {
			damage = damageMaterial.get(itemStack.getType());
			if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL)) {
				damage += itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
			}
		}

		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
				int amplifier = effect.getAmplifier() + 1;
				damage += (amplifier * 2);
			} else if (effect.getType().equals(PotionEffectType.WEAKNESS)) {
				damage -= (effect.getAmplifier() + 1);
			}
		}

		if (event.getEntity() instanceof LivingEntity) {
			GamerHitEntityEvent gamerEvent = new GamerHitEntityEvent(player, (LivingEntity) event.getEntity(), damage);
			Bukkit.getPluginManager().callEvent(gamerEvent);
			if (gamerEvent.isCancelled()) {
				event.setCancelled(true);
				return;
			} else {
				LivingEntity le = (LivingEntity) event.getEntity();
				if (le.hasPotionEffect(PotionEffectType.WEAKNESS)) {
					for (PotionEffect effect : le.getActivePotionEffects()) {
						if (!effect.getType().equals(PotionEffectType.WEAKNESS)) {
							continue;
						}
						gamerEvent.setDamage(gamerEvent.getDamage() + (effect.getAmplifier() + 1));
					}
				}
				if (player.hasPermission("*")) {
					damage = gamerEvent.getDamage() + 1;
				} else {
					damage = gamerEvent.getDamage();
				}

			}
		}

		if (event.getEntity() instanceof Player && getManager().getGameManager().isGame()) {
			Gamer entity = getManager().getGamerManager().getGamer((Player) event.getEntity());
			Gamer damager = getManager().getGamerManager().getGamer(player);
			if (entity != null && damager != null) {
				damager.setFighting(10);
				entity.setFighting(10);
			}
		}
		event.setDamage(damage);
	}

	@EventHandler
	public void onSecond(ServerTimeEvent event) {
		for (Gamer g : getManager().getGamerManager().getAliveGamers()) {
			if (g.isFighting())
				g.refreshFighting();
		}
	}

}
