package br.com.zenix.hungergames.commands.game;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.server.Variables;
import br.com.zenix.core.spigot.server.minigames.hungergames.ServerEvents;
import br.com.zenix.hungergames.game.custom.HungerCommand;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class EventoCommand extends HungerCommand {

	public EventoCommand() {
		super("event", "Desabilite e habilite o modo evento");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}
		if (!hasPermission(commandSender, "evento")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		if (args.length == 2) {
			for (ServerEvents serverEvents : ServerEvents.values()) {
				if (serverEvents.toString().toLowerCase().equals(args[0].toLowerCase())) {

					commandSender.sendMessage("§e§lEVENTO §fO evento §6" + serverEvents.getName() + " foi iniciado!");
					
					Twitter twitter = getManager().getCoreManager().getTwitterManager().getDefaultTwitter();
					String statusMessage = "🎮 Evento - HungerGames » " + serverEvents.getName() + " \n"
							+ "📦 Prêmio: "
							+ (args[1].toLowerCase().equals("4fun") ? "Sem prêmios" : "1 dia de RANK ULTIMATE")
							+ "\n\nConecte-se por eventos.zenix.cc";

					File file = new File(getManager().getPlugin().getDataFolder(), "evento.png");

					StatusUpdate status = new StatusUpdate(statusMessage);
					status.setMedia(file); 

					try {
						twitter.updateStatus(status);
					} catch (TwitterException e) {
						e.printStackTrace();
					}
					
					Variables.EVENT = true;
					Variables.serverEvent = serverEvents;
					getManager().getGameManager().getTimer().setEvent(true);
					
					if (args[1].toLowerCase().contains("ultimate")) {
						getManager().getGameManager().getTimer().setPrizeEvent(true);
					}
				}
			}

			for (Player players : Bukkit.getOnlinePlayers()) {
				Account account = getManager().getCoreManager().getAccountManager().getAccount(players);

				for (PunishRecord punish : account.getPunishRecords().values()) {
					if (punish != null)
						if (punish.getMotive().toLowerCase().contains("blacklist"))
							players.kickPlayer("§c§lUm evento está acontecendo e você está blacklist dos eventos!");

				}
			}

		} else

		{
			commandSender.sendMessage("§e§lEVENTO §fUse: /evento <"
					+ ServerEvents.getEvents().toString().toLowerCase().replace("[", "").replace("]", "")
					+ "> <4fun, ultimate> ");
		}

		return false;
	}
}
