package com.atraparalagato.base.strategy;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.Position;

public interface CatMovementStrategy {
    Position findBestMove(GameBoard gameBoard);
}