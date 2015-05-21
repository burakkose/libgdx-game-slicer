package com.slicer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.slicer.controller.GameInputController;
import com.slicer.game.SlicerGame;
import com.slicer.world.GameRenderer;
import com.slicer.world.GameWorld;

public class GameScreen implements Screen {

    private GameRenderer gameRenderer;
    private GameWorld gameWorld;
    private OrthographicCamera cam;
    private GameInputController gameInputController;

    public GameScreen(SlicerGame game) {
        gameWorld = new GameWorld(game, this);
        gameRenderer = new GameRenderer(gameWorld, this);
        cam = gameRenderer.getCamera();
        gameInputController = new GameInputController(gameWorld, cam);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gameInputController);
        gameInputController.resetScore();
    }

    @Override
    public void render(float delta) {
        gameRenderer.getGameWorld().update(delta);
        gameRenderer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        OrthographicCamera cam = gameRenderer.getCamera();
        cam.viewportHeight = height / 25;
        cam.viewportWidth = width / 25;
        cam.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
    }

    public OrthographicCamera getCam() {
        return cam;
    }

    public float getScoreOfLevel() {
        return gameInputController.getScore();
    }
}