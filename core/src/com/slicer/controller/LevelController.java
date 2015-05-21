package com.slicer.controller;

import com.badlogic.gdx.Gdx;
import com.slicer.utils.AssetLoader;
import com.slicer.utils.BodyEditorLoader;
import com.slicer.world.GameWorld;

public class LevelController {
    //Game World
    private GameWorld gameWorld;
    //Utils
    private int numOfLevels;
    private int currentLevel = 0;
    //Level Reader
    private BodyEditorLoader bodyEditorLoader;

    public LevelController(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        bodyEditorLoader = new BodyEditorLoader(AssetLoader.levelsFile);
        numOfLevels = bodyEditorLoader.getLevelSize();
    }

    public void restartCurrentLevel() {
        reset();
        gameWorld.setGameState(GameWorld.GameState.RUNNING);
    }

    public void nextLevel() {
        if (currentLevel == numOfLevels) {
            gameWorld.setGameState(GameWorld.GameState.FINISH);
        } else {
            currentLevel++;
            gameWorld.setGameState(GameWorld.GameState.RUNNING);
            gameWorld.getField().createBody(bodyEditorLoader.
                    getVertices(Integer.toString(currentLevel), Gdx.graphics.getDensity() * 8.5f + 15));
            reset();
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    private void reset() {
        gameWorld.getBall().reset();
        gameWorld.getShave().reset();
        gameWorld.getField().reset();
    }
}
