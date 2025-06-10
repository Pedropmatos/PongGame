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
import com.pingpong.pong.logic.GameScore;
import com.pingpong.pong.logic.ScoreDisplay;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class GameScreen implements Screen {
    ShapeRenderer shapeRenderer;
    Paddle player1;
    Paddle player2;
    Ball ball;
    SpriteBatch batch;
    BitmapFont font;
    BallFactory ballFactory;

    GameScore gameScore;
    ScoreDisplay scoreDisplay;

    private boolean gameOver;
    private String winnerMessage;
    private GlyphLayout layout;

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

        gameScore = new GameScore();
        scoreDisplay = new ScoreDisplay(batch, font);
        gameScore.addObserver(scoreDisplay);

        gameOver = false;
        winnerMessage = "";
        layout = new GlyphLayout();
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

        if (!gameOver) {
            ball.render(shapeRenderer);
        }

        shapeRenderer.end();

        scoreDisplay.render();


        if (gameOver) {
            batch.begin();
            layout.setText(font, winnerMessage);
            float textWidth = layout.width;
            font.draw(batch, winnerMessage, Constants.SCREEN_WIDTH / 2f - textWidth / 2f, Constants.SCREEN_HEIGHT / 2f);
            batch.end();
        }
    }

    public void update(float delta) {
        if (gameOver) {
            return;
        }

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
            gameScore.player2Scores();
            checkWinCondition();
            if (!gameOver) {
                resetBall();
            }
        } else if (ball.bounds.x + ball.bounds.radius >= Constants.SCREEN_WIDTH) {
            gameScore.player1Scores();
            checkWinCondition();
            if (!gameOver) {
                resetBall();
            }
        }
    }

    private void clampPaddleToScreen(Paddle paddle) {
        if (paddle.bounds.y < 0) paddle.bounds.y = 0;
        if (paddle.bounds.y + paddle.bounds.height > Constants.SCREEN_HEIGHT)
            paddle.bounds.y = Constants.SCREEN_HEIGHT - paddle.bounds.height;
    }

    private void resetBall() {
        ball = ballFactory.createBall(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, Constants.BALL_RADIUS);

        if (Math.random() < 0.5) {
            ball.reverseX();
        }
        if (Math.random() < 0.5) {
            ball.reverseY();
        }
    }

    private void checkWinCondition() {
        if (gameScore.getPlayer1Score() >= Constants.MAX_SCORE) {
            gameOver = true;
            winnerMessage = "Player 1 Wins!";
        } else if (gameScore.getPlayer2Score() >= Constants.MAX_SCORE) {
            gameOver = true;
            winnerMessage = "Player 2 Wins!";
        }
    }

    @Override
    public void resize(int width, int height) {

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
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
