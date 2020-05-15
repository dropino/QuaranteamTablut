package domain;

import enums.PlayerKind;
import ai.HeuristicTablut;
import ai.ResearchAlphaBeta;
import ai.ResearchPVS;
import aiMCTS.MCTSearch;

public class AiPlayer extends Player {

	ResearchAlphaBeta ab = new ResearchAlphaBeta();
	ResearchPVS pvs = new ResearchPVS();
	MCTSearch mtcs = new MCTSearch();

	public AiPlayer(String id, PlayerKind kind) {
		super(id, kind);
	}

	@Override
	public Move getNextMove(State newState) {	
<<<<<<< HEAD
//		return ab.AlphaBetaSearch(new HeuristicTablut(), 7, newState);
=======
		return ab.AlphaBetaSearch(new HeuristicTablut(), 5, newState);
>>>>>>> 5e5aab05aca85664bf67895f306d5b1ee5cc8146
//		return pvs.pvSearch(newState, Integer.MAX_VALUE, Integer.MIN_VALUE, 3);
		TablutMCTSState MCTSState = new TablutMCTSState(newState.getBoard());
		return mtcs.findNextMove(MCTSState);
	}

}
