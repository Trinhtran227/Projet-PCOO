package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class Bullet extends GameEntity {
    private float vx, vy;

    public Bullet(float x, float y, float rotation, Texture texture) {
        super(x, y, 500f, texture);
        setRotation(rotation);

        // Calculer la direction du vol en fonction de la rotation
        float angleRad = MathUtils.degreesToRadians * (rotation + 90);
        this.vx = MathUtils.cos(angleRad) * speed;
        this.vy = MathUtils.sin(angleRad) * speed;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Mettre à jour la position
        setX(getX() + vx * delta);
        setY(getY() + vy * delta);

        // Supprimer la balle si elle sort de l'écran
        if (getStage() != null) {
            float sw = getStage().getWidth();
            float sh = getStage().getHeight();
            if (getX() < 0 || getX() > sw || getY() < 0 || getY() > sh) {
                remove();
            }
        }
        updateBounds(1.0f);
    }
}
