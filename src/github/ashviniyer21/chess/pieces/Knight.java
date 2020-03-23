package github.ashviniyer21.chess.pieces;

import github.ashviniyer21.chess.Main;
import github.ashviniyer21.chess.Side;

import java.io.FileNotFoundException;

public class Knight extends Piece {
    public Knight(Side side, Position position) throws FileNotFoundException {
        super("knight_white.png", "knight_black.png", side, 3, position, PieceType.KNIGHT);
    }

    @Override
    public void move(Position position) {
        int xDiff = position.getX() - getPosition().getX();
        int yDiff = position.getY() - getPosition().getY();
        if(Math.abs(xDiff) == 1 && Math.abs(yDiff) == 2){
            doMove(position);
        } else if(Math.abs(xDiff) == 2 && Math.abs(yDiff) == 1){
            doMove(position);
        }
        updatePosImage();
    }

    @Override
    public void take(Piece piece) {
        int xDiff = piece.getPosition().getX() - getPosition().getX();
        int yDiff = piece.getPosition().getY() - getPosition().getY();
        if(piece.getSide() == getSide()){
            return;
        }
        if(Math.abs(xDiff) == 1 && Math.abs(yDiff) == 2){
            doTake(piece);
        } else if(Math.abs(xDiff) == 2 && Math.abs(yDiff) == 1){
            doTake(piece);
        }
        updatePosImage();
    }
    private void doMove(Position position){
        Position oldPos = new Position(getPosition().getX(), getPosition().getY());
        getPosition().setPos(position);
        if(!Main.isSquareThreat(getSide())){
            Main.changeSide();
        } else {
            getPosition().setPos(oldPos);
        }
    }
    private void doTake(Piece piece){
        Position oldPos = new Position(getPosition().getX(), getPosition().getY());
        getPosition().setPos(piece.getPosition());
        piece.setDead();
        if(!Main.isSquareThreat(getSide())){
            Main.changeSide();
        } else {
            getPosition().setPos(oldPos);
            piece.undoDead();
        }
    }
}
