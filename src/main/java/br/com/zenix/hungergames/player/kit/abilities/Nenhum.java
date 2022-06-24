package br.com.zenix.hungergames.player.kit.abilities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Nenhum extends Kit {

	public Nenhum(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(0);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.GLASS));
		setItems(new ItemStack(Material.AIR));
		setFree(false);
		setDescription("Kit sem habilidade");
	}
}
