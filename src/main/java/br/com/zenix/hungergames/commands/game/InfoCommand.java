package br.com.zenix.hungergames.commands.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.player.gamer.Gamer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class InfoCommand extends HungerCommand {

	public InfoCommand() {
		super("info", "Veja as informações do jogo!");
	}

	@Override 
	public boolean execute(CommandSender commandSender, String s, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (args.length == 0) {
			sendInfo(getManager().getGamerManager().getGamer((Player) commandSender));
			return false;
		} else if (args.length == 1){
			if (((Player)commandSender).hasPermission("hunger.staff")){
				
				Player sujeito;
				
				if (Bukkit.getPlayer(args[0]) == null){
					commandSender.sendMessage("§6§lINFO §fPlayer invalido!");
					return false;
				} else {
					sujeito = Bukkit.getPlayer(args[0]);
				}
				
				sendInfo(getManager().getGamerManager().getGamer(sujeito));
				return false;
			}
		}

		return true;
	}

	public void sendInfo(Gamer gamer) {
		if (getManager().getGameManager().isPreGame()) {
			gamer.sendMessage(" ");
			gamer.sendMessage("§e§lINFO §fInformações do game:");
			gamer.sendMessage(" ");
			gamer.sendMessage("§fComeçando em: §6" + getManager().getUtils().formatTime(getManager().getGameManager().getGameTime()));
			gamer.sendMessage("§fJogadores: §e" + getManager().getGamerManager().getAliveGamers().size() + "/" + Bukkit.getMaxPlayers());
			gamer.sendMessage("§fKills: §a" + gamer.getGameKills());
			gamer.sendMessage("§fKit: §a" + gamer.getKit().getName());
			gamer.sendMessage("§fServidor: §a" + getManager().getCoreManager().getServerIP());
			gamer.sendMessage(" ");

		} else if (getManager().getGameManager().isInvencibility()) {
			gamer.sendMessage(" ");
			gamer.sendMessage("§e§lINFO §fInformações do game:");
			gamer.sendMessage(" ");
			gamer.sendMessage("§fInvencibilidade acaba em: §6" + getManager().getUtils().formatTime(getManager().getGameManager().getGameTime()));
			gamer.sendMessage("§fJogadores: §e" + getManager().getGamerManager().getAliveGamers().size() + "/" + Bukkit.getMaxPlayers());
			gamer.sendMessage("§fKills: §a" + gamer.getGameKills());
			gamer.sendMessage("§fKit: §a" + gamer.getKit().getName());
			gamer.sendMessage("§fServidor: §a" + getManager().getCoreManager().getServerIP());
			gamer.sendMessage(" ");
		} else if (getManager().getGameManager().isGame()) {
			gamer.sendMessage(" ");
			gamer.sendMessage("§e§lINFO §fInformações do game:");
			gamer.sendMessage(" ");
			gamer.sendMessage("§fJogo em: §6" + getManager().getUtils().formatTime(getManager().getGameManager().getGameTime()));
			gamer.sendMessage("§fPlayers: §e" + getManager().getGamerManager().getAliveGamers().size() + "/" + Bukkit.getMaxPlayers());
			gamer.sendMessage("§fKit: §a" + gamer.getKit().getName());
			gamer.sendMessage("§fKills: §a" + gamer.getGameKills());
			gamer.sendMessage("§fServidor: §a" + getManager().getCoreManager().getServerIP());
			gamer.sendMessage(" ");
		}
	}

}
