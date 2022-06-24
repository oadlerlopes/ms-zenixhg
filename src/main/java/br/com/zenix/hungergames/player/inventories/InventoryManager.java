package br.com.zenix.hungergames.player.inventories;

import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.manager.constructor.Management;
import br.com.zenix.hungergames.player.inventories.inventory.KitSelectorHandler;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class InventoryManager extends Management {

	private KitSelectorHandler kitSelectorHandler;

	public InventoryManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		this.kitSelectorHandler = new KitSelectorHandler(getManager());

		return kitSelectorHandler != null;
	}

	public KitSelectorHandler getKitSelectorHandler() {
		return kitSelectorHandler;
	}

}
