package com.pingpong.pong.entities;

import com.badlogic.gdx.math.Vector2;

public class DefaultBall extends Ball {
    public DefaultBall(float x, float y, float radius) {
        super(x, y, radius);
        // Define a velocidade inicial padrão para este tipo de bola
        this.velocity = new Vector2(200, 200);
    }
}
