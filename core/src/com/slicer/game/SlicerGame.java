package com.slicer.game;

import com.badlogic.gdx.Game;
import com.slicer.screens.GameScreen;
import com.slicer.screens.MainMenuScreen;
import com.slicer.utils.AssetLoader;

public class SlicerGame extends Game {


    @Override
    public void create() {
        AssetLoader.load();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
    }
}