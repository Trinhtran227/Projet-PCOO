package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Explosion extends Actor {
    private Animation<TextureRegion> animation;
    private float stateTime = 0;

    // Không giữ biến Texture sheet riêng nữa

    public Explosion(float x, float y, float width, float height) {
        // Lấy Texture nổ đã nạp sẵn từ Manager
        Texture sheet = ResourceManager.getInstance().getTexture(Constants.IMG_EXPLOSION);

        int FRAME_COLS = 4;
        int FRAME_ROWS = 5;

        int tileWidth = sheet.getWidth() / FRAME_COLS;
        int tileHeight = sheet.getHeight() / FRAME_ROWS;

        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;

        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = new TextureRegion(sheet, j * tileWidth, i * tileHeight, tileWidth, tileHeight);
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
            // Không dispose texture ở đây!
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
        // Đây là phần bạn bị thiếu: Trả lại chế độ blend mặc định
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
}
