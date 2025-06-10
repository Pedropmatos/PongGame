package com.pingpong.pong;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

import com.badlogic.gdx.Game;
import com.pingpong.pong.screens.GameScreen; // A importação agora deve funcionar

public class PongGame extends Game {
    @Override
    public void create() {
        // A criação da tela agora é muito mais simples
        this.setScreen(new GameScreen());
    }
}
