package com.slicer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.slicer.game.SlicerGame;
import com.slicer.utils.AssetLoader;

public class MainMenuScreen extends InputAdapter implements Screen {

    private SlicerGame game;

    private Sprite bg;
    private Vector3 touchPoint;
    private Rectangle soundBounds;

    private BitmapFont font;
    private SpriteBatch batcher;

    private OrthographicCamera cam;

    private float fcx1, fcx2, fcy1, fcy2;
    private GlyphLayout layout1, layout2;

    public MainMenuScreen(SlicerGame game) {
        this.game = game;
        font = new BitmapFont();
        layout1 = new GlyphLayout();
        layout2 = new GlyphLayout();
        batcher = new SpriteBatch();

        cam = new OrthographicCamera(320, 480);
        cam.position.set(320 / 2, 480 / 2, 0);

        soundBounds = new Rectangle(0, 0, 80, 80);
        touchPoint = new Vector3();

        bg = new Sprite(AssetLoader.bgGame);

        font.getData().setScale((float) (8 / 6.) * Gdx.graphics.getDensity());

        layout1.setText(font, "Tap the screen to play");
        fcx1 = Gdx.graphics.getWidth() / 2 - layout1.width / 2;
        fcy1 = 3 * Gdx.graphics.getHeight() / 4 - layout1.height / 2;

        layout2.setText(font, "120202063 - Mert Esen" + "\n120202052 - Burak Köse");
        fcx2 = Gdx.graphics.getWidth() / 2 - layout2.width / 2;
        fcy2 = 0 + layout2.height * 1.5f;

        Gdx.input.setInputProcessor(this);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();

        batcher.begin();
        bg.draw(batcher);
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        bg.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        font.draw(batcher, layout1, fcx1, fcy1);
        font.draw(batcher, layout2, fcx2, fcy2);
        batcher.draw(AssetLoader.sound ? AssetLoader.soundOn : AssetLoader.soundOff, 0, 0, 64, 64);
        batcher.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        cam.unproject(touchPoint.set(screenX, screenY, 0));
        if (soundBounds.contains(touchPoint.x, touchPoint.y)) {
            AssetLoader.play(AssetLoader.click);
            AssetLoader.sound = !AssetLoader.sound;
        } else {
            game.setScreen(new GameScreen(game));
        }
        return true;
    }
}