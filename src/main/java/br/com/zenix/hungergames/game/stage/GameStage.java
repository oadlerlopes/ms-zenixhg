package br.com.zenix.hungergames.game.stage;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public enum GameStage {

	PREGAME("Pré Jogo"),
	INVENCIBILITY("Invencibilidade"),
	GAME("Jogo"),
	FINAL("Final"),
	WINNING("Vitória");

	public String name;

	GameStage(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
