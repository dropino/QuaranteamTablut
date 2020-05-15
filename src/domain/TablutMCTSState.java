package domain;

import java.util.List;
import java.util.Random;

import enums.GameState;
import enums.PlayerKind;

public class TablutMCTSState extends TablutState {
    
    //COSTRUTTORI
	public TablutMCTSState(State newState) {
		super(newState.getBoard().clone());
		this.setGameState(newState.getGameState());
		this.setTurnOf(newState.getTurnOf());
	}
        
    public Pair<GameState, PlayerKind> randomPlay() {
   
    	
    	List<Move> moveList = getPossibleMoves();
    	
    	if (moveList.size() < 0)
    	{
    		System.out.println("moveList size NEGATIVE");
    	}
    	
    	Random rand = new Random(); 
    	Move toApply = moveList.get(Math.abs(rand.nextInt(moveList.size())));
    	
    	applyMove(toApply);
//    	Pair<GameState, PlayerKind> result = randomPlay();
    	
//		undoMove(toApply, eaten);
		
//    	if (this.getGameState() != GameState.PLAYING)
//    	{
    		return new Pair<GameState, PlayerKind>(this.getGameState(), this.getTurnOf());
//    	} 	
   }


	public PlayerKind getOpponent() {
		if(getTurnOf() == PlayerKind.BLACK) return PlayerKind.WHITE;
		else return PlayerKind.BLACK;
	}
	
	public TablutMCTSState deepCopy() {
		return this.clone();
	}
	
	@Override
	protected TablutMCTSState clone() {
		TablutMCTSState newState = null;
		
		try {
			newState = (TablutMCTSState) super.clone();
			newState.setBoard((Board) this.getBoard().clone());
			switch(this.getTurnOf())
			{
			case WHITE:
				newState.setTurnOf(PlayerKind.WHITE);	
				break;
			case BLACK:
				newState.setTurnOf(PlayerKind.BLACK);	
				break;
			}
			
			switch (this.getGameState())
			{
			case PLAYING:
				newState.setGameState(GameState.PLAYING);
				break;
			case WIN:
				newState.setGameState(GameState.WIN);
				break;
			case LOSE:
				newState.setGameState(GameState.LOSE);
				break;
			case DRAW:
				newState.setGameState(GameState.DRAW);
				break;
			default:
				break;
				
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return newState;
	}

}
