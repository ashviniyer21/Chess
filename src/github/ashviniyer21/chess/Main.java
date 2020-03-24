package github.ashviniyer21.chess;

import github.ashviniyer21.chess.pieces.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;


public class Main extends Application {
	private static Side currentSide;
	private static Piece[][] board;
	private static Pane root;
	private static Piece[] whitePieces;
	private static Piece[] blackPieces;
	private ClickState clickState;
	private Piece selectedPiece;
	private Rectangle highlight;
	public static final int PIECE_SIZE = 80;
	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {
		var ref = new Object() {
			boolean gameOver = false;
		};
		Text whiteWin = new Text("White wins");
		whiteWin.setX(PIECE_SIZE * 4);
		whiteWin.setY(PIECE_SIZE * 4);
		whiteWin.setFill(Color.BLUE);
		whiteWin.setFont(new Font(20));
		Text blackWin = new Text("Black wins");
		blackWin.setFill(Color.BLUE);
		blackWin.setFont(new Font(20));
		blackWin.setX(PIECE_SIZE * 4);
		blackWin.setY(PIECE_SIZE * 4);
		clickState = ClickState.FINDING_PIECE;
		currentSide = Side.WHITE;
		board = new Piece[8][8];
		root = new Pane();
		highlight = new Rectangle(80*9, 80*9, PIECE_SIZE, PIECE_SIZE);
		highlight.setFill(Color.TRANSPARENT);
		highlight.setStrokeWidth(5);
		highlight.setStroke(Color.YELLOW);
		root.getChildren().addAll(highlight);
		BackgroundImage image = new BackgroundImage(new Image(new FileInputStream("chess_board.png"), 640, 640, false, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		Background background = new Background(image);
		root.setBackground(background);
		primaryStage.setTitle("Chess");
		Scene scene = new Scene(root, 8 * PIECE_SIZE, 8 * PIECE_SIZE);
		primaryStage.setScene(scene);
		whitePieces = new Piece[16];
		blackPieces = new Piece[16];
		for(int i = 0; i < 8; i++){
			whitePieces[i] = new Pawn(Side.WHITE, new Position(i, 6));
			blackPieces[i] = new Pawn(Side.BLACK, new Position(i, 1));
		}
		for(int i = 8; i < 16; i++){
			if(i == 8 || i == 15){
				whitePieces[i] = new Rook(Side.WHITE, new Position(i-8, 7));
				blackPieces[i] = new Rook(Side.BLACK, new Position(i-8, 0));
			} else if(i == 9 || i == 14){
				whitePieces[i] = new Knight(Side.WHITE, new Position(i-8, 7));
				blackPieces[i] = new Knight(Side.BLACK, new Position(i-8, 0));
			} else if(i == 10 || i == 13){
				whitePieces[i] = new Bishop(Side.WHITE, new Position(i-8, 7));
				blackPieces[i] = new Bishop(Side.BLACK, new Position(i-8, 0));
			} else if(i == 11){
				whitePieces[i] = new Queen(Side.WHITE, new Position(i-8, 7));
				blackPieces[i] = new Queen(Side.BLACK, new Position(i-8, 0));
			} else {
				whitePieces[i] = new King(Side.WHITE, new Position(i-8, 7));
				blackPieces[i] = new King(Side.BLACK, new Position(i-8, 0));
			}
		}
		scene.setOnMousePressed(event -> {
			if(!ref.gameOver) {
				Position position = mouseToPos(event.getSceneX(), event.getSceneY());
				if (clickState == ClickState.FINDING_PIECE) {
					if (board[position.getY()][position.getX()] != null && board[position.getY()][position.getX()].getSide() == currentSide) {
						selectedPiece = board[position.getY()][position.getX()];
						changeState();
					}
				} else {
					if (board[position.getY()][position.getX()] != null) {
						selectedPiece.take(board[position.getY()][position.getX()]);
					} else {
						selectedPiece.move(position);
					}
					selectedPiece = null;
					changeState();
				}
				if (determineCheckMate(currentSide)) {
					ref.gameOver = true;
				}
				updateBoard();
				highlight(position.getX(), position.getY());
				if(ref.gameOver){
					if (currentSide == Side.BLACK) {
						root.getChildren().addAll(whiteWin);
					} else {
						root.getChildren().addAll(blackWin);
					}
				}
			}
		});
		addToBoard(whitePieces);
		addToBoard(blackPieces);
		updateBoard(true);
		primaryStage.show();
	}



	public static void main(String[] args) {
		launch(args);
	}

	private void highlight(int x, int y){
		highlight.setX(x * PIECE_SIZE);
		highlight.setY(y * PIECE_SIZE);
	}

	public static void updateBoard(boolean firstTime){
		for (Piece[] value : board) {
			Arrays.fill(value, null);
		}
		addToBoard(whitePieces);
		addToBoard(blackPieces);
		if(!firstTime){
			root.getChildren().removeAll(whitePieces);
			root.getChildren().removeAll(blackPieces);
		}
		for(Piece[] pieces: board){
			for(Piece piece: pieces){
				if(piece != null){
					piece.updatePosImage();
					if(!piece.isDead()){
						root.getChildren().addAll(piece);
					}
				}
			}
		}
	}
	public static void updateBoard(){
		updateBoard(false);
	}

	private static void addToBoard(Piece[] pieces){
		for (Piece piece : pieces) {
			Position piecePos = piece.getPosition();
			if(!piece.isDead()){
				board[piecePos.getY()][piecePos.getX()] = piece;
			}
		}
	}

	private Position mouseToPos(double x, double y){
		return new Position(((int)x) / 80, ((int)y) / 80);
	}
	public static void changeSide(){
		if(currentSide == Side.WHITE){
			currentSide = Side.BLACK;
		} else {
			currentSide = Side.WHITE;
		}
	}
	private void changeState(){
		if(clickState == ClickState.FINDING_PIECE){
			clickState = ClickState.MOVING_PIECE;
		} else {
			clickState = ClickState.FINDING_PIECE;
		}
	}
	public static Piece getPiece(int x, int y){
		return board[y][x];
	}

	enum ClickState{
		FINDING_PIECE, MOVING_PIECE
	}
	public static boolean isSquareThreat(Side side){
		updateBoard();
		if(side == Side.WHITE){
			return isSquareThreat(whitePieces[12].getPosition().getX(), whitePieces[12].getPosition().getY(), side);
		} else {
			return isSquareThreat(blackPieces[12].getPosition().getX(), blackPieces[12].getPosition().getY(), side);
		}
	}
	public static boolean isSquareThreat(int x, int y, Side side){
		Piece[] pieces;
		if(side == Side.WHITE){
			pieces = blackPieces;
		} else {
			pieces = whitePieces;
		}
		for (Piece piece : pieces) {
			Position position = piece.getPosition();
			if(!piece.isDead()){
				switch (piece.getPieceType()){
					case KING:
						for(int i = -1; i <= 1; i++){
							for(int j = -1; j <= 1; j++){
								if(position.getX() + i == x && position.getY() + j == y){
									return true;
								}
							}
						}
						break;
					case PAWN:
						if(Math.abs(x - position.getX()) == 1){
							if(side == Side.WHITE){
								if(position.getY() + 1 == y){
									return true;
								}
							} else {
								if(position.getY() - 1 == y){
									return true;
								}
							}
						}
						break;
					case ROOK:
						int xDiff = position.getX() - x;
						int yDiff = position.getY() - y;
						boolean giveTrue = false;
						if(xDiff == 0){
							giveTrue = true;
							if(yDiff < 0){
								for(int i = y - 1; i > position.getY(); i--){
									if (getPiece(x, i) != null) {
										giveTrue = false;
										break;
									}
								}
							} else if(yDiff > 0){
								for(int i = y + 1; i < position.getY(); i++){
									if (getPiece(x, i) != null) {
										giveTrue = false;
										break;
									}
								}
							}
						} else if(yDiff == 0){
							giveTrue = true;
							if(xDiff < 0){
								for(int i = x - 1; i > position.getX(); i--){
									if (getPiece(i, y) != null) {
										giveTrue = false;
										break;
									}
								}
							} else {
								for(int i = x + 1; i < position.getX(); i++){
									if (getPiece(i, y) != null) {
										giveTrue = false;
										break;
									}
								}
							}
						}
						if(giveTrue){
							return true;
						}
						break;
					case QUEEN:
						xDiff = position.getX() - x;
						yDiff = position.getY() - y;
						giveTrue = false;
						if(xDiff == 0){
							giveTrue = true;
							if(yDiff < 0){
								for(int i = y - 1; i > position.getY(); i--){
									if (getPiece(x, i) != null) {
										giveTrue = false;
										break;
									}
								}
							} else if(yDiff > 0){
								for(int i = y + 1; i < position.getY(); i++){
									if (getPiece(x, i) != null) {
										giveTrue = false;
										break;
									}
								}
							}
						} else if(yDiff == 0){
							giveTrue = true;
							if(xDiff < 0){
								for(int i = x - 1; i > position.getX(); i--){
									if (getPiece(i, y) != null) {
										giveTrue = false;
										break;
									}
								}
							} else {
								for(int i = x + 1; i < position.getX(); i++){
									if (getPiece(i, y) != null) {
										giveTrue = false;
										break;
									}
								}
							}
						}
						if(giveTrue){
							return true;
						}
						boolean isGood = false;
						if(Math.abs(xDiff) == Math.abs(yDiff)){
							isGood = true;
							int xMult = 1;
							int yMult = 1;
							if(xDiff > 0){
								xMult = -1;
							}
							if(yDiff > 0){
								yMult = -1;
							}
							for(int i = 1; i < Math.abs(xDiff); i++){
								if(Main.getPiece(position.getX() + i * xMult, position.getY() + i * yMult) != null){
									isGood = false;
									break;
								}
							}
						}
						if(isGood){
							return true;
						}
						break;
					case BISHOP:
						xDiff = position.getX() - x;
						yDiff = position.getY() - y;
						isGood = false;
						if(Math.abs(xDiff) == Math.abs(yDiff)){
							isGood = true;
							int xMult = 1;
							int yMult = 1;
							if(xDiff > 0){
								xMult = -1;
							}
							if(yDiff > 0){
								yMult = -1;
							}
							for(int i = 1; i < Math.abs(xDiff); i++){
								if(Main.getPiece(position.getX() + i * xMult, position.getY() + i * yMult) != null){
									isGood = false;
									break;
								}
							}
						}
						if(isGood){
							return true;
						}
						break;
					case KNIGHT:
						if((position.getX() + 2 == x && position.getY() + 1 == y)
								|| (position.getX() + 2 == x && position.getY() - 1 == y)
								|| (position.getX() - 2 == x && position.getY() + 1 == y)
								|| (position.getX() - 2 == x && position.getY() - 1 == y)
								|| (position.getX() + 1 == x && position.getY() + 2 == y)
								|| (position.getX() + 1 == x && position.getY() - 2 == y)
								|| (position.getX() - 1 == x && position.getY() + 2 == y)
								|| (position.getX() - 1 == x && position.getY() - 2 == y)){
							return true;
						}
						break;
				}
			}
		}
		return false;
	}
	private boolean determineCheckMate(Side side){
		Piece[][] oldBoard = new Piece[8][8];
		for(int i = 0; i < oldBoard.length; i++){
			System.arraycopy(board[i], 0, oldBoard[i], 0, oldBoard[i].length);
		}
		Piece[] pieces;
		if(side == Side.WHITE){
			pieces = whitePieces;
		} else {
			pieces = blackPieces;
		}
		for (Piece piece : pieces) {
			Position originalPos = new Position(piece.getPosition().getX(), piece.getPosition().getY());
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					updateBoard();
					if (getPiece(j, k) == null) {
						piece.move(new Position(j, k));
						updateBoard();
						if (!isSquareThreat(side)) {
							piece.getPosition().setPos(originalPos);
							updateBoard();
							currentSide = side;
							return false;
						}
						piece.getPosition().setPos(originalPos);
						updateBoard();
					} else {
						Piece thing = getPiece(j, k);
						boolean hi = false;
						piece.take(thing);
						updateBoard();
						if(!isSquareThreat(side)){
							hi = true;
						}
						piece.getPosition().setPos(originalPos);
						thing.undoDead();
						updateBoard();
						if(hi){
							currentSide = side;
							return false;
						}
					}
				}
			}
		}
		currentSide = side;
		updateBoard();
		return true;
	}
}