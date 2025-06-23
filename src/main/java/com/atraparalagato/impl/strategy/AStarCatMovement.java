package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;

public class AStarCatMovement extends CatMovementStrategy<HexPosition> {

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
    public List<HexPosition> getFullPath(HexPosition start, HexGameBoard board) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Map<HexPosition, Double> gScore = new HashMap<>();
        Map<HexPosition, Double> fScore = new HashMap<>();

        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, board));
        openSet.add(new Node(start, fScore.get(start)));

        Set<HexPosition> closedSet = new HashSet<>();

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            HexPosition currPos = current.position;

            if (isGoal(currPos, board)) {
                return reconstructPath(cameFrom, currPos);
            }

            closedSet.add(currPos);

            for (HexPosition neighbor : board.getAdjacentPositions(currPos)) {
                if (board.isBlocked(neighbor) || closedSet.contains(neighbor)) continue;

                double tentativeG = gScore.getOrDefault(currPos, Double.POSITIVE_INFINITY) + 1;

                if (tentativeG < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighbor, currPos);
                    gScore.put(neighbor, tentativeG);
                    double f = tentativeG + heuristic(neighbor, board);
                    fScore.put(neighbor, f);
                    openSet.add(new Node(neighbor, f));
                }
            }
        }

        return null;
    }

    private double heuristic(HexPosition pos, HexGameBoard board) {
        int distToEdgeQ = board.getSize() - Math.abs(pos.getQ());
        int distToEdgeR = board.getSize() - Math.abs(pos.getR());
        int distToEdgeS = board.getSize() - Math.abs(-pos.getQ() - pos.getR());
        return Math.min(distToEdgeQ, Math.min(distToEdgeR, distToEdgeS));
    }

    private boolean isGoal(HexPosition pos, HexGameBoard board) {
        int size = board.getSize();
        return Math.abs(pos.getQ()) == size || Math.abs(pos.getR()) == size || Math.abs(-pos.getQ() - pos.getR()) == size;
    }

    private List<HexPosition> reconstructPath(Map<HexPosition, HexPosition> cameFrom, HexPosition current) {
        List<HexPosition> path = new LinkedList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    private static class Node {
        HexPosition position;
        double fScore;

        Node(HexPosition position, double fScore) {
            this.position = position;
            this.fScore = fScore;
        }
    }
}
