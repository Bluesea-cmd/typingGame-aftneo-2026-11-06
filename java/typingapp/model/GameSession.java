package typingapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameSession {
	
	private final String userName;
	private final String difficulty;
	
    private final int totalQuestions;	// 最大問題数
    private int remainingQuestions;		// 残り問題数  
 
    private List<WordResult> results = new ArrayList<>();
    private boolean finished;
    
    private int score;
    
    public GameSession(String userName, String difficulty, int totalQuestions, int score) {
        Objects.requireNonNull(userName, "userName must not be null");
        Objects.requireNonNull(difficulty, "difficulty must not be null");

        if (userName.isBlank()) {
            throw new IllegalArgumentException("userName must not be blank");
        }
        if (difficulty.isBlank()) {
            throw new IllegalArgumentException("difficulty must not be blank");
        }
        if (totalQuestions <= 0) {
            throw new IllegalArgumentException("totalQuestions must be greater than 0");
        }
        
        this.userName = userName;
        this.difficulty = difficulty;
        this.totalQuestions = totalQuestions;
        remainingQuestions = totalQuestions;
        this.results = new ArrayList<>();
        this.score = score;
        this.finished = false;
    }

    public void record(WordResult result) {
        Objects.requireNonNull(result);

        if (finished) {
            throw new IllegalStateException("GameSession is already finished");
        }

        results.add(result);

        if (remainingQuestions > 0) {
            remainingQuestions--;
        }

        if (remainingQuestions == 0) {
            finished = true;
        }
    }

    public List<WordResult> results() {
        return List.copyOf(results);
    }

    public String userName() {
        return userName;
    }

    public String difficulty() {
        return difficulty;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    public int getRecordedCount() {
        return results.size();
    }
    
    public int getRemainingQuestions() {
    	return remainingQuestions;
    }
    
    public int getScores() {
    	return score;
    }
    public boolean isFinished() {
        return finished;
    }    
}
