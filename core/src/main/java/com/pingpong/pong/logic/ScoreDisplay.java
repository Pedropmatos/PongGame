package com.pingpong.pong.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pingpong.pong.utils.Constants;

public class ScoreDisplay implements ScoreObserver {
    private SpriteBatch batch;
    private BitmapFont font;
    private int displayedPlayer1Score;
    private int displayedPlayer2Score;

    public ScoreDisplay(SpriteBatch batch, BitmapFont font) {
        this.batch = batch;
        this.font = font;
        this.displayedPlayer1Score = 0;
        this.displayedPlayer2Score = 0;
    }

    @Override
    public void updateScore(int player1Score, int player2Score) {
        this.displayedPlayer1Score = player1Score;
        this.displayedPlayer2Score = player2Score;
    }

    public void render() {
        batch.begin();
        font.draw(batch, String.valueOf(displayedPlayer1Score), Constants.SCREEN_WIDTH / 4f, Constants.SCREEN_HEIGHT - 20);
        font.draw(batch, String.valueOf(displayedPlayer2Score), Constants.SCREEN_WIDTH * 3 / 4f, Constants.SCREEN_HEIGHT - 20);
        batch.end();
    }
}
