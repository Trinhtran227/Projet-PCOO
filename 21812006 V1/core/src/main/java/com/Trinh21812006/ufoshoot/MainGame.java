package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainGame extends ApplicationAdapter {
    private OrthographicCamera camera;
    private GameMapManager mapManager;
    private Stage stage;

    // CÁC THÀNH PHẦN MVC
    private GameModel gameModel;
    private PlayerController playerController;
    private CollisionController collisionController;

    private float shakeTimer = 0;
    private float shakeIntensity = 0;
    private BitmapFont font;
    private SpriteBatch batch;

    // Biến âm thanh nổ
    private Sound playerExplodeSound;

    private float gameOverTimer = 0;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 1024);
        stage = new Stage(new FitViewport(1280, 1024, camera));

        // Nạp âm thanh nổ
        // Đảm bảo file assets/sounds/explosion.mp3 tồn tại
        playerExplodeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));

        gameModel = new GameModel();

        mapManager = new GameMapManager();
        mapManager.loadMap("maps/level1.tmx");
        mapManager.setupPlayer(stage);
        mapManager.loadEnemyWaves();

        MyActor player = mapManager.getPlayer();
        playerController = new PlayerController(player);
        collisionController = new CollisionController(this, gameModel);

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

        if (!gameModel.isGameOver()) {
            stage.act(delta);
            playerController.update(delta);

            if (!gameModel.isCollisionOccurred()) {
                mapManager.update(delta, stage);
                mapManager.checkAndSpawnBoss(delta, stage);
                collisionController.update(stage, delta);
            } else {
                gameOverTimer -= delta;
                if (gameOverTimer <= 0) {
                    gameModel.setGameOver(true);
                }
            }
        }

        updateCameraShake(delta);

        mapManager.render(camera);
        stage.draw();

        renderUI();
    }

    private void updateCameraShake(float delta) {
        if (shakeTimer > 0) {
            camera.position.x = 640 + MathUtils.random(-shakeIntensity, shakeIntensity);
            camera.position.y = 512 + MathUtils.random(-shakeIntensity, shakeIntensity);
            shakeTimer -= delta;
        } else {
            camera.position.set(640, 512, 0);
        }
        camera.update();
    }

    private void renderUI() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "SCORE: " + gameModel.getScore(), 50, 980);

        if (gameModel.isGameOver()) {
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER", 500, 600);
            font.setColor(Color.WHITE);
            font.getData().setScale(2f);
            font.draw(batch, "Press R to Restart", 520, 500);
            font.getData().setScale(3f);

            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                restartGame();
            }
        }
        batch.end();
    }

    // --- SỬA LỖI TẠI ĐÂY ---
    public void triggerShake() {
        // Đã xóa dòng if (!gameModel.isCollisionOccurred()) vì Controller đã set True rồi

        this.shakeIntensity = 25f;
        this.shakeTimer = 0.5f;
        this.gameOverTimer = 1.5f;

        // Phát âm thanh
        if (playerExplodeSound != null) {
            playerExplodeSound.play(1.0f);
            System.out.println("BOOM! Sound played."); // Dòng debug để kiểm tra
        }
    }

    private void restartGame() {
        gameModel.reset();
        gameOverTimer = 0;
        stage.clear();

        mapManager.reset();
        mapManager.setupPlayer(stage);
        playerController.setPlayer(mapManager.getPlayer());

        camera.position.set(640, 512, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        mapManager.dispose();
        stage.dispose();
        if (playerExplodeSound != null) playerExplodeSound.dispose();
    }
}
