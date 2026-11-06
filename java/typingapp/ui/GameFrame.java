package typingapp.ui;

import javax.swing.*;
import java.awt.*;

/**
 * ゲーム画面のベースとなるパネルクラス。
 * 選択された難易度に応じた背景画像を画面全体に伸縮描画します。
 */
public class GameFrame extends JPanel {

    private static final long serialVersionUID = 1L;
    private Image backgroundImage;

    /**
     * コンストラクタ
     */
    public GameFrame() {
        // 背景画像や上に載るUI要素が透過して見えるように設定
        setOpaque(true);
        setLayout(new BorderLayout());
    }

    /**
     * 難易度に応じた背景画像を読み込んで更新する
     * @param difficulty "初級", "中級", "上級"
     */
    public void setDifficultyBackground(String difficulty) {
        String imagePath = switch (difficulty) {
            case "初級" -> "/images/gameForBeginner.jpg";
            case "中級" -> "/images/gameForMidium.jpg";
            case "上級" -> "/images/gameForPro.jpg";
            default    -> "";
        };

        var resource = getClass().getResource(imagePath);
        if (resource != null) {
            this.backgroundImage = new ImageIcon(resource).getImage();
            repaint(); // 画像読み込み後に再描画をリクエスト
        } else {
            System.err.println("背景画像が見つかりません: " + imagePath);
        }
    }

    /**
     * パネルの背景画像描画処理
     */
    @Override
    protected void paintComponent(Graphics g) {
    	
        // 1. 親クラスの標準描画（背景クリア等）を最初に行う
        super.paintComponent(g);

        // 2. 背景画像が存在する場合、画面の幅・高さに合わせて拡大縮小して描画
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}