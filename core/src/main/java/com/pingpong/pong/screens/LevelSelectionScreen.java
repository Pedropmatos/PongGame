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

import java.util.ArrayList;
import java.util.List;

public class LevelSelectionScreen implements Screen {

    private PongGame game;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private List<LevelButton> levelButtons;
    private GlyphLayout layout;

    private static class LevelButton {
        public Rectangle bounds;
        public String text;
        public int level;

        public LevelButton(float x, float y, float width, float height, String text, int level) {
            this.bounds = new Rectangle(x, y, width, height);
            this.text = text;
            this.level = level;
        }
    }

    public LevelSelectionScreen(PongGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        shapeRenderer = new ShapeRenderer();
        layout = new GlyphLayout();

        levelButtons = new ArrayList<>();
        int numberOfLevels = 3;
        float buttonWidth = 180;
        float buttonHeight = 70;
        float padding = 20;

        float totalHeight = (buttonHeight * numberOfLevels) + (padding * (numberOfLevels - 1));
        float startY = (Gdx.graphics.getHeight() / 2f) + (totalHeight / 2f) - buttonHeight;

        for (int i = 0; i < numberOfLevels; i++) {
            float buttonX = Gdx.graphics.getWidth() / 2f - buttonWidth / 2f;
            float buttonY = startY - (i * (buttonHeight + padding));
            String buttonText = "Level " + (i + 1);
            levelButtons.add(new LevelButton(buttonX, buttonY, buttonWidth, buttonHeight, buttonText, i + 1));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 1. Desenha as formas (retângulos dos botões)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        for (LevelButton button : levelButtons) {
            shapeRenderer.rect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height);
        }
        shapeRenderer.end();

        // 2. Desenha os textos sobre os botões
        batch.begin();
        font.setColor(Color.WHITE);
        for (LevelButton button : levelButtons) {
            layout.setText(font, button.text);
            float textX = button.bounds.x + (button.bounds.width - layout.width) / 2;
            float textY = button.bounds.y + (button.bounds.height + layout.height) / 2;
            font.draw(batch, button.text, textX, textY);
        }
        batch.end();


        // 3. Verifica se algum botão de fase foi clicado
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (LevelButton button : levelButtons) {
                if (button.bounds.contains(touchX, touchY)) {
                    game.setScreen(new GameScreen(button.level));
                    dispose();
                    return;
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        float buttonWidth = 180;
        float buttonHeight = 70;
        float padding = 20;
        int numberOfLevels = levelButtons.size();

        float totalHeight = (buttonHeight * numberOfLevels) + (padding * (numberOfLevels - 1));
        float startY = (height / 2f) + (totalHeight / 2f) - buttonHeight;

        for (int i = 0; i < numberOfLevels; i++) {
            LevelButton button = levelButtons.get(i);
            float buttonX = width / 2f - buttonWidth / 2f;
            float buttonY = startY - (i * (buttonHeight + padding));
            button.bounds.set(buttonX, buttonY, buttonWidth, buttonHeight);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
