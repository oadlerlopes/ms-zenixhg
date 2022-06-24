package br.com.zenix.hungergames.game.handler;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.zenix.core.proxy.server.ServerStatus;
import br.com.zenix.core.spigot.commands.base.MessagesConstructor;
import br.com.zenix.core.spigot.player.events.ServerTimeEvent;
import br.com.zenix.core.spigot.server.Variables;
import br.com.zenix.hungergames.game.stage.GameStage;
import br.com.zenix.hungergames.game.structures.types.BonusFeast;
import br.com.zenix.hungergames.game.structures.types.Feast;
import br.com.zenix.hungergames.game.structures.types.FinalArena;
import br.com.zenix.hungergames.game.structures.types.MiniFeast;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.manager.constructor.Management;
import br.com.zenix.hungergames.manager.managers.FileManager;
import br.com.zenix.hungergames.player.gamer.Gamer;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Timer extends Management implements Listener {

	protected Boolean star;

	private Feast feast;
	private BonusFeast bonusFeast;
	private FinalArena finalArena;
	private boolean start;
	private boolean event;
	private boolean prizeEvent;
	public boolean startg;

	public Timer(Manager manager) {
		super(manager);
	}

	public boolean initialize() {

		this.finalArena = new FinalArena(getManager(), new Location(FileManager.getWorld(), 0, 10, 0));
		this.feast = null;
		this.bonusFeast = null;
		this.start = false;
		this.event = false;
		this.prizeEvent = false;

		borderSpawn(500);

		FileManager.getWorld().setSpawnLocation(0, FileManager.getWorld().getHighestBlockYAt(0, 0) + 15, 0);
		return true;
	}

	public boolean isEvent() {
		return event;
	}

	public void setEvent(boolean event) {
		this.event = event;
	}

	public boolean isPrizeEvent() {
		return prizeEvent;
	}

	public void setPrizeEvent(boolean prizeEvent) {
		this.prizeEvent = prizeEvent;
	}

	public boolean borderSpawn(int borderSize) {
		for (int y = 0; y <= 200; y++) {
			for (int x = -borderSize; x <= borderSize; x++) {
				Location loc = new Location(Bukkit.getWorld("world"), x, y, borderSize);
				if (!loc.getChunk().isLoaded())
					loc.getChunk().load(true);
				getManager().getCoreManager().getBO2().setBlockFast(loc, getBorderMaterial(), (byte) 0);
			}
			for (int x = -borderSize; x <= borderSize; x++) {
				Location loc = new Location(Bukkit.getWorld("world"), x, y, -borderSize);
				if (!loc.getChunk().isLoaded())
					loc.getChunk().load(true);
				getManager().getCoreManager().getBO2().setBlockFast(loc, getBorderMaterial(), (byte) 0);
			}
			for (int z = -borderSize; z <= borderSize; z++) {
				Location loc = new Location(Bukkit.getWorld("world"), borderSize, y, z);
				if (!loc.getChunk().isLoaded())
					loc.getChunk().load(true);
				getManager().getCoreManager().getBO2().setBlockFast(loc, getBorderMaterial(), (byte) 0);
			}
			for (int z = -borderSize; z <= borderSize; z++) {
				Location loc = new Location(Bukkit.getWorld("world"), -borderSize, y, z);
				if (!loc.getChunk().isLoaded())
					loc.getChunk().load(true);
				getManager().getCoreManager().getBO2().setBlockFast(loc, getBorderMaterial(), (byte) 0);
			}
		}
		return true;
	}

	public Material getBorderMaterial() {
		if (getManager().getRandom().nextBoolean())
			return Material.GLASS;
		if (getManager().getRandom().nextBoolean())
			return Material.QUARTZ_BLOCK;
		return Material.GLASS;
	}

	@EventHandler
	public void onTimer(ServerTimeEvent event) {
		if (getManager().getGameManager().isPreGame()) {

			Bukkit.getWorlds().get(0).setTime(0);
			
			getManager().getCoreManager().setServerStatus(ServerStatus.PREGAME, "Aguardando players.");

			getManager().getCoreManager().setServerStatus(ServerStatus.PREGAME,
					(Variables.EVENT == true
							? "" + (isEvent() ? "ALPHA-1" : "") + "O jogo começa em "
									+ getManager().getUtils().formatTime(getTime())
							: "O jogo começa em " + getManager().getUtils().formatTime(getTime())));

			if (Bukkit.getOnlinePlayers().size() <= 3 && this.start == false) {
				return;
			} else {
				this.start = true;
			}

			getManager().getGameManager().setGameTime(getTime() - 1);

			if (getTime() > 40 && Bukkit.getOnlinePlayers().size() >= 80) {
				if (startg == false) {
					startg = true;

					getManager().getGameManager().setGameTime(30);
					Bukkit.broadcastMessage("§3§lTORNEIO §fO tempo foi reduzido pois a partida está lotada!");
				}
			}

			if (60 <= getBorderTime())
				getManager().getGameManager().setBorderTime(getBorderTime() - 5);

			if ((getTime() % 30 == 0 || getTime() % 60 == 0) && getTime() >= 30) {
				Bukkit.broadcastMessage(
						"§3§lTORNEIO §fO torneio iniciará em §c§l" + getManager().getUtils().formatTime(getTime()));
				playSound(Sound.CLICK);
			}

			if (getTime() % 5 == 0 && getTime() >= 10 && getTime() <= 20) {
				Bukkit.broadcastMessage(
						"§3§lTORNEIO §fO torneio iniciará em §c§l" + getManager().getUtils().formatTime(getTime()));
				playSound(Sound.CLICK);
			}

			if (getTime() % 1 == 0 && getTime() <= 5 && getTime() != 0) {
				Bukkit.broadcastMessage(
						"§3§lTORNEIO §fO torneio iniciará em §c§l" + getManager().getUtils().formatTime(getTime()));
				playSound(Sound.NOTE_PLING);
			}

			if (getTime() <= 0) {
				for (Player data : Bukkit.getOnlinePlayers()) {
					Gamer gamer = getManager().getGamerManager().getGamer(data);
					gamer.setBlockFunction(true);

					if (!data.hasPermission("hunger.cmd.vanish")) {
						data.closeInventory();
					}
				}
				startGame();

				for (Gamer gamer : getManager().getGamerManager().getGamers().values())
					gamer.getAccount().setScoreboardHandler(null);
			}

		} else if (getManager().getGameManager().isInvencibility()) {

			getManager().getCoreManager().setServerStatus(ServerStatus.INVENCIBILITY,
					(Variables.EVENT == true
							? "" + (isEvent() ? "ALPHA-1" : "") + "A invencibilidade acaba em "
									+ getManager().getUtils().formatTime(getTime())
							: "A invencibilidade acaba em " + getManager().getUtils().formatTime(getTime())));

			if ((getTime() % 30 == 0 || getTime() % 60 == 0) && getTime() >= 30
					&& getTime() != getManager().getConfig().getInt("game.invencibility")) {
				Bukkit.broadcastMessage(
						"§e§lINVENCIBILIDADE§f acabará em " + getManager().getUtils().formatTime(getTime()));
			}

			if (getTime() % 1 == 0 && getTime() <= 5 && getTime() != 0) {
				playSound(Sound.CLICK);
				Bukkit.broadcastMessage(
						"§e§lINVENCIBILIDADE§f acabará em " + getManager().getUtils().formatTime(getTime()));
			}

			getManager().getGameManager().setGameTime(getTime() - 1);

			for (Player players : Bukkit.getOnlinePlayers()) {
				MessagesConstructor.sendActionBarMessage(players, "§e§lINVENCIBILIDADE §c§l"
						+ getManager().getUtils().formatOldTime(getManager().getGameManager().getGameTime()));
			}

			if (getTime() <= 0) {
				Bukkit.broadcastMessage("§e§lINVENCIBILIDADE§c§l ACABOU");
				getManager().getGameManager().setGameStage(GameStage.GAME);

				for (Gamer gamer : getManager().getGamerManager().getGamers().values())
					gamer.getAccount().setScoreboardHandler(null);

				if (!getManager().getCupManager().isCup()) {
					getManager().getGameManager().setGameTime(120);
				} else {
					getManager().getGameManager().setGameTime(600);
				}
			}

		} else if (getManager().getGameManager().isGame()) {

			getManager().getCoreManager().setServerStatus(ServerStatus.GAME,
					(Variables.EVENT == true
							? "" + (isEvent() ? "ALPHA-1" : "") + "O jogo está em andamento há "
									+ getManager().getUtils().formatTime(getTime())
							: "O jogo está em andamento há " + getManager().getUtils().formatTime(getTime())));

			getManager().getGameManager().setGameTime(getTime() + 1);
			getManager().getGamerManager().checkWinner();

			if (getTime() % 300 == 0) {
				new MiniFeast(getManager(), 10).spawnMiniFeast();
			}

			if (getTime() == 600) {
				feast = new Feast(getManager(), 15);
				feast.spawnFeast();
			}

			if (getTime() == 1320) {
				bonusFeast = new BonusFeast(getManager(), 15);
				bonusFeast.spawnFeast();
			}

			if (getTime() == 2400) {
				finalArena.cleanPlayer();

				Location loc = finalArena.getLocation();

				for (Gamer gamers : getManager().getGamerManager().getAliveGamers()) {
					gamers.getPlayer().teleport(loc);
				}
			}

		}
	}

	public void startGame() {
		getManager().getKitManager().registerKits();

		getManager().getGameManager().setGameStage(GameStage.INVENCIBILITY);
		if (!getManager().getCupManager().isCup()) {
			getManager().getGameManager().setGameTime(getManager().getConfig().getInt("game.invencibility"));
		} else {
			getManager().getGameManager().setGameTime(840);
		}

		Bukkit.broadcastMessage("§cO torneio iniciou!");
		Bukkit.broadcastMessage("§cTodos estão invencíveis por " + (!getManager().getCupManager().isCup()
				? getManager().getUtils().formatTime(getManager().getConfig().getInt("game.invencibility"))
				: "10 minutos"));
		Bukkit.broadcastMessage("§bQue a sorte esteja ao seu favor");

		playSound(Sound.ENDERDRAGON_GROWL);

		for (Player data : Bukkit.getOnlinePlayers()) {
			MessagesConstructor.sendTitleMessage(data, "§6§lPartida iniciou", "§fEntrando na INVENCIBILIDADE");
		}

		for (Gamer gamer : getManager().getGamerManager().getAliveGamers()) {

			if (!getManager().getAdminManager().isAdmin(gamer.getPlayer())) {
				startGamer(gamer);
			} else {
				gamer.setGameKills(0);
				getManager().getGamerManager().setDied(gamer);
				getManager().getGamerManager().checkWinner();
			}

			gamer.setGameKills(0);
			gamer.getAccount().setScoreboardHandler(null);
		}

		if (Variables.EVENT == true) {
			Twitter twitter = getManager().getCoreManager().getTwitterManager().getDefaultTwitter();

			List<Status> statusList = null;
			try {
				statusList = twitter.getHomeTimeline();
			} catch (TwitterException e) {
				e.printStackTrace();
			}

			Status bendidoStatus = null;

			for (Status statusl : statusList) {
				if (statusl.getText().contains("Evento")) {
					bendidoStatus = statusl;
				}

			}

			try {
				twitter.destroyStatus(bendidoStatus.getId());
			} catch (TwitterException e) {
				e.printStackTrace();
			}

		}
	}

	public void startGamer(Gamer gamer) {
		gamer.getPlayer().getInventory().clear();
		gamer.getPlayer().getActivePotionEffects().clear();
		gamer.getPlayer().getInventory().setArmorContents(null);
		gamer.getPlayer().setFireTicks(0);
		gamer.getPlayer().setFoodLevel(20);
		gamer.getPlayer().setFlying(false);
		gamer.getPlayer().setAllowFlight(false);
		gamer.getPlayer().setSaturation(3.2F);
		gamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
		gamer.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
		gamer.getKit().give(gamer.getPlayer());
		gamer.setGameKills(0);
		gamer.setItemsGive(true);
		gamer.getPlayer().updateInventory();
	}

	public Feast getFeast() {
		return feast;
	}

	public FinalArena getFinalArena() {
		return finalArena;
	}

	public int getBorderSize() {
		return 500;
	}

	public Integer getTime() {
		return getManager().getGameManager().getGameTime();
	}

	public Integer getBorderTime() {
		return getManager().getGameManager().getBorderTime();
	}

	public void playSound(Sound sound) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player.getLocation(), sound, 5.0F, 5.0F);
		}
	}
}
