package typingapp.service;

import java.util.Optional;

import typingapp.model.TypingWord;

public class GameFlowManager {

    private final GameService gameService;
    private final GameSessionManager sessionManager;
    private final GameTimer gameTimer;

    public GameFlowManager(
            GameService gameService,
            GameSessionManager sessionManager,
            GameTimer gameTimer) {

        this.gameService = gameService;
        this.sessionManager = sessionManager;
        this.gameTimer = gameTimer;
    }

    public Optional<TypingWord> nextWord() {

        Optional<TypingWord> word =
                gameService.nextWord();

        word.ifPresent(
                sessionManager::setCurrentWord);

        return word;
    }

    public void startQuestionTimer() {
        gameTimer.startQuestionTimer();
    }

    public void stopAll() {
        gameTimer.stopAll();
    }
}