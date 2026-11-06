package typingapp.ui;

import javax.swing.*;

import typingapp.util.WindowSettings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Optional;

public class StartGameUI {
	private JButton easyButton;
	private JButton normalButton;
	private JButton hardButton;
	
	private enum ScreenState {
	    SELECT,   // 難易度選択中
	}

	private ScreenState screenState = ScreenState.SELECT;
	
	class BackgroundPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private Image image;
		
	    public BackgroundPanel(String path){
	        image = new ImageIcon(getClass().getResource(path)).getImage();
	    }

	    @Override
	    protected void paintComponent(Graphics g){
	        super.paintComponent(g);

	        int panelWidth = getWidth();
	        int panelHeight = getHeight();

	        int imageWidth = image.getWidth(this);
	        int imageHeight = image.getHeight(this);

	        double scale = Math.max(
	            (double)panelWidth / imageWidth,
	            (double)panelHeight / imageHeight
	        );

	        int drawWidth = (int)(imageWidth * scale);
	        int drawHeight = (int)(imageHeight * scale);

	        int x = (panelWidth - drawWidth) / 2;
	        int y = (panelHeight - drawHeight) / 2;

	        System.out.println("StartGameUI");
            System.out.println("panel=" + panelWidth + "x" + panelHeight + " image=" + imageWidth + "x" + imageHeight);

	        g.drawImage( image, x, y, drawWidth, drawHeight, this);
	    }
	}
	
    // 難易度を選択するメソッド    
	public Optional<String> chooseDifficulty() {

	    final String[] selected = {null};
	    
	    screenState = ScreenState.SELECT;
	    
		JDialog frame = new JDialog((Frame) null, "難易度選択", true);
		frame.setSize(
		        WindowSettings.getWidth(),
		        WindowSettings.getHeight()
		);
	    frame.setLocationRelativeTo(null);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    	final Image[] backgroundImage = {
    			new ImageIcon( getClass().getResource("/images/アフターネオ寿司タイピング_難易度選択画面.png")).getImage()
    	};
	    	
    	JPanel background = new JPanel() {	    
	    	@Override
	        protected void paintComponent(Graphics g) {
	
	            super.paintComponent(g);
	            
//	            System.out.println("難易度選択背景描画");
	            
	            g.drawImage(backgroundImage[0], 0, 0, getWidth(), getHeight(), this);
	        }
    	};

    	Runnable resetBackground = () -> {
    	    backgroundImage[0] = new ImageIcon( getClass().getResource( "/images/アフターネオ寿司タイピング_難易度選択画面.png")).getImage();
    	    background.repaint();

    	};


    	java.util.function.Consumer<String> changeBackground = path -> {
	        backgroundImage[0] = new ImageIcon( getClass().getResource(path)).getImage();
	        background.repaint();
    	};
	    
	    GridBagConstraints gbc = new GridBagConstraints();

	    // 難易度選択文字
	    JLabel title = new JLabel();

	    title.setFont( new Font("MS Gothic", Font.BOLD, 32));
	    title.setForeground(Color.WHITE);

	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.gridwidth = 3;
	    gbc.insets = new Insets(20,0,80,0);

	    background.add( title, gbc);

	    easyButton = createDifficultyButton();
	    normalButton = createDifficultyButton();
	    hardButton = createDifficultyButton();
		    
    	background.setLayout(null);

    	background.add(easyButton);
    	background.add(normalButton);
    	background.add(hardButton);
		    
	    easyButton.addActionListener(_ -> {
	        selected[0] = "初級";	        
//	        screenState = ScreenState.START;
//        
//            backgroundImage[0] = new ImageIcon(getClass().getResource("/images/アフターネオ寿司タイピング開始タイトル画面.png")).getImage();
//            System.out.println("初級");
//            
//	        easyButton.setVisible(false);
//	        normalButton.setVisible(false);
//	        hardButton.setVisible(false);
//
//	        background.repaint();
	        frame.dispose();
	    });

	    normalButton.addActionListener(_ -> {
	        selected[0] = "中級";
//	        screenState = ScreenState.START;
//
//            backgroundImage[0] = new ImageIcon(getClass().getResource("/images/アフターネオ寿司タイピング開始タイトル画面.png")).getImage();
//            System.out.println("中級");
//            
//	        easyButton.setVisible(false);
//	        normalButton.setVisible(false);
//	        hardButton.setVisible(false);
//
//	        background.repaint();
	        frame.dispose();
	    });

	    hardButton.addActionListener(_ -> {
	        selected[0] = "上級";
//	        screenState = ScreenState.START;
//
//            backgroundImage[0] = new ImageIcon(getClass().getResource("/images/アフターネオ寿司タイピング開始タイトル画面.png")).getImage();
//            System.out.println("上級");
//            
//	        easyButton.setVisible(false);
//	        normalButton.setVisible(false);
//	        hardButton.setVisible(false);
//
//	        background.repaint();
	        frame.dispose();
	    });

	    easyButton.addMouseListener( new MouseAdapter() {
	    	
	        @Override
	        public void mouseEntered(MouseEvent e) {
	            changeBackground.accept("/images/アフターネオ寿司タイピング_難易①初級選択画面.png");
//	            System.out.println("初級");
	        }
	        
	        @Override
	        public void mouseExited(MouseEvent e) {
	            if (screenState == ScreenState.SELECT) {
	                resetBackground.run();
	            }
	        }
	   });

	    normalButton.addMouseListener( new MouseAdapter() {
	    	
	        @Override
	        public void mouseEntered(MouseEvent e) {
	            changeBackground.accept("/images/アフターネオ寿司タイピング_難易②中級選択画面.png");
//	            System.out.println("中級");
	        }

	        @Override
	        public void mouseExited(MouseEvent e) {
	            if (screenState == ScreenState.SELECT) {
	                resetBackground.run();
	            }
	        }
	    });

	    hardButton.addMouseListener( new MouseAdapter() {
	    	
	        @Override
	        public void mouseEntered(MouseEvent e) {
	            changeBackground.accept("/images/アフターネオ寿司タイピング_難易③上級選択画面.png");
//	            System.out.println("上級");
	        }
	        
	        @Override
	        public void mouseExited(MouseEvent e) {
	            if (screenState == ScreenState.SELECT) {
	                resetBackground.run();
	            }
	        }
	    });

	    gbc.gridy=1;
	    gbc.gridwidth=3;
	    gbc.insets = new Insets(0,0,0,0);

	    background.addComponentListener(
	    	    new ComponentAdapter(){

	    	        @Override
	    	        public void componentResized(ComponentEvent e){
	    	            updateDifficultyButtonPosition(background);
	    	        }
	    	    }
	    	);
	    
	    frame.add(background);
//	    JRootPane rootPane = frame.getRootPane();
//
//	    rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
//	            .put(KeyStroke.getKeyStroke("SPACE"),"startGame");
//
//	    rootPane.getActionMap()
//				.put("startGame",new AbstractAction() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//        System.out.println("スペースキーは難易度選択画面では使用できません");
////                    	  if (screenState != ScreenState.START) {
////                    	      return;
////                    	  }
//	    
////                        synchronized (lock) {
////                      	  lock.notify();
////                        }
////                      
////	    				  frame.dispose();
//					  }
//	    });
	    frame.setVisible(true);

	    updateDifficultyButtonPosition(background);

	    return Optional.ofNullable(selected[0]);
	}
	
//    public Optional<String> chooseDifficulty() {
//    	
//    	//難易度の実装
//        String[] difficulty = {						//2025/12地点
//                "初級：かんたん。タイピングはじめたての人向け",		//330個
//                "中級：普通。タイピングに慣れてきた人向け",		//274個
//                "上級：難しい。タイピングを究めしもの向け"		//96個
//        };
//        
//        // 難易度選択ダイアログ
//        String choice = (String) JOptionPane.showInputDialog(
//                null,						// 中央配置
//                "難易度を選択してください",			// メッセージ
//                "難易度選択",					// タイトル
//                JOptionPane.PLAIN_MESSAGE,	// メッセージのタイプ(アイコンなし)
//                null,						// アイコンの指定
//                difficulty,					// 選択肢の配列
//                difficulty[0]				// デフォルトで選ばれる配列を指定
//        );
//
//        // 何らかの原因で空を返したときの処理
//        if (choice == null) {
//            return Optional.empty();
//        }
//        
//    	// 選んだ物を保管する変数
//        String selected = choice.split("：")[0];
//
//        return Optional.of(selected);
//    }    

	//追加
	private JButton createDifficultyButton() {

	    JButton button = new JButton();

	    // 完全透明ボタン
	    button.setOpaque(false);
	    button.setContentAreaFilled(false);
	    button.setBorderPainted(false);
	    button.setFocusPainted(false);

	    button.setFocusable(false);
	    
	    return button;
	}
	
	private void updateDifficultyButtonPosition( JPanel panel) {

	    double baseWidth = 1920.0;
	    double baseHeight = 1080.0;

	    double scaleX = panel.getWidth() / baseWidth;
	    double scaleY = panel.getHeight() / baseHeight;
	    
	    // 初級
	    easyButton.setBounds(
	            (int)(490 * scaleX),
	            (int)(347 * scaleY),
	            (int)(473 * scaleX),
	            (int)(607 * scaleY)
	    );


	    // 中級
	    normalButton.setBounds(
	            (int)(967 * scaleX),
	            (int)(347 * scaleY),
	            (int)(448 * scaleX),
	            (int)(607 * scaleY)
	    );


	    // 上級
	    hardButton.setBounds(
	            (int)(1423 * scaleX),
	            (int)(347 * scaleY),
	            (int)(478 * scaleX),
	            (int)(607 * scaleY)
	    );
	}
	
    // スペースキー入力を待機してゲームを開始するメソッド
    public void waitForSpaceToStart(Runnable callback) {
        // ウィンドウを作成
        JFrame frame = new JFrame("ゲーム開始");					// タイトル
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// バツボタンを押したときの動作設定
        
        final Image[] backgroundImage = {
    			new ImageIcon(getClass().getResource("/images/アフターネオ寿司タイピング開始タイトル画面.png")).getImage()
        };
        
    	JPanel background = new JPanel() {	    
	    	@Override
	        protected void paintComponent(Graphics g) {
	
	            super.paintComponent(g);
	
	            System.out.println("ゲーム開始背景描画");
	            
	            g.drawImage(backgroundImage[0], 0, 0, getWidth(), getHeight(), this);
	        }
    	};

    	Runnable resetBackground = () -> {
    	    backgroundImage[0] = new ImageIcon( getClass().getResource( "/images/アフターネオ寿司タイピング_難易度選択画面.png")).getImage();
    	    background.repaint();

    	};
    	
    	frame.setSize(
    	        WindowSettings.getWidth(),
    	        WindowSettings.getHeight()
    	);
        frame.setLocationRelativeTo(null);         				// 画面中央に表示
        frame.setContentPane(background);
        frame.setLayout(new BorderLayout());					// BorderLayout式(東西南北中央)に配置するように設定する
        
        // キーボードの入力を検知するキーボードリスナーを追加
        JRootPane rootPane = frame.getRootPane(); 					// キー入力をするため、rootPaneを呼び出しておく
        
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)		// ウィンドウを表示している間にキー入力を受け付けるようにする
                .put(KeyStroke.getKeyStroke("SPACE"), "startGame");	// 今回はスペースキーを対象にし、ゲームをスタートする
        
        rootPane.getActionMap()										// キー入力を受け付けたら(今回はスペースキー)
                .put("startGame", new AbstractAction() {			// ゲームスタートすると同時にAbstractActionを宣言する(なんらかの操作を行うと実行されるメソッド)
                    @Override										// Override宣言(これがないとactionPerformedが使えない)
                    public void actionPerformed(ActionEvent e) {	// ボタンを押すと動作する設計のため、このメソッドが必須
                    	resetBackground.run();
                        frame.dispose(); 							// ウィンドウを閉じる
                        System.out.println("ゲーム開始!");
                        callback.run();
                    }
                });
        
        frame.setVisible(true); //　レイアウトが完成したので表示する(先に宣言すると内容がないため表示も変になる)
    }							// 先に入れると、キー入力を受け付けず、ラベルが設定されてない状態で表示されるので、混乱を防ぐために最後に行う事。
 
}