package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Bullet extends Actor {
    private TextureRegion textureRegion;
    private float speed = 500f; // Tốc độ đạn bay
    private float vx, vy; // Vận tốc theo trục X và Y
    private Rectangle bounds;

    public Bullet(float x, float y, float rotation, Texture texture) {
        this.textureRegion = new TextureRegion(texture);

        // Đặt viên đạn ở mũi tàu
        setPosition(x, y);
        setRotation(rotation);
        setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);

        // SỬ DỤNG LẠI CÔNG THỨC LƯỢNG GIÁC:
        // Đạn bay theo hướng tàu đang chỉ (+90 độ để khớp mũi tàu)
        float angleRad = (float) Math.toRadians(rotation + 90);
        vx = (float) Math.cos(angleRad) * speed;
        vy = (float) Math.sin(angleRad) * speed;

        this.bounds = new com.badlogic.gdx.math.Rectangle(x, y, getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setX(getX() + vx * delta);
        setY(getY() + vy * delta);

        // KIỂM TRA BIÊN CHÍNH XÁC
        // 1. Biên trái: Toàn bộ chiều rộng đạn đã ra khỏi x=0
        if (getStage() != null) { // Kiểm tra an toàn để tránh lỗi Null
            float stageWidth = getStage().getWidth();
            float stageHeight = getStage().getHeight();

            // 1. Biên trái
            if (getX() + getWidth() < 0) {
                this.remove();
            }
            // 2. Biên phải: Thay 1280 bằng stageWidth
            else if (getX() > stageWidth) {
                this.remove();
            }
            // 3. Biên dưới
            else if (getY() + getHeight() < 0) {
                this.remove();
            }
            // 4. Biên trên: Thay 1024 bằng stageHeight
            else if (getY() > stageHeight) {
                this.remove();
            }
        }
        bounds.setPosition(getX(), getY());
    }

    public com.badlogic.gdx.math.Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), 1, 1, getRotation());
    }
}
