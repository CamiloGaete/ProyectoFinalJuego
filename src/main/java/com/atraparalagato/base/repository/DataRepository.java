package com.atraparalagato.base.repository;

import com.atraparalagato.base.model.Position;
import com.atraparalagato.base.model.GameState;

import java.util.List;

public interface DataRepository {
    void save(Position position);
    Position findById(Position position);
    List<Position> findAll();
    GameState startNewGame(int size, GameState gameState);
}