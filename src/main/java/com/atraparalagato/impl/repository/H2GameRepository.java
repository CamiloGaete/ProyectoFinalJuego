package com.atraparalagato.impl.repository;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.impl.model.HexGameState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class H2GameRepository implements DataRepository<HexGameState> {

    private static final String JDBC_URL = "jdbc:h2:file:./atrapar-al-gato-db";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public H2GameRepository() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS game_state (" +
                    "game_id VARCHAR(100) PRIMARY KEY, " +
                    "state CLOB NOT NULL)");
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }

    @Override
    public void save(HexGameState state) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            String json = objectMapper.writeValueAsString(state.getSerializableState());
            PreparedStatement ps = conn.prepareStatement(
                    "MERGE INTO game_state (game_id, state) VALUES (?, ?)");
            ps.setString(1, state.getGameId());
            ps.setString(2, json);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el estado", e);
        }
    }

    @Override
    public Optional<HexGameState> findById(String id) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT state FROM game_state WHERE game_id = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String json = rs.getString("state");
                Map<String, Object> map = objectMapper.readValue(json, Map.class);
                HexGameState state = new HexGameState(id, 5); // tamaño puede ajustarse según se almacene
                state.restoreFromSerializable(map);
                return Optional.of(state);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar estado por ID", e);
        }
    }

    @Override
    public List<HexGameState> findAll() {
        return findWhere(s -> true);
    }

    @Override
    public List<HexGameState> findWhere(Predicate<HexGameState> condition) {
        return findAndTransform(condition, Function.identity());
    }

    @Override
    public <R> List<R> findAndTransform(Predicate<HexGameState> condition, Function<HexGameState, R> transformer) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT game_id, state FROM game_state")) {
            List<R> result = new ArrayList<>();
            while (rs.next()) {
                String gameId = rs.getString("game_id");
                String json = rs.getString("state");
                Map<String, Object> map = objectMapper.readValue(json, Map.class);
                HexGameState state = new HexGameState(gameId, 5);
                state.restoreFromSerializable(map);
                if (condition.test(state)) {
                    result.add(transformer.apply(state));
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener y transformar estados", e);
        }
    }

    @Override
    public void executeInTransaction(Runnable action) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            conn.setAutoCommit(false);
            try {
                action.run();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Transacción fallida", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al ejecutar en transacción", e);
        }
    }
}
