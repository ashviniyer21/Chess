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
import java.util.*;


public class Main extends Application {
	private final int posGain = 2;
	private final int pieceGain = 5;
	private final boolean isAi = false;
	private static Side currentSide;
	private static Piece[][] board;
	private static Pane root;
	private static Piece[] whitePieces;
	private static Piece[] blackPieces;
	private ClickState clickState;
	private Piece selectedPiece;
	private Rectangle highlight;
	private static int count;
	public static final int PIECE_SIZE = 60;
	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {
		var ref = new Object() {
			boolean gameOver = false;
		};
		Text stalemate = new Text("Stalemate");
		stalemate.setX(PIECE_SIZE * 4);
		stalemate.setY(PIECE_SIZE * 4);
		stalemate.setFill(Color.BLUE);
		stalemate.setFont(new Font(20));
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
		highlight = new Rectangle(PIECE_SIZE*9, PIECE_SIZE*9, PIECE_SIZE, PIECE_SIZE);
		highlight.setFill(Color.TRANSPARENT);
		highlight.setStrokeWidth(5);
		highlight.setStroke(Color.YELLOW);
		root.getChildren().addAll(highlight);
		BackgroundImage image = new BackgroundImage(new Image(new FileInputStream("chess_board.png"), 8*PIECE_SIZE, 8*PIECE_SIZE, false, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
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
				count++;
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
				boolean stale = false;
				if (determineCheckMate(currentSide)) {
					ref.gameOver = true;
				} else if(isStalemate(currentSide)){
					ref.gameOver = true;
					stale = true;
				}
				try {
					checkPromotion(currentSide);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				updateBoard();
				highlight(position.getX(), position.getY());
				if(ref.gameOver){
					if(stale){
						root.getChildren().addAll(stalemate);
					} else if (currentSide == Side.BLACK) {
						root.getChildren().addAll(whiteWin);
					} else {
						root.getChildren().addAll(blackWin);
					}
				}
			}
			if(!ref.gameOver && clickState == ClickState.FINDING_PIECE && isAi && currentSide == Side.BLACK){
				aiMove(currentSide);
				boolean stale = false;
				if (determineCheckMate(currentSide)) {
					ref.gameOver = true;
				} else if(isStalemate(currentSide)){
					ref.gameOver = true;
					stale = true;
				}
				try {
					checkPromotion(currentSide);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				updateBoard();
				if(ref.gameOver){
					if(stale){
						root.getChildren().addAll(stalemate);
					} else if (currentSide == Side.BLACK) {
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

	private static void updatePrevMoves(Side side){
		Piece[] pieces;
		if(side == Side.WHITE){
			pieces = whitePieces;
		} else {
			pieces = blackPieces;
		}
		for (Piece piece : pieces) {
			piece.updatePrevMoves();
		}
	}

	private Position mouseToPos(double x, double y){
		return new Position(((int)x) / PIECE_SIZE, ((int)y) / PIECE_SIZE);
	}
	public static void changeSide(){
		updatePrevMoves(currentSide);
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
		if(x >= 0 && x <= 7 && y >= 0 && y <= 7){
			return board[y][x];
		} else {
			return null;
		}
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
		if(!isSquareThreat(side)){
			return false;
		}
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
	private void checkPromotion(Side side) throws FileNotFoundException {
		Piece[] pieces;
		if(side == Side.BLACK){
			pieces = whitePieces;
			side = Side.WHITE;
		} else {
			pieces = blackPieces;
			side = Side.BLACK;
		}
		for(int i = 0; i < 8; i++){
			Piece piece = pieces[i];
			if (piece.getPieceType() == PieceType.PAWN && (piece.getPosition().getY() == 0 || piece.getPosition().getY() == 7)){
				pieces[i].setDead();
				updateBoard();
				pieces[i] = new Queen(side, new Position(piece.getPosition().getX(), piece.getPosition().getY()));
				updateBoard();
			}
		}
	}
	private boolean isStalemate(Side side){
		if(isSquareThreat(side)){
			return false;
		}
		if(count > 50){
			System.out.println("COUNT");
			return true;
		}
		if(insufficentMaterial(Side.BLACK) && insufficentMaterial(Side.WHITE)){
			System.out.println("INSUF");
			return true;
		}
		if(!isSquareThreat(side)){
			Piece[] pieces;
			if(side == Side.WHITE){
				pieces = whitePieces;
			} else {
				pieces = blackPieces;
			}
			for(Piece piece: pieces){
				if(piece.checkStalemate()){
					return true;
				}
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
		}
		System.out.println("NO MOVE");
		return true;
	}

	public static void resetCount(){
		count = 0;
	}
	private boolean insufficentMaterial(Side side){
		Piece[] pieces;
		if(side == Side.WHITE){
			pieces = whitePieces;
		} else {
			pieces = blackPieces;
		}
		int tempCount = 0;
		boolean stalemate = true;
		for (Piece whitePiece : pieces) {
			if (whitePiece != null && !whitePiece.isDead()) {
				if (whitePiece.getPieceType() == PieceType.ROOK || whitePiece.getPieceType() == PieceType.PAWN || whitePiece.getPieceType() == PieceType.QUEEN) {
					stalemate = false;
					break;
				} else if (whitePiece.getPieceType() == PieceType.BISHOP || whitePiece.getPieceType() == PieceType.KNIGHT) {
					tempCount++;
				}
			}
			if (tempCount > 1) {
				stalemate = false;
				break;
			}
		}
		return stalemate;
	}

	private void aiMove(Side side){
		HashMap<Piece, HashMap<Position, Integer>> values = new HashMap<>();
		Piece[] pieces;
		if(side == Side.WHITE){
			pieces = whitePieces;
		} else {
			pieces = blackPieces;
		}
		for (Piece piece : pieces) {
			HashMap<Position, Integer> list = new HashMap<>();
			values.put(piece, list);
			Position originalPos = new Position(piece.getPosition().getX(), piece.getPosition().getY());
			int orgPosVal = calcPosValue(piece, originalPos);
			for (int j = 0; j < 8; j++) {
				if(piece.isDead()){
					break;
				}
				for (int k = 0; k < 8; k++) {
					updateBoard();
					if (getPiece(j, k) == null) {
						Position temp = new Position(j, k);
						boolean tempHasMoved = piece.hasMoved();
						piece.move(temp);
						updateBoard();
						int val = Integer.MIN_VALUE;
						if(!isSquareThreat(side)){
							val = (calcPosValue(piece, piece.getPosition()) - orgPosVal) * posGain;
							for(int a = 0; a < 8; a++){
								for(int b = 0; b < 8; b++){
									Side oSide;
									if(side == Side.WHITE){
										oSide = Side.BLACK;
									} else {
										oSide = Side.WHITE;
									}
									ArrayList<Integer> list1 = threats(a, b, side);
									boolean good = false;
									if(getPiece(a, b) != null && getPiece(a, b).getSide() == side){
										list1.add(0, getPiece(a, b).getValue());
										good = true;
									}
									ArrayList<Integer> list2 = threats(a, b, oSide);
//							if(isSquareThreat(temp.getX(), temp.getY(), side) && !isSquareThreat(temp.getX(), temp.getY(), oSide)){
//								val -= piece.getValue() * pieceGain;
//							}
									while (list1.size() > 0 && list2.size() > 0 && good){
										val -= list1.remove(0) * pieceGain;
										if(list1.size() <= 0){
											break;
										}
										val += list2.remove(0) * pieceGain;
									}
								}
							}
						}
						if (!piece.getPosition().equals(originalPos)) {
							list.put(temp, val);
						}
						piece.setHasMoved(tempHasMoved);
						piece.getPosition().setPos(originalPos);
						updateBoard();
					} else {
						Piece thing = getPiece(j, k);
						boolean tempHasMoved = piece.hasMoved();
						piece.take(thing);
						updateBoard();
						int val = Integer.MIN_VALUE;
						if(!isSquareThreat(side) && thing.getSide() != side){
							val = (calcPosValue(piece, thing.getPosition()) - calcPosValue(piece, originalPos))*posGain;
//							val = thing.getValue();
//							if(isSquareThreat(thing.getPosition().getX(), thing.getPosition().getY(), side)){
//								val -= piece.getValue();
//							}
							for(int a = 0; a < 8; a++){
								for(int b = 0; b < 8; b++){
									Side oSide;
									if(side == Side.WHITE){
										oSide = Side.BLACK;
									} else {
										oSide = Side.WHITE;
									}
									ArrayList<Integer> list1 = threats(j, k, side);
									boolean good = false;
									if(getPiece(a, b) != null && getPiece(a, b).getSide() == side){
										list1.add(0, getPiece(a, b).getValue());
										good = true;
									}
									ArrayList<Integer> list2 = threats(j, k, oSide);
//							if(isSquareThreat(temp.getX(), temp.getY(), side) && !isSquareThreat(temp.getX(), temp.getY(), oSide)){
//								val -= piece.getValue() * pieceGain;
//							}
									while (list1.size() > 0 && list2.size() > 0 && good){
										val -= list1.remove(0);
										if(list1.size() <= 0){
											break;
										}
										val += list2.remove(0);
										if(list2.size() <= 0){
											break;
										}
									}
								}
							}
							val += thing.getValue();
							val *= pieceGain;
						}
						if(!piece.getPosition().equals(originalPos)){
							list.put(thing.getPosition(), val);
						}
						piece.getPosition().setPos(originalPos);
						piece.setHasMoved(tempHasMoved);
						thing.undoDead();
						updateBoard();
					}
				}
			}
		}
		Piece move = pieces[0];
		Position movePos = new Position(0, 0);
		int maxVal = Integer.MIN_VALUE;
		for(Piece piece: pieces){
			HashMap<Position, Integer> map = values.get(piece);
			for (Map.Entry<Position, Integer> entry : map.entrySet()) {
				if(entry.getValue() > maxVal){
					maxVal = entry.getValue();
					movePos = entry.getKey();
					move = piece;
				}
			}
		}
		if(getPiece(movePos.getX(), movePos.getY()) == null){
			move.move(movePos);
		} else {
			move.take(getPiece(movePos.getX(), movePos.getY()));
		}
		if(currentSide == side){
			changeSide();
		}
	}

	private int calcPosValue(Piece piece, Position position){
		int[][] values = new int[8][8];
		switch (piece.getPieceType()){
			case PAWN:
				values = new int[][] {
						{0, 0, 0, 0, 0, 0, 0, 0},
						{1, 2, 2, -4, -4, 2, 2, 1},
						{1, -1, -2, 0, 0, -2, -1, 1},
						{0, 0, 0, 4, 4, 0, 0, 0},
						{1, 1, 2, 5, 5, 2, 1, 1},
						{2, 2, 4, 6, 6, 4, 2, 2},
						{10, 10, 10, 10, 10, 10, 10, 10},
						{0, 0, 0, 0, 0, 0, 0, 0}
				};
				break;
			case ROOK:
				values = new int[][] {
						{0, 0, 0, 1, 1, 0, 0, 0},
						{-1, 0, 0, 0, 0, 0, 0, -1},
						{-1, 0, 0, 0, 0, 0, 0, -1},
						{-1, 0, 0, 0, 0, 0, 0, -1},
						{-1, 0, 0, 0, 0, 0, 0, -1},
						{-1, 0, 0, 0, 0, 0, 0, -1},
						{1, 2, 2, 2, 2, 2, 2, 1},
						{0, 0, 0, 0, 0, 0, 0, 0}
				};
				break;
			case QUEEN:
				values = new int[][] {
						{-4, -2, -2, -1, -1, -2, -2, -4},
						{-2, 0, 1, 0, 0, 0, 0, -2},
						{-2, 1, 1, 1, 1, 1, 0, -2},
						{0, 0, 1, 1, 1, 1, 0, 0},
						{-1, 0, 1, 1, 1, 1, 0, -1},
						{-2, 0, 1, 1, 1, 1, 0, -2},
						{-2, 0, 0, 0, 0, 0, 0, -2},
						{-4, -2, -2, -1, -1, -2, -2, -4}
				};
				break;
			case BISHOP:
				values = new int[][] {
						{-4, -2, -2, -2, -2, -2, -2, -4},
						{-2, 1, 0, 0, 0, 0, 1, -2},
						{-2, 2, 2, 2, 2, 2, 2, -2},
						{-2, 0, 2, 2, 2, 2, 0, -2},
						{-2, 1, 1, 2, 2, 1, 1, -2},
						{-2, 0, 1, 2, 2, 1, 0, -2},
						{-2, 0, 0, 0, 0, 0, 0, -2},
						{-4, -2, -2, -2, -2, -2, -2, -4}
				};
				break;
			case KNIGHT:
				values = new int[][] {
						{-10, -8, -6, -6, -6, -6, -8, -10},
						{-8, -4, 0, 1, 1, 0, -4, -8},
						{-6, 1, 2, 3, 3, 2, 1, -6},
						{-6, 0, 3, 4, 4, 3, 0, -6},
						{-6, 1, 3, 4, 4, 3, 1, -6},
						{-6, 0, 2, 3, 3, 2, 0, -6},
						{-8, -4, 0, 0, 0, 0, -4, -8},
						{-10, -8, -6, -6, -6, -6, -8, -10}
				};
				break;
			case KING:
				values = new int[][] {
						{4, 6, 2, 0, 0, 2, 6, 4},
						{4, 4, 0, 0, 0, 0, 4, 4},
						{-2, -4, -4, -4, -4, -4, -4, -2},
						{-4, -6, -6, -8, -8, -6, -6, -4},
						{-6, -8, -8, -10, -10, -8, -8, -6},
						{-6, -8, -8, -10, -10, -8, -8, -6},
						{-6, -8, -8, -10, -10, -8, -8, -6},
						{-6, -8, -8, -10, -10, -8, -8, -6}
				};
				break;
		}
//		if(piece.getSide() == Side.BLACK){
			for(int a = 0; a < values.length/2; a++){
				int[] x = new int[8];
				int[] y = new int[8];
				for(int b = 0; b < values.length; b++){
					x[b] = values[a][b];
					y[b] = values[7-a][b];
				}
				values[a] = y;
				values[7-a] = x;
			}
//		}
		return values[position.getY()][position.getX()];
	}

	private ArrayList<Integer> threats(int x, int y, Side side){
		ArrayList<Integer> list = new ArrayList<>();
		Piece[] pieces;
		if(side == Side.WHITE){
			pieces = whitePieces;
		} else {
			pieces = blackPieces;
		}
		for (Piece piece : pieces) {
			Position position = piece.getPosition();
			if(!piece.isDead()){
				switch (piece.getPieceType()){
					case KING:
						for(int i = -1; i <= 1; i++){
							for(int j = -1; j <= 1; j++){
								if(position.getX() + i == x && position.getY() + j == y){
									list.add(piece.getValue());
								}
							}
						}
						break;
					case PAWN:
						if(Math.abs(x - position.getX()) == 1){
							if(side == Side.WHITE){
								if(position.getY() + 1 == y){
									list.add(piece.getValue());
								}
							} else {
								if(position.getY() - 1 == y){
									list.add(piece.getValue());
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
							list.add(piece.getValue());
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
							list.add(piece.getValue());
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
							list.add(piece.getValue());
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
							list.add(piece.getValue());
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
							list.add(piece.getValue());
						}
						break;
				}
			}
		}
		Collections.sort(list);
		return list;
	}
}