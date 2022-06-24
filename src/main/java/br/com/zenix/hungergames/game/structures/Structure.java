package br.com.zenix.hungergames.game.structures;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import br.com.zenix.core.spigot.bo2.BO2Constructor.FutureBlock;
import br.com.zenix.hungergames.manager.Manager;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public abstract class Structure {

	private final List<FutureBlock> blocks;

	private final Manager manager;

	protected Location location;

	public Structure(Manager manager) {
		this.manager = manager;

		this.blocks = new ArrayList<>();
	}

	public Structure(Manager manager, Location location) {
		this.manager = manager;
		this.location = location;

		this.blocks = new ArrayList<>();
	}

	public Manager getManager() {
		return manager;
	}

	public Location getLocation() {
		return location;
	}

	public List<FutureBlock> getBlocks() {
		return blocks;
	}
}
