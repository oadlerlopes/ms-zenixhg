package br.com.zenix.hungergames.player.kitaward.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import br.com.zenix.hungergames.game.custom.HungerListener;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class AwardListener extends HungerListener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getClickedInventory() != null) {
			if (event.getClickedInventory().getTitle().contains("Kit supresa da partida")) {
				event.setCancelled(true);
			}
		}
	}

}
