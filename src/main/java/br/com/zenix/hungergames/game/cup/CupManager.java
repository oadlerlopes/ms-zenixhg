package br.com.zenix.hungergames.game.cup;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.WorldCreator;

import br.com.zenix.hungergames.game.cup.stage.GroupStage;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.manager.constructor.Management;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class CupManager extends Management {

	private GroupStage groupStage;
	private int actualDay;
	private int actualMonth;

	public CupManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		Date now = new Date();

		SimpleDateFormat simpleDay, simpleMonth;

		simpleDay = new SimpleDateFormat("dd");
		actualDay = Integer.valueOf(simpleDay.format(now));

		simpleMonth = new SimpleDateFormat("MM");
		actualMonth = Integer.valueOf(simpleMonth.format(now));

		getManager().getPlugin().getServer().createWorld(new WorldCreator("arena")).setAutoSave(false);
		getLogger().log("The world 'arena' has loaded correcly.");

		if (isCup()) {
			getLogger()
					.log("The server detected that in this room will happen the cup! The cup mode is being activated.");

			groupStage = GroupStage.FINAL;
		}

		return true;
	}

	public GroupStage getNext(GroupStage stage) {
		if (stage == GroupStage.A || stage == GroupStage.B || stage == GroupStage.C || stage == GroupStage.D)
			return GroupStage.SEMI_FINAL;
		if (stage == GroupStage.SEMI_FINAL)
			return GroupStage.FINAL;
		return GroupStage.INVALID_GROUP;
	}

	public GroupStage getGroupStage() {
		return groupStage;
	}

	public int getDay() {
		return actualDay;
	}

	public int getMonth() {
		return actualMonth;
	}

	public boolean isCup() {
		if (getManager().getCoreManager().getServerName().toLowerCase().startsWith("copa")
				|| getManager().getCoreManager().getServerName().toLowerCase().startsWith("evento"))
			return true;
		else
			return false;
	}

	public boolean isFinal() {
		if (groupStage == GroupStage.FINAL)
			return true;
		else
			return false;
	}

}
