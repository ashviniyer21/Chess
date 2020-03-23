package github.ashviniyer21.chess.pieces;

import github.ashviniyer21.chess.Main;
import github.ashviniyer21.chess.Side;
import javafx.geometry.Pos;

import java.io.FileNotFoundException;

public class Queen extends Piece {
    public Queen(Side side, Position position) throws FileNotFoundException {
        super("queen_white.png", "queen_black.png", side, 9, position, PieceType.QUEEN);
    }

    @Override
    public void move(Position position) {
        int xDiff = position.getX() - getPosition().getX();
        int yDiff = position.getY() - getPosition().getY();
        if(xDiff == 0 && yDiff != 0){
            boolean goodToMove = true;
            if(yDiff < 0){
                for(int i = getPosition().getY()-1; i >= position.getY(); i--){
                    if (Main.getPiece(position.getX(), i) != null) {
                        goodToMove = false;
                        break;
                    }
                }
            } else {
                for(int i = getPosition().getY()+1; i <= position.getY(); i++){
                    if (Main.getPiece(position.getX(), i) != null) {
                        goodToMove = false;
                        break;
                    }
                }
            }
            if(goodToMove){
                doMove(position);
            }
        } else if(xDiff != 0 && yDiff == 0){
            boolean goodToMove = true;
            if(xDiff < 0){
                for(int i = getPosition().getX()-1; i >= position.getX(); i--){
                    if (Main.getPiece(i, position.getY()) != null) {
                        goodToMove = false;
                        break;
                    }
                }
            } else {
                for(int i = getPosition().getX()+1; i <= position.getX(); i++){
                    if (Main.getPiece(i, position.getY()) != null) {
                        goodToMove = false;
                        break;
                    }
                }
            }
            if(goodToMove){
                doMove(position);
            }
        } else if(Math.abs(xDiff) == Math.abs(yDiff)){
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
        if(piece.getSide() == getSide()){
            return;
        }
        Position position = piece.getPosition();
        int xDiff = position.getX() - getPosition().getX();
        int yDiff = position.getY() - getPosition().getY();
        if(xDiff == 0 && yDiff != 0){
            boolean goodToMove = true;
            if(yDiff < 0){
                for(int i = getPosition().getY()-1; i > position.getY(); i--){
                    if (Main.getPiece(position.getX(), i) != null) {
                        goodToMove = false;
                        break;
                    }
                }
            } else {
                for(int i = getPosition().getY()+1; i < position.getY(); i++){
                    if (Main.getPiece(position.getX(), i) != null) {
                        goodToMove = false;
                        break;
                    }
                }
            }
            if(goodToMove){
                doTake(piece);
            }
        } else if(xDiff != 0 && yDiff == 0){
            boolean goodToMove = true;
            if(xDiff < 0){
                for(int i = getPosition().getX()-1; i > position.getX(); i--){
                    if (Main.getPiece(i, position.getY()) != null) {
                        goodToMove = false;
                        break;
                    }
                }
            } else {
                for(int i = getPosition().getX()+1; i < position.getX(); i++){
                    if (Main.getPiece(i, position.getY()) != null) {
                        goodToMove = false;
                        break;
                    }
                }
            }
            if(goodToMove){
                doTake(piece);
            }
        } else if(Math.abs(xDiff) == Math.abs(yDiff)){
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
        } else {
            getPosition().setPos(oldPos);
            piece.undoDead();
        }
    }
}