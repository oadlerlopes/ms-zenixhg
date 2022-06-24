package br.com.zenix.hungergames.commands.player;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.player.gamer.Gamer;
import br.com.zenix.hungergames.player.gamer.GamerMode;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GiveUpCommand extends HungerCommand {

	public GiveUpCommand() {
		super("desisto", "Desista da partida.", Arrays.asList("desistir"));
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return true;
		}

		if (args.length != 0) {
			commandSender.sendMessage("§c§lDESISTO §fUse: /desisto");
			return true;
		}

		Gamer gamer = getManager().getGamerManager().getGamer((Player) commandSender);

		if (gamer.getMode() != GamerMode.ALIVE) {
			commandSender.sendMessage("§c§lDESISTO §fVocê não está em §4§lJOGO!");
			return true;
		}

		if (getManager().getGameManager().isPreGame()) {
			commandSender.sendMessage("§c§lDESISTO §fVocê não pode §4§lDESISTIU AGORA!");
			return true;
		}

		Location location = gamer.getPlayer().getLocation().clone().add(0, 0.5, 0);

		for (ItemStack items : gamer.getPlayer().getInventory().getContents()) {
			if (items == null || items.getType() == Material.AIR) {
				continue;
			}
			if (!getManager().getKitManager().isItemKit(items)) {
				gamer.getPlayer().getWorld().dropItemNaturally(location, items);
			}
		}

		if (gamer.getPlayer().hasPermission("hunger.addon.spec")) {
			getManager().getGamerManager().updateGamer(gamer);
			getManager().getGamerManager().checkWinner();
			getManager().getGamerManager().setSpectator(gamer);


			Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + "[" + gamer.getKit().getName() + "] desistiu da partida. §4[" + getManager().getGamerManager().getAlivePlayers().size() + "]");
		} else {

			for (Player players : Bukkit.getOnlinePlayers()) {
				players.hidePlayer(gamer.getPlayer());
			}

			getManager().getGamerManager().setSpectator(gamer);
			getManager().getGamerManager().updateGamer(gamer);
			getManager().getGamerManager().resetKits(gamer);
			getManager().getGamerManager().checkWinner();

			gamer.getPlayer().setHealth(20.0);
			gamer.getPlayer().setFoodLevel(20);
			gamer.getPlayer().getInventory().clear();
			gamer.getPlayer().getActivePotionEffects().clear();
			gamer.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
			gamer.getPlayer().setFireTicks(0);
			gamer.getPlayer().setFoodLevel(20);
			gamer.getPlayer().setFlying(true);
			gamer.getPlayer().setAllowFlight(true);
			gamer.getPlayer().setSaturation(3.2F);

			Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + "[" + gamer.getKit().getName() + "] desistiu da partida. §4[" + getManager().getGamerManager().getAlivePlayers().size() + "]");

			gamer.getPlayer().kickPlayer("\n§c§lDESISTO §fVocê §4§lDESISTIU§f da §c§lPARTIDA§f.");
		}

		commandSender.sendMessage("§c§lDESISTO §fVocê §4§lDESISTIU§f da §c§lPARTIDA§F!");

		return false;
	}

}
