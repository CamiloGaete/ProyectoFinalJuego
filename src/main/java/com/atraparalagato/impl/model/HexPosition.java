package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;

import java.util.Objects;

public class HexPosition extends Position {
    private final int q;
    private final int r;

    public HexPosition(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    @Override
    public int distanceTo(Position other) {
        if (!(other instanceof HexPosition)) return Integer.MAX_VALUE;
        HexPosition o = (HexPosition) other;
        int dq = this.q - o.q;
        int dr = this.r - o.r;
        int ds = (-this.q - this.r) - (-o.q - o.r);
        return (Math.abs(dq) + Math.abs(dr) + Math.abs(ds)) / 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HexPosition)) return false;
        HexPosition other = (HexPosition) obj;
        return q == other.q && r == other.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    @Override
    public String toString() {
        return "HexPosition(" + q + "," + r + ")";
    }
}
