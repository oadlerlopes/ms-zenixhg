package br.com.zenix.hungergames.commands.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.hungergames.game.custom.HungerCommand;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class FeastCommand extends HungerCommand {

	public FeastCommand() {
		super("feast", "Veja as informações do feast.");
	}

	@Override 
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (getManager().getGameManager().getTimer().getFeast() == null) {
			commandSender.sendMessage("§6§lFEAST §fO §e§lFEAST§f ainda não §6§lNASCEU!");
			return false;
		}
		((Player) commandSender).setCompassTarget(getManager().getGameManager().getTimer().getFeast().getLocation());
		commandSender.sendMessage("§6§lFEAST §fBússola §e§lAPONTANDO§F para o §6§lFEAST§F!");

		return true;
	}

}
