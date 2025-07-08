package com.pingpong.pong;

import com.badlogic.gdx.Game;
import com.pingpong.pong.screens.MenuScreen;

public class PongGame extends Game {
    @Override
    public void create() {
        // A linha de debug foi removida pois não é mais necessária.
        this.setScreen(new MenuScreen(this));
    }
}