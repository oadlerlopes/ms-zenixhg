package br.com.zenix.hungergames.player.kit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.plugin.utilitaries.loader.Getter;
import br.com.zenix.core.spigot.player.item.ItemBuilder;
import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.manager.constructor.Management;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class KitManager extends Management {

	private static final HashMap<Integer, List<Integer>> groupKits = new HashMap<>();
	
	@Deprecated
	private static final HashMap<UUID, KitRotation> rotationRequest = new HashMap<>();
	
	private static final ArrayList<Kit> kits = new ArrayList<>();

	private static final List<String> items = new ArrayList<>();

	public KitManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		return loadKits();
	}

	public Kit getKit(String string) {
		for (Kit kit : kits)
			if (kit.getName().equalsIgnoreCase(string))
				return kit;
		return null;
	}

	public Kit getKit(int i) {
		for (Kit kit : kits)
			if (kit.getID() == i)
				return kit;
		return null;
	}

	public Kit getKitBySize(int i) {
		return kits.get(i);
	}

	public boolean loadKits() {
		getLogger().log("Starting trying to load all the kits of the plugin.");

		for (Class<?> c : Getter.getClassesForPackage(getManager().getPlugin(), "br.com.zenix.hungergames.player.kit.abilities")) {
			if (Kit.class.isAssignableFrom(c) && (c != Kit.class)) {
				try {
					Kit kit = (Kit) c.getConstructor(Manager.class).newInstance(getManager());

					if (registerKitData(kit) == null) {
						throw new Exception("Error to register the kit " + kit.getName());
					}

					if (kit.isActive()) {

						kits.add(kit);

						getLogger().debug("The kit " + kit.getName() + "(" + kit.getID() + "," + kit.toString() + ") was added to list of kits!");
					}

					if (kit.getItems() != null) {
						for (ItemStack item : kit.getItems()) {
							if (item != null) {
								if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
									items.add(item.getItemMeta().getDisplayName());
								}
							}
						}
					}
					getLogger().debug("The kit " + kit.getName() + "(" + kit.getID() + "," + kit.toString() + ") was correctly loaded!");
				} catch (Exception exception) {
					getLogger().error("Error to load the kit " + c.getCanonicalName() + "(" + c.toString() + "), stopping the process!", exception);
					return false;
				}
			}
		}

		if (getManager().getCupManager().isCup()) {
			String[] str =  { "Stomper", "Endermage", "Madman", "Nocturne", "Urgal", "Tank", "Justice", "Demoman", "Barbarian",
					"Achilles", "Poseidon", "Jackhammer" } ;
			
			for (String string : str){
				System.out.print(string + ">> PRE");
				if (getKit(string) != null){
					System.out.print(string + ">> LOAD");
					getKit(string).setActive(false);
				}
			}
		}
		
		return true;
	}

	public Kit registerKitData(Kit kit) {
		try {

			PreparedStatement statement = getManager().getMySQLManager().getMySQL().getConnection().prepareStatement(CoreQueries.GLOBAL_KITS_SELECT.toString());
			statement.setInt(1, getManager().getServerType().getId());
			statement.setString(2, kit.getName());
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				int id = result.getInt(1);
				kit.setID(id);

				result.close();
				statement.close();
			} else {
				statement = getManager().getMySQLManager().getMySQL().getConnection().prepareStatement(CoreQueries.GLOBAL_KITS_INSERT.toString());
				statement.setString(1, kit.getName());
				statement.setInt(2, getManager().getServerType().getId());
				statement.execute();
				statement.close();

				return registerKitData(kit);
			}

			return kit;

		} catch (Exception exception) {
			getLogger().error("Error to register the kit '" + kit.getName() + "', stopping the process!", exception);
			return null;
		}

	}

	public void registerKits() {
		for (Kit kit : kits) {
			Bukkit.getPluginManager().registerEvents(kit, getManager().getPlugin());
		}
	}

	public ArrayList<Kit> getKits() {
		return kits;
	}

	public List<Kit> getPlayerKits(Player player) {
		List<Kit> playerKits = new ArrayList<>();
		for (Kit kit : kits) {
			if (hasKit(player, kit)) {
				playerKits.add(kit);
			}
		}
		return playerKits;
	}

	public List<Kit> getPlayerDontKits(Player player) {
		List<Kit> playerKits = new ArrayList<>();
		for (Kit kit : kits) {
			if (!hasKit(player, kit)) {
				playerKits.add(kit);
			}
		}
		return playerKits;
	}

	public List<Kit> getPlayerSecondaryKits(Player player) {
		List<Kit> playerKits = new ArrayList<>();
		for (Kit kit : kits) {
			if (hasKitSecondary(player, kit)) {
				playerKits.add(kit);
			}
		}
		return playerKits;
	}

	public boolean hasKit(Player player, Kit kit) {
		if (getManager().getServerType() == ServerType.HG)
			return true;

		if (player.hasPermission("hgkit." + kit.getName().toLowerCase()) || kit.isFree() || player.hasPermission("hgkit.*")) {
			return true;
		}
		return false;
	}

	public boolean hasKitSecondary(Player player, Kit kit) {
		if (player.hasPermission("hgkit." + kit.getName().toLowerCase()) || kit.isFree() || player.hasPermission("hgkit.*")) {
			return true;
		}
		return false;
	}

	public boolean hasKit(Player player, String kit) {
		return hasKit(player, getKit(kit));
	}

	public boolean isItemKit(ItemStack item) {
		for (String string : items) {
			if (new ItemBuilder(Material.AIR).checkItem(item, string)) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	public HashMap<UUID, KitRotation> getRotationRequest() {
		return rotationRequest;
	}

	public HashMap<Integer, List<Integer>> getGroupKits() {
		return groupKits;
	}
}
