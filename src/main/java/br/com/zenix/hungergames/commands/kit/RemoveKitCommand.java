package br.com.zenix.hungergames.commands.kit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.hungergames.game.custom.HungerCommand;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class RemoveKitCommand extends HungerCommand {

	public RemoveKitCommand() {
		super("removekit", "Remove o kit");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (!hasPermission(commandSender, "removekit")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		if (args.length != 1) {
			commandSender.sendMessage("§4§lREMOVE §fUse: /removekit <player>");
			return false;
		}

		Player removed = Bukkit.getPlayer(args[0]);
		if (removed == null) {
			commandSender.sendMessage("§4§lREMOVE §fO player está §c§lOFFLINE");
			return false;
		}

		commandSender.sendMessage("§4§lREMOVE §fVocê removeu o kit §c§l"
				+ getManager().getGamerManager().getGamer(removed).getKit().getName() + "§F do player §C§l"
				+ removed.getName());
		getManager().getGamerManager().getGamer(removed).setKit(getManager().getKitManager().getKit("Nenhum"));
		return true;
	}
}
