package com.pingpong.pong.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public abstract class Ball {
    public Circle bounds;
    public Vector2 velocity;

    public Ball(float x, float y, float radius) {
        bounds = new Circle(x, y, radius);
        velocity = new Vector2();
    }

    public void update(float delta) {
        bounds.x += velocity.x * delta;
        bounds.y += velocity.y * delta;
    }

    public void render(ShapeRenderer renderer) {
        renderer.circle(bounds.x, bounds.y, bounds.radius);
    }

    public void reverseX() { velocity.x = -velocity.x; }
    public void reverseY() { velocity.y = -velocity.y; }
}
