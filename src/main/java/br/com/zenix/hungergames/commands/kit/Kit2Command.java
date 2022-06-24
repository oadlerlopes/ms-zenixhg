package br.com.zenix.hungergames.commands.kit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.hungergames.game.custom.HungerCommand;
import br.com.zenix.hungergames.player.gamer.Gamer;
import br.com.zenix.hungergames.player.gamer.GamerMode;
import br.com.zenix.hungergames.player.kit.Kit;
import br.com.zenix.hungergames.player.kit.abilities.Nenhum;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Kit2Command extends HungerCommand {

	public Kit2Command() {
		super("kit2", "Escolha o seu kit.");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return false;
		}

		Player player = (Player) commandSender;
		List<String> kits = new ArrayList<>();
		
		for (Kit kit : getManager().getKitManager().getPlayerKits(player)) {
			kits.add(kit.getName());
		}
		
		if (!getManager().isDoubleKit()){
			return true;
		}

		if (args.length == 0) {
			TextComponent tagsMessage = new TextComponent("§eVocê tem §9§n" + getManager().getKitManager().getPlayerKits(player).size() + "§e kits: ");
			for (int i = 0; i < kits.size(); i++) {
				String kit = kits.get(i);
				tagsMessage.addExtra(i == 0 ? "" : ", ");
				tagsMessage.addExtra(buildKitComponent(getManager().getKitManager().getKit(kit)));
			}

			player.spigot().sendMessage(tagsMessage);
			player.sendMessage("§eMais kits em: §9loja.zenix.cc");
			player.sendMessage("§7§nDICA: §7Escolha o kit clicando no chat");

			return true;
		}
		String name = args[0];
		Kit kit = isInteger(name) ? getManager().getKitManager().getKit(Integer.valueOf(name)) : getManager().getKitManager().getKit(name);

		if (kit == null) {
			commandSender.sendMessage("§b§lKITS §fO kit §3§l" + name + "§f não existe!");
			return false;
		}

		if (!kit.isActive()) {
			commandSender.sendMessage("§b§lKITS §fO kit §3§l" + kit.getName() + "§f não está ativado!");
			return false;
		}

		if (!getManager().getKitManager().hasKit(player, kit)) {
			commandSender.sendMessage("§b§lKITS §fVocê não possui o kit §3§l" + kit.getName() + "§f, compre-o em nossa loja §fwww.zenix.cc");
			return false;
		}

		Gamer gamer = getManager().getGamerManager().getGamer(player);

		if (gamer.getKit().getName().equalsIgnoreCase(kit.getName())) {
			commandSender.sendMessage("§b§lKITS §fVocê já está utilizando o kit " + gamer.getKit().getName() + "!");
			return false;
		}

		if (gamer.getKit2().getName().equalsIgnoreCase(kit.getName())) {
			commandSender.sendMessage("§b§lKITS §fVocê já está utilizando o kit " + gamer.getKit2().getName() + "!");
			return false;
		}

		if (gamer.getKit().getName().equalsIgnoreCase(gamer.getKit2().getName()) && (!(gamer.getKit() instanceof Nenhum)) && (!(gamer.getKit2() instanceof Nenhum))) {
			commandSender.sendMessage("§b§lKITS §fVocê já está utilizando o kit " + gamer.getKit2().getName() + "!");
			return false;
		}

		if (!getManager().getGameManager().isPreGame()) {

			if (!gamer.getMode().equals(GamerMode.ALIVE)) {
				commandSender.sendMessage("§b§lKITS §fVocê não está mais participando da partida, portanto Você não pode pegar kits!");
				return false;
			}

			if (!gamer.getKit().getName().equals("Nenhum")) {
				commandSender.sendMessage("§b§lKITS §fVocê já está com um kit!");
				return false;
			}

			if (getManager().getGameManager().getGameTime() > 300 && !player.hasPermission("hunger.cmd.kit5m")) {
				commandSender.sendMessage("§b§lKITS §fVocê não pode pegar kit agora!");
				return false;
			}

			gamer.setKit2(kit);
			gamer.getKit2().give(player);
			player.sendMessage("§b§lKITS §fVocê selecionou o kit §3§l" + kit.getName());

		} else {
			player.sendMessage("§b§lKITS §fVocê selecionou o kit §3§l" + kit.getName());
			gamer.setKit2(kit);
		}

		return true;
	}

	private BaseComponent buildKitComponent(Kit kit) {
		BaseComponent baseComponent = new TextComponent((kit.isActive() ? "§e" : "§c") + kit.getName());
		BaseComponent descComponent = new TextComponent("§9Informações:");
		descComponent.addExtra("\n");

		for (String lore : Utils.getFormattedLore(kit.getDescription())) {
			descComponent.addExtra(lore + "\n");
		}

		baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { descComponent, new TextComponent("\n"), new TextComponent("§aClique para selecionar!") }));
		baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kit " + kit.getName()));
		return baseComponent;
	}

}
