package typingapp.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import typingapp.util.WindowSettings;

public class TitleUI {
	
    private JFrame frame;
    
    private JButton startButton;			// スタートボタン
    private JButton settingButton;			// 設定ボタン

    private Runnable startCallback;
    private Runnable settingCallback;
	
    public TitleUI() {
        createUI();
    }
	
    // スタートボタンが押されたときの処理
    public void setStartCallback(Runnable callback) {
        this.startCallback = callback;
    }
    
    //設定ボタンが押されたときの処理
    public void setSettingCallback(Runnable callback) {
        this.settingCallback = callback;
    }
    
    class BackgroundPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private Image image;

        public BackgroundPanel(String path) {
            setBackgroundImage(path);
        }

        public void setBackgroundImage(String path) {

            var url = getClass().getClassLoader().getResource(path);

            if (url == null) {
                throw new IllegalArgumentException(
                        "背景画像が見つかりません: " + path
                );
            }

            image = new ImageIcon(url).getImage();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            g.drawImage(
                    image,
                    0,
                    0,
                    panelWidth,
                    panelHeight,
                    this
            );
        }
    }
	
    //背景画像基準で部品配置
    private void updateComponentPosition(JPanel panel) {

        double baseWidth = 1920.0;
        double baseHeight = 1080.0;

        double scaleX = panel.getWidth() / baseWidth;
        double scaleY = panel.getHeight() / baseHeight;;
        
        
        // 設定ボタン
        settingButton.setBounds(
                (int)(50 * scaleX),
                (int)(78 * scaleY),
                (int)(245 * scaleX),
                (int)(205 * scaleY)
        );
        
        //スタートボタン
        startButton.setBounds(
                (int)(1470 * scaleX),
                (int)(775 * scaleY),
                (int)(410 * scaleX),
                (int)(295 * scaleY)
        );
    }
    
    private void createUI() {
        frame = new JFrame("タイトル画面");
        frame.setSize(
                WindowSettings.getWidth(),
                WindowSettings.getHeight()
        );
        frame.setLocationRelativeTo(null);  // ★画面中央に表示
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // メインパネル
        BackgroundPanel panel = new BackgroundPanel("images/アフターネオ寿司タイピングタイトル画面_default.png");

        panel.setLayout(null);

        // 開店ボタン
        startButton = new JButton();
        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);

//        // 枠を表示
//        startButton.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        // 枠を表示するか
        startButton.setBorderPainted(false);

        // フォーカス枠を消す
        startButton.setFocusPainted(true);

        // 文字色
        startButton.setForeground(null);
        
        startButton.setFocusable(false);
        panel.add(startButton);

        // 設定ボタン
        settingButton = new JButton();
        settingButton.setOpaque(false);
        settingButton.setContentAreaFilled(false);
                
//        settingButton.setBorder(BorderFactory.createLineBorder(Color.BLUE,3));
     
        settingButton.setBorderPainted(false);

        settingButton.setFocusPainted(true);

        settingButton.setForeground(null);

        settingButton.setFocusable(false);
        
        panel.add(settingButton);

     // スタートボタンにカーソルを合わせたとき
        startButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                panel.setBackgroundImage(
                        "images/寿司タイピングゲームタイトル画面_開始.png"
                );
            }

            @Override
            public void mouseExited(MouseEvent e) {

                panel.setBackgroundImage(
                        "images/アフターネオ寿司タイピングタイトル画面_default.png"
                );
            }
        });

        // 設定ボタンにカーソルを合わせたとき
        settingButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                panel.setBackgroundImage(
                        "images/寿司タイピングゲームタイトル画面_設定.png"
                );
            }

            @Override
            public void mouseExited(MouseEvent e) {

                panel.setBackgroundImage(
                        "images/アフターネオ寿司タイピングタイトル画面_default.png"
                );
            }
        });
        
        // ログインボタンのアクションリスナー(ログイン時の処理)
        startButton.addActionListener(_ -> {

            if (startCallback != null) {
                startCallback.run();
            }
            
            System.out.println("開店ボタンが押されました");
        });

        // 設定ボタンのアクションリスナー
        settingButton.addActionListener(_ -> {

            if (settingCallback != null) {
                settingCallback.run();
            }
            
            System.out.println("設定ボタンが押されました");
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

        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
        
        updateComponentPosition(panel);
    }
    
    public void showTitle() {
        frame.setVisible(true);
    }
    
    public void hideTitle() {
        frame.setVisible(false);
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
}