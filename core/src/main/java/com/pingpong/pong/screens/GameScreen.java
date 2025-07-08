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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private int currentLevel;

    private Texture backgroundTexture;

    // Variáveis para o desafio das folhas cadentes (Fase 2)
    private List<Rectangle> fallingLeaves;
    private float leafSpawnTimer;
    private float leafSpawnInterval = 20.0f; // A folha cairá a cada 20 segundos
    private float leafSpeed = 100f; // Velocidade de queda das folhas
    private final float LEAF_WIDTH = 30; // Largura da folha
    private final float LEAF_HEIGHT = 30; // Altura da folha

    // Construtor padrão (mantido para compatibilidade)
    public GameScreen() {
        this(1); // Por padrão, inicia na Fase 1 se nenhum nível for especificado
    }

    // Construtor que aceita o nível selecionado
    public GameScreen(int level) {
        this.currentLevel = level;
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        player1 = new Paddle(30, Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);
        player2 = new Paddle(Constants.SCREEN_WIDTH - 30 - Constants.PADDLE_WIDTH,
            Constants.SCREEN_HEIGHT / 2f - Constants.PADDLE_HEIGHT / 2f, Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT);

        float initialBallSpeed = 200;
        float paddleSpeed = 300;
        int maxScore = Constants.MAX_SCORE;

        batch = new SpriteBatch();

        // Inicializa a lista de folhas se for a Fase 2
        if (currentLevel == 2) {
            fallingLeaves = new ArrayList<>();
            leafSpawnTimer = leafSpawnInterval; // Inicia o temporizador para a primeira folha
        } else {
            fallingLeaves = null; // Garante que a lista seja nula em outras fases
        }

        switch (currentLevel) {
            case 1:
                ballFactory = new DefaultBallFactory();
                initialBallSpeed = 200;
                paddleSpeed = 300;
                maxScore = 5;
                backgroundTexture = null;
                break;
            case 2:
                ballFactory = new DefaultBallFactory();
                initialBallSpeed = 300;
                paddleSpeed = 350;
                maxScore = 7;
                backgroundTexture = new Texture(Gdx.files.internal("forest_background.png"));
                break;
            case 3:
                ballFactory = new FastBallFactory();
                initialBallSpeed = 400;
                paddleSpeed = 400;
                maxScore = 10;
                backgroundTexture = new Texture(Gdx.files.internal("space_background.png"));
                break;
            default:
                ballFactory = new DefaultBallFactory();
                initialBallSpeed = 200;
                paddleSpeed = 300;
                maxScore = 5;
                backgroundTexture = null;
                break;
        }

        ball = ballFactory.createBall(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f, Constants.BALL_RADIUS);
        ball.velocity = new Vector2(initialBallSpeed, initialBallSpeed);

        player1.speed = paddleSpeed;
        player2.speed = paddleSpeed;
        Constants.MAX_SCORE = maxScore;

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

        if (backgroundTexture != null) {
            batch.begin();
            batch.draw(backgroundTexture, 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
            batch.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Ajusta a cor da linha central e das bordas laterais para contraste com o background
        if (currentLevel == 2) {
            shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1); // Cinza claro para contraste com floresta
        } else if (currentLevel == 3) {
            shapeRenderer.setColor(0.6f, 0.6f, 0.6f, 1); // Cinza médio para contraste com espaço
        }
        else {
            shapeRenderer.setColor(1, 1, 1, 1); // Branco padrão para fundo preto
        }

        // Linha central pontilhada
        for (int y = 0; y < Constants.SCREEN_HEIGHT; y += 30) {
            if (y % 60 == 0) {
                shapeRenderer.rectLine(Constants.SCREEN_WIDTH / 2f, y, Constants.SCREEN_WIDTH / 2f, y + 15, 2);
            }
        }

        // Linhas de borda laterais
        shapeRenderer.rectLine(0, 0, 0, Constants.SCREEN_HEIGHT, 4);
        shapeRenderer.rectLine(Constants.SCREEN_WIDTH, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, 4);

        // Desenha as raquetes
        shapeRenderer.setColor(1, 0, 0, 1); // Vermelho para o Jogador 1
        player1.render(shapeRenderer);

        shapeRenderer.setColor(0, 0, 1, 1); // Azul para o Jogador 2
        player2.render(shapeRenderer);

        // Desenha as folhas cadentes (apenas na Fase 2)
        if (currentLevel == 2 && fallingLeaves != null) {
            shapeRenderer.setColor(0.6f, 0.4f, 0.2f, 1); // Cor marrom/laranja para as folhas
            for (Rectangle leaf : fallingLeaves) {
                shapeRenderer.rect(leaf.x, leaf.y, leaf.width, leaf.height);
            }
        }

        // Desenha a bola
        shapeRenderer.setColor(1, 1, 1, 1); // Branco para a bola
        if (!gameOver) {
            ball.render(shapeRenderer);
        }

        shapeRenderer.end();

        scoreDisplay.render();

        // Exibe a mensagem de vitória/derrota
        if (gameOver) {
            batch.begin();
            font.setColor(Color.WHITE);
            layout.setText(font, winnerMessage);
            float textX = Constants.SCREEN_WIDTH / 2f - layout.width / 2f;
            float textY = Constants.SCREEN_HEIGHT / 2f;
            font.draw(batch, winnerMessage, textX, textY);
            batch.end();
            if (Gdx.input.justTouched()) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        }
    }

    public void update(float delta) {
        if (gameOver) {
            return;
        }

        // Lógica do desafio das folhas cadentes (apenas na Fase 2)
        if (currentLevel == 2 && fallingLeaves != null) {
            leafSpawnTimer -= delta;
            if (leafSpawnTimer <= 0) {
                // Cria apenas UMA folha no meio da tela
                float leafX = Constants.SCREEN_WIDTH / 2f - LEAF_WIDTH / 2f;
                fallingLeaves.add(new Rectangle(leafX, Constants.SCREEN_HEIGHT, LEAF_WIDTH, LEAF_HEIGHT));
                leafSpawnTimer = leafSpawnInterval; // Reseta o temporizador
            }

            // Atualiza a posição das folhas e verifica colisões
            Iterator<Rectangle> iterator = fallingLeaves.iterator();
            while (iterator.hasNext()) {
                Rectangle leaf = iterator.next();
                leaf.y -= leafSpeed * delta; // Move a folha para baixo

                // Verifica colisão da bola com a folha
                if (ball.bounds.overlaps(leaf)) {
                    ball.reverseY(); // Inverte a direção Y da bola
                    iterator.remove(); // Remove a folha após a colisão
                }
                // Remove a folha se ela sair da tela
                else if (leaf.y + leaf.height < 0) {
                    iterator.remove();
                }
            }
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
        CollisionHandler.handleBallPaddleCollision(ball, ball.velocity.x < 0 ? player1 : player2);
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
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
