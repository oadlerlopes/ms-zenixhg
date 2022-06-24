package br.com.zenix.hungergames.player.inventories;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import br.com.zenix.core.spigot.player.item.ItemBuilder;
import br.com.zenix.hungergames.manager.Manager;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public abstract class CustomInventory implements Listener {

	private Manager manager;

	private String title;
	private ItemBuilder itemBuilder;

	public CustomInventory(Manager manager) {
		this.manager = manager;
		this.itemBuilder = new ItemBuilder(Material.AIR);
	}

	@EventHandler
	public abstract void onClick(InventoryClickEvent event);

	public String getTitle() {
		return title;
	}

	public ItemBuilder getItemBuilder() {
		return itemBuilder;
	}

	public Manager getManager() {
		return manager;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
