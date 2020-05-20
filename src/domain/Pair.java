package domain;

public class Pair<F, S> {
	private F first;
	private S second;
	
	public F getFirst() {
		return first;
	}
	
	public void setFirst(F first) {
		this.first = first;
	}
	
	public S getSecond() {
		return second;
	}
	
	public void setValue(S second) {
		this.second = second;
	}
	
	public Pair(F first, S second) {
		super();
		this.first = first;
		this.second = second;
	}
	
}
