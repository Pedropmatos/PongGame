package com.pingpong.pong.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Adicione este import
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.logic.BallFactory;

public abstract class Level {
    public abstract BallFactory getBallFactory();
    public abstract float getInitialBallSpeed();
    public abstract float getPaddleSpeed();
    public abstract int getMaxScore();
    public abstract Texture getBackground();
    public abstract void update(float delta, Ball ball, Array<Rectangle> fallingLeaves);

    // MÉTODO ATUALIZADO para receber o SpriteBatch
    public abstract void render(ShapeRenderer shapeRenderer, SpriteBatch batch, Array<Rectangle> fallingLeaves);

    public abstract void dispose();
}
