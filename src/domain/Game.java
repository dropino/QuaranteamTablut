package domain;

import java.util.List;

public abstract class Game {
	
	protected List<Player> players;
	protected State state;
	
	
	public Game(List<Player> players) {
		this.players = players;
	}
	
	
	public abstract void loop();

	
	public State getState() {
		return state;
	}

}
