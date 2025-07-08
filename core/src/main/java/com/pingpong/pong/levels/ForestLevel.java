package com.pingpong.pong.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.logic.BallFactory;
import com.pingpong.pong.logic.DefaultBallFactory;
import com.pingpong.pong.utils.Constants;
import java.util.Iterator;

public class ForestLevel extends Level {

    private Texture background;
    private float leafSpawnTimer;
    private final float leafSpawnInterval = 20.0f;
    private final float leafSpeed = 100f;
    private final float LEAF_WIDTH = 30;
    private final float LEAF_HEIGHT = 30;

    public ForestLevel() {
        // CORREÇÃO: O caminho agora está correto, sem o prefixo "assets/"
        this.background = new Texture(Gdx.files.internal("forest_background.jpg"));
        this.leafSpawnTimer = leafSpawnInterval;
    }

    @Override
    public BallFactory getBallFactory() {
        return new DefaultBallFactory();
    }

    @Override
    public float getInitialBallSpeed() {
        return 300f;
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
            float leafX = Constants.SCREEN_WIDTH / 2f - LEAF_WIDTH / 2f;
            fallingLeaves.add(new Rectangle(leafX, Constants.SCREEN_HEIGHT, LEAF_WIDTH, LEAF_HEIGHT));
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
    public void render(ShapeRenderer shapeRenderer, Array<Rectangle> fallingLeaves) {
        if (fallingLeaves == null) return;
        
        shapeRenderer.setColor(0.6f, 0.4f, 0.2f, 1);
        for (Rectangle leaf : fallingLeaves) {
            shapeRenderer.rect(leaf.x, leaf.y, leaf.width, leaf.height);
        }
    }

    @Override
    public void dispose() {
        if (background != null) {
            background.dispose();
        }
    }
}