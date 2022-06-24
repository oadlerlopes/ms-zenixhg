package br.com.zenix.hungergames.manager.managers;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.event.Listener;

import br.com.zenix.core.plugin.utilitaries.loader.Getter;
import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.game.custom.HungerListener;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.manager.constructor.Management;
import br.com.zenix.hungergames.player.inventories.CustomInventory;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ClassManager extends Management {

	public ClassManager(Manager manager) {
		super(manager, "ClassManager");
	}

	public boolean initialize() {
		return load();
	}

	public boolean load() {
		getLogger().log("Starting trying to load all the classes of commands and listeners of the plugin.");

		for (Class<?> classes : Getter.getClassesForPackage(getManager().getPlugin(), "br.com.zenix.hungergames")) {
			try {
				if (HungerCommand.class.isAssignableFrom(classes) && classes != HungerCommand.class) {
					HungerCommand command = (HungerCommand) classes.newInstance();
					if (command.enabled) {
						((CraftServer) Bukkit.getServer()).getCommandMap().register(command.getName(), command);
					}
					getLogger().debug("The command " + command.getName() + "(" + command.getDescription() + ") its " + (command.enabled ? "enabled and loaded correcly" : "disabled and not loaded") + "!");
				}
			} catch (Exception exception) {
				getLogger().error("Error to load the command " + classes.getSimpleName() + ", stopping the process!", exception);
				return false;
			}
			try {
				Listener listener = null;
				if (!Listener.class.isAssignableFrom(classes)) {
					continue;
				} else if (Kit.class.isAssignableFrom(classes)) {
					continue;
				} else if (classes.getSimpleName().equals("Timer")) {
					continue;
				} else if (classes.getSimpleName().equals("")) {
					continue;
				} else if (classes.getSimpleName().equals("CustomInventory")) {
					continue;
				} else if (HungerCommand.class.isAssignableFrom(classes)) {
					listener = (Listener) classes.getConstructor().newInstance();
				} else if (HungerListener.class.isAssignableFrom(classes)) {
					listener = (Listener) classes.getConstructor().newInstance();
				} else if (CustomInventory.class.isAssignableFrom(classes)) {
					listener = (Listener) classes.getConstructor(Manager.class).newInstance(getManager());
				} else {
					listener = (Listener) classes.getConstructor(Manager.class).newInstance(getManager());
				}

				Bukkit.getPluginManager().registerEvents(listener, getManager().getPlugin());
				getLogger().debug("The listener " + listener.getClass().getSimpleName() + " was loaded correcly!");

			} catch (Exception exception) {
				getLogger().error("Error to load the listener " + classes.getSimpleName() + ", stopping the process!", exception);
				return false;
			}
		}
		return true;
	}

}
