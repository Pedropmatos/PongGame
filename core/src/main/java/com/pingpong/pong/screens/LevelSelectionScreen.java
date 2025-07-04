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

    // Classe interna para representar um botão de fase
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
        font.getData().setScale(1.5f); // Tamanho da fonte para os botões de fase
        shapeRenderer = new ShapeRenderer();
        layout = new GlyphLayout();

        levelButtons = new ArrayList<>();
        // Definir as fases disponíveis e criar os botões
        // Exemplo: 3 fases
        int numberOfLevels = 3;
        float buttonWidth = 180;
        float buttonHeight = 70;
        float padding = 20; // Espaçamento entre os botões

        float totalHeight = (buttonHeight * numberOfLevels) + (padding * (numberOfLevels - 1));
        float startY = (Gdx.graphics.getHeight() / 2f) + (totalHeight / 2f) - buttonHeight;

        for (int i = 0; i < numberOfLevels; i++) {
            float buttonX = Gdx.graphics.getWidth() / 2f - buttonWidth / 2f;
            float buttonY = startY - (i * (buttonHeight + padding));
            String buttonText = "Fase " + (i + 1);
            levelButtons.add(new LevelButton(buttonX, buttonY, buttonWidth, buttonHeight, buttonText, i + 1));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        batch.begin();
        font.setColor(Color.WHITE);

        // Desenha cada botão de fase
        for (LevelButton button : levelButtons) {
            shapeRenderer.setColor(Color.BLUE); // Cor dos botões de fase
            shapeRenderer.rect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height);

            // Desenha o texto do botão
            layout.setText(font, button.text);
            float textX = button.bounds.x + (button.bounds.width - layout.width) / 2;
            float textY = button.bounds.y + (button.bounds.height + layout.height) / 2;
            font.draw(batch, button.text, textX, textY);
        }

        batch.end();
        shapeRenderer.end();

        // Verifica se algum botão de fase foi clicado
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Inverte a coordenada Y

            for (LevelButton button : levelButtons) {
                if (button.bounds.contains(touchX, touchY)) {
                    // Transiciona para a GameScreen, passando o nível selecionado
                    game.setScreen(new GameScreen(button.level));
                    dispose();
                    return; // Sai do loop após encontrar o botão clicado
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Recalcula as posições dos botões se a tela for redimensionada
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
