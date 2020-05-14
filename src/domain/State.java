package domain;

import java.util.List;

import enums.GameState;
import enums.PlayerKind;

public abstract class State implements Cloneable {

	private Board board;

	private PlayerKind turnOf;
	private GameState currentState;

	public State(Board board, PlayerKind startingPlayer) {
		this.board = board;
		turnOf = startingPlayer;
		currentState = GameState.PLAYING;
	}
	
	public PlayerKind getTurnOf() {
		return turnOf;
	}

	public void setTurnOf(PlayerKind turnOf) {
		this.turnOf = turnOf;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}
	
	public GameState getGameState() {
		return currentState;
	}
	
//	public PlayerKind getMyKind() {
//		return myKind;
//	}
	
	public void setGameState(GameState currentState) {
		this.currentState = currentState;
	}
	
	public abstract boolean hasWon(PlayerKind playerKind);

	public abstract List<Position> applyMove(Move nextMove);

	public abstract List<Move> getPossibleMoves();
	public abstract List<Move> getPossibleMoves(PlayerKind playerKind);
	
	@Override
	protected State clone() {
		State newState = null;
		
		try {
			newState = (State) super.clone();
			newState.setBoard((Board) this.getBoard().clone());

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return newState;
	}

	public abstract void undoMove(Move nextMove, List<Position> eaten) ;
	
}
