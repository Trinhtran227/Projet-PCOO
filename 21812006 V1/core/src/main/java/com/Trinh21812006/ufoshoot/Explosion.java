package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Explosion extends Actor {
    private Animation<TextureRegion> animation;
    private float stateTime = 0;

    public Explosion(float x, float y, float width, float height) {
        // Sử dụng đúng tên file bạn đã gửi
        Texture sheet = new Texture(Gdx.files.internal("images/explosion.jpg"));

        int FRAME_COLS = 4;
        int FRAME_ROWS = 5;

        int tileWidth = sheet.getWidth() / FRAME_COLS;
        int tileHeight = sheet.getHeight() / FRAME_ROWS;

        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;

        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                // KỸ THUẬT QUAN TRỌNG: Thụt lùi vào 1 pixel ở mọi cạnh để xóa viền trắng
                frames[index++] = new TextureRegion(sheet,
                    j * tileWidth + 1,
                    i * tileHeight + 1,
                    tileWidth - 2,
                    tileHeight - 2);
            }
        }

        animation = new Animation<>(0.02f, frames);
        setPosition(x, y);
        setSize(width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        if (animation.isAnimationFinished(stateTime)) {
            this.remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime);

        // BẬT CHẾ ĐỘ TRỘN MÀU (Làm biến mất màu đen/trắng caro, giữ lại màu lửa rực rỡ)
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());

        // Trả lại chế độ trộn mặc định để các hình ảnh khác không bị lỗi hiển thị
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
}
