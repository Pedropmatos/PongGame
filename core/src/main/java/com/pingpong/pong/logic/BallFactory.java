package com.pingpong.pong.logic;

import com.pingpong.pong.entities.Ball;

public interface BallFactory {
    Ball createBall(float x, float y, float radius);
}
