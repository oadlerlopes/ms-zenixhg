package br.com.zenix.hungergames.game.arena;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import br.com.zenix.hungergames.HungerGames;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.gamer.Gamer;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class ArenaConstructor {

	private Location l1, l2, l3, l4, l5;
	private int count;
	private boolean stopProcess;

	public ArenaConstructor() {
		this.stopProcess = false;

		this.l1 = new Location(Bukkit.getWorld("arena"), 1397, 5, 212);
		this.l2 = new Location(Bukkit.getWorld("arena"), 1397, 5, 200);
		this.l3 = new Location(Bukkit.getWorld("arena"), 1403, 4, 206);
		this.l4 = new Location(Bukkit.getWorld("arena"), 1397, 5, 206);
		this.l5 = new Location(Bukkit.getWorld("arena"), 1391, 5, 206);
		
		this.count = 0;
	}

	public void teleportAll() {

		Manager manager = HungerGames.getManager();

		for (Player players : Bukkit.getOnlinePlayers()) {
			Gamer gamer = HungerGames.getManager().getGamerManager().getGamer(players);

			if (gamer.isAlive()) {
				HungerGames.getManager().getArenaManager().getGamersAlready().add(players);
			}
		}
		
		while (this.stopProcess == false && this.count >= 5){
			
			if (count >= 4 && this.stopProcess == false){
				count = 0;
			}
			
			count++;
		}

		while (this.stopProcess == false) {

			Player randomPlayer = manager.getArenaManager().getGamersAlready()
					.get(new Random().nextInt(manager.getArenaManager().getGamersAlready().size()));

			Gamer gmr = manager.getGamerManager().getGamer(randomPlayer);

			if (gmr.isAlive()) {
				manager.getArenaManager().getGamersAlready().remove(randomPlayer);
				randomPlayer.teleport(getLocation());
			}

		}

	}

	public Location getLocation() {
		Location loc = null;
		Manager manager = HungerGames.getManager();
		ArenaManager arenaManager = manager.getArenaManager();

		if (arenaManager.getLastLocation() == null) {
			loc = l1;
		} else {
			if (arenaManager.getLastLocation() == l1) {
				loc = l2;
			} else if (arenaManager.getLastLocation() == l2) {
				loc = l3;
			} else if (arenaManager.getLastLocation() == l3) {
				loc = l4;
			} else if (arenaManager.getLastLocation() == l4) {
				loc = l5;
			}
		}
		
		return loc;
	}

}
