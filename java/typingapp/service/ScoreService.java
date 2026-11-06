// ScoreService.java
package typingapp.service;

import typingapp.dao.ScoreDAO;
import typingapp.model.GameResult;
import typingapp.model.Score;

import java.time.LocalDateTime;
import java.util.Objects;

public class ScoreService {
    private final ScoreDAO scoreDAO;

    public ScoreService(ScoreDAO scoreDAO) {
        Objects.requireNonNull(scoreDAO, "scoreDAO must not be null");
        this.scoreDAO = scoreDAO;
    }

    // ✅ 統一されたパラメータ
    public Score createScore(
        String userName,
        String difficulty,
        GameResult gameResult
    ) {
        Objects.requireNonNull(userName, "userName must not be null");
        Objects.requireNonNull(difficulty, "difficulty must not be null");
        Objects.requireNonNull(gameResult, "gameResult must not be null");
        
        return new Score(
            userName,
            difficulty,
            gameResult.finalScore(),
            gameResult.correctCount(),
            gameResult.nearCount(),
            gameResult.incorrectCount(),
            gameResult.timeoutCount(),
            gameResult.correctCharCount(),
            gameResult.wrongCharCount(),
            gameResult.averageTypingSpeed(),
            gameResult.timeRemaining(),
            gameResult.totalTime(),
            LocalDateTime.now()
        );
    }
    
    public boolean save(Score score) {
        Objects.requireNonNull(score, "score must not be null");
        return scoreDAO.save(score);
    }
}