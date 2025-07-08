package com.pingpong.pong;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

import com.badlogic.gdx.Game;
import com.pingpong.pong.screens.GameScreen;
import com.pingpong.pong.screens.MenuScreen;

public class PongGame extends Game {
    @Override
    public void create() {
        this.setScreen(new MenuScreen(this));
    }
}
