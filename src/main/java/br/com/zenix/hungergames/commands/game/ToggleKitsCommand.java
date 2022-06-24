package br.com.zenix.hungergames.commands.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.player.gamer.Gamer;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ToggleKitsCommand extends HungerCommand {

	public ToggleKitsCommand() {
		super("togglekits", "Defina as opções de um kit.");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (!hasPermission(commandSender, "togglekits")) {
			sendPermissionMessage(commandSender);
			return false;
		}
		
		if (args.length != 2) {
			commandSender.sendMessage("§a§lTOGGLEKIT §fUse: /togglekits <kit/all> <on/off>");
			return false;
		}

		Kit kit = getManager().getKitManager().getKit(args[0]);
		if (!args[0].equalsIgnoreCase("all") && kit == null) {
			commandSender.sendMessage("§a§lTOGGLEKIT §fO kit §a" + args[0] + "§f não existe!");
			return false;
		}

		boolean active = args[1].equalsIgnoreCase("on") ? true : false;

		if (args[0].equalsIgnoreCase("all")) {
			for (Kit kits : getManager().getKitManager().getKits()) {
				getManager().getKitManager().getKit(kits.getName()).setActive(active);
			}
			if (!active) {
				for (Gamer gamers : getManager().getGamerManager().getGamers().values()) {
					gamers.setKit(getManager().getKitManager().getKit("Nenhum"));
				}
			}

			Bukkit.broadcastMessage(
					"§7Todos os kits: " + (active ? "§aativados".toLowerCase() : "§cdesativados".toLowerCase()));
		} else {
			if (!active) {
				for (Gamer gamers : getManager().getGamerManager().getGamers().values()) {
					if (gamers.getKit().getName().equalsIgnoreCase(kit.getName()))
						gamers.setKit(getManager().getKitManager().getKit("Nenhum"));
				}
			}
			getManager().getKitManager().getKit(kit.getName()).setActive(active);
			Bukkit.broadcastMessage("§7Kit " + kit.getName() + ": "
					+ (active ? "§aativado".toUpperCase() : "§cdesativado".toUpperCase()));
		}

		return false;
	}

}
