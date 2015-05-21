package com.slicer.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.slicer.models.Ball;
import com.slicer.screens.GameScreen;
import com.slicer.utils.AssetLoader;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

public class GameRenderer {

    //Box2D
    private World box2dWorld;
    private Box2DDebugRenderer box2DDebugRenderer;
    //Game World
    private GameWorld gameWorld;
    private GameScreen screen;
    //For Render
    private Sprite bg;
    private BitmapFont font;
    private GlyphLayout layout;
    private SpriteBatch guiBatch;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    public GameRenderer(GameWorld gameWorld, GameScreen screen) {
        this.gameWorld = gameWorld;
        this.screen = screen;

        box2dWorld = gameWorld.getBox2dWorld();

        camera = new OrthographicCamera(Gdx.graphics.getWidth() / 25, Gdx.graphics.getHeight() / 25);

        font = new BitmapFont();
        guiBatch = new SpriteBatch();
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        box2DDebugRenderer = new Box2DDebugRenderer();

        bg = new Sprite(AssetLoader.bgGame);
        layout = new GlyphLayout();
        font.getData().setScale((float) (8 / 6.) * Gdx.graphics.getDensity());
    }

    public void render(float delta) {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        box2dWorld.step(delta, 8, 3);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.disableBlending();
        bg.draw(spriteBatch);
        bg.setSize(Gdx.graphics.getWidth() / 25, Gdx.graphics.getHeight() / 25);
        bg.setCenter(0, 0);
        spriteBatch.end();

        //for ball
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.MAROON);
        Ball b = gameWorld.getBall();
        float x = Gdx.graphics.getWidth() / 2 + b.getPosition().x * 25;
        float y = Gdx.graphics.getHeight() / 2 + b.getPosition().y * 25;
        float radius = Ball.getRadius() * 25;
        shapeRenderer.circle(x, y, radius);
        shapeRenderer.end();

        float[] f = new float[gameWorld.getField().getVertices().length * 2];
        int i = 0;
        for (Vector2 a : gameWorld.getField().getVertices()) {
            f[i++] = Gdx.graphics.getWidth() / 2 + a.x * 25;
            f[i++] = Gdx.graphics.getHeight() / 2 + a.y * 25;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        Vector2[] vecs = gameWorld.getShave().getPositions();
        float x1 = Gdx.graphics.getWidth() / 2 + vecs[0].x * 25;
        float y1 = Gdx.graphics.getHeight() / 2 + vecs[0].y * 25;
        float x2 = Gdx.graphics.getWidth() / 2 + vecs[1].x * 25;
        float y2 = Gdx.graphics.getHeight() / 2 + vecs[1].y * 25;
        shapeRenderer.line(x1, y1, x2, y2);
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        if (!gameWorld.getField().isSliced())
            shapeRenderer.polygon(f);
        else
            box2DDebugRenderer.render(box2dWorld, camera.combined);
        shapeRenderer.end();

        guiBatch.begin();
        String text = "Time Remaining : " + Integer.toString((int) gameWorld.getCurrentTime());
        text += "    Level : " + Integer.toString(gameWorld.getLevelNum());
        text += "    Cutting Area : " + "% " + (int) Math.ceil(screen.getScoreOfLevel());
        layout.setText(font, text);
        float fcx = Gdx.graphics.getWidth() / 2 - layout.width / 2;
        float fcy = Gdx.graphics.getHeight() - layout.height / 2;
        font.draw(guiBatch, layout, fcx, fcy);
        guiBatch.end();

    }

    public void dispose() {
        font.dispose();
        box2dWorld.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
        box2DDebugRenderer.dispose();
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}