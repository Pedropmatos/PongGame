package com.pingpong.pong.logic;

import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.entities.FastBall;

public class FastBallFactory implements BallFactory {
    @Override
    public Ball createBall(float x, float y, float radius) {
        return new FastBall(x, y, radius);
    }
}
