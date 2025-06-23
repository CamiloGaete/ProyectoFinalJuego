package com.atraparalagato.impl.service;

import com.atraparalagato.base.model.GameStatus;
import com.atraparalagato.base.service.GameService;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.repository.H2GameRepository;
import com.atraparalagato.impl.strategy.BFSCatMovement;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HexGameService extends GameService {

    private final H2GameRepository repository = new H2GameRepository(); // también se puede inyectar con @Autowired
    private final CatMovementStrategy<HexPosition> catStrategy = new BFSCatMovement(); // o AStarCatMovement

    @Override
    public String initializeGame(int boardSize) {
        String gameId = UUID.randomUUID().toString();
        HexGameState gameState = new HexGameState(gameId, boardSize);
        repository.save(gameState);
        return gameId;
    }

    @Override
    public boolean isValidMove(String gameId, int q, int r) {
        Optional<HexGameState> optionalState = repository.findById(gameId);
        if (optionalState.isEmpty()) return false;
        HexPosition pos = new HexPosition(q, r);
        return optionalState.get().canExecuteMove(pos);
    }

    @Override
    public void makeMove(String gameId, int q, int r) {
        Optional<HexGameState> optionalState = repository.findById(gameId);
        if (optionalState.isEmpty()) return;

        HexGameState game = optionalState.get();
        HexPosition target = new HexPosition(q, r);

        if (!game.canExecuteMove(target)) return;

        // Bloqueo del jugador
        game.performMove(target);

        // Movimiento del gato solo si el juego sigue
        game.updateGameStatus();
        if (!game.isGameFinished()) {
            HexPosition nextMove = catStrategy.selectBestMove(game.getCatPosition(), game.getBoard());
            if (nextMove != null) {
                game.setCatPosition(nextMove);
            }
        }

        game.updateGameStatus();
        repository.save(game);
    }

    @Override
    public Map<String, Object> getGameState(String gameId) {
        Optional<HexGameState> optionalState = repository.findById(gameId);
        return optionalState.map(HexGameState::getSerializableState)
                            .orElse(Map.of("error", "Juego no encontrado"));
    }

    // Métodos adicionales opcionales:
    public HexPosition getSuggestedMove(String gameId) {
        Optional<HexGameState> optionalState = repository.findById(gameId);
        if (optionalState.isEmpty()) return null;

        HexGameState game = optionalState.get();
        return catStrategy.selectBestMove(game.getCatPosition(), game.getBoard());
    }

    public int getScore(String gameId) {
        Optional<HexGameState> optionalState = repository.findById(gameId);
        return optionalState.map(HexGameState::calculateScore).orElse(0);
    }

    public GameStatus getGameStatus(String gameId) {
        Optional<HexGameState> optionalState = repository.findById(gameId);
        return optionalState.map(HexGameState::getStatus).orElse(GameStatus.FINISHED);
    }
}
