package br.com.zenix.hungergames.player.kit.abilities.normal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.spigot.player.events.ServerTimeEvent;
import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.game.event.GamerHitEntityEvent;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.gamer.Gamer;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Madman extends Kit {

	public static HashMap<UUID, Integer> effect = new HashMap<>();

	public Madman(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(47500);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.POTION, 1, (short) 8232));
		setFree(false);
		setDescription("Seja o ponto dos seus oponentes e deixe-os com fraqueza.");
		setRecent(false);
	}

	@EventHandler
	public void onDamage(GamerHitEntityEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		if (effect.containsKey(e.getEntity().getUniqueId()))
			e.setDamage(e.getDamage() + ((e.getDamage() / 80.0D) * effect.get(e.getEntity().getUniqueId())));
	}

	@EventHandler
	public void onSec(ServerTimeEvent e) {
		if (getManager().getGameManager().isInvencibility())
			return;

		for (Gamer gamer : getManager().getGamerManager().getAliveGamers()) {
			if (hasKit(gamer) && !gamer.isSpectating()) {
				List<Player> lista = getNearbyPlayers(gamer, 10);

				if (lista.size() < 2)
					continue;

				for (Player perto : lista)
					addEffect(perto.getUniqueId(), lista.size() * 2);

			}
			if (effect.containsKey(gamer.getPlayer().getUniqueId()))
				removeEffect(gamer.getPlayer().getUniqueId());
		}
	}

	private void removeEffect(UUID u) {
		int effectNumber = effect.get(u);
		effectNumber = effectNumber - 10;
		effect.put(u, effectNumber);

		if (effectNumber <= 0)
			effect.remove(u);

	}

	private void addEffect(UUID u, int effectNumber) {
		int effectIndex = effect.containsKey(u) ? effect.get(u) : 0;
		effectIndex = effectIndex + effectNumber + 10;
		effect.put(u, effectIndex);

		Player player = Bukkit.getPlayer(u);
		if (player != null) {
			player.sendMessage("§6§lMADMAN §fHá um §e§lMADMAN§f por perto, você está levando o efeito!");
		}
	}

	private List<Player> getNearbyPlayers(Gamer p, int i) {
		List<Player> players = new ArrayList<Player>();
		List<Entity> entities = p.getPlayer().getNearbyEntities(i, i, i);
		for (Entity e : entities) {
			if (!(e instanceof Player))
				continue;
			if (getGamer((Player) e).isSpectating())
				continue;
			players.add((Player) e);
		}
		return players;
	}
}
