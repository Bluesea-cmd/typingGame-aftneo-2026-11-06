package typingapp.ui;

import typingapp.controller.LoginController;
import typingapp.service.LoginService;
import typingapp.util.AudioManager;
import typingapp.util.WindowSettings;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import javax.swing.*;

public class LoginScreenUI {
    private JFrame frame;					// フレームを使用するので宣言
    private JTextField usernameField;		// ユーザー名を格納するフィールドの宣言
    private JPasswordField passwordField;	// パスワードを格納するフィールドの宣言
    private JButton loginButton;			// ログインボタン
    private JButton signupButton;			// 新規登録ボタン
    private JCheckBox showPasswordCheckBox;	// パスワードをデフォルトで隠して表示するので、それを解除するためのチェックボタンを追加するために宣言
 
    private JLabel messageLabel;			// メッセージを表示するためのラベル宣言
    
    private String userName;				// ユーザー名の宣言
    private Consumer<String> loginCallback;

    private static final int BASE_FIELD_FONT_SIZE = 55;
    
    private AudioManager audioManager;
    
    private LoginController loginController;
	
    public LoginScreenUI(LoginController loginController, AudioManager audioManager) {
        this.loginController = loginController;
        this.audioManager = audioManager;
        createUI();
    }
    
    public void setLoginCallback(Consumer<String> callback) {
        this.loginCallback = callback;
    }
    
    class BackgroundPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        
		private Image image;

        public BackgroundPanel(String path) {
            image = new ImageIcon( getClass().getClassLoader().getResource(path)).getImage();
        }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);

	        int panelWidth = getWidth();
	        int panelHeight = getHeight();

//	        int imageWidth = image.getWidth(this);
//	        int imageHeight = image.getHeight(this);
//
//	        double scale = Math.max(
//	            (double)panelWidth / imageWidth,
//	            (double)panelHeight / imageHeight
//	        );
//
//	        int drawWidth = (int)(imageWidth * scale);
//	        int drawHeight = (int)(imageHeight * scale);
//
//	        int x = (panelWidth - drawWidth) / 2;
//	        int y = (panelHeight - drawHeight) / 2;
//
//	        System.out.println("StartGameUI");
//            System.out.println("panel=" + panelWidth + "x" + panelHeight + " image=" + imageWidth + "x" + imageHeight);

//	        g.drawImage( image, x, y, drawWidth, drawHeight, this);
	        g.drawImage( image, 0, 0, panelWidth, panelHeight, this);
	    }
    }
    
    private static class ScaledCheckBoxIcon implements Icon {

        private final int size;
        private final boolean selected;

        public ScaledCheckBoxIcon(int size, boolean selected) {
            this.size = size;
            this.selected = selected;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
        	if (selected) {
                return;
            }
        	
        	Graphics2D g2 = (Graphics2D) g.create();
            
            try {
		        g2.setRenderingHint(
		                RenderingHints.KEY_ANTIALIASING,
		                RenderingHints.VALUE_ANTIALIAS_ON
		        );
		        
		        int iconY = y + (c.getHeight() - size) /2;
		        int drawSize = size;
		        
		     // ★ 角丸の大きさを指定（サイズに応じた動的な値にするのがおすすめ）
		        int arc = (int)(drawSize * 0.3); // サイズの30%程度の丸み（固定値で 8 や 10 などでも可）
		        
		        // チェックボックスの背景
		        g2.setColor(Color.BLACK);
		        g2.fillRoundRect(x - 2, iconY - 4, drawSize - 6, drawSize - 5, arc, arc);
            } finally {
            	g2.dispose();
            }
        }
    }
    
    //背景画像基準で部品配置
    private void updateComponentPosition(BackgroundPanel panel) {

        double baseWidth = 1920.0;
        double baseHeight = 1080.0;

        double scaleX = panel.getWidth() / baseWidth;
        double scaleY = panel.getHeight() / baseHeight;;

        // フォントは高さ方向の倍率を基準にする
        double fontScale = Math.min(scaleX, scaleY);
        
        // 入力欄フォントサイズ
        int fieldFontSize = Math.max(14,(int)(BASE_FIELD_FONT_SIZE * fontScale));
        Font scaledFieldFont = new Font("SansSerif",Font.PLAIN,fieldFontSize);
        
        // ユーザー名
        usernameField.setBounds(
                (int)(1298 * scaleX),
                (int)(250 * scaleY),
                (int)(590 * scaleX),
                (int)(110 * scaleY)
        );

        usernameField.setFont(scaledFieldFont);
        
        // パスワード
        passwordField.setBounds(
                (int)(1303 * scaleX),
                (int)(382 * scaleY),
                (int)(587 * scaleX),
                (int)(117 * scaleY)
        );

        passwordField.setFont(scaledFieldFont);
        
        int checkBoxSize = Math.max(20,(int)(50 * fontScale));
        
        showPasswordCheckBox.setFont(scaledFieldFont);
        
        // チェックボックス
        showPasswordCheckBox.setBounds(
                (int)(1300 * scaleX),
                (int)(565 * scaleY),
                (int)(280 * scaleX),
                (int)(75 * scaleY)
        );
        
        showPasswordCheckBox.setIcon( new ScaledCheckBoxIcon(checkBoxSize, false));

        showPasswordCheckBox.setSelectedIcon( new ScaledCheckBoxIcon(checkBoxSize, true));
        
        int textOffset = (int)(-12 * scaleY); // 画面サイズ変更に合わせて追従させる調整値
        showPasswordCheckBox.setBorder(BorderFactory.createEmptyBorder(textOffset, 0, 0, 0));
        
        // ログインボタン
        loginButton.setBounds(
                (int)(1300 * scaleX),
                (int)(722 * scaleY),
                (int)(470 * scaleX),
                (int)(145 * scaleY)
        );
        
        // 新規登録ボタン
        signupButton.setBounds(
                (int)(1300 * scaleX),
                (int)(880 * scaleY),
                (int)(470 * scaleX),
                (int)(145 * scaleY)
        );
    }
    
    private void createUI() {
        frame = new JFrame("ログイン画面");
        frame.setSize(
                WindowSettings.getWidth(),
                WindowSettings.getHeight()
        );
        frame.setLocationRelativeTo(null);  // ★画面中央に表示
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // フォント設定
        Font fieldFont = new Font("SansSerif", Font.PLAIN, BASE_FIELD_FONT_SIZE);

        // メインパネル
        BackgroundPanel panel = new BackgroundPanel("images/アフターネオ武蔵小杉寿司タイピングログイン画面.png");

        panel.setLayout(null);

        audioManager.stopBgm();
        audioManager.playBgm("/sounds/BGM/ログイン場面.mp3");
//        GridBagConstraints gbc = new GridBagConstraints();

//        gbc.insets = new Insets(12, 10, 0, 10);
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.anchor = GridBagConstraints.EAST;
//        gbc.weightx = 1.0;

//		  ユーザー名
//        JLabel userLabel = new JLabel("ユーザー名:");
//        userLabel.setFont(labelFont);
//        panel.add(userLabel, gbc);

//      gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.anchor = GridBagConstraints.EAST;
        usernameField = new JTextField();
        usernameField.setFont(fieldFont);
        usernameField.setBorder(null);
        usernameField.setOpaque(false);
        usernameField.setText("kaiseyokote");
        
        panel.add(usernameField);

        // パスワード
//		JLabel passLabel = new JLabel("パスワード:");
//		passLabel.setFont(labelFont);
//      panel.add(passLabel, gbc);

//      gbc.gridx = 1;
//      gbc.gridy = 1;
        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBorder(null);
        passwordField.setOpaque(false);
        passwordField.setText("musako63369");
        
        panel.add(passwordField);

        // パスワード表示チェックボックス
        char defaultEcho = passwordField.getEchoChar();
        
        showPasswordCheckBox = new JCheckBox("しない:");
        showPasswordCheckBox.setFont(fieldFont);
        showPasswordCheckBox.setForeground(Color.BLACK); // 文字色
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setContentAreaFilled(false);
        showPasswordCheckBox.setMargin(new Insets(0, 0, 0, 0));
        
        showPasswordCheckBox.setBorderPainted(false);
        //showPasswordCheckBox.setBorder(null);
        showPasswordCheckBox.setFocusPainted(false);
        
        showPasswordCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
        showPasswordCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        showPasswordCheckBox.setVerticalAlignment(SwingConstants.CENTER);
        
        showPasswordCheckBox.addActionListener(_ -> {

            if(showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char)0);
                showPasswordCheckBox.setText("　する:"); // 選択時のテキスト
            } else {
                passwordField.setEchoChar(defaultEcho);
                showPasswordCheckBox.setText("しない:"); // 未選択時のテキスト
            }
            showPasswordCheckBox.revalidate();
            showPasswordCheckBox.repaint();
        });
        
        panel.add(showPasswordCheckBox);
        
        // ボタンパネル（縦配置）
//        buttonPanel = new JPanel();
////        GridBagConstraints gbcButton = new GridBagConstraints();
////        gbc.insets = new Insets(5, 10, 5, 10);
////        gbc.fill = GridBagConstraints.NONE;
////        gbc.anchor = GridBagConstraints.EAST;
////        gbc.weightx = 1.0;
//        buttonPanel.setLayout(new GridLayout(2,1,0,20));
//        buttonPanel.setOpaque(false);

        // ログインボタン
        loginButton = new JButton();
        loginButton.setFont(fieldFont);
        loginButton.setOpaque(false);
        loginButton.setContentAreaFilled(false);

        // 枠を表示
//        loginButton.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        // 枠を透明
        loginButton.setBorderPainted(true);

        // フォーカス枠を消す
        loginButton.setFocusPainted(false);

        // 文字色
        loginButton.setForeground(null);
        panel.add(loginButton);
//        gbcButton.gridx = 0;
//        gbcButton.gridy = 0;
//        buttonPanel.add(loginButton);

//		空白ラベルを追加して間隔を空ける
//		gbcButton.gridy = 1;
//		buttonPanel.add(Box.createVerticalStrut(20), gbcButton);

        // 新規登録ボタン
        signupButton = new JButton();
        signupButton.setOpaque(false);
        signupButton.setContentAreaFilled(false);
        
        signupButton.setBorderPainted(true);
//        signupButton.setBorder(BorderFactory.createLineBorder(Color.BLUE,3));
        
        signupButton.setFocusPainted(false);

        signupButton.setForeground(null);
        panel.add(signupButton);
//        gbcButton.gridy = 2;
//        buttonPanel.add(loginButton);
//        buttonPanel.add(signUpButton);
//        gbc.gridx = 1;
//        gbc.gridy = 3;
//        gbc.weightx = 1.0;
//        gbc.anchor = GridBagConstraints.EAST;
//        panel.add(buttonPanel);

        // メッセージラベル（エラーメッセージ表示用）
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(fieldFont);

        // ログインボタンのアクションリスナー(ログイン時の処理)


        loginButton.addActionListener(_ -> {

            LoginService.LoginResult result =
            		loginController.login(
                        usernameField.getText(),
                        new String(passwordField.getPassword())
                    );

            switch (result) {
	            case SUCCESS -> {
	                messageLabel.setText("ログイン成功！");
	                userName = usernameField.getText();
	
	                // ★ 1.5秒（1500ミリ秒）待ってから画面遷移するタイマー
	                Timer timer = new Timer(1500, _ -> {
	                    frame.dispose(); // ログイン画面を閉じる
	                    
	                    if (loginCallback != null) {
	                        loginCallback.accept(userName); // 難易度選択画面への遷移処理などを実行
	                    }
	                });
	                timer.setRepeats(false); // 1回だけ実行する設定
	                timer.start();          // タイマースタート
	                audioManager.stopBgm();
	            }
                case USER_NOT_FOUND ->
                    messageLabel.setText("ユーザー名が存在しません");

                case WRONG_PASSWORD ->
                	// セキュリティ配慮のためパスワードは返さない
                    messageLabel.setText("パスワードが間違っています");
                    
                case INVALID_INPUT ->
                    messageLabel.setText("入力が不正です");

                case SYSTEM_ERROR -> 
                    JOptionPane.showMessageDialog(
                            frame,
                            "システムエラーが発生しました。",
                            "エラー",
                            JOptionPane.ERROR_MESSAGE);
            }
            
        });

        // 新規登録ボタンのアクションリスナー
        signupButton.addActionListener(_ -> {
            SignUpScreenUI ui = new SignUpScreenUI(loginController);
            ui.setLoginCallback(loginCallback);

        });
        // 各パネルをフレームに追加
        // サイズ変更時に再配置

        panel.addComponentListener( new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateComponentPosition(panel);
                }
            }
        );
//        gbc.gridx = 1;
//        gbc.gridy = 4;
//        gbc.weightx = 1.0;
//        gbc.anchor = GridBagConstraints.EAST;
//
//        panel.add(buttonPanel, gbc);


        frame.add(messageLabel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
        
        updateComponentPosition(panel);
    }


    public String getUserName() {
        return userName;
    }
    
    public void showLogin() {
        frame.setVisible(true);
    }
}