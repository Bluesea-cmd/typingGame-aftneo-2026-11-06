package typingapp.model;

import java.util.Objects;

public record WordResult(
        TypingWord word,
        String correctRomaji,
        String input,
        JudgeResult result,
        int wrongCharCount
) {
	public WordResult {
    Objects.requireNonNull(word, "word must not be null");
    Objects.requireNonNull(correctRomaji, "correctRomaji must not be null");
    Objects.requireNonNull(input, "input must not be null");
    Objects.requireNonNull(result, "result must not be null");
    if (wrongCharCount < 0) { 
        throw new IllegalArgumentException("wrongCharCount must not be negative: " + wrongCharCount); 
    }
}
    public boolean isCorrect() {
        return result.isCorrect();
    }
    
    public boolean isNear() {
        return result.isNear();
    }
    
    public boolean isIncorrect() {
        return result.isIncorrect();
    }

    public boolean isTimeout() {
        return result.isTimeout();
    }

    public String displayText() {
        return result.displayText();
    }
    
    public String inputText() {
        return input;
    }
}