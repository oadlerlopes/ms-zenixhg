package br.com.zenix.hungergames.commands.game;

import org.bukkit.command.CommandSender;

import br.com.zenix.hungergames.game.custom.HungerCommand;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class IpCommand extends HungerCommand {

	public IpCommand() {
		super("ip", "Veja o ip do servidor que você está.");
	} 

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}
		
		if (args.length != 0) {
			commandSender.sendMessage("§e§lIP §fUse: /ip");
		} else {
			commandSender.sendMessage("§e§lIP §fVocê está no servidor §6§l" + getManager().getCoreManager().getServerIP());
		}

		return true;
	}
}
