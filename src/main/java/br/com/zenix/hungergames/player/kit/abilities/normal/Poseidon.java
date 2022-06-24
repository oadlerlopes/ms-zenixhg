package br.com.zenix.hungergames.player.kit.abilities.normal;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.zenix.core.spigot.player.events.ServerTimeEvent;
import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Poseidon extends Kit {

	public Poseidon(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(47000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.WATER_BUCKET));
		setFree(false);
		setDescription("Ganhe habilidades de um deus grego e adquira diversos poderes como força, velocidade e respiração.");
		setRecent(false);
	}

	@EventHandler
	public void onPoseidon(ServerTimeEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (hasKit(player)) {
				Block block = player.getLocation().getBlock();
				if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 1));
					player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 80, 0));
				}
			}
		}
	}
}
