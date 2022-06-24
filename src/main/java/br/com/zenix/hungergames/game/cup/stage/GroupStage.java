package br.com.zenix.hungergames.game.cup.stage;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public enum GroupStage {

	A("A"),
	B("B"),
	C("C"),
	D("D"),
	E("E"),
	MINI_COPA("Mini-Copa"),
	REFISHING("Repescagem"),
	SEMI_FINAL("Semi-Final"),
	FINAL("Final"),
	INVALID_GROUP("Grupo inválido");
	
	private String name;

	private GroupStage(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
