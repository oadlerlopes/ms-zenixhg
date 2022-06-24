package br.com.zenix.hungergames.player.gamer.listener;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.zenix.core.spigot.player.events.ServerTimeEvent;
import br.com.zenix.hungergames.game.custom.HungerListener;
import br.com.zenix.hungergames.player.gamer.Gamer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GamerCompassSearch extends HungerListener {

	private static ArrayList<UUID> compass = new ArrayList<>();

	@EventHandler
	public void onCompass(PlayerInteractEvent event) {
		if (event.hasItem() && event.getItem().getType() == Material.COMPASS && event.getAction() != Action.PHYSICAL) {
			event.setCancelled(true);

			if (compass.contains(event.getPlayer().getUniqueId())) {
				return;
			}

			compass.add(event.getPlayer().getUniqueId());

			Player player = event.getPlayer();
			Player target = getTarget(player);

			if (target == null) {
				event.getPlayer().sendMessage("§3§lBUSSOLA§f Nenhum player foi §b§lENCONTRADO!");
				event.getPlayer().setCompassTarget(event.getPlayer().getWorld().getSpawnLocation());
			} else {
				event.getPlayer().sendMessage("§3§lBUSSOLA§f Apontando para §b§l" + target.getName());
				event.getPlayer().setCompassTarget(target.getLocation());
			}
		}
	}

	private Player getTarget(Player player) {
		Player target = null;

		for (Gamer gamers : getManager().getGamerManager().getAliveGamers()) {
			if (gamers.getPlayer().isOnline()) {

				Player playerTarget = gamers.getPlayer();
				if (playerTarget.equals(player)) {
					continue;
				}
				if (playerTarget.getLocation().distance(player.getLocation()) < 15.0D) {
					continue;
				}
				if (target == null) {
					target = playerTarget;
				} else {
					if (target.getLocation().distance(player.getLocation()) > playerTarget.getLocation()
							.distance(player.getLocation())) {
						target = playerTarget;
					}
				}
			}
		}

		return target;
	}

	@EventHandler
	public void onSecond(ServerTimeEvent event) {
		compass.clear();
	}

}
