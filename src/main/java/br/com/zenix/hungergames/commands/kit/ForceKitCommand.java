package br.com.zenix.hungergames.commands.kit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.player.gamer.Gamer;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ForceKitCommand extends HungerCommand {

	public ForceKitCommand() {
		super("forcekit", "Escolha um kit para um player.");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {

		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (!hasPermission(commandSender, "forcekit")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		if (args.length != 2) {
			commandSender.sendMessage("§d§lFORCEKIT§f Use: /forcekit <player/all> <kit>");
			return false;
		}

		boolean all = false;
		Player player = null;

		if (args[0].equalsIgnoreCase("all"))
			all = true;
		else {
			player = Bukkit.getPlayer(args[0]);
		}

		if (player == null && !all) {
			commandSender.sendMessage("§d§lFORCEKIT§f O sujeito está §5§OFFLINE");
			return false;
		}

		Kit kit = getManager().getKitManager().getKit(args[1]);
		if (kit == null) {
			commandSender.sendMessage("§d§lFORCEKIT§f O kit §5§l" + args[1] + "§f não existe!");
			return false;
		}

		if (all) {
			for (Gamer players : getManager().getGamerManager().getAliveGamers()) {
				players.setKit(kit);
				players.getKit().give(players.getPlayer());
			}

		} else {
			getManager().getGamerManager().getGamer(player).setKit(kit);
			getManager().getGamerManager().getGamer(player).getKit().give(player);

		}
		
		if (args[0].equalsIgnoreCase("all"))
			commandSender.sendMessage(
					"§d§lFORCEKIT§f Você setou o kit §5§l" + kit.getName() + "§f para os §5§lPLAYERS");
		else {
			commandSender.sendMessage(
					"§d§lFORCEKIT§f Você setou o kit §5§l" + kit.getName() + "§f para o player §5§l" + player.getName());
		}
		

		return true;
	}
}
