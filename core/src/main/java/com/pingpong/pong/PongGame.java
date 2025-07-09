package com.pingpong.pong;

import com.badlogic.gdx.Game;
import com.pingpong.pong.screens.GameScreen;
import com.pingpong.pong.screens.MenuScreen;

public class PongGame extends Game {
    @Override
    public void create() {
        this.setScreen(new MenuScreen(this));
    }
}
