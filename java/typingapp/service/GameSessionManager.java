package typingapp.service;

import typingapp.model.GameSession;
import typingapp.model.TypingWord;
import typingapp.model.WordResult; 

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GameSessionManager {

    private GameSession session;

    private TypingWord currentWord;

    private final List<WordResult> results = new ArrayList<>();

    // =========================
    // 初期化
    // =========================
    public void start(GameSession session) {
        this.session = Objects.requireNonNull(session, "session must not be null");
        this.results.clear();
        this.currentWord = null;
    }

    private GameSession session() {
        if (session == null) {
            throw new IllegalStateException("session is null");
        }
        return session;
    }
    
    // =========================
    // 現在単語管理
    // =========================
    public void setCurrentWord(TypingWord word) {
    	Objects.requireNonNull(word, "word must not be null");
        this.currentWord = word;
    }

    private TypingWord currentWord() {
        if (currentWord == null) {
            throw new IllegalStateException("currentWord is not set");
        }
        return currentWord;
    }
    
    public TypingWord getCurrentWord() {
        return currentWord();
    }
    
    // =========================
    // 結果記録
    // =========================
    public void record(WordResult result) {
        Objects.requireNonNull(result, "result must not be null");

        results.add(result);
        session.record(result);
    }

    public List<WordResult> getResults() {
        return List.copyOf(results);
    }

    // =========================
    // 進行管理
    // =========================
    public int getRemainingQuestions() {
        return session().getRemainingQuestions();
    }

    public boolean isFinished() {
        return session().isFinished();
    }

    public int getTotalQuestions() {
        return session().getTotalQuestions();
    }

    public int getAnsweredCount() {
        return results.size();
    }

    // =========================
    // 整合性チェック（デバッグ用）
    // =========================
    public void validate() {
    	GameSession s = session();

    	if (results.size() > s.getTotalQuestions()) {
    	    throw new IllegalStateException( "results overflow: " + results.size());
    	}

    	if (s.getRemainingQuestions() < 0) {
    	    throw new IllegalStateException( "remainingQuestions < 0" );
    	}
    }

    // =========================
    // getter
    // =========================
    public GameSession getSession() {
        return session;
    }

    public Optional<TypingWord> getCurrentWordOptional() {
        return Optional.ofNullable(currentWord);
    }
}