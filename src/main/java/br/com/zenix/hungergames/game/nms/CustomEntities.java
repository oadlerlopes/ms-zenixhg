package br.com.zenix.hungergames.game.nms;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import br.com.zenix.core.spigot.Core;
import net.minecraft.server.v1_7_R4.Entity;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

@SuppressWarnings("deprecation")
public enum CustomEntities {

	CUSTOM_VILLAGER("CustomVillager", EntityType.VILLAGER.getTypeId(), CustomVillagerEntity.class),
	CUSTOM_ITEM("CustomItem", EntityType.DROPPED_ITEM.getTypeId(), CustomItemEntity.class);

	public static void spawnEntity(Entity entity, Location loc) {
		entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		entity.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		((CraftWorld) loc.getWorld()).getHandle().addEntity(entity, SpawnReason.SPAWNER);
	}

	CustomEntities(String name, int id, Class<? extends Entity> custom) {
		addToMaps(custom, name, id);
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	private static void addToMaps(Class<? extends Entity> clazz, String name, int id) {
		((Map<String, Class<? extends Entity>>) Core.getCoreManager().getUtils().getValue("c", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, clazz);
		((Map<Class<? extends Entity>, String>) Core.getCoreManager().getUtils().getValue("d", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(clazz, name);
		((Map<Class<? extends Entity>, Integer>) Core.getCoreManager().getUtils().getValue("f", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(clazz, Integer.valueOf(id));
	}

}
