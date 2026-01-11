package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class GameEntity extends Actor {
    protected TextureRegion region;
    protected Rectangle bounds;
    protected float speed;

    public GameEntity(float x, float y, float speed, Texture texture) {
        this.speed = speed;
        if (texture != null) {
            this.region = new TextureRegion(texture);
            setSize(this.region.getRegionWidth(), this.region.getRegionHeight());
            setOrigin(getWidth() / 2, getHeight() / 2);
        }
        setPosition(x, y);
        this.bounds = new Rectangle(x, y, getWidth(), getHeight());
    }

    protected void updateBounds(float scalePercentage) {
        float w = getWidth() * scalePercentage;
        float h = getHeight() * scalePercentage;
        bounds.set(getX() + (getWidth() - w) / 2, getY() + (getHeight() - h) / 2, w, h);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (region != null) {
            batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }
}
