package typingapp.service;

import java.util.Objects;

import typingapp.model.EvaluationResult;
import typingapp.model.JudgeResult;
import typingapp.model.PartialResult;
import typingapp.model.TypingWord;

public class GameEngine {      
	
    private static final int NEAR_DISTANCE_MIN = 1;
    private static final int NEAR_DISTANCE_MAX = 2;
    
    private final RomajiMatcher matcher;
    private final RomajiNormalizationService normalizationService;
	private final RomajiVariantService romajiVariantService;

	// 単語開始時の初期モラ（リセット用）
    private TypingWord currentWord;
	
	// 前回評価した入力文字列
    private String previousInput = "";

    // 単語中に発生した累積ミス数
    private int cumulativeWrongCount = 0;
    
    private Boolean wrongFlag = false;
    
    private int totalInputCharCount = 0;
    
    public GameEngine(RomajiNormalizationService normalizationService,
    				  RomajiVariantService romajiVariantService) {
        Objects.requireNonNull(normalizationService, "normalizationService must not be null");
        Objects.requireNonNull(romajiVariantService, "romajiVariantService must not be null");

        this.matcher = new RomajiMatcher(romajiVariantService);
        this.normalizationService = normalizationService;
        this.romajiVariantService = romajiVariantService;
    }
    
    public void startWord(TypingWord word) {
        this.currentWord = word;

        // 新しい単語なので入力状態をリセット
        this.previousInput = "";
        this.cumulativeWrongCount = 0;

        matcher.init(word.getMoraJoined());
    }
    
//    public void startWord(TypingWord word){
//    	this.currentWord = word;
//        matcher.init(word.getMoraJoined());
//    }
    
    private PartialResult createPartialResult(String input, int cumulativeWrongCount) {

        // 正解部分と残り部分のプレビューを取得
        String correctPart = matcher.getCorrectPreview();
        String remainingPart = matcher.getRemainingPreview();

        String display = correctPart + remainingPart;

        boolean[] wrongFlags = new boolean[display.length()];

        // 現在の入力が正解部分を超えている場合、
        // 現在の位置をミス文字として表示
//        boolean isCurrentlyWrong = input.length() > matcher.getCorrectInputLength();
//
//        if (isCurrentlyWrong && correctPart.length() < display.length()) {
//            wrongFlags[correctPart.length()] = true;
//        }

        // 実際の入力履歴からミス位置を取得する
        for (RomajiMatcher.CharacterStatus status
                : matcher.getCharacterHistory()) {

            if (status.correct()) {
                continue;
            }

            int index = status.previewIndex();

            if (index >= 0 && index < wrongFlags.length) {
                wrongFlags[index] = true;
                wrongFlag = true;
            }
        }
        System.out.println("cumulativeWrongCount: " + cumulativeWrongCount);
        
        return new PartialResult(
                display,
                correctPart,
                wrongFlag,
                remainingPart,
                cumulativeWrongCount,
                wrongFlags
        );
    }
//    private PartialResult createPartialResult(String input) {
//
//    	// 正解部分と残り部分のプレビューを取得
//        String correctPart = matcher.getCorrectPreview();
//        String remainingPart = matcher.getRemainingPreview();
//        
////        StringBuilder display = new StringBuilder();
//        String display = correctPart + remainingPart;
//        
////        List<RomajiMatcher.CharacterStatus> history = matcher.getCharacterHistory();
//        List<CharacterStatus> history = matcher.getCharacterHistory();
//            
////        String answer = matcher.getCorrectPreview() + matcher.getRemainingPreview();
//        
//        int wrongCount = 0;
//       
//        // ミス入力のカウント
//        for (CharacterStatus s : history) {
//            if (!s.correct()) {
//                wrongCount++;
//            }
//        }
//        
//        
//        boolean[] wrongFlags = new boolean[display.length()];
//        
//        boolean isCurrentlyWrong = input.length() > matcher.getCorrectInputLength();
//        
//        if (isCurrentlyWrong && correctPart.length() < display.length()) {
//            wrongFlags[correctPart.length()] = true;
//        }
//        
//////        String display = correct + remaining;
//////
//////        boolean[] wrongFlags = new boolean[display.length()];
////
////        
//////        int displayIndex = 0;
//////
//////        // 追加
//////        StringBuilder correctPart = new StringBuilder();
//////        StringBuilder wrongPart = new StringBuilder();
//////        
//////        int inputIndex = 0;
//////        int index = 0;
////
////        int answerIndex = 0;
////        
////        for (int i = 0; i < history.size(); i++) {
////
////            CharacterStatus s = history.get(i);
////
////            display.append(s.character());
////
////            if (s.correct()) {
////            	answerIndex++;
////            } else {
////                wrongFlags[i] = true;
////                wrongCount++;
////            }
////        }
//////        for(CharacterStatus s : history){
//////            display.append(s.character());
//////
//////            if (!s.correct()) {
//////
////////                int index = display.length() - 1;
//////
//////                if (answerIndex < answer.length()) {
//////                    display.append(answer.charAt(answerIndex));
//////                    answerIndex++;
//////                }
////////
//////            }else{
//////
//////                display.append(s.character());
//////
//////                if(answerIndex < wrongFlags.length){
//////                    wrongFlags[answerIndex] = true;
//////                }
//////
//////                wrongCount++;
//////            }
//////        }
////
//////        if(answerIndex<answer.length()){
//////            display.append(answer.substring(answerIndex));
//////        }
////        
//////        for (RomajiMatcher.CharacterStatus status : matcher.getCharacterHistory()) {
//////            if (!status.correct()) {
//////                if (index < wrongFlags.length) {
//////                    wrongFlags[index] = true;
//////                }
//////                wrongCount++;
//////            }
//////            index++;
//////            
////////        for (RomajiMatcher.InputRecord record : matcher.getCharacterHistory()) {
////////
////////            if (record.type() == RomajiMatcher.InputType.WRONG) {
////////
////////                int pos = record.previewIndex();
////////
////////                if (0 <= pos && pos < wrongFlags.length) {
////////
////////                    wrongFlags[pos] = true;
////////                }
////////
////////                wrongCount++;
////////            }
//////        }
//////        String wrong = "";
////
//////        if (wrongCount > 0) {
//////
//////            StringBuilder sb = new StringBuilder();
//////            
//////            for (int i = 0; i < history.size(); i++) {
//////
//////                RomajiMatcher.CharacterStatus status = history.get(i);
//////
//////                if (status.correct()) {
//////
//////                    display.append(answer.charAt(answerIndex));
//////
//////                    answerIndex++;
//////                } else {
//////
//////                    display.append(status.character());
//////
//////                    wrongFlags[i] = true;
//////                }
//////            }
//////            
//////            if (answerIndex < answer.length()) {
//////
//////                display.append(answer.substring(answerIndex));
//////            }
//////        }
//////        
////////            for (int i = 0; i < matcher.getCharacterHistory().size(); i++) {
////////
////////                RomajiMatcher.CharacterStatus status =
////////                        matcher.getCharacterHistory().get(i);
////////
////////                if (!status.correct()) {
////////                    sb.append(status.character());
////////                }
////////            }
//////            
//////////            for (RomajiMatcher.InputRecord record : matcher.getCharacterHistory()) {
//////////
//////////                if (record.type() == RomajiMatcher.InputType.WRONG) {
//////////
//////////                    int index = record.previewIndex();
//////////
//////////                    if (index < input.length()) {
//////////                        sb.append(input.charAt(index));
//////////                   }
//////////                }
//////////           }
//////
//////////           wrong = sb.toString();
//////////        }
////
////        if(answerIndex < answer.length()) {
////            display.append(answer.substring(answerIndex));
////        }
//        
//        return new PartialResult(
//        		display,
//                correctPart,
//                remainingPart,
//                wrongCount,
//                wrongFlags);
//    }
    
    private String getDisplayText(TypingWord word) {
        return romajiVariantService.getPreview(word.getMoraJoined());
    }
    
    // 文字キーが入力されたときに呼び出す。
    // Backspaceによる削除では呼び出さない。
    // @param count 新しく入力された文字数
    public void recordTypedCharacters(int count) {
        if (count < 0) {
            throw new IllegalArgumentException(
                "count must not be negative: " + count
            );
        }

        totalInputCharCount += count;
    }

    // 新しいゲームを開始するときに入力文字数をリセットする。
    public void resetTotalInputCharCount() {
        totalInputCharCount = 0;
    }
    
    // ゲーム全体の入力文字数を取得する。
    public int getTotalInputCharCount() {
        return totalInputCharCount;
    }
    
    public PartialResult evaluatePartial(String input) {

        Objects.requireNonNull(input, "input must not be null");

        System.out.println("=== evaluatePartial DEBUG ===");
        System.out.println("入力文字列: '" + input + "'");
        System.out.println("前回入力: '" + previousInput + "'");

        // Backspaceされた場合
        if (input.length() < previousInput.length()) {

            int deleteCount = previousInput.length() - input.length();

            for (int i = 0; i < deleteCount; i++) {
                matcher.backspace();
            }

            // Backspaceしても過去のミス数は減らさない
            previousInput = input;

            System.out.println("Backspace: " + deleteCount + "文字");
            System.out.println("累積ミス数: " + cumulativeWrongCount);

            return createPartialResult(input, cumulativeWrongCount);
        }

        // 新しく入力された文字だけ処理
        if (input.length() > previousInput.length()) {

            for (int i = previousInput.length(); i < input.length(); i++) {

                char c = input.charAt(i);
                
                RomajiMatcher.InputResult result = matcher.input(c);

                if (result == RomajiMatcher.InputResult.WRONG) {

                    cumulativeWrongCount++;

                    System.out.println(
                            "❌ 間違った入力: '" + c
                            + "' → 累積ミス数: "
                            + cumulativeWrongCount
                    );

                } else if (result == RomajiMatcher.InputResult.COMPLETE) {
                    System.out.println( "⭕ 正しい入力: '" + c + "' → 完成");
                } else {
                    System.out.println( "⭕ 正しい入力: '" + c + "'");
                }
            }
        }

        previousInput = input;

        System.out.println( "現在の累積ミス数: " + cumulativeWrongCount);

        return createPartialResult(input, cumulativeWrongCount);
    }
    
//    public PartialResult evaluatePartial(String input) {
//
//        Objects.requireNonNull(input);
//
////        matcher.reset();
//        
//        System.out.println("=== evaluatePartial DEBUG ===");
//        System.out.println("入力文字列: '" + input + "'");
//     // 状態を一旦リセット（単語の最初から再読み込み）
//        if (currentWord != null) {
//            matcher.init(currentWord.getMoraJoined());
//        } else {
//            matcher.reset();
//        }
//
//        // 入力された文字列の先頭から1文字ずつ matcher に投入
//        for (char c : input.toCharArray()) {
//            matcher.input(c);
//        }
//
//        return createPartialResult(input);
//    }
        
//        if (input.length() > previousInput.length()) {
//
//            // 追加された文字だけ処理
//            for (int i = previousInput.length(); i < input.length(); i++) {
//                matcher.input(input.charAt(i));
//            }
//
//        } else if (input.length() < previousInput.length()) {
//
//            // Backspace
//            while (previousInput.length() > input.length()) {
//                matcher.backspace();
//                previousInput = previousInput.substring(0, previousInput.length() - 1);
//            }
//        }
//
//        previousInput = input;
//
//        return createPartialResult(input);
//    }
    
//削除
//    public PartialResult evaluatePartial(String input) {
//        Objects.requireNonNull(input, "input must not be null");
//        
//        String correct = matcher.getCurrentPreview();
//
//        int consumed = matcher.getCorrectInputLength();
//
//        String wrong = "";
//
//        if (input.length() > consumed) {
//            wrong = input.substring(consumed);
//        }
//       
//        System.out.println("=== evaluatePartial DEBUG ===");
//        System.out.println("入力文字列: '" + input + "'");
//        
//        return new PartialResult(
//                correct,
//                wrong,
//                matcher.getRemainingPreview(),
//                wrong.length());
//    }
    
//    private String getRemainingPreview(TypingWord word, String correctInput) {
//
//        String preview = romajiVariantService.getPreview(word.getMoraJoined());
//
//        if (correctInput.length() >= preview.length()) {
//            return "";
//        }
//
//        return preview.substring(correctInput.length());
//    }
    
//    public PartialResult evaluatePartial(String input) {
//        Objects.requireNonNull(input);
//
//        System.out.println("=== evaluatePartial DEBUG ===");
//        System.out.println("入力文字列: '" + input + "'");
//
//        String correct = matcher.getCorrectInput();
//
//        String wrong = "";
//
//        if (input.length() > correct.length()) {
//            wrong = input.substring(correct.length());
//        }
//
//        String remaining = matcher.getRemainingPreview();
//
//        return new PartialResult(
//                correct,
//                wrong,
//                remaining,
//                wrong.length());
//    }
    
//        RomajiMatcher tempMatcher = new RomajiMatcher(romajiVariantService);
//        tempMatcher.init(word.getMoraJoined());
//
//        StringBuilder correct = new StringBuilder();
//        int correctLength = 0;
//
//        for (char c : input.toCharArray()) {
//            boolean result = tempMatcher.input(c);
//            System.out.println("文字 '" + c + "' -> " + result + " (correct: " + correct.toString() + ")");
//            if (result) {
//                correct.append(c);
//                correctLength++;
//            } else {
//                break;
//            }
//        }
//
//        String correctPart = matcher.getCorrectPreview();
//        String wrongPart = input.substring(correctLength);
//        
//        System.out.println("correctPart: '" + correctPart + "'");
//        System.out.println("wrongPart: '" + wrongPart + "'");
//        System.out.println("=== END DEBUG ===\n");
//        
//        int wrongCharCount = wrongPart.length();
//        
//        if (input.isBlank()) {
//            wrongCharCount = 0;
//        }
//        
//        if (!wrongPart.isEmpty()) {
//            System.out.println("❌ 間違った入力: " + wrongPart + " (文字数: " + wrongCharCount + ")");
//        }
//        
//        String remainingPart = getRemainingPreview(word, correctPart);
//        
//        PartialResult result = new PartialResult(correctPart, wrongPart, remainingPart, wrongCharCount);
//        
//        return result;
//    }
    
//    private int calculateCorrectLength( String input, TypingWord word) {
//
//        matcher.init(word.getMoraJoined());
//
//        int length = 0;
//
//        for (char c : input.toCharArray()) {
//            if (!matcher.input(c)) { break;}
//            length++;
//        }
//
//        return length;
//    }
    
    public int calculateWrongCharCountForIncorrect( String input) {
        if (input.isBlank()) {
        	return 0;
        }
        
        return Math.max(0, input.length() - matcher.getCorrectInputLength());
    }

    //現在の問題で発生した累積ミス数を取得する
	//@return 現在の問題の累積ミス数

    public int getCumulativeWrongCount() {
        return cumulativeWrongCount;
    }
    
    private int levenshteinDistance(String a, String b) {

        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {

                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[a.length()][b.length()];
    }
    
    public RomajiMatcher.InputResult input(char c) {
        return matcher.input(c);
    }

    public void backspace() {
        matcher.backspace();
    }
    
    private boolean isNear( String input, TypingWord word) {
    	
        String normalizedInput = normalizationService.normalize(input);
        String normalizedWord = normalizationService.normalize(word.getMoraJoined());
        
        int distance = levenshteinDistance( normalizedInput, normalizedWord);       

//      if (distance >= NEAR_DISTANCE_MIN && distance <= NEAR_DISTANCE_MAX) {
//         return new JudgeResult(
//                  EvaluationResult.NEAR,
//                  displayText
//          );
//      }
        
        return distance >= NEAR_DISTANCE_MIN && distance <= NEAR_DISTANCE_MAX;
    }
    
    public boolean isCompleteInput(String input) {
//        // 全モーラが完成し、かつ入力長が正確に一致する場合のみ正解
//        if (!matcher.isComplete()) {
//            return false;
//        }
        
        // currentWordがnullの場合は不正解
        if (currentWord == null) {
            return false;
        }
        
//        // 正解の正確な長さを取得
//        String correctAnswer = getDisplayText(currentWord);
//        
//        // 入力長が正確に一致するかチェック
//        return input.length() == correctAnswer.length();
        return matcher.isComplete();
    }
    
//    public boolean isCompleteInput(String input) {
//
//        matcher.reset();
//
//        for (char c : input.toCharArray()) {
//            matcher.input(c);
//        }
//
//        return matcher.isComplete();
//    }
    
//	リファクタリング前のコード(////はそのまえにリファクタリングした時の変更前のコード)
//    private boolean isCompleteInput(String input, TypingWord word) {
//
////  	boolean alive = true;
//
////      if (!matcher.input(c)) {
////      	alive = false;
////      	break;
////  	}
////  
////		if (alive && matcher.isComplete()) {
////  		return new JudgeResult( EvaluationResult.CORRECT, input );
////		}
//
//        matcher.init(word.getMoraJoined());
//        
//        for (char c : input.toCharArray()) {
//
//            boolean ok = matcher.input(c);
//
//            System.out.println( c + " -> " + ok + " states=" + matcher.getStates().size() );
//
//            if (!ok) {
//                return false;
//            }
//            
////      	if (!matcher.input(c)) {
////     			alive = false;
////      		break;
////  		}
//            
//// 			if (alive && matcher.isComplete()) {
////          	return new JudgeResult( EvaluationResult.CORRECT,input);
////			}
//        }
//        
//        System.out.println("isComplete = " + matcher.isComplete());
//
//        return matcher.isComplete();
//    }
    
    public JudgeResult evaluate(String input, TypingWord word, boolean isTimeout) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(word);

        String displayText = getDisplayText(word);
        
        if (input.isBlank()) {
            return new JudgeResult(
                    EvaluationResult.INCORRECT,
                    displayText
            );
        }
        
        if (isCompleteInput(input)) {
            return new JudgeResult(
                    EvaluationResult.CORRECT,
                    input
            );
        }
        
//		  リファクタリング前2
//        if (isCompleteInput()) {
//
//            return new JudgeResult(
//                    EvaluationResult.CORRECT,
//                    input
//            );
//        }
        
//			リファクタリング前1
//        	if (isCompleteInput(input, word)) {
//          	return new JudgeResult(
//              	EvaluationResult.CORRECT,
//              	input
//            );
//        }

        if (isTimeout) {
            return new JudgeResult(
                    EvaluationResult.TIMEOUT,
                    displayText
            );
        }

        if (isNear(input, word)) {
            return new JudgeResult( EvaluationResult.NEAR, displayText );
        }

        return new JudgeResult(
                EvaluationResult.INCORRECT,
                displayText
        );
    }
    
    public RomajiMatcher getMatcher() {
        return matcher;
    }
    
}
