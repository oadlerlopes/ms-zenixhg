package br.com.zenix.hungergames.player.kit.abilities.normal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.spigot.player.particle.ParticleType;
import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Stomper extends Kit {

	public Stomper(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(51000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.ANVIL));
		setFree(false);
		setDescription("Converta toda energia potencial gravitacional em dano a seus oponentes.");
		setRecent(false);
	}

	@EventHandler
	public void onStomp(EntityDamageEvent e) {
		stompar(e, 6.0D);
	}

	public void stompar(EntityDamageEvent event, double radius) {
		if (event.getEntity() instanceof Player && hasKit(((Player) event.getEntity())) && event.getCause() == DamageCause.FALL && !isInvencibility()) {
			Player player = (Player) event.getEntity();

			if (getGamer(player).isSpectating()) {
				return;
			}

			if (Endermage.invencible.contains(player.getUniqueId())) {
				return;
			}

			double damage = event.getDamage() * 2.0D;

			if (Launcher.getNoFallList().contains(player)) {
				event.setCancelled(true);
				Launcher.getNoFallList().remove(player);
				return;
			}

			if (event.getDamage() > 4.0D) {
				event.setCancelled(true);
				player.damage(4.00D);
			} else {
				return;
			}
			for (Entity entity : player.getNearbyEntities(radius, 2, radius)) {
				if (!(entity instanceof Player)) {
					continue;
				}
				Player nearby = (Player) entity;
				if (getGamer(nearby).isSpectating()) {
					continue;
				}

				if (getGamer(nearby).getKit().getName().equalsIgnoreCase("AntiTower"))
					continue;

				if (nearby.isSneaking()) {
					getManager().getGamerManager().giveDamage(nearby, player, 4.0D, true);
				} else {
					if (nearby.getHealth() - event.getDamage() > 0.0D) {
						nearby.damage(event.getDamage());
					} else {
						getManager().getGamerManager().giveDamage(nearby, player, damage, false);
					}
				}
			}
			for (int x = (int) -radius; x <= radius; x++) {
				for (int z = (int) -radius; z <= radius; z++) {
					Location effect = player.getLocation().clone().add(x, 0, z);
					if (effect.distance(player.getLocation()) > radius) {
						continue;
					}
					ParticleType.WITCH_MAGIC.setParticle(effect, 0.1F, 0.1F, 0.1F, 1, 30);
				}
			}
		}
	}

}
