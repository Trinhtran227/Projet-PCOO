package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Enemy extends GameEntity {
    private int health;
    private int maxHealth;
    private Actor player;
    private boolean isBoss = false;

    // Logique de déplacement
    private float zigzagTimer = 0;
    private boolean isHoming = false;
    private int directionX = 1;
    private int directionY = -1;
    private float randomPhase;
    private float randomFrequency;
    private float randomAmplitude;

    private static Sound explosionSound;
    private static Texture healthBarTexture; // Texture statique pour économiser la mémoire

    public Enemy(float x, float y, float speed, int health, Texture texture, Actor player) {
        super(x, y, speed, texture);
        this.health = health;
        this.maxHealth = health;
        this.player = player;

        // Initialiser le son (chargé une seule fois via Gdx ou Manager)
        if (explosionSound == null) {
            explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        }
        if (healthBarTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            healthBarTexture = new Texture(pixmap);
            pixmap.dispose();
        }

        this.randomPhase = MathUtils.random(0f, 6.28f);
        this.randomFrequency = MathUtils.random(3f, 7f);
        this.randomAmplitude = MathUtils.random(100f, 250f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        zigzagTimer += delta;

        // IA : Poursuite ou Zigzag
        float distance = (float) Math.sqrt(Math.pow(getX() - player.getX(), 2) + Math.pow(getY() - player.getY(), 2));
        if (distance < 600f) isHoming = true;

        if (isHoming) {
            float dx = player.getX() - getX();
            float dy = player.getY() - getY();
            float angle = (float) Math.atan2(dy, dx);
            setX(getX() + MathUtils.cos(angle) * speed * 1.5f * delta);
            setY(getY() + MathUtils.sin(angle) * speed * 1.5f * delta);
        } else {
            setY(getY() + (speed * directionY * delta));
            float vx = (directionX * speed * 1.2f) + MathUtils.sin(zigzagTimer * randomFrequency + randomPhase) * randomAmplitude;
            setX(getX() + vx * delta);
        }

        // Rebondir sur les bords de l'écran
        if (getX() <= 0) { setX(10); directionX = 1; zigzagTimer = 0; }
        else if (getX() + getWidth() >= 1280) { setX(1280 - getWidth() - 10); directionX = -2; zigzagTimer = 0; }

        if (getY() <= 0) { setY(10); directionY = 1; }
        else if (getY() + getHeight() >= 1024) { setY(1024 - getHeight() - 10); directionY = -2; }

        updateBounds(0.7f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Dessiner la barre de vie si c'est un Boss
        if (isBoss) {
            float barWidth = getWidth();
            float barHeight = 10;
            float barY = getY() + getHeight() + 5;

            // Fond rouge
            batch.setColor(Color.RED);
            batch.draw(healthBarTexture, getX(), barY, barWidth, barHeight);

            // Barre verte (santé actuelle)
            float currentPercent = Math.max(0, (float) health / (float) maxHealth);
            batch.setColor(Color.GREEN);
            batch.draw(healthBarTexture, getX(), barY, barWidth * currentPercent, barHeight);

            // Restaurer la couleur blanche
            batch.setColor(Color.WHITE);
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            if (explosionSound != null) explosionSound.play(0.7f);

            // CORRECTION : Ajouter l'effet d'explosion AVANT de supprimer l'entité
            if (getStage() != null) {
                Explosion exp = new Explosion(getX(), getY(), getWidth(), getHeight());
                getStage().addActor(exp);
            }

            this.remove();
        }
    }

    public boolean isBoss() { return isBoss; }
    public void setBoss(boolean boss) { this.isBoss = boss; }
    public int getHealth() { return health; }
}
