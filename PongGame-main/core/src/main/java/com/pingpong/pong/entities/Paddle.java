package com.pingpong.pong.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Paddle {
    public Rectangle bounds;
    public float speed = 300;

    public Paddle(float x, float y, float width, float height) {
        bounds = new Rectangle(x, y, width, height);
    }

    public void update(float delta, boolean up, boolean down) {
        if (up) bounds.y += speed * delta;
        if (down) bounds.y -= speed * delta;
    }

    public void render(ShapeRenderer renderer) {
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
