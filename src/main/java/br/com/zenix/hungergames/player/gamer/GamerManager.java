package br.com.zenix.hungergames.player.gamer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.ProtocolInjector;

import br.com.zenix.core.plugin.data.handler.DataHandler;
import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.commands.base.MessagesConstructor;
import br.com.zenix.core.spigot.options.ServerOptions;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.item.ItemBuilder;
import br.com.zenix.core.spigot.server.Variables;
import br.com.zenix.hungergames.HungerGames;
import br.com.zenix.hungergames.game.handler.item.CacheItems;
import br.com.zenix.hungergames.game.stage.GameStage;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.manager.constructor.Management;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.PlayerConnection;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GamerManager extends Management {

	private static final HashMap<UUID, Gamer> gamers = new HashMap<>();
	private static final List<Gamer> afkGamers = new ArrayList<>();

	private boolean endGame = false;

	public GamerManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		return true;
	}

	public void addGamer(Gamer gamer) {
		gamers.put(gamer.getUUID(), gamer);
	}

	public boolean isEndgame() {
		return endGame;
	}

	public Gamer getGamer(UUID uuid) {
		return gamers.get(uuid);
	}

	public Gamer getGamer(Player player) {
		return gamers.get(player.getUniqueId());
	}

	public HashMap<UUID, Gamer> getGamers() {
		return gamers;
	}

	public List<Gamer> getAFKGamers() {
		return afkGamers;
	}

	public List<Gamer> getAliveGamers() {
		List<Gamer> gamers = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers())
			if (getGamer(player).isAlive())
				gamers.add(getGamer(player));
		return gamers;
	}

	public Collection<? extends Player> getAlivePlayers() {
		List<Player> gamers = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers())
			if (getGamer(player).isAlive())
				gamers.add(player);
		return gamers;
	}

	public void givePreGameItems(Player player) {
		player.getInventory().clear();

		if (getManager().isDoubleKit()) {

		} else {
			CacheItems.JOIN_ONEKIT.build(player);
		}

		player.updateInventory();
	}

	public void hideSpecs(Player player) {
		Gamer gamer = getManager().getGamerManager().getGamer(player);

		for (Player playerToHide : Bukkit.getOnlinePlayers()) {
			Gamer toHide = getManager().getGamerManager().getGamer(playerToHide);
			if (gamer.isSpecs()) {
				if (!toHide.isSpectating())
					continue;
				player.hidePlayer(playerToHide);

			}
		}

		for (Player players : Bukkit.getOnlinePlayers()) {
			if (!gamer.isSpecs()) {
				if (getManager().getGamerManager().getGamer(players).isSpectating()) {
					player.hidePlayer(players);
				}
			}
		}

		for (Player playerToHide : Bukkit.getOnlinePlayers()) {
			Gamer toHide = getManager().getGamerManager().getGamer(playerToHide);
			if (toHide.isSpecs()) {
				if (!toHide.isSpectating())
					continue;
				player.hidePlayer(playerToHide);
			}

		}
	}

	public void setSpectator(Gamer gamer) {
		resetKits(gamer);
		gamer.setMode(GamerMode.SPECTING);
		gamer.setItemsGive(true);

		for (Player players : Bukkit.getOnlinePlayers()) {
			if (getManager().getGamerManager().getGamer(players).isSpecs()) {
				players.hidePlayer(gamer.getPlayer());
			}
			players.hidePlayer(gamer.getPlayer());
		}

		final Player player = gamer.getPlayer();

		player.setAllowFlight(true);
		player.setFlying(true);

		for (Player playerToHide : Bukkit.getOnlinePlayers()) {
			Gamer toHide = getManager().getGamerManager().getGamer(playerToHide);
			if (gamer.isSpecs()) {
				if (!toHide.isSpectating())
					continue;
				player.hidePlayer(playerToHide);
			}
		}

		hideSpecs(player);

		new BukkitRunnable() {
			public void run() {
				player.setGameMode(GameMode.ADVENTURE);

				if (player.hasPermission("hunger.cmd.admin")) {
					player.chat("/admin");
				}

				player.closeInventory();
				player.getInventory().clear();
				player.getActivePotionEffects().clear();
				player.getInventory().setArmorContents(new ItemStack[4]);
				player.setAllowFlight(true);
				player.setFlying(true);

				CacheItems.SPEC.build(player);

				for (Player players : Bukkit.getOnlinePlayers()) {
					players.hidePlayer(gamer.getPlayer());
				}

				hideSpecs(player);

			}
		}.runTaskLater(getManager().getPlugin(), 2L);
	}

	public void setDied(Gamer gamer) {
		resetKits(gamer);
		gamer.setMode(GamerMode.DEAD);
		gamer.setItemsGive(true);
	}

	public void respawnPlayer(Gamer gamer) {
		gamer.getPlayer().setHealth(20.0D);
	}

	public void setRespawn(Gamer gamer) {
		gamer.setMode(GamerMode.ALIVE);
		new BukkitRunnable() {
			public void run() {
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
				gamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
				gamer.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));

				gamer.getKit().give(gamer.getPlayer());

				gamer.setItemsGive(true);
				teleportRandom(gamer.getPlayer());

			}
		}.runTaskLater(getManager().getPlugin(), 5L);
	}

	public void resetKits(Gamer gamer) {
		gamer.setKit(getManager().getKitManager().getKit("Nenhum"));
	}

	public void updateTab(Player player) {
		if (player == null)
			return;

		Gamer gamer = getManager().getGamerManager().getGamer(player);

		StringBuilder headerBuilder = new StringBuilder();
		int players = getManager().getGamerManager().getAliveGamers().size();
		int maxPlayers = Bukkit.getMaxPlayers();

		int ping = 0;
		ping = ((CraftPlayer) gamer.getAccount().getPlayer()).getHandle().ping;

		String tempo = getManager().getUtils().formatOldTime(getManager().getGameManager().getGameTime());
		headerBuilder.append("\n");
		if (getManager().getCupManager().isCup()) {
			headerBuilder.append("   §e" + tempo + " §6§l> §f" + "a" + "copa.zenix.cc" + " §6§l< §e" + players + "/"
					+ maxPlayers + " ");
		} else {
			headerBuilder.append("   §e" + tempo + " §6§l> §f" + getManager().getCoreManager().getServerIP()
					+ " §6§l< §e" + players + "/" + maxPlayers + " ");
		}
		headerBuilder.append("\n");
		headerBuilder.append("  §6Kit: §e" + gamer.getKit().getName() + " §1- §6Kills: §e" + gamer.getGameKills()
				+ " §1- §6Ping: §e" + ping + "  ");
		headerBuilder.append("\n");

		StringBuilder footerBuilder = new StringBuilder();
		footerBuilder.append(" \n ");
		footerBuilder.append("§bNick: §f" + gamer.getAccount().getPlayer().getName() + " §1- §bLiga: §f"
				+ gamer.getAccount().getLeague().getName().toUpperCase() + " §1- §bXP: §f"
				+ gamer.getAccount().getXp());
		footerBuilder.append(" \n ");
		footerBuilder.append("§bMais informações em §fwww.zenix.cc");
		footerBuilder.append(" \n ");

		getManager().getGamerManager().updateTab(player, headerBuilder.toString(), footerBuilder.toString());
	}

	public void updateTab(Player player, String up, String down) {
		if (((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 46) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new ProtocolInjector.PacketTabHeader(ChatSerializer.a("{'text': '" + up + "'}"),
					ChatSerializer.a("{'text': '" + down + "'}")));
		}
	}

	public void teleportRandom(Player player) {
		World world = player.getWorld();
		int x = getManager().getRandom().nextInt(200) + 100, z = getManager().getRandom().nextInt(200) + 100;
		if (getManager().getRandom().nextBoolean())
			x = -x;
		if (getManager().getRandom().nextBoolean())
			z = -z;
		player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z) + 7, z));
	}

	public void teleportSpawn(Player player) {
		World world = player.getWorld();
		int x = getManager().getRandom().nextInt(10) + 10, z = getManager().getRandom().nextInt(20) + 20;
		if (getManager().getRandom().nextBoolean())
			x = -x;
		if (getManager().getRandom().nextBoolean())
			z = -z;
		player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z) + 7, z));
	}

	public void updateGamer(Account account) {
		account.update();
	}

	public void updateGamer(Gamer gamer) {
		updateGamer(gamer.getAccount());
	}

	public void giveDamage(LivingEntity reciveDamage, Player giveDamage, double damage, boolean bool) {
		if (reciveDamage == null || reciveDamage.isDead() || giveDamage == null || giveDamage.isDead())
			return;

		if (bool) {
			if (reciveDamage.getHealth() < damage) {
				reciveDamage.setHealth(1.0D);
				giveDamage.setMetadata("custom",
						new FixedMetadataValue(HungerGames.getPlugin(HungerGames.class), null));
				reciveDamage.damage(6.0D, giveDamage);
			} else {
				reciveDamage.damage(damage);
			}
		} else {
			giveDamage.setMetadata("custom", new FixedMetadataValue(HungerGames.getPlugin(HungerGames.class), null));
			reciveDamage.damage(damage, giveDamage);
		}
	}

	public void checkWinner1() {
		if (getManager().getGameManager().isEnded()) {
			return;
		}
		if (getAliveGamers().size() == 1) {
			getManager().getGameManager().setEnded(true);
			Gamer gamer = getAliveGamers().get(0);

			if (gamer.getPlayer().isOnline()) {
				makeWin(gamer.getPlayer(), gamer);
			}

			Bukkit.getScheduler().runTaskTimer(getManager().getPlugin(), new Runnable() {
				int cancel = 15;

				public void run() {
					if (cancel == 0) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.kickPlayer("morreu");
						}
					}
					cancel--;
				}
			}, 0L, 20L);

			Bukkit.getScheduler().runTaskTimer(getManager().getPlugin(), new Runnable() {
				int cancel = 20;

				public void run() {
					if (cancel == 0) {
						getManager().getPlugin().handleStop();
					}
					cancel--;
				}
			}, 0L, 20L);

		} else if (getAliveGamers().size() == 0) {
			getManager().getPlugin().handleStop();

		}

	}

	public void checkWinner() {
		if (getManager().getGameManager().isEnded()) {
			return;
		}
		if (!getManager().getCupManager().isCup()) {
			if (getAliveGamers().size() == 1) {
				getManager().getGameManager().setEnded(true);
				Gamer gamer = getAliveGamers().get(0);

				if (gamer.getPlayer().isOnline()) {
					makeWin(gamer.getPlayer(), gamer);
				}

				Bukkit.getScheduler().runTaskTimer(getManager().getPlugin(), new Runnable() {
					int cancel = 15;

					public void run() {
						if (cancel == 0) {
							for (Player players : Bukkit.getOnlinePlayers()) {
								players.kickPlayer("morreu");
							}
						}
						cancel--;
					}
				}, 0L, 20L);

				Bukkit.getScheduler().runTaskTimer(getManager().getPlugin(), new Runnable() {
					int cancel = 20;

					public void run() {
						if (cancel == 0) {
							getManager().getPlugin().handleStop();
						}
						cancel--;
					}
				}, 0L, 20L);

			} else if (getAliveGamers().size() == 0) {
				getManager().getPlugin().handleStop();

			}
		} else {
			if (getAliveGamers().size() <= 10) {
				
				ServerOptions.PVP.setActive(false);
				ServerOptions.GLOBAL_PVP.setActive(false);

			} else if (getAliveGamers().size() == 0) {
				getManager().getPlugin().handleStop();

			}
		}
	}

	public void makeWin(Player player, Gamer gamer) {
		getManager().getGameManager().setGameStage(GameStage.WINNING);

		Location cake = player.getLocation().clone();
		cake.setY(156);
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				cake.clone().add(x, 0, z).getBlock().setType(Material.GLASS);
				cake.clone().add(x, 1, z).getBlock().setType(Material.CAKE_BLOCK);
			}
		}
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
		player.teleport(cake.clone().add(0, 4, 0));
		player.setGameMode(GameMode.CREATIVE);
		player.getInventory().setArmorContents(null);

		player.getInventory().setItem(0, new ItemBuilder(Material.MAP).setName("§b" + player.getName()).getStack());

		player.getInventory().setItem(1, new ItemBuilder(Material.WATER_BUCKET).setName("§bMlg").getStack());
		player.updateInventory();

		Account account = gamer.getAccount();
		DataHandler dataHandler = account.getDataHandler();

		dataHandler.getValue(DataType.GLOBAL_XP).setValue(dataHandler.getValue(DataType.GLOBAL_XP).getValue() + 30);
		dataHandler.getValue(DataType.GLOBAL_COINS)
				.setValue(dataHandler.getValue(DataType.GLOBAL_COINS).getValue() + 50);
		dataHandler.getValue(DataType.HG_WINS).setValue(dataHandler.getValue(DataType.HG_WINS).getValue() + 1);

		dataHandler.update(DataType.GLOBAL_XP);
		dataHandler.update(DataType.GLOBAL_COINS);
		dataHandler.update(DataType.HG_WINS);

		gamer.update();

		endGame = true;

		startFirework(player, player.getLocation(), getManager().getRandom());

		String tempo = getManager().getUtils().toTime(getManager().getGameManager().getGameTime());

		Bukkit.broadcastMessage(
				"§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-=");
		Bukkit.broadcastMessage("§6" + player.getName() + " ganhou"
				+ (getManager().getGameManager().getTimer().isEvent() ? " o evento" : "") + "!");
		Bukkit.broadcastMessage("§aMatou §2" + gamer.getGameKills() + "§a players com o kit §2"
				+ gamer.getKit().getName() + "§a em " + tempo);
		Bukkit.broadcastMessage(
				"§7§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-=");

		Bukkit.getWorld("world").setTime(13000);

		if (account.isHaveClan())
			Core.getCoreManager().getClanAccountManager().getClanAccount(gamer.getPlayer()).getClan()
					.giveWin(gamer.getPlayer());

		MessagesConstructor.sendTitleMessage(gamer.getPlayer(), "§6Parabéns!", "§7Você foi o ultimo sobrevivente!");

		if (Variables.EVENT == true && getManager().getGameManager().getTimer().isPrizeEvent()) {
			if (!gamer.getPlayer().hasPermission("commons.cmd.light")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"groupset " + gamer.getPlayer().getName() + " ultimate 1d");
			}
		}

	}

	public void makeWinCup(Player player, Gamer gamer) {
		getManager().getGameManager().setGameStage(GameStage.WINNING);

		player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
		player.teleport(player.getLocation().clone().add(0, 4, 0));
		player.setGameMode(GameMode.CREATIVE);
		player.getInventory().setArmorContents(null);

		player.getInventory().setItem(0, new ItemBuilder(Material.MAP).setName("§b" + player.getName()).getStack());

		player.getInventory().setItem(1, new ItemBuilder(Material.WATER_BUCKET).setName("§bMlg").getStack());
		player.updateInventory();

		Account account = gamer.getAccount();
		DataHandler dataHandler = account.getDataHandler();

		dataHandler.getValue(DataType.GLOBAL_XP).setValue(dataHandler.getValue(DataType.GLOBAL_XP).getValue() + 30);
		dataHandler.getValue(DataType.GLOBAL_COINS)
				.setValue(dataHandler.getValue(DataType.GLOBAL_COINS).getValue() + 50);
		dataHandler.getValue(DataType.HG_WINS).setValue(dataHandler.getValue(DataType.HG_WINS).getValue() + 1);

		dataHandler.update(DataType.GLOBAL_XP);
		dataHandler.update(DataType.GLOBAL_COINS);
		dataHandler.update(DataType.HG_WINS);

		gamer.update();

		endGame = true;

		Bukkit.getWorld("world").setTime(13000);

		MessagesConstructor.sendTitleMessage(gamer.getPlayer(), "§6Parabéns!", "§7Você está classificado!");

	}

	public void startFirework(final Player player, Location location, Random random) {
		for (int i = 0; i < 5; i++) {
			spawnRandomFirework(location.add(-10 + random.nextInt(20), 0.0D, -10 + random.nextInt(20)));
		}
		new BukkitRunnable() {
			public void run() {
				spawnRandomFirework(player.getLocation().add(-10.0D, 0.0D, -10.0D));
				spawnRandomFirework(player.getLocation().add(-10.0D, 0.0D, 10.0D));
				spawnRandomFirework(player.getLocation().add(10.0D, 0.0D, -10.0D));
				spawnRandomFirework(player.getLocation().add(10.0D, 0.0D, 10.0D));
				spawnRandomFirework(player.getLocation().add(-5.0D, 0.0D, -5.0D));
				spawnRandomFirework(player.getLocation().add(-5.0D, 0.0D, 5.0D));
				spawnRandomFirework(player.getLocation().add(5.0D, 0.0D, -5.0D));
				spawnRandomFirework(player.getLocation().add(5.0D, 0.0D, 5.0D));
				spawnRandomFirework(player.getLocation().add(-4.0D, 0.0D, -3.0D));
				spawnRandomFirework(player.getLocation().add(-3.0D, 0.0D, 4.0D));
				spawnRandomFirework(player.getLocation().add(2.0D, 0.0D, -6.0D));
				spawnRandomFirework(player.getLocation().add(1.0D, 0.0D, 9.0D));

			}
		}.runTaskTimer(getManager().getPlugin(), 10L, 30L);
	}

	public void spawnRandomFirework(Location location) {
		Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		FireworkMeta fwm = firework.getFireworkMeta();

		int rt = getManager().getRandom().nextInt(4) + 1;

		FireworkEffect.Type type = FireworkEffect.Type.BALL;
		if (rt == 1) {
			type = FireworkEffect.Type.BALL;
		} else if (rt == 2) {
			type = FireworkEffect.Type.BALL_LARGE;
		} else if (rt == 3) {
			type = FireworkEffect.Type.BURST;
		} else if (rt == 4) {
			type = FireworkEffect.Type.STAR;
		}

		FireworkEffect effect = FireworkEffect.builder().flicker(getManager().getRandom().nextBoolean())
				.withColor(Color.WHITE).withColor(Color.ORANGE).withFade(Color.FUCHSIA).with(type)
				.trail(getManager().getRandom().nextBoolean()).build();
		fwm.addEffect(effect);
		fwm.setPower(getManager().getRandom().nextInt(2) + 1);

		firework.setFireworkMeta(fwm);
	}

}
