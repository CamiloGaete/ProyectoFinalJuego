package com.atraparalagato.controller;

import com.atraparalagato.base.service.GameService;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.Position;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService GameService;

    public GameController(GameService GameService) {
        this.GameService = GameService;
    }

    @PostMapping("/start/{size}")
    public boolean startNewGame(@PathVariable int size) {
        return GameService.startNewGame(size);
    }

    @PostMapping("/move")
    public boolean makeMove(@RequestBody Position position) {
        return GameService.makeMove(position);
    }

    @GetMapping("/state")
    public GameState getGameState() {
        return GameService.getGameState();
    }
}