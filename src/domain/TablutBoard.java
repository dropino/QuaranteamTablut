package domain;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import enums.Loader;
import enums.Pawn;
import enums.Tile;
import utils.BoardLoader;

public class TablutBoard extends Board {
	
	public static int DIM = 9;
	
	private Position kingPosition = new Position(4, 4);
	Map<Position, Boolean> blackPawnsInCentralCitadels = new HashMap<Position, Boolean>();
	
	private int whitePawns = 8;
	private int blackPawns = 16;
	
	public int getWhitePawnNumber() {
		return whitePawns;
	}
	
	public int getBlackPawnNumber() {
		return blackPawns;
	}
	
	public Position getKingPosition() {
		return kingPosition;
	}

	public Map<Position, Boolean> getBlackPawnsInCentralCitadels() {
		return blackPawnsInCentralCitadels;
	}

	public TablutBoard() {
		super(DIM, DIM);
	}
	
	public TablutBoard(Loader boardLoader, String source) {
		super(DIM, DIM);
		
		initializeBoard(boardLoader, source);
	}
	

	public TablutBoard(Pawn[][] pawnBoard, Tile[][] tileBoard) {
		super(pawnBoard, tileBoard);
		
		initializeBlackPawnsInCentralCitadels();
	}

	private void initializeBoard(Loader boardLoader, String source) {

		BoardLoader loader = BoardLoader.BoardLoaderFactory(boardLoader, source);
		this.setPawnBoard(loader.getPawnBoardSetup());
		this.setTileBoard(loader.getTileBoardSetup());
		
		initializeBlackPawnsInCentralCitadels();
	}
	
	private void initializeBlackPawnsInCentralCitadels() {
		blackPawnsInCentralCitadels.put(new Position(4, 0), false);
		blackPawnsInCentralCitadels.put(new Position(0, 4), false);
		blackPawnsInCentralCitadels.put(new Position(4, 8), false);
		blackPawnsInCentralCitadels.put(new Position(8, 4), false);

	}
	
	@Override
	public Position[] applyMove(Move m) {
		Position[] eaten = getEatenPawns(m);
		
		if(eaten != null)
		{
			for (Position p : eaten)
			{
				if (p != null) removePawn(p);
			}
		}
		
		if (getPawn(m.getStarting()) == Pawn.KING)
			kingPosition = m.getEnding();
		
		if (blackPawnsInCentralCitadels.containsKey(m.getStarting()))
			blackPawnsInCentralCitadels.put(m.getStarting(), true);
		
		super.applyMove(m);
		
		return eaten;
	}
	
	@Override
	public void undoMove(Move m, Position[] eaten) {
		
		Pawn pawnType = getPawn(m.getEnding());
		
		super.removePawn(m.getEnding());
		
		getPawnBoard()[m.getStartX()][m.getStartY()] = pawnType;
		
		if (pawnType == Pawn.KING)
			kingPosition = m.getStarting();
		
		if (blackPawnsInCentralCitadels.containsKey(m.getStarting()))
			blackPawnsInCentralCitadels.put(m.getStarting(), false);
		
		if( eaten == null)
				return;
		
		for (int i = 0; i < eaten.length; i++)
		{
			if (eaten[i] == null)
				break;
			Pawn eatenPawnType = pawnType == Pawn.WHITE || pawnType == Pawn.KING ? Pawn.BLACK : Pawn.WHITE;
			getPawnBoard()[eaten[i].getX()][eaten[i].getY()] = eatenPawnType;
			if (eatenPawnType == Pawn.BLACK) blackPawns++;
			else whitePawns++;
		}
	}
	
	@Override
	public void removePawn(Position position) {
		
		switch(getPawn(position))
		{
			case WHITE:
				whitePawns--;
				break;
			case BLACK:
				blackPawns--;
				break;
			default:
				break;
		}
		
		super.removePawn(position);
	}
	
	public Position[] getEatenPawns(Move move) {
		Pawn moving = getPawnBoard()[move.getStartX()][move.getStartY()];
		
		switch(moving)
		{
		case WHITE:
		case KING:
			return getWhiteEatenPawns(move);
		case BLACK:
			return getBlackEatenPawns(move);
		default:
			break;
		}
		
		return null;
	}
	
	public boolean surroundedAdiacentToCitadel(Position position) {
		Position[] neighborsH = position.getHorizontalNeighbors(DIM, DIM);
		Position[] neighborsV = position.getVerticalNeighbors(DIM, DIM);
		
		return (getPawn(neighborsH[0]) == Pawn.BLACK && getTile(neighborsH[1]) == Tile.CAMP)
				|| (getPawn(neighborsH[1]) == Pawn.BLACK && getTile(neighborsH[0]) == Tile.CAMP)
				|| (getPawn(neighborsV[0]) == Pawn.BLACK && getTile(neighborsV[1]) == Tile.CAMP)
				|| (getPawn(neighborsV[1]) == Pawn.BLACK && getTile(neighborsV[0]) == Tile.CAMP);
	}
	
	public boolean surroundedOnX(Position position) {
		Pawn type = getPawn(position);
		int surround = 0;
		for (Position p : position.getHorizontalNeighbors(DIM, DIM))
		{
			Pawn onSide = getPawn(p);
			if ( (onSide == Pawn.BLACK && (type == Pawn.WHITE || type == Pawn.KING)) 
					|| ((onSide == Pawn.WHITE|| onSide == Pawn.KING) && type == Pawn.BLACK)) {
				surround++;
			}
		}
		
		return surround == 2;
	}
	
	public boolean surroundedOnY(Position position) {
		Pawn type = getPawn(position);
		int surround = 0;
		for (Position p : position.getVerticalNeighbors(DIM, DIM))
		{
			Pawn onSide = getPawn(p);
			if ( (onSide == Pawn.BLACK  && (type == Pawn.WHITE || type == Pawn.KING)) 
					|| ((onSide == Pawn.WHITE|| onSide == Pawn.KING) && type == Pawn.BLACK)) {
				surround++;
			}
		}
		
		return surround == 2;
	}

	public boolean isKingOnEscapeTile() {
		if (getTile(kingPosition) == Tile.ESCAPE)
		{
			return true;
		}
		return false;
	}
	
	public int countKingSurrounded() {
		int result = 0;
		
		for(Position p : kingPosition.getOrthogonalNeighbors(DIM, DIM))
		{
			if (getPawn(p) == Pawn.BLACK)
			{
				result++;
			}
		}
		
		return result;
	}
	
	public boolean isKingAdiacentToCastle() {
		for(Position p : kingPosition.getOrthogonalNeighbors(DIM, DIM))
		{
			if (getTile(p) == Tile.CASTLE)
			{
				return true;
			}
		}
		
		return false;
	}

	private Position[] getWhiteEatenPawns(Move move)
	{
		Position[] eatenPawns = new Position[4];
		Position endingPosition = move.getEnding();		
		
		int idx = 0;
		
		if (getPawn(endingPosition.getNextPositionX(DIM)) == Pawn.BLACK 
				&& (getPawn(endingPosition.getNextPositionX(DIM).getNextPositionX(DIM)) == Pawn.WHITE
					|| (getTile(endingPosition.getNextPositionX(DIM).getNextPositionX(DIM)) == Tile.CAMP
						&& getTile(endingPosition.getNextPositionX(DIM)) == Tile.EMPTY)
					|| getPawn(endingPosition.getNextPositionX(DIM).getNextPositionX(DIM)) == Pawn.KING
					|| getTile(endingPosition.getNextPositionX(DIM).getNextPositionX(DIM)) == Tile.CASTLE) )
		{
			eatenPawns[idx] = endingPosition.getNextPositionX(DIM);
			idx++;
		}
		
		if (getPawn(endingPosition.getPreviousPositionX()) == Pawn.BLACK 
				&& (getPawn(endingPosition.getPreviousPositionX().getPreviousPositionX()) == Pawn.WHITE
				|| (getTile(endingPosition.getPreviousPositionX().getPreviousPositionX()) == Tile.CAMP
						&& getTile(endingPosition.getPreviousPositionX()) == Tile.EMPTY)
				|| getPawn(endingPosition.getPreviousPositionX().getPreviousPositionX()) == Pawn.KING
				|| getTile(endingPosition.getPreviousPositionX().getPreviousPositionX()) == Tile.CASTLE) )
		{
			eatenPawns[idx] = endingPosition.getPreviousPositionX();
			idx++;
		}
		
		if (getPawn(endingPosition.getNextPositionY(DIM)) == Pawn.BLACK 
				&& (getPawn(endingPosition.getNextPositionY(DIM).getNextPositionY(DIM)) == Pawn.WHITE 
					|| (getTile(endingPosition.getNextPositionY(DIM).getNextPositionY(DIM)) == Tile.CAMP
						&& getTile(endingPosition.getNextPositionY(DIM)) == Tile.EMPTY)
				|| getPawn(endingPosition.getNextPositionY(DIM).getNextPositionY(DIM)) == Pawn.KING
				|| getTile(endingPosition.getNextPositionY(DIM).getNextPositionY(DIM)) == Tile.CASTLE) )
		{
			eatenPawns[idx] = endingPosition.getNextPositionY(DIM);
			idx++;
		}
		
		if (getPawn(endingPosition.getPreviousPositionY()) == Pawn.BLACK 
				&& (getPawn(endingPosition.getPreviousPositionY().getPreviousPositionY()) == Pawn.WHITE 
						|| (getTile(endingPosition.getPreviousPositionY().getPreviousPositionY()) == Tile.CAMP
						&& getTile(endingPosition.getPreviousPositionY()) == Tile.EMPTY)
				|| getPawn(endingPosition.getPreviousPositionY().getPreviousPositionY()) == Pawn.KING
				|| getTile(endingPosition.getPreviousPositionY().getPreviousPositionY()) == Tile.CASTLE) )
		{
			eatenPawns[idx] = endingPosition.getPreviousPositionY();
			idx++;
		}
		
		return idx == 0 ? null : eatenPawns;
	}
	
	
	private Position[] getBlackEatenPawns(Move move) {
		Position[] eatenPawns = new Position[4];
		Position endingPosition = move.getEnding();

		int idx = 0;
		
		if ( getPawn(endingPosition.getNextPositionX(DIM)) == Pawn.WHITE 
				&& (getPawn(endingPosition.getNextPositionX(DIM).getNextPositionX(DIM)) == Pawn.BLACK 
						|| (getTile(endingPosition.getNextPositionX(DIM).getNextPositionX(DIM)) == Tile.CAMP
						&& getTile(endingPosition.getNextPositionX(DIM)) == Tile.EMPTY)
				|| getTile(endingPosition.getNextPositionX(DIM).getNextPositionX(DIM)) == Tile.CASTLE) )
		{
			eatenPawns[idx] = endingPosition.getNextPositionX(DIM);
			idx++;
		}
		
		if (getPawn(endingPosition.getPreviousPositionX()) == Pawn.WHITE 
				&& (getPawn(endingPosition.getPreviousPositionX().getPreviousPositionX()) == Pawn.BLACK 
						|| (getTile(endingPosition.getPreviousPositionX().getPreviousPositionX()) == Tile.CAMP
						&& getTile(endingPosition.getPreviousPositionX()) == Tile.EMPTY)
				|| getTile(endingPosition.getPreviousPositionX().getPreviousPositionX()) == Tile.CASTLE) )
		{
			eatenPawns[idx] = endingPosition.getPreviousPositionX();
			idx++;
		}
		
		if (getPawn(endingPosition.getNextPositionY(DIM)) == Pawn.WHITE 
				&& (getPawn(endingPosition.getNextPositionY(DIM).getNextPositionY(DIM)) == Pawn.BLACK 
						|| (getTile(endingPosition.getNextPositionY(DIM).getNextPositionY(DIM)) == Tile.CAMP
						&& getTile(endingPosition.getNextPositionY(DIM)) == Tile.EMPTY)
				|| getTile(endingPosition.getNextPositionY(DIM).getNextPositionY(DIM)) == Tile.CASTLE) )
		{
			eatenPawns[idx] = endingPosition.getNextPositionY(DIM);
			idx++;
		}
		
		if (getPawn(endingPosition.getPreviousPositionY()) == Pawn.WHITE 
				&& (getPawn(endingPosition.getPreviousPositionY().getPreviousPositionY()) == Pawn.BLACK 
					|| (getTile(endingPosition.getPreviousPositionY().getPreviousPositionY()) == Tile.CAMP
					&& getTile(endingPosition.getPreviousPositionY()) == Tile.EMPTY)
				|| getTile(endingPosition.getPreviousPositionY().getPreviousPositionY()) == Tile.CASTLE) )
		{
			eatenPawns[idx] = endingPosition.getPreviousPositionY();
			idx++;
		}
		
		return idx == 0 ? null : eatenPawns;
	}
	
	public Pair<Integer, Integer> getPawnCount() {

			return new Pair<Integer, Integer>(whitePawns, blackPawns);
	}
	
	@Override
	public int getPawnCount(Pawn pawnType) {

		if (pawnType == Pawn.BLACK)
			return blackPawns;
		
		if (pawnType == Pawn.WHITE)
			return whitePawns;
		
		return -1;
	}
	
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\t0\t1\t2\t3\t4\t5\t6\t7\t8\n");
		
		for (int x = 0; x < 9; x++)
		{
			builder.append(x);
			builder.append("\t");
			for (int y = 0; y < 9; y++)
			{
				builder.append(getPawn(x, y));
				builder.append("\t");
			}
			builder.append("\n");
		}
				
		
		
		return builder.toString();
		
	}
	
    public Pair<Integer, Integer> getKingHalfBoardPawns() {

        int whitePawns = 0;
        int blackPawns = 0;
        
        if (getKingPosition().getX() < 4) {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 9; j++) {
                    if (getPawn(i, j).equals(Pawn.WHITE))
                        whitePawns++;
                    else if (getPawn(i, j).equals(Pawn.BLACK))
                        blackPawns++;
                }
        } else if (getKingPosition().getX() > 4) {
            for (int i = 5; i < 9; i++)
                for (int j = 0; j < 9; j++) {
                    if (getPawn(i, j).equals(Pawn.WHITE))
                        whitePawns++;
                    else if (getPawn(i, j).equals(Pawn.BLACK))
                        blackPawns++;
                }

        }
        if (getKingPosition().getY() < 4) {
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 4; j++) {
                    if (getPawn(i, j).equals(Pawn.WHITE))
                        whitePawns++;
                    else if (getPawn(i, j).equals(Pawn.BLACK))
                        blackPawns++;
                }
        } else if (getKingPosition().getY() > 4) {
            for (int i = 0; i < 9; i++)
                for (int j = 5; j < 9; j++) {
                    if (getPawn(i, j).equals(Pawn.WHITE))
                        whitePawns++;
                    else if (getPawn(i, j).equals(Pawn.BLACK))
                        blackPawns++;
                }

        }
        return new Pair<Integer, Integer>(whitePawns, blackPawns);
    }	
	
	public boolean isKingInDanger() {

		Position kingpos = getKingPosition();
		int kingY = kingpos.getY();
		int kingX = kingpos.getX();
		int possibileCattura = countKingSurrounded();

		if (kingX == 4) {
			if (kingY == 4 && possibileCattura == 3) return true; // trono
			else if (kingY == 3 && possibileCattura == 2) return true; //adiacente al trono
			else if (kingY == 5 && possibileCattura == 2) return true;
		}
		else if (kingY == 4) {
			if ((kingX == 3 || kingX == 5) && possibileCattura == 2) return true;//adiacente al trono
		} 
		else if (kingX == 2 && kingY == 4) return true; //accampamento
		else if (kingX == 4 && ( kingY == 2 || kingY == 6)) return true;//accampamento
		else if (kingX == 6 && kingY == 4) return true;//accampamento

		else if (possibileCattura == 1) return true;//altro

		else if ((kingX == 5 || kingX == 3) && (kingY == 1 || kingY == 7)) return true;
		else if ((kingX == 1 || kingX == 7) && (kingY == 5 || kingY == 3)) return true;

		return false;

	}

	
	public boolean isKingReadyToWin() {
		Position kingpos = getKingPosition();
		int kingY = kingpos.getY();
		int kingX = kingpos.getX();
		
		int x, y;
		//se il percorso in orizzontale � libero e non c'� alcun accampamento
		for (x = kingX  + 1; x < getDimX(); x++)
			if (!getPawn(x, kingY).equals(Pawn.EMPTY) || (getTile(x, kingY) == Tile.CAMP)) break;
		if (x == getDimX()) return true;
		
		for (x = kingX - 1; x >= 0; x--)
			if (!getPawn(x, kingY).equals(Pawn.EMPTY) || ((getTile(x, kingY)==Tile.CAMP))) break;
		if (x < 0) return true;
		
		//se il percorso in verticale � libero e non c'� alcun accampamento
		for (y = kingY + 1; y < getDimY(); y++)
			if (!getPawn(kingX, y).equals(Pawn.EMPTY) || (getTile(kingX, y)==Tile.CAMP)) break;
		if (y == getDimX()) return true;
		
		for (y = kingY - 1; y >= 0; y--)
			if (!getPawn(kingX, y).equals(Pawn.EMPTY) || (getTile(kingX, y)==Tile.CAMP)) break;
		if (y < 0) return true;
		
		//altrimenti non � pronto a vincere	
		return false;
	}
	
	public Pair<Integer, Integer> pawnsBlockingKingVictory() {
		int kingY = kingPosition.getY();
		int kingX = kingPosition.getX();
		
		if ( kingX < 5 && kingX > 3 && kingY < 5 && kingY > 3 )
			return null; //because the camps are the ones blocking its path
		
		int blacks = 0;
		int whites = 0;
		
		//controllo mosse possibili in un unico ciclo
		for (int prevX = kingX-1, prevY = kingY-1, postX = kingX+1, postY = kingY+1;
				prevX >= 0 || prevY >= 0 || postX < DIM || postY < DIM; 
				prevX--, prevY--, postX++, postY++)
		{
			//if king is on Y=7 the citadel blocks previous Xs
			if (prevX >= 0 && kingY != 7 && !getPawn(prevX, kingY).equals(Pawn.EMPTY)) 
				if (getPawn(prevX, kingY) == Pawn.BLACK) blacks++;
				else whites++;
			if (postX < DIM &&  kingY != 1 && !getPawn(postX, kingY).equals(Pawn.EMPTY)) 
				if (getPawn(postX, kingY) == Pawn.BLACK) blacks++;
				else whites++;
			if (prevY >= 0 && kingX != 7 && !getPawn(kingX, prevY).equals(Pawn.EMPTY))
				if (getPawn(kingX, prevY) == Pawn.BLACK) blacks++;
				else whites++;
			if (postY < DIM && kingX != 1 && !getPawn(kingX, postY).equals(Pawn.EMPTY)) 
				if (getPawn(kingX, postY) == Pawn.BLACK) blacks++;
				else whites++;

		}
		
		return new Pair<Integer, Integer>(whites, blacks);
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fromJson(String jsonString) {
		// TODO Auto-generated method stub
	}

	@Override
	public void fromJson(JsonObject obj) {
		JsonArray jarr = obj.getAsJsonArray("board");
		int x = 0;
		int y = 0;
		for (JsonElement jx : jarr){
			JsonArray jArr2 = jx.getAsJsonArray();
//			System.out.println("[TB] Analizing incoming board array " 
//			+ x + ": " + jArr2.toString());
			y = 0;
			for(JsonElement jy : jArr2) {
				//System.out.println("[TB] Pawn in " + x + ", " + y  + " before: " + getPawn(x, y));
				switch(jy.toString()) {
				//		EMPTY("O"), WHITE("W"), BLACK("B"), THRONE("T"), KING("K");
					case "\"EMPTY\"": 
						this.getPawnBoard()[x][y] = Pawn.EMPTY;
						break;
					case "\"WHITE\"": 
						this.getPawnBoard()[x][y] = Pawn.WHITE;
						break;
					case "\"BLACK\"": 
						this.getPawnBoard()[x][y] = Pawn.BLACK;
						break;
					case "\"THRONE\"": 
						this.getPawnBoard()[x][y] = Pawn.EMPTY;
						break;
					case "\"KING\"":
						this.getPawnBoard()[x][y] = Pawn.KING;
						this.getKingPosition().setX(x);
						this.getKingPosition().setY(y);
						break;
					default:
						System.out.println("key " + jy.toString()+ " not found in board");
				}
//				System.out.println("[TB] Pawn in " + x + ", " + y  + " after: " + getPawn(x, y));
				y++;
			}
			x++;
		}
	}

	@Override
	public JsonObject toJsonObject() {
		// TODO Auto-generated method stub
		return null;
	}
}