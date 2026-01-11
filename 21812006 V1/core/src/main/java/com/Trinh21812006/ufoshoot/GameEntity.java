package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class GameEntity extends Actor {
    protected TextureRegion region; // Hình ảnh hiển thị
    protected Rectangle bounds;     // Khung va chạm (Hitbox)
    protected float speed;

    // Constructor chung cho mọi thực thể trong game
    public GameEntity(float x, float y, float speed, Texture texture) {
        this.speed = speed;
        if (texture != null) {
            this.region = new TextureRegion(texture);
            setSize(this.region.getRegionWidth(), this.region.getRegionHeight());
            setOrigin(getWidth() / 2, getHeight() / 2); // Luôn xoay quanh tâm
        }

        setPosition(x, y);
        this.bounds = new Rectangle(x, y, getWidth(), getHeight());
    }

    // Hàm cập nhật hitbox theo tỷ lệ phần trăm (Ví dụ 0.7f = 70%)
    // Giúp code con không phải viết lại công thức tính toán
    protected void updateBounds(float scalePercentage) {
        float w = getWidth() * scalePercentage;
        float h = getHeight() * scalePercentage;
        // Căn giữa hitbox vào giữa hình ảnh
        bounds.set(getX() + (getWidth() - w) / 2, getY() + (getHeight() - h) / 2, w, h);
    }

    // Hàm trả về hitbox để CollisionController kiểm tra
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Logic vẽ cơ bản: Nếu có ảnh thì vẽ
        if (region != null) {
            batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }
}
