package br.com.zenix.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.com.zenix.core.spigot.player.particle.ParticleType;
import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Grandpa extends Kit {

	public Grandpa(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(40000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.STICK));
		setFree(false);
		setRecent(false);
		setDescription("Use seu graveto para arremessar seus oponentes para trás.");
		setItems(createItemStack("§aGrandpa", Material.STICK));
	}

	@EventHandler(ignoreCancelled = true)
	public void grandpaKit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();

			if (hasKit(player) && isKitItem(player.getItemInHand(), "§aGrandpa")) {
				Vector vector = ((LivingEntity) event.getEntity()).getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

				double knockBack = 2.0D;
				try {
					ParticleType.WITCH_MAGIC.setParticle(player, event.getEntity().getLocation(), 0.2F, 0.2F, 0.2F, 1.0F, 30);
				} catch (Exception e) {
					e.printStackTrace();
				}
				((LivingEntity) event.getEntity()).setVelocity(vector.multiply(knockBack));
			}
		}
	}
}
