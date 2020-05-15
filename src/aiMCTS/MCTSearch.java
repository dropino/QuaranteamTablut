package aiMCTS;

import java.util.List;

import domain.Move;
import domain.Pair;
import domain.Position;
import domain.TablutMCTSState;
import enums.GameState;
import enums.PlayerKind;

public class MCTSearch {
   static final int WIN_SCORE = 10;
   static final int END_TIMER = 40000; //60 secondi
   int level;
   PlayerKind opponent;
 
    public Move findNextMove (TablutMCTSState state) {
    	long start= System.currentTimeMillis();
        int count = 0;
        Tree tree = new Tree();
        tree.getRoot().setState(state.toString());
        tree.getRoot().setPlayer(state.getTurnOf());
    	TablutMCTSState stateCopy = state.deepCopy();

        expandNode(tree.getRoot(), stateCopy);
        
        
        while (System.currentTimeMillis() - start < END_TIMER) {
            //selects the most promising child of the root node so that we have to analize less 
        	Node promisingNode = selectPromisingNode(tree.getRoot());
            //promising nodes are only nodes of 0-1 breadth level
            Position[] eaten = stateCopy.applyMove(promisingNode.getUsedMove());
        	
            //if it's not a winning state, we expand the children of the promising node
        	if (stateCopy.hasWon(stateCopy.getTurnOf()) == false) {
                expandNode(promisingNode, stateCopy);
            }
        	
            Node nodeToExplore = promisingNode;
            //if the promising node has been expanded, get a random child
            if (promisingNode.getChildArray().size() > 0) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            
            PlayerKind winner = simulateRandomPlayout(nodeToExplore, stateCopy);
            backPropogation(nodeToExplore, winner);
            stateCopy.undoMove(promisingNode.getUsedMove(), eaten);
            count++;
        }
 

        Node winnerNode = tree.getRoot().getChildWithMaxScore();
        System.out.println("Loops: " + count + " Visited: " + winnerNode.getVisitNumber() + " WinScore: " + winnerNode.getWinScore());

        tree.setRoot(winnerNode);
        return winnerNode.getUsedMove();
    }
   
    
    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.getChildArray().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }
    
    private void expandNode(Node node, TablutMCTSState state) {
        List<Move> possibleMoves = state.getPossibleMoves();
        
        for (Move nextMove : possibleMoves)
        {
        	Position[] eaten = state.applyMove(nextMove);
        	Node newNode = new Node(state.getCurrentBoardString(), state.getTurnOf());
        	state.undoMove(nextMove, eaten);
            newNode.setParent(node);
            newNode.saveMoveUsed(nextMove);
            node.getChildArray().add(newNode);
        }
    }

   
    private PlayerKind simulateRandomPlayout(Node nodeToExplore, TablutMCTSState state) {
    	
    	TablutMCTSState stateCopy = state.deepCopy();
    	
    	Pair<GameState, PlayerKind> boardStatus  = stateCopy.randomPlay();
    			
		while (boardStatus.getKey() == GameState.PLAYING)
		{
			boardStatus = stateCopy.randomPlay();
		}
        
        if (boardStatus.getKey() == GameState.LOSE) {
//        	nodeToExplore.getParent().setWinScore(Integer.MIN_VALUE);
            return boardStatus.getValue() == PlayerKind.WHITE ? PlayerKind.BLACK : PlayerKind.WHITE;
        }
        else if (boardStatus.getKey()  == GameState.WIN){
//        	nodeToExplore.getParent().setWinScore(Integer.MAX_VALUE);
            return boardStatus.getValue();
        }
        else {
        	//TODO: cercare un buon valore per DRAW config
//        	nodeToExplore.getParent().setWinScore(Integer.MAX_VALUE/100);
        	return null;
        }    
    }
    
    private void backPropogation(Node nodeToExplore, PlayerKind playerNo) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.incrementVisit();
            if (tempNode.getPlayer() == playerNo) {
                tempNode.incrementWinScore(WIN_SCORE);
            }
            tempNode = tempNode.getParent();
        }
    }
    
   
    
}
