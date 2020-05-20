package domain;

import enums.PlayerKind;
import ai.ResearchAlgorithm;

public class AiPlayer extends Player {

	private int turn;
	private ResearchAlgorithm researchInitial;
	private ResearchAlgorithm research;
	
	
	public ResearchAlgorithm getResearch() {
		return research;
	}
	
	
	public AiPlayer(String id, PlayerKind kind, ResearchAlgorithm researchInitial, ResearchAlgorithm research) {
		super(id, kind);
		turn = 0;
		this.research = research;
		this.researchInitial = researchInitial;
	}

	
	@Override
	public Move getNextMove(State newState) {
		System.gc();

		if (turn < 4 && researchInitial != null)
		{	
			turn++;
			return this.researchInitial.getNextMove(newState);
		}
		return this.research.getNextMove(newState);
	}

}
