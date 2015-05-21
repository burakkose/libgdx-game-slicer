package com.slicer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.slicer.game.SlicerGame;
import com.slicer.utils.AssetLoader;
import com.slicer.world.GameWorld;

public class SplashScreen implements Screen {

    private SlicerGame game;
    private GameScreen gameScreen;

    private Sprite bg;
    private BitmapFont font;
    private GlyphLayout layout;
    private OrthographicCamera cam;
    private SpriteBatch spriteBatch;

    private GameWorld.GameState currentGameState;

    private double score;

    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();


    public SplashScreen(SlicerGame game, GameScreen gameScreen, GameWorld.GameState currentGameState) {
        this.game = game;
        this.cam = gameScreen.getCam();
        this.gameScreen = gameScreen;
        this.currentGameState = currentGameState;
        layout = new GlyphLayout();
    }

    public SplashScreen(SlicerGame game, GameScreen gameScreen, GameWorld.GameState gameState, double score) {
        this(game, gameScreen, gameState);
        this.score = score;
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.RED);
        font.getData().scale(2f);

        switch (currentGameState) {
            case GAME_OVER:
                bg = new Sprite(AssetLoader.bgGameOver);
                Gdx.input.setInputProcessor(null);
                break;
            case FINISH:
                bg = new Sprite(AssetLoader.bgFinish);
                h /= 4;
                break;
            case NEXT_LEVEL:
                bg = new Sprite(AssetLoader.score);
                h /= 4;
                break;
        }

        bg.setSize(w, h);
        bg.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        layout.setText(font, Integer.toString((int) score));
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        bg.draw(spriteBatch);
        if (currentGameState != GameWorld.GameState.GAME_OVER) {
            float fcx = Gdx.graphics.getWidth() / 2 - layout.width / 2;
            float fcy = h - layout.height / 2;
            font.draw(spriteBatch, layout, fcx, fcy);
        }
        spriteBatch.end();

        if (Gdx.input.justTouched()) {
            if (currentGameState == GameWorld.GameState.FINISH)
                Gdx.app.exit();
            game.setScreen(gameScreen);
        }

    }

    @Override
    public void resize(int width, int height) {
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
        font.dispose();
        spriteBatch.dispose();
    }
}
