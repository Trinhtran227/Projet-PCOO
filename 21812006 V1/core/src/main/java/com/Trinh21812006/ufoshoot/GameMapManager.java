package com.Trinh21812006.ufoshoot;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

public class GameMapManager implements Disposable {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private MyActor player; // Thêm dòng này cạnh các biến cũ

    // Khai báo các biến để lưu thông tin từ Tiled
    private Rectangle spawnArea;
    private float spawnInterval; // Lấy từ SpawnTime
    private float enemySpeed;    // Lấy từ Speed
    private String enemyTextureName; // Lấy từ TextureName
    private int totalUfos;       // Lấy từ ufoCount


    private float spawnTimer = 0;    // Bộ đếm thời gian để sinh quái
    private int currentSpawned = 0;  // Đếm xem đã sinh bao nhiêu con rồi
    private Texture enemyTexture;    // Lưu hình ảnh UFO
    private int enemyHealth;         // Lưu máu của quái

    // Thêm vào phần khai báo biến của GameMapManager
    private Rectangle bossSpawnArea;
    private float bossSpeed;
    private int bossHealth;
    private String bossTextureName;
    private boolean bossSpawned = false;
    private String bossTrigger; // Sẽ lưu giá trị "onClear"
    // Thêm/Sửa các biến này trong GameMapManager
    private int totalBosses;      // Lấy từ ufoCount của Boss trên Tiled
    private int currentBossSpawned = 0; // Đếm số Boss đã sinh
    private float bossSpawnTimer = 0;
    private int bossesCreated = 0; // Đếm số Boss đã được sinh ra thực tế
    private float bossTimer = 0;   // Bộ đếm thời gian trễ giữa mỗi con Boss// Bộ đệm thời gian sinh giữa các Boss
    private float bossSpawnInterval;

    // Hàm nạp bản đồ
    public void loadMap(String filePath) {
        // 1. Dùng TmxMapLoader để đọc file .tmx
        map = new TmxMapLoader().load(filePath);

        // 2. Tạo bộ vẽ (Renderer) cho bản đồ này
        // Tham số thứ 2 là tỉ lệ (unit scale). 1f nghĩa là 1 pixel = 1 đơn vị
        renderer = new OrthogonalTiledMapRenderer(map, 1f);

        System.out.println("Map loaded thành công!");
    }

    // Hàm vẽ bản đồ lên màn hình (gọi liên tục)
    public void render(OrthographicCamera camera) {
        if (renderer == null) return;

        // Cập nhật camera cho renderer biết đang nhìn vào đâu
        renderer.setView(camera);

        // Vẽ toàn bộ các layer (Background, v.v.)
        renderer.render();
    }
    // Trong GameMapManager.java
    public void setupPlayer(Stage stage) {
        // 1. Tìm layer chứa Player
        MapLayer playerLayer = map.getLayers().get("PlayerLayer");
        if (playerLayer == null) return; // Bảo vệ nếu không tìm thấy layer

        // 2. Lấy đối tượng tên là "playerShip3_red"
        MapObject playerObj = playerLayer.getObjects().get("playerShip3_red");

        if (playerObj != null) {
            float x = 0;
            float y = 0;

            // KIỂM TRA LOẠI ĐỐI TƯỢNG (Đây là phần quan trọng nhất cần sửa)
            if (playerObj instanceof RectangleMapObject) {
                // Nếu là hình chữ nhật (cách cũ)
                Rectangle rect = ((RectangleMapObject) playerObj).getRectangle();
                x = rect.x;
                y = rect.y;
            } else if (playerObj instanceof TiledMapTileMapObject) {
                // Nếu là hình ảnh con tàu (Trường hợp hiện tại của bạn)
                TiledMapTileMapObject tileObj = (TiledMapTileMapObject) playerObj;
                x = tileObj.getX();
                y = tileObj.getY();
            }

            // 3. Đọc các thông số tốc độ và tỉ lệ bắn từ Tiled
            float speed = playerObj.getProperties().get("speed", 300f, Float.class);
            float fireRate = playerObj.getProperties().get("fireRate", 0.5f, Float.class);

            // 4. Tạo Actor và truyền tọa độ x, y đã lấy được vào
            MyActor player = new MyActor(x, y, speed, fireRate, stage);
            this.player = player; // THÊM DÒNG NÀY: Lưu lại để dùng cho Enemy

            // In ra console để bạn kiểm tra xem code có chạy vào đây không
            System.out.println("Đã tạo Player từ Tiled thành công tại: " + x + ", " + y);
        } else {
            System.out.println("LỖI: Không tìm thấy đối tượng playerShip3_red trên Map!");
        }


    }

    public void loadEnemyWaves() {
        MapLayer waveLayer = map.getLayers().get("EnemyWaves");
        MapObject waveObj = waveLayer.getObjects().get("EnemyWave");

        if (waveObj instanceof RectangleMapObject) {
            RectangleMapObject rectObj = (RectangleMapObject) waveObj;
            this.spawnArea = rectObj.getRectangle();

            MapProperties props = waveObj.getProperties();
            this.spawnInterval = props.get("SpawnTime", 2.0f, Float.class);
            this.enemySpeed = props.get("Speed", 200.0f, Float.class);
            this.enemyTextureName = props.get("TextureName", "ufoRed", String.class);
            this.totalUfos = props.get("ufoCount", 5, Integer.class);

            // --- PHẦN THÊM MỚI ---
            this.enemyHealth = props.get("health", 1, Integer.class);
            // Nạp ảnh từ thư mục assets/images/ufoRed.png
            this.enemyTexture = new Texture("images/" + enemyTextureName + ".png");
            // ----------------------

            System.out.println("Đã nạp Wave: " + enemyTextureName + " thành công!");
        }

        // NẠP THÔNG TIN BOSS
        MapObject bossObj = map.getLayers().get("EnemyWaves").getObjects().get("Boss");
        if (bossObj instanceof RectangleMapObject) {
            RectangleMapObject rectObj = (RectangleMapObject) bossObj;
            this.bossSpawnArea = rectObj.getRectangle();

            MapProperties props = bossObj.getProperties();
            this.bossSpawnInterval = props.get("SpawnTime", 1.0f, Float.class); // Mặc định 1 giây nếu không tìm thấy
            this.bossSpeed = props.get("Speed", 300.0f, Float.class);
            this.bossHealth = props.get("health", 2, Integer.class);
            this.bossTextureName = props.get("TextureName", "ufoBlue", String.class);
            this.bossTrigger = props.get("Trigger", "onClear", String.class);
            this.totalBosses = props.get("ufoCount", 1, Integer.class); // Đọc số lượng Boss
            System.out.println("Đã nạp dữ liệu Boss: " + bossTextureName + " chuẩn bị xuất hiện khi " + bossTrigger);
        }
    }

    // Dán đoạn này vào trên hàm dispose() trong GameMapManager.java
    public void update(float delta, Stage stage) {
        // Nếu chưa nạp vùng sinh quái hoặc đã sinh đủ số lượng thì dừng
        if (spawnArea == null || currentSpawned >= totalUfos) return;

        spawnTimer += delta;

        // Nếu thời gian chờ đã đủ (SpawnTime từ Tiled)
        if (spawnTimer >= spawnInterval) {
            // Lấy tọa độ X ngẫu nhiên trong khung EnemyWave
            float randomX = spawnArea.x + (float) (Math.random() * spawnArea.width);
            // Tọa độ Y ở ngay mép trên của khung
            float randomY = spawnArea.y + spawnArea.height;

            // TẠO ENEMY VỚI PLAYER ĐỂ ĐUỔI THEO
            // Tham số cuối cùng 'this.player' đã được lưu ở hàm setupPlayer
            Enemy enemy = new Enemy(randomX, randomY, enemySpeed, enemyHealth, enemyTexture, this.player);
            stage.addActor(enemy);

            // Reset bộ đếm và tăng số lượng đã sinh
            spawnTimer = 0;
            currentSpawned++;
            System.out.println("Đã sinh UFO thứ: " + currentSpawned);
        }
    }
    public void checkAndSpawnBoss(float delta, Stage stage) {
        // 1. Điều kiện: Đã hết Wave 1 và chưa sinh đủ số lượng Boss (ufoCount từ Tiled)
        if (currentSpawned >= totalUfos && bossesCreated < totalBosses && "onClear".equals(bossTrigger)) {

            // 2. Kiểm tra xem trên màn hình còn Enemy thường nào không
            boolean enemyAlive = false;
            for (Actor a : stage.getActors()) {
                if (a instanceof Enemy && !((Enemy) a).isBoss()) {
                    enemyAlive = true;
                    break;
                }
            }

            // 3. Nếu đã sạch bóng quân địch, bắt đầu đếm thời gian để sinh lần lượt
            if (!enemyAlive) {
                bossTimer += delta; // Cộng dồn thời gian thực

                // Sau mỗi 0.2 giây thì sinh 1 con
                if (bossTimer >= bossSpawnInterval) {
                    Texture bossTxt = new Texture("images/" + bossTextureName + ".png");

                    // Vị trí Random trong vùng Boss của Tiled
                    float rx = bossSpawnArea.x + (float) (Math.random() * (bossSpawnArea.width - 100));
                    float ry = bossSpawnArea.y + (float) (Math.random() * bossSpawnArea.height);

                    Enemy boss = new Enemy(rx, ry, bossSpeed, bossHealth, bossTxt, this.player);
                    boss.setBoss(true);
                    boss.setScale(2.0f);
                    stage.addActor(boss);

                    bossesCreated++; // Tăng số lượng đã tạo
                    bossTimer = 0;    // Reset timer để chờ 0.2 giây tiếp theo cho con sau

                    System.out.println("Boss thứ " + bossesCreated + " đã xuất hiện!");
                }
            }
        }
    }
    // Thêm vào cuối class GameMapManager.java, trước hàm dispose()

    // Trong GameMapManager.java
    public void reset() {
        this.currentSpawned = 0;   // Reset số UFO thường đã sinh
        this.bossesCreated = 0;    // Reset số Boss đã sinh
        this.spawnTimer = 0;       // Reset bộ đếm thời gian sinh quái
        this.bossTimer = 0;        // Reset bộ đếm thời gian chờ Boss
    }

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (renderer != null) renderer.dispose();
    }
}
