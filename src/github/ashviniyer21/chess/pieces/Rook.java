package github.ashviniyer21.chess.pieces;

import github.ashviniyer21.chess.Main;
import github.ashviniyer21.chess.Side;

import java.io.FileNotFoundException;

public class Rook extends Piece {
    private boolean hasMoved;
    public Rook(Side side, Position position) throws FileNotFoundException {
        super("rook_white.png", "rook_black.png", side, 5, position, PieceType.ROOK);
        hasMoved = false;
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
        }
        updatePosImage();
    }
    @Override
    public boolean hasMoved(){
        return hasMoved;
    }
    private void doMove(Position position){
        Position oldPos = new Position(getPosition().getX(), getPosition().getY());
        getPosition().setPos(position);
        if(!Main.isSquareThreat(getSide())){
            Main.changeSide();
            hasMoved = true;
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
            hasMoved = true;
        } else {
            getPosition().setPos(oldPos);
            piece.undoDead();
        }
    }
}
