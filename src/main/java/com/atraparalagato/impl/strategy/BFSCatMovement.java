package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;

public class BFSCatMovement extends CatMovementStrategy<HexPosition> {

    @Override
    public HexPosition selectBestMove(HexPosition from, HexGameBoard board) {
        List<HexPosition> path = getFullPath(from, board);
        return (path != null && path.size() > 1) ? path.get(1) : from;
    }

    @Override
    public boolean hasPathToGoal(HexPosition from, HexGameBoard board) {
        return getFullPath(from, board) != null;
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition from, HexGameBoard board) {
        Queue<List<HexPosition>> queue = new LinkedList<>();
        Set<HexPosition> visited = new HashSet<>();

        queue.add(List.of(from));
        visited.add(from);

        while (!queue.isEmpty()) {
            List<HexPosition> path = queue.poll();
            HexPosition current = path.get(path.size() - 1);

            if (isGoal(current, board)) {
                return path;
            }

            for (HexPosition neighbor : board.getAdjacentPositions(current)) {
                if (!visited.contains(neighbor) && !board.isBlocked(neighbor)) {
                    visited.add(neighbor);
                    List<HexPosition> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }
        return null;
    }

    private boolean isGoal(HexPosition pos, HexGameBoard board) {
        int size = board.getSize();
        return Math.abs(pos.getQ()) == size || Math.abs(pos.getR()) == size || Math.abs(-pos.getQ() - pos.getR()) == size;
    }
}
