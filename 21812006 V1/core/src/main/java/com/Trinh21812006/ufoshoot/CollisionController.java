package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class CollisionController {
    private MainGame game;
    private GameModel model;

    public CollisionController(MainGame game, GameModel model) {
        this.game = game;
        this.model = model;
    }

    public void update(Stage stage, float delta) {
        MyActor playerActor = null;
        for (Actor a : stage.getActors()) {
            if (a instanceof MyActor) {
                playerActor = (MyActor) a;
                break;
            }
        }

        for (int i = 0; i < stage.getActors().size; i++) {
            Actor a = stage.getActors().get(i);

            if (a instanceof Bullet) {
                handleBulletCollision((Bullet) a, stage);
            }

            if (a instanceof Enemy) {
                handlePlayerCollision((Enemy) a, playerActor, stage);
            }
        }
    }

    private void handleBulletCollision(Bullet bullet, Stage stage) {
        for (Actor other : stage.getActors()) {
            if (other instanceof Enemy) {
                Enemy enemy = (Enemy) other;
                if (bullet.getBounds().overlaps(enemy.getBounds())) {
                    enemy.takeDamage(1);
                    bullet.remove();
                    if (enemy.getHealth() <= 0) {
                        // Cộng điểm vào Model
                        model.addScore(enemy.isBoss() ? 100 : 10);

                    }
                    break;
                }
            }
        }
    }

    private void handlePlayerCollision(Enemy enemy, MyActor player, Stage stage) {
        if (player != null && !model.isCollisionOccurred() && player.getBounds().overlaps(enemy.getBounds())) {
            stage.addActor(new Explosion(player.getX(), player.getY(), 120, 120));
            stage.addActor(new Explosion(enemy.getX(), enemy.getY(), 120, 120));

            player.setVisible(false);
            enemy.setVisible(false);

            // Cập nhật trạng thái vào Model
            model.setCollisionOccurred(true);
            game.triggerShake(); // Báo MainGame rung màn hình
        }
    }
}
