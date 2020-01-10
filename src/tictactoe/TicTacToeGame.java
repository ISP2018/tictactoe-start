package tictactoe;

import java.util.function.Predicate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;


public class TicTacToeGame {
	private final int boardsize;
	/** View of the TicTacToe board. */
	private Board board;
	/** Pieces on the board. */
	private Piece[][] pieces;
	/** Flag for game over. An observable object. */
	private SimpleBooleanProperty gameOver;
	
	private Player nextPlayer = Player.X;
	
	public TicTacToeGame(int size) {
		this.boardsize = size;
		board = new Board(boardsize,boardsize);   // view of the gameboard
		pieces = new Piece[boardsize][boardsize]; // stores info about pieces on board
		gameOver = new SimpleBooleanProperty(false);
		startNewGame();
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void startNewGame() {
		// Avoid nulls. Assign a "none" object to each location on the board.
		for(int row=0; row<boardsize; row++) 
			for(int col=0; col<boardsize; col++) pieces[row][col] = Piece.NONE;
		// Remove Pieces from the board (view), but not the squares themselves. Use a Predicate to test for Piece.
		Predicate<Node> isPiece = (node) -> node instanceof Piece;
		board.getChildren().removeIf(isPiece);
		gameOver.set(false);
	}
	
	/**
	 * Test whether a player can move to a square.
	 * @return true if can move to the requested (col,row) on board.
	 */
	public boolean canMoveTo(Player player, int col, int row) {
		if (row<0 || row>pieces.length) return false;
		if (col<0 || col>pieces[row].length) return false;
		if (isGameOver()) return false;
		return pieces[row][col] == null || pieces[row][col] == Piece.NONE;
	}
	
	/**
	 * Place a piece at a given (row,col) on the game board.
	 * It is up to the caller to make sure that the cell can
	 * be occupied before calling moveTo.
	 * 
	 * @param piece the piece to place 
	 * @param row board row to move to
	 * @param col board column to move to
	 */
	public void moveTo(Piece piece, int col, int row) {
		assert canMoveTo(piece.type, col, row): 
			String.format("moveTo(%s,%d,%d) is invalid",piece.toString(),row,col);
		if (! canMoveTo(piece.type, col, row) ) return; // not reached when assertions enabled
		pieces[row][col] = piece;
		board.add(piece, col, row); // GridPane.add has column param before row param
		
		/** next player's turn to move. */
		if (piece.type == Player.X) nextPlayer = Player.O;
		else nextPlayer = Player.X;
		
		/** check for a winner. */
		if (winner() != Player.NONE) gameOver.set(true);
		
		/** after each move check if board is full */
		if (boardIsFull()) gameOver.set(true);
	}
	
	/**
	 * Evaluate board to see if a player has won.
	 * @return reference to Player that wins. If no winner returns Player.NONE.
	 */
	public Player winner() {
		// This is fast but hacky and only works when the
		// number of pieces in a line to win (3) equals board size (3).
		
		// Look for N matching pieces on same row.
		rowtest:
		for(int row=0; row<boardsize; row++) {
			Player p = pieces[row][0].type;
			if (p == Player.NONE) continue;
			for(int col=1; col<boardsize; col++) {
				if (pieces[row][col].type != p) continue rowtest;
			}
			// all pieces on this row belong to p
			return p;
		}
		// Look for N matching pieces on same column
		coltest:
		for(int col=0; col<boardsize; col++) {
			Player p = pieces[0][col].type;
			if (p == Player.NONE) continue;
			for(int row=1; row<boardsize; row++) {
				if (pieces[row][col].type != p) continue coltest;
			}
			return p;
		}
		// Look for N matching pieces on downward diagonal.
		Player p = pieces[0][0].type;
		for(int col=1; col<boardsize; col++) {
			if (p != pieces[col][col].type) {
				p = Player.NONE;
				break;
			}
		}
		if (p != Player.NONE) return p;
		// Look for N matching pieces on upward diagonal
		p = pieces[0][boardsize-1].type; // start at lower-left corner
		for(int col=1; col<boardsize; col++) {
			int row = boardsize - col - 1;
			if (p != pieces[col][row].type) {
				p = Player.NONE;
				break;
			}
		}
		return p;
	}
	
	/**
	 * Get the value of gameOver status.
	 * @return true if game is over.
	 */
	public boolean isGameOver() {
		return gameOver.get();
	}
	
	/**
	 * Get the GameOver property.
	 * This enables adding observers to the gameOver property.
	 * @return gameOver property
	 */
	public BooleanProperty gameOver() {
		return gameOver;
	}
	
	/** 
	 * Get player who's turn it is to move. 
	 * @return player who's turn it is.
	 */
	public Player getNextPlayer() {
		return nextPlayer;
	}
	
	/** 
	 * Check if game board is fully occupied.
	 * @return true if board is full
	 */
	public boolean boardIsFull() {
		/** check if board is full */
		for(int row=0; row<boardsize; row++) {
			for(int col=0; col<boardsize; col++) if (pieces[row][col] == Piece.NONE) return false;
		}
		return true;
	}
}
