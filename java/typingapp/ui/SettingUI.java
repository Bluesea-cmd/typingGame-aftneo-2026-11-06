package typingapp.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import typingapp.util.WindowSettings;


public class SettingUI {

    private JDialog dialog;

    private JComboBox<String> screenSizeComboBox;

    private JRadioButton smallRadioButton;
    private JRadioButton normalRadioButton;
    private JRadioButton largeRadioButton;
    private JRadioButton extraLargeRadioButton;

    private JButton applyButton;
    private JButton cancelButton;

    // 設定対象のフレーム
    private java.awt.Window targetWindow;
    
    private int selectedWidth = 1024;
    private int selectedHeight = 576;
    private int selectedFontSize = 16;
    
    private boolean isFullScreen = false;
    
    public SettingUI(java.awt.Window targetWindow) {
    	
        this.targetWindow = targetWindow;

        if (targetWindow != null) {
            selectedWidth = targetWindow.getWidth();
            selectedHeight = targetWindow.getHeight();
        }
        
        createUI();
    }

    private void applyWindowSize() {

        if (targetWindow == null) {
            return;
        }

        if (!(targetWindow instanceof JFrame frame)) {
            return;
        }

        // ========================================
        // フルスクリーン
        // ========================================

        if (isFullScreen) {
        	
        	// すでに枠なしフルスクリーン状態ならBoundsの保存は行わない
            if (!frame.isUndecorated()) {
                // setUndecorated を変更するために再構成
                frame.dispose();
                frame.setUndecorated(true);
            }

            // ディスプレイ画面全体の領域（タスクバー含む）を取得
            GraphicsConfiguration gc = frame.getGraphicsConfiguration();
            
            if (gc == null) {
                gc = GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice()
                        .getDefaultConfiguration();
            }
            
            Rectangle bounds = gc.getBounds();

            // フレームを画面いっぱいにリサイズ＆配置
            frame.setBounds(bounds);
            frame.setExtendedState(JFrame.NORMAL);
            frame.setVisible(true);

            frame.revalidate();
            frame.repaint();
            
            System.out.println(
                    "フルスクリーンに変更しました: "
                            + bounds.width
                            + " × "
                            + bounds.height
            );
            
        // ========================================
        // 通常画面
        // ========================================
        } else {

            // 枠なしフルスクリーンから戻す
            if (frame.isUndecorated()) {

                // 枠を戻すために再構成
                frame.dispose();
                frame.setUndecorated(false);
            }

            // 最大化状態を解除
            frame.setExtendedState(JFrame.NORMAL);

            // 画面サイズの適用
            Dimension newSize = new Dimension(selectedWidth, selectedHeight);
            frame.setPreferredSize(newSize);
            frame.setSize(newSize);

            // ========================================
            // 常に画面中央へ配置
            // ========================================

            frame.setLocationRelativeTo(null);

            frame.setVisible(true);

            frame.revalidate();
            frame.repaint();

            System.out.println(
                    "画面サイズ変更: "
                            + frame.getWidth()
                            + " × "
                            + frame.getHeight()
            );
        }

        // ========================================
        // 設定画面をタイトル画面の中央へ
        // ========================================

        if (dialog != null && dialog.isShowing()) {

            dialog.setLocationRelativeTo(frame);

            dialog.toFront();
            dialog.requestFocus();
        }
    }
    
    private void createUI() {

        // タイトル画面を親にする
        dialog = new JDialog(
            (java.awt.Frame) null,
            "設定",
            true
        );

        dialog.setSize(400, 300);

        // タイトル画面の中央に表示
        dialog.setLocationRelativeTo(targetWindow);

        JPanel mainPanel = new JPanel(new GridBagLayout());

        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ==============================
        // 画面サイズ
        // ==============================

        JLabel screenSizeLabel =
                new JLabel("画面サイズ");

        screenSizeLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        18
                )
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        mainPanel.add(
                screenSizeLabel,
                gbc
        );

        screenSizeComboBox =
                new JComboBox<>(
                        new String[] {
                                "800 × 450",
                        		"1024 × 576",
                                "フルスクリーン"
                        }
                );

     // 現在の画面サイズをコンボボックスに反映
        if (targetWindow != null) {

            int currentWidth = targetWindow.getWidth();
            int currentHeight = targetWindow.getHeight();

            String currentSize =
                    currentWidth + " × " + currentHeight;

            screenSizeComboBox.setSelectedItem(currentSize);
        }

        screenSizeComboBox.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        18
                )
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        mainPanel.add(
                screenSizeComboBox,
                gbc
        );

        // ==============================
        // 文字サイズ
        // ==============================

        JLabel fontSizeLabel =
                new JLabel("文字サイズ");

        fontSizeLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        18
                )
        );

        gbc.gridx = 0;
        gbc.gridy = 1;

        mainPanel.add(
                fontSizeLabel,
                gbc
        );

        JPanel fontPanel =
                new JPanel();

        smallRadioButton =
                new JRadioButton("小");

        normalRadioButton =
                new JRadioButton("標準");

        largeRadioButton =
                new JRadioButton("大");

        extraLargeRadioButton =
                new JRadioButton("特大");

        ButtonGroup fontGroup =
                new ButtonGroup();

        fontGroup.add(smallRadioButton);
        fontGroup.add(normalRadioButton);
        fontGroup.add(largeRadioButton);
        fontGroup.add(extraLargeRadioButton);

        normalRadioButton.setSelected(true);

        fontPanel.add(smallRadioButton);
        fontPanel.add(normalRadioButton);
        fontPanel.add(largeRadioButton);
        fontPanel.add(extraLargeRadioButton);

        gbc.gridx = 1;

        mainPanel.add(
                fontPanel,
                gbc
        );

        // ==============================
        // ボタン
        // ==============================

        JPanel buttonPanel =
                new JPanel();

        applyButton =
                new JButton("適用");

        cancelButton =
                new JButton("閉じる");

        applyButton.setPreferredSize(
                new Dimension(120, 40)
        );

        cancelButton.setPreferredSize(
                new Dimension(120, 40)
        );

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        mainPanel.add(
                buttonPanel,
                gbc
        );

        // ==============================
        // 適用
        // ==============================

        applyButton.addActionListener(_ -> {

            // 選択された画面サイズを取得
            updateSelectedScreenSize();

            // 選択された文字サイズを取得
            updateSelectedFontSize();

            if (isFullScreen) {
                WindowSettings.setFullScreen(true);
            } else {
                WindowSettings.setSize(
                        selectedWidth,
                        selectedHeight
                );
            }

            WindowSettings.setFontSize(selectedFontSize);
            
//            // ★重要: frame.dispose() を行う前にダイアログを隠す
//            dialog.setVisible(false);

            // タイトル画面にサイズを反映
            applyWindowSize();

//            // ダイアログを破棄する
//            dialog.dispose();
            
         // 設定ダイアログを親画面の前面に維持・フォーカスをあてる
            if (dialog != null && dialog.isShowing()) {
                dialog.toFront();
                dialog.requestFocus();
            }
        });
        // ==============================
        // キャンセル
        // ==============================

        cancelButton.addActionListener(_ -> {

            dialog.dispose();
        });

        dialog.add( mainPanel, BorderLayout.CENTER);
    }
    
    //画面サイズを取得

    private void updateSelectedScreenSize() {

        String selected = (String) screenSizeComboBox.getSelectedItem();

        isFullScreen = false;

        if ("800 × 450".equals(selected)) {

            selectedWidth = 800;
            selectedHeight = 450;

        } else if ("1024 × 576".equals(selected)) {

            selectedWidth = 1024;
            selectedHeight = 576;

        } else if ("フルスクリーン".equals(selected)) {

            isFullScreen = true;
        }
    }

    /**
     * 文字サイズを取得
     */
    private void updateSelectedFontSize() {

        if (smallRadioButton.isSelected()) {

            selectedFontSize = 12;

        } else if (normalRadioButton.isSelected()) {

            selectedFontSize = 16;

        } else if (largeRadioButton.isSelected()) {

            selectedFontSize = 20;

        } else if (extraLargeRadioButton.isSelected()) {

            selectedFontSize = 24;
        }
    }

    /**
     * 設定画面を表示
     */
    public void show() {
        dialog.setVisible(true);
    }

    public int getSelectedWidth() {
        return selectedWidth;
    }

    public int getSelectedHeight() {
        return selectedHeight;
    }

    public int getSelectedFontSize() {
        return selectedFontSize;
    }
}