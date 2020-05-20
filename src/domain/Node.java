package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import enums.PlayerKind;

public class Node {
	private String state;
	private Node parent;
    private List<Node> childArray;
    private Move usedMove;   
    private int winScore;
    private int visitNumber;
	private PlayerKind player;
    
    public Node() {
    	winScore = 0;
    	visitNumber = 0;
    	childArray = new ArrayList<Node>();
    }
    
    public Node(String state, PlayerKind kind) {
    	this.state = state;
    	winScore = 0;
    	visitNumber = 0;
    	childArray = new ArrayList<Node>();
    	player = kind;
    }
    
    public Node(Node node) {
    	this.state = node.getState();
    	this.parent = node.getParent();
    	this.childArray = node.getChildArray();
    	winScore = 0;
    	visitNumber = 0;
    }

	// setters and getters
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public List<Node> getChildArray() {
		return childArray;
	}
	public void setChildArray(List<Node> childArray) {
		this.childArray = childArray;
	}

	public Node getRandomChildNode() {
    	Random rand = new Random(); 
    	return childArray.get(rand.nextInt(childArray.size())); 
	}

	public Node getChildWithMaxScore() {
		double max = 0;
		Node maxNode = new Node();
		for(Node n : getChildArray()) {
			if (n.getWinScore() >= max) {
				max = n.getWinScore();
				maxNode = n;
			}
		}
		return maxNode;
	}

	public void saveMoveUsed(Move usedMove) {
		this.setUsedMove(usedMove);
	}

	public Move getUsedMove() {
		return usedMove;
	}

	public void setUsedMove(Move usedMove) {
		this.usedMove = usedMove;
	}

	public void setWinScore(int value) {
		this.winScore = value;
	}
	
	public void incrementWinScore(int value) {
		this.winScore += value;
	}
	
	public void incrementVisit() {
		this.visitNumber++;
	}

	public int getWinScore() {
		return winScore;
	}

	public int getVisitNumber() {
		return visitNumber;
	}
	
    public PlayerKind getPlayer() {
		return player;
	}

	public void setPlayer(PlayerKind turnOf) {

		this.player = turnOf;		
	}
	
}