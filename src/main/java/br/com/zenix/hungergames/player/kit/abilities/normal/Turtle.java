package br.com.zenix.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Turtle extends Kit {

	public Turtle(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(43000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.DIAMOND_HELMET));
		setFree(false);
		setDescription("Transforme-se em uma tartaruga e com sua carapaça receba pouco dano dos inimigos.");
		setRecent(false);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player && hasKit((Player) event.getDamager()) && ((Player) event.getDamager()).isSneaking()) {
			event.setDamage(1.0D);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && hasKit((Player) event.getEntity())) {
			Player player = (Player) event.getEntity();
			if (!player.isBlocking()) {
				return;
			}
			if (event.getCause() == DamageCause.CONTACT) {
				return;
			} else if (event.getCause() == DamageCause.FALL || event.getCause().name().contains("EXPLOSION") || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.LIGHTNING
					|| event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.MAGIC || event.getCause() == DamageCause.PROJECTILE
					|| event.getCause() == DamageCause.VOID || event.getCause() == DamageCause.WITHER) {
				event.setDamage(2.0D);
			} else {
				event.setDamage(event.getDamage() / 2.0D);
			}
		}
	}
}
