package br.com.zenix.hungergames.game.nms;

import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityItem;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.World;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class CustomItemEntity extends EntityItem {

	public CustomItemEntity(World world) {
		super(world);
	}

	public boolean damageEntity(DamageSource damagesource, float f) {
		return false;
	}

	public void collide(Entity arg0) {

	}

	public void move(double d0, double d1, double d2) {

	}

	public ItemStack getItemStack() {
		return super.getItemStack();
	}

	public void setItemStack(ItemStack itemstack) {
		super.setItemStack(itemstack);
	}
}
