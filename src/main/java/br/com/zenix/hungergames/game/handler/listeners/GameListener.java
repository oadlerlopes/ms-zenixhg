package br.com.zenix.hungergames.game.handler.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.spigot.bo2.BO2Constructor.FutureBlock;
import br.com.zenix.core.spigot.commands.base.MessagesConstructor;
import br.com.zenix.core.spigot.player.events.PlayerAdminEvent;
import br.com.zenix.core.spigot.player.events.PlayerChatCoreEvent;
import br.com.zenix.core.spigot.player.events.PlayerTellCoreEvent;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;
import br.com.zenix.core.spigot.server.Variables;
import br.com.zenix.hungergames.game.custom.HungerListener;
import br.com.zenix.hungergames.game.handler.item.CacheItems;
import br.com.zenix.hungergames.game.stage.GameStage;
import br.com.zenix.hungergames.player.gamer.Gamer;
import br.com.zenix.hungergames.player.gamer.GamerMode;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GameListener extends HungerListener {

	@EventHandler
	public void onTell(PlayerTellCoreEvent event) {
		Gamer tell = getManager().getGamerManager().getGamer(event.getTarget());

		if (!tell.isAlive())
			event.setCancelled(true);
	}

	@EventHandler
	public void onChat(PlayerChatCoreEvent event) {
		if (event.isCancelled())
			return;

		Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());

		if (gamer.isSpectating()) {
			for (Player players : Bukkit.getOnlinePlayers()) {
				if (!gamer.isAlive() && !event.getPlayer().hasPermission("hunger.chat")) {
					event.setCancelled(true);
					if (!getManager().getGamerManager().getGamer(players).isAlive()) {
						players.sendMessage("§7[SPECTATE] "
								+ getManager().getCoreManager().getTagManager().getDisplayName(event.getPlayer())
								+ "§f: " + event.getMessage());
					}
				}
			}
		}
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			player.setSaturation(3.0F);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {

		event.setJoinMessage(null);
		Player player = event.getPlayer();

		Gamer gamer = getManager().getGamerManager().getGamer(player);

		PermissionAttachment attachment = player.addAttachment(getManager().getPlugin());

		if (gamer.getSurpriseKit() != null) {
			attachment.getPermissions().put("hgkit." + gamer.getSurpriseKit().getName().toLowerCase(), true);
		}

		if (Variables.EVENT == true) {
			for (PunishRecord punish : gamer.getAccount().getPunishRecords().values()) {
				if (punish.getType() == PunishType.TEMPBAN) {
					if (punish.getMotive().toLowerCase().contains("blacklist")) {
						player.kickPlayer("§c§lUm evento está acontecendo e você está blacklist dos eventos!");
					}
				}
			}
		}

		if (getManager().getGameManager().isPreGame()) {

			event.getPlayer().getInventory().clear();

			getManager().getGamerManager().givePreGameItems(player);
			player.setGameMode(GameMode.SURVIVAL);

			player.setHealth(20.0);
			player.setFoodLevel(20);
			player.getInventory().setArmorContents(null);
			player.setFireTicks(0);
			player.setFoodLevel(20);
			player.setFlying(false);
			player.setAllowFlight(false);
			player.setSaturation(3.2F);
			player.getActivePotionEffects().clear();

			player.sendMessage(" ");
			player.sendMessage("§6§lZENIXHG");
			player.sendMessage(" ");
			player.sendMessage("§fEscolha seu kit clicando no §e§lBAU§f da sua §e§lMAO");
			player.sendMessage(" ");
			player.sendMessage("§9§lTENHA UM BOM JOGO!");

			getManager().getGamerManager().teleportSpawn(player);
			getManager().getGamerManager().getGamer(player).setMode(GamerMode.ALIVE);

			if (player.hasPermission("hunger.cmd.admin")) {
				player.chat("/admin");
			}

			MessagesConstructor.sendTitleMessage(player, "§f§lZenix§cHG", "§7Seja o último player de pé");

		} else {

			if (gamer.getMode() == GamerMode.DEAD) {
				if (!player.hasPermission("hunger.addon.spec")) {
					player.kickPlayer("\n§6§lTORNEIO §Você morreu!\n§6www.zenix.cc");
					return;
				}
			}

			gamer.setOnline(true);

			if (getManager().getGamerManager().getAFKGamers().contains(gamer)) {
				return;
			}

			if (!gamer.isAlive()) {
				if (gamer.getMode() == GamerMode.LOADING) {
					if (!getManager().getCupManager().isCup()) {
						if (gamer.getPlayer().hasPermission("hunger.addons.respawn")
								&& getManager().getGameManager().getGameTime() <= 300) {
							gamer.getPlayer().setHealth(20.0);
							gamer.getPlayer().setFoodLevel(20);
							gamer.getPlayer().getInventory().clear();
							gamer.getPlayer().getActivePotionEffects().clear();
							gamer.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
							gamer.getPlayer().setFireTicks(0);
							gamer.getPlayer().setFoodLevel(20);
							gamer.getPlayer().setFlying(false);
							gamer.getPlayer().setAllowFlight(false);
							gamer.getPlayer().setSaturation(3.2F);
							gamer.getPlayer()
									.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
							gamer.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));

							getManager().getGamerManager().setRespawn(gamer);
							Bukkit.broadcastMessage("§e" + gamer.getNick() + " entrou no servidor");
						} else {
							event.setJoinMessage(null);
							getManager().getGamerManager().setSpectator(gamer);
						}

					} else {
						getManager().getGamerManager().setSpectator(gamer);
					}
				} else {
					if (gamer.isAlive()) {
						Bukkit.broadcastMessage("§e" + gamer.getNick() + " entrou no servidor");
					} else {
						getManager().getGamerManager().setSpectator(gamer);
					}
				}
			}

		}
		getManager().getGamerManager().hideSpecs(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = getManager().getGamerManager().getGamer(player);

		if (getManager().getGamerManager().isEndgame()) {
			event.disallow(Result.KICK_OTHER,
					"\n§6§lTORNEIO §fA partida acabou!\n§fO servidor está reiniciando!!\n§6www.zenix.cc");
		}
		if (!getManager().getGamerManager().getAFKGamers().contains(gamer)) {
			if (getManager().getGameManager().isInvencibility()
					|| (getManager().getGameManager().isGame() && getManager().getGameManager().getGameTime() <= 300))
				if (!player.hasPermission("hunger.addon.respawn") && !player.hasPermission("hunger.addon.spec"))
					event.disallow(Result.KICK_OTHER,
							"\n§6§lTORNEIO §fO torneio já iniciou!\n§fAdquira §e§lVIP§f para poder entrar após o inicio!\n§6www.zenix.cc");

			if (getManager().getGameManager().isGame())
				if (!player.hasPermission("hunger.addon.spec"))
					event.disallow(Result.KICK_OTHER,
							"\n§6§lTORNEIO §fO torneio já iniciou!\n§fAdquira §e§lVIP§f para poder espectar!\n§6www.zenix.cc");
		}
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.NETHER_PORTAL || event.getCause() == TeleportCause.END_PORTAL)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());

		if (getManager().getGameManager().isGame()) {
			if (gamer.isAlive()) {
				if (gamer.isFighting()) {
					List<ItemStack> drop = new ArrayList<>();
					drop.addAll(Arrays.asList(event.getPlayer().getInventory().getContents()));
					drop.addAll(Arrays.asList(event.getPlayer().getInventory().getArmorContents()));

					for (ItemStack i : drop) {
						if (i == null)
							continue;

						if (i.getType() != Material.AIR)
							if (getManager().getKitManager().isItemKit(i))
								event.getPlayer().getWorld()
										.dropItemNaturally(event.getPlayer().getLocation().clone().add(0, 0.5, 0), i);

					}

					gamer.setMode(GamerMode.DEAD);
					getManager().getGamerManager().setDied(gamer);

					Bukkit.broadcastMessage("§e" + gamer.getNick() + " saiu do servidor");
					Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + "[" + gamer.getKit().getName()
							+ "] deslogou em combate. §4[" + getManager().getGamerManager().getAlivePlayers().size()
							+ "]");

				} else {
					Bukkit.broadcastMessage("§e" + gamer.getNick() + " saiu do servidor");
					getManager().getGamerManager().getAFKGamers().add(gamer);
					gamer.setOnline(false);

					new BukkitRunnable() {
						public void run() {
							if (!gamer.isOnline() && gamer.isAlive()) {
								getManager().getGamerManager().getAFKGamers().remove(gamer);
								getManager().getGamerManager().setDied(gamer);
								getManager().getGamerManager().checkWinner();

								Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + "["
										+ gamer.getKit().getName() + "] deslogou e não voltou. §4["
										+ getManager().getGamerManager().getAlivePlayers().size() + "]");
							}
						}
					}.runTaskLater(getManager().getPlugin(), 20L * 300);
				}
			}
		} else if (!getManager().getGameManager().isPreGame()) {
			if (gamer.isAlive()) {
				getManager().getGamerManager().getAFKGamers().add(gamer);
				gamer.setOnline(false);

				new BukkitRunnable() {
					public void run() {
						if (!gamer.isOnline()) {
							Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + "[" + gamer.getKit().getName()
									+ "] deslogou e não voltou. §4["
									+ getManager().getGamerManager().getAlivePlayers().size() + "]");
							getManager().getGamerManager().getAFKGamers().remove(gamer);
							getManager().getGamerManager().setDied(gamer);
							getManager().getGamerManager().checkWinner();
						} else {
							getManager().getGamerManager().getAFKGamers().remove(gamer);
						}
					}
				}.runTaskLater(getManager().getPlugin(), 20L * 300);
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (getManager().getKitManager().isItemKit(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§e§lDROP §fVocê não pode §6§lDROPAR§f este item no chão!");
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (getManager().getKitManager().isItemKit(event.getItemInHand())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§e§lDROP §fVocê não pode §6§lCOLOCAR§f este item no chão!");
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		if (event.getLeaveMessage().toLowerCase().contains("you logged in from another location")) {
			event.setCancelled(true);
		} else if (event.getLeaveMessage().toLowerCase().contains("flying is not enabled on this server")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {

		int border = getManager().getGameManager().getTimer().getBorderSize();

		Iterator<Block> blocks = event.blockList().iterator();
		while (blocks.hasNext()) {
			Block block = blocks.next();
			if (block.getLocation().getBlockX() == border || block.getLocation().getBlockZ() == border
					|| block.getLocation().getBlockX() == -border || block.getLocation().getBlockZ() == -border) {
				blocks.remove();
			}
			for (FutureBlock futureBlock : getManager().getGameManager().getTimer().getFinalArena().getBlocks()) {
				if (futureBlock.getLocation().equals(block.getLocation())) {
					blocks.remove();
				}
			}
		}
	}

	@EventHandler
	public void onBurn(BlockBurnEvent event) {
		for (FutureBlock futureBlock : getManager().getGameManager().getTimer().getFinalArena().getBlocks()) {
			if (futureBlock.getLocation().equals(event.getBlock().getLocation())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBreakBlock(BlockDamageEvent event) {
		int border = getManager().getGameManager().getTimer().getBorderSize();
		Block block = event.getBlock();
		if (block.getLocation().getBlockX() == border || block.getLocation().getBlockZ() == border
				|| block.getLocation().getBlockX() == -border || block.getLocation().getBlockZ() == -border) {
			block.setType(Material.GLASS);
		}
	}

	@EventHandler
	public void onPlayerAdminEvent(PlayerAdminEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = getManager().getGamerManager().getGamer(player);
		if (getManager().getGameManager().isPreGame()) {
			if (!event.isJoin()) {
				player.updateInventory();

				gamer.setMode(GamerMode.ALIVE);
				gamer.setItemsGive(false);
				getManager().getScoreListener().createScoreboard(player);
			} else {
				player.updateInventory();

				getManager().getGamerManager().setDied(gamer);
			}
		} else {
			if (gamer.isAlive()) {
				getManager().getGamerManager().setDied(gamer);
				getManager().getGamerManager().checkWinner();

				Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + "[" + gamer.getKit().getName()
						+ "] desistiu da partida. §4[" + getManager().getGamerManager().getAlivePlayers().size() + "]");
			}

			CacheItems.SPEC.build(player);

			player.updateInventory();

		}
	}

	@EventHandler
	public void aoMap(MapInitializeEvent event) {
		if (getManager().getGameManager().getGameStage() == GameStage.WINNING) {
			event.getMap().getRenderers().clear();
			for (MapRenderer r : event.getMap().getRenderers()) {
				event.getMap().removeRenderer(r);
			}
			event.getMap().addRenderer(new MapRenderer() {
				public void render(MapView mapa, MapCanvas canvas, Player player) {
					if (!getManager().getCupManager().isCup()) {
						canvas.drawText(17, 10, MinecraftFont.Font, "Parabéns, " + player.getName());
						canvas.drawText(9, 20, MinecraftFont.Font, "Você ganhou o torneio!");
					} else {
						canvas.drawText(17, 10, MinecraftFont.Font, "Parabéns, " + player.getName());
						canvas.drawText(9, 20, MinecraftFont.Font, "Você foi classificado!");
					}
					canvas.drawImage(30, 60,
							new ImageIcon(getManager().getFileManager().getServerImageFile().getPath()).getImage());

				}
			});
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBrakBlock(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()
				&& !event.getPlayer().hasPermission("hunger.staff")) {
			event.setCancelled(true);
			return;
		}

		for (FutureBlock futureBlock : getManager().getGameManager().getTimer().getFinalArena().getBlocks()) {
			if (futureBlock.getLocation().equals(event.getBlock().getLocation())) {
				event.setCancelled(true);
			}
		}

		if (event.getBlock().getLocation().getY() > 128)
			event.setCancelled(true);

		int border = getManager().getGameManager().getTimer().getBorderSize();

		Block block = event.getBlock();

		if (!getManager().getGameManager().isPreGame()) {
			if (block.getType() != Material.BEDROCK) {

				Player player = event.getPlayer();
				ArrayList<ItemStack> items = new ArrayList<>(event.getBlock().getDrops());

				if (player.getInventory().firstEmpty() != -1) {
					for (ItemStack item : items) {
						float totalExp = player.getExp();
						int newExp = (int) (totalExp + event.getExpToDrop());

						// player.setLevel(0);
						// player.setExp(0);

						player.giveExp(newExp);
						player.getInventory().addItem(item);
					}

					event.setCancelled(true);

					if (event.getBlock().getType().equals(Material.getMaterial(115))
							|| event.getBlock().getType() == (Material.getMaterial(115))) {
						player.getInventory().addItem(new ItemStack(Material.NETHER_WARTS));
						player.getInventory().addItem(new ItemStack(event.getBlock().getType()));
						player.getInventory().addItem(new ItemStack(event.getBlock().getTypeId()));
						player.getInventory().addItem(new ItemStack(115));
						player.getInventory().addItem(new ItemStack(372));
					}
					event.getBlock().setType(Material.AIR);

				} else {
					for (ItemStack item : event.getBlock().getDrops()) {

						for (int i = 0; i < 35; i++) {
							if (player.getInventory().getItem(i).getAmount() + item.getAmount() <= 64) {
								if (player.getInventory().getItem(i).getType().equals(item.getType())) {
									player.getInventory().addItem(item);

									event.setCancelled(true);
									event.getBlock().setType(Material.AIR);
									break;
								}
							}
						}
					}
				}
			}
		}

		if (block.getLocation().getBlockX() == border || block.getLocation().getBlockZ() == border
				|| block.getLocation().getBlockX() == -border || block.getLocation().getBlockZ() == -border) {
			event.setCancelled(true);
		}

		if (getManager().getGameManager().getTimer().getFeast() != null) {
			for (FutureBlock ftb : getManager().getGameManager().getTimer().getFeast().getBlocks()) {
				if (block.getLocation().getBlockX() == ftb.getLocation().getBlockX()
						&& block.getLocation().getBlockY() == ftb.getLocation().getBlockY()
						&& block.getLocation().getBlockZ() == ftb.getLocation().getBlockZ()) {
					event.setCancelled(true);
				}
			}

			if (getManager().getGameManager().getTimer().getFeast().getBlockData().contains(block.getLocation())) {
				event.setCancelled(true);
			}
		}
	}

}
