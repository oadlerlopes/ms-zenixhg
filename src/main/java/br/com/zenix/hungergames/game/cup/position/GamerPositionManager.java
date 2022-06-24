package br.com.zenix.hungergames.game.cup.position;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.manager.constructor.Management;

public class GamerPositionManager extends Management {
	
	private static final List<GamerPosition> topScore = new ArrayList<>();
	private static final List<GamerPosition> topScoreCup = new ArrayList<>();
	
	public GamerPositionManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		topScore.clear();
		loadList(topScore);
		return true;
	}
	
	public StringBuilder getGroupA(){
		
		StringBuilder sb = new StringBuilder();
		
		for (GamerPosition scoreTop : topScoreCup) {
			if ((scoreTop != null) && (scoreTop.getTop() != 0)) {
				sb.append("\n"+scoreTop.getId().intValue() + "º - " + scoreTop.getName());
			}
		}
		return sb;	
	}

	public boolean loadList(List<GamerPosition> list) {
		ResultSet set = getManager().getCoreManager().getDataManager().getMySQL()
				.executeQuery("SELECT * FROM `global_data` WHERE `type`=12 ORDER BY `value` DESC");
		try {
			int id = 0;
			while (set.next()) {
				id++;
				list.add(new GamerPosition(Integer.valueOf(id),
						getManager().getCoreManager().getNameFetcher().getName(set.getInt("player")),
						set.getInt("value")));
			}
			set.close();
			return true;
		} catch (Exception exeption) {
			exeption.printStackTrace();
		}
		return false;
	}


	public boolean loadListCup(List<GamerPosition> list) {
		ResultSet set = getManager().getCoreManager().getDataManager().getMySQL()
				.executeQuery("SELECT * FROM `global_data` WHERE `type`=36 ORDER BY `value` DESC");
		int id = 0;
		try {
			while (set.next()) {
				id++;
				list.add(new GamerPosition(id,
						getManager().getCoreManager().getNameFetcher().getName(set.getInt("player")),
						set.getInt("value")));
			}
			set.close();
			return true;
		} catch (Exception exeption) {
			exeption.printStackTrace();
		}
		return false;
	}

	public int getPosition(Player player) {
		int i = 0;
		for (GamerPosition scoreTop : topScore) {
			if ((scoreTop != null) && (scoreTop.getName().equals(player.getName()))) {
				i = scoreTop.getId().intValue();
			}
		}
		return i;
	}
}
