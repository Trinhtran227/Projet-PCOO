package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

// KẾ THỪA TỪ GameEntity
public class Enemy extends GameEntity {
    private int health;
    private int maxHealth; // Dùng để tính % thanh máu
    private Actor player;

    // Logic di chuyển riêng của Enemy
    private float zigzagTimer = 0;
    private boolean isHoming = false;
    private int directionX = 1;
    private int directionY = -1;

    private float randomPhase;
    private float randomFrequency;
    private float randomAmplitude;

    private boolean isBoss = false;
    private static Sound explosionSound;
    private static Texture healthBarTexture; // Texture static để tiết kiệm bộ nhớ

    public Enemy(float x, float y, float speed, int health, Texture texture, Actor player) {
        // Gọi Constructor lớp cha để khởi tạo ảnh, vị trí, bounds, speed
        super(x, y, speed, texture);

        this.health = health;
        this.maxHealth = health; // Lưu máu tối đa ban đầu
        this.player = player;

        // Khởi tạo âm thanh (Chỉ load 1 lần)
        if (explosionSound == null) {
            explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        }

        // Khởi tạo Texture trắng để vẽ thanh máu (Chỉ tạo 1 lần)
        if (healthBarTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            healthBarTexture = new Texture(pixmap);
            pixmap.dispose();
        }

        // Random chỉ số di chuyển zigzag
        this.randomPhase = MathUtils.random(0f, 6.28f);
        this.randomFrequency = MathUtils.random(3f, 7f);
        this.randomAmplitude = MathUtils.random(100f, 250f);
    }

    @Override
    public void act(float delta) {
        super.act(delta); // Gọi act của Actor
        zigzagTimer += delta;

        // Logic AI: Đuổi theo hoặc Zigzag
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

        // Logic Bật biên
        if (getX() <= 0) { setX(10); directionX = 1; zigzagTimer = 0; }
        else if (getX() + getWidth() >= 1280) { setX(1280 - getWidth() - 10); directionX = -1; zigzagTimer = 0; }

        if (getY() <= 0) { setY(10); directionY = 1; }
        else if (getY() + getHeight() >= 1024) { setY(1024 - getHeight() - 10); directionY = -1; }

        // QUAN TRỌNG: Gọi hàm cập nhật hitbox của cha (70%)
        updateBounds(0.7f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha); // Gọi lớp cha để vẽ tàu địch trước

        // Sau đó vẽ thêm thanh máu nếu là Boss
        if (isBoss) {
            float barWidth = getWidth();
            float barHeight = 10;
            float barY = getY() + getHeight() + 5;

            // Nền đỏ
            batch.setColor(Color.RED);
            batch.draw(healthBarTexture, getX(), barY, barWidth, barHeight);

            // Thanh xanh (Máu hiện tại)
            float currentPercent = Math.max(0, (float) health / (float) maxHealth);
            batch.setColor(Color.GREEN);
            batch.draw(healthBarTexture, getX(), barY, barWidth * currentPercent, barHeight);

            // Trả lại màu trắng
            batch.setColor(Color.WHITE);
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            // 1. Phát âm thanh
            if (explosionSound != null) {
                explosionSound.play(0.7f);
            }

            // 2. SỬA LỖI TẠI ĐÂY: Tạo và thêm hiệu ứng nổ vào Stage TRƯỚC khi xóa Enemy
            if (getStage() != null) {
                Explosion exp = new Explosion(getX(), getY(), getWidth(), getHeight());
                getStage().addActor(exp);
            }

            // 3. Sau đó mới xóa Enemy
            this.remove();
        }
    }

    // Getters & Setters
    public boolean isBoss() { return isBoss; }
    public void setBoss(boolean boss) { this.isBoss = boss; }
    public int getHealth() { return health; }
}
