package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import enums.GameState;
import enums.Pawn;
import enums.PlayerKind;
import enums.Tile;

public class TablutState extends State {

	private Stack<String> boardHistory;
	String currentBoard;
		
	public TablutState(Board board) {
		super(board, PlayerKind.WHITE);
		setCurrentBoard();
		boardHistory = new Stack<String>();
		this.setGameState(GameState.PLAYING);
	}
	
	@Override
	public List<Move> getPossibleMoves(PlayerKind playerKind) {
		List<Move> result = new ArrayList<Move>();
		TablutBoard board = (TablutBoard)getBoard();
		int dimX = board.getDimX();
		int dimY = board.getDimY();
		
		for (int x = 0; x < dimX; x++)
		{
			for (int y = 0; y < dimY; y++)
			{
				//per evitare di avere due metodi che fanno la stessa cosa, controllo di chi devo controllare le mosse in questo if
				if(  (  playerKind == PlayerKind.WHITE 
						&& (board.getPawn(x, y) == Pawn.WHITE || board.getPawn(x, y) == Pawn.KING)
						)
						|| (playerKind == PlayerKind.BLACK && board.getPawn(x, y) == Pawn.BLACK)   )
				{
					Position current = new Position(x, y);
					
					for (int prevX = x-1; prevX >= 0; prevX--) {
						//celle precedenti
						//in orizzontale...
						if ( board.getPawn(prevX, y) == Pawn.EMPTY 
							&&( (board.getBlackPawnsInCentralCitadels().containsKey(current)
									&& !board.getBlackPawnsInCentralCitadels().get(current)) 
								|| board.getTile(prevX, y) != Tile.CAMP )
							&& board.getTile(prevX, y) != Tile.CASTLE )
						{
							result.add(new Move(x, y, prevX, y));
						}
						else break;
					}
					
					for (int prevY = y-1; prevY >= 0; prevY--) {
						if (board.getPawn(x, prevY) == Pawn.EMPTY 
							&&( (board.getBlackPawnsInCentralCitadels().containsKey(current)
									&& !board.getBlackPawnsInCentralCitadels().get(current)) 
								|| board.getTile(x, prevY) != Tile.CAMP )
							&& board.getTile(x, prevY) != Tile.CASTLE )
						{
							result.add(new Move(x, y, x, prevY));
						}
						else break;
					}
					
					for (int postX = x+1; postX < dimX; postX++) {
						if (board.getPawn(postX, y) == Pawn.EMPTY 
								&&( (board.getBlackPawnsInCentralCitadels().containsKey(current)
										&& !board.getBlackPawnsInCentralCitadels().get(current)) 
									|| board.getTile(postX, y) != Tile.CAMP )
								&& board.getTile(postX, y) != Tile.CASTLE )
						{
							result.add(new Move(x, y, postX, y));
						}
						else break;
					}
					
					for (int postY = y+1; postY < dimY; postY++) {
						if (board.getPawn(x, postY) == Pawn.EMPTY 
								&&( (board.getBlackPawnsInCentralCitadels().containsKey(current)
										&& !board.getBlackPawnsInCentralCitadels().get(current)) 
									|| board.getTile(x, postY) != Tile.CAMP )
								&& board.getTile(x, postY) != Tile.CASTLE )
						{
							result.add(new Move(x, y, x, postY));
						}
						else break;
					}
				}//if WHITE or KING
			}//for y
			
		}//for x
		return result;
	}
	
	
	public boolean checkRepeatingBoardConfiguration(String current) {
		boardHistory.trimToSize();
		for (int i = 0; i< boardHistory.size(); i++)
		{
			if (boardHistory.get(i).equals(current))
			{
				return true;
			}
		}
		return false;
	}

	
	@Override
	public Position[] applyMove(Move nextMove) {
	
		Position[] eaten = getBoard().applyMove(nextMove);
		boardHistory.push(currentBoard);
		setCurrentBoard();
		
		updateGameState();
				
		this.setTurnOf(getTurnOf() == PlayerKind.WHITE ? PlayerKind.BLACK : PlayerKind.WHITE);
		return eaten;	
	}

	
	private void updateGameState() {
		TablutBoard tb = (TablutBoard) getBoard();
		boolean kingCaptured = isKingCaptured();
		boolean kingEscaped = tb.isKingOnEscapeTile();
		
		if ( (kingCaptured && this.getTurnOf() == PlayerKind.BLACK)
			|| (kingEscaped && this.getTurnOf() == PlayerKind.WHITE) )
		{
			this.setGameState(GameState.WIN);
		}
		else if ( (kingCaptured && this.getTurnOf() == PlayerKind.WHITE)
				|| (kingEscaped && this.getTurnOf() == PlayerKind.BLACK) 
				|| getPossibleMoves(getTurnOf()).size() == 0)
		{
			this.setGameState(GameState.LOSE);
		}
		else if (checkRepeatingBoardConfiguration(currentBoard))
		{
			this.setGameState(GameState.DRAW);
		}
			
	}
	
	
	private boolean isKingCaptured() {
		TablutBoard tb = (TablutBoard) getBoard();
		int kingSurrounded = tb.countKingSurrounded();
		if ( (tb.getTile(tb.getKingPosition()) == Tile.CASTLE && kingSurrounded == 4)
				|| (tb.isKingAdiacentToCastle() && kingSurrounded == 3)
				|| (	this.getTurnOf() == PlayerKind.BLACK 
						&& ( tb.surroundedOnX(tb.getKingPosition()) 
							|| tb.surroundedOnY(tb.getKingPosition())
							|| tb.surroundedAdiacentToCitadel(tb.getKingPosition()) )	) 
				)
		{
			return true;
		}
		
		return false;
	}

	
	@Override
	public List<Move> getPossibleMoves() {
		return getPossibleMoves(getTurnOf());
	}

	
	@Override
	public boolean hasWon(PlayerKind playerKind) {
		switch(playerKind) {
		case WHITE:
			if ( (this.getGameState() == GameState.WIN && this.getTurnOf() == PlayerKind.BLACK)
					|| (this.getGameState() == GameState.LOSE && this.getTurnOf() == PlayerKind.WHITE) )
			{
				return true;
			}
			return false;
		case BLACK:
			if ( (this.getGameState() == GameState.WIN && this.getTurnOf() == PlayerKind.WHITE)
					|| (this.getGameState() == GameState.LOSE && this.getTurnOf() == PlayerKind.BLACK) )
			{
				return true;
			}
			return false;
		}
		return false;
	}
	
	
	public String toString() {
		return currentBoard;
	}

	
	private void setCurrentBoard() {
		currentBoard = this.getTurnOf() + "\n" + this.getBoard().toString();
	}
	
	
	@Override
	public void undoMove(Move nextMove, Position[] eaten) {
		
		boardHistory.pop();
		getBoard().undoMove(nextMove, eaten);
				
		this.setGameState(GameState.PLAYING);
		
		this.setTurnOf(getTurnOf() == PlayerKind.WHITE ? PlayerKind.BLACK : PlayerKind.WHITE);
		setCurrentBoard();	
	}

	
	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public void fromJson(String jsonString) {
		// 		EMPTY("O"), WHITE("W"), BLACK("B"), THRONE("T"), KING("K");

		JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
		System.out.println("[TS] reading state: " + obj.get("turn").toString());

		switch(obj.get("turn").toString()) {
			case "\"WHITE\"": 
				this.setTurnOf(PlayerKind.WHITE);
				break;
			case "\"BLACK\"": 
				this.setTurnOf(PlayerKind.BLACK);
				break;
			case "\"BLACKWIN\"": 
				if (getTurnOf()==PlayerKind.WHITE)
					this.setGameState(GameState.LOSE);
				if (getTurnOf()==PlayerKind.BLACK)
					this.setGameState(GameState.WIN);
				break;
			case "\"WHITEWIN\"": 
				if (getTurnOf()==PlayerKind.WHITE)
					this.setGameState(GameState.WIN);
				if (getTurnOf()==PlayerKind.BLACK)
					this.setGameState(GameState.LOSE);
				break;			
			case "\"DRAW\"": 
				this.setGameState(GameState.DRAW);
				break;
		}
		
		System.out.println("[TS] reading board...");
		this.getBoard().fromJson(obj);
		if (!currentBoard.equals(getBoard().toString()))
		{
			this.setCurrentBoard();
			this.boardHistory.add(currentBoard);
		}
		System.out.println("[TS] DONE.");
	}

	
	@Override
	public void fromJson(JsonObject jsonObj) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public JsonObject toJsonObject() {
		// TODO Auto-generated method stub
		return null;
	}
}
