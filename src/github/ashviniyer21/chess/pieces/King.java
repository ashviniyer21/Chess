package github.ashviniyer21.chess.pieces;

import github.ashviniyer21.chess.Main;
import github.ashviniyer21.chess.Side;

import java.io.FileNotFoundException;

public class King extends Piece {
    private boolean hasMoved;
    public King(Side side, Position position) throws FileNotFoundException {
        super("king_white.png", "king_black.png", side, 0, position, PieceType.KING);
        hasMoved = false;
    }

    @Override
    public void move(Position position) {
        int xDiff = position.getX() - getPosition().getX();
        int yDiff = position.getY() - getPosition().getY();
        int isle = 0;
        if(getSide() == Side.WHITE){
            isle = 7;
        }
        if(Main.isSquareThreat(position.getX(), position.getY(), getSide())){
            System.out.println("HERE");
            return;
        }
        if(xDiff != 0 || yDiff != 0){
            if(Math.abs(xDiff) <= 1 && Math.abs(yDiff) <= 1){
                getPosition().setPos(position);
                hasMoved = true;
                Main.changeSide();

            } else if(xDiff == 2){

                if(Main.getPiece(getPosition().getX() + 3, isle) != null
                        && !Main.getPiece(getPosition().getX() + 3, isle).hasMoved()
                        && Main.getPiece(getPosition().getX() + 2, isle) == null
                        && Main.getPiece(getPosition().getX() + 1, isle) == null
                ){
                    Main.getPiece(getPosition().getX() + 3, isle).getPosition().setPos(getPosition().getX() + 1, isle);
                    Main.changeSide();
                    getPosition().setPos(position);
                    hasMoved = true;
                }
            } else if(xDiff == -2){
                if(Main.getPiece(getPosition().getX() - 4, isle) != null
                        && !Main.getPiece(getPosition().getX() - 4, isle).hasMoved()
                        && Main.getPiece(getPosition().getX() - 3, isle )== null
                        && Main.getPiece(getPosition().getX() - 2, isle) == null
                        && Main.getPiece(getPosition().getX() - 1, isle) == null
                ){
                    Main.getPiece(getPosition().getX() - 4, isle).getPosition().setPos(getPosition().getX() - 1, isle);
                    Main.changeSide();
                    getPosition().setPos(position);
                    hasMoved = true;
                }
            }
        }
    }

    @Override
    public void take(Piece piece) {
        piece.setDead();
        if(Main.isSquareThreat(piece.getPosition().getX(), piece.getPosition().getY(), getSide())){
            piece.undoDead();
            return;
        }
        if(getSide() == piece.getSide()){
            return;
        }
        piece.undoDead();
        int xDiff = piece.getPosition().getX() - getPosition().getX();
        int yDiff = piece.getPosition().getY() - getPosition().getY();
        if(xDiff != 0 || yDiff != 0){
            if(Math.abs(xDiff) <= 1 && Math.abs(yDiff) <= 1){
                getPosition().setPos(piece.getPosition());
                piece.setDead();
                hasMoved = true;
                Main.changeSide();
            }
        }
    }
}