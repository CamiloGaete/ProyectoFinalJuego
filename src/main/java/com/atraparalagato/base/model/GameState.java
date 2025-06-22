package com.atraparalagato.base.model;

public class GameState {
    private GameBoard gameBoard;
    private boolean isGameFinished;

    public GameState() {
        this.gameBoard = new GameBoard();
        this.isGameFinished = false;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public boolean isGameFinished() {
        return isGameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        isGameFinished = gameFinished;
    }
}