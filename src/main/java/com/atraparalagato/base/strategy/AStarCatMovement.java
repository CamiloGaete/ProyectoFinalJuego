package com.atraparalagato.base.strategy;    

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.Position;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class AStarCatMovement implements CatMovementStrategy {
    @Override
    public Position findBestMove(GameBoard gameBoard) {
        Position current = gameBoard.getCatPosition();
        Set<Position> visited = new HashSet<>();
        PriorityQueue<Position> queue = new PriorityQueue<>((p1, p2) -> {
            int d1 = Math.abs(p1.getX()) + Math.abs(p1.getY());
            int d2 = Math.abs(p2.getX()) + Math.abs(p2.getY());
            return Integer.compare(d1, d2);
        });

        queue.add(current);
        visited.add(current);

        while (!queue.isEmpty()) {
            Position pos = queue.poll();
            if (!gameBoard.getBlockedPositions().contains(pos)) {
                return pos;
            }
            addNeighbors(pos, gameBoard, queue, visited);
        }
        return null; // No valid move
    }

    private void addNeighbors(Position pos, GameBoard gameBoard, PriorityQueue<Position> queue, Set<Position> visited) {
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};
        for (int i = 0; i < 4; i++) {
            int newX = pos.getX() + dx[i];
            int newY = pos.getY() + dy[i];
            if (newX >= 0 && newX < gameBoard.getSize() && newY >= 0 && newY < gameBoard.getSize()) {
                Position neighbor = new Position(newX, newY);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }
}