package com.slicer.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class AssetLoader {

    public static Texture bgGame, bgGameOver, bgFinish, score, soundOn, soundOff;
    public static Sound gameOver, victory, slice, click;
    public static FileHandle levelsFile;

    public static boolean sound = true;

    public static void load() {

        bgGame = new Texture(Gdx.files.internal("data/images/bg1.jpeg"));
        bgGameOver = new Texture(Gdx.files.internal("data/images/bg3.png"));
        bgFinish = new Texture(Gdx.files.internal("data/images/bg2.png"));
        soundOn = new Texture(Gdx.files.internal("data/images/on.png"));
        soundOff = new Texture(Gdx.files.internal("data/images/off.png"));

        score = new Texture(Gdx.files.internal("data/images/score.png"));

        gameOver = Gdx.audio.newSound(Gdx.files.internal("data/sound/gameOver.wav"));
        victory = Gdx.audio.newSound(Gdx.files.internal("data/sound/victory.wav"));
        slice = Gdx.audio.newSound(Gdx.files.internal("data/sound/slice.wav"));
        click = Gdx.audio.newSound(Gdx.files.internal("data/sound/click.wav"));

        levelsFile = Gdx.files.internal("data/levels/levels.json");
    }

    public static void play(Sound s) {
        if (sound)
            s.play();
    }

    public static void dispose() {
        bgGame.dispose();
        bgGameOver.dispose();
        bgFinish.dispose();

        gameOver.dispose();
        victory.dispose();
        slice.dispose();
    }

}