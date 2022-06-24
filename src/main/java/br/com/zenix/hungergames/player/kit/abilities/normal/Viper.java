package br.com.zenix.hungergames.player.kit.abilities.normal;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Viper extends Kit {

	public Viper(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(45000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.SPIDER_EYE));
		setFree(false);
		setDescription("Use suas habilidades para envenenar a vida de seus inimigos.");
		setRecent(false);
	}

	@EventHandler
	public void onViper(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			Player player = (Player) event.getDamager();

			if (hasKit(player) && new Random().nextInt(100) <= 33)
				((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0));
		}
	}

}
