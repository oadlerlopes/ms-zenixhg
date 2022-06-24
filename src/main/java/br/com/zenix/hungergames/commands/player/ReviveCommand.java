package br.com.zenix.hungergames.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

public class ReviveCommand extends HungerCommand {

	public ReviveCommand() {
		super("revive", "Reviva um player!");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		if (!hasPermission(commandSender, "revive")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		if (args.length != 1) {
			commandSender.sendMessage("§c§lREVIVE §fUse: /revive <player>");
			return false;
		}

		Player player = Bukkit.getPlayer(args[0]);
		if (player == null) {
			commandSender.sendMessage("§c§lREVIVE §fO player está §4§lOFFLINE");
			return false;
		}
		Gamer g = getManager().getGamerManager().getGamer(player);

		if (g.isAlive()) {
			commandSender.sendMessage("§c§lREVIVE §fO player §4§L" + player.getName() + "§f já está vivo!");
			return false;
		}
		
		if (getManager().getAdminManager().isAdmin(player)) {
			g.getPlayer().chat("/admin");
		}
		
		g.setMode(GamerMode.ALIVE);
		
		for (Player o : Bukkit.getOnlinePlayers()) {
			o.showPlayer(g.getPlayer());
		}
		
		player.setGameMode(GameMode.SURVIVAL);
		player.setAllowFlight(false);
		player.closeInventory();
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.getInventory().addItem(new ItemStack(Material.COMPASS));
		
		getManager().getGamerManager().respawnPlayer(g);
		
		commandSender.sendMessage("§c§lREVIVE §fVocê reviveu o player §4§L" + player.getName() );
		return true;
	}

}
