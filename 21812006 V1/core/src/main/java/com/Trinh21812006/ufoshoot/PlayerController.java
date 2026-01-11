package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PlayerController {
    private MyActor player;

    public PlayerController(MyActor player) {
        this.player = player;
    }

    // Mettre à jour le joueur lors de la réinitialisation du jeu
    public void setPlayer(MyActor player) {
        this.player = player;
    }

    public void update(float delta) {
        if (player == null) return;

        // Gestion des entrées (Input) : Déléguer les commandes à MyActor
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.rotateLeft(delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.rotateRight(delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.moveForward(delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.moveBackward(delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            player.shoot();
        }
    }
}
