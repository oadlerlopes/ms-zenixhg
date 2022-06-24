package br.com.zenix.hungergames.player.kit.abilities.normal;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;
import br.com.zenix.hungergames.utilitaries.HookUtil;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Grappler extends Kit {

	private static HashMap<UUID, HookUtil> hooks = new HashMap<>();
	private static HashMap<UUID, Long> hitNerf = new HashMap<>();

	public Grappler(Manager manager) {
		super(manager);
		setServerTypes(ServerType.HG);
		setPrice(52000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.LEASH));
		setFree(false);
		setDescription("Tenha suas hablidades de um escalador profissional e com sua corda, ande rapidamente.");
		setRecent(false);
		setItems(createItemStack("§aGrappler", Material.LEASH));
	}

	@EventHandler
	private void grapplerHabilidade(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (player.getItemInHand().getType().equals(Material.LEASH)) {
			if (hasKit(player)) {
				event.setCancelled(true);

				if (hitNerf.containsKey(player.getUniqueId())
						&& hitNerf.get(player.getUniqueId()) > System.currentTimeMillis()) {
					player.setVelocity(new Vector(0, -1.0, 0));
					player.sendMessage("§6§lGRAPPLER §fVocê está em §4§lCOMBATE§f, aguarde para usar sua habilidade.");
					return;
				}

				Location location1 = player.getLocation();
				if ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.LEFT_CLICK_BLOCK)) {
					if (hooks.containsKey(player.getUniqueId())) {
						hooks.get(player.getUniqueId()).remove();
					}
					Vector direction = location1.getDirection();
					HookUtil nms = new HookUtil(player.getWorld(), ((CraftPlayer) player).getHandle());

					nms.spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()));
					nms.move(5.0D * direction.getX(), 5.0D * direction.getY(), 5.0D * direction.getZ());

					hooks.put(player.getUniqueId(), nms);
				} else if ((hooks.containsKey(player.getUniqueId()))
						&& ((hooks.get(player.getUniqueId())).isHooked())) {
					Location location2 = ((HookUtil) hooks.get(player.getUniqueId())).getBukkitEntity()
							.getLocation();

					double distance = location2.distance(location1);
					double vectorX = (1.0D + 0.07D * distance) * (location2.getX() - location1.getX()) / distance;
					double vectorY = (1.0D + 0.03D * distance) * (location2.getY() - location1.getY()) / distance;
					double vectorZ = (1.0D + 0.07D * distance) * (location2.getZ() - location1.getZ()) / distance;

					player.setVelocity(new Vector(vectorX, vectorY, vectorZ));
				}
			}
		}
	}

	@EventHandler
	private void onRemoveLeash(PlayerItemHeldEvent event) {
		if (hooks.containsKey(event.getPlayer().getUniqueId())) {
			hooks.get(event.getPlayer().getUniqueId()).remove();
			hooks.remove(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (!hasKit((Player) event.getEntity()))
			return;
		if (event.isCancelled())
			return;
		if (!(event.getDamager() instanceof LivingEntity))
			return;
		if (getManager().getGameManager().isInvencibility())
			return;
		if (event.getDamager() instanceof Player && getGamer((Player) event.getDamager()).isSpectating())
			return;

		hitNerf.put(event.getEntity().getUniqueId(), System.currentTimeMillis() + 5000L);
	}

	@EventHandler
	public void KitDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		ItemStack itemStack = event.getItemDrop().getItemStack();
		if (hasKit(player) && (itemStack.getType() == Material.LEASH)) {
			event.setCancelled(true);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getManager().getPlugin(), new Runnable() {
				public void run() {
					player.updateInventory();
				}
			}, 1L);
		}
	}

}
