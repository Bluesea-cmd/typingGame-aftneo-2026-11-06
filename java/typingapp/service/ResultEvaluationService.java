package typingapp.service;


import typingapp.model.GameResult;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ResultEvaluationService {

	private static final int MANY_MISTAKES = 8;
	private static final int SOME_MISTAKES = 4;
	
	private static final double SPEED_SLOW = 1.0;
	private static final double SPEED_NORMAL = 2.0;
	private static final double SPEED_FAST = 3.0;
	private static final double SPEED_PRO = 4.0;
	
    private static final String DIFFICULTY_EASY = "初級";
    private static final String DIFFICULTY_NORMAL = "中級";
    
    // 不正解&タイムアウト数によってコメントを変更する
    private static final List<Rule<Integer>> MISTAKE_RULES = List.of(
    	    new Rule<>(m -> m > MANY_MISTAKES,
    	            "間違えすぎ！もっと気を付けて入力しよう！"),

    	    new Rule<>(m -> m > SOME_MISTAKES,
    	            "ミスが多めです...落ち着いて入力してみましょう！"),

    	    new Rule<>(m -> m > 0,
    	            "惜しい！気を付けて入力しよう！")
    	);
    
    private static final List<Rule<Integer>> TIMEOUT_RULES = List.of(

    	    new Rule<>(t -> t > MANY_MISTAKES,
    	            "もっと早く入力しよう！"),

    	    new Rule<>(t -> t > SOME_MISTAKES,
    	            "もう少し早く入力しよう"),

    	    new Rule<>(t -> t > 0,
    	            "惜しい！！！もう少し早く入力しよう！")
    	);
    
    private static final List<Rule<Double>> SPEED_RULES = List.of(

    	    new Rule<>(s -> s == 0.0,
    	            "残り時間が0なため、計測不可。入力が終わったらすぐに「enter」キーを押そう！"),

    	    new Rule<>(s -> s < SPEED_SLOW,
    	            "ギリギリセーフ！。もっと努力しよう！"),

    	    new Rule<>(s -> s < SPEED_NORMAL,
    	            "頑張ったね。しかしもっと努力しよう！"),

    	    new Rule<>(s -> s < SPEED_FAST,
    	            "素晴らしい！もっと上を目指せるかも！"),

    	    new Rule<>(s -> s < SPEED_PRO,
    	            "素晴らしい！これ以上言うことなし！")
    	);
    
    private static final List<Rule<Integer>> SCORE_RULES = List.of(

    	    new Rule<>(s -> s <= 50,
    	            "とても悪いスコアです...。もっと頑張りましょう。"),

    	    new Rule<>(s -> s <= 100,
    	            "まあまあのスコアです。もう少し頑張りましょう！"),

    	    new Rule<>(s -> s <= 150,
    	            "良いスコアです！その調子で頑張りましょう！！！"),

    	    new Rule<>(s -> s <= 200,
    	            "とても良いスコアです！その調子で頑張りましょう！！！")
    	);
    
	public record EvaluationMessages(
		    String mistake,
		    String timeout,
		    String speed,
		    String rank
		) {
		public EvaluationMessages {
	        Objects.requireNonNull(mistake, "mistake message is null");
	        Objects.requireNonNull(timeout, "timeout message is null");
	        Objects.requireNonNull(speed, "speed message is null");
	        Objects.requireNonNull(rank, "rank message is null");
	    }
	}
	
    private record Rule<T>(Predicate<T> condition, String message) {}

    // 評価コメントを判定して挿入（間違い＞タイムアウト＞平均タイプ数＞総合スコア の順）
    public EvaluationMessages generate(GameResult result) { 
        Objects.requireNonNull(result, "result must not be null");

    	return new EvaluationMessages (
        buildMistakeMessage(result),
        buildTimeoutMessage(result),
        buildSpeedMessage(result),
        buildTotalMessage(result)
        );
    }

    private <T> String evaluateRules(T value, List<Rule<T>> rules, String defaultMessage) {

        for (Rule<T> rule : rules) {
            if (rule.condition.test(value)) {
                return rule.message;
            }
        }

        return defaultMessage;
    }
   
    private String buildMistakeMessage(GameResult result) {
		
      // 不正解&タイムアウト数を合計してカウント
      int totalMistakes = result.incorrectCount() + result.timeoutCount();
      
      return evaluateRules(
    	        totalMistakes,
    	        MISTAKE_RULES,
    	        "全問正解！素晴らしい！！！"
    	);
  }
    
	private String buildTimeoutMessage(GameResult result) {
		
		// タイムアウト数によってコメントを変更する
		int timeout = result.timeoutCount();
		
		return evaluateRules( timeout, TIMEOUT_RULES, "タイムアウト無し！素晴らしい！！！" );
    }
	
    private String buildSpeedMessage(GameResult result) {
    	
    	// 平均キータイプ数によってコメントを変更する(4.0以上はめったにないかも)
    	double speed = result.averageTypingSpeed();
    	
    	return evaluateRules( speed, SPEED_RULES, "あまりにも素早すぎて最高！！！" );
    }
    
	private String buildTotalMessage(GameResult result) { 
		
	// 総合評価の算出
	int score = result.finalScore();
	
	String base = evaluateRules(score, SCORE_RULES, null);

	if (base != null) { return base; }
	
	// 初級や中級でスコアが250以上、且つ、ノーミスなら次の難易度に挑むように促す、メッセ―ジを出す
	
	if (score <= 250) {
		// 初級や中級でスコアが250以上、且つ、ノーミスなら次の難易度に挑むように促す、メッセ―ジを出す
		if ((DIFFICULTY_EASY.equals(result.difficulty()) || DIFFICULTY_NORMAL.equals(result.difficulty()))
				&& result.correctCount() == result.totalQuestions()) {
			return "素晴らしい！！！次の難易度に挑戦してみませんか？";
		}
		
		// ミスがあった場合
		if (result.correctCount() < result.totalQuestions()) {
			return "素晴らしい！！！あとはノーミスを目指そう！！！";
		}
		return "驚異的なスコアです！！！";
	}
	return "驚異的なスコアです！！！(特殊エラー)";
	}
}