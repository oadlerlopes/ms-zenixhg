package br.com.zenix.hungergames.player.gamer.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.plugin.data.handler.DataHandler;
import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.item.ItemBuilder;
import br.com.zenix.core.spigot.player.league.player.PlayerLeague;
import br.com.zenix.core.spigot.server.Variables;
import br.com.zenix.hungergames.game.custom.HungerListener;
import br.com.zenix.hungergames.game.event.GamerDeathEvent;
import br.com.zenix.hungergames.player.gamer.Gamer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GamerDeath extends HungerListener {

	private boolean firstKill = false;

	private String getItem(Material type) {
		String cause = "";

		if (type.equals(Material.BOWL))
			cause = "tigela";
		else if (type.equals(Material.MUSHROOM_SOUP))
			cause = "sopa";
		else if (type.equals(Material.COMPASS))
			cause = "bússola";
		else if (type.equals(Material.STICK))
			cause = "madeira";
		else if (type.equals(Material.IRON_INGOT))
			cause = "barra de ferro";
		else if (type.equals(Material.GOLD_INGOT))
			cause = "barra de ouro";
		else if (type.equals(Material.BOW))
			cause = "arco";
		else if (type.equals(Material.WOOD_SWORD))
			cause = "espada de madeira";
		else if (type.equals(Material.STONE_SWORD))
			cause = "espada de pedra";
		else if (type.equals(Material.IRON_SWORD))
			cause = "espada de ferro";
		else if (type.equals(Material.DIAMOND_SWORD))
			cause = "espada de diamante";
		else if (type.equals(Material.WOOD_AXE))
			cause = "machado de madeira";
		else if (type.equals(Material.STONE_AXE))
			cause = "machado de pedra";
		else if (type.equals(Material.IRON_AXE))
			cause = "machado de ferro";
		else if (type.equals(Material.DIAMOND_AXE))
			cause = "machado de diamante";
		else
			cause = "mão";
		return cause;
	}

	private String getCause(DamageCause deathCause) {
		String cause = "";
		if (deathCause.equals(DamageCause.ENTITY_ATTACK)) {
			cause = "atacado por um monstro";
		} else if (deathCause.equals(DamageCause.CUSTOM)) {
			cause = "de uma forma não conhecida";
		} else if (deathCause.equals(DamageCause.BLOCK_EXPLOSION)) {
			cause = "explodido em mil pedaços";
		} else if (deathCause.equals(DamageCause.ENTITY_EXPLOSION)) {
			cause = "explodido por um monstro";
		} else if (deathCause.equals(DamageCause.CONTACT)) {
			cause = "abraçando um cacto";
		} else if (deathCause.equals(DamageCause.FALL)) {
			cause = "esquecendo de abrir os paraquedas";
		} else if (deathCause.equals(DamageCause.FALLING_BLOCK)) {
			cause = "stompado por um bloco";
		} else if (deathCause.equals(DamageCause.FIRE_TICK)) {
			cause = "pegando fogo";
		} else if (deathCause.equals(DamageCause.LAVA)) {
			cause = "nadando na lava";
		} else if (deathCause.equals(DamageCause.LIGHTNING)) {
			cause = "atingido por um raio";
		} else if (deathCause.equals(DamageCause.MAGIC)) {
			cause = "atingido por uma magia";
		} else if (deathCause.equals(DamageCause.MELTING)) {
			cause = "atingido por um boneco de neve";
		} else if (deathCause.equals(DamageCause.POISON)) {
			cause = "envenenado";
		} else if (deathCause.equals(DamageCause.PROJECTILE)) {
			cause = "atingido por um projetil";
		} else if (deathCause.equals(DamageCause.STARVATION)) {
			cause = "de fome";
		} else if (deathCause.equals(DamageCause.SUFFOCATION)) {
			cause = "sufocado";
		} else if (deathCause.equals(DamageCause.SUICIDE)) {
			cause = "se suicidando";
		} else if (deathCause.equals(DamageCause.THORNS)) {
			cause = "encostando em alguns espinhos";
		} else if (deathCause.equals(DamageCause.VOID)) {
			cause = "pela pressão do void";
		} else if (deathCause.equals(DamageCause.WITHER)) {
			cause = "pelo efeito do whiter";
		} else {
			cause = "por uma causa desconhecida";
		}
		return cause;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!getManager().getGameManager().isPreGame()) {
			event.setDeathMessage(null);

			Gamer player = getManager().getGamerManager().getGamer(event.getEntity());
			Account playerAccount = player.getAccount();
			StringBuilder message = new StringBuilder();

			List<ItemStack> drops = event.getDrops();
			Location location = event.getEntity().getLocation();

			for (ItemStack items : drops) {
				if (items == null || items.getType() == Material.AIR) {
					continue;
				}
				if (!getManager().getKitManager().isItemKit(items)) {
					if (event.getEntity().getKiller() instanceof Player) {
						event.getEntity().getWorld().dropItemNaturally(event.getEntity().getKiller().getLocation(),
								items);
					} else {
						event.getEntity().getWorld().dropItemNaturally(location, items);
					}
				}
			}

			GamerDeathEvent eventGamer;
			if (event.getEntity().getKiller() instanceof Player) {
				eventGamer = new GamerDeathEvent(event.getEntity().getKiller().getPlayer(), event.getEntity(),
						event.getEntity().getLocation().clone().add(0, 0.5, 0), event.getDrops());
			} else {
				eventGamer = new GamerDeathEvent(event.getEntity().getKiller(), event.getEntity().getPlayer(),
						event.getEntity().getPlayer().getLocation().clone().add(0, 0.5, 0), event.getDrops());
			}

			Bukkit.getPluginManager().callEvent(eventGamer);

			event.getDrops().clear();

			String kitDeath = player.getKit().getName();

			if (player.getAccount().getPlayer().getWorld() == Bukkit.getWorld("world"))
				Bukkit.getWorld("world")
						.strikeLightning(location.clone().add(new Location(Bukkit.getWorld("world"), 0, 100, 0)));

			Gamer playerKiller = null;

			if (!(event.getEntity().getKiller() instanceof Player)) {
				message.append("§e" + player.getPlayer().getName());

				message.append("[" + kitDeath + "]");
				message.append(" morreu " + getCause(player.getPlayer().getLastDamageCause().getCause()));
				getManager().getGamerManager().hideSpecs(player.getPlayer());
			} else {
				playerKiller = getManager().getGamerManager().getGamer(event.getEntity().getKiller());
				Account accountKiller = playerKiller.getAccount();

				String kitKill = playerKiller.getKit().getName();

				message.append("§e" + playerKiller.getPlayer().getName());
				message.append("[" + kitKill + "]");
				message.append(" não teve piedade nenhuma de " + player.getPlayer().getName());
				message.append("[" + kitDeath + "]");
				message.append(" usou sua ");
				message.append(getItem(playerKiller.getPlayer().getItemInHand().getType()));

				DataHandler dataHandler = accountKiller.getDataHandler();

				dataHandler.getValue(DataType.HG_KILL).setValue(dataHandler.getValue(DataType.HG_KILL).getValue() + 1);
				dataHandler.update(DataType.HG_KILL);

				playerKiller.setGameKills(playerKiller.getGameKills() + 1);

				DataHandler dataDeathHandler = playerAccount.getDataHandler();

				dataDeathHandler.getValue(DataType.HG_DEATH)
						.setValue(dataDeathHandler.getValue(DataType.HG_DEATH).getValue() + 1);
				dataDeathHandler.update(DataType.HG_DEATH);

				if (!firstKill) {
					firstKill = true;
					playerKiller.getPlayer().getInventory().setChestplate(new ItemBuilder(Material.AIR)
							.setColor(Material.LEATHER_CHESTPLATE, Color.GREEN, "§aPeitoral"));
					playerKiller.sendMessage("§a§lFIRSTBLOOD §fVocê foi o §2§lPRIMEIRO§f a §a§lMATAR!");
					playerKiller.sendMessage("§b§lXP §f15XP Bônus por ser o 1° a matar!");

					playerKiller.getAccount().getDataHandler().getValue(DataType.GLOBAL_XP).setValue(
							playerKiller.getAccount().getDataHandler().getValue(DataType.GLOBAL_XP).getValue() + 15);
					playerKiller.getAccount().getDataHandler().update(DataType.GLOBAL_XP);
				}

				getManager().getGamerManager().hideSpecs(player.getPlayer());
			}

			player.update();

			if (event.getEntity().getKiller() instanceof Player) {
				getManager().getGamerManager().hideSpecs(playerKiller.getPlayer());
				if (Variables.EVENT == false) {
					new PlayerLeague(playerKiller.getPlayer(), playerAccount.getPlayer()).prizeLeague();
				}
			}

			boolean hasRespawn = false;

			if (player.getPlayer().hasPermission("hunger.addon.respawn.plus")
					&& getManager().getGameManager().getGameTime() <= 420) {
				hasRespawn = true;
				getManager().getGamerManager().respawnPlayer(player);
				getManager().getGamerManager().setRespawn(player);
			} else if (player.getPlayer().hasPermission("hunger.addon.respawn")
					&& getManager().getGameManager().getGameTime() <= 300) {
				hasRespawn = true;
				getManager().getGamerManager().respawnPlayer(player);
				getManager().getGamerManager().setRespawn(player);
			} else if (player.getPlayer().hasPermission("hunger.addon.spec")) {
				getManager().getGamerManager().respawnPlayer(player);
				getManager().getGamerManager().setSpectator(player);
			} else {
				getManager().getGamerManager().setDied(player);
				if (event.getEntity().getKiller() instanceof Player) {
					player.getPlayer().kickPlayer("§4§lMORTE §fVocê morreu pelo player §f"
							+ event.getEntity().getKiller().getName()
							+ "\n§fPara continuar §6§lJOGANDO§f ou §6§lASSISTINDO§f\n§6§lADQUIRA§f VIP em §6www.zenix.cc");
				} else {
					player.getPlayer().kickPlayer("§4§lMORTE §fVocê morreu §c§l"
							+ getCause(player.getPlayer().getLastDamageCause().getCause()).toUpperCase()
							+ "\n§fPara continuar §6§lJOGANDO§f ou §6§lASSISTINDO§f\n§6§lADQUIRA§f VIP em §6www.zenix.cc");
				}
			}

			getManager().getGamerManager().checkWinner();

			if (!hasRespawn) {
				Bukkit.broadcastMessage(
						message.toString() + " §4[" + getManager().getGamerManager().getAlivePlayers().size() + "]");
			}
		}
	}
}