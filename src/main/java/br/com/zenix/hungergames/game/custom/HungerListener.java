package br.com.zenix.hungergames.game.custom;

import org.bukkit.event.Listener;

import br.com.zenix.hungergames.HungerGames;
import br.com.zenix.hungergames.manager.Manager;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class HungerListener implements Listener {

	public Manager getManager() {
		return HungerGames.getManager();
	}

}
