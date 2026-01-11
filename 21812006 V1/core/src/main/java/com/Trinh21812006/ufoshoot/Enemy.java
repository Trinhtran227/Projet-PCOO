package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Enemy extends Actor {
    private TextureRegion textureRegion;
    private float speed;
    private int health;
    private Rectangle bounds;

    private Actor player;
    private float zigzagTimer = 0;
    private boolean isHoming = false;
    private int directionX = 1;
    private static Sound explosionSound;
    // Trong file Enemy.java
    private boolean isBoss = false; // Mặc định là quái thường
    private float randomPhase;      // Độ lệch pha ban đầu
    private float randomFrequency;  // Tốc độ uốn lượn
    private float randomAmplitude;  // Độ rộng của cú zigzag

    public Enemy(float x, float y, float speed, int health, Texture texture, Actor player) {
        this.textureRegion = new TextureRegion(texture);
        this.speed = speed;
        this.health = health;
        this.player = player;

        setPosition(x, y);
        setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());

        // Khởi tạo hitbox nhỏ hơn (70% kích thước ảnh) và căn giữa
        float hbW = getWidth() * 0.7f;
        float hbH = getHeight() * 0.7f;
        this.bounds = new Rectangle(x + (getWidth()-hbW)/2, y + (getHeight()-hbH)/2, hbW, hbH);

        // Nạp file mp3 từ thư mục sounds
        if (explosionSound == null) {
            explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        }

        this.randomPhase = MathUtils.random(0f, 6.28f); // Ngẫu nhiên từ 0 đến 2pi
        this.randomFrequency = MathUtils.random(3f, 7f); // Độ nhanh chậm của zigzag
        this.randomAmplitude = MathUtils.random(100f, 250f); // Độ rộng của zigzag
    }

    // Thêm biến hướng Y ở đầu class Enemy (cạnh directionX)
    private int directionY = -1; // -1 là đi xuống, 1 là đi lên

    @Override
    public void act(float delta) {
        super.act(delta);
        zigzagTimer += delta;

        float distance = (float) Math.sqrt(Math.pow(getX() - player.getX(), 2) + Math.pow(getY() - player.getY(), 2));

        if (distance < 600f) isHoming = true;

        if (isHoming) {
            // ĐUỔI THEO PLAYER (Giữ nguyên vì lượng giác tự xử lý hướng)
            float dx = player.getX() - getX();
            float dy = player.getY() - getY();
            float angle = (float) Math.atan2(dy, dx);
            setX(getX() + MathUtils.cos(angle) * speed * 1.5f * delta);
            setY(getY() + MathUtils.sin(angle) * speed * 1.5f * delta);
        } else {
            // DI CHUYỂN ZIGZAG CÓ HƯỚNG Y
            // Thay vì luôn trừ speed, ta nhân với directionY
            setY(getY() + (speed * directionY * delta));
            float vx = (directionX * speed * 1.2f) + MathUtils.sin(zigzagTimer * randomFrequency + randomPhase) * randomAmplitude;
            setX(getX() + vx * delta);
//            float vx = (directionX * speed * 1.2f) + (MathUtils.sin(zigzagTimer * 5f) * 150f);
//            setX(getX() + vx * delta);
        }

        // --- LOGIC BẬT BIÊN CỰC MẠNH CHO CẢ 2 TRỤC ---

        // 1. Bật biên Ngang (X)
        if (getX() <= 0) {
            setX(10); // Đẩy mạnh ra 10px để thoát dính
            directionX = 1;
            zigzagTimer = 0;
        } else if (getX() + getWidth() >= 1280) {
            setX(1280 - getWidth() - 10);
            directionX = -1;
            zigzagTimer = 0;
        }

        // 2. Bật biên Dọc (Y) - GIẢI PHÁP CHO VẤN ĐỀ CỦA BẠN
        if (getY() <= 0) {
            setY(10); // Đẩy ngược lên trên 10px
            directionY = 1; // Đổi hướng thành đi lên
        } else if (getY() + getHeight() >= 1024) {
            setY(1024 - getHeight() - 10); // Đẩy xuống dưới
            directionY = -1; // Đổi hướng thành đi xuống
        }

        // Cập nhật hitbox
        float hbW = getWidth() * 0.7f;
        float hbH = getHeight() * 0.7f;
        bounds.set(getX() + (getWidth()-hbW)/2, getY() + (getHeight()-hbH)/2, hbW, hbH);
    }

    public Rectangle getBounds() { return bounds; }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textureRegion, getX(), getY(), getWidth(), getHeight());
    }

    // Trong file Enemy.java
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            // 1. Phát âm thanh nổ mp3 đã cài ở bước trước
            if (explosionSound != null) explosionSound.play(0.7f);

            // 2. Tạo hiệu ứng hình ảnh nổ
            Explosion exp = new Explosion(getX(), getY(), getWidth(), getHeight());
            if (getStage() != null) {
                getStage().addActor(exp);
            }

            // 3. Xóa UFO khỏi màn hình
            this.remove();
        }
    }



    // Thêm phương thức này để GameMapManager có thể gọi được
    public boolean isBoss() {
        return isBoss;
    }
    // Trong file Enemy.java, thêm phương thức này vào cuối class
    public int getHealth() {
        return health;
    }

    // Thêm phương thức này để thiết lập trạng thái Boss khi sinh ra
    public void setBoss(boolean boss) {
        this.isBoss = boss;
    }
}
