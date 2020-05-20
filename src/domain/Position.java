package domain;

public class Position {

	private int x;
	private int y;
	
	public int getX() {
		return x;
	}
	
	
	public void setX(int x) {
		this.x = x;
	}
	
	
	public int getY() {
		return y;
	}
	
	
	public void setY(int y) {
		this.y = y;
	}
	
	
	public Position(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	
	public Position getNextPositionX(int xBound) {
		if (x+1 >= xBound)
		{
			return null;
		}
		return new Position(x+1, y);
	}
	
	
	public Position getNextPositionY(int yBound) {
		if (y+1 >= yBound)
		{
			return null;
		}	
		return new Position(x, y+1);
	}
	
	
	public Position getPreviousPositionX() {
		if (x-1 < 0)
		{
			return null;
		}
		return new Position(x-1, y);
	}
	
	
	public Position getPreviousPositionY() {
		if (y-1 < 0)
		{
			return null;
		}
		return new Position(x, y-1);
	}
	
	
	public Position[] getOrthogonalNeighbors(int xBound, int yBound) {
		Position[] positions = new Position[4];
	
		int idx = 0;
		
		if (getNextPositionX(xBound) != null) positions[idx] = getNextPositionX(xBound); idx++;
		if (getNextPositionY(yBound) != null) positions[idx] = getNextPositionY(yBound); idx++;
		if (getPreviousPositionX() != null) positions[idx] = getPreviousPositionX(); idx++;
		if (getPreviousPositionY() != null) positions[idx] = getPreviousPositionY(); idx++;
		
		return positions;
	}
	
	
	public Position[] getHorizontalNeighbors(int xBound, int yBound) {
		Position[] positions = new Position[2];
	
		int idx = 0;

		if (getNextPositionX(xBound) != null) positions[idx] = getNextPositionX(xBound); idx++;
		if (getPreviousPositionX() != null) positions[idx] = getPreviousPositionX(); idx++;
		
		return positions;
	}
	
	
	public Position[] getVerticalNeighbors(int xBound, int yBound) {
		Position[] positions = new Position[2];
		int idx = 0;
		
		if (getNextPositionY(yBound) != null)  positions[idx] = getNextPositionY(yBound); idx++;
		if (getPreviousPositionY() != null)  positions[idx] = getPreviousPositionY(); idx++;
		
		return positions;
	}
	
	
	@Override
	public String toString() {
		return "[" + this.getX() + ", " + this.getY() + "]";
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x * 2 + y;  
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position incoming = (Position) obj;
		return this.x == incoming.getX() && this.y == incoming.getY();
	}

}
