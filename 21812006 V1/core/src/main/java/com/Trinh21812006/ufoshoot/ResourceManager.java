package com.Trinh21812006.ufoshoot;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class ResourceManager implements Disposable {
    // Singleton : Assure qu'il n'y a qu'un seul gestionnaire de ressources dans le jeu
    private static ResourceManager instance;
    public final AssetManager manager = new AssetManager();

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    // Charger toutes les ressources en mémoire
    public void loadAll() {
        manager.load(Constants.IMG_PLAYER, Texture.class);
        manager.load(Constants.IMG_BULLET, Texture.class);
        manager.load(Constants.IMG_EXPLOSION, Texture.class);
        manager.load(Constants.IMG_UFO_RED, Texture.class);
        manager.load(Constants.IMG_UFO_BLUE, Texture.class);

        manager.load(Constants.SOUND_SHOOT, Sound.class);
        manager.load(Constants.SOUND_EXPLOSION, Sound.class);

        manager.finishLoading(); // Attendre la fin du chargement avant de commencer le jeu
    }

    // Méthodes pour récupérer les ressources
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
