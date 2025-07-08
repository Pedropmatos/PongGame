package com.pingpong.pong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.pingpong.pong.PongGame;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.entities.Paddle;
import com.pingpong.pong.levels.DefaultLevel;
import com.pingpong.pong.levels.ForestLevel;
import com.pingpong.pong.levels.Level;
import com.pingpong.pong.levels.SpaceLevel;
import com.pingpong.pong.logic.*;
import com.pingpong.pong.utils.Constants;

public class GameScreen implements Screen {
    private PongGame game;
    private ShapeRenderer shapeRenderer;
    private Paddle player1;
    private Paddle player2;
    private Ball ball;
    private SpriteBatch batch;
    private BitmapFont font;
    private BallFactory ballFactory;

    private GameScore gameScore;
    private ScoreDisplay scoreDisplay;

    private boolean gameOver;
    private String winnerMessage;
    private GlyphLayout layout;

    private Level currentLevel;
    private Texture backgroundTexture;
    private Array<Rectangle> fallingLeaves; // Lista para guardar as folhas

    public GameScreen(PongGame game, int level) {
        this.game = game;

        switch (level) {
            case 2:
                this.currentLevel = new ForestLevel();
                break;
            case 3:
                this.currentLevel = new SpaceLevel();
                break;
            default:
                this.currentLevel = new DefaultLevel();
                break;
        }
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        player1 = new Paddle(30, Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);
        player2 = new Paddle(Constants.SCREEN_WIDTH - 30 - Constants.PADDLE_WIDTH,
                Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);

        ballFactory = currentLevel.getBallFactory();
        float initialBallSpeed = currentLevel.getInitialBallSpeed();
        float paddleSpeed = currentLevel.getPaddleSpeed();
        Constants.MAX_SCORE = currentLevel.getMaxScore();
        backgroundTexture = currentLevel.getBackground();

        batch = new SpriteBatch();

        // Inicializa a lista de folhas apenas se for o ForestLevel
        if (currentLevel instanceof ForestLevel) {
            fallingLeaves = new Array<>();
        } else {
            fallingLeaves = null;
        }

        ball = ballFactory.createBall(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, Constants.BALL_RADIUS);
        ball.velocity = new Vector2(initialBallSpeed, initialBallSpeed);

        player1.speed = paddleSpeed;
        player2.speed = paddleSpeed;

        font = new BitmapFont();
        font.getData().setScale(2);

        gameScore = new GameScore();
        scoreDisplay = new ScoreDisplay(batch, font);
        gameScore.addObserver(scoreDisplay);

        gameOver = false;
        winnerMessage = "";
        layout = new GlyphLayout();
    }

    public void update(float delta) {
        if (gameOver) {
            if (Gdx.input.justTouched()) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
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

        // Chama a atualização do nível, passando a bola e as folhas
        currentLevel.update(delta, ball, fallingLeaves);

        CollisionHandler.handleBallPaddleCollision(ball, player1);
        CollisionHandler.handleBallPaddleCollision(ball, ball.velocity.x < 0 ? player1 : player2);
        CollisionHandler.handleBallWallCollision(ball);

        if (ball.bounds.x - ball.bounds.radius <= 0) {
            gameScore.player2Scores();
            checkWinCondition();
            if (!gameOver) resetBall();
        } else if (ball.bounds.x + ball.bounds.radius >= Constants.SCREEN_WIDTH) {
            gameScore.player1Scores();
            checkWinCondition();
            if (!gameOver) resetBall();
        }
    }


    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- Início da Renderização com SpriteBatch (para texturas) ---
        batch.begin();

        // Desenha o fundo
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        }

        // Chama a renderização do nível para desenhar as folhas (que usam SpriteBatch)
        currentLevel.render(null, batch, fallingLeaves);

        batch.end();
        // --- Fim da Renderização com SpriteBatch ---


        // --- Início da Renderização com ShapeRenderer (para formas) ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Define a cor da linha do meio
        if (currentLevel instanceof ForestLevel) {
            shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1);
        } else if (currentLevel instanceof SpaceLevel) {
            shapeRenderer.setColor(0.6f, 0.6f, 0.6f, 1);
        } else {
            shapeRenderer.setColor(1, 1, 1, 1);
        }

        // Desenha a linha do meio
        for (int y = 0; y < Constants.SCREEN_HEIGHT; y += 30) {
            if (y % 60 == 0) {
                shapeRenderer.rectLine(Constants.SCREEN_WIDTH / 2f, y, Constants.SCREEN_WIDTH / 2f, y + 15, 2);
            }
        }

        // Desenha as bordas
        shapeRenderer.rectLine(0, 0, 0, Constants.SCREEN_HEIGHT, 4);
        shapeRenderer.rectLine(Constants.SCREEN_WIDTH, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, 4);

        // Desenha as raquetes e a bola
        shapeRenderer.setColor(1, 0, 0, 1);
        player1.render(shapeRenderer);

        shapeRenderer.setColor(0, 0, 1, 1);
        player2.render(shapeRenderer);

        shapeRenderer.setColor(1, 1, 1, 1);
        if (!gameOver) {
            ball.render(shapeRenderer);
        }

        shapeRenderer.end();
        // --- Fim da Renderização com ShapeRenderer ---


        // Renderiza o placar e a mensagem de fim de jogo
        scoreDisplay.render();

        if (gameOver) {
            batch.begin();
            font.setColor(Color.WHITE);
            layout.setText(font, winnerMessage);
            float textX = Constants.SCREEN_WIDTH / 2f - layout.width / 2f;
            float textY = Constants.SCREEN_HEIGHT / 2f;
            font.draw(batch, winnerMessage, textX, textY);
            batch.end();
        }
    }

    private void clampPaddleToScreen(Paddle paddle) {
        if (paddle.bounds.y < 0) paddle.bounds.y = 0;
        if (paddle.bounds.y + paddle.bounds.height > Constants.SCREEN_HEIGHT)
            paddle.bounds.y = Constants.SCREEN_HEIGHT - paddle.bounds.height;
    }

    private void resetBall() {
        ball = ballFactory.createBall(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, Constants.BALL_RADIUS);
        float initialBallSpeed = currentLevel.getInitialBallSpeed();
        ball.velocity = new Vector2(initialBallSpeed, initialBallSpeed);

        if (Math.random() < 0.5) ball.reverseX();
        if (Math.random() < 0.5) ball.reverseY();
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
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        if (currentLevel != null) {
            currentLevel.dispose();
        }
    }
}
