package br.com.zenix.hungergames.player.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.scoreboard.ScoreboardConstructor;
import br.com.zenix.core.spigot.player.scoreboard.ScoreboardScroller;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.manager.constructor.Management;
import br.com.zenix.hungergames.player.gamer.Gamer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ScoreboardManager extends Management {

	private String title = "§6§lZENIX";
	private String name = "";
	private ScoreboardScroller scoreboardScroller;

	public ScoreboardManager(Manager manager) {
		super(manager, "Scoreboard");
	}

	public boolean initialize() {
		name = (getManager().getCupManager().isCup() ? "COPA HG" : "ZENIX HG");

		scoreboardScroller = new ScoreboardScroller("  " + name + "  ", "§e§l", "§6§l", "§6§l", 3);

		return startScores();
	}

	public boolean startScores() {

		new BukkitRunnable() {
			public void run() {
				if (Bukkit.getOnlinePlayers().size() == 0)
					return;

				title = "§f§l" + scoreboardScroller.next();

				for (Player player : Bukkit.getOnlinePlayers()) {
					updateScoreboard(player);

					getManager().getGamerManager().updateTab(player);
				}
			}
		}.runTaskTimer(getManager().getPlugin(), 2, 2);
		return true;
	}

	public void createScoreboard(Player player) {
		Account account = getManager().getCoreManager().getAccountManager().getAccount(player);

		ScoreboardConstructor scoreboardHandler = new ScoreboardConstructor(player);
		scoreboardHandler.initialize(" §6§l" + getManager().getCoreManager().getServerName() + " ");

		scoreboardHandler.setScore("§b§c", "§2§l", "§f");
		scoreboardHandler.setScore("§fRank ", "§f", "§f");
		scoreboardHandler.setScore("§fLiga ", "§f", "" + account.getLeague().getColor()
				+ account.getLeague().getSymbol() + " " + account.getLeague().getName().toUpperCase());

		if (getManager().getCupManager().isCup()) {
			scoreboardHandler.setScore("§fGrupo ", "§f", "§90");
			scoreboardHandler.setScore("§fPosição ", "§f", "§90");
		}
		
		scoreboardHandler.setScore("§6§f", "§1§l", "§f");

		if (getManager().getGameManager().isPreGame()) {
			scoreboardHandler.setScore("§fPartida ini", "§f", "ciando §f0");
		} else if (getManager().getGameManager().isInvencibility()) {
			scoreboardHandler.setScore("§fRestante", "§f", " §f0");
		} else if (getManager().getGameManager().isGame()) {
			scoreboardHandler.setScore("§fJogo", "§f", " §f0");
		}
		
		scoreboardHandler.setScore("§4§f", "§1§l", "§f");
		scoreboardHandler.setScore("§fJogadores", "§f", " §f0");

		scoreboardHandler.setScore("§4§f", "§1§l", "§f");

		scoreboardHandler.setScore("§fKills ", "§f", "§90");
		scoreboardHandler.setScore("§fKit", "§f", ": §bNenhum");

		if (getManager().isDoubleKit()) {
			scoreboardHandler.setScore("§fKit2", "§f", " §bNenhum");
		}

		scoreboardHandler.setScore("§d§c", "§2§l", "§f");

		scoreboardHandler.setScore("§ehg", "§e", "§e.zenix.cc");

		getManager().getGamerManager().getGamer(player).getAccount().setScoreboardHandler(scoreboardHandler);
	}

	public void updateScoreboard(Player player) {

		Gamer gamer = getManager().getGamerManager().getGamer(player);
		Account account = gamer.getAccount();

		if (account.getScoreboardHandler() == null) {
			createScoreboard(player);
		}

		ScoreboardConstructor scoreboardHandler = getManager().getGamerManager().getGamer(player).getAccount()
				.getScoreboardHandler();
		scoreboardHandler.setDisplayName(title);

		String time = getManager().getUtils().formatOldTime(getManager().getGameManager().getGameTime());

		if (getManager().getGameManager().isPreGame())
			scoreboardHandler.updateScore("§fPartida ini", "§f", "ciando §a" + time);
		else if (getManager().getGameManager().isInvencibility())
			scoreboardHandler.updateScore("§fRestante", "§f", " §a" + time);
		else if (getManager().getGameManager().isGame())
			scoreboardHandler.updateScore("§fJogo", "§f", " §a" + time);

		if (getManager().getCupManager().isCup()) {
			scoreboardHandler.updateScore("§fGrupo ", "§f",
					"§6" + getManager().getCupManager().getGroupStage().getName());
		}

		scoreboardHandler.updateScore("§fKills ", "§f", "§7" + gamer.getGameKills());
		scoreboardHandler.updateScore("§fRank ", "§f",
				"" + gamer.getAccount().getRank().getTag().getColor().replace("§l", "")
						+ getManager().getUtils().captalize(gamer.getAccount().getRank().getName()));
		scoreboardHandler.updateScore("§fJogadores", "§f",
				"§f §7" + getManager().getGamerManager().getAliveGamers().size());

		scoreboardHandler.updateScore("§fPosição ", "§f",
				"§6" + getManager().getGamerPositionManager().getPosition(player) + "º");

		scoreboardHandler.updateScore("§fKit", "§f", "§f §a" + gamer.getKit().getName());

		if (getManager().isDoubleKit()) {
			scoreboardHandler.updateScore("§fKit2", "§f", "§f §a" + gamer.getKit2().getName());
		}
	}

}
