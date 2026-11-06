package typingapp.model;

import java.util.Objects;

public record JudgeResult(
        EvaluationResult result,
        String displayText
) {
	public JudgeResult {
    Objects.requireNonNull(result,"result must not be null");
    Objects.requireNonNull(displayText,"displayText must not be null");
    
    if (displayText.isBlank()) { throw new IllegalArgumentException("displayText must not be blank"); }
	} 
	
	public boolean isCorrect() {
	    return result.isCorrect();
	}
	
	public boolean isNear() {
	    return result.isNear();
	}
	
    public boolean isIncorrect() {
        return result == EvaluationResult.INCORRECT;
    }
    
	public boolean isTimeout() {
	    return result.isTimeout();
	}

}
