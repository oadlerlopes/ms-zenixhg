package br.com.zenix.hungergames.player.kit.abilities.normal;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;
import br.com.zenix.hungergames.player.kit.abilities.Nenhum;

public class Surprise extends Kit {

	public Surprise(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(49000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.CAKE));
		setFree(false);
		setDescription("Deixe o sistema escolher um kit aleatório para você independente se você possui ele ou não.");
		setRecent(false);
	}

	public Kit getRandomKit() {
		return getManager().getKitManager().getKits().get(getManager().getRandom().nextInt(getManager().getKitManager().getKits().size()));
	}

	public void give(Player p) {
		Kit kit = getRandomKit();

		while ((kit instanceof Nenhum) || (kit instanceof Surprise))
			kit = getRandomKit();

		if (getGamer(p).getKit().getName().equalsIgnoreCase("Surprise")) {
			if (kit.isActive() == false){
				kit = getRandomKit();
				getGamer(p).setKit(kit);
			} else {
				getGamer(p).setKit(kit);
			}
		}

		kit.give(p);
		p.sendMessage("§B§LSURPRISE §fO Surprise selecionou o kit §3§L" + kit.getName());
	}

}
