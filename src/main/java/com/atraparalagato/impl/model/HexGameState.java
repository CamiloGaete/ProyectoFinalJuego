package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameStatus;
import com.atraparalagato.base.model.GameState;

import java.util.HashMap;
import java.util.Map;

public class HexGameState extends GameState<HexPosition> {

    private HexGameBoard board;
    private HexPosition catPosition;

    public HexGameState(String gameId, int size) {
        super(gameId);
        this.board = new HexGameBoard(size);
        this.catPosition = new HexPosition(0, 0); // centro del tablero
    }

    @Override
    public boolean canExecuteMove(HexPosition target) {
        return board.isPositionInBounds(target) && !board.isBlocked(target);
    }

    @Override
    public void performMove(HexPosition target) {
        board.blockPosition(target);
        moveCount++;
    }

    @Override
    public void updateGameStatus() {
        if (isOnBorder(catPosition)) {
            status = GameStatus.LOST;
        } else if (board.getAdjacentPositions(catPosition).stream().allMatch(board::isBlocked)) {
            status = GameStatus.WON;
        } else {
            status = GameStatus.RUNNING;
        }
    }

    private boolean isOnBorder(HexPosition position) {
        return Math.abs(position.getQ()) == board.getSize()
            || Math.abs(position.getR()) == board.getSize()
            || Math.abs(-position.getQ() - position.getR()) == board.getSize();
    }

    @Override
    public HexPosition getCatPosition() {
        return catPosition;
    }

    @Override
    public void setCatPosition(HexPosition position) {
        this.catPosition = position;
    }

    @Override
    public boolean isGameFinished() {
        return status != GameStatus.RUNNING;
    }

    @Override
    public boolean hasPlayerWon() {
        return status == GameStatus.WON;
    }

    @Override
    public int calculateScore() {
        return 100 - moveCount * 5;
    }

    @Override
    public Map<String, Object> getSerializableState() {
        Map<String, Object> state = new HashMap<>();
        state.put("status", status.toString());
        state.put("catQ", catPosition.getQ());
        state.put("catR", catPosition.getR());
        state.put("moveCount", moveCount);
        state.put("blocked", board.getBlockedPositions());
        return state;
    }

    @Override
    public void restoreFromSerializable(Map<String, Object> state) {
        this.status = GameStatus.valueOf((String) state.get("status"));
        this.catPosition = new HexPosition((int) state.get("catQ"), (int) state.get("catR"));
        this.moveCount = (int) state.get("moveCount");
        // Nota: agregar lógica para restaurar posiciones bloqueadas si necesario
    }

    public HexGameBoard getBoard() {
        return board;
    }
}
