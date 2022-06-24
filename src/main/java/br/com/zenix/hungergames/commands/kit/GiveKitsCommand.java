package br.com.zenix.hungergames.commands.kit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import br.com.zenix.core.networking.packet.direction.out.PacketOutPlayerPermission;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GiveKitsCommand extends HungerCommand {

	public GiveKitsCommand() {
		super("givekits", "Give kit to player.");
	}

	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!hasPermission(commandSender, "event")) {
			return false;
		}

		if (args.length == 0) {
			sendHelp(commandSender);
		} else if (args.length == 1) {
			sendHelp(commandSender);
		} else if (args.length == 2) {
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
		commandSender.sendMessage("§3§lGIVEKIT §fUse: /givekit <nick/uuid> <kit>");
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

			UUID uuid = isUUID(name) ? UUID.fromString(name)
					: getManager().getCoreManager().getNameFetcher().getUUID(name);
			if (uuid == null) {
				commandSender.sendMessage("§3§lGIVEKIT §fO player com o nick/uuid §f'" + name
						+ "'§c não existe ou nunca entrou no servidor.");
				return;
			}

			int id = getManager().getCoreManager().getNameFetcher().getId(uuid);

			Kit kit = getManager().getKitManager().getKit(args[1]);

			if (kit == null) {
				commandSender.sendMessage("§3§lGIVEKIT §fEsse kit não existe!");
				return;
			}

			String permission = "hgkit." + kit.getName().toLowerCase();

			if (getManager().getCoreManager().getPermissionManager().updatePermissionsPlayer(id, permission, true,
					-1)) {
				commandSender.sendMessage("§3§lGIVEKIT §fVocê adicionou" + " o kit '" + permission + "' para o"
						+ " player '" + name + "'.");
			} else {
				commandSender.sendMessage("§3§lGIVEKIT §fErro ao modificar ao dar o kit para o player!");
			}

			Bukkit.getScheduler().runTask(getManager().getCoreManager().getPlugin(),
					new PostGiveKitTask(uuid, permission, true));

		}
	}

	private final class PostGiveKitTask implements Runnable {

		private final UUID uuid;
		private final String permission;
		private final boolean value;

		private PostGiveKitTask(UUID uuid, String permission, boolean value) {
			this.uuid = uuid;
			this.permission = permission;
			this.value = value;
		}

		public void run() {
			Account player = getManager().getCoreManager().getAccountManager().getAccount(uuid);
			if (player != null) {
				player.getPermissions().put(permission, value);
				player.setRank(player.getRank(), player.getRankTime());
			} else {
				getManager().getCoreManager().getPacketHandler()
						.sendGlobalPacket(new PacketOutPlayerPermission(uuid, permission, value));
			}
		}
	}

}
