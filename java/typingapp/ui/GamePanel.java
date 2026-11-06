package typingapp.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import typingapp.model.PartialResult;
import typingapp.model.TypingWord;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

    private static final double BASE_WIDTH = 1920.0;
    private static final double BASE_HEIGHT = 1080.0;
    
    private final JLabel wordLabel;
    private final JLabel readingLabel;
    private final JLabel normalizedArea1;

    private final JTextField inputField;
    private final JTextPane typingLabel;

    private final JLabel quizRewardLabel;
    private final JLabel questionTimerLabel;
    private final JLabel gameTimerLabel;

    private final JLabel resultLabel;
    private final JLabel RewardResultLabel;
    private final SushiPanel sushiPanel;
    private final JLabel scoreLabel;
    private final JLabel tipsLabel;
    
    private final String difficulty;
    
    private final String[] tips = {
    	    "<html>TIPS : ローマ字は複数候補がある場合があります<br>"
    	    + "TIPS : 「し」は shi / si の両方が使えます</html>",

    	    "<html>TIPS : 基本的に半角英数字入力(A)モードにしてください<br>"
    	    + "TIPS : 「・」はひらがな入力モードにする必要があります</html>"
    	};

    	private int tipIndex = 0;
    	private Timer tipTimer;
    	
    	private final TypingDisplayRenderer renderer;
    	       
    	public GamePanel(int totalQuestions, String difficulty) {
    		System.out.println("GamePanel created");
    	    this.difficulty = difficulty;
    	    
            setOpaque(false);
    	    
            setLayout(null);

    	    wordLabel = new JLabel("", SwingConstants.CENTER);

    	    readingLabel = new JLabel("", SwingConstants.CENTER);

    	    normalizedArea1 = new JLabel("", SwingConstants.CENTER);

    	    inputField = new JTextField();

    	    typingLabel = new JTextPane();
    	    typingLabel.setEditable(false);
    	    typingLabel.setFocusable(false);
    	    
    	    quizRewardLabel = new JLabel("残り問題数 : " + totalQuestions, SwingConstants.CENTER);

    	    questionTimerLabel = new JLabel("問題残り時間 : 0秒");

    	    gameTimerLabel = new JLabel("ゲーム残り時間 : 0秒");

    	    resultLabel = new JLabel("");
    	    RewardResultLabel = new JLabel("");
    	    
    	    sushiPanel = new SushiPanel(difficulty);

    	    scoreLabel = new JLabel("現在の点数 : 0");

    	    tipsLabel = new JLabel("", SwingConstants.LEFT);

    	    renderer = new TypingDisplayRenderer();
    	    
    	    initializeBounds();
    	    addComponents();
    	    
    	    // --------------------------
    	    // Tips初期表示
    	    // --------------------------
    	    tipsLabel.setText(tips[0]);

    	    tipTimer = new Timer(5000, _ -> {
    	        tipIndex = (tipIndex + 1) % tips.length;
    	        tipsLabel.setText(tips[tipIndex]);
    	    });

    	    tipTimer.start();
    	}
    	
    	private int getScaleFontSize(int baseSize) {

    	    int width = getWidth();
    	    int height = getHeight();

    	    if (width <= 0 || height <= 0) {
    	        return baseSize;
    	    }

    	    double scaleX = width / BASE_WIDTH;
    	    double scaleY = height / BASE_HEIGHT;

    	    double scale = Math.min(scaleX, scaleY);

    	    return Math.max(8, (int) Math.round(baseSize * scale));
    	}
    	
    	private int getFontSize(int baseSize, double scale) {
    	    return Math.max(8, (int) Math.round(baseSize * scale));
    	}
    	
    	private void updateFontSizes(double scale) {

    	    int sideSize = getFontSize(30, scale);

    	    gameTimerLabel.setFont( new Font("MS Gothic", Font.BOLD, sideSize));
    	    questionTimerLabel.setFont( new Font("MS Gothic", Font.BOLD, sideSize));
    	    quizRewardLabel.setFont( new Font("MS Gothic", Font.BOLD, sideSize));
    	    readingLabel.setFont( new Font("MS Gothic", Font.PLAIN, getFontSize(60, scale)));
    	    wordLabel.setFont( new Font("MS Gothic", Font.BOLD, getFontSize(70, scale)));
    	    normalizedArea1.setFont( new Font("MS Gothic", Font.BOLD, getFontSize(70, scale)));
    	    
    	    typingLabel.setFont( new Font("Consolas", Font.BOLD, getFontSize(75, scale)));
    	    inputField.setFont( new Font("Consolas", Font.BOLD, getFontSize(75, scale)));
    	    
    	    resultLabel.setFont( new Font("MS Gothic", Font.BOLD, sideSize));
    	    RewardResultLabel.setFont( new Font("MS Gothic", Font.BOLD, sideSize));
    	    
    	    scoreLabel.setFont( new Font("MS Gothic", Font.BOLD, sideSize));
    	    tipsLabel.setFont( new Font("MS Gothic", Font.PLAIN, sideSize));
    	}
    	
    	private void initializeBounds() {

    	    Font sideFont = new Font("MS Gothic", Font.BOLD, 140);

    	    gameTimerLabel.setFont(sideFont);
    	    questionTimerLabel.setFont(sideFont);
    	    quizRewardLabel.setFont(sideFont);
    	    readingLabel.setFont(new Font("MS Gothic", Font.PLAIN, 120));
    	    wordLabel.setFont(sideFont);
    	    normalizedArea1.setFont(sideFont);
    	    
    	    typingLabel.setFont(new Font("Consolas", Font.BOLD, 150));
    	    typingLabel.setBackground(new Color(235, 235, 235));
    	    typingLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    	    
    	    inputField.setFont(new Font("Consolas", Font.BOLD, 150));
    	    
    	    resultLabel.setFont(sideFont);
    	    RewardResultLabel.setFont(sideFont);
    	    scoreLabel.setFont(sideFont);
    	    tipsLabel.setFont(sideFont);
    	}
    
    private void addComponents() {
        add(gameTimerLabel);
        add(questionTimerLabel);
        add(quizRewardLabel);
        add(readingLabel);
        add(wordLabel);
        add(normalizedArea1);

        add(typingLabel);
        add(inputField);
       
        add(resultLabel);
        add(RewardResultLabel);
        
        add(scoreLabel);
        add(tipsLabel);

        add(sushiPanel);
    }

    private int scaleX(double value, double scale) {
        return (int) Math.round(value * scale);
    }

    private int scaleY(double value, double scale) {
        return (int) Math.round(value * scale);
    }

    private int scaleWidth(double value, double scale) {
        return (int) Math.round(value * scale);
    }

    private int scaleHeight(double value, double scale) {
        return (int) Math.round(value * scale);
    }
    
    @Override
    public void doLayout() {

    	super.doLayout();
    	
        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        double scaleX = width / BASE_WIDTH;
        double scaleY = height / BASE_HEIGHT;
        
        double scale = Math.min(scaleX, scaleY);
        
        int scaleWidthConst = 800;
        int scaleHeightConst = 80;
        
        // ゲーム残り時間        
        gameTimerLabel.setBounds(
        	scaleX(15, scaleX),
        	scaleY(50, scaleY),
        	scaleWidth(400, scaleX),
        	scaleHeight(35, scaleY)
        );      
        

        // 問題残り時間
        questionTimerLabel.setBounds(
    		scaleX(15, scaleX),
    		scaleY(100, scaleY),
    		scaleWidth(400, scaleX),
    		scaleHeight(35, scaleY)
        );
        
        // 残り問題数
        quizRewardLabel.setBounds(
        	scaleX(650, scaleX),
        	scaleY(50, scaleY),
        	scaleWidth(scaleWidthConst + 200, scaleX),
        	scaleHeight(scaleHeightConst, scaleY)
        );

        // 読み仮名
        readingLabel.setBounds(
        	scaleX(650, scaleX),
        	scaleY(45, scaleY),
        	scaleWidth(scaleWidthConst + 200, scaleX),
        	scaleHeight(scaleHeightConst, scaleY)
        );
        
        // 単語
        wordLabel.setBounds(
        	scaleX(650, scaleX),
        	scaleY(130, scaleY),
        	scaleWidth(scaleWidthConst + 200, scaleX),
        	scaleHeight(scaleHeightConst, scaleY)
        );
        
        // ローマ字
        normalizedArea1.setBounds(
        	scaleX(650, scaleX),
        	scaleY(210, scaleY),
        	scaleWidth(scaleWidthConst + 200, scaleX),
        	scaleHeight(scaleHeightConst, scaleY)
        );
        
        // タイピング表示
        typingLabel.setBounds(
    		scaleX(650, scaleX),
    		scaleY(300, scaleY),
    		scaleWidth(scaleWidthConst + 200, scaleX),
    		scaleHeight(scaleHeightConst, scaleY)
        );
        
        // 入力欄
        inputField.setBounds(
        	scaleX(650, scaleX),
        	scaleY(400, scaleY),
        	scaleWidth(scaleWidthConst + 200, scaleX),
        	scaleHeight(scaleHeightConst, scaleY)
        );
        
        // 結果
        resultLabel.setBounds(
        	scaleX(650, scaleX),
        	scaleY(475, scaleY),
        	scaleWidth(scaleWidthConst, scaleX),
        	scaleHeight(scaleHeightConst, scaleY)
        );
        
        RewardResultLabel.setBounds(
        	scaleX(650, scaleX),
        	scaleY(500, scaleY),
        	scaleWidth(scaleWidthConst, scaleX),
        	scaleHeight(scaleHeightConst, scaleY)
        );
        
        // 現スコア数
        scoreLabel.setBounds(
        	scaleX(50, scaleX),
        	scaleY(980, scaleY),
        	scaleWidth(400, scaleX),
        	scaleHeight(160, scaleY)
        );
        
        // TIPS
        tipsLabel.setBounds(
        	scaleX(500, scaleX),
        	scaleY(980, scaleY),
        	scaleWidth(1500, scaleX),
        	scaleHeight(70, scaleY)
        );
        
        // 寿司は画面全体を使用
        sushiPanel.setBounds(0,0,width,height);
        
        updateFontSizes(scale);
	    
    }
    
    public JTextField getInputField() { return inputField; }

    public String getInputText() { return inputField.getText().trim(); }

    public void setInputText(String text) { inputField.setText(text); }
    
    public void clearInput() {
    	
    	typingLabel.setText("");
        inputField.setText("");
        
    }
    
    private void setWordText(String text) {

        int baseSize = 20;

        if (text.length() > 15) {
            baseSize = 15;
        }

        if (text.length() > 25) {
            baseSize = 10;
        }

        if (text.length() > 35) {
            baseSize = 5;
        }

        int size = getScaleFontSize(baseSize);

        wordLabel.setFont(new Font("Meiryo", Font.BOLD, size));
        wordLabel.setText(text);
    }

    public void showWord(TypingWord word) {
    	setWordText(word.word());
        readingLabel.setText(word.hiragana());
    }

    public void showRomaji(String romaji) {
        normalizedArea1.setText("ローマ字 : " + romaji);
    }

    public void updateRemainingQuestions(int remain) {
        quizRewardLabel.setText("残り問題数 : " + remain);
    }

    public void updateQuestionTime(int remain) {
        questionTimerLabel.setText("問題残り時間 : " + remain + "秒");
    }

    public void updateGameTime(int remain) {
        gameTimerLabel.setText("ゲーム残り時間 : " + remain + "秒");
    }

    public void updateScore(int score) {
        scoreLabel.setText("現在のスコア : " + score);
    }
    
    public void showCorrect(String text) {
        resultLabel.setText("〇 正解 : " + text);
    }

    public void showNear(String text) {
        resultLabel.setText("△ 惜しい : " + text);
    }

    public void showMiss(String text) {
        resultLabel.setText("✕ 不正解 : " + text);
    }

    public void showTimeout(String text) {
        resultLabel.setText("⌛ 時間切れ : " + text);
    }
    
    public void showReward(String text) {
        RewardResultLabel.setText("入力した文字：" + text);
    }  
    
    public void updateTypingDisplay(PartialResult result) {
        renderer.render(typingLabel, result);
    }
    
    public SushiPanel getSushiPanel() {
        return sushiPanel;
    }
    
    public JTextPane getTypingLabel() {
        return typingLabel;
    }
    
    public void changeSushi(){
        sushiPanel.changeSushi(difficulty);
    }
    
}