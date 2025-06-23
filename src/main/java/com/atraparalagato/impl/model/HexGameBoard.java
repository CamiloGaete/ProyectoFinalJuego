package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HexGameBoard extends GameBoard<HexPosition> {

    private final int size;
    private final Set<HexPosition> blockedPositions = new HashSet<>();

    public HexGameBoard(int size) {
        this.size = size;
    }

    @Override
    public boolean isPositionInBounds(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        int s = -q - r;
        return Math.abs(q) <= size && Math.abs(r) <= size && Math.abs(s) <= size;
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }

    @Override
    public boolean isValidMove(HexPosition from, HexPosition to) {
        return isPositionInBounds(to) && !isBlocked(to) && getAdjacentPositions(from).contains(to);
    }

    @Override
    public void executeMove(HexPosition from, HexPosition to) {
        if (!isValidMove(from, to)) {
            throw new IllegalArgumentException("Movimiento inválido de " + from + " a " + to);
        }
        // En este juego, no actualizamos nada aquí directamente.
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        List<HexPosition> result = new ArrayList<>();
        for (int q = -size; q <= size; q++) {
            for (int r = Math.max(-size, -q - size); r <= Math.min(size, -q + size); r++) {
                HexPosition pos = new HexPosition(q, r);
                if (isPositionInBounds(pos) && condition.test(pos)) {
                    result.add(pos);
                }
            }
        }
        return result;
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] directions = {
                {+1, 0}, {+1, -1}, {0, -1},
                {-1, 0}, {-1, +1}, {0, +1}
        };
        List<HexPosition> adj = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition neighbor = new HexPosition(position.getQ() + dir[0], position.getR() + dir[1]);
            if (isPositionInBounds(neighbor)) {
                adj.add(neighbor);
            }
        }
        return adj;
    }

    public void blockPosition(HexPosition position) {
        if (isPositionInBounds(position)) {
            blockedPositions.add(position);
        }
    }

    public Set<HexPosition> getBlockedPositions() {
        return Collections.unmodifiableSet(blockedPositions);
    }

    public int getSize() {
        return size;
    }
}
