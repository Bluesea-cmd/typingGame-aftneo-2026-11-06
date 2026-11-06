package typingapp;

import typingapp.controller.GameController;
import typingapp.controller.LoginController;
import typingapp.dao.ScoreDAO;
import typingapp.dao.UserDAO;
import typingapp.dao.WordDAO;
import typingapp.service.GameService;
import typingapp.service.GameSessionManager;
import typingapp.service.LoginService;
import typingapp.service.RomajiNormalizationService;
import typingapp.service.RomajiVariantService;
import typingapp.service.ScoreService;
import typingapp.ui.GameFrame;
import typingapp.ui.GamePanel;
import typingapp.ui.LoginScreenUI;
import typingapp.ui.SettingUI;
import typingapp.ui.StartGameUI;
import typingapp.ui.TitleUI;
import typingapp.util.AudioManager;
import typingapp.util.WindowSettings;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.Optional;

public class Main {
    private static final int QUESTION_COUNTER = 12;		// 問題数
    
    private static void startApplication() {
    	
    	try {
		    // DAO
		    ScoreDAO scoreDAO = new ScoreDAO();
		    UserDAO userDAO = new UserDAO();
		    WordDAO wordDAO = new WordDAO();
		    
		    // Service
		    LoginService loginService = new LoginService(userDAO);
		    GameSessionManager sessionManager = new GameSessionManager();
		    RomajiNormalizationService romajiNormalizationService = new RomajiNormalizationService();
		    RomajiVariantService romajiVariantService = new RomajiVariantService(romajiNormalizationService);
		    GameService gameService = new GameService(wordDAO);
		    ScoreService scoreService = new ScoreService(scoreDAO);
		    
		    // Controller
		    LoginController loginController = new LoginController(loginService);
		    
		    System.out.println("GF: " + GamePanel.class.getResource("/images/gameForBeginner.jpg"));
		    System.out.println("GF images: " + GamePanel.class.getResource("/images/gameForMidium.jpg"));
		    System.out.println("TG: " + GamePanel.class.getResource("/images/gameForPro.jpg"));

		    // Audio
		    AudioManager audioManager = new AudioManager();
		    
		    // UI
            TitleUI titleUI = new TitleUI();

            // スタートボタンが押されたとき
            titleUI.setStartCallback(() -> {

                // タイトル画面を閉じる
                titleUI.hideTitle();

                // ==============================
                // ログイン画面
                // ==============================
                LoginScreenUI loginUI = new LoginScreenUI(loginController, audioManager);

                // ログイン成功時
                loginUI.setLoginCallback(username -> {

                    // ログインBGM → 難易度選択BGM
                    audioManager.playBgm("/sounds/BGM/レベル選択画面BGM.mp3");
                    
                    // ==============================
                    // 難易度選択
                    // ==============================
                    StartGameUI startUI = new StartGameUI();

                    Optional<String> difficulty = startUI.chooseDifficulty();

                    if (difficulty.isEmpty()) {
                        System.out.println("難易度選択がキャンセルされました");

                        // 必要ならタイトル画面へ戻す
                        titleUI.showTitle();
                        return;
                    }

                    String selectedDifficulty = difficulty.get();
                
	                // スペースキー押下でゲーム開始
	                startUI.waitForSpaceToStart(() -> {
	                    
	                	// ゲーム初期化を別スレッドで実行（UI ブロッキングを防ぐ）
	                    new Thread(() -> {
	                        System.out.println("ゲーム開始: ユーザー=" + username + ", 難易度=" + selectedDifficulty);
	                        
	                        // 1. メインウィンドウ(JFrame)の生成・設定
		                    JFrame windowFrame = new JFrame("タイピングゲーム");
		                    windowFrame.setSize(
		                            WindowSettings.getWidth(),
		                            WindowSettings.getHeight()
		                    );
		                    windowFrame.setLocationRelativeTo(null);
		                    windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		                    // 2. 背景を描画する GameFrame(JPanel) の生成と難易度背景の設定
		                    GameFrame gameFrame = new GameFrame();
		                    gameFrame.setDifficultyBackground(selectedDifficulty);
		
		                    // 3. UIパーツを持つ GamePanel の生成（背景を透過させるため Opaque=false に設定）
		                    GamePanel panel = new GamePanel(QUESTION_COUNTER, selectedDifficulty);
		                    panel.setOpaque(false); // ★背景画像が見えるように透過設定
		
		                    // 4. 背景パネル(gameFrame)の上にUIパネル(panel)を置く
		                    gameFrame.add(panel);
		
		                    // 5. ウィンドウに背景パネルをセット
		                    windowFrame.setContentPane(gameFrame);
		                    
		                    // UI スレッドでゲーム画面を表示
		                    SwingUtilities.invokeLater(() -> {
		                        windowFrame.setVisible(true);
		                        
			                 // 6. Controller の初期化とゲーム開始
			                    GameController gameController = new GameController(
			                        windowFrame,
			                        gameService,
			                        panel,
			                        scoreService,
			                        QUESTION_COUNTER,
			                        romajiVariantService,
			                        sessionManager,
			                        romajiNormalizationService,
			                        audioManager);
				
						            gameController.startGame(
						                    username,
						                    selectedDifficulty,
						                    QUESTION_COUNTER);
			                });
					    }, "GameInitThread").start();
		            });
		         });
            });
            
            // 設定ボタンが押されたとき
            titleUI.setSettingCallback(() -> {
                SettingUI settingUI = new SettingUI(titleUI.getFrame());
                settingUI.show();
            });
    	} catch (Exception e) {
            System.err.println("アプリケーション初期化エラー: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
            
    	}
}

	public static void main(String[] args) {
        System.out.println("=== タイピングゲーム起動 ===");
        System.out.println("JVM バージョン: " + System.getProperty("java.version"));

	    SwingUtilities.invokeLater(Main::startApplication);
	}
}