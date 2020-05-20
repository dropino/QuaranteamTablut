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
    	int idx = 0;
    	try {
    			rand.nextInt(moveList.size());
    	} catch(IllegalArgumentException e) {
    		
    	}
    	idx = Math.abs(idx);
    	Move toApply = moveList.get(idx%moveList.size());
    	PlayerKind currentPlayer = this.getTurnOf();
    	
    	applyMove(toApply);
		return new Pair<GameState, PlayerKind>(this.getGameState(), currentPlayer);	
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
