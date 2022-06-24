package br.com.zenix.hungergames.player.kit.abilities.normal;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Barbarian extends Kit {

	private static HashMap<UUID, Integer> bkills = new HashMap<>();

	public Barbarian(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(43000);
		setCooldownTime(0D);
		setIcon(createItemStack("", Material.GOLD_SWORD, 1, Enchantment.DAMAGE_ALL, 1));
		setFree(false);
		setDescription("Transforme-se em um barbaro da idade média e evolua sua espada a cada vítima.");
		setRecent(false);

		ItemStack item = createItemStack("§cBarbarian", Material.WOOD_SWORD, 1, Enchantment.DURABILITY, 1);
		item.setDurability((short) 45);

		setItems(item);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEnchant(PrepareItemEnchantEvent event) {
		Player player = event.getEnchanter();
		if (hasKit(player)) {
			event.setCancelled(true);
			player.sendMessage("§d§lBARBARIAN §fVocê não pode §5§lENCANTAR§f items com seu kit atual!");
		}
	}

	@EventHandler
	public void onBigorna(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (!hasKit(player)) {
			return;
		}
		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (block.getType() == Material.ANVIL)) {
			event.setCancelled(true);
			player.sendMessage("§d§lBARBARIAN §fVocê não pode §5§lUSAR§f a bigorna com seu kit atual!");
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!hasKit(player))
			return;

		if (isKitItem(player.getItemInHand(), "§cBarbarian")) {
			player.getItemInHand().setDurability((short) 0);
			EntityPlayer eP = ((CraftPlayer) player).getHandle();
			eP.playerConnection.sendPacket(new PacketPlayOutSetSlot(eP.activeContainer.windowId, 44 - Math.abs(player.getInventory().getHeldItemSlot() - 8), CraftItemStack.asNMSCopy(player.getItemInHand())));
		}
	}

	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		if ((event.getDamager() instanceof Player)) {
			Player player = (Player) event.getDamager();
			if (!hasKit(player)) {
				return;
			}

			if (isKitItem(player.getItemInHand(), "§cBarbarian")) {
				player.getItemInHand().setDurability((short) 0);
				EntityPlayer eP = ((CraftPlayer) player).getHandle();
				eP.playerConnection.sendPacket(new PacketPlayOutSetSlot(eP.activeContainer.windowId, 44 - Math.abs(player.getInventory().getHeldItemSlot() - 8), CraftItemStack.asNMSCopy(player.getItemInHand())));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onKill(PlayerDeathEvent e) {
		if (((e.getEntity() instanceof Player)) && ((e.getEntity().getKiller() instanceof Player))) {
			Player playerKiller = e.getEntity().getKiller();
			if (!hasKit(playerKiller)) {
				return;
			}

			if (!bkills.containsKey(playerKiller.getUniqueId())) {
				bkills.put(playerKiller.getUniqueId(), 1);
			} else {
				bkills.put(playerKiller.getUniqueId(), bkills.get(playerKiller.getUniqueId()) + 1);
			}

			boolean update = false;
			int changed = 0;
			if (bkills.containsKey(playerKiller.getUniqueId())) {
				if (bkills.get(playerKiller.getUniqueId()) % 3 == 0) {
					changed++;
				}
			}

			int times = 1;
			if (changed == 1) {
				update = true;
			}
			if (update) {
				boolean sword = false;
				int slot = -1;
				for (int i = 0; i < playerKiller.getInventory().getSize(); i++) {
					ItemStack item = playerKiller.getInventory().getItem(i);
					if (item != null) {
						if (item.getType().toString().contains("_SWORD")) {
							if (item.hasItemMeta()) {
								if (item.getItemMeta().hasDisplayName()) {
									if (item.getItemMeta().getDisplayName().contains("§cBarbarian")) {
										sword = true;
										slot = i;
										break;
									}
								}
							}
						}
					}
				}
				if (sword) {
					for (int i = 0; i < times; i++) {
						playerKiller.getInventory().setItem(slot, update(playerKiller.getInventory().getItem(slot)));
					}
					playerKiller.sendMessage("§5§lBARBARIAN §fA sua espada foi §5§lUPADA!");
				}
			}
		}
	}

	private ItemStack update(ItemStack item) {
		if ((item.getType() == Material.DIAMOND_SWORD) && (item.getItemMeta().getDisplayName().contains("Barbarian"))) {
			item.setType(Material.DIAMOND_SWORD);
			item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		}
		if ((item.getType() == Material.IRON_SWORD) && (item.getItemMeta().getDisplayName().contains("Barbarian"))) {
			item.setType(Material.DIAMOND_SWORD);
		}
		if ((item.getType() == Material.STONE_SWORD) && (item.getItemMeta().getDisplayName().contains("Barbarian"))) {
			item.setType(Material.IRON_SWORD);
		}
		if ((item.getType() == Material.WOOD_SWORD) && (item.getItemMeta().getDisplayName().contains("Barbarian"))) {
			item.setType(Material.STONE_SWORD);
		}
		return item;
	}

}
