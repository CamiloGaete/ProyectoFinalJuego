package com.atraparalagato.base.repository;
import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.Position;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class H2GameRepository implements DataRepository {
    private final DataSource dataSource;

    public H2GameRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Position position) {
        String sql = "INSERT INTO positions (x, y, is_blocked) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE is_blocked = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, position.getX());
            stmt.setInt(2, position.getY());
            stmt.setBoolean(3, position.isBlocked());
            stmt.setBoolean(4, position.isBlocked());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving position", e);
        }
    }

    @Override
    public Position findById(Position position) {
        String sql = "SELECT x, y, is_blocked FROM positions WHERE x = ? AND y = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, position.getX());
            stmt.setInt(2, position.getY());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Position pos = new Position(rs.getInt("x"), rs.getInt("y"));
                pos.setBlocked(rs.getBoolean("is_blocked"));
                return pos;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding position", e);
        }
    }

    @Override
    public List<Position> findAll() {
        List<Position> positions = new ArrayList<>();
        String sql = "SELECT x, y, is_blocked FROM positions";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Position pos = new Position(rs.getInt("x"), rs.getInt("y"));
                pos.setBlocked(rs.getBoolean("is_blocked"));
                positions.add(pos);
            }
            return positions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all positions", e);
        }
    }

    @Override
    public GameState startNewGame(int size, GameState gameState) {
        gameState.setGameBoard(new GameBoard(size));
        gameState.setGameFinished(false);
        String sql = "DELETE FROM positions";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error starting new game", e);
        }
        return gameState;
    }
}