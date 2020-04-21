package github.ashviniyer21.chess.pieces;

import github.ashviniyer21.chess.Main;
import github.ashviniyer21.chess.Side;

import java.io.FileNotFoundException;

public class Pawn extends Piece {
	private boolean hasMoved = false;
	public Pawn(Side side, Position position) throws FileNotFoundException {
		super("pawn_white.png", "pawn_black.png", side, 1, position, PieceType.PAWN);
	}

	@Override
	public void move(Position position) {
		int xDiff = getPosition().posDifference(position)[0];
		int yDiff = getPosition().posDifference(position)[1];
		if(getSide() == Side.WHITE){
			xDiff *= -1;
			yDiff *= -1;
		}
		int sign = 1;
		if(getSide() == Side.BLACK){
			sign = -1;
		}
		if(xDiff == 0){
			if(yDiff == 1){
				doMove(position);
			} else if(yDiff == 2 && !hasMoved){
				if(Main.getPiece(getPosition().getX(), getPosition().getY() - sign) == null){
					doMove(position);
				}
			}
		} else if(Math.abs(xDiff) == 1 && yDiff == 1){
			if(Main.getPiece(position.getX(), position.getY() + sign) != null && prevMovesLength() > 1
					&& Math.abs(Main.getPiece(position.getX(), position.getY() + sign).getPrevPosition(1).getY() -  getPosition().getY()) == 2
					&& doMove(position)){
				Main.getPiece(position.getX(), position.getY() + sign).setDead();
			}
		}
		updatePosImage();
	}

	@Override
	public void take(Piece piece) {
		int xDiff = getPosition().posDifference(piece.getPosition())[0];
		int yDiff = getPosition().posDifference(piece.getPosition())[1];
		if(getSide() == Side.WHITE){
			xDiff *= -1;
			yDiff *= -1;
		}
		if(yDiff == 1 && Math.abs(xDiff) == 1 && !(piece.isDead())){
			doTake(piece);
		}
		updatePosImage();
	}
	private boolean doMove(Position position){
		Position oldPos = new Position(getPosition().getX(), getPosition().getY());
		getPosition().setPos(position);
		if(!Main.isSquareThreat(getSide())){
			hasMoved = true;
			Main.changeSide();
			return true;
		} else {
			getPosition().setPos(oldPos);
			return false;
		}
	}
	private void doTake(Piece piece){
		Position oldPos = new Position(getPosition().getX(), getPosition().getY());
		getPosition().setPos(piece.getPosition());
		piece.setDead();
		if(!Main.isSquareThreat(getSide())){
			Main.changeSide();
			hasMoved = true;
			Main.resetCount();
		} else {
			getPosition().setPos(oldPos);
			piece.undoDead();
		}
	}

	@Override
	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
}
