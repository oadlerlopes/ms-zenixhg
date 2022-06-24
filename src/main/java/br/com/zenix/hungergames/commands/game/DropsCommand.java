package br.com.zenix.hungergames.commands.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import br.com.zenix.core.spigot.options.ServerOptions;
import br.com.zenix.hungergames.game.custom.HungerCommand;

public class DropsCommand extends HungerCommand {

	public DropsCommand() {
		super("drops", "Change the option of build in the server.");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		if (!hasPermission(commandSender, "admin")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		ServerOptions.DROPS.setActive(!ServerOptions.DROPS.isActive());
		Bukkit.broadcastMessage("§7[get-items-message]: "
				+ (ServerOptions.DROPS.isActive() ? "§aATIVADO".toUpperCase() : "§cDESATIVADO".toUpperCase()));

		return true;
	}

}