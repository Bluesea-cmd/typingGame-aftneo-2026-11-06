package typingapp.controller;

import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import typingapp.model.EvaluationResult;
import typingapp.model.GameResult;
import typingapp.model.GameSession;
import typingapp.model.JudgeResult;
import typingapp.model.PartialResult;
import typingapp.model.Score;
import typingapp.model.TypingWord;
import typingapp.model.WordResult;
import typingapp.service.GameEngine;
import typingapp.service.GameService;
import typingapp.service.GameSessionManager;
import typingapp.service.GameTimer;
import typingapp.service.RomajiNormalizationService;
import typingapp.service.RomajiVariantService;
import typingapp.service.ScoreCalculator;
import typingapp.service.ScoreService;
import typingapp.ui.GamePanel;
import typingapp.ui.ScoreResultUI;
import typingapp.ui.StartGameUI;
import typingapp.util.AudioManager;

public class GameController {
	
    private final GameService gameService;
    private final GameEngine gameEngine;
    private final RomajiVariantService romajiVariantService;
    private final ScoreService scoreService;
    private final AudioManager audioManager;
    
    private final GamePanel gamePanel;

    private final GameSessionManager sessionManager;

    private GameTimer gameTimer;

    private String userName;
    private String difficulty;
    private int questionCounter;

    private TypingWord currentWord;
    
    private boolean isProcessingTimeout = false;
    private boolean isGameActive = false;
    
    private static final int QUESTION_TIME_LIMIT = 5;
    private static final int GAME_TIME_LIMIT = 60;
    
    private final JFrame frame;
    
    private javax.swing.Timer sushiTimer;
    
    //追加
    private javax.swing.Timer nextQuestionTimer;
    
    private boolean waitingNextQuestion = false;
    
    // 正解後、Enterキーによる次の問題への進行を待っているか
    private boolean waitingForEnterAfterCorrect = false;
    
    private String previousInput = "";
    
    // 正解した時点の入力内容
    private String pendingCorrectInput = "";
    
    // 正解した時点の誤入力数
    private int pendingWrongCharCount = 0;
    
    private int score = 0;
    
    private void installDocumentListener() {

        gamePanel.getInputField().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                handleInputChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleInputChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }
    
    public GameController(JFrame frame,GameService gameService,
    		GamePanel gamePanel, ScoreService scoreService,
            int questionCounter, RomajiVariantService romajiVariantService, 
            GameSessionManager sessionManager, RomajiNormalizationService normalizationService,
            AudioManager audioManager) {
		Objects.requireNonNull(gameService, "gameService must not be null");
		Objects.requireNonNull(gamePanel, "gamePanel must not be null");
		Objects.requireNonNull(scoreService, "scoreService must not be null");
		Objects.requireNonNull(romajiVariantService, "romajiVariantService must not be null");
		Objects.requireNonNull(sessionManager, "sessionManager must not be null");
		Objects.requireNonNull(normalizationService, "normalizationService must not be null");
		Objects.requireNonNull(audioManager, "audioManager must not be null");
		
	    this.frame = frame;
		this.gameService = gameService;
		this.gamePanel = gamePanel;
        setupEnterKey();
		this.scoreService = scoreService;
		this.romajiVariantService = romajiVariantService;
		this.sessionManager = sessionManager;
		this.gameEngine = new GameEngine(normalizationService, romajiVariantService);
		this.questionCounter = questionCounter;
        this.isProcessingTimeout = false;
        this.isGameActive = false;
        this.audioManager = audioManager;

        installDocumentListener();
	}
    
    
    private void loadNextWord() {
    	
        if (!isGameActive) {
            return;
        }
        
    	System.out.println("loadNextWord");
    	
        waitingNextQuestion = false;
        waitingForEnterAfterCorrect = false;
        pendingCorrectInput = "";
        previousInput = "";
        pendingWrongCharCount = 0;
        
        Optional<TypingWord> opt = gameService.nextWord();

        if (opt.isEmpty()) {
            finishGame();
            return;
        }

        currentWord = opt.get();

        sessionManager.setCurrentWord(currentWord);

        gameEngine.startWord(currentWord);
        
        gamePanel.changeSushi();
        
        gamePanel.showWord(currentWord);

        gamePanel.showRomaji(romajiVariantService.getPreview(currentWord.getMoraJoined()));
        
        gamePanel.clearInput();

        gamePanel.getInputField().setEditable(true);
        gamePanel.getInputField().requestFocusInWindow();
        
        gamePanel.updateRemainingQuestions(sessionManager.getRemainingQuestions());
        System.out.println("showWord: " + romajiVariantService.getPreview(currentWord.getMoraJoined()));
        gameTimer.startQuestionTimer();
    }
    
    private void delayNextQuestion(int delayMs) {

        // 既に予約済みなら止める
        if (nextQuestionTimer != null) {
            nextQuestionTimer.stop();
        }

        nextQuestionTimer = new javax.swing.Timer(delayMs, _ -> {

            if (!isGameActive) {
                return;
            }

            if (sessionManager.isFinished()) {
                finishGame();
            } else {
                loadNextWord();
            }

        });

        nextQuestionTimer.setRepeats(false);
        nextQuestionTimer.start();
    }
    
    private void setupEnterKey() {
        JTextField inputField = gamePanel.getInputField();

        inputField.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("ENTER"), "advanceQuestion");

        inputField.getActionMap().put("advanceQuestion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isGameActive || currentWord == null) {
                    return;
                }

                // 正解後なら最終判定を行う
                if (waitingForEnterAfterCorrect) {
                    finalizeCorrectAnswer();
                    return;
                }

                // 正解前なら、従来どおり問題をスキップ
                advanceToNextQuestion();
            }
        });
    }

    private void advanceToNextQuestion() {

        // ゲームが終了している場合は何もしない
        if (!isGameActive || waitingNextQuestion || currentWord == null) {
            return;
        }

        // 現在の問題を二重処理しないようにする
        waitingNextQuestion = true;

        // 入力を受け付けないようにする
        gamePanel.getInputField().setEditable(false);

        // 次の問題への予約があればキャンセル
        if (nextQuestionTimer != null) {
            nextQuestionTimer.stop();
            nextQuestionTimer = null;
        }

        // 現在の問題タイマーとゲームタイマーを一時停止
        gameTimer.stopAll();

        // 現在入力されている文字列を取得
        String input = gamePanel.getInputText();

        // 入力内容から誤入力数を計算
        int wrongCount = gameEngine.getCumulativeWrongCount();

        // スキップした問題を不正解として判定
        String displayText =
                romajiVariantService.getPreview(currentWord.getMoraJoined());

        JudgeResult judgeResult = new JudgeResult(EvaluationResult.INCORRECT, displayText);
        
        // 問題結果を作成
        WordResult wordResult = new WordResult(
                currentWord,
                romajiVariantService.getPreview(
                        currentWord.getMoraJoined()
                ),
                input,
                judgeResult,
                wrongCount
        );

        // セッションに記録
        sessionManager.record(wordResult);

     // スキップ結果の記録確認
     System.out.println("===== スキップ記録確認 =====");
     System.out.println("判定結果: " + wordResult.result().result());
     System.out.println("isIncorrect: " + wordResult.isIncorrect());
     System.out.println("記録件数: " + sessionManager.getAnsweredCount());
     System.out.println("セッション記録件数: "
             + sessionManager.getSession().results().size());
     System.out.println("============================");
     
        // スコアを再計算して画面に反映
        updateCurrentScore();

        // 残り問題数を更新
        gamePanel.updateRemainingQuestions(
                sessionManager.getRemainingQuestions()
        );

        System.out.println("===== 問題スキップ =====");
        System.out.println("問題: " + currentWord.word());
        System.out.println("入力: " + input);
        System.out.println("誤入力数: " + wrongCount);
        System.out.println("判定: 不正解");
        System.out.println("========================");

        // 次の問題へ進む
        loadNextWord();
    }

    private void onCorrectAnswer(String input, PartialResult partialResult) {

        previousInput = "";

        // タイマー停止
        gameTimer.stopAll();

        // Enterキー待ち
        waitingNextQuestion = true;
        waitingForEnterAfterCorrect = true;

        // 正解時の情報を一時保存
        pendingCorrectInput = input;
        pendingWrongCharCount = partialResult.wrongCharCount();

        // 正解判定時に入力文字を表示
        gamePanel.showReward(input);
        
        // 追加文字を入力できる状態にする
        gamePanel.getInputField().setEditable(true);
        gamePanel.getInputField().requestFocusInWindow();

//        // 正解表示
//        gamePanel.showCorrect(input);

        System.out.println("正解時の入力: " + pendingCorrectInput);
        System.out.println("Enterキー待ち");
        
        
    }
    
//    private void onCorrectAnswer( String input, PartialResult partialResult) {
//
//        previousInput = "";
//        
//    	gamePanel.getInputField().setEditable(true);
//    	
//        gameTimer.stopAll();
//        
//        waitingNextQuestion = true;
//        waitingForEnterAfterCorrect = true;
//        
//        System.out.println("partialResult.wrongCharCount: " + partialResult.wrongCharCount());
//        JudgeResult judgeResult = new JudgeResult( EvaluationResult.CORRECT, input);
//
//        WordResult result = new WordResult(
//	        currentWord,
//	        romajiVariantService.getPreview( currentWord.getMoraJoined()),
//	        input,
//	        judgeResult,
//	        partialResult.wrongCharCount()
//        );
//
//        sessionManager.record(result);
//        
//        // 得点を再計算して表示
//        updateCurrentScore();
//        
//        gamePanel.showCorrect(input);
//
//        gamePanel.updateRemainingQuestions(sessionManager.getRemainingQuestions());
//
//    }
    
    private void finalizeCorrectAnswer() {

        if (!isGameActive || !waitingForEnterAfterCorrect) {
            return;
        }

        String finalInput = gamePanel.getInputText();

        // 正解時の入力とEnter時の入力を比較
        boolean isCorrect = pendingCorrectInput.equals(finalInput);

        // 正解後に余分な文字が入力されていた場合は、不正解表示に変更
        if (isCorrect) {
            gamePanel.showCorrect(pendingCorrectInput);
        } else {
            gamePanel.showMiss(pendingCorrectInput);
        }

        EvaluationResult evaluationResult = isCorrect
                ? EvaluationResult.CORRECT
                : EvaluationResult.INCORRECT;

        // 正解後に追加された文字数
        int additionalWrongCount = Math.max(
                0,
                finalInput.length() - pendingCorrectInput.length()
        );

        int totalWrongCount =
                pendingWrongCharCount + additionalWrongCount;

        JudgeResult judgeResult = new JudgeResult(
                evaluationResult,
                finalInput
        );

        WordResult wordResult = new WordResult(
                currentWord,
                romajiVariantService.getPreview(currentWord.getMoraJoined()),
                finalInput,
                judgeResult,
                totalWrongCount
        );

        // Enterキーを押した時点で初めて記録する
        sessionManager.record(wordResult);

        updateCurrentScore();

        gamePanel.updateRemainingQuestions(
                sessionManager.getRemainingQuestions()
        );

        System.out.println("===== 最終判定 =====");
        System.out.println("正解時の入力: " + pendingCorrectInput);
        System.out.println("Enter時の入力: " + finalInput);
        System.out.println("判定: " + evaluationResult);
        System.out.println("誤入力数: " + totalWrongCount);
        System.out.println("====================");

        // 状態をリセット
        waitingForEnterAfterCorrect = false;
        waitingNextQuestion = false;

        pendingCorrectInput = "";
        pendingWrongCharCount = 0;

        // 次の問題へ
        loadNextWord();
    }
    
    private void updateCurrentScore() {
        int correctCount = 0;
        int nearCount = 0;
        int incorrectCount = 0;
        int wrongCharCount = 0;
        int timeoutCount = 0;

        // 記録済みの問題結果を集計する
        for (WordResult result : sessionManager.getResults()) {
            if (result.isCorrect()) {
                correctCount++;
            }

            if (result.isNear()) {
                nearCount++;
            }

            if (result.isIncorrect()) {
                incorrectCount++;
            }

            if (result.isTimeout()) {
                timeoutCount++;
            }

            wrongCharCount += result.wrongCharCount();
        }

        // 現在の残り時間を使って得点を再計算する
        score = ScoreCalculator.calculateFinalScore(
                correctCount,
                nearCount,
                incorrectCount,
                wrongCharCount,
                timeoutCount,
                gameTimer.getGameTimeRemaining()
        );

        // 画面に現在の得点を表示する
        gamePanel.updateScore(score);
    }
    
    private void onTypingInput(String input) {
    	
        System.out.println("onTypingInput : " + input);

        if (!isGameActive || waitingNextQuestion) {
        	return;
        }

        // 問題切替直後の空入力を無視
        if (input.isEmpty()) {

            PartialResult partialResult = gameEngine.evaluatePartial(input);

            gamePanel.updateTypingDisplay(partialResult);

            return;
        }

        if (input.isBlank()) {

            PartialResult partialResult = gameEngine.evaluatePartial(input);
            gamePanel.updateTypingDisplay(partialResult);

            return;
        }
        
        PartialResult partialResult = gameEngine.evaluatePartial(input);

        System.out.println("===== PartialResult =====");
        System.out.println("input         = [" + input + "]");
        System.out.println("displayText   = [" + partialResult.displayText() + "]");
        System.out.println("correctPart   = [" + partialResult.correctPart() + "]");
        System.out.println("remainingPart = [" + partialResult.remainingPart() + "]");
        System.out.println("wrongCount    = " + partialResult.wrongCharCount());
        System.out.println("=========================");
        
        gamePanel.updateTypingDisplay(partialResult);
            
        if (gameEngine.isCompleteInput(input)) {
            onCorrectAnswer(input, partialResult);
        }
        
//        JudgeResult judgeResult = gameEngine.evaluate( input, currentWord, false);
//
//        System.out.println("judge = " + judgeResult.result());
//        System.out.println("complete = " + judgeResult.isCorrect());
        
//        if (gameEngine.isCompleteInput(input)) {
//            onCorrectAnswer(input, partialResult);
//        }
    }

    private void handleInputChanged() {

        if (!isGameActive || waitingNextQuestion) {
            return;
        }

        String currentInput = gamePanel.getInputText();

        // バックスペース
        if (currentInput.length() > previousInput.length()) {
            int addedCharacters = currentInput.length() - previousInput.length();
            gameEngine.recordTypedCharacters(addedCharacters);
        }

        onTypingInput(currentInput);

        previousInput = currentInput;
//        // 1文字追加
//        if (currentInput.length() == previousInput.length() + 1) {
//
//            boolean accepted = onTypingInput(currentInput);
//
//            if (accepted) {
//                previousInput = currentInput;
//            } else {
//                SwingUtilities.invokeLater(() ->
//                    gamePanel.setInputText(previousInput)
//                );
//            }
            
//        	onTypingInput(currentInput);
//
//        	previousInput = currentInput;

// 削除
// 正しい入力なら保存
//            if (gameEngine.getMatcher().getStates() != null
//                    && !gameEngine.getMatcher().getStates().isEmpty()) {
//
//                previousInput = currentInput;
//
//            } else {
//
//                // 間違った文字なので削除
//                SwingUtilities.invokeLater(() -> {
//                    gamePanel.setInputText(previousInput);
//                });
//            }
    }

        // その他（貼り付け等）
    
//    private void handleInputChanged() {
//
//        if (!isGameActive) {
//            return;
//        }
//
//        onTypingInput(gamePanel.getInputText());
//    }
    
    public void startGame(String userName, String difficulty, int questionCounter) {
    	
        Objects.requireNonNull(userName, "userName must not be null");
        Objects.requireNonNull(difficulty, "difficulty must not  be null");
        
        score = 0;

        // ゲーム全体の入力文字数をリセット
        gameEngine.resetTotalInputCharCount();
        
        System.out.println("\n========== ゲーム開始 ==========");
        System.out.println("ユーザー: " + userName);
        System.out.println("難易度: " + difficulty);
        System.out.println("問題数: " + questionCounter);
        
        this.userName = userName;
        this.difficulty = difficulty;
        this.questionCounter = questionCounter;
   	
        isProcessingTimeout = false;
        // ✅ 修正：ゲーム開始フラグをON
        isGameActive = true;

        audioManager.playDifficultyBgm(difficulty);
        
        sessionManager.start(new GameSession(userName, difficulty, questionCounter, score));
        
        gameService.loadWords(difficulty, questionCounter);
        
        gameTimer = new GameTimer(
                GAME_TIME_LIMIT,
                QUESTION_TIME_LIMIT,
                this::finishGame,
                this::onTimeout,
                gamePanel::updateGameTime,
                gamePanel::updateQuestionTime,
                audioManager
        );

    	System.out.println( gamePanel.getInputField().getKeyListeners().length);

    	loadNextWord();

        sushiTimer = new javax.swing.Timer(50,_ -> gamePanel.getSushiPanel().move());

        sushiTimer.start();
        
        SwingUtilities.invokeLater(() -> {
            gamePanel.getInputField().requestFocusInWindow();
        });
        
        gameTimer.startGameTimer();
        
        System.out.println("remaining: " + sessionManager.getSession().getRemainingQuestions());
        System.out.println("========== ゲーム開始完了 ==========\n");
    }
    
    private void onTimeout() {

//      if (!isGameActive || isProcessingTimeout) {
//      return;
//  }
//
//  previousInput = "";
//  
//  isProcessingTimeout = true;
    	
        if (!isGameActive || isProcessingTimeout || waitingNextQuestion) {
            return;
        }

        // タイムアウトした問題の二重処理を防ぐ
        waitingNextQuestion = true;

        // タイムアウト後の入力を禁止
        gamePanel.getInputField().setEditable(false);

        previousInput = "";

        isProcessingTimeout = true;

        try {
            // この下は既存の処理をそのまま残す
        	
            String input = gamePanel.getInputText();

            int wrongCount = gameEngine.calculateWrongCharCountForIncorrect(input);

            JudgeResult result = gameEngine.evaluate(input, currentWord, true);

            WordResult wordResult = new WordResult(
            	    currentWord,
            	    romajiVariantService.getPreview(currentWord.getMoraJoined()),
            	    input,
            	    result,
            	    wrongCount
            	);
            audioManager.playRandomStageMissVoice();
            
            sessionManager.record(wordResult);

            updateCurrentScore();
            
            gamePanel.showTimeout( result.displayText());

            gamePanel.updateRemainingQuestions( sessionManager.getRemainingQuestions());

            delayNextQuestion(1000);

        } finally {
            isProcessingTimeout = false;
        }
    }
    
    private void finishGame() {
        // ✅ 修正：ゲーム進行中フラグを最初にOFF（これ以上のイベント処理を受け付けない）
        isGameActive = false;
        
        // ゲームBGM停止
        audioManager.stopBgm();
        
        if (nextQuestionTimer != null && nextQuestionTimer.isRunning()) {
            nextQuestionTimer.stop();
        }
        
        gameTimer.stopAll();
        
        if (sushiTimer != null) {
            sushiTimer.stop();
        }
        
        GameResult result = gameService.finishGame(
            sessionManager.getSession(),
            gameTimer.getGameTimeLimit(),
            gameTimer.getGameTimeRemaining(),
            gameEngine.getTotalInputCharCount()
        );
        
        // クリアできなかったときのボイス
        if (result.correctCount() + result.incorrectCount() + result.timeoutCount() < 12) {
        	audioManager.playRandomStageOverVoice();
        } else {
            // クリア時のボイス
            audioManager.playRandomClearVoice(difficulty);
        }
        
        System.out.println("\n========== スコア計算 ==========");
        System.out.println("ゲーム結果:");
        System.out.println("  正解数: " + result.correctCount() + "問");
        System.out.println("  惜しい数: " + result.nearCount() + "問");
        System.out.println("  不正解数: " + result.incorrectCount() + "問");
        System.out.println("  タイムアウト: " + result.timeoutCount() + "問");
        System.out.println("  合計入力文字数: " + result.totalInputCharCount());
        System.out.println("  正しく入力できた文字数: " + result.correctCharCount());
        System.out.println("  間違えた文字数: " + result.wrongCharCount());
        System.out.println("  最終スコア: " + result.finalScore() + "点");
        System.out.println("  残り時間: " + gameTimer.getGameTimeRemaining() + "秒");
        
        try {      
            Score score = scoreService.createScore(
                userName,
                difficulty,
                result
            );
            scoreService.save(score);
            System.out.println("スコア保存完了: " + score.finalScore() + "点");

        } catch (Exception e) {
            System.err.println("スコア保存に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            frame.setVisible(false);
            
            ScoreResultUI resultUI = new ScoreResultUI();
            resultUI.showScoreResultScreen(result, this::restart);
        });
    }

    private void restart() {

        if (nextQuestionTimer != null) {
            nextQuestionTimer.stop();
            nextQuestionTimer = null;
        }

        if (sushiTimer != null) {
            sushiTimer.stop();
            sushiTimer = null;
        }

        if (gameTimer != null) {
            gameTimer.stopAll();
        }
        
        score = 0;
        
        System.out.println("========== リトライ ==========");
        System.out.println("開始画面を表示します");

        // 開始画面を表示
        StartGameUI startGameUI = new StartGameUI();

        startGameUI.waitForSpaceToStart(() -> {

            System.out.println("スペースキーが押されました");
            System.out.println("リトライゲーム開始");
            
            // 破棄していたゲーム画面を再表示
            frame.setVisible(true);

            // 新しいゲームを開始
            startGame(userName, difficulty, questionCounter);
        });
    }
}
