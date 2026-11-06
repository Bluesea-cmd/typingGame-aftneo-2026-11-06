package typingapp.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import typingapp.controller.LoginController;

public class SignUpScreenUI {
	
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;
    private JCheckBox showPasswordCheckBox; // パスワード表示用チェックボックス

    private String userName;				// ユーザー名の宣言

    private Consumer<String> loginCallback;
    
	// 新規登録画面
    private LoginController controller;
    
    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        	messageLabel.setText("ユーザー名とパスワードを入力してください");
        	return;
        }
        
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("パスワードが一致しません");
            return;
        }
        
        boolean success = controller.register(username, password);
        
        if (success) {
            messageLabel.setText("ユーザー登録成功！");
            userName = username;
            frame.dispose();

            if (loginCallback != null) {
                loginCallback.accept(userName);
            }

        } else {
            messageLabel.setText("そのユーザー名はすでに存在します");
        }
    }
    
    public SignUpScreenUI(LoginController controller) {
    	this.controller = controller;
        frame = new JFrame("新規登録");
        frame.setSize(450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);  // ★画面中央に表示
        frame.setLayout(new BorderLayout());

        // フォント設定
        Font labelFont = new Font("SansSerif", Font.BOLD, 16);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        // メインパネル
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;

        // ユーザー名
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel userLabel = new JLabel("ユーザー名:");
        userLabel.setFont(labelFont);
        panel.add(userLabel, gbc);
        //ユーザー名入力欄
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // パスワード
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("パスワード:");
        passLabel.setFont(labelFont);
        panel.add(passLabel, gbc);
        //パスワード入力欄
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // パスワード確認
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel RepassLabel = new JLabel("パスワードの再確認:");
        RepassLabel.setFont(labelFont);
        panel.add(RepassLabel, gbc);
        //パスワード確認欄
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(15);
        panel.add(confirmPasswordField, gbc);
        
        // パスワード表示チェックボックス
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        
        showPasswordCheckBox = new JCheckBox("パスワードを表示:");
        showPasswordCheckBox.setFont(labelFont);
        showPasswordCheckBox.addActionListener(_ -> {
            char echoChar = showPasswordCheckBox.isSelected() ? (char) 0 : '●';
            passwordField.setEchoChar(echoChar);
            confirmPasswordField.setEchoChar(echoChar);
        });
        
        panel.add(showPasswordCheckBox, gbc);

        // メッセージラベル（上部）
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(labelFont);

        // ボタンパネル（下部：戻るは左、新規登録は右）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5)); // 中央寄せ・ボタン間隔あり

        JButton backButton = new JButton("戻る");
        backButton.setFont(buttonFont);
        backButton.addActionListener(_ ->
        	frame.dispose()
        );
        buttonPanel.add(backButton);  // 中央寄せの1つ目

        JButton signUpButton = new JButton("新規登録をする");
        signUpButton.setFont(buttonFont);
        buttonPanel.add(signUpButton);  // 中央寄せの2つ目

        signUpButton.addActionListener(_ ->
        	registerUser()
        );

        // フレームに追加
        frame.add(messageLabel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    public void setLoginCallback(Consumer<String> callback) {
        this.loginCallback = callback;
    }
    
}
