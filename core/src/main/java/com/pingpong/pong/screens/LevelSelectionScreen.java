package com.pingpong.pong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pingpong.pong.PongGame;

import java.util.ArrayList;
import java.util.List;

public class LevelSelectionScreen implements Screen {

    private PongGame game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
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
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        titleFont.setColor(Color.YELLOW);

        shapeRenderer = new ShapeRenderer();
        layout = new GlyphLayout();

        levelButtons = new ArrayList<>();
        int numberOfLevels = 3;
        float buttonWidth = 220;
        float buttonHeight = 70;
        float padding = 20;

        float totalHeight = (buttonHeight * numberOfLevels) + (padding * (numberOfLevels - 1));
        // Ajuste no Y inicial para melhor centralização com o título
        float startY = (Gdx.graphics.getHeight() - totalHeight) / 2 + (buttonHeight * (numberOfLevels - 1)) + (padding * (numberOfLevels -1)) / 2 - 30;

        for (int i = 0; i < numberOfLevels; i++) {
            float buttonX = Gdx.graphics.getWidth() / 2f - buttonWidth / 2f;
            float buttonY = startY - (i * (buttonHeight + padding));
            
            // AQUI ESTAVA O ERRO. AGORA OS PARÂMETROS ESTÃO CORRETOS:
            levelButtons.add(new LevelButton(buttonX, buttonY, buttonWidth, buttonHeight, "Fase", i + 1));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        
        layout.setText(titleFont, "Selecione a Fase");
        float titleX = (Gdx.graphics.getWidth() - layout.width) / 2;
        float titleY = Gdx.graphics.getHeight() - 50;
        titleFont.draw(batch, layout, titleX, titleY);

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (LevelButton button : levelButtons) {
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.rect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height);
        }
        
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);

        for (LevelButton button : levelButtons) {
            layout.setText(font, button.text);
            float textX = button.bounds.x + 40;
            float textY = button.bounds.y + (button.bounds.height + layout.height) / 2;
            font.draw(batch, button.text, textX, textY);

            String levelNumber = String.valueOf(button.level);
            layout.setText(font, levelNumber);
            float numberX = button.bounds.x + button.bounds.width - layout.width - 40;
            font.draw(batch, levelNumber, numberX, textY);
        }

        batch.end();

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (LevelButton button : levelButtons) {
                if (button.bounds.contains(touchX, touchY)) {
                    game.setScreen(new GameScreen(game, button.level));
                    dispose();
                    return;
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // O código de redimensionamento pode ser ajustado se necessário, mas por enquanto está ok.
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
        titleFont.dispose();
        shapeRenderer.dispose();
    }
}