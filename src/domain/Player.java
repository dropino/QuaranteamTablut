package domain;

import enums.PlayerKind;

public abstract class Player {
	
	private String id;
	private PlayerKind kind;
	
	
	public Player(String id, PlayerKind kind) {
		this.id = id;
		this.kind = kind;
	}
	
	
	public String getId() {
		return id;
	}

	
	public PlayerKind getKind() {
		return kind;
	}

	
	public abstract Move getNextMove(State newState);
}
