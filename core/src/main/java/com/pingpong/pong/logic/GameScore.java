package com.pingpong.pong.logic;

import java.util.ArrayList;
import java.util.List;

public class GameScore implements ScoreSubject {
    private int player1Score;
    private int player2Score;
    private List<ScoreObserver> observers;

    public GameScore() {
        this.player1Score = 0;
        this.player2Score = 0;
        this.observers = new ArrayList<>();
    }

    @Override
    public void addObserver(ScoreObserver observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(int player1Score, int player2Score) {
        for (ScoreObserver observer : observers) {
            observer.updateScore(player1Score, player2Score);
        }
    }

    public void player1Scores() {
        player1Score++;
        notifyObservers(player1Score, player2Score);
    }

    public void player2Scores() {
        player2Score++;
        notifyObservers(player1Score, player2Score);
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }
}
