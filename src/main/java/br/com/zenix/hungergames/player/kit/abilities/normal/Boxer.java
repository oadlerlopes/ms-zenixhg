package br.com.zenix.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Boxer extends Kit {

	public Boxer(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(45000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.STONE_SWORD));
		setFree(false);
		setDescription("Sinta-se no ringue e ganhe habilidades especiais como força e resistência.");
		setRecent(false);
	}

	@EventHandler
	public void onHitBoxer(EntityDamageByEntityEvent event) {
		if ((event.getDamager() instanceof Player)) {
			Player player = (Player) event.getDamager();
			if (hasKit(player) && player.getItemInHand().getType() == Material.AIR) {
				event.setDamage(event.getDamage() + 2);
				return;
			}
			if (hasKit(player) && player.getItemInHand().getType() != Material.AIR) {
				event.setDamage(event.getDamage() + 1);
			}
		}
	}

	@EventHandler
	public void onDamageBoxer(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (hasKit(player) && event.getDamage() > 1.0D) {
				event.setDamage(event.getDamage() - 1.0D);
			}
		}
	}

}
