package typingapp.service;

import javax.swing.Timer;

import typingapp.util.AudioManager;

import java.util.Objects;
import java.util.function.IntConsumer;

public class GameTimer {
 
	private Timer gameTimer;									// 1ゲームの制限時間のタイマー設定(Mainで指定された秒数と同じ)
    private Timer questionTimer;								// 単語ごとの制限時間のタイマー設定(Mainで指定された秒数と同じ)

    private int gameTimeRemaining;			// 1ゲームの制限時間(変動)
    private int questionTimeRemaining;		// 単語ごとの問題時間(変動)
    
    private int gameTimeLimit;				// 1ゲームの制限時間(固定)
    private int questionTimeLimit;			// 1問題の制限時間(固定)
    // TIMER初期数値
    private static final int TIMER_INTERVAL_MS = 10000; 			// 単語の秒数(1秒)
    
    private boolean isQuestionTimedOut = false;
    
    private final AudioManager audioManager;
    
    private static final String[] TIMENEARLIMIT_VOICES = {
            "/sounds/Voice/残り時間が少ない時/お皿が通り過ぎちゃうニャ！急ぐニャ！.mp3",
            "/sounds/Voice/残り時間が少ない時/スピードアップニャ！.mp3",
            "/sounds/Voice/残り時間が少ない時/レーンの最後尾が見えてきたニャ…！.mp3",
            "/sounds/Voice/残り時間が少ない時/注文キャンセル寸前ニャ！.mp3"
    };
    
    /* ===== コールバック ===== */
    private final Runnable onGameTimeout;
    private final Runnable onQuestionTimeout;
    private final IntConsumer onGameTick;
    private final IntConsumer onQuestionTick;
    
    // このGameTimerが呼び出された時に、数値を自動的に置き換える
    public GameTimer(
            int gameLimit,										// 1ゲームの入力制限時間
            int questionLimit,									// 1問の制限時間
            Runnable onGameTimeout,
            Runnable onQuestionTimeout,
            IntConsumer onGameTick,
            IntConsumer onQuestionTick,
            AudioManager audioManager) {
    	
        Objects.requireNonNull(onGameTimeout, "onGameTimeout must not be null");
        Objects.requireNonNull(onQuestionTimeout, "onQuestionTimeout must not be null");
        Objects.requireNonNull(onGameTick, "onGameTick must not be null");
        Objects.requireNonNull(onQuestionTick, "onQuestionTick must not be null");
        
        if (gameLimit <= 0 || questionLimit <= 0) {
            throw new IllegalArgumentException("Time limits must be positive");
        }
        
        this.gameTimeRemaining = gameLimit;
        this.questionTimeRemaining = questionLimit;
        this.gameTimeLimit = gameLimit;
        this.questionTimeLimit = questionLimit;
        this.onGameTimeout = onGameTimeout;
        this.onQuestionTimeout = onQuestionTimeout;
        this.onGameTick = onGameTick;
        this.onQuestionTick = onQuestionTick;
        this.audioManager = audioManager;
        initTimers();
    }
    
    private void initTimers() {

        gameTimer = new Timer(TIMER_INTERVAL_MS, _ -> {
        	gameTimeRemaining = Math.max(0, gameTimeRemaining - 1);
            onGameTick.accept(gameTimeRemaining);

            if (gameTimeRemaining <= 0) {
                stopAll();
                onGameTimeout.run();
            }
        });

        questionTimer = new Timer(TIMER_INTERVAL_MS, _ -> {
        	questionTimeRemaining = Math.max(0, questionTimeRemaining - 1);
            onQuestionTick.accept(questionTimeRemaining);

            if (questionTimeRemaining == 15) {
            	audioManager.playRandomVoice(TIMENEARLIMIT_VOICES);
            }
            
            if (questionTimeRemaining <= 0) {
                questionTimer.stop();
                isQuestionTimedOut = true;
                onQuestionTimeout.run();
            }
        });
    }
    
    public int getGameTimeLimit() {
        return gameTimeLimit;
    }
    
    public int getGameTimeRemaining() {
        return gameTimeRemaining;
    }

    public int getQuestionTimeRemaining() {
        return questionTimeRemaining;
    }
    
    public boolean isQuestionTimedOut() {
        return isQuestionTimedOut;
    }
    
    // ゲーム開始
    public void startGameTimer() {

        gameTimer.stop();

        gameTimeRemaining = gameTimeLimit;

        onGameTick.accept(gameTimeRemaining);

        gameTimer.start();
    }
    
    // 1ゲームに途中で止めた時間を再開する
    public void startQuestionTimer() {

        questionTimer.stop();   // ← 重要
        gameTimer.stop();
       
        questionTimeRemaining = questionTimeLimit;

        isQuestionTimedOut = false;
        
        onQuestionTick.accept(questionTimeRemaining); // UI更新

        questionTimer.start();
        gameTimer.start();
    }
    
    // 全ての時間を停止する
    public void stopAll() {
        System.out.println("全タイマー停止");
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }
    }
    
    public void reset() {
        stopAll();

        gameTimeRemaining = gameTimeLimit;
        questionTimeRemaining = questionTimeLimit;

        isQuestionTimedOut = false;
        
        onGameTick.accept(gameTimeRemaining);
        onQuestionTick.accept(questionTimeRemaining);
    }
    
    public int getElapsedTime() {
        return gameTimeLimit - gameTimeRemaining;
    }
    
}