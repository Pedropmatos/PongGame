package com.pingpong.pong.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.logic.BallFactory;
import com.pingpong.pong.logic.FastBallFactory;

public class SpaceLevel extends Level {

    private Texture background;

    public SpaceLevel() {
        // CORREÇÃO: O caminho agora está correto, sem o prefixo "assets/"
        this.background = new Texture(Gdx.files.internal("space_background.jpg"));
    }

    @Override
    public BallFactory getBallFactory() {
        return new FastBallFactory();
    }

    @Override
    public float getInitialBallSpeed() {
        return 400f;
    }

    @Override
    public float getPaddleSpeed() {
        return 400f;
    }

    @Override
    public int getMaxScore() {
        return 10;
    }

    @Override
    public Texture getBackground() {
        return background;
    }

    @Override
    public void update(float delta, Ball ball, Array<Rectangle> fallingLeaves) {
        // Nenhuma lógica de atualização específica para este nível
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, Array<Rectangle> fallingLeaves) {
        // Nenhuma lógica de renderização específica para este nível
    }
    
    @Override
    public void dispose() {
        if (background != null) {
            background.dispose();
        }
    }
}