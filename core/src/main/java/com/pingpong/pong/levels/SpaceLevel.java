package com.pingpong.pong.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.logic.BallFactory;
import com.pingpong.pong.logic.FastBallFactory;
import com.pingpong.pong.utils.Constants;

public class SpaceLevel extends Level {

    private final Texture background;
    private final Texture blackHoleTexture;
    private final Sound blackHoleSound;

    private final Array<BlackHole> blackHoles;

    // Classe interna para representar um buraco negro
    private static class BlackHole {
        final Circle bounds;
        boolean isVisible;
        float timer;

        BlackHole(float x, float y, float radius) {
            this.bounds = new Circle(x, y, radius);
            this.timer = MathUtils.random(1.0f, 3.0f); // Temporizador aleatório para cada buraco negro
            this.isVisible = true;
        }
    }

    public SpaceLevel() {
        this.background = new Texture(Gdx.files.internal("space_background.jpg"));
        this.blackHoleTexture = new Texture(Gdx.files.internal("black-hole.jpg"));
        this.blackHoleSound = Gdx.audio.newSound(Gdx.files.internal("sugar1.mp3"));

        this.blackHoles = new Array<>();
        // Cria 3 buracos negros em posições aleatórias (evitando as bordas)
        for (int i = 0; i < 3; i++) {
            float x = MathUtils.random(Constants.SCREEN_WIDTH * 0.2f, Constants.SCREEN_WIDTH * 0.8f);
            float y = MathUtils.random(Constants.SCREEN_HEIGHT * 0.2f, Constants.SCREEN_HEIGHT * 0.8f);
            blackHoles.add(new BlackHole(x, y, 15f)); // 15f é o raio do buraco negro
        }
    }

    @Override
    public BallFactory getBallFactory() {
        return new FastBallFactory();
    }

    @Override
    public float getInitialBallSpeed() {
        return 300f;
    }

    @Override
    public float getPaddleSpeed() {
        return 400f;
    }

    @Override
    public int getMaxScore() {
        return 10;
    }

    @Override
    public Texture getBackground() {
        return background;
    }

    @Override
    public void update(float delta, Ball ball, Array<Rectangle> fallingLeaves) {
        // Lógica de atualização dos buracos negros
        for (BlackHole bh : blackHoles) {
            bh.timer -= delta;
            // A cada 2 segundos, alterna a visibilidade
            if (bh.timer <= 0) {
                bh.isVisible = !bh.isVisible;
                bh.timer = 2.0f; // Reseta o temporizador
            }

            // Verifica colisão da bola com o buraco negro (se ele estiver visível)
            if (bh.isVisible && ball != null && Intersector.overlaps(ball.bounds, bh.bounds)) {
                blackHoleSound.play();

                // Reposiciona a bola no centro da tela
                ball.bounds.setPosition(Constants.SCREEN_WIDTH / 2f, Constants.SCREEN_HEIGHT / 2f);

                // Muda a direção da bola para um ângulo aleatório, mantendo a velocidade alta
                float currentSpeed = ball.velocity.len();
                ball.velocity.setAngleDeg(MathUtils.random(360));
                ball.velocity.setLength(currentSpeed); // Mantém a velocidade
            }
        }
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, Array<Rectangle> fallingLeaves) {
        // Desenha os buracos negros que estiverem visíveis
        for (BlackHole bh : blackHoles) {
            if (bh.isVisible) {
                batch.draw(blackHoleTexture,
                    bh.bounds.x - bh.bounds.radius,
                    bh.bounds.y - bh.bounds.radius,
                    bh.bounds.radius * 2,
                    bh.bounds.radius * 2);
            }
        }
    }

    @Override
    public void dispose() {
        if (background != null) {
            background.dispose();
        }
        if (blackHoleTexture != null) {
            blackHoleTexture.dispose();
        }
        if (blackHoleSound != null) {
            blackHoleSound.dispose();
        }
    }
}
