package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

// KẾ THỪA TỪ GameEntity
public class Bullet extends GameEntity {
    private float vx, vy;

    public Bullet(float x, float y, float rotation, Texture texture) {
        // Gọi constructor lớp cha (x, y, speed=500f, texture)
        // Tốc độ 500f lấy từ file Bullet cũ
        super(x, y, 500f, texture);

        setRotation(rotation);

        // Tính toán hướng bay dựa trên góc quay (Logic cũ giữ nguyên)
        float angleRad = MathUtils.degreesToRadians * (rotation + 90);
        this.vx = MathUtils.cos(angleRad) * speed;
        this.vy = MathUtils.sin(angleRad) * speed;
    }

    @Override
    public void act(float delta) {
        super.act(delta); // Để cha xử lý các logic cơ bản nếu có

        // Cập nhật vị trí
        setX(getX() + vx * delta);
        setY(getY() + vy * delta);

        // Kiểm tra ra khỏi màn hình thì xóa (Logic cũ)
        if (getStage() != null) {
            float sw = getStage().getWidth();
            float sh = getStage().getHeight();
            if (getX() < 0 || getX() > sw || getY() < 0 || getY() > sh) {
                remove();
            }
        }

        // Cập nhật hitbox (100% kích thước đạn)
        updateBounds(1.0f);
    }

    // Không cần hàm draw()
    // Không cần hàm getBounds()
}
