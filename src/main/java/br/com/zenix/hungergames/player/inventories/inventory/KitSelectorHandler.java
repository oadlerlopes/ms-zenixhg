package br.com.zenix.hungergames.player.inventories.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.spigot.player.item.ItemBuilder;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.inventories.CustomInventory;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class KitSelectorHandler extends CustomInventory {

	public static final HashMap<UUID, Integer> inventoryIndex = new HashMap<>();

	public KitSelectorHandler(Manager manager) {
		super(manager);
	}

	public void generate(Player player, KitType mode) {
		if (mode.equals(KitType.YOUR_KITS))
			setTitle("Selecione seu kit");
		else if (mode.equals(KitType.SECONDARY_KITS))
			setTitle("Selecione seu kit 2");

		Inventory inventory = Bukkit.createInventory(player, 54, getTitle());
		int index = inventoryIndex.containsKey(player.getUniqueId()) ? inventoryIndex.get(player.getUniqueId()) : 1;
		update(player, mode, inventory, index);
		player.openInventory(inventory);

	}

	@SuppressWarnings("unused")
	private List<ItemStack> getKits(KitType mode, Player player, int index) {

		List<ItemStack> toShow = new ArrayList<>();
		List<Kit> kits = new ArrayList<Kit>();

		for (Kit kit1 : getManager().getKitManager().getKits()) {
			if (kit1.isActive()) {
				kits.add(kit1);
			}
		}

		for (int range : range(index * 8 - 7, (index + 3) * 8 - 7)) {
			Kit kit = null;

			if (range < kits.size())
				if (kits.get(range).isActive())
					kit = kits.get(range);

			if (kit != null) {

				boolean haskit = getManager().getKitManager().hasKit(player, kit);

				if (toShow.size() > 24)
					break;

				if (kit == null) {
					toShow.add(
							new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§e§a").setDurability(7).getStack());
				} else {
					if (kit.isActive() && haskit) {
						toShow.add(new ItemBuilder(kit.getIcon().getType()).setName("§a" + kit.getName())
								.setDescription(getDescription(mode, haskit, kit)).getStack());
					}
				}
			}
		}

		return toShow;

	}

	private String getDescription(KitType mode, boolean haskit, Kit kit) {
		String description = new String();

		description = "\n§f" + kit.getDescription() + "\n \n§aClique para escolher!";

		return description;
	}

	public void update(Player player, KitType mode, Inventory inventory, int index) {
		inventoryIndex.put(player.getUniqueId(), index);

		inventory.clear();

		boolean hasGlass = false;
		int slot = index == 1 ? 11 : 11;

		if (index > 3)
			return;

		List<ItemStack> items = getKits(mode, player, index);
		for (ItemStack item : items) {

			if (slot % 9 > 7)
				slot += 3;

			inventory.setItem(slot, item);

			if (new ItemBuilder(Material.AIR).checkItem(item, "§e§a"))
				hasGlass = true;

			slot++;

		}

		new ItemBuilder(Material.CHEST).setName("§7Seus Kits").setGlowed(mode == KitType.YOUR_KITS ? true : false)
				.build(inventory, 18);
		new ItemBuilder(Material.ENDER_CHEST).setName("§7Loja de Kits")
				.setGlowed(mode == KitType.YOUR_KITS ? false : true).build(inventory, 27);

		if (index != 1) {
			new ItemBuilder(Material.INK_SACK).setDurability(1).setName("§cSubir").build(inventory, 49);
		} else {
			new ItemBuilder(Material.INK_SACK).setDurability(8).setName("§7Subir").build(inventory, 49);
		}

		if (!hasGlass) {
			if (index != 3) {
				new ItemBuilder(Material.INK_SACK).setDurability(10).setName("§aDescer").build(inventory, 50);
			}
		} else {
			new ItemBuilder(Material.INK_SACK).setDurability(8).setName("§7Descer").build(inventory, 50);
		}

		player.playSound(player.getLocation(), Sound.CLICK, 5F, 5F);
		player.updateInventory();
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (event.getClickedInventory() != null) {
			if (event.getClickedInventory().getTitle().startsWith("Selecione seu kit")) {
				event.setCancelled(true);
				if (event.getCurrentItem() != null) {

					if (!inventoryIndex.containsKey(player.getUniqueId()))
						inventoryIndex.put(player.getUniqueId(), 1);

					Material type = event.getCurrentItem().getType();
					Integer index = inventoryIndex.get(player.getUniqueId());

					if (type == Material.AIR)
						return;

					if (!type.equals(Material.INK_SACK) && !type.equals(Material.CHEST)
							&& !type.equals(Material.GOLD_INGOT) && !type.equals(Material.ENDER_CHEST)
							&& !type.equals(Material.STAINED_GLASS_PANE)) {
						Kit kit = getManager().getKitManager().getKit(event.getCurrentItem().getItemMeta()
								.getDisplayName().replace("§a", "").replace("§c", ""));
						player.chat("/kit " + kit.getName());
						player.closeInventory();
					} else if (type.equals(Material.INK_SACK)) {
						if (event.getCurrentItem().getDurability() == 10) {
							update(player, KitType.YOUR_KITS, event.getInventory(), index + 1);
						} else if (event.getCurrentItem().getDurability() == 1) {
							update(player, KitType.YOUR_KITS, event.getInventory(), index - 1);
						}
					} else if (type.equals(Material.ENDER_CHEST)) {
						player.closeInventory();
						player.sendMessage("§c§lKITS§f A loja de kits estará §4§lDISPONIVEL§f em breve!");
					}
				}
			} else if (event.getClickedInventory().getTitle().startsWith("Selecione seu kit 2")) {
				event.setCancelled(true);
				if (event.getCurrentItem() != null) {

					if (!inventoryIndex.containsKey(player.getUniqueId()))
						inventoryIndex.put(player.getUniqueId(), 1);

					Material type = event.getCurrentItem().getType();
					Integer index = inventoryIndex.get(player.getUniqueId());

					if (type == Material.AIR)
						return;

					if (!type.equals(Material.INK_SACK) && !type.equals(Material.CHEST)
							&& !type.equals(Material.GOLD_INGOT) && !type.equals(Material.ENDER_CHEST)
							&& !type.equals(Material.STAINED_GLASS_PANE)) {
						Kit kit = getManager().getKitManager().getKit(event.getCurrentItem().getItemMeta()
								.getDisplayName().replace("§a", "").replace("§c", ""));
						player.chat("/kit2 " + kit.getName());
						player.closeInventory();
					} else if (type.equals(Material.INK_SACK)) {
						if (event.getCurrentItem().getDurability() == 10) {
							update(player, KitType.YOUR_KITS, event.getInventory(), index + 1);
						} else if (event.getCurrentItem().getDurability() == 1) {
							update(player, KitType.YOUR_KITS, event.getInventory(), index - 1);
						}
					} else if (type.equals(Material.ENDER_CHEST)) {
						player.closeInventory();
						player.sendMessage("§c§lKITS§f A loja de kits estará §4§lDISPONIVEL§f em breve!");
					}

				}

			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (inventoryIndex.containsKey(event.getPlayer().getUniqueId()))
			inventoryIndex.remove(event.getPlayer().getUniqueId());
	}

	public enum KitType {
		YOUR_KITS, SECONDARY_KITS;
	}

	public int[] range(int start, int stop) {
		int[] result = new int[stop - start];

		for (int i = 0; i < stop - start; i++)
			result[i] = start + i;

		return result;
	}

}
