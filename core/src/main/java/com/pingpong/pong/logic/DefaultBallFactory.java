package com.pingpong.pong.logic;

import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.entities.DefaultBall;

// A Fábrica Concreta
public class DefaultBallFactory implements BallFactory {
    @Override
    public Ball createBall(float x, float y, float radius) {
        return new DefaultBall(x, y, radius);
    }
}
