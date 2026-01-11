package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MyActor extends GameEntity {
    private float fireRate;
    private float fireTimer = 0;
    private Texture bulletTexture;
    private Sound shootSound;

    public MyActor(float x, float y, float speed, float fireRate, Stage s) {
        // Lấy ảnh từ ResourceManager
        super(x, y, speed, ResourceManager.getInstance().getTexture(Constants.IMG_PLAYER));

        this.fireRate = fireRate;
        // Lấy ảnh đạn và âm thanh từ ResourceManager
        this.bulletTexture = ResourceManager.getInstance().getTexture(Constants.IMG_BULLET);
        this.shootSound = ResourceManager.getInstance().getSound(Constants.SOUND_SHOOT);

        s.addActor(this);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        fireTimer += delta;

        if (getX() < 0) setX(0);
        else if (getX() + getWidth() > Constants.SCREEN_WIDTH) setX(Constants.SCREEN_WIDTH - getWidth());
        if (getY() < 0) setY(0);
        else if (getY() + getHeight() > Constants.SCREEN_HEIGHT) setY(Constants.SCREEN_HEIGHT - getHeight());

        updateBounds(0.6f);
    }

    public void rotateLeft(float delta) { setRotation(getRotation() + 200f * delta); }
    public void rotateRight(float delta) { setRotation(getRotation() - 200f * delta); }

    public void moveForward(float delta) {
        float angleRad = MathUtils.degreesToRadians * (getRotation() + 90);
        setX(getX() + MathUtils.cos(angleRad) * speed * delta);
        setY(getY() + MathUtils.sin(angleRad) * speed * delta);
    }

    public void moveBackward(float delta) {
        float angleRad = MathUtils.degreesToRadians * (getRotation() + 90);
        setX(getX() - MathUtils.cos(angleRad) * speed * delta);
        setY(getY() - MathUtils.sin(angleRad) * speed * delta);
    }

    public void shoot() {
        if (fireTimer >= fireRate) {
            float centerX = getX() + getWidth() / 2;
            float centerY = getY() + getHeight() / 2;
            Bullet b = new Bullet(centerX - bulletTexture.getWidth()/2f, centerY - bulletTexture.getHeight()/2f, getRotation(), bulletTexture);
            getStage().addActor(b);

            if (shootSound != null) shootSound.play(0.5f);
            fireTimer = 0;
        }
    }

    @Override
    public boolean remove() {
        // Không dispose sound ở đây nữa vì ResourceManager quản lý rồi!
        return super.remove();
    }
}
