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
    private MyActor player;

    // Variables pour les vagues d'ennemis
    private Rectangle spawnArea;
    private float spawnInterval;
    private float enemySpeed;
    private String enemyTextureName;
    private int totalUfos;
    private float spawnTimer = -5.0f; // Délai initial de 5 secondes pour le tutoriel
    private int currentSpawned = 0;
    private Texture enemyTexture;
    private int enemyHealth;

    // Variables pour le Boss
    private Rectangle bossSpawnArea;
    private float bossSpeed;
    private int bossHealth;
    private String bossTextureName;
    private String bossTrigger;
    private int totalBosses;
    private int bossesCreated = 0;
    private float bossTimer = 0;
    private float bossSpawnInterval;

    public void loadMap(String filePath) {
        map = new TmxMapLoader().load(filePath);
        renderer = new OrthogonalTiledMapRenderer(map, 1f);
    }

    public void render(OrthographicCamera camera) {
        if (renderer == null) return;
        renderer.setView(camera);
        renderer.render();
    }

    public void setupPlayer(Stage stage) {
        MapLayer playerLayer = map.getLayers().get("PlayerLayer");
        if (playerLayer == null) return;

        MapObject playerObj = playerLayer.getObjects().get("playerShip3_red");
        if (playerObj != null) {
            float x = 0, y = 0;
            if (playerObj instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) playerObj).getRectangle();
                x = rect.x; y = rect.y;
            } else if (playerObj instanceof TiledMapTileMapObject) {
                TiledMapTileMapObject tileObj = (TiledMapTileMapObject) playerObj;
                x = tileObj.getX(); y = tileObj.getY();
            }

            float speed = playerObj.getProperties().get("speed", 300f, Float.class);
            float fireRate = playerObj.getProperties().get("fireRate", 0.5f, Float.class);

            this.player = new MyActor(x, y, speed, fireRate, stage);
        }
    }

    public MyActor getPlayer() {
        return this.player;
    }

    public void loadEnemyWaves() {
        // Chargement des vagues normales
        MapObject waveObj = map.getLayers().get("EnemyWaves").getObjects().get("EnemyWave");
        if (waveObj instanceof RectangleMapObject) {
            this.spawnArea = ((RectangleMapObject) waveObj).getRectangle();
            MapProperties props = waveObj.getProperties();
            this.spawnInterval = props.get("SpawnTime", 2.0f, Float.class);
            this.enemySpeed = props.get("Speed", 200.0f, Float.class);
            this.enemyTextureName = props.get("TextureName", "ufoRed", String.class);
            this.totalUfos = props.get("ufoCount", 5, Integer.class);
            this.enemyHealth = props.get("health", 1, Integer.class);

            // Optimisation : Récupérer la texture depuis le Manager
            if ("ufoRed".equals(enemyTextureName)) {
                this.enemyTexture = ResourceManager.getInstance().getTexture(Constants.IMG_UFO_RED);
            } else {
                this.enemyTexture = ResourceManager.getInstance().getTexture(Constants.IMG_UFO_BLUE);
            }
        }

        // Chargement du Boss
        MapObject bossObj = map.getLayers().get("EnemyWaves").getObjects().get("Boss");
        if (bossObj instanceof RectangleMapObject) {
            this.bossSpawnArea = ((RectangleMapObject) bossObj).getRectangle();
            MapProperties props = bossObj.getProperties();
            this.bossSpawnInterval = props.get("SpawnTime", 1.0f, Float.class);
            this.bossSpeed = props.get("Speed", 300.0f, Float.class);
            this.bossHealth = props.get("health", 2, Integer.class);
            this.bossTextureName = props.get("TextureName", "ufoBlue", String.class);
            this.bossTrigger = props.get("Trigger", "onClear", String.class);
            this.totalBosses = props.get("ufoCount", 1, Integer.class);
        }
    }

    public void update(float delta, Stage stage) {
        if (spawnArea == null || currentSpawned >= totalUfos) return;
        spawnTimer += delta;
        if (spawnTimer >= spawnInterval) {
            float randomX = spawnArea.x + (float) (Math.random() * spawnArea.width);
            float randomY = spawnArea.y + spawnArea.height;

            // Instancier l'ennemi avec la texture du Manager
            Enemy enemy = new Enemy(randomX, randomY, enemySpeed, enemyHealth, enemyTexture, this.player);
            stage.addActor(enemy);
            spawnTimer = 0;
            currentSpawned++;
        }
    }

    public void checkAndSpawnBoss(float delta, Stage stage) {
        if (currentSpawned >= totalUfos && bossesCreated < totalBosses && "onClear".equals(bossTrigger)) {
            boolean enemyAlive = false;
            for (Actor a : stage.getActors()) {
                if (a instanceof Enemy && !((Enemy) a).isBoss()) {
                    enemyAlive = true; break;
                }
            }
            if (!enemyAlive) {
                bossTimer += delta;
                if (bossTimer >= bossSpawnInterval) {
                    Texture bossTxt;
                    if ("ufoBlue".equals(bossTextureName)) {
                        bossTxt = ResourceManager.getInstance().getTexture(Constants.IMG_UFO_BLUE);
                    } else {
                        bossTxt = ResourceManager.getInstance().getTexture(Constants.IMG_UFO_RED);
                    }

                    float rx = bossSpawnArea.x + (float) (Math.random() * (bossSpawnArea.width - 100));
                    float ry = bossSpawnArea.y + (float) (Math.random() * bossSpawnArea.height);

                    Enemy boss = new Enemy(rx, ry, bossSpeed, bossHealth, bossTxt, this.player);
                    boss.setBoss(true);
                    boss.setScale(1.4f); // Échelle du Boss

                    stage.addActor(boss);
                    bossesCreated++;
                    bossTimer = 0;
                }
            }
        }
    }

    // Vérifier si la mission est accomplie (Victoire)
    public boolean isMissionCompleted(Stage stage) {
        boolean allSpawned = (currentSpawned >= totalUfos) && (bossesCreated >= totalBosses);
        if (!allSpawned) return false;

        for (Actor actor : stage.getActors()) {
            if (actor instanceof Enemy) {
                return false;
            }
        }
        return true;
    }

    public void reset() {
        this.currentSpawned = 0;
        this.bossesCreated = 0;
        this.spawnTimer = -5.0f; // Réinitialiser le délai
        this.bossTimer = 0;
    }

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (renderer != null) renderer.dispose();
    }
}
