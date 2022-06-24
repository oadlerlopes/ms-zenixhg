package br.com.zenix.hungergames.player.kit.abilities.normal;

import org.bukkit.Effect;
import org.bukkit.Location;
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

public class Snail extends Kit {

	public Snail(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(45000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.SOUL_SAND));
		setFree(false);
		setDescription("Tranforme seu inimigo em uma lesma, deixando-o com lentidão durante alguns segundos.");
		setRecent(false);
	}

	@EventHandler
	public void onSnail(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		Player snail = (Player) event.getDamager();
		Location location = player.getLocation();
		if (!hasKit(snail)) {
			return;
		}
		if (player instanceof Player && getManager().getRandom().nextInt(3) == 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
			location.getWorld().playEffect(location.add(0.0D, 0.4D, 0.0D), Effect.STEP_SOUND, 159, 13);
		}
	}

}
