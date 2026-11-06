package typingapp.service;

public final class ScoreCalculator {

    // 定数化（EvaluationResult と合わせる）
    private static final int CORRECT_POINT = 15;
    private static final int NEAR_POINT = 11;
    private static final int WRONG_POINT = 7;
    private static final int TIMEOUT_POINT = 5;
    private static final int TIME_BONUS = 2;

    // インスタンス化防止
    private ScoreCalculator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 最終スコア計算
     */
    public static int calculateFinalScore(
            int correctCount,
            int nearCount,
            int incorrectCount,
            int wrongCharCount,
            int timeoutCount,
            int timeRemaining
    ) {
        validateCounts(
                correctCount,
                nearCount,
                incorrectCount,
                wrongCharCount,
                timeoutCount,
                timeRemaining
        );

        int baseScore = (correctCount * CORRECT_POINT)
        		+ (nearCount * NEAR_POINT)
                - (incorrectCount * WRONG_POINT)
//                - (wrongCharCount * WRONG_POINT)
                - (timeoutCount * TIMEOUT_POINT);
        
        int timeBonus = Math.max(0, timeRemaining * TIME_BONUS);
        
        if (baseScore <= 0) return 0;
        
        return Math.max(baseScore + timeBonus, 0);
    }

    /**
     * 平均キータイプ数計算
     */
    public static double calculateTypingSpeed(
            int correctCharCount,
            int totalTime,
            int timeRemaining
    ) {
        if (correctCharCount < 0) {
            throw new IllegalArgumentException(
                    "correctCharCount must not be negative"
            );
        }

        if (totalTime <= 0) {
            throw new IllegalArgumentException(
                    "totalTime must be positive"
            );
        }

        if (timeRemaining < 0) {
            throw new IllegalArgumentException(
                    "timeRemaining must not be negative"
            );
        }

        int spentTime = totalTime - timeRemaining;

        // 0除算防止
        if (correctCharCount == 0 || spentTime <= 0) {
            return 0.0;
        }

        double speed = (double) correctCharCount / spentTime;

        // 小数第1位まで
        return Math.round(speed * 10) / 10.0;
    }

    /**
     * 共通バリデーション
     */
    private static void validateCounts(
            int correctCount,
            int nearCount,
            int incorrectCount,
            int wrongCharCount,
            int timeoutCount,
            int timeRemaining
    ) {
        if (correctCount < 0) {
            throw new IllegalArgumentException(
                    "correctCount must not be negative"
            );
        }

        if (nearCount < 0) {
            throw new IllegalArgumentException(
                    "nearCount must not be negative"
            );
        }

        if (incorrectCount < 0) {
            throw new IllegalArgumentException(
                    "incorrectCount must not be negative"
            );
        }
        
        if (wrongCharCount < 0) {
            throw new IllegalArgumentException(
                    "wrongCharCount must not be negative"
            );
        }
        if (timeoutCount < 0) {
            throw new IllegalArgumentException(
                    "timeoutCount must not be negative"
            );
        }

        if (timeRemaining < 0) {
            throw new IllegalArgumentException(
                    "timeRemaining must not be negative"
            );
        }
    }
}