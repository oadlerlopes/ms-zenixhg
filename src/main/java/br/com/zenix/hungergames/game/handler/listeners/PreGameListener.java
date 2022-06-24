package br.com.zenix.hungergames.game.handler.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import br.com.zenix.core.spigot.options.ServerOptions;
import br.com.zenix.core.spigot.player.events.PlayerInventoryOpenEvent;
import br.com.zenix.core.spigot.player.events.ServerTimeEvent;
import br.com.zenix.hungergames.game.custom.HungerListener;
import br.com.zenix.hungergames.player.gamer.Gamer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

@SuppressWarnings("deprecation")
public class PreGameListener extends HungerListener {

	@EventHandler
	private void onServerListPing(ServerListPingEvent event) {
		String time = getManager().getUtils().toTime(getManager().getGameManager().getGameTime());
		if (getManager().getGameManager().isPreGame()) {
			event.setMotd("§cIniciando em " + time + "!\n§eVisite §ewww.zenix.cc");
		} else if (getManager().getGameManager().isInvencibility()) {
			event.setMotd("§cEm progresso na invencibilidade. Tente www.zenix.cc.\n§eVisite §ewww.zenix.cc");
		} else {
			event.setMotd("§cEm progresso. Tente www.zenix.cc.\n§eVisite §ewww.zenix.cc");
		}
	}

	@EventHandler
	private void onInventaryOpen(PlayerInventoryOpenEvent event) {
		Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
		if (gamer.isSpectating()) {
			if (!gamer.getPlayer().hasPermission("hunger.staff")) {
				gamer.sendMessage("§6§lINVENTARIO §fVocê não pode §e§lABRIR§f isto agora.");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage().toLowerCase();
		Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
		if (!getManager().getGameManager().isPreGame()) {
			if (command.startsWith("/admin") || command.startsWith("/gamemode") || command.startsWith("/invsee")
					|| command.startsWith("/effect") || command.startsWith("/give") || command.startsWith("/enchant")) {
				if (!gamer.isSpectating() && !gamer.getPlayer().hasPermission("hunger.cmd.abusebypass"))
					event.setCancelled(true);
			}
		}
		if (!getManager().getAdminManager().isAdmin(event.getPlayer()) && !gamer.isSpectating()
				&& command.startsWith("/tp") && !gamer.getPlayer().hasPermission("hunger.cmd.abusebypass")) {
			gamer.sendMessage("§6§lCOMMAND §fVocê não pode usar isso agora!");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if (event.getCreatureType() == CreatureType.GHAST) {
			event.setCancelled(true);
		}
		if (getManager().getGameManager().isPreGame()) {
			event.getEntity().remove();
		}
	}

	@EventHandler
	public void aoExplodir(ExplosionPrimeEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onChuva(WeatherChangeEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);

	}

	@EventHandler
	public void onBreakBlocks(BlockBreakEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);
		else {
			int border = getManager().getGameManager().getTimer().getBorderSize() - 10;
			Block block = event.getBlock();
			Location worldLocation = block.getWorld().getSpawnLocation();
			if ((Math.abs(block.getLocation().getBlockX() + worldLocation.getBlockX()) >= border)
					|| (Math.abs(block.getLocation().getBlockZ() + worldLocation.getBlockZ()) >= border)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlaceBlocks(BlockPlaceEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);
		else {
			if (event.getPlayer().getWorld() == Bukkit.getWorld("world")) {
				int border = getManager().getGameManager().getTimer().getBorderSize() - 10;
				Block block = event.getBlock();
				Location worldLocation = block.getWorld().getSpawnLocation();
				if ((Math.abs(block.getLocation().getBlockX() + worldLocation.getBlockX()) >= border)
						|| (Math.abs(block.getLocation().getBlockZ() + worldLocation.getBlockZ()) >= border)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			if (!(event.getEntity() instanceof Player)) {
				event.setCancelled(true);
				return;
			}

			Gamer gamer = getManager().getGamerManager().getGamer((Player) event.getEntity());
			if (!gamer.isOnPvpPregame()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player)) {
				event.setCancelled(true);
				return;
			}

			Gamer gamer = getManager().getGamerManager().getGamer((Player) event.getDamager());
			if (!gamer.isOnPvpPregame()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
			if (!gamer.isOnPvpPregame()) {
				event.setCancelled(true);
			}
		} else {
			if (!ServerOptions.DROPS.isActive()) {
				if (!event.getPlayer().hasPermission("commons.cmd.build")) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
			if (!gamer.isOnPvpPregame()) {
				event.setCancelled(true);
			}
			if (event.getPlayer().getItemInHand().getType().equals(Material.STONE_SWORD)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player)
			if (getManager().getGameManager().isPreGame())
				event.setCancelled(true);
	}

	@EventHandler
	public void onSecond(ServerTimeEvent event) {
		if (!getManager().getGameManager().isPreGame()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				int border = 500;
				Location location = player.getLocation();

				if (player.getLocation().getWorld() == Bukkit.getWorld("world")) {
					Location worldLocation = location.getWorld().getSpawnLocation();
					if ((Math.abs(location.getBlockX() + worldLocation.getBlockX()) >= border + 1)
							|| (Math.abs(location.getBlockZ() + worldLocation.getBlockZ()) >= border + 1)) {
						if (getManager().getGamerManager().getGamer(player).isAlive()) {
							if (getManager().getGamerManager().getGamer(player).getKit()
									.equals(getManager().getKitManager().getKit("fireman"))) {
								player.damage(2.0, player);
							}

							player.setFireTicks(50);
							player.sendMessage(
									"§4§lBORDA §fVocê §c§lULTRAPASSOU§f a borda do §6§lMUNDO§f! Volte para o §e§lSPAWN§f!");
							worldLocation.getWorld().strikeLightning(player.getLocation());
						} else {
							player.teleport(worldLocation);
							return;
						}
					}

					int value = getManager().getGameManager().getBorderTime();

					if (location.getBlockX() > worldLocation.getBlockX() + value
							|| location.getBlockX() < -(value - worldLocation.getBlockX())
							|| location.getBlockZ() > worldLocation.getBlockZ() + value
							|| location.getBlockZ() < -(value - worldLocation.getBlockZ())) {
						if (getManager().getGameManager().isPreGame()) {
							getManager().getGamerManager().teleportSpawn(player);
							return;
						}
					}
				}
			}
		}
	}

}
