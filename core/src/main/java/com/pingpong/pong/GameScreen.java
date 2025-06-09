package com.pingpong.pong.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Input.Keys;
import com.pingpong.pong.entities.Paddle;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.logic.CollisionHandler;
import com.pingpong.pong.utils.Constants;
import com.badlogic.gdx.Screen;

public abstract class GameScreen implements Screen {
    ShapeRenderer shapeRenderer;
    Paddle player1;
    Paddle player2;  // Segunda raquete
    Ball ball;
    SpriteBatch batch;
    BitmapFont font;

    int scorePlayer1 = 0;
    int scorePlayer2 = 0;

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        player1 = new Paddle(30, Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);
        player2 = new Paddle(Constants.SCREEN_WIDTH - 30 - Constants.PADDLE_WIDTH,
            Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);
        ball = new Ball(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, Constants.BALL_RADIUS);
        batch = new SpriteBatch();
        font = new BitmapFont();  // Usa a fonte padrão
        font.getData().setScale(2);  // Aumenta tamanho da fonte
    }

    @Override
    public void render(float delta) {
            update(delta);

            Gdx.gl.glClearColor(0, 0, 0, 1); // fundo preto
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            for (int y = 0; y < Constants.SCREEN_HEIGHT; y += 30) {
                if (y % 60 == 0) {
                shapeRenderer.rectLine(Constants.SCREEN_WIDTH / 2f, y, Constants.SCREEN_WIDTH / 2f, y + 15, 2);
               }
             }

            // Linhas laterais
            shapeRenderer.rectLine(0, 0, 0, Constants.SCREEN_HEIGHT, 4); // esquerda
            shapeRenderer.rectLine(Constants.SCREEN_WIDTH, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, 4); // direita

            shapeRenderer.setColor(1, 0, 0, 1); // vermelho
            player1.render(shapeRenderer);

            shapeRenderer.setColor(0, 0, 1, 1); // azul
            player2.render(shapeRenderer);

            shapeRenderer.setColor(1, 1, 1, 1); // branco
            ball.render(shapeRenderer);

            shapeRenderer.end();

            batch.begin();
            font.draw(batch, String.valueOf(scorePlayer1), Constants.SCREEN_WIDTH / 4f, Constants.SCREEN_HEIGHT - 20);
            font.draw(batch, String.valueOf(scorePlayer2), Constants.SCREEN_WIDTH * 3 / 4f, Constants.SCREEN_HEIGHT - 20);
            batch.end();

    }

    public void update(float delta) {
        // Controle do player 1 (W/S)
        boolean upP1 = Gdx.input.isKeyPressed(Keys.W);
        boolean downP1 = Gdx.input.isKeyPressed(Keys.S);
        player1.update(delta, upP1, downP1);

        // Controle do player 2 (setas para cima e para baixo)
        boolean upP2 = Gdx.input.isKeyPressed(Keys.UP);
        boolean downP2 = Gdx.input.isKeyPressed(Keys.DOWN);
        player2.update(delta, upP2, downP2);


        // Limitar as raquetes para não saírem da tela (X e Y)
        clampPaddleToScreen(player1);
        clampPaddleToScreen(player2);

        ball.update(delta);

        // Checar colisões com as duas raquetes
        CollisionHandler.handleBallPaddleCollision(ball, player1);
        CollisionHandler.handleBallPaddleCollision(ball, player2);

        // Checar colisão com teto e chão
        CollisionHandler.handleBallWallCollision(ball);

        // Checar se a bola saiu pela esquerda ou direita (ponto)
        if (ball.bounds.x - ball.bounds.radius <= 0) {
            // Player 2 marcou ponto
            scorePlayer2++;
            resetBall();
        } else if (ball.bounds.x + ball.bounds.radius >= Constants.SCREEN_WIDTH) {
            // Player 1 marcou ponto
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
        ball.bounds.x = Constants.SCREEN_WIDTH / 2f;
        ball.bounds.y = Constants.SCREEN_HEIGHT / 2f;
        ball.velocity.x = (Math.random() < 0.5 ? -1 : 1) * 200;  // Direção aleatória X
        ball.velocity.y = (Math.random() < 0.5 ? -1 : 1) * 200;  // Direção aleatória Y
    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
