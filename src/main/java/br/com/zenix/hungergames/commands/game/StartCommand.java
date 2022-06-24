package br.com.zenix.hungergames.commands.game;

import org.bukkit.command.CommandSender;

import br.com.zenix.hungergames.game.custom.HungerCommand;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class StartCommand extends HungerCommand {

	public StartCommand() {
		super("start", "Forçar o inicio da partida");
	}
 
	@Override
	public boolean execute(CommandSender commandSender, String s, String[] strings) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (!hasPermission(commandSender, "start")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		if (!getManager().getGameManager().isPreGame()) {
			commandSender.sendMessage("§a§lSTART §fVocê não pode §a§lINICIAR§f a partida depois que a mesma iniciou.");
			return false;
		}

		getManager().getGameManager().getTimer().startGame();
		commandSender.sendMessage("§a§lSTART §fVocê §a§lINICIOU§f a §2§lPARTIDA!");
		return true;
	}
}
