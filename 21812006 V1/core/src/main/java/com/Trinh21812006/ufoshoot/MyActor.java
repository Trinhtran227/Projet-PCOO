package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private Rectangle bounds; // Khai báo khung va chạm
    private Sound shootSound; // Biến lưu âm thanh bắn

    MyActor(float x, float y, float speed, float fireRate, Stage s) {
        textureRegion = new TextureRegion(new Texture("images/playerShip3_red.png"));
        bulletTexture = new Texture("images/laserBlue01.png");
        this.speed = speed;
        this.fireRate = fireRate;
        setPosition(x, y);
        s.addActor(this);
        setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        setOrigin(getWidth()/2, getHeight()/2);
        shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_laser1.ogg"));

        // Khởi tạo hitbox nhỏ (60%) nằm giữa tàu
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

        float rotateSpeed = 200f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            setRotation(getRotation() + rotateSpeed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            setRotation(getRotation() - rotateSpeed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            float direction = Gdx.input.isKeyPressed(Input.Keys.UP) ? 1 : -1;
            float angleRad = (float) Math.toRadians(getRotation() + 90);
            setX(getX() + (float) Math.cos(angleRad) * speed * direction * delta);
            setY(getY() + (float) Math.sin(angleRad) * speed * direction * delta);
        }

        // Giới hạn biên màn hình
        if (getX() < 0) setX(0);
        else if (getX() + getWidth() > 1280) setX(1280 - getWidth());
        if (getY() < 0) setY(0);
        else if (getY() + getHeight() > 1024) setY(1024 - getHeight());

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && fireTimer >= fireRate) {
            shoot();
            fireTimer = 0;
        }

        // Cập nhật hitbox luôn đi theo tâm tàu
        float hbW = getWidth() * 0.6f;
        float hbH = getHeight() * 0.6f;
        bounds.set(getX() + (getWidth()-hbW)/2, getY() + (getHeight()-hbH)/2, hbW, hbH);
    }

    private void shoot() {
        float centerX = getX() + getWidth() / 2;
        float centerY = getY() + getHeight() / 2;
        float bulletX = centerX - (bulletTexture.getWidth() / 2f);
        float bulletY = centerY - (bulletTexture.getHeight() / 2f);
        Bullet b = new Bullet(bulletX, bulletY, getRotation(), bulletTexture);
        getStage().addActor(b);

        // Phát âm thanh bắn
        if (shootSound != null) {
            shootSound.play(0.5f); // Âm lượng 50%
        }
    }

    public Rectangle getBounds() { return bounds; }

    @Override
    public boolean remove() {
        if (shootSound != null) {
            shootSound.dispose();
        }
        return super.remove();
    }
}
