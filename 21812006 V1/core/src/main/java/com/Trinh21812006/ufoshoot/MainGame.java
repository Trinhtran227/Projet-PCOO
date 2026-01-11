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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainGame extends ApplicationAdapter {
    private OrthographicCamera camera;
    private GameMapManager mapManager;
    private Stage stage;

    // Composants MVC
    private GameModel gameModel;
    private PlayerController playerController;
    private CollisionController collisionController;

    private float shakeTimer = 0;
    private float shakeIntensity = 0;
    private BitmapFont font;
    private SpriteBatch batch;
    private Sound playerExplodeSound;
    private float gameOverTimer = 0;

    @Override
    public void create() {
        // Chargement centralisé des ressources (Singleton)
        ResourceManager.getInstance().loadAll();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 1024);
        stage = new Stage(new FitViewport(1280, 1024, camera));

        playerExplodeSound = ResourceManager.getInstance().getSound(Constants.SOUND_EXPLOSION);

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

        createTutorial();
    }

    private void createTutorial() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.GREEN);
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.YELLOW);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label titleLabel = new Label("SURVIVE UFO", titleStyle);
        titleLabel.setFontScale(2.5f);

        Label moveLabel = new Label("MOVE & ROTATE: [Arrow Keys]", labelStyle);
        Label shootLabel = new Label("SHOOT: [SPACE]", labelStyle);

        table.add(titleLabel).padBottom(60).row();
        table.add(moveLabel).padBottom(20).row();
        table.add(shootLabel).row();

        // Animation : Apparaître, attendre, disparaître, se supprimer
        table.addAction(Actions.sequence(
            Actions.alpha(0),
            Actions.fadeIn(1f),
            Actions.delay(3f),
            Actions.fadeOut(2f),
            Actions.removeActor()
        ));

        stage.addActor(table);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // La logique du jeu ne tourne que si le jeu n'est pas terminé (Game Over) ET qu'il n'est pas gagné
        if (!gameModel.isGameOver() && !gameModel.isVictory()) {
            stage.act(delta);
            playerController.update(delta);

            if (!gameModel.isCollisionOccurred()) {
                mapManager.update(delta, stage);
                mapManager.checkAndSpawnBoss(delta, stage);
                collisionController.update(stage, delta);

                // Vérifier la condition de victoire
                if (mapManager.isMissionCompleted(stage)) {
                    gameModel.setVictory(true);
                }
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

        // Cas : GAME OVER
        if (gameModel.isGameOver()) {
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER", 500, 600);

            font.setColor(Color.WHITE);
            font.getData().setScale(2f);
            font.draw(batch, "Press R to Restart", 520, 500);
            font.getData().setScale(3f);
        }

        // Cas : VICTOIRE
        if (gameModel.isVictory()) {
            font.setColor(Color.GREEN);
            font.draw(batch, "YOU WIN!", 540, 600);

            font.setColor(Color.WHITE);
            font.getData().setScale(2f);
            font.draw(batch, "Press R to Play Again", 500, 500);
            font.getData().setScale(3f);
        }

        if ((gameModel.isGameOver() || gameModel.isVictory()) && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            restartGame();
        }

        batch.end();
    }

    public void triggerShake() {
        this.shakeIntensity = 25f;
        this.shakeTimer = 0.5f;
        this.gameOverTimer = 1.5f;

        if (playerExplodeSound != null) {
            playerExplodeSound.play(1.0f);
        }
    }

    private void restartGame() {
        gameModel.reset();
        gameOverTimer = 0;
        stage.clear();
        mapManager.reset();
        mapManager.setupPlayer(stage);
        playerController.setPlayer(mapManager.getPlayer());

        createTutorial(); // Afficher à nouveau le tutoriel

        camera.position.set(640, 512, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        mapManager.dispose();
        stage.dispose();
        ResourceManager.getInstance().dispose();
    }
}
