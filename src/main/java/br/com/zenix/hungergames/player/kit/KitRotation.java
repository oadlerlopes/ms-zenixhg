package br.com.zenix.hungergames.player.kit;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.zenix.core.spigot.player.permissions.constructor.Rank;
import br.com.zenix.hungergames.manager.Manager;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

@Deprecated
public class KitRotation {

	private static final HashMap<Integer, List<Integer>> groupKits = new HashMap<>();

	private Manager manager;

	@SuppressWarnings({ "unchecked", "static-access" })
	public KitRotation(Manager manager) {
		this.manager = manager;

		ArrayList<Kit> allKits = (ArrayList<Kit>) manager.getKitManager().getKits().clone();

		for (Rank group : manager.getCoreManager().getPermissionManager().getRanks().values()) {
			for (int i = 0; i < 5; i++) {
				Kit kit = allKits.get(manager.getRandom().nextInt(allKits.size()));

				List<Integer> kits = (List<Integer>) (groupKits.containsKey(i) ? groupKits.get(i) : new ArrayList<>());

				if (kits.contains(kit.getID())) {
					kit = allKits.get(manager.getRandom().nextInt(allKits.size()));
				}

				kits.add(kit.getID());

				groupKits.put(group.getId(), kits);
			}
		}

	}

	public void aplicateRotation() {
		manager.getMySQLManager().getMySQL().executeUpdate("DELETE FROM `global_kits` WHERE `type`='" + manager.getServerType().getId() + "';");

		for (int groupId : groupKits.keySet()) {

			List<Integer> kits = groupKits.get(groupId);

			for (int kit : kits) {

				try {
					PreparedStatement preparedStatement = manager.getMySQLManager().getMySQL().getSlaveConnection().prepareStatement("INSERT INTO `global_kits` (`group_id`, `kit`, `type`) VALUES (?, ?, ?);");
					preparedStatement.setInt(1, groupId);
					preparedStatement.setInt(2, kit);
					preparedStatement.setInt(3, manager.getServerType().getId());
					preparedStatement.execute();
					preparedStatement.close();
				} catch (Exception e) {
					manager.getLogger().error("Error when the plugin tried to aplicate the kits rotation, check the error in console.", e);
				}
			}
		}
	}

	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append("§c§m----------------------------");
		builder.append("\n");
		builder.append("§f§lRotação de Kits");
		builder.append("\n");
		builder.append(" ");
		builder.append("\n");

		for (int group : groupKits.keySet()) {

			Rank rank = manager.getCoreManager().getPermissionManager().getRank(group);

			builder.append("§6▪ §7" + rank.getName() + "§8: ");

			for (int i : groupKits.get(group)) {
				builder.append("§f" + manager.getKitManager().getKit(i).getName() + "§6,§f ");
			}

			builder.append("\n");
		}
		
		builder.append("\n");
		builder.append(" ");
		builder.append("\n");
		builder.append("§6▪ §fUse §6/rotation accept §fpara aplicar.");
		builder.append("\n");
		builder.append("§6▪ §fUse §e/rotation generate §fpara gerar novamente.");
		builder.append("\n");
		builder.append("§c§m----------------------------");
		return builder.toString();
	}
}
