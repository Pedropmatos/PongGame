package com.pingpong.pong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.entities.Paddle;
import com.pingpong.pong.logic.BallFactory;
import com.pingpong.pong.logic.CollisionHandler;
import com.pingpong.pong.logic.DefaultBallFactory;
import com.pingpong.pong.logic.FastBallFactory;
import com.pingpong.pong.utils.Constants;

public class GameScreen implements Screen {
    ShapeRenderer shapeRenderer;
    Paddle player1;
    Paddle player2;
    Ball ball;
    SpriteBatch batch;
    BitmapFont font;
    BallFactory ballFactory;

    int scorePlayer1 = 0;
    int scorePlayer2 = 0;

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        player1 = new Paddle(30, Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);
        player2 = new Paddle(Constants.SCREEN_WIDTH - 30 - Constants.PADDLE_WIDTH,
            Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);

        this.ballFactory = new FastBallFactory();

        ball = ballFactory.createBall(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, Constants.BALL_RADIUS);

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int y = 0; y < Constants.SCREEN_HEIGHT; y += 30) {
            if (y % 60 == 0) {
                shapeRenderer.rectLine(Constants.SCREEN_WIDTH / 2f, y, Constants.SCREEN_WIDTH / 2f, y + 15, 2);
            }
        }

        shapeRenderer.rectLine(0, 0, 0, Constants.SCREEN_HEIGHT, 4);
        shapeRenderer.rectLine(Constants.SCREEN_WIDTH, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, 4);

        shapeRenderer.setColor(1, 0, 0, 1);
        player1.render(shapeRenderer);

        shapeRenderer.setColor(0, 0, 1, 1);
        player2.render(shapeRenderer);

        shapeRenderer.setColor(1, 1, 1, 1);
        ball.render(shapeRenderer);

        shapeRenderer.end();

        batch.begin();
        font.draw(batch, String.valueOf(scorePlayer1), Constants.SCREEN_WIDTH / 4f, Constants.SCREEN_HEIGHT - 20);
        font.draw(batch, String.valueOf(scorePlayer2), Constants.SCREEN_WIDTH * 3 / 4f, Constants.SCREEN_HEIGHT - 20);
        batch.end();
    }

    public void update(float delta) {
        boolean upP1 = Gdx.input.isKeyPressed(Keys.W);
        boolean downP1 = Gdx.input.isKeyPressed(Keys.S);
        player1.update(delta, upP1, downP1);

        boolean upP2 = Gdx.input.isKeyPressed(Keys.UP);
        boolean downP2 = Gdx.input.isKeyPressed(Keys.DOWN);
        player2.update(delta, upP2, downP2);

        clampPaddleToScreen(player1);
        clampPaddleToScreen(player2);

        ball.update(delta);

        CollisionHandler.handleBallPaddleCollision(ball, player1);
        CollisionHandler.handleBallPaddleCollision(ball, player2);
        CollisionHandler.handleBallWallCollision(ball);

        if (ball.bounds.x - ball.bounds.radius <= 0) {
            scorePlayer2++;
            resetBall();
        } else if (ball.bounds.x + ball.bounds.radius >= Constants.SCREEN_WIDTH) {
            scorePlayer1++;
            resetBall();
        }
    }

    private void clampPaddleToScreen(Paddle paddle) {
        if (paddle.bounds.x < 0) paddle.bounds.x = 0;
        if (paddle.bounds.x + paddle.bounds.width > Constants.SCREEN_WIDTH)
            paddle.bounds.x = Constants.SCREEN_WIDTH - paddle.bounds.width;

        if (paddle.bounds.y < 0) paddle.bounds.y = 0;
        if (paddle.bounds.y + paddle.bounds.height > Constants.SCREEN_HEIGHT)
            paddle.bounds.y = Constants.SCREEN_HEIGHT - paddle.bounds.height;
    }

    private void resetBall() {

        ball = ballFactory.createBall(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, Constants.BALL_RADIUS);

        // Opcional: se você ainda quiser uma direção aleatória para a nova bola, pode fazer assim:
        if (Math.random() < 0.5) {
            ball.reverseX();
        }
        if (Math.random() < 0.5) {
            ball.reverseY();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Pode deixar vazio por enquanto
    }

    @Override
    public void pause() {
        // Pode deixar vazio por enquanto
    }

    @Override
    public void resume() {
        // Pode deixar vazio por enquanto
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
