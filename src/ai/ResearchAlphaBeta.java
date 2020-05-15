package ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.Move;
import domain.Position;
import domain.State;
import enums.GameState;
import enums.PlayerKind;

public class ResearchAlphaBeta {

		
	private Map<Integer, Move> mapMoves;
	private int maxDepth;
	private HeuristicFunction h;
	
	/**
	 * algoritmo minmax con potature alpha-beta a profondità limitata
	 * 
	 * @params maxDepth
	 * 					livelli di profondità da esplorare
	 * @params ts
	 * 				rappresentazione dello stato
	 * @return
	 * 			ritorna la migliore azione 
	 */
	public Move AlphaBetaSearch(HeuristicFunction h, int  maxDepth, State ts) {

		this.h = h;
		mapMoves = new HashMap<Integer, Move>();
		this.maxDepth = maxDepth;
		
		if(ts.getTurnOf().equals(PlayerKind.WHITE)) {	//MAX player
			int v = MaxValue(maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, ts);
			System.gc();
			return mapMoves.get(v);	//si recupera l'azione con il valore v più alto
		}
		else if(ts.getTurnOf().equals(PlayerKind.BLACK)) {	//MIN player
			int v = MinValue(maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, ts);
			System.gc();
			return mapMoves.get(v);	//si recupera l'azione con il valore v più basso
		}			
			return null;
	}
 
		 
		
		
		/**
	 * funzione di valutazione del valore massimo di AlphaBetaSearch
	 * 
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @param state
	 * @return
	 */
	
	//@Matteo manca il confronto sul percorso minimo , forse la mappa non basta , per ora provo a usare depth in cutoff
	private int MaxValue(int depth, int alpha, int beta, State state) {
		//all'interuzione si ritorna un valore 
		if (cutoff(depth, state)) {
			//System.out.println("[MAX] CUTOFF" );
			return h.getStateValue(state) - depth;
		}
		int tmp;
		int v = Integer.MIN_VALUE;

		//System.out.println("[MAX] Depth: " + depth );
		//System.out.println("[MAX] Alpha: " + alpha );
		//System.out.println("[MAX] Beta: " + beta );
		
		List<Move> moves = state.getPossibleMoves(PlayerKind.WHITE);

		//System.out.println("[MAX] Possible Moves: " + moves.size() );
		for (Move m : moves) {
		
			Position[] eaten = state.applyMove(m);
			
			tmp=MinValue(depth - 1, alpha, beta, state);
		
			state.undoMove(m, eaten);
			
			//tmp strettamente maggiore di v altirmenti inserisce nella mappa anche i valori infiniti
			if(this.maxDepth == depth && tmp > v) {
				this.mapMoves.put(tmp, m);					
				//ci si salva in mappa le coppie <valore, mossa> del primo livello di profondità
			}


			v = Math.max(v, tmp);
			if (v >= beta) {
				//System.out.println("[MAX] Return Value: " + v );
				//System.out.println("[MAX] Time elapsed: " + (System.currentTimeMillis() - now) );
				return v;
			}

			alpha = Math.max(alpha, v);
		}
		
		//System.out.println("[MAX] Return Value: " + v );
		//System.out.println("[MAX] Time elapsed: " + (System.currentTimeMillis() - now) );
		return v;
	

	}

	/**
	 * funzione di valutazione del valore minimo di AlphaBetaSearch
	 * 
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @param state
	 * @return
	 */
	private int MinValue(int depth, int alpha, int beta, State state) {
		//all'interuzione si ritorna un valore 
		if (cutoff(depth, state)) {
			//System.out.println("[MIN] CUTOFF" );
			return h.getStateValue(state) + depth;
		}
		int tmp;
		int v = Integer.MAX_VALUE;

		//System.out.println("[MIN] Depth: " + depth );
		//System.out.println("[MIN] Alpha: " + alpha );
		//System.out.println("[MIN] Beta: " + beta );
		
		List<Move> moves = state.getPossibleMoves(PlayerKind.BLACK);
		
		//System.out.println("[MIN] Possible Moves: " + moves.size() );
				
		for (Move m : moves) {											
			//create childstate
//			State childState = state.deepCopy();
			Position[] eaten = state.applyMove(m);
			
			tmp=MaxValue(depth - 1, alpha, beta, state);
			
			state.undoMove(m, eaten);
			
			if(this.maxDepth==depth && tmp < v) {
				this.mapMoves.put(tmp, m);	
				//ci si salva in mappa le coppie <valore, mossa> del primo livello di profondità
			}
			
			v = Math.min(v,tmp);
			if (v <= alpha) {
				//System.out.println("[MIN] Return Value: " + v );
				//System.out.println("[MIN] Time elapsed: " + (System.currentTimeMillis() - now) );
				return v;
			}
			beta = Math.min(beta, v);
		}
		//System.out.println("[MIN] Return Value: " + v );
		//System.out.println("[MIN] Time elapsed: " + (System.currentTimeMillis() - now) );
		return v;

	}
	
	/**
	 * funzione per fermarsi nella ricerca in profondità di AlphaBetaSearch
	 * 
	 * @param depth
	 * @param state
	 * @return
	 */
	private boolean cutoff(int depth, State state) {
		//ci si blocca quando si raggiunge una certa profondità o si è in un nodo
		//foglia -> quindi si è determinato una vittoria o sconfitta o pareggio
			return depth <= 0 
					|| (state.getGameState() != GameState.PLAYING);
		}
	
}
