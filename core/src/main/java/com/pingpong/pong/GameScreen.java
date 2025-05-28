package com.pingpong.pong.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pingpong.pong.entities.Paddle;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.utils.Constants;

public abstract class GameScreen implements Screen {
    ShapeRenderer shapeRenderer;
    Paddle player;
    Ball ball;

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        player = new Paddle(30, Constants.SCREEN_HEIGHT / 2f - 50, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);
        ball = new Ball(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, Constants.BALL_RADIUS);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.render(shapeRenderer);
        ball.render(shapeRenderer);
        shapeRenderer.end();
    }

    public void update(float delta) {
        boolean up = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W);
        boolean down = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S);
        player.update(delta, up, down);
        ball.update(delta);
    }

}
