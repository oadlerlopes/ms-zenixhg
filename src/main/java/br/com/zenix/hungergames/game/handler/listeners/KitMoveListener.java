package br.com.zenix.hungergames.game.handler.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import br.com.zenix.core.spigot.player.bossbar.BarAPI;
import br.com.zenix.hungergames.game.custom.HungerListener;
import br.com.zenix.hungergames.player.gamer.Gamer;

public class KitMoveListener extends HungerListener {
	
	public ArrayList<Player> kitMove = new ArrayList<Player>();

	@EventHandler
	public void onPlayerKit(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		if (!(e.getDamager() instanceof Player)) {
			return;
		}
		if (!getManager().getGameManager().isGame()) {
			return;
		}
		Player player = (Player) e.getEntity();
		Player playerDamager = (Player) e.getDamager();

		Gamer gamer = getManager().getGamerManager().getGamer(player);

		if (player == playerDamager) {
			return;
		}

		kitMove.add(player);
		kitMove.add(playerDamager);

		BarAPI.setMessage(playerDamager, "§f" + player.getName() + " - " + gamer.getKit().getName());
		BarAPI.setMessage(player,
				"§f" + playerDamager.getName() + " - " + getManager().getGamerManager().getGamer(playerDamager).getKit().getName());
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getManager().getPlugin(), new Runnable() {
			public void run() {
				if (!kitMove.contains(player) && !kitMove.contains(playerDamager))
					return;
				
				BarAPI.removeBar(playerDamager);
				
				kitMove.remove(player);
				kitMove.remove(playerDamager);
			}
		}, 40L);
		return;
	}

}
