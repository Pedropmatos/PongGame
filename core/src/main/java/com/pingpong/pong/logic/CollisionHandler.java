package com.pingpong.pong.logic;

import com.badlogic.gdx.math.Intersector;
import com.pingpong.pong.entities.Ball;
import com.pingpong.pong.entities.Paddle;
import com.pingpong.pong.utils.Constants;

public class CollisionHandler {

    public static void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        // Verifica se o círculo da bola colide com o retângulo da raquete
        if (Intersector.overlaps(ball.bounds, paddle.bounds)) {
            ball.reverseX();
        }
    }

    public static void handleBallWallCollision(Ball ball) {
        // Colisão com o teto
        if (ball.bounds.y + ball.bounds.radius >= Constants.SCREEN_HEIGHT) {
            ball.reverseY();
            ball.bounds.y = Constants.SCREEN_HEIGHT - ball.bounds.radius; // Corrige posição
        }

        // Colisão com o chão
        if (ball.bounds.y - ball.bounds.radius <= 0) {
            ball.reverseY();
            ball.bounds.y = ball.bounds.radius; // Corrige posição
        }
    }
}
