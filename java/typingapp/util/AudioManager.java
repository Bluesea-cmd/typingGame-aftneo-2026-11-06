package typingapp.util;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Random;

public class AudioManager {

    private MediaPlayer bgmPlayer;
    private MediaPlayer voicePlayer;

    private double bgmVolume = 0.5;
    private double voiceVolume = 1.0;

    private final Random random = new Random();
    
    //JavaFXを初期化する
    public AudioManager() {
        // SwingアプリからJavaFXを使用するための初期化
        new JFXPanel();
    }
    // 難易度に応じたBGMを再生する
    
    public void playDifficultyBgm(String difficulty) {

        String[] voicePaths;
        
        switch (difficulty) {

            case "初級" -> voicePaths = new String[] {
                  "/sounds/BGM/緊張をほぐすようなBGM(初級).mp3"
            };

            case "中級" -> voicePaths = new String[] {
            	"/sounds/BGM/POPなBGM(中級).mp3"
            };

            case "上級" -> voicePaths = new String[] {
                "/sounds/BGM/上級のBGM.mp3"
            };

            default -> {
                System.err.println("不明な難易度: " + difficulty);
                return;
            }
        }
        System.out.println("難易度別BGMボイス: " + difficulty);
        playRandomVoice(voicePaths);
    }
    
    //難易度に応じた開始ボイスをランダムに再生する
    //@param difficulty 難易度 ("初級", "中級", "上級")
    public void playRandomStartVoice(String difficulty) {

        String[] voicePaths;

        switch (difficulty) {
            case "初級" -> voicePaths = new String[] {
                "/sounds/Voice/初級/始まり①いらっしゃいませニャ！のれんをくぐって、お好きな席へ！.mp3",
                "/sounds/Voice/初級/始まり②カウンターへどうぞニャ！.mp3"
            };
            case "中級" -> voicePaths = new String[] {
                "/sounds/Voice/中級/始まり①いらっしゃいませニャ！ちょうど良いネタが入ったニャ！.mp3",
                "/sounds/Voice/中級/始まり②おっ、常連さんニャね。お次はお待ちかねの人気ネタをどうぞニャ！.mp3"
            };
            case "上級" -> voicePaths = new String[] {
                "/sounds/Voice/上級/始まり①いらっしゃいませニャ…！ここからは極上の最高級ネタしか回さないニャ。.mp3",
                "/sounds/Voice/上級/始まり②よくぞここまで来たニャ！大将特選、神速のフルコースを開幕するニャ！.mp3"
            };
            default -> {
                System.err.println("不明な難易度: " + difficulty);
                return;
            }
        }

        System.out.println("難易度別ランダム開始ボイス: " + difficulty);
        playRandomVoice(voicePaths);
    }
    
    
    //難易度に応じたコンボボイスをランダムに再生する
    //@param difficulty 難易度 ("初級", "中級", "上級")
    public void playRandomComboVoice(String difficulty) {

        String[] voicePaths;

        switch (difficulty) {
            case "初級" -> voicePaths = new String[] {
                "/sounds/Voice/初級/コンボ①リズムが良いニャ。テンポよく皿が空いていくニャ。.mp3"
            };
            case "中級" -> voicePaths = new String[] {
                "/sounds/Voice/中級/コンボ①流れるようなタイピングニャ、見事な箸さばきニャ。.mp3"
            };
            case "上級" -> voicePaths = new String[] {
                "/sounds/Voice/上級/コンボ①まさに神業ニャ。板前も腰を抜かす肉球さばきニャ。.mp3"
            };
            default -> {
                System.err.println("不明な難易度: " + difficulty);
                return;
            }
        }

        System.out.println("難易度別ランダム開始ボイス: " + difficulty);
        playRandomVoice(voicePaths);
    }
    
    //難易度に応じたクリアボイスをランダムに再生する
    //@param difficulty 難易度 ("初級", "中級", "上級")
    public void playRandomClearVoice(String difficulty) {

        String[] voicePaths;

        switch (difficulty) {
            case "初級" -> voicePaths = new String[] {
                "/sounds/Voice/初級/クリア①へいお待ちニャ！かっぱも玉子も、見事に完食したニャ。.mp3"
            };
            case "中級" -> voicePaths = new String[] {
                "/sounds/Voice/中級/クリア①ガッテン承知の介ニャ！脂の乗ったサーモンもあるニャ！.mp3"
            };
            case "上級" -> voicePaths = new String[] {
                "/sounds/Voice/上級/クリア①大勝利ニャ！.mp3"
            };
            default -> {
                System.err.println("不明な難易度: " + difficulty);
                return;
            }
        }

        System.out.println("難易度別ランダムクリアボイス: " + difficulty);
        playRandomVoice(voicePaths);
    }
    
    //難易度に応じたクリア進捗(50%)ボイスをランダムに再生する
    //@param difficulty 難易度 ("初級", "中級", "上級")
    public void playRandomStage50perVoice(String difficulty) {

        String[] voicePaths;

        switch (difficulty) {
            case "初級" -> voicePaths = new String[] {
                "/sounds/Voice/初級/進捗50％①いいペースニャ。大将、ノってきたニャ。.mp3"
            };
            case "中級" -> voicePaths = new String[] {
                "/sounds/Voice/中級/進捗50％①注文ラッシュ入ったニャ！.mp3"
            };
            case "上級" -> voicePaths = new String[] {
                "/sounds/Voice/上級/進捗50％①速すぎるニャ。レーンのマグロが追いつかないニャ。.mp3"
            };
            default -> {
                System.err.println("不明な難易度: " + difficulty);
                return;
            }
        }

        System.out.println("難易度別ランダム進捗50%ボイス: " + difficulty);
        playRandomVoice(voicePaths);
    }
    
    //難易度に応じた進捗(75%)ボイスをランダムに再生する
    //@param difficulty 難易度 ("初級", "中級", "上級")
    public void playRandomStage75perVoice(String difficulty) {

        String[] voicePaths;

        switch (difficulty) {
            case "初級" -> voicePaths = new String[] {
                "/sounds/Voice/初級/進捗75％①あと一貫！あと一貫ニャ！.mp3"
            };
            case "中級" -> voicePaths = new String[] {
                "/sounds/Voice/中級/進捗75％①特急レーンが通り過ぎるニャ！ラストスパートニャ！.mp3.mp3"
            };
            case "上級" -> voicePaths = new String[] {
                "/sounds/Voice/上級/進捗75％①極上のいくらが回るニャ！一瞬の油断も許さないニャ。.mp3"
            };
            default -> {
                System.err.println("不明な難易度: " + difficulty);
                return;
            }
        }

        System.out.println("難易度別ランダム75%進捗ボイス: " + difficulty);
        playRandomVoice(voicePaths);
    }
    
    //失敗したボイスをランダムに再生する
    //@param difficulty 難易度 ("初級", "中級", "上級")
    public void playRandomStageOverVoice() {

        String[] voicePaths;

        voicePaths = new String[] {
                "/sounds/Voice/クリア失敗時/おあいそ(お会計)ニャ。またの挑戦を待ってるニャ。.mp3",
                "/sounds/Voice/クリア失敗時/お魚に逃げられたニャ…出直してくるニャ。.mp3",
                "/sounds/Voice/クリア失敗時/本日閉店ニャ。次はもっとたくさん握るニャ。.mp3"
            };

        System.out.println("ランダム失敗ボイス");
        playRandomVoice(voicePaths);
    }
    
    //ミスしたボイスをランダムに再生する
    public void playRandomStageMissVoice() {

        String[] voicePaths;

        voicePaths = new String[] {
                "/sounds/Voice/ミスした時/おっと、シャリが崩れたニャ。.mp3",
                "/sounds/Voice/ミスした時/お皿を落とさないように注意ニャ。.mp3",
                "/sounds/Voice/ミスした時/ネタがシャリから落ちちゃうニャ！.mp3"
            };

        System.out.println("ランダムMissボイス");
        playRandomVoice(voicePaths);
    }

    //BGMを再生する
    //@param resourcePath resourcesからのパス
    public void playBgm(String resourcePath) {

        var resource = getClass().getResource(resourcePath);

        if (resource == null) {
            System.err.println("BGMが見つかりません: " + resourcePath);
            return;
        }

        Platform.runLater(() -> {

            // 現在のBGMを停止
            stopBgmInternal();

            try {
                Media media = new Media(resource.toExternalForm());

                bgmPlayer = new MediaPlayer(media);

                bgmPlayer.setVolume(bgmVolume);

                // 無限ループ
                bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);

                bgmPlayer.play();

                System.out.println("BGM再生: " + resourcePath);

            } catch (Exception e) {
                System.err.println("BGM再生エラー: " + resourcePath);
                e.printStackTrace();
            }
        });
    }

    /**
     * BGMを停止する
     */
    public void stopBgm() {

        Platform.runLater(this::stopBgmInternal);
    }

    /**
     * JavaFXスレッド内でBGMを停止する
     */
    private void stopBgmInternal() {

        if (bgmPlayer != null) {

            bgmPlayer.stop();
            bgmPlayer.dispose();

            bgmPlayer = null;
        }
    }

    /**
     * BGM音量を設定する
     *
     * @param volume 0.0 ～ 1.0
     */
    public void setBgmVolume(double volume) {

        bgmVolume = Math.max(0.0, Math.min(1.0, volume));

        Platform.runLater(() -> {

            if (bgmPlayer != null) {
                bgmPlayer.setVolume(bgmVolume);
            }
        });
    }

    /**
     * 副音声を再生する
     *
     * BGMは停止しない
     *
     * @param resourcePath resourcesからのパス
     */
    public void playVoice(String resourcePath) {

        var resource = getClass().getResource(resourcePath);

        if (resource == null) {
            System.err.println("音声が見つかりません: " + resourcePath);
            return;
        }

        Platform.runLater(() -> {

            // 現在の副音声を停止
            stopVoiceInternal();

            try {
                Media media = new Media(resource.toExternalForm());

                voicePlayer = new MediaPlayer(media);

                voicePlayer.setVolume(voiceVolume);

                voicePlayer.play();

                System.out.println("音声再生: " + resourcePath);

            } catch (Exception e) {
                System.err.println("音声再生エラー: " + resourcePath);
                e.printStackTrace();
            }
        });
    }
    /**
     * 複数の音声からランダムに1つを選んで再生する
     *
     * BGMは停止しない
     *
     * @param resourcePaths 音声ファイルのパス配列
     */
    public void playRandomVoice(String[] resourcePaths) {

        if (resourcePaths == null || resourcePaths.length == 0) {
            System.err.println("音声ファイルのパスが指定されていません");
            return;
        }

        // ランダムに選択
        String selectedPath = resourcePaths[random.nextInt(resourcePaths.length)];

        System.out.println("音声選択: " + selectedPath);

        playVoice(selectedPath);
    }
    /**
     * 副音声を停止する
     */
    public void stopVoice() {

        Platform.runLater(this::stopVoiceInternal);
    }

    /**
     * JavaFXスレッド内で副音声を停止する
     */
    private void stopVoiceInternal() {

        if (voicePlayer != null) {

            voicePlayer.stop();
            voicePlayer.dispose();

            voicePlayer = null;
        }
    }

    /**
     * 副音声音量を設定する
     *
     * @param volume 0.0 ～ 1.0
     */
    public void setVoiceVolume(double volume) {

        voiceVolume = Math.max(0.0, Math.min(1.0, volume));

        Platform.runLater(() -> {

            if (voicePlayer != null) {
                voicePlayer.setVolume(voiceVolume);
            }
        });
    }

    /**
     * 全音声を停止する
     */
    public void stopAll() {

        Platform.runLater(() -> {

            stopBgmInternal();
            stopVoiceInternal();
        });
    }
}