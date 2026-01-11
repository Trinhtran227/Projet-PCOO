package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MyActor extends Actor {
    TextureRegion textureRegion;
    private float speed;
    private float fireRate;
    private float fireTimer = 0;
    private Texture bulletTexture;
    private Rectangle bounds;
    private Sound shootSound;

    public MyActor(float x, float y, float speed, float fireRate, Stage s) {
        textureRegion = new TextureRegion(new Texture("images/playerShip3_red.png"));
        bulletTexture = new Texture("images/laserBlue01.png");
        this.speed = speed;
        this.fireRate = fireRate;
        setPosition(x, y);
        s.addActor(this);
        setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        setOrigin(getWidth()/2, getHeight()/2);
        shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_laser1.ogg"));

        float hbW = getWidth() * 0.6f;
        float hbH = getHeight() * 0.6f;
        this.bounds = new Rectangle(x + (getWidth()-hbW)/2, y + (getHeight()-hbH)/2, hbW, hbH);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), 1, 1, getRotation());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        fireTimer += delta;

        // Giới hạn biên màn hình (Logic View)
        if (getX() < 0) setX(0);
        else if (getX() + getWidth() > 1280) setX(1280 - getWidth());
        if (getY() < 0) setY(0);
        else if (getY() + getHeight() > 1024) setY(1024 - getHeight());

        // Cập nhật hitbox
        float hbW = getWidth() * 0.6f;
        float hbH = getHeight() * 0.6f;
        bounds.set(getX() + (getWidth()-hbW)/2, getY() + (getHeight()-hbH)/2, hbW, hbH);
    }

    // --- CÁC HÀM HÀNH ĐỘNG (Controller sẽ gọi các hàm này) ---

    public void rotateLeft(float delta) {
        setRotation(getRotation() + 200f * delta);
    }

    public void rotateRight(float delta) {
        setRotation(getRotation() - 200f * delta);
    }

    public void moveForward(float delta) {
        // Cộng 90 độ vì ảnh gốc mũi tàu hướng lên trên
        float angleRad = (float) Math.toRadians(getRotation() + 90);
        setX(getX() + (float) Math.cos(angleRad) * speed * delta);
        setY(getY() + (float) Math.sin(angleRad) * speed * delta);
    }

    public void moveBackward(float delta) {
        float angleRad = (float) Math.toRadians(getRotation() + 90);
        // Trừ đi để đi lùi
        setX(getX() - (float) Math.cos(angleRad) * speed * delta);
        setY(getY() - (float) Math.sin(angleRad) * speed * delta);
    }

    public void shoot() {
        if (fireTimer >= fireRate) {
            float centerX = getX() + getWidth() / 2;
            float centerY = getY() + getHeight() / 2;
            float bulletX = centerX - (bulletTexture.getWidth() / 2f);
            float bulletY = centerY - (bulletTexture.getHeight() / 2f);
            Bullet b = new Bullet(bulletX, bulletY, getRotation(), bulletTexture);
            getStage().addActor(b);

            if (shootSound != null) shootSound.play(0.5f);
            fireTimer = 0;
        }
    }

    public Rectangle getBounds() { return bounds; }

    @Override
    public boolean remove() {
        if (shootSound != null) shootSound.dispose();
        return super.remove();
    }
}
