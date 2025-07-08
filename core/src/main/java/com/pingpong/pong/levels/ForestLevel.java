package com.pingpong.pong.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.logic.BallFactory;
import com.pingpong.pong.logic.DefaultBallFactory;
import java.util.Iterator;

public class ForestLevel extends Level {

    private Texture background;
    private Texture leafTexture; // Textura para a imagem da folha
    private float leafSpawnTimer;

    // CORREÇÃO: Frequência diminuída. Aumente este valor para mais tempo entre as folhas.
    private final float leafSpawnInterval = 2.8f;
    private final float leafSpeed = 100f;
    private final float LEAF_WIDTH = 25; // Tamanho ajustado para a imagem
    private final float LEAF_HEIGHT = 25;

    public ForestLevel() {
        this.background = new Texture(Gdx.files.internal("forest_background.jpg"));
        this.leafTexture = new Texture(Gdx.files.internal("leaf.png")); // Carrega a imagem da folha
        this.leafSpawnTimer = leafSpawnInterval;
    }

    @Override
    public BallFactory getBallFactory() {
        return new DefaultBallFactory();
    }

    @Override
    public float getInitialBallSpeed() {
        return 220f;
    }

    @Override
    public float getPaddleSpeed() {
        return 350f;
    }

    @Override
    public int getMaxScore() {
        return 7;
    }

    @Override
    public Texture getBackground() {
        return background;
    }

    @Override
    public void update(float delta, Ball ball, Array<Rectangle> fallingLeaves) {
        if (fallingLeaves == null) return;

        leafSpawnTimer -= delta;
        if (leafSpawnTimer <= 0) {
            float minX = 50; // Limite esquerdo
            float maxX = 600 - LEAF_WIDTH; // Limite direito
            float leafX = MathUtils.random(minX, maxX);

            fallingLeaves.add(new Rectangle(leafX, 480, LEAF_WIDTH, LEAF_HEIGHT));
            leafSpawnTimer = leafSpawnInterval;
        }

        Iterator<Rectangle> iterator = fallingLeaves.iterator();
        while (iterator.hasNext()) {
            Rectangle leaf = iterator.next();
            leaf.y -= leafSpeed * delta;

            if (Intersector.overlaps(ball.bounds, leaf)) {
                ball.reverseY();
                iterator.remove();
            } else if (leaf.y + leaf.height < 0) {
                iterator.remove();
            }
        }
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, Array<Rectangle> fallingLeaves) {
        if (fallingLeaves == null) return;

        // Usa o SpriteBatch para desenhar a textura da folha
        for (Rectangle leaf : fallingLeaves) {
            batch.draw(leafTexture, leaf.x, leaf.y, leaf.width, leaf.height);
        }
    }

    @Override
    public void dispose() {
        if (background != null) {
            background.dispose();
        }
        if (leafTexture != null) {
            leafTexture.dispose(); // Libera a textura da folha
        }
    }
}
