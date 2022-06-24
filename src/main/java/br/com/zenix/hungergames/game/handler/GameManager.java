package br.com.zenix.hungergames.game.handler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;

import br.com.zenix.core.spigot.player.hologram.Hologram;
import br.com.zenix.hungergames.game.stage.GameStage;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.manager.constructor.Management;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GameManager extends Management {

	private static final List<Hologram> holograms = new ArrayList<>();

	private GameStage gameStage;
	private Timer timer;

	private boolean vipsCmds, isEnded;
	private int gameTime, borderTime;

	public GameManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {

		timer = new Timer(getManager());
		if (!timer.correctlyStart())
			return false;

		gameTime = getManager().getConfig().getInt("game.start");
		borderTime = 300;
		gameStage = GameStage.PREGAME;

		vipsCmds = true;
		isEnded = false;

		getManager().registerListener(timer);

		World world = Bukkit.getWorld("world");
		world.setDifficulty(Difficulty.NORMAL);
		
		if (world.hasStorm()) {
			world.setStorm(false);
		}
		
		world.setTime(0);

		return true;

	}

	public int getBorderTime() {
		return borderTime;
	}

	public void setBorderTime(int borderTime) {
		this.borderTime = borderTime;
	}

	public void setGameStage(GameStage gameStage) {
		getLogger().log("The stage of the game is changing of " + this.gameStage + " to " + gameStage + ".");
		this.gameStage = gameStage;
	}

	public void setGameTime(Integer gameTime) {
		this.gameTime = gameTime;
	}

	public boolean isPreGame() {
		return gameStage == GameStage.PREGAME;
	}

	public boolean isInvencibility() {
		return gameStage == GameStage.INVENCIBILITY;
	}

	public boolean isGame() {
		return gameStage == GameStage.GAME;
	}

	public Integer getGameTime() {
		return gameTime;
	}

	public GameStage getGameStage() {
		return gameStage;
	}

	public Timer getTimer() {
		return timer;
	}

	public Boolean isEnded() {
		return isEnded;
	}

	public void setEnded(Boolean isEnded) {
		this.isEnded = isEnded;
	}

	public Boolean isVipCmds() {
		return vipsCmds;
	}

	public void setVipsCmds(Boolean vipsCmds) {
		this.vipsCmds = vipsCmds;
	}

	public static List<Hologram> getHolograms() {
		return holograms;
	}

}
