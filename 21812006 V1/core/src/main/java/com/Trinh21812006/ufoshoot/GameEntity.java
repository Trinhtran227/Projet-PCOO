package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class GameEntity extends Actor {
    protected TextureRegion region; // Texture à afficher
    protected Rectangle bounds;     // Zone de collision (Hitbox)
    protected float speed;

    // Constructeur commun pour toutes les entités du jeu
    public GameEntity(float x, float y, float speed, Texture texture) {
        this.speed = speed;
        if (texture != null) {
            this.region = new TextureRegion(texture);
            setSize(this.region.getRegionWidth(), this.region.getRegionHeight());
            setOrigin(getWidth() / 2, getHeight() / 2); // Toujours pivoter autour du centre
        }
        setPosition(x, y);
        this.bounds = new Rectangle(x, y, getWidth(), getHeight());
    }

    // Mettre à jour la hitbox selon un pourcentage (ex: 0.7f = 70%)
    // Permet aux sous-classes d'éviter de réécrire les calculs
    protected void updateBounds(float scalePercentage) {
        float w = getWidth() * scalePercentage;
        float h = getHeight() * scalePercentage;
        // Centrer la hitbox sur l'image
        bounds.set(getX() + (getWidth() - w) / 2, getY() + (getHeight() - h) / 2, w, h);
    }

    // Retourne la hitbox pour la vérification des collisions
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Logique de dessin de base : dessiner si la texture existe
        if (region != null) {
            batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }
}
