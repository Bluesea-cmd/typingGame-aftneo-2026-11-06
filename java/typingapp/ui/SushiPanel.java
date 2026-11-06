package typingapp.ui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;

public class SushiPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private static final int SUSHI_WIDTH = 150;
    private static final int SUSHI_HEIGHT = 150;
    
    private static final double BASE_WIDTH = 1080.0;   
    private static final double BASE_HEIGHT = 900.0;
    private static final double BASE_LANE_Y = 750.0; 
    
    private int sushiX = -SUSHI_WIDTH;
    private BufferedImage sushiImage;
    
    private final Random random = new Random();
    
    private JFXPanel fxPanel;

    private MeshView sushiMesh;

    private Rotate rotateZ;
    private Rotate rotateX;
//    private Rotate rotateY;
    private double angle = 0.0;
//    private double flipAngle = 0.0;

    public SushiPanel(String difficulty) {
        setOpaque(false); // 背景画像を透過させるため
        setLayout(new BorderLayout());

        loadSushiImage(difficulty);

        initializeJavaFX();
    }

    private void initializeJavaFX() {

        fxPanel = new JFXPanel();
        fxPanel.setOpaque(false);

        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
             
        	// 3D平面を作成
            TriangleMesh mesh = createBoxMesh();
            sushiMesh = new MeshView(mesh);
            
            // 表裏どちらからでも見えるようにする
            sushiMesh.setCullFace(CullFace.NONE);
            
            // X軸回転（前から見下ろす視点のために、画像を傾ける）
            rotateX = new Rotate(-30, 0, SUSHI_HEIGHT / 2.0, 0, Rotate.X_AXIS);

//            // Y軸回転（奥行き感を出す）
//            rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
            
            // Z軸回転（今のところ未使用）
            rotateZ = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

//            sushiMesh.getTransforms().addAll(rotateX, rotateY, rotateZ);
            sushiMesh.getTransforms().addAll(rotateX, rotateZ);
            
            Image image = SwingFXUtils.toFXImage(sushiImage, null);
            PhongMaterial material = new PhongMaterial();
            
            material.setDiffuseMap(image);
            sushiMesh.setMaterial(material);

            // 3D空間
            Group root = new Group();
            root.getChildren().add(sushiMesh);

            // 常に明るくする環境光
            AmbientLight ambientLight = new AmbientLight(javafx.scene.paint.Color.WHITE);
            root.getChildren().add(ambientLight);
            
            ParallelCamera camera = new ParallelCamera();

            camera.setNearClip(0.1);
            camera.setFarClip(4000.0);


            Scene scene = new Scene(
            	root,
            	getWidth() > 0 ? getWidth() : BASE_WIDTH,
            	getHeight() > 0 ? getHeight() : BASE_HEIGHT,
            	true
            );
            
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.setCamera(camera);

            fxPanel.setScene(scene);

            updatePosition();
        });
    }

    private TriangleMesh createBoxMesh() {

        TriangleMesh mesh = new TriangleMesh();

        float width = SUSHI_WIDTH;
        float height = SUSHI_HEIGHT;

        // 厚み
        float depth = 15.0f;

        float hw = width / 2.0f;
        float hh = height / 2.0f;
        float hd = depth / 2.0f;

        /*
         * 頂点
         *
         *      0 -------- 1
         *      |          |
         *      |          |
         *      |          |
         *      3 -------- 2
         */
        mesh.getPoints().addAll(
        		
        		// 前面
                -hw, -hh, -hd, // 0 左上
                 hw, -hh, -hd, // 1 右上
                 hw,  hh, -hd, // 2 右下
                -hw,  hh, -hd, // 3 左下

                // 背面
                -hw, -hh, hd,  // 4
                 hw, -hh, hd,  // 5
                 hw,  hh, hd,  // 6
                -hw,  hh, hd   // 7
        );

        // テクスチャ座標
        mesh.getTexCoords().addAll(
                0.0f, 0.0f, // 0: 左上
                1.0f, 0.0f, // 1: 右上
                1.0f, 1.0f, // 2: 右下
                0.0f, 1.0f, // 3: 左下
                0.01f, 0.01f // 4: 側面用（端のピクセル）
            );

        // 各面
        mesh.getFaces().addAll(
        		// 前面（画像を表示）
                0,0,  1,1,  2,2,
                0,0,  2,2,  3,3,

                // 背面（単色）
                5,4,  4,4,  7,4,
                5,4,  7,4,  6,4,

                // 上面
                4,4,  5,4,  1,4,
                4,4,  1,4,  0,4,

                // 下面
                3,4,  2,4,  6,4,
                3,4,  6,4,  7,4,

                // 左面
                4,4,  0,4,  3,4,
                4,4,  3,4,  7,4,

                // 右面
                1,4,  5,4,  6,4,
                1,4,  6,4,  2,4
            );

            return mesh;
        }

    private void updateTexture() {

        if (sushiImage == null || sushiMesh == null) {
            return;
        }

        Image image = SwingFXUtils.toFXImage(sushiImage,null);

        PhongMaterial material = new PhongMaterial();

        material.setDiffuseMap(image);

        sushiMesh.setMaterial(material);
    }
    
    private String getFolderName(String difficulty) {

        switch (difficulty) {
            case "初級": return "寿司緑皿";
            case "中級": return "寿司青皿";
            case "上級": return "寿司赤皿";

            default: throw new IllegalArgumentException("不明な難易度：" + difficulty);
        }
    }
    
    private void loadSushiImage(String difficulty) {

        try {
            String folder = getFolderName(difficulty);
            String fileName = chooseRandomImage(difficulty);
            String path = "/images/sushi/" + folder + "/" + fileName;

            System.out.println("Loading image: " + path);

            var url = getClass().getResource(path);

            if (url == null) {
                throw new RuntimeException("画像がありません : " + path);
            }
            
            System.out.println("画像ファイル発見: " + url);
            
            sushiImage = ImageIO.read(url);

            if (sushiImage == null) {
                throw new IOException("画像を読み込めませんでした: " + path);
            }
            
            System.out.println( "画像読み込み成功: " + sushiImage.getWidth() + " × " + sushiImage.getHeight());
            
        } catch (Exception e) {
            System.err.println("寿司画像の読み込みに失敗しました。");
            e.printStackTrace();
        }
    }
    
    private int getLaneY() {

        int panelHeight = getHeight();

        if (panelHeight <= 0) {
            return 0;
        }

        return (int) (BASE_LANE_Y * panelHeight / BASE_HEIGHT);
    }
    
    private List<String> loadSushiList(String difficulty) throws IOException {

        String listFile = switch (difficulty) {
            case "初級" -> "/sushi/寿司緑皿.txt";
            case "中級" -> "/sushi/寿司青皿.txt";
            case "上級" -> "/sushi/寿司赤皿.txt";
            default -> throw new IllegalArgumentException("不明な難易度");
        };

        List<String> sushiList = new ArrayList<>();
        
        var stream = getClass().getResourceAsStream(listFile);

        if (stream == null) {
            throw new IOException("寿司リストがありません : "  + listFile);
        }
        
        try (BufferedReader reader = new BufferedReader( new InputStreamReader(
	        getClass().getResourceAsStream(listFile),
	        StandardCharsets.UTF_8))) {
        	
            String line;

            while ((line = reader.readLine()) != null) {

                if (!line.isBlank()) {
                    sushiList.add(line.trim());
                }
            }
        }

        if (sushiList.isEmpty()) {
            throw new IOException("寿司リストが空です : " + listFile);
        }
        
        return sushiList;
    }
    
    public void resetPosition() {
        sushiX = - SUSHI_WIDTH;
        angle = 0.0; // リセット時に角度も0に戻す場合
        
        Platform.runLater(() -> {
//        	if (rotateY != null) { rotateY.setAngle(0);}
            if (rotateZ != null) { rotateZ.setAngle(0);}
        });
        
        
        updatePosition();
        
        repaint();
    }
    
    private String chooseRandomImage(String difficulty) throws IOException {

        List<String> sushiList = loadSushiList(difficulty);

        if (sushiList.isEmpty()) {
            throw new RuntimeException("寿司リストが空です");
        }

        String sushiName = sushiList.get(random.nextInt(sushiList.size()));

        return sushiName + ".png";
    }
    
    public void changeSushi(String difficulty) {

        loadSushiImage(difficulty);

        Platform.runLater(() -> {

            updateTexture();

            resetPosition();
        });
    }
    
    private void updatePosition() {

        if (sushiMesh == null) {
            return;
        }

        int laneY = getLaneY();
        double x = sushiX;
        double y = laneY - SUSHI_HEIGHT / 2.0;

        Platform.runLater(() -> {
        	if (sushiMesh != null) {
        		sushiMesh.setTranslateX(x);
        		sushiMesh.setTranslateY(y);
        		}
        });
    }
    
    @Override
    public void doLayout() {

        super.doLayout();

        if (fxPanel == null) {
            return;
        }
        
        int w = getWidth();
        int h = getHeight();
        
        fxPanel.setBounds( 0, 0, w, h);

        Platform.runLater(() -> {
            if (fxPanel.getScene() != null && w > 0 && h > 0) {
                // 位置の計算を最新の幅・高さで再実行
                updatePosition();
            }
        });
    }
    
    public void move() {
    	
    	int panelWidth = getWidth();
        
        // パネル幅がまだ取得できていない場合の安全対策
    	if (panelWidth <= 0 && getParent() != null) {
            panelWidth = getParent().getWidth();
        }
        
    	if (panelWidth <= 0) {
            return;
        }
    	
    	int totalDistance = panelWidth + SUSHI_WIDTH;
        int step = Math.max(2,(int)Math.ceil(totalDistance / 120.0));
        sushiX += step;

        // Z軸回転（フリスビーのような回転）
        angle += 9;
        
        if (angle >= 360.0) {
            angle -= 360.0;
        }
        
//        flipAngle += 3.0;
//        // 値が大きくなり続けないようにする
//        if (flipAngle >= 360.0) {
//            flipAngle -= 360.0;
//        }
        
        //JavaFXスレッドで回転角度を更新        
        double currentAngle = angle;
//        double currentFlipAngle = flipAngle;

     Platform.runLater(() -> {
         if (rotateZ != null) {
             rotateZ.setAngle(currentAngle);
         }
         
//         if (rotateY != null) {
//             rotateY.setAngle(currentFlipAngle);
//         }
         
     });
     
        //画面右端を超えたら左端から再登場
        if (sushiX > panelWidth + SUSHI_WIDTH) {
            resetPosition();
            return;
        }

        /*
         * JavaFX側の位置を更新
         */
        updatePosition();
    }

    public int getSushiPosition() {
    	return sushiX;
    }
}
