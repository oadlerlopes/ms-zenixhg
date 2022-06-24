package br.com.zenix.hungergames.commands.game;

import org.bukkit.command.CommandSender;

import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.game.structures.types.Feast;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ForceFeastCommand extends HungerCommand {

	public ForceFeastCommand() {
		super("forcefeast", "Spawne um novo feast.");
	}

	@Override 
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (!hasPermission(commandSender, "forcefeast")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		if (getManager().getGameManager().isPreGame()) {
			commandSender.sendMessage("§e§lFORCEFEAST §fÉ necessário que a partida esteja em jogo para spawnar o feast!");
			return false;
		}

		Feast feast = new Feast(getManager(), 50);
		feast.forceFeast();
		commandSender.sendMessage("§e§lFORCEFEAST §fVocê criou um novo §6§lFEAST");
		return true;
	}

}
