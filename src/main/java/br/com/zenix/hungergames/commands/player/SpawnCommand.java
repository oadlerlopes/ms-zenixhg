package br.com.zenix.hungergames.commands.player;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.manager.managers.FileManager;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class SpawnCommand extends HungerCommand {

	public SpawnCommand() {
		super("spawn", "Va ate o spawn.");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] strings) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		Player player = (Player) commandSender;

		if (!getManager().getGameManager().isPreGame()) {
			player.sendMessage("§e§lSPAWN §fVocê não pode usar isto §6§lAGORA§f!");
			return false;
		}

		Random random = getManager().getRandom();

		int x, z;

		x = random.nextInt(10) + 1;
		z = random.nextInt(10) + 1;

		if (random.nextBoolean()) {
			x = x * -1;
		}

		if (random.nextBoolean()) {
			z = z * -1;
		}
		
		player.teleport(new Location(FileManager.getWorld(), x, FileManager.getWorld().getSpawnLocation().getY(), z));

		return true;
	}
}
