package com.pingpong.pong.logic;

public interface ScoreSubject {
    void addObserver(ScoreObserver observer);
    void removeObserver(ScoreObserver observer);
    void notifyObservers(int player1Score, int player2Score);
}
