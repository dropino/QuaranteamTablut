package test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ai.IterativeDeepeningSearch;
import domain.*;
import enums.GameState;
import enums.PlayerKind;

class TestAi {

//	@Test
//	void test_Alpha_Beta_Pruning_time_white() {
//		List<Player> players = new ArrayList<Player>();
//		
//		players.add(new AiPlayer("TheQuaranteam", PlayerKind.WHITE));
//		players.add(new AiPlayer("Adversary", PlayerKind.BLACK));
//		
//		Game game = new TablutGame(players);
//		
//		game.loop();
//		System.out.println("=================END OF TEST 1=================");
//	}
	
//	@Test
//	void test_Alpha_Beta_Pruning_time_black() {
//		List<Player> players = new ArrayList<Player>();
//		
//		players.add(new AiPlayer("TheQuaranteam", PlayerKind.BLACK));
//		players.add(new AiPlayer("Adversary", PlayerKind.WHITE));
//
//		Game game = new TablutGame(players);
//		game.getState().setTurnOf(PlayerKind.BLACK);	
//		
//		game.loop();
//		System.out.println("=================END OF TEST 2=================");
//	}

//	@Test
//	void test_Alpha_Beta_Pruning_time_white() {
//		List<Player> players = new ArrayList<Player>();
//		
//		players.add(new AiPlayer("TheQuaranteam", PlayerKind.WHITE));
//		players.add(new AiPlayer("Adversary", PlayerKind.BLACK));
//		
//		Game game = new TablutGame(players);
//		
//		game.loop();
//	}
//	
//	@Test
//	void test_Alpha_Beta_Pruning_time_black() {
//		List<Player> players = new ArrayList<Player>();
//		
//		players.add(new AiPlayer("TheQuaranteam", PlayerKind.BLACK));
//		players.add(new AiPlayer("Adversary", PlayerKind.WHITE));
//
//		Game game = new TablutGame(players);
//		game.getState().setTurnOf(PlayerKind.BLACK);	
//		
//		game.loop();
//	}
	
//	@Test
//	void test_Alpha_Beta_Pruning_10_turns() {
//		List<Player> players = new ArrayList<Player>();
//		
//		players.add(new AiPlayer("TheQuaranteam", PlayerKind.WHITE));
//		players.add(new AiPlayer("Adversary", PlayerKind.BLACK));
//		
//		Game game = new TablutGame(players);
//		
//		for(int i = 0; i < 10; i++) {
//			game.loop();
//		}
//	}
	
//	@Test
//	void test_MCS_vs_AB() {
//		List<Player> players = new ArrayList<Player>();
//		
//		players.add(new AiPlayer("TheQuaranteam", PlayerKind.WHITE, new MCTSearch()));
//		players.add(new AiPlayer("Adversary", PlayerKind.BLACK, new ResearchAlphaBeta(20, new HeuristicTablut())));
//		
//		Game game = new TablutGame(players);
//		
//		while (game.getState().getGameState() == GameState.PLAYING)	
//		{
//			game.loop();
//		}
//	}
//	
//	@Test
//	void test_AB_vs_MCS_10_turns() {
//		List<Player> players = new ArrayList<Player>();
//		
//		players.add(new AiPlayer("TheQuaranteam", PlayerKind.WHITE, null, new ResearchAlphaBeta(7, new HeuristicTablut())));
//		players.add(new AiPlayer("Adversary", PlayerKind.BLACK, null, new ResearchAlphaBeta(7, new HeuristicTablut())));
//		
//		int ww = 0, bw = 0, d = 0;
////		for (int i = 0; i < 10; i++) {
//			Game game = new TablutGame(players);
//	
//			while (game.getState().getGameState() == GameState.PLAYING)	
//			{
//				game.loop();
//			}
//			
//			if (game.getState().hasWon(PlayerKind.WHITE)) ww++;
//			else if (game.getState().hasWon(PlayerKind.BLACK)) bw++;
//			else d++;
////		}
//		
//		System.out.println("WHITE won: " + ww);
//		System.out.println("BLACK won: " + bw);
//		System.out.println("DRAW: " + d);
//	}
	
	@Test
	void test_IterativeDeepening_turns() {
		List<Player> players = new ArrayList<Player>();
		
		int maxTime = 55000;
		
		Function<Pair<Long, Long>, Boolean> f = pair -> {
			if((pair.getFirst() - pair.getSecond()) >= maxTime) 
				return true; 
			else return false;
		};
		
		players.add(new AiPlayer("TheQuaranteam", PlayerKind.WHITE, null, new IterativeDeepeningSearch(3, 11, f, true)));
		players.add(new AiPlayer("Adversary", PlayerKind.BLACK, null, new IterativeDeepeningSearch(3, 11, f, false)));
//		players.add(new AiPlayer("Adversary", PlayerKind.BLACK, null, new MCTSearch()));

		
//		int ww = 0, bw = 0, d = 0;
//		for (int i = 0; i < 10; i++) {
			Game game = new TablutGame(players);
	
			while (game.getState().getGameState() == GameState.PLAYING)	
			{
				game.loop();
			}
			
//			if (game.getState().hasWon(PlayerKind.WHITE)) ww++;
//			else if (game.getState().hasWon(PlayerKind.BLACK)) bw++;
//			else d++;
//		}
		
//		System.out.println("WHITE won: " + ww);
//		System.out.println("BLACK won: " + bw);
//		System.out.println("DRAW: " + d);
	}
	
	

}
