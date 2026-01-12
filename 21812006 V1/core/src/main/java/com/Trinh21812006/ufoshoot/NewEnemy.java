package com.Trinh21812006.ufoshoot;

import com.Trinh21812006.ufoshoot.GameEntity;
import com.badlogic.gdx.graphics.Texture;

public class NewEnemy extends GameEntity {
    public NewEnemy(float x, float y, Texture texture) {
        super(x, y, 500f, texture); // Vitesse rapide de 500
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Logique simple : Fonce tout droit vers le bas
        setY(getY() - speed * delta);
        updateBounds(0.8f); // Ajuster la hitbox
    }
}
