package com.pingpong.pong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

import com.badlogic.gdx.Game;
import com.pingpong.pong.screens.GameScreen;

public class PongGame extends Game {
    @Override
    public void create() {
        this.setScreen(new GameScreen() {
            @Override
            public void resize(int i, int i1) {

            }

            @Override
            public void pause() {

            }

            @Override
            public void resume() {

            }

            @Override
            public void hide() {

            }

            @Override
            public void dispose() {

            }
        });
    }
}
