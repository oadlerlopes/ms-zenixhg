package br.com.zenix.hungergames.player.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.manager.constructor.Management;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class AdminManager extends Management {

	private static ArrayList<UUID> admin = new ArrayList<UUID>(), quick = new ArrayList<UUID>();
	public static HashMap<UUID, ItemStack[]> items = new HashMap<UUID, ItemStack[]>();

	public AdminManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		return true;
	}

	public void setAdmin(Player player, boolean admin) {
		if (admin)
			setAdmin(player);
		else
			setPlayer(player);
	}

	public void setAdmin(Player player) {
		if ((!admin.contains(player.getUniqueId())) || (admin.isEmpty())) {
			admin.add(player.getUniqueId());
		}
		player.setGameMode(GameMode.CREATIVE);

		getManager().getVanish().makeVanished(player);
		getManager().getVanish().updateVanished();

		player.sendMessage("§4§lADMIN§f Você está no modo §c§lADMIN");
		player.sendMessage("§6§lVANISH§f Você está invisível para " + getInvisible(player) + " e abaixo!");
	}

	public void setPlayer(Player player) {
		admin.remove(player.getUniqueId());

		getManager().getVanish().makeVisible(player);
		getManager().getVanish().updateVanished();

		if (player.isOnline()) {
			player.setGameMode(GameMode.SURVIVAL);
			player.sendMessage("§4§lADMIN§f Você saiu do modo §c§lADMIN");
			player.sendMessage("§6§lVANISH§f Você esta VISIVEL para TODOS OS PLAYERS");
		}

	}

	public void setPlayerQuit(Player player) {
		admin.remove(player.getUniqueId());

		for (Player players : Bukkit.getOnlinePlayers()) {
			players.showPlayer(player);
		}

		if (player.isOnline()) {
			player.getInventory().clear();

			player.setGameMode(GameMode.SURVIVAL);
		}

	}

	public AdminLevel getPlayerLevel(Player p) {
		return (AdminLevel) Vanish.vanished.get(p.getUniqueId());
	}

	public boolean isAdmin(Player player) {
		return admin.contains(player.getUniqueId());
	}

	public boolean isVanished(Player p) {
		return (Vanish.vanished.containsKey(p.getUniqueId()));
	}

	public boolean isQuick(Player player) {
		return quick.contains(player.getUniqueId());
	}

	public static enum AdminLevel {
		PLAYER, MOD, MODPLUS, ADMIN, DONO;
	}

	private String getInvisible(Player player) {
		if (player.hasPermission("commons.tag.dono")) {
			return "§4§lDIRETORES§f";
		}
		if (player.hasPermission("commons.tag.diretor")) {
			return "§4§lADMINS§f";
		}
		if (player.hasPermission("commons.tag.admin")) {
			return "§5§lMODPLUS§f";
		}
		if (player.hasPermission("commons.tag.modplus")) {
			return "§5§lMOD§f";
		}
		if (player.hasPermission("commons.tag.youtuber")) {
			return "§5§lTRIAL§f";
		}
		if (player.hasPermission("commons.tag.trial")) {
			return "§5§lTRIAL§f";
		}
		return "§7§lJOGADORES§f";
	}
}