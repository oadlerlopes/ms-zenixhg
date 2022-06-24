package br.com.zenix.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import br.com.zenix.core.spigot.player.events.ServerTimeEvent;
import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.game.event.GamerHitEntityEvent;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;
import net.minecraft.server.v1_7_R4.DamageSource;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Magma extends Kit {

	public Magma(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(47500);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.LAVA_BUCKET));
		setFree(false);
		setDescription("Seja igual o magma terrestre, fique imune a altas temperaturas mas receba dano na agua.");
		setRecent(false);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && hasKit((Player) e.getEntity()) && (e.getCause() == DamageCause.LAVA || e.getCause().name().contains("FIRE"))) {
			if (e.getCause() == DamageCause.LIGHTNING && (Math.abs(e.getEntity().getLocation().getBlockX()) > 490 || Math.abs(e.getEntity().getLocation().getBlockZ()) > 490))
				return;
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(GamerHitEntityEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		if (getManager().getGameManager().isPreGame())
			return;
		if (getGamer(e.getDamager()).isSpectating())
			return;
		if (hasKit(e.getDamager())) {
			if (getManager().getRandom().nextInt(100) <= 33) {
				e.getEntity().setFireTicks(90);
			}
		}
	}

	@EventHandler
	public void onUpdate(ServerTimeEvent event) {
		for (Player p : getManager().getGamerManager().getAlivePlayers()) {
			if (hasKit(p)) {
				if (!p.getLocation().getBlock().getType().name().contains("WATER")) {
					continue;
				}
				((CraftPlayer) p).getHandle().damageEntity(DamageSource.DROWN, 2);
			}
		}
	}
}
