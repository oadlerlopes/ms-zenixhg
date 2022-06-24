package br.com.zenix.hungergames.player.kit.abilities.normal;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Cannibal extends Kit {

	public Cannibal(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(45000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.RAW_FISH));
		setFree(false);
		setDescription("Transforme-se em um canibal e recupere sua fome 'comendo' seus inimigos.");
		setRecent(false);
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player && hasKit((Player) event.getDamager()) && new Random().nextInt(100) <= 20) {

			((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 80, 1), true);
			Player player = (Player) event.getDamager();
			int hungry = player.getFoodLevel();
			hungry++;
			if (hungry <= 20) {
				player.setFoodLevel(hungry);
			}
		}
	}

}
