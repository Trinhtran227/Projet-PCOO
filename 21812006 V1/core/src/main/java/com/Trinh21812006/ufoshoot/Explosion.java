package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

// Explosion nên kế thừa Actor vì nó không cần logic vật lý của GameEntity
public class Explosion extends Actor {
    private Animation<TextureRegion> animation;
    private float stateTime = 0;

    // Nếu bạn muốn tối ưu, hãy đưa texture này sang ResourceManager,
    // nhưng để code chạy ngay, tôi sẽ giữ loading trực tiếp ở đây.
    private Texture sheet;

    public Explosion(float x, float y, float width, float height) {
        // Đảm bảo đường dẫn ảnh đúng
        sheet = new Texture(Gdx.files.internal("images/explosion.jpg"));

        // Cấu hình lưới 4x5 theo ảnh của bạn
        int FRAME_COLS = 4;
        int FRAME_ROWS = 5;

        int tileWidth = sheet.getWidth() / FRAME_COLS;
        int tileHeight = sheet.getHeight() / FRAME_ROWS;

        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;

        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                // Kỹ thuật xóa viền trắng: +1 và -2
                frames[index++] = new TextureRegion(sheet,
                    j * tileWidth + 1, i * tileHeight + 1,
                    tileWidth - 2, tileHeight - 2);
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
        // Khi chạy hết hoạt ảnh thì tự xóa mình khỏi sân khấu
        if (animation.isAnimationFinished(stateTime)) {
            this.remove();
            // Giải phóng bộ nhớ ảnh khi nổ xong (Quan trọng nếu không dùng ResourceManager)
            if (sheet != null) sheet.dispose();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Lấy khung hình tương ứng với thời gian hiện tại
        TextureRegion currentFrame = animation.getKeyFrame(stateTime);

        // Chế độ hòa trộn màu (Blend Mode) giúp lửa trông sáng rực và xóa nền đen
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());

        // Trả về chế độ mặc định để không ảnh hưởng các vật thể khác
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
}
