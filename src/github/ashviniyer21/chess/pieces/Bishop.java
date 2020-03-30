package github.ashviniyer21.chess.pieces;

import github.ashviniyer21.chess.Main;
import github.ashviniyer21.chess.Side;

import java.io.FileNotFoundException;

public class Bishop extends Piece {
    public Bishop(Side side, Position position) throws FileNotFoundException {
        super("bishop_white.png", "bishop_black.png", side, 3, position, PieceType.BISHOP);
    }

    @Override
    public void move(Position position) {
        int xDiff = position.getX() - getPosition().getX();
        int yDiff = position.getY() - getPosition().getY();
        if(Math.abs(xDiff) == Math.abs(yDiff)){
            boolean isGood = true;
            int xMult = 1;
            int yMult = 1;
            if(xDiff < 0){
                xMult = -1;
            }
            if(yDiff < 0){
                yMult = -1;
            }
            for(int i = 1; i <= Math.abs(xDiff); i++){
                if(Main.getPiece(getPosition().getX() + i * xMult, getPosition().getY() + i * yMult) != null){
                    isGood = false;
                    break;
                }
            }
            if(isGood){
                doMove(position);
            }
        }
        updatePosImage();
    }

    @Override
    public void take(Piece piece) {
        Position position = piece.getPosition();
        int xDiff = position.getX() - getPosition().getX();
        int yDiff = position.getY() - getPosition().getY();
        if(piece.getSide() == getSide()){
            return;
        }
        if(Math.abs(xDiff) == Math.abs(yDiff)){
            boolean isGood = true;
            int xMult = 1;
            int yMult = 1;
            if(xDiff < 0){
                xMult = -1;
            }
            if(yDiff < 0){
                yMult = -1;
            }
            for(int i = 1; i < Math.abs(xDiff); i++){
                if(Main.getPiece(getPosition().getX() + i * xMult, getPosition().getY() + i * yMult) != null){
                    isGood = false;
                    break;
                }
            }
            if(isGood){
                doTake(piece);
                piece.setDead();
            }
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
            Main.resetCount();
        } else {
            getPosition().setPos(oldPos);
            piece.undoDead();
        }
    }
}