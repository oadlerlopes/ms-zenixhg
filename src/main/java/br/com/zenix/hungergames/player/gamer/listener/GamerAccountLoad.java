package br.com.zenix.hungergames.player.gamer.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.hungergames.game.custom.HungerListener;
import br.com.zenix.hungergames.player.gamer.Gamer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GamerAccountLoad extends HungerListener {

	public String[] players = new String[] { "manoFullRazer", "Khooda", "GANGMEMBERPATRAO", "Alexandroo", "Sad000",
			"starsshinehere", "_Whitee_", "IMPOSSIVEL", "kimarceus2cm", "IGuuti", "ganglux", "scrow777", "zVapeSAMG",
			"KingSkuuk", "suavao", "YSiR_G0D", "Gabirugah", "TheoCTT", "malokeey", "PingumooST", "alucard7896", "Maqro",
			"Pebrine", "HigorAlexandre", "HowlingCHEAT", "shynx777", "MeChamavaSpop", "Fiishhy", "ImNonaka", "HfzinPvP",
			"MACEexex", "speedyTank", "Verdin000", "ImSprings_", "MaybePopey", "Loows", "Brisei", "Andrew4M",
			"Suavizeey_", "Sharukan_pvp", "Viado", "Cellfish_PvP", "SuiicideChroma", "RefusalSS", "Snooqqz",
			"bjb_class", "DieTheEnd", "SouNinja", "Unknownspq", "sentanovinha", "DieTheEnd", "SouNinja", "Unknownspq",
			"sentanovinha", "BetoMeneses11", "Chaarllie", "DaddyHonny", "Fishey_", "GANGMEMBERSPIDER", "GabrielMC",
			"IsImpossibleFind", "Kavera_", "Krysher", "OniquisXiter", "Saltenha", "TANKODEMAIS", "VansPvP",
			"VelociteyXITER", "YukenST", "alwayss", "solteirao", "PardauDEUS", "GANGMEMBERCHR0MA", "ScooutZ",
			"stillfck", "thiagoftw", "yIaguuh_", "resetaay_", "zxfx", "DaddyWhiteeoutZ", "PRIVACIDADE", "Trapaay1",
			"KingCastro", "CasalBRUMAR", "AmorDaClarinha", "ittiloP", "smokeyneverstops", "MaybeHeero", "Taylam",
			"FishMooney7896", "witchbladez" };
	public String[] staff = new String[] { "faane", "adleeer", "start_", "eduardow", "nettonunes", "prismuu_",
			"lawlexinho", "beaat37", "jowbly" };

	@EventHandler(priority = EventPriority.NORMAL)
	public void login(PlayerLoginEvent event) {
		if (event.getResult() != org.bukkit.event.player.PlayerLoginEvent.Result.ALLOWED)
			return;

		Player player = event.getPlayer();
		Gamer gamer = getManager().getGamerManager().getGamer(player.getUniqueId());
		getManager().getGamerManager().getLogger().log("The player with uuid " + player.getUniqueId() + " and nickname "
				+ player.getName() + " logged into the server, starting to load her status.");

		if (gamer != null) {
			getManager().getGamerManager().getLogger().log("The player with uuid " + player.getUniqueId() + "("
					+ player.getName() + ") its already loaded, skipping a few processes.");
		} else {
			gamer = new Gamer(getManager().getCoreManager().getAccountManager().getAccount(player));
			getManager().getGamerManager().addGamer(gamer);
			getManager().getGamerManager().getLogger().log("The player with uuid " + player.getUniqueId() + "("
					+ player.getName() + ") was loaded correctly.");
			gamer.unload();
		}

		gamer.updatePlayer(event.getPlayer());

		boolean load = false;

		if (getManager().getCupManager().isCup()) {
			if (!player.hasPermission("commons.cmd.staff"))
				if (gamer.getAccount().getDataHandler().getValue(DataType.CUP_GROUP).getValue() == 0) {
					if (!player.hasPermission("commons.cmd.staff"))
						event.disallow(Result.KICK_OTHER, "§cVocê não está nesse grupo!");
				} else {
					for (String s : players) {
						if (event.getPlayer().getName().equalsIgnoreCase(s)) {
							event.allow();
							load = true;
							return;
						}
					}
					for (String s : staff) {
						if (event.getPlayer().hasPermission("commons.cmd.staff")
								|| event.getPlayer().hasPermission("commons.tag.youtuber")) {
							event.allow();
							load = true;
							return;
						}
						if (event.getPlayer().getName().equalsIgnoreCase(s)) {
							event.allow();
							load = true;
							return;
						}
					}

					if (load == false) {
						event.disallow(Result.KICK_OTHER, "§cVocê não está na lista!");
					}
				}

		}

	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!getManager().getAdminManager().isAdmin(player)) {

			if (getManager().getGameManager().isPreGame()) {

				if (player.getLocation().getBlockX() > Bukkit.getWorld("world").getSpawnLocation().getBlockX() + 100
						|| player.getLocation()
								.getBlockX() < -(100 - Bukkit.getWorld("world").getSpawnLocation().getBlockX())
						|| player.getLocation().getBlockZ() > Bukkit.getWorld("world").getSpawnLocation().getBlockZ()
								+ 100
						|| player.getLocation()
								.getBlockZ() < -(100 - Bukkit.getWorld("world").getSpawnLocation().getBlockZ())) {

					player.sendMessage(
							"§c§lBORDA §fVocê foi §4§lTELETRANSPORTADO§f para o §5§lSPAWN§f! Não se vá para muito §c§lLONGE");
					getManager().getGamerManager().teleportSpawn(player);

				}
			}

		}
	}

}
