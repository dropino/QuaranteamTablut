package ai;

import java.util.LinkedHashMap;

import domain.Pair;

public class TranspositionTable extends LinkedHashMap<String, Pair<Integer, Integer>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1507589712717292051L;
	//in the Pair Key is the depth, Value is the Value associated to the configuration by ABP
	private static TranspositionTable instance = null;
	private static int MAX_DIM = 4000000;
	
	public static TranspositionTable getInstance() {
		if (instance == null)
			instance = new TranspositionTable();
		return instance;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<String, Pair<Integer, Integer>> eldest) 
    { 
        return size() > MAX_DIM; 
    } 
	
	public void add(String board, int depth, int value) {
		
		if (this.containsKey(board)) {
			int oldDepth = this.get(board).getFirst();
			if (oldDepth < depth)
				this.put(board, new Pair<Integer, Integer>(depth, value));
		}
		else {
			this.put(board, new Pair<Integer, Integer>(depth, value));
		}
	}

	public boolean contains(String board) {
		return this.containsKey(board);
	}

	public Integer getValue(String board, int depth) {
		
		if(this.containsKey(board))
			return this.get(board).getFirst() > depth ? 
					this.get(board).getSecond() 
					: null
					;
			
		return null;
	}
	
}
