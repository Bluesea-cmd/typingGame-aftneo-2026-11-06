package typingapp.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import typingapp.model.GameResult;
import typingapp.model.WordResult;
import typingapp.util.WindowSettings;

public class ScoreResultUI {

    private static final int BASE_WIDTH = 1920;
    private static final int BASE_HEIGHT = 1080;

    private static final String FONT_NAME = "MS Gothic";


    /**
     * 背景画像を表示するパネル
     */
    class BackgroundPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private final Image image;

        public BackgroundPanel(String path) {

            image = new ImageIcon(getClass().getResource(path)).getImage();
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0 ,0, getWidth(), getHeight(), this);
        }
    }


    /**
     * 結果画面を表示
     */
    public void showScoreResultScreen(
            GameResult result,
            Runnable retryAction) {

        JDialog frame = new JDialog((Frame) null, "タイピング結果", true);

        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        frame.setSize(
                WindowSettings.getWidth(),
                WindowSettings.getHeight()
        );

        frame.setLocationRelativeTo(null);


        // ========================================
        // 背景
        // ========================================

        BackgroundPanel panel = new BackgroundPanel("/images/タイピング結果画面.png");

        frame.setContentPane(panel);


        // ========================================
        // スコア表示用
        // ========================================

        ResultValueLabel correctCharCountLabel = createResultLabel();

        ResultValueLabel averageTypingSpeedLabel = createResultLabel();

        ResultValueLabel wrongCharCountLabel = createResultLabel();

        ResultValueLabel finalScoreLabel = createResultLabel();
        
        correctCharCountLabel.setText( String.valueOf(result.correctCharCount()));

        averageTypingSpeedLabel.setText( String.format( "%.1f", result.averageTypingSpeed()));

        wrongCharCountLabel.setText( String.valueOf(result.wrongCharCount()));

        finalScoreLabel.setText( String.valueOf(result.finalScore()));
        
        panel.add(correctCharCountLabel);
        panel.add(averageTypingSpeedLabel);
        panel.add(wrongCharCountLabel);
        panel.add(finalScoreLabel);

        
     // ========================================
        // 入力した文字（Input）のリスト表示用
        // ========================================

        DefaultListModel<String> listModel = new DefaultListModel<>();

        if (result.wordResults() != null) {
            for (WordResult wr : result.wordResults()) {
                // 入力された文字（Input）のみを追加
                listModel.addElement(wr.input());
            }
        }

        JList<String> inputList = new JList<>(listModel);
        inputList.setFont(new Font(FONT_NAME, Font.PLAIN, 28));
        inputList.setForeground(Color.BLACK);
        inputList.setOpaque(false);

        // リストをスクロールペインで包む（背景透過設定）
        JScrollPane scrollPane = new JScrollPane(inputList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null); // 枠線を消す場合

        panel.add(scrollPane); // ★ 画面に追加
        // ========================================
        // もう一度挑戦ボタン
        // ========================================

        JButton retryButton = createImageButton();

        retryButton.addActionListener(_ -> {
            frame.dispose();
            retryAction.run();
        });

        panel.add(retryButton);


        // ========================================
        // 終了ボタン
        // ========================================

        JButton exitButton = createImageButton();

        exitButton.addActionListener(_ -> {
            int choice = JOptionPane.showConfirmDialog(
                            frame,
                            "本当に終了しますか？",
                            "終了確認",
                            JOptionPane.YES_NO_OPTION
                    );

            if (choice == JOptionPane.YES_OPTION) {

                System.exit(0);
            }
        });

        panel.add(exitButton);


        // ========================================
        // 画面サイズ変更時に位置を再計算
        // ========================================

        panel.addComponentListener( new ComponentAdapter() {
                    @Override
                    public void componentResized(
                            ComponentEvent e) {

                        updateLayout(
                                panel,
                                correctCharCountLabel,
                                averageTypingSpeedLabel,
                                wrongCharCountLabel,
                                finalScoreLabel,
                                retryButton,
                                exitButton
                        );
                    }
                }
        );

        frame.setVisible(true);
    }


    /**
     * 結果の数値表示ラベル
     */
    private ResultValueLabel createResultLabel() {

        ResultValueLabel label = new ResultValueLabel();

        label.setForeground(Color.BLACK);
        label.setFont( new Font( FONT_NAME, Font.BOLD, 38));

//        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        return label;
    }

    /**
     * 画像上のボタン位置に合わせる透明ボタン
     */
    private JButton createImageButton() {

        JButton button = new JButton();

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setContentAreaFilled(false);

        button.setBorderPainted(false);
//        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        button.setOpaque(false);

        return button;
    }

    /**
     * 背景画像に合わせてコンポーネントを配置
     */
    private void updateLayout(
            JPanel panel,
            ResultValueLabel totalKeyCountLabel,
            ResultValueLabel averageKeyCountLabel,
            ResultValueLabel missCountLabel,
            ResultValueLabel totalScoreLabel,
            JButton retryButton,
            JButton exitButton) {

        double scaleX = (double) panel.getWidth() / BASE_WIDTH;

        double scaleY = (double) panel.getHeight() / BASE_HEIGHT;


        // ========================================
        // 左側の結果数値
        // ========================================

        setScaledBounds(
                totalKeyCountLabel,
                780, 255, 300, 70,
                68.4f,
                scaleX, scaleY
        );

        setScaledBounds(
                averageKeyCountLabel,
                780, 425, 300, 70,
                68.4f,
                scaleX, scaleY
        );

        setScaledBounds(
                missCountLabel,
                780, 580, 300, 70,
                68.4f,
                scaleX, scaleY
        );

        setScaledBounds(
                totalScoreLabel,
                780, 730, 300, 70,
                68.4f,
                scaleX, scaleY
        );


        // 下のボタン

        setScaledBounds(
                retryButton,
                162, 928, 520, 132,
                0,
                scaleX, scaleY
        );

        setScaledBounds(
                exitButton,
                752, 930, 340, 127,
                0,
                scaleX, scaleY
        );
    }


    /**
     * 画像サイズに応じて位置を拡大縮小
     */
    private void setScaledBounds(
            java.awt.Component component,
            int x,
            int y,
            int width,
            int height,
            float baseFontSize,
            double scaleX,
            double scaleY) {

        component.setBounds(
                (int) (x * scaleX),
                (int) (y * scaleY),
                (int) (width * scaleX),
                (int) (height * scaleY)
        );
        
        float scaledFontSize = Math.max(8.0f, (float) (baseFontSize * scaleY));
        component.setFont(component.getFont().deriveFont(scaledFontSize));
    }


    /**
     * 数字を右寄せで表示するラベル
     */
    private static class ResultValueLabel
            extends javax.swing.JLabel {

        private static final long serialVersionUID = 1L;

        public ResultValueLabel() {

            setHorizontalAlignment(
                    javax.swing.SwingConstants.RIGHT
            );
        }
    }
}