package br.com.zenix.hungergames.commands.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.hungergames.game.custom.HungerCommand;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ArenaCommand extends HungerCommand {

	public ArenaCommand() {
		super("createarena");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		Player p = (Player) commandSender;

		if (!hasPermission(commandSender, "createarena")) {
			sendPermissionMessage(commandSender);
			return false;
		}
		if (args.length != 4) {
			commandSender.sendMessage("§d§lARENA §fUse: /createarena <raio> <bloco> <altura> <quadrado, redondo>.");
			return false;

		}

		if (args.length == 4) {
			if (!isInteger(args[0])) {
				p.sendMessage("§d§lARENA §fO raio precisa ser um número!");
				return false;
			}

			if (!isInteger(args[1])) {
				p.sendMessage("§d§lARENA §fO bloco precisa ser um número (id)!");
				return false;
			}

			if (!isInteger(args[2])) {
				p.sendMessage("§d§lARENA §fA altura precisa ser um número!");
				return false;
			}

			if (Integer.valueOf(args[0]) > 100) {
				p.sendMessage("§d§lARENA §fO raio não pode ser maior que 100 blocos!");
				return false;
			}

			if (Integer.valueOf(args[2]) > 100) {
				p.sendMessage("§d§lARENA §fA altura não pode ser maior que 100 blocos!");
				return false;
			}

			Integer size = Integer.valueOf(args[0]);
			Integer block = Integer.valueOf(args[1]);
			Integer y = Integer.valueOf(args[2]);
			if (args[3].equalsIgnoreCase("quadrado")) {
				generateArena(p.getLocation(), size, y, Material.getMaterial(block).getId());
				p.sendMessage("§d§lARENA §fVocê gerou uma arena §6§lQUADRADA§f com o tamanho de §d§l" + size
						+ "§f blocos e com uma altura de §d§l" + y + "§f com o bloco §d§l"
						+ Material.getMaterial(block).name().toUpperCase() + "§f!");
			} else if (args[3].equalsIgnoreCase("redondo")) {
				generateCircleArena(p.getLocation(), size, y, Material.getMaterial(block));
				p.sendMessage("§d§lARENA §fVocê gerou uma arena §6§lREDONDA§f com o tamanho de §d§l" + size
						+ "§f blocos e com uma altura de §d§l" + y + "§f com o bloco §d§l"
						+ Material.getMaterial(block).name().toUpperCase() + "§f!");
			} else {
				commandSender.sendMessage("§d§lARENA §fUse: /createarena <raio> <bloco> <altura> <quadrado, redondo>.");
			}

			return true;
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	public void generateArena(Location location, int size, int height, int blockId) {
		Material type = Material.getMaterial(blockId);
		for (int y = 0; y <= height; y++) {
			if (y == 0) {
				for (int x = -size; x <= size; x++) {
					for (int z = -size; z <= size; z++) {
						Location loc = location.clone().add(x, y, z);
						getManager().getCoreManager().getBO2().setBlockFast(location.getWorld(), loc.getBlockX(),
								loc.getBlockY(), loc.getBlockZ(), type.getId(), (byte) 0);
					}
				}
			} else {
				for (int x = -size; x <= size; x++) {
					Location loc = location.clone().add(x, y, size);
					Location loc2 = location.clone().add(x, y, -size);
					getManager().getCoreManager().getBO2().setBlockFast(location.getWorld(), loc.getBlockX(),
							loc.getBlockY(), loc.getBlockZ(), type.getId(), (byte) 0);
					getManager().getCoreManager().getBO2().setBlockFast(location.getWorld(), loc2.getBlockX(),
							loc2.getBlockY(), loc2.getBlockZ(), type.getId(), (byte) 0);
				}
				for (int z = -size; z <= size; z++) {
					Location loc = location.clone().add(size, y, z);
					Location loc2 = location.clone().add(-size, y, z);
					getManager().getCoreManager().getBO2().setBlockFast(location.getWorld(), loc.getBlockX(),
							loc.getBlockY(), loc.getBlockZ(), type.getId(), (byte) 0);
					getManager().getCoreManager().getBO2().setBlockFast(location.getWorld(), loc2.getBlockX(),
							loc2.getBlockY(), loc2.getBlockZ(), type.getId(), (byte) 0);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void generateCircleArena(Location location, int range, int height, Material type) {
		int x1 = location.getBlockX();
		int y1 = location.getBlockY();
		int z1 = location.getBlockZ();

		for (int y = 0; y < height; y++) {
			if (y == 0) {
				for (int r = 0; r <= range; r++) {
					for (int i = 0; i < 360; i++) {
						Location loc = location.clone().add(x1 + (Math.cos(Math.toRadians(i)) * r), y1 + y,
								z1 + (Math.sin(Math.toRadians(i)) * r));
						getManager().getCoreManager().getBO2().setBlockFast(location.getWorld(), loc.getBlockX(),
								loc.getBlockY(), loc.getBlockZ(), type.getId(), (byte) 0);
					}
				}
			} else {
				for (int i = 0; i < 360; i++) {
					Location loc = location.clone().add(x1 + (Math.cos(Math.toRadians(i)) * range), y1 + y,
							z1 + (Math.sin(Math.toRadians(i)) * range));
					getManager().getCoreManager().getBO2().setBlockFast(location.getWorld(), loc.getBlockX(),
							loc.getBlockY(), loc.getBlockZ(), type.getId(), (byte) 0);
				}
			}
		}
	}

}
