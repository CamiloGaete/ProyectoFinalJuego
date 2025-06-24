package com.atraparalagato.controller;

import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.service.HexGameService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin // opcional, por si el frontend lanza errores CORS
public class GameController {

    private final HexGameService gameService = new HexGameService();

    @PostMapping("/start")
    public String startGame(@RequestParam int boardSize) {
        return gameService.initializeGame(boardSize);
    }

    @PostMapping("/block")
    public void blockCell(@RequestParam String gameId,
                          @RequestParam int q,
                          @RequestParam int r) {
        gameService.makeMove(gameId, q, r);
    }

    @GetMapping("/state/{gameId}")
    public Map<String, Object> getState(@PathVariable String gameId) {
        return gameService.getGameState(gameId);
    }
}
