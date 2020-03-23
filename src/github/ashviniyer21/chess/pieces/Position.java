package github.ashviniyer21.chess.pieces;

public class Position {
	private int x, y;
	public Position(int x, int y){
		setPos(x, y);
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public void setX(int x){
		this.x = x;
		limitPos();
	}
	public void setY(int y){
		this.y = y;
		limitPos();
	}
	public void setPos(int x, int y){
		setX(x);
		setY(y);
	}
	public void setPos(Position pos) {
		setX(pos.getX());
		setY(pos.getY());
	}
	public boolean equals(Position position){
		return getX() == position.getX() && getY() == position.getY();
	}
	public int[] posDifference(Position position){
		return new int[]{position.getX() - getX(), position.getY() - getY()};
	}
	private void limitPos(){
		x = Math.max(Math.min(x, 7), 0);
		y = Math.max(Math.min(y, 7), 0);
	}
}