package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainGame extends ApplicationAdapter {
    private OrthographicCamera camera;
    private GameMapManager mapManager;
    private Stage stage;
    private MyActor player;

    private float shakeTimer = 0;
    private float shakeIntensity = 0;
    private BitmapFont font;
    private int score = 0;
    private boolean isGameOver = false;
    private SpriteBatch batch;
    // Trong MainGame.java, thêm biến mới:
    private float gameOverTimer = 0;
    private boolean collisionOccurred = false; // Đánh dấu đã va chạm

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 1024);
        stage = new Stage(new FitViewport(1280, 1024, camera));

        mapManager = new GameMapManager();
        mapManager.loadMap("maps/level1.tmx");
        mapManager.setupPlayer(stage);
        mapManager.loadEnemyWaves();

        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        font.getData().setScale(3f);
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isGameOver) {
            stage.act(delta); // Hiệu ứng nổ cần act() để chạy animation

            if (!collisionOccurred) {
                mapManager.update(delta, stage);
                mapManager.checkAndSpawnBoss(delta, stage);
                checkCollisions();
            } else {
                // Nếu đã va chạm, đếm ngược thời gian
                gameOverTimer -= delta;
                if (gameOverTimer <= 0) {
                    isGameOver = true;
                    // Sau khi nổ xong mới thực sự remove player
                    if (player != null) player.remove();
                }
            }
        }

        // Xử lý Rung Camera
        if (shakeTimer > 0) {
            camera.position.x = 640 + MathUtils.random(-shakeIntensity, shakeIntensity);
            camera.position.y = 512 + MathUtils.random(-shakeIntensity, shakeIntensity);
            shakeTimer -= delta;
        } else {
            camera.position.set(640, 512, 0);
        }
        camera.update();

        mapManager.render(camera);
        stage.draw();

        // Vẽ UI
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, "SCORE: " + score, 50, 980);

        if (isGameOver) {
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER", 500, 600);
            font.setColor(Color.WHITE);
            font.draw(batch, "Press R to Restart", 480, 500);

            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                restartGame();
            }
        }
        batch.end();
    }

    private void checkCollisions() {
        if (collisionOccurred) return; // Nếu đã va chạm rồi thì không xét nữa


        MyActor playerActor = null;
        for (Actor a : stage.getActors()) {
            if (a instanceof MyActor) playerActor = (MyActor) a;
        }

        for (int i = 0; i < stage.getActors().size; i++) {
            Actor a = stage.getActors().get(i);

            if (a instanceof Enemy) {
                Enemy enemy = (Enemy) a;
                if (playerActor != null && playerActor.getBounds().overlaps(enemy.getBounds())) {
                    stage.addActor(new Explosion(playerActor.getX(), playerActor.getY(), 100, 100));
                    stage.addActor(new Explosion(enemy.getX(), enemy.getY(), 100, 100));
                    shake(20f, 0.5f);

                    playerActor.setVisible(false);
                    enemy.setVisible(false);

                    collisionOccurred = true;
                    gameOverTimer = 1.0f; // Chờ 1.5 giây mới hiện Game Over

                    System.out.println("Va chạm đã xảy ra, đang chờ nổ...");
                }
            }

            if (a instanceof Bullet) {
                Bullet bullet = (Bullet) a;
                for (Actor other : stage.getActors()) {
                    if (other instanceof Enemy) {
                        Enemy e = (Enemy) other;
                        if (bullet.getBounds().overlaps(e.getBounds())) {
                            e.takeDamage(1);
                            bullet.remove();
                            if (e.getHealth() <= 0) score += (e.isBoss() ? 100 : 10);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void shake(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeTimer = duration;
    }

    private void restartGame() {
        // 1. Reset các biến trạng thái
        score = 0;
        isGameOver = false;
        collisionOccurred = false; // QUAN TRỌNG: Phải reset biến này
        gameOverTimer = 0;

        // 2. Dọn dẹp Stage (xóa hết quái, đạn, vụ nổ cũ)
        stage.clear();

        // 3. Reset MapManager (đặt lại đếm quái, đếm Boss về 0)
        mapManager.reset();

        // 4. Tạo lại người chơi mới
        mapManager.setupPlayer(stage);

        // 5. Đặt lại Camera về vị trí chuẩn (phòng trường hợp đang rung)
        camera.position.set(640, 512, 0);
        camera.update();

        System.out.println("Game đã khởi động lại!");
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        mapManager.dispose();
        stage.dispose();
    }
}
