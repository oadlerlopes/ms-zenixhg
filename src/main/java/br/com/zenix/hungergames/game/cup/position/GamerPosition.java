package br.com.zenix.hungergames.game.cup.position;

public class GamerPosition {
	private String name;
	private Integer id;
	private int top;

	public GamerPosition(Integer id, String name, int top) {
		this.id = id;
		this.name = name;
		this.top = top;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getTop() {
		return this.top;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTop(int top) {
		this.top = top;
	}
}
