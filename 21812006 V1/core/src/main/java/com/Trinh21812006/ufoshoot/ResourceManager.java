package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class ResourceManager implements Disposable {
    private static ResourceManager instance;
    public final AssetManager manager = new AssetManager();

    public static ResourceManager getInstance() {
        if (instance == null) instance = new ResourceManager();
        return instance;
    }

    public void loadAll() {
        // Nạp Player & Đạn
        manager.load(Constants.IMG_PLAYER, Texture.class);
        manager.load(Constants.IMG_BULLET, Texture.class);
        manager.load(Constants.IMG_EXPLOSION, Texture.class);

        // Nạp Địch (Thêm mới)
        manager.load(Constants.IMG_UFO_RED, Texture.class);
        manager.load(Constants.IMG_UFO_BLUE, Texture.class);

        // Nạp Âm thanh
        manager.load(Constants.SOUND_SHOOT, Sound.class);
        manager.load(Constants.SOUND_EXPLOSION, Sound.class);

        manager.finishLoading();
    }

    public Texture getTexture(String fileName) {
        return manager.get(fileName, Texture.class);
    }

    public Sound getSound(String fileName) {
        return manager.get(fileName, Sound.class);
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
