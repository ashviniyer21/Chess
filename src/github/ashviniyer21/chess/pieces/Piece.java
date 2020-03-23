package github.ashviniyer21.chess.pieces;

import github.ashviniyer21.chess.Main;
import github.ashviniyer21.chess.Side;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class Piece extends ImageView {

	private Side side;
	private int value;
	private Position position;
	private boolean isDead;
	private PieceType pieceType;
	public Piece(String imageLocationWhite, String imageLocationBlack, Side side, int value, Position position, PieceType pieceType) throws FileNotFoundException {
		super();
		if(side == Side.WHITE){
			setImage(new Image(new FileInputStream(imageLocationWhite)));
		} else {
			setImage(new Image(new FileInputStream(imageLocationBlack)));
		}
		this.pieceType = pieceType;
		this.side = side;
		this.value = value;
		this.position = position;
		updatePosImage();
		setFitHeight(Main.PIECE_SIZE);
		setFitWidth(Main.PIECE_SIZE);
		isDead = false;
	}
	public Side getSide() {
		return side;
	}
	public void setDead(){
		isDead = true;
	}
	public double getValue(){
		return value;
	}
	public Position getPosition(){
		return position;
	}
	public abstract void move(Position position);
	public abstract void take(Piece piece);
	public void updatePosImage(){
		setX(position.getX() * Main.PIECE_SIZE);
		setY(position.getY() * Main.PIECE_SIZE);
	}

	public boolean isDead(){
		return isDead;
	}

	public boolean hasMoved(){
		return true;
	}
	public PieceType getPieceType(){
		return pieceType;
	}
	public void undoDead(){
		isDead = false;
	}
}
