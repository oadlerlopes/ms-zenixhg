package br.com.zenix.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Specialist extends Kit {

	public Specialist(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(49000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.BOOK));
		setFree(false);
		setDescription("Tenha a habilidade de encantar seus itens em sua mesa de encantamentos portatil.");
		setRecent(false);
		setItems(createItemStack("§aSpecialist", Material.BOOK));
	}

	@EventHandler
	private void playerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();
		if ((action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) && hasKit(player) && item != null && item.getType() == Material.BOOK) {
			event.setCancelled(true);
			player.openEnchanting(null, true);
		}
	}

	@EventHandler
	public void playerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity().getKiller();
		if (player == null) {
			return;
		}
		if (hasKit(player)) {
			player.setLevel(player.getLevel() + 1);
		}
	}

	@EventHandler
	public void expBottleEvent(ExpBottleEvent event) {
		event.setExperience(event.getExperience() * 2 + 5);
	}

}
