package org.example;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

import org.example.model.Board;
import org.example.model.GameStatus;
import org.example.model.PieceType;
import org.example.model.Player;
import org.example.model.PlayingPieceO;
import org.example.model.PlayingPieceX;

public class TicTacToeGame {
    Deque<Player> players;
    Board gameBoard;
    Player winner;
    Scanner inputScanner;

    public void initializeGame(){
        players = new ArrayDeque<>();

        PlayingPieceX crossPiece = new PlayingPieceX();
        Player player1 = new Player("Player1",crossPiece);

        PlayingPieceO noughtsPiece = new PlayingPieceO();
        Player player2 = new Player("Player2",noughtsPiece);

        players.add(player1);
        players.add(player2);

        gameBoard = new Board(3);
        inputScanner = new Scanner(System.in);
    }

    public GameStatus startGame(){
        boolean noWinner = true;

        while(noWinner){
            Player currentPlayer = players.removeFirst();

            gameBoard.printBoard();
            List<int[]> freeSpaces = gameBoard.getFreeCells();

            if(freeSpaces.isEmpty()){
                noWinner = false;
                continue;
            }

            //Read the user input
            System.out.print("Player: " + currentPlayer.name + " - Please enter [row,column]: ");
            String s = inputScanner.nextLine();
            String[] values = s.split(",");
            int inputRow = Integer.parseInt(values[0]);
            int inputColumn = Integer.parseInt(values[1]);

            // Place the piece in the board

            boolean validMove = gameBoard.addPiece(inputRow,inputColumn,currentPlayer.playingPiece);
            if(!validMove){
                System.out.println("Incorrect position chosen, try again");
                players.addFirst(currentPlayer);
                continue;
            }
            players.addLast(currentPlayer);

            boolean isWinner = checkForWinner(inputRow,inputColumn,currentPlayer.playingPiece.pieceType);
            if(isWinner) {
                gameBoard.printBoard();
                winner = currentPlayer;
                return GameStatus.WIN;
            }

        }
        return GameStatus.DRAW;
    }

    private boolean checkForWinner(int row, int col, PieceType pieceType) {
        boolean rowMatch = true;
        boolean columnMatch = true;
        boolean diagonalMatch = true;
        boolean antiDiagonalMatch = true;

        // Check row
        for (int i = 0; i < gameBoard.size; i++) {
            if (gameBoard.board[row][i] == null || gameBoard.board[row][i].pieceType != pieceType) {
                rowMatch = false;
                break;
            }
        }

        // Check column
        for (int i = 0; i < gameBoard.size; i++) {
            if (gameBoard.board[i][col] == null || gameBoard.board[i][col].pieceType != pieceType) {
                columnMatch = false;
                break;
            }
        }

        // Check diagonal
        for (int i = 0, j = 0; i < gameBoard.size; i++, j++) {
            if (gameBoard.board[i][j] == null || gameBoard.board[i][j].pieceType != pieceType) {
                diagonalMatch = false;
                break;
            }
        }

        // Check anti-diagonal
        for (int i = 0, j = gameBoard.size - 1; i < gameBoard.size; i++, j--) {
            if (gameBoard.board[i][j] == null || gameBoard.board[i][j].pieceType != pieceType) {
                antiDiagonalMatch = false;
                break;
            }
        }

        return rowMatch || columnMatch || diagonalMatch || antiDiagonalMatch;
    }

    public void cleanup() {
        if (inputScanner != null) {
            inputScanner.close();
        }
    }
}
