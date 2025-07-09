package com.pingpong.pong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.entities.Paddle;
import com.pingpong.pong.levels.DefaultLevel;
import com.pingpong.pong.levels.ForestLevel;
import com.pingpong.pong.levels.Level;
import com.pingpong.pong.levels.SpaceLevel;
import com.pingpong.pong.logic.BallFactory;
import com.pingpong.pong.logic.CollisionHandler;
import com.pingpong.pong.logic.GameScore;
import com.pingpong.pong.logic.ScoreDisplay;
import com.pingpong.pong.utils.Constants;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreen implements Screen {

    Texture backgroundTexture;
    TextureRegion backgroundTextureRegion;

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

    private float countdownTimer;
    private boolean gameStarted;
    private BitmapFont countdownFont;

    // Variáveis de Nível
    private Level level;
    private Array<Rectangle> fallingLeaves;

    public GameScreen(int level) {
        if (level == 2) {
            this.level = new ForestLevel();
        } else if (level == 3) {
            this.level = new SpaceLevel();
        } else {
            this.level = new DefaultLevel();
        }
    }

    public GameScreen() {
        this(1); // Construtor padrão carrega o nível 1
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.classpath("default.fnt"));
        font.getData().setScale(2);
        layout = new GlyphLayout();

        // Inicializa entidades e lógica do jogo
        player1 = new Paddle(30, Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);
        player2 = new Paddle(Constants.SCREEN_WIDTH - 30 - Constants.PADDLE_WIDTH,
            Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);

        gameScore = new GameScore();
        scoreDisplay = new ScoreDisplay(batch, font);
        gameScore.addObserver(scoreDisplay);

        // Configurações baseadas no nível
        ballFactory = level.getBallFactory();
        player1.speed = level.getPaddleSpeed();
        player2.speed = level.getPaddleSpeed();
        Constants.MAX_SCORE = level.getMaxScore();
        ball = ballFactory.createBall(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, Constants.BALL_RADIUS);

        Texture bg = level.getBackground();
        if (bg != null) {
            backgroundTexture = bg;
        } else {
            backgroundTexture = new Texture(Gdx.files.internal("pexels-francesco-ungaro-998641.jpg"));
        }
        backgroundTextureRegion = new TextureRegion(backgroundTexture);

        fallingLeaves = new Array<>();

        gameOver = false;
        winnerMessage = "";
        countdownTimer = 3.0f;
        gameStarted = false;
        countdownFont = new BitmapFont();
        countdownFont.getData().setScale(4);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTextureRegion, 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        batch.end();

        batch.begin();
        level.render(shapeRenderer, batch, fallingLeaves);
        batch.end();

        // Desenha os elementos do jogo com ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Linha do meio
        for (int y = 0; y < Constants.SCREEN_HEIGHT; y += 30) {
            if (y % 60 == 0) {
                shapeRenderer.rectLine(Constants.SCREEN_WIDTH / 2f, y, Constants.SCREEN_WIDTH / 2f, y + 15, 2);
            }
        }
        // Bordas
        shapeRenderer.rectLine(0, 0, 0, Constants.SCREEN_HEIGHT, 4);
        shapeRenderer.rectLine(Constants.SCREEN_WIDTH, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, 4);

        // Raquetes e Bola
        shapeRenderer.setColor(1, 0, 0, 1);
        player1.render(shapeRenderer);
        shapeRenderer.setColor(0, 0, 1, 1);
        player2.render(shapeRenderer);
        shapeRenderer.setColor(1, 1, 1, 1);
        if (gameStarted && !gameOver) {
            ball.render(shapeRenderer);
        }
        shapeRenderer.end();

        // Renderiza o placar e outras informações de texto
        scoreDisplay.render();

        if (gameOver) {
            batch.begin();
            layout.setText(font, winnerMessage);
            float textWidth = layout.width;
            font.draw(batch, winnerMessage, Constants.SCREEN_WIDTH / 2f - textWidth / 2f, Constants.SCREEN_HEIGHT / 2f);
            batch.end();
        }

        if (!gameStarted && countdownTimer > 0) {
            batch.begin();
            countdownFont.setColor(1, 1, 1, 1);
            String countdownText = String.valueOf((int) Math.ceil(countdownTimer));
            layout.setText(countdownFont, countdownText);
            float textX = Constants.SCREEN_WIDTH / 2f - layout.width / 2f;
            float textY = Constants.SCREEN_HEIGHT / 2f + layout.height / 2f;
            countdownFont.draw(batch, countdownText, textX, textY);
            batch.end();
        }
    }

    public void update(float delta) {
        if (!gameStarted) {
            countdownTimer -= delta;
            if (countdownTimer <= 0) {
                gameStarted = true;
            }
            return;
        }

        if (gameOver) {
            return;
        }

        level.update(delta, ball, fallingLeaves);

        // Controles dos jogadores
        boolean upP1 = Gdx.input.isKeyPressed(Keys.W);
        boolean downP1 = Gdx.input.isKeyPressed(Keys.S);
        player1.update(delta, upP1, downP1);

        boolean upP2 = Gdx.input.isKeyPressed(Keys.UP);
        boolean downP2 = Gdx.input.isKeyPressed(Keys.DOWN);
        player2.update(delta, upP2, downP2);

        // Mantém as raquetes dentro da tela
        clampPaddleToScreen(player1);
        clampPaddleToScreen(player2);

        // Lógica da bola
        ball.update(delta);
        CollisionHandler.handleBallPaddleCollision(ball, player1);
        CollisionHandler.handleBallPaddleCollision(ball, player2);
        CollisionHandler.handleBallWallCollision(ball);

        // Verifica pontuação
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
        countdownFont.dispose();
        backgroundTexture.dispose();
        level.dispose(); // Libera recursos do nível
    }
}
