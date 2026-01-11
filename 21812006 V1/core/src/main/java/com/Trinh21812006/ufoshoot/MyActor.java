package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;

// KẾ THỪA TỪ GameEntity
public class MyActor extends GameEntity {
    private float fireRate;
    private float fireTimer = 0;
    private Texture bulletTexture;
    private Sound shootSound;

    public MyActor(float x, float y, float speed, float fireRate, Stage s) {
        // Gọi constructor cha: nạp ảnh Player và thiết lập speed
        super(x, y, speed, new Texture("images/playerShip3_red.png"));

        this.fireRate = fireRate;
        this.bulletTexture = new Texture("images/laserBlue01.png");
        this.shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_laser1.ogg"));

        s.addActor(this); // Thêm chính mình vào stage
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        fireTimer += delta;

        // Giới hạn biên màn hình
        if (getX() < 0) setX(0);
        else if (getX() + getWidth() > 1280) setX(1280 - getWidth());
        if (getY() < 0) setY(0);
        else if (getY() + getHeight() > 1024) setY(1024 - getHeight());

        // Cập nhật hitbox (60% kích thước tàu)
        updateBounds(0.6f);
    }

    // --- CÁC HÀM HÀNH ĐỘNG CHO CONTROLLER GỌI ---

    public void rotateLeft(float delta) {
        setRotation(getRotation() + 200f * delta);
    }

    public void rotateRight(float delta) {
        setRotation(getRotation() - 200f * delta);
    }

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
            // Tính toán vị trí xuất phát của đạn
            float centerX = getX() + getWidth() / 2;
            float centerY = getY() + getHeight() / 2;
            float bulletX = centerX - (bulletTexture.getWidth() / 2f);
            float bulletY = centerY - (bulletTexture.getHeight() / 2f);

            // Tạo đạn
            Bullet b = new Bullet(bulletX, bulletY, getRotation(), bulletTexture);
            getStage().addActor(b);

            if (shootSound != null) shootSound.play(0.5f);
            fireTimer = 0;
        }
    }

    @Override
    public boolean remove() {
        if (shootSound != null) shootSound.dispose();
        // Không dispose texture ở đây nếu dùng chung (Resource Manager),
        // nhưng hiện tại bạn đang new Texture nên để hệ thống tự quản lý GC hoặc dispose ở MainGame
        return super.remove();
    }
}
