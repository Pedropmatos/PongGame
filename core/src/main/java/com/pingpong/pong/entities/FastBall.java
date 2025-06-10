package com.pingpong.pong.entities;

import com.badlogic.gdx.math.Vector2;

public class FastBall extends Ball {
    public FastBall(float x, float y, float radius) {
        super(x, y, radius);

        this.velocity = new Vector2(300, 300);
    }
}
