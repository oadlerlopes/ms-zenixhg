package br.com.zenix.hungergames.commands.kit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.player.gamer.Gamer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GiveKitCommand extends HungerCommand {

	public GiveKitCommand() {
		super("givekit", "Give kit to player.");
	}

	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!hasPermission(commandSender, "event")) {
			return false;
		}

		if (args.length == 0) {
			sendHelp(commandSender);
		} else if (args.length == 1) {
			Bukkit.getScheduler().runTaskAsynchronously(getManager().getCoreManager().getPlugin(),
					new AsyncPermissionSetTask(commandSender, args));
		} else {
			sendHelp(commandSender);
		}

		return false;
	}

	public boolean validString(String str) {
		return (str.matches("[a-zA-Z0-9_]+")) && str.length() >= 2 && str.length() <= 6;
	}

	private void sendHelp(CommandSender commandSender) {
		commandSender.sendMessage("§3§lGIVEKIT §fUse: /givekit <nick/all> ");
	}

	private final class AsyncPermissionSetTask implements Runnable {

		private final CommandSender commandSender;
		private final String[] args;

		private AsyncPermissionSetTask(CommandSender sender, String[] args) {
			this.commandSender = sender;
			this.args = args;
		}

		public void run() {

			String name = args[0];

			Player suject = null;

			if (args[0].equalsIgnoreCase("all")) {
				
				for (Player all : Bukkit.getOnlinePlayers()) {
					Gamer gamer = getManager().getGamerManager().getGamer(all);

					if (gamer.isAlive()) {
						all.getInventory().addItem(gamer.getKit().getItems());
					}
				}

				commandSender.sendMessage("§3§lGIVEKIT §fFoi adicionado ao PLAYERS");
				
			} else {

				if (Bukkit.getPlayer(name) != null) {
					suject = Bukkit.getPlayer(name);
				}

				Gamer gamer = getManager().getGamerManager().getGamer(suject);
				if (!gamer.isAlive()) {
					commandSender.sendMessage("§3§lGIVEKIT §fO PLAYER nao esta vivo!");
					return;
				}
				suject.getInventory().addItem(gamer.getKit().getItems());


				commandSender.sendMessage("§3§lGIVEKIT §fFoi adicionado ao PLAYER");
			}
		}
	}

}
