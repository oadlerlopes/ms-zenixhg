package br.com.zenix.hungergames.player.admin.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.hungergames.game.custom.HungerListener;


/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class AdminListener extends HungerListener {

	@EventHandler
	private void onCancelBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (getManager().getAdminManager().isAdmin(player) && !player.hasPermission("core.cmd.adminplus"))
			event.setCancelled(true);
	}

	@EventHandler
	private void onInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {
			Player player = event.getPlayer();

			if (getManager().getAdminManager().isAdmin(player)) {
				Player clicked = (Player) event.getRightClicked();
				ItemStack item = player.getInventory().getItemInHand();

				if (item.getType().equals(Material.AIR)) {
					player.performCommand("invsee " + clicked.getName());
				}
			}
		}
	}
}
