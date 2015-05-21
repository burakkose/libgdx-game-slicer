package com.slicer.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.slicer.controller.GameInputController;
import com.slicer.controller.LevelController;
import com.slicer.game.SlicerGame;
import com.slicer.models.Ball;
import com.slicer.models.Field;
import com.slicer.models.Shave;
import com.slicer.screens.GameScreen;
import com.slicer.screens.SplashScreen;
import com.slicer.utils.AssetLoader;

public class GameWorld {

    private static final float GAME_TIME = 5F;

    //Game State
    public enum GameState {
        RUNNING, GAME_OVER, NEXT_LEVEL, FINISH
    }

    //Box2D
    private World box2dWorld;
    private BodyDef box2dBodyDef;
    private FixtureDef box2dFixtureDef;
    //Entities
    private Ball ball;
    private Field field;
    private Shave shave;
    //Level Controller
    private LevelController levelController;
    private GameState gameState;
    //Game
    private SlicerGame game;
    private GameScreen gameScreen;
    //Utils
    private double topScore = 0;
    private float currentTime = 5;
    private boolean timeFlag = false;

    public GameWorld(SlicerGame game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;

        box2dBodyDef = new BodyDef();
        box2dFixtureDef = new FixtureDef();
        box2dWorld = new World(new Vector2(0, 0), true);

        gameState = GameState.NEXT_LEVEL;

        shave = new Shave(box2dWorld, box2dBodyDef, box2dFixtureDef);
        field = new Field(box2dWorld, box2dBodyDef, box2dFixtureDef);
        ball = new Ball(box2dWorld, box2dBodyDef, box2dFixtureDef, this);

        levelController = new LevelController(this);
        update(0);
    }

    public void update(float delta) {

        if (!timeFlag) {
            currentTime -= delta;
            if (currentTime <= 0) {
                gameState = GameState.GAME_OVER;
                GameInputController.setFlag(true);
            }

        }

        switch (gameState) {
            case GAME_OVER:
                currentTime = 5;
                timeFlag = false;
                AssetLoader.play(AssetLoader.gameOver);
                game.setScreen(new SplashScreen(game, gameScreen, gameState));
                levelController.restartCurrentLevel();
                break;
            case NEXT_LEVEL:
                if (levelController.getCurrentLevel() != 0) {
                    AssetLoader.play(AssetLoader.victory);
                    double score = calculateScore();
                    topScore += score;
                    currentTime = 5;
                    timeFlag = false;
                    game.setScreen(new SplashScreen(game, gameScreen, gameState, score));
                }
                levelController.nextLevel();
                break;
        }

        if (gameState == GameState.FINISH) {
            AssetLoader.play(AssetLoader.victory);
            game.setScreen(new SplashScreen(game, gameScreen, gameState, topScore));
        }
    }

    public Ball getBall() {
        return ball;
    }

    public Field getField() {
        return field;

    }

    public Shave getShave() {
        return shave;

    }

    public World getBox2dWorld() {
        return box2dWorld;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getLevelNum() {
        return levelController.getCurrentLevel();
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setTimeFlag(boolean timeFlag) {
        this.timeFlag = timeFlag;
    }

    private double calculateScore() {
        return levelController.getCurrentLevel() * gameScreen.getScoreOfLevel() * 100. / (GAME_TIME - currentTime);
    }

}