package br.com.zenix.hungergames.manager;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.plugin.data.management.DataManager;
import br.com.zenix.core.plugin.logger.Logger;
import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.server.Variables;
import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.HungerGames;
import br.com.zenix.hungergames.game.arena.ArenaManager;
import br.com.zenix.hungergames.game.cup.CupManager;
import br.com.zenix.hungergames.game.cup.position.GamerPositionManager;
import br.com.zenix.hungergames.game.handler.GameManager;
import br.com.zenix.hungergames.game.handler.listeners.SoupListener;
import br.com.zenix.hungergames.manager.managers.ClassManager;
import br.com.zenix.hungergames.manager.managers.FileManager;
import br.com.zenix.hungergames.player.admin.AdminManager;
import br.com.zenix.hungergames.player.admin.Vanish;
import br.com.zenix.hungergames.player.gamer.GamerManager;
import br.com.zenix.hungergames.player.inventories.InventoryManager;
import br.com.zenix.hungergames.player.kit.KitManager;
import br.com.zenix.hungergames.player.kitaward.AwardManager;
import br.com.zenix.hungergames.player.scoreboard.ScoreboardManager;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Manager {

	private final CoreManager coreManager;

	private HungerGames plugin;
	private Utils utils;

	private FileManager fileManager;

	private GamerManager gamerManager;
	private GameManager gameManager;

	private KitManager kitManager;

	private InventoryManager inventoryManager;
	private ScoreboardManager scoreboardManager;
	private CupManager cupManager;
	private AwardManager awardManager;
	private boolean doubleKit;

	private ArenaManager arenaManager;
	private AdminManager adminManager;

	private GamerPositionManager gamerPositionManager;

	private ClassManager classManager;

	private Random random;
	private Vanish vanish;

	private int time;

	public Manager(Core core) {
		this.coreManager = Core.getCoreManager();

		plugin = HungerGames.getPlugin(HungerGames.class);
		plugin.saveDefaultConfig();

		getPlugin().loadChunks();

		getLogger().log(
				"Starting the plugin " + plugin.getName() + " version " + plugin.getDescription().getVersion() + "...");
		if (!plugin.TEST_SERVER()) {
			getLogger().log("Starting to loading all the chunks of the world.");

			getLogger().log("The chunks that will be used, was loaded.");
		}

		if (getCoreManager().getServerName().startsWith("DK-")) {
			doubleKit = true;
		} else {
			doubleKit = false;
		}

		getLogger().log("Making connection with plugin " + coreManager.getPlugin().getName() + " version "
				+ coreManager.getPlugin().getDescription().getVersion() + ".");

		utils = coreManager.getUtils();

		random = new Random();

		fileManager = new FileManager(this);
		if (!fileManager.correctlyStart()) {
			return;
		}
		
		gamerPositionManager = new GamerPositionManager(this);
		if (!fileManager.correctlyStart()) {
			return;
		}

		cupManager = new CupManager(this);
		if (!cupManager.correctlyStart()) {
			return;
		}

		kitManager = new KitManager(this);
		if (!kitManager.correctlyStart()) {
			return;
		}

		scoreboardManager = new ScoreboardManager(this);
		if (!scoreboardManager.correctlyStart()) {
			return;
		}

		gamerManager = new GamerManager(this);
		if (!gamerManager.correctlyStart()) {
			return;
		}

		gameManager = new GameManager(this);
		if (!gameManager.correctlyStart()) {
			return;
		}

		awardManager = new AwardManager(this);
		if (!awardManager.correctlyStart()) {
			return;
		}

		arenaManager = new ArenaManager(this);
		if (!arenaManager.correctlyStart()) {
			return;
		}

		inventoryManager = new InventoryManager(this);
		if (!inventoryManager.correctlyStart()) {
			return;
		}

		adminManager = new AdminManager(this);
		if (!adminManager.correctlyStart()) {
			return;
		}

		vanish = new Vanish(this);

		classManager = new ClassManager(this);
		if (!classManager.correctlyStart()) {
			return;
		}

		SoupListener.createSoups();
		getPlugin().getServer().setWhitelist(false);

		if (getCoreManager().getServerName().startsWith("EVENTO")) {
			getPlugin().getServer().setWhitelist(true);
			Variables.EVENT = true;
		}

		Random random = new Random();
		int rnd = random.nextInt(600);

		time = 3600 + rnd;

		new BukkitRunnable() {
			public void run() {
				if (time <= 0) {
					if (getGameManager().isPreGame()) {
						if (Bukkit.getOnlinePlayers().size() == 0) {

							Bukkit.shutdown();
							cancel();
							return;
						}
					}
				}

				time--;
			}
		}.runTaskTimer(getPlugin(), 0L, 20L);
		getLogger().log("The plugin " + plugin.getName() + " version " + plugin.getDescription().getVersion()
				+ " was started correcly.");
	}

	public void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, getPlugin());
	}
	
	public GamerPositionManager getGamerPositionManager() {
		return gamerPositionManager;
	}

	public boolean isDoubleKit() {
		return doubleKit;
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}

	public AwardManager getSurpriseKitManager() {
		return awardManager;
	}

	public Vanish getVanish() {
		return vanish;
	}

	public Logger getLogger() {
		return getCoreManager().getLogger();
	}

	public CupManager getCupManager() {
		return cupManager;
	}

	public ServerType getServerType() {
		return coreManager.getServerType();
	}

	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}

	public AdminManager getAdminManager() {
		return adminManager;
	}

	public HungerGames getPlugin() {
		return plugin;
	}

	public Utils getUtils() {
		return utils;
	}

	public ScoreboardManager getScoreListener() {
		return scoreboardManager;
	}

	public CoreManager getCoreManager() {
		return coreManager;
	}

	public DataManager getMySQLManager() {
		return coreManager.getDataManager();
	}

	public GamerManager getGamerManager() {
		return gamerManager;
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public KitManager getKitManager() {
		return kitManager;
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public Random getRandom() {
		return random;
	}

	public ClassManager getClassLoader() {
		return classManager;
	}

	public FileManager getFileManager() {
		return fileManager;
	}
}
