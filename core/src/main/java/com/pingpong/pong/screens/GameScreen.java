package com.pingpong.pong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

public class GameScreen implements Screen {

    // Variáveis de Jogo
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont countdownFont;
    private GlyphLayout layout;

    // Entidades
    private Paddle player1;
    private Paddle player2;
    private Ball ball;

    // Lógica de Jogo
    private GameScore gameScore;
    private ScoreDisplay scoreDisplay;
    private boolean gameOver;
    private boolean gameStarted;
    private String winnerMessage;
    private float countdownTimer;

    // Variáveis de Nível
    private final Level level;
    private BallFactory ballFactory;
    private Array<Rectangle> fallingLeaves;
    private Texture backgroundTexture;
    private TextureRegion backgroundTextureRegion;

    // Sons
    private Sound paddleHitSound;
    private Sound wallHitSound;
    private Sound scoreSound;
    private Sound victorySound;

    public GameScreen(int levelNum) {
        if (levelNum == 2) {
            this.level = new ForestLevel();
        } else if (levelNum == 3) {
            this.level = new SpaceLevel();
        } else {
            this.level = new DefaultLevel();
        }
    }

    @Override
    public void show() {
        // --- 1. INICIALIZAÇÃO DE OBJETOS GRÁFICOS E DE JOGO ---
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        layout = new GlyphLayout();
        font = new BitmapFont(Gdx.files.classpath("default.fnt"));
        font.getData().setScale(2);
        countdownFont = new BitmapFont();
        countdownFont.getData().setScale(4);
        fallingLeaves = new Array<>();

        // --- 2. CARREGAMENTO DE SONS ---
        paddleHitSound = Gdx.audio.newSound(Gdx.files.internal("tennis-ball-hit-151257.mp3"));
        wallHitSound = Gdx.audio.newSound(Gdx.files.internal("rubberballbouncing-251948.mp3"));
        scoreSound = Gdx.audio.newSound(Gdx.files.internal("bonus-points-190035.mp3"));
        victorySound = Gdx.audio.newSound(Gdx.files.internal("badass-victory-85546.mp3"));

        // --- 3. INICIALIZAÇÃO DE ENTIDADES E LÓGICA DE PLACAR ---
        player1 = new Paddle(30, Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);
        player2 = new Paddle(Constants.SCREEN_WIDTH - 30 - Constants.PADDLE_WIDTH, Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);
        gameScore = new GameScore();
        scoreDisplay = new ScoreDisplay(batch, font);
        gameScore.addObserver(scoreDisplay);

        // --- 4. CONFIGURAÇÃO BASEADA NO NÍVEL (ORDEM CRÍTICA) ---
        this.ballFactory = level.getBallFactory();

        // Adicionámos uma verificação de segurança. Se o erro persistir, a mensagem será mais clara.
        if (this.ballFactory == null) {
            throw new IllegalStateException("A BallFactory não pode ser nula. O nível " + level.getClass().getSimpleName() + " não a configurou corretamente.");
        }

        player1.speed = level.getPaddleSpeed();
        player2.speed = level.getPaddleSpeed();
        Constants.MAX_SCORE = level.getMaxScore();

        // --- 5. CRIAÇÃO DA BOLA INICIAL ---
        // Em vez de criar a bola aqui, chamamos o método resetBall que já faz isso.
        // Isto centraliza a criação da bola e torna o código mais seguro.
        resetBall();

        // --- 6. CONFIGURAÇÃO FINAL ---
        Texture bg = level.getBackground();
        if (bg != null) {
            backgroundTexture = bg;
        } else {
            backgroundTexture = new Texture(Gdx.files.internal("pexels-francesco-ungaro-998641.jpg"));
        }
        backgroundTextureRegion = new TextureRegion(backgroundTexture);

        gameOver = false;
        gameStarted = false;
        winnerMessage = "";
        countdownTimer = 3.0f;
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTextureRegion, 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        if (level != null) {
            level.render(shapeRenderer, batch, fallingLeaves);
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int y = 0; y < Constants.SCREEN_HEIGHT; y += 30) {
            if (y % 60 == 0) shapeRenderer.rectLine(Constants.SCREEN_WIDTH / 2f, y, Constants.SCREEN_WIDTH / 2f, y + 15, 2);
        }
        shapeRenderer.rectLine(0, 0, 0, Constants.SCREEN_HEIGHT, 4);
        shapeRenderer.rectLine(Constants.SCREEN_WIDTH, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, 4);
        if (player1 != null) {
            shapeRenderer.setColor(1, 0, 0, 1);
            player1.render(shapeRenderer);
        }
        if (player2 != null) {
            shapeRenderer.setColor(0, 0, 1, 1);
            player2.render(shapeRenderer);
        }
        if (ball != null && gameStarted && !gameOver) {
            shapeRenderer.setColor(1, 1, 1, 1);
            ball.render(shapeRenderer);
        }
        shapeRenderer.end();

        scoreDisplay.render();

        batch.begin();
        if (gameOver) {
            layout.setText(font, winnerMessage);
            font.draw(batch, winnerMessage, (Constants.SCREEN_WIDTH - layout.width) / 2f, Constants.SCREEN_HEIGHT / 2f);
        } else if (!gameStarted) {
            String countdownText = String.valueOf((int) Math.ceil(countdownTimer));
            layout.setText(countdownFont, countdownText);
            countdownFont.draw(batch, countdownText, (Constants.SCREEN_WIDTH - layout.width) / 2f, (Constants.SCREEN_HEIGHT + layout.height) / 2f);
        }
        batch.end();
    }

    public void update(float delta) {
        if (gameOver) return;

        if (!gameStarted) {
            countdownTimer -= delta;
            if (countdownTimer <= 0) gameStarted = true;
            return;
        }

        level.update(delta, ball, fallingLeaves);

        player1.update(delta, Gdx.input.isKeyPressed(Keys.W), Gdx.input.isKeyPressed(Keys.S));
        player2.update(delta, Gdx.input.isKeyPressed(Keys.UP), Gdx.input.isKeyPressed(Keys.DOWN));

        clampPaddleToScreen(player1);
        clampPaddleToScreen(player2);

        if (ball != null) {
            ball.update(delta);
            CollisionHandler.handleBallPaddleCollision(ball, player1, paddleHitSound);
            CollisionHandler.handleBallPaddleCollision(ball, player2, paddleHitSound);
            CollisionHandler.handleBallWallCollision(ball, wallHitSound);

            if (ball.bounds.x - ball.bounds.radius <= 0) {
                gameScore.player2Scores();
                scoreSound.play();
                checkWinCondition();
                if (!gameOver) resetBall();
            } else if (ball.bounds.x + ball.bounds.radius >= Constants.SCREEN_WIDTH) {
                gameScore.player1Scores();
                scoreSound.play();
                checkWinCondition();
                if (!gameOver) resetBall();
            }
        }
    }

    private void checkWinCondition() {
        if (!gameOver && gameScore.getPlayer1Score() >= Constants.MAX_SCORE) {
            gameOver = true;
            winnerMessage = "Player 1 Wins!";
            victorySound.play();
        } else if (!gameOver && gameScore.getPlayer2Score() >= Constants.MAX_SCORE) {
            gameOver = true;
            winnerMessage = "Player 2 Wins!";
            victorySound.play();
        }
    }

    private void clampPaddleToScreen(Paddle paddle) {
        if (paddle != null && paddle.bounds.y < 0) paddle.bounds.y = 0;
        if (paddle != null && paddle.bounds.y + paddle.bounds.height > Constants.SCREEN_HEIGHT)
            paddle.bounds.y = Constants.SCREEN_HEIGHT - paddle.bounds.height;
    }

    private void resetBall() {
        if (ballFactory != null) {
            ball = ballFactory.createBall(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, Constants.BALL_RADIUS);
            if (Math.random() < 0.5) ball.reverseX();
            if (Math.random() < 0.5) ball.reverseY();
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
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (countdownFont != null) countdownFont.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (level != null) level.dispose();
        if (paddleHitSound != null) paddleHitSound.dispose();
        if (wallHitSound != null) wallHitSound.dispose();
        if (scoreSound != null) scoreSound.dispose();
        if (victorySound != null) victorySound.dispose();
    }
}
