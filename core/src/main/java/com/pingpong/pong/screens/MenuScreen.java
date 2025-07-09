package com.pingpong.pong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.pingpong.pong.PongGame;

public class MenuScreen implements Screen {

    private PongGame game;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private Rectangle playButtonBounds;
    private GlyphLayout layout;

    public MenuScreen(PongGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
        shapeRenderer = new ShapeRenderer();
        layout = new GlyphLayout();

        // Define as dimensões e posição do botão "Play"
        float buttonWidth = 150;
        float buttonHeight = 60;
        float buttonX = Gdx.graphics.getWidth() / 2f - buttonWidth / 2f;
        float buttonY = Gdx.graphics.getHeight() / 2f - buttonHeight / 2f;
        playButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
    }

    @Override
    public void render(float delta) {
        // Limpa a tela
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN); // Cor do botão
        shapeRenderer.rect(playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);
        layout.setText(font, "Play");
        float textX = playButtonBounds.x + (playButtonBounds.width - layout.width) / 2;
        float textY = playButtonBounds.y + (playButtonBounds.height + layout.height) / 2;
        font.draw(batch, "Play", textX, textY);
        batch.end();

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (playButtonBounds.contains(touchX, touchY)) {
                game.setScreen(new LevelSelectionScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        float buttonWidth = 150;
        float buttonHeight = 60;
        float buttonX = width / 2f - buttonWidth / 2f;
        float buttonY = height / 2f - buttonHeight / 2f;
        playButtonBounds.set(buttonX, buttonY, buttonWidth, buttonHeight);
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
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
