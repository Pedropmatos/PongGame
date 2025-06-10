package com.pingpong.pong.logic;

public interface ScoreObserver {
    void updateScore(int player1Score, int player2Score);
}
