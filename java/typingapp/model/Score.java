// Score.java - フィールド名を明確に
package typingapp.model;

import java.time.LocalDateTime;
import java.util.Objects;

public record Score(
    String userName,
    String difficulty,
    int finalScore,
    int correctCount,        // 正解した単語数
    int nearCount,           // 惜しい単語数
    int incorrectCount,      // 不正解した単語数
    int timeoutCount,        // タイムアウト数
    int correctCharCount,    // 正しく入力できた文字数
    int wrongCharCount,      // 間違えた文字数（累積）
    double averageTypingSpeed,
    int timeRemaining,
    int totalTime,
    LocalDateTime currentDate
) {
    public Score {
        Objects.requireNonNull(userName, "userName must not be null");
        if (userName.isBlank()) { throw new IllegalArgumentException("userName must not be blank"); }

        Objects.requireNonNull(difficulty, "difficulty must not be null");
        if (difficulty.isBlank()) { throw new IllegalArgumentException("difficulty must not be blank"); }

        Objects.requireNonNull(currentDate, "currentDate must not be null");
        
        if (finalScore < 0) throw new IllegalArgumentException("finalScore must not be negative");
        if (correctCount < 0) throw new IllegalArgumentException("correctCount must not be negative");
        if (nearCount < 0) throw new IllegalArgumentException("nearCount must not be negative");
        if (incorrectCount < 0) throw new IllegalArgumentException("incorrectCount must not be negative");
        if (timeoutCount < 0) throw new IllegalArgumentException("timeoutCount must not be negative");
        if (correctCharCount < 0) throw new IllegalArgumentException("correctCharCount must not be negative");
        if (wrongCharCount < 0) throw new IllegalArgumentException("wrongCharCount must not be negative");
        if (averageTypingSpeed < 0) throw new IllegalArgumentException("averageTypingSpeed must not be negative");
        if (timeRemaining < 0) throw new IllegalArgumentException("timeRemaining must not be negative");
        if (totalTime <= 0) throw new IllegalArgumentException("totalTime must be positive");
        if (timeRemaining > totalTime) throw new IllegalArgumentException("timeRemaining cannot exceed totalTime");
    }
}