package typingapp.model;

// 結果を保存する
public enum EvaluationResult {
    CORRECT(true),	// 正解した単語
    NEAR(false),		// 惜しい単語
    INCORRECT(false),	// 不正解した単語
    TIMEOUT(false);		// タイムアウトした単語
	
    private final boolean isCorrect;
    
    EvaluationResult(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
    
    public boolean isCorrect() {
        return isCorrect;
    }
    
    public boolean isNear() {
        return this == NEAR;
    }
    
    public boolean isTimeout() {
        return this == TIMEOUT;
    }
}
