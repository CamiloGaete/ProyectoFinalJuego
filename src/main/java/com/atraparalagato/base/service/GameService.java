package com.atraparalagato.base.service;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.Position;
import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.base.strategy.CatMovementStrategy;

import java.util.List;
import java.util.stream.Collectors;

public class GameService {
    private final DataRepository dataRepository;
    private final CatMovementStrategy catStrategy;
    private GameState gameState;

    public GameService(DataRepository repository, CatMovementStrategy strategy) {
        this.dataRepository = repository;
        this.catStrategy = strategy;
        this.gameState = new GameState();
    }

    // Procedural: Manage game flow and turns
    public boolean startNewGame(int size) {
        gameState = dataRepository.startNewGame(size, gameState);
        return !gameState.isGameFinished();
    }

    public boolean makeMove(Position position) {
        if (dataRepository.findById(position) == null) {
            dataRepository.save(position);
            moveCatAutomatically();
            return true;
        }
        return false;
    }

    // Object-Oriented: Encapsulate game state and board logic
    private void moveCatAutomatically() {
        Position newCatPosition = catStrategy.findBestMove(gameState.getGameBoard());
        if (newCatPosition != null) {
            gameState.getGameBoard().updateCatPosition(newCatPosition);
            gameState.setGameFinished(checkGameFinished());
        }
    }

    // Functional: Use streams for analysis
    private boolean checkGameFinished() {
        List<Position> blockedPositions = dataRepository.findAll().stream()
            .filter(pos -> pos.isBlocked())
            .collect(Collectors.toList());
        return blockedPositions.size() >= gameState.getGameBoard().getTotalPositions() - 1;
    }

    public GameState getGameState() {
        return gameState;
    }
}