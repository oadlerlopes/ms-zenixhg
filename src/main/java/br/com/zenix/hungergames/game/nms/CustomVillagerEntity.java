package br.com.zenix.hungergames.game.nms;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;

import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityAgeable;
import net.minecraft.server.v1_7_R4.EntityVillager;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class CustomVillagerEntity extends EntityVillager {

	public CustomVillagerEntity(net.minecraft.server.v1_7_R4.World world) {
		super(world);
	}

	public boolean damageEntity(DamageSource damagesource, float f) {
		return false;
	}

	@Override
	public void collide(Entity arg0) {
	}

	@Override
	public void move(double arg0, double arg1, double arg2) {
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityAgeable) {
		return null;
	}

	public void remove() {
		((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().removeEntity(this);
	}
}