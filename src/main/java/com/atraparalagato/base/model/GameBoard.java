package com.atraparalagato.base.model;

import java.util.HashSet;
import java.util.Set;

public class GameBoard {
    private int size;
    private Position catPosition;
    private Set<Position> blockedPositions;

    public GameBoard() {
        this.size = 0;
        this.catPosition = new Position(0, 0);
        this.blockedPositions = new HashSet<>();
    }

    public GameBoard(int size) {
        this.size = size;
        this.catPosition = new Position(0, 0);
        this.blockedPositions = new HashSet<>();
    }

    public void updateCatPosition(Position newPosition) {
        this.catPosition = newPosition;
    }

    public int getSize() {
        return size;
    }

    public Position getCatPosition() {
        return catPosition;
    }

    public Set<Position> getBlockedPositions() {
        return blockedPositions;
    }

    public void addBlockedPosition(Position position) {
        blockedPositions.add(position);
    }

    public int getTotalPositions() {
        return size * size;
    }
}