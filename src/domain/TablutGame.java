package domain;

import java.util.List;

import enums.GameState;
import enums.Loader;

public class TablutGame extends Game {

	
	public TablutGame(List<Player> players){
		super(players);
		
		this.state = new TablutState(new TablutBoard(Loader.JSON, "resources/board.json"));
	}
	
	@Override
	public void loop() {
		
		for (Player p : players )
		{
			long now = System.currentTimeMillis();
			Move nextMove = p.getNextMove(state);

			state.applyMove(nextMove);

			System.out.println("[GAME LOOP] turno "+ p.getKind() + " time elapsed: " + (System.currentTimeMillis() - now));

			System.out.println(state.toString());
			
			
			
			if (checkGameState())
			{
				return;
			}
		}
	}
	
	private boolean checkGameState() {
		
		System.out.println("[GAME LOOP] current GameState " + state.getGameState());
		
		if (state.getGameState() == GameState.WIN)
		{
			System.out.println("You win");
			return true;
		}
		
		else if(state.getGameState() == GameState.LOSE)
		{
			System.out.println("You lost");
			return true;
		}
		
		else if(state.getGameState() == GameState.DRAW)
		{
			System.out.println("Draw");
			return true;
		}
		return false;
	}

}
