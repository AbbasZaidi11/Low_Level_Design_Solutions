package org.example;

import org.example.model.GameStatus;

public class PlayGame {
    public static void main(String[] args){
        System.out.println("\n===>>> TicTacToe Game\n");
        TicTacToeGame game = new TicTacToeGame();
        game.initializeGame();
        GameStatus status = game.startGame();
        System.out.println("\n===>>> GAME OVER: \n");
        switch (status) {
            case WIN -> System.out.print(game.winner.name + " won the game ");
            case DRAW -> System.out.print(" Its a Draw!");
            default -> System.out.print(" Game Ends");
        }
        
        // Clean up resources
        game.cleanup();
    }
}
