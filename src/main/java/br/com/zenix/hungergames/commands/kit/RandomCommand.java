package br.com.zenix.hungergames.commands.kit;

import java.util.List;
import java.util.Random;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class RandomCommand extends HungerCommand {

	public RandomCommand() {
		super("random", "Escolha um kit randomico.");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (!hasPermission(commandSender, "random")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		List<Kit> kits = getManager().getKitManager().getPlayerKits((Player) commandSender);
		Kit kit = kits.get(new Random().nextInt(kits.size()));
		commandSender.sendMessage("§d§lRANDOM §fUm misterioso mestre selecionou o kit §5§l" + kit.getName().toUpperCase() + "§f para você!");
		getManager().getGamerManager().getGamer((Player) commandSender).setKit(kit);

		return true;
	}

}
