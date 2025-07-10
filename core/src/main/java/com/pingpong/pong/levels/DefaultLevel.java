package com.pingpong.pong.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.logic.BallFactory;
import com.pingpong.pong.logic.DefaultBallFactory;

public class DefaultLevel extends Level {

    @Override
    public BallFactory getBallFactory() {
        return new DefaultBallFactory();
    }

    @Override
    public float getPaddleSpeed() {
        return 300f;
    }

    @Override
    public int getMaxScore() {
        return 5;
    }

    @Override
    public Texture getBackground() {
        return null;
    }

    @Override
    public void update(float delta, Ball ball, Array<Rectangle> fallingLeaves) {
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, Array<Rectangle> fallingLeaves) {
    }

    @Override
    public void dispose() {
    }
}
