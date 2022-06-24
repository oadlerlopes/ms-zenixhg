package br.com.zenix.hungergames.player.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.zenix.core.spigot.player.item.ItemBuilder;
import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.gamer.Gamer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Kit implements Listener {

	private static final ItemBuilder itemBuilder = new ItemBuilder(Material.AIR);

	private final List<ServerType> serverTypes = new ArrayList<>();
	private final Manager manager;

	private ConcurrentHashMap<UUID, Long> cooldown = new ConcurrentHashMap<>();

	private ItemStack icon;
	private ItemStack[] items;

	private String name, description;

	private boolean free, active, recent;

	private double cooldownTime;
	private int id, price;

	public Kit(Manager manager) {
		this.manager = manager;
		this.active = true;
		this.free = false;
		this.name = getClass().getSimpleName();
		this.items = null;
		this.recent = false;
	}

	public String getName() {
		return name;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public double getCooldown(Player player) {
		return cooldown.contains(player.getUniqueId()) ? 0 : (cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 10;
	}

	public double getCooldownTime() {
		return cooldownTime;
	}

	public String getDescription() {
		return this.description;
	}

	public ItemStack[] getItems() {
		return this.items;
	}

	public Manager getManager() {
		return this.manager;
	}

	public Gamer getGamer(UUID uuid) {
		return getManager().getGamerManager().getGamer(uuid);
	}

	public Gamer getGamer(Player player) {
		return getManager().getGamerManager().getGamer(player);
	}

	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}

	public void setItems(ItemStack... itens) {
		this.items = itens;
	}

	public void addCooldown(Player player) {
		addCooldown(player, cooldownTime);
	}

	public void addCooldown(Player player, double segundos) {
		cooldown.put(player.getUniqueId(), (long) (System.currentTimeMillis() + (segundos * 1000)));
	}

	public void removeCooldown(Player player) {
		cooldown.remove(player.getUniqueId());
	}

	public void setRecent(Boolean bool) {
		this.recent = bool;
	}

	public void sendCooldown(Player player) {
		double cooldown = getCooldown(player) / 100;
		
		player.sendMessage("§e§lCOOLDOWN §fAguarde mais §e§l" + cooldown + "§f segundos para usar novamente!");
	}

	public void give(Player player) {
		if (this.items == null) {
			return;
		}

		for (ItemStack item : this.items) {
			player.getInventory().addItem(item);
		}
	}

	public void setDescription(String desc) {
		this.description = desc;
		ItemMeta meta = this.icon.getItemMeta();

		List<String> lore = new ArrayList<String>();

		lore.add(ChatColor.RESET + desc);

		meta.setDisplayName(ChatColor.RESET + this.name);
		meta.setLore(lore);

		this.icon.setItemMeta(meta);
	}

	public void setFree(boolean bool) {
		this.free = bool;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setCooldownTime(double cooldownTime) {
		this.cooldownTime = cooldownTime;
	}

	public void setServerTypes(ServerType... serverTypes) {
		for (ServerType type : serverTypes) {
			this.serverTypes.add(type);
		}
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public boolean inCooldown(Player player) {
		return cooldown.containsKey(player.getUniqueId()) ? getCooldown(player) / 100 >= 0 ? true : false : false;
	}

	public boolean isFree() {
		return free;
	}

	public boolean isRecent() {
		return recent;
	}

	public boolean isActive() {
		return active;
	}

	public int getPrice() {
		return price;
	}

	public int getID() {
		return id;
	}

	public List<ServerType> getServerType() {
		return serverTypes;
	}

	public boolean isPreGame() {
		return getManager().getGameManager().isPreGame();
	}

	public boolean isInvencibility() {
		return getManager().getGameManager().isInvencibility();
	}

	public boolean hasKit(Gamer gamer) {
		return (name.equals(gamer.getKit().getName()));
	}

	public boolean hasKit(Player player) {
		return (name.equals(manager.getGamerManager().getGamer(player).getKit().getName())) || (name.equals(manager.getGamerManager().getGamer(player).getKit2().getName()));
	}
	
	public boolean hasKitSecondary(Player player) {
		return (name.equals(manager.getGamerManager().getGamer(player).getKit2().getName()));
	}

	public boolean isKitItem(ItemStack item, Material material, String name) {
		return ((item != null) && (item.getType() == material) && (item.hasItemMeta()) && (item.getItemMeta().hasDisplayName()) && (item.getItemMeta().getDisplayName().equals(name)));
	}

	public boolean isKitItem(ItemStack item, String name) {
		return (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(name));
	}

	public ItemBuilder getItemBuilder() {
		return itemBuilder;
	}

	public ItemStack createItemStack(String name, Material material) {
		return getItemBuilder().setMaterial(material).setName(name).getStack();
	}

	public ItemStack createItemStack(String name, Material material, int amount) {
		return getItemBuilder().setMaterial(material).setName(name).setAmount(amount).getStack();
	}

	public ItemStack createItemStack(String name, String[] desc, Material material, int amount) {
		return getItemBuilder().setMaterial(material).setName(name).setAmount(amount).setDescription(desc).getStack();
	}

	public ItemStack createItemStack(String name, Material material, int amount, Enchantment enchant, int level) {
		return getItemBuilder().setMaterial(material).setName(name).setAmount(amount).setEnchant(enchant, level).getStack();
	}

}
