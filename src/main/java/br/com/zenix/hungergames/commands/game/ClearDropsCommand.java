package br.com.zenix.hungergames.commands.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;

import br.com.zenix.hungergames.game.custom.HungerCommand;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ClearDropsCommand extends HungerCommand {

	public ClearDropsCommand() {
		super("cleardrops");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (!hasPermission(commandSender, "cleardrops")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		Bukkit.getWorlds().get(0).getEntities().stream().filter(entity -> entity instanceof Item).forEach(entity -> entity.remove());
		commandSender.sendMessage("§3§lCLEARDROPS §fVocê limpou os §b§lDROPS§f do chao!");

		return true;
	}

}
