package br.com.zenix.hungergames.utilitaries;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftSnowball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;

import net.minecraft.server.v1_7_R4.EntityFishingHook;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntitySnowball;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class HookUtil extends EntityFishingHook {

	private Entity entity;
	private Snowball snowball;
	private EntitySnowball entitySnowball;
	private boolean hooked;

	public HookUtil(World world, EntityHuman entity) {
		super(((CraftWorld) world).getHandle(), entity);
		owner = entity;
	}

	public void remove() {
		super.die();
	}

	public boolean isHooked() {
		return hooked;
	}

	public void spawn(Location location) {
		snowball = owner.getBukkitEntity().launchProjectile(Snowball.class);
		entitySnowball = ((CraftSnowball) snowball).getHandle();

		for (Player all : Bukkit.getOnlinePlayers())
			((CraftPlayer) all).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[] { entitySnowball.getId() }));

		((CraftWorld) location.getWorld()).getHandle().addEntity(this);
	}

	@Override
	public void h() {
		for (Entity entity : entitySnowball.world.getWorld().getEntities()) {
			if (entity.getEntityId() != getBukkitEntity().getEntityId() && entity.getEntityId() != owner.getBukkitEntity().getEntityId() && entity.getEntityId() != entitySnowball.getBukkitEntity().getEntityId()
					&& (entity.getLocation().distance(entitySnowball.getBukkitEntity().getLocation()) < 2.0
							|| (entity instanceof Player && ((Player) entity).getEyeLocation().distance(entitySnowball.getBukkitEntity().getLocation()) < 2.0))) {
				entitySnowball.die();
				this.entity = entity;
				hooked = true;
				locX = entity.getLocation().getX();
				locY = entity.getLocation().getY();
				locZ = entity.getLocation().getZ();
				motX = 0.0;
				motY = 0.04;
				motZ = 0.0;
			}
		}
		try {
			locX = entity.getLocation().getX();
			locY = entity.getLocation().getY();
			locZ = entity.getLocation().getZ();
			motX = 0.0;
			motY = 0.04;
			motZ = 0.0;
			hooked = true;
		} catch (Exception e) {
			if (entitySnowball.dead)
				hooked = true;

			locX = entitySnowball.locX;
			locY = entitySnowball.locY;
			locZ = entitySnowball.locZ;
		}
	}

}
