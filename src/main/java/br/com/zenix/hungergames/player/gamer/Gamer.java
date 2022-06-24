package br.com.zenix.hungergames.player.gamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.hungergames.HungerGames;
import br.com.zenix.hungergames.manager.Manager;
import br.com.zenix.hungergames.player.kit.Kit;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Gamer {

	@Deprecated
	private static final List<Kit> playerKits = new ArrayList<>();

	private final Account account;

	private Player player;

	private Kit kit, kit2;
	private GamerMode mode;

	private UUID uuid;
	private boolean blocked, blockFunction;

	private Kit surpriseKit = null;

	private int gameKills, fighting, credits;
	private boolean online, isloaded, chatspec, itemsGive, pvpPregame, specs;

	public Gamer(Account account) {
		this.account = account;
		this.uuid = account.getUniqueId();

		mode = GamerMode.LOADING;

		gameKills = 0;
		fighting = 0;
		credits = 0;

		kit = getManager().getKitManager().getKit("Nenhum");
		kit2 = getManager().getKitManager().getKit("Nenhum");

		isloaded = false;
		online = true;
		pvpPregame = false;
		chatspec = false;
		itemsGive = false;
		pvpPregame = false;
		blocked = false;
		specs = false;
		blockFunction = false;
	}

	public boolean isBlockFunction() {
		return blockFunction;
	}

	public void setBlockFunction(boolean blockFunction) {
		this.blockFunction = blockFunction;
	}

	public Kit getKit2() {
		return kit2;
	}

	public void setKit2(Kit kit2) {
		this.kit2 = kit2;
	}

	public boolean isSpecs() {
		return specs;
	}

	public void setSpecs(boolean specs) {
		this.specs = specs;
	}

	public Kit getSurpriseKit() {
		return surpriseKit;
	}

	public void setSurpriseKit(Kit surpriseKit) {
		this.surpriseKit = surpriseKit;
	}

	public void updatePlayer(Player player) {
		this.player = player;
		getAccount().updatePlayer(player);
	}

	public void update() {
		getManager().getGamerManager().updateGamer(this);
	}

	public void unload() {
		kit = getManager().getKitManager().getKit("Nenhum");
	}

	public Account getAccount() {
		return account;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public Boolean isFighting() {
		return fighting > 0;
	}

	public Boolean isLoaded() {
		return isloaded;
	}

	public Boolean isOnline() {
		return online;
	}

	public UUID getUUID() {
		return uuid;
	}

	public Integer getGameKills() {
		return gameKills;
	}

	public GamerMode getMode() {
		return mode;
	}

	public Kit getKit() {
		return kit;
	}

	public Boolean isSpectating() {
		if (getManager().getAdminManager().isAdmin(player))
			return true;
		return mode == GamerMode.SPECTING;
	}

	public Boolean isAlive() {
		return mode == GamerMode.ALIVE;
	}

	public Boolean isDead() {
		return mode == GamerMode.DEAD;
	}

	public Player getPlayer() {
		return player;
	}

	public Integer getCredits() {
		return credits;
	}

	public Boolean isOnPvpPregame() {
		return pvpPregame;
	}

	public Boolean hasKit(String kit) {
		return (kit.equals(getManager().getGamerManager().getGamer(player).getKit().getName()));
	}

	public Boolean inChatSpec() {
		return chatspec;
	}

	public Boolean getItemsGive() {
		return itemsGive;
	}

	@Deprecated
	public List<Kit> getKits() {
		return playerKits;
	}

	public List<Kit> getMatchKits() {
		return Arrays.asList(getKit());
	}

	public Manager getManager() {
		return HungerGames.getManager();
	}

	public String getNick() {
		return getAccount().getNickname();
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public void setFighting(Integer fighting) {
		this.fighting = fighting;
	}

	public void setGameKills(Integer gameKills) {
		this.gameKills = gameKills;

		if (gameKills > getAccount().getDataHandler().getValue(DataType.HG_MOST_KILLSTREAK).getValue())
			getAccount().getDataHandler().getValue(DataType.HG_MOST_KILLSTREAK).setValue(gameKills);
	}

	public void setLoaded(Boolean bool) {
		this.isloaded = bool;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public void setCredits(Integer credits) {
		this.credits = credits;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public void setMode(GamerMode mode) {
		this.mode = mode;
	}

	public void sendMessage(String string) {
		getPlayer().sendMessage(string);
	}

	public void refreshFighting() {
		if (fighting > 0) {
			fighting -= 1;
		}
	}

	public void setItemsGive(Boolean itemsGive) {
		this.itemsGive = itemsGive;
	}

	public void setPvpPregame(Boolean pvpPregame) {
		this.pvpPregame = pvpPregame;
	}

	public void setChatspec(Boolean chatspec) {
		this.chatspec = chatspec;
	}
}
