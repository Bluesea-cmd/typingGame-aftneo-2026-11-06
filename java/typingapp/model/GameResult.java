package typingapp.model;

import java.util.List;
import java.util.Objects;

// recordでgetter、setterを同時に行う(this.test = testと、getTest関数を同時に行える)
public record GameResult(
	    String userName,				// ユーザー名
	    String difficulty,				// 難易度
	    
	    int correctCount,				// 正解数
	    int incorrectCount,				// 不正解数
	    int nearCount,					// 惜しい（1-2文字異なる）数
	    int timeoutCount,				// タイムアウト数
	    
	    int correctCharCount,			// 合計入力数
	    int wrongCharCount,				// 不正解入力数
	    int totalInputCharCount,		// 総入力文字数
	    
	    int totalQuestions,				// 総問題数
	    double averageTypingSpeed,		// 平均キータイプ数
	    int finalScore,					// 最終スコア
	    
	    int timeRemaining,				// 経過時間
	    int totalTime,					// 制限時間
	    
	    List<WordResult> wordResults	// wordResultを読み込ませる
	) {
	public GameResult {
        Objects.requireNonNull(userName, "userName must not be null");
        if (userName.isBlank()) { throw new IllegalArgumentException("userName must not be blank"); }
        
        Objects.requireNonNull(difficulty, "difficulty must not be null");
        if (difficulty.isBlank()) { throw new IllegalArgumentException("difficulty must not be blank"); }

        // カウント数の検証（負の数チェック）
        if (correctCharCount < 0) { throw new IllegalArgumentException("correctCount must not be negative: " + correctCharCount); }
        if (wrongCharCount < 0) { throw new IllegalArgumentException("wrongCharCount must not be negative: " + wrongCharCount); }
        if (totalInputCharCount < 0) { throw new IllegalArgumentException("totalInputCharCount must not be negative: " + totalInputCharCount); }
        
        if (correctCount < 0) { throw new IllegalArgumentException("incorrectCount must not be negative: " + correctCount); }
        if (incorrectCount < 0) { throw new IllegalArgumentException("incorrectCount must not be negative: " + incorrectCount); }
        if (nearCount < 0) { throw new IllegalArgumentException("nearCount must not be negative: " + nearCount); }
        if (timeoutCount < 0) { throw new IllegalArgumentException("timeoutCount must not be negative: " + timeoutCount); }

        // 総問題数の検証
        if (totalQuestions <= 0) { throw new IllegalArgumentException("totalQuestions must be positive: " + totalQuestions); }
      
        // 平均タイプ速度の検証
        if (averageTypingSpeed < 0) { throw new IllegalArgumentException("averageTypingSpeed must not be negative: " + averageTypingSpeed); }

        // スコアの検証
        if (finalScore < 0) { throw new IllegalArgumentException("finalScore must not be negative: " + finalScore); }

        // 時間関連の検証
        if (timeRemaining < 0) { throw new IllegalArgumentException("timeRemaining must not be negative: " + timeRemaining); }
        if (totalTime <= 0) { throw new IllegalArgumentException("totalTime must be positive: " + totalTime); }

        // リストの検証
        Objects.requireNonNull(wordResults, "wordResults must not be null");
     // ゲーム中断時は記録数 < 総問題数 の場合があるため、チェックを緩くする
        if (wordResults.size() > totalQuestions) {
            throw new IllegalArgumentException(
                String.format(
                    "wordResults のサイズ(%d)が総問題数(%d)を超えています",
                    wordResults.size(), totalQuestions
                )
            );
        }
        // イミュータブルなコピーを作成
        wordResults = List.copyOf(wordResults);
    }
}
