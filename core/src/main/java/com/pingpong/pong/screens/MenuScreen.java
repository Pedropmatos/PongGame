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
import com.pingpong.pong.PongGame; // Importa a classe principal do jogo para acesso ao setScreen

public class MenuScreen implements Screen {

    private PongGame game; // Referência ao objeto Game principal
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private Rectangle playButtonBounds;
    private GlyphLayout layout;

    // Construtor que recebe a instância do jogo
    public MenuScreen(PongGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2); // Aumenta o tamanho da fonte
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
        font.setColor(Color.WHITE); // Cor do texto
        layout.setText(font, "Play");
        float textX = playButtonBounds.x + (playButtonBounds.width - layout.width) / 2;
        float textY = playButtonBounds.y + (playButtonBounds.height + layout.height) / 2;
        font.draw(batch, "Play", textX, textY);
        batch.end();

        // Verifica se o botão "Play" foi clicado
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Inverte a coordenada Y para corresponder ao sistema de coordenadas do LibGDX

            if (playButtonBounds.contains(touchX, touchY)) {
                // Se o botão "Play" foi clicado, transiciona para a tela de seleção de fases
                game.setScreen(new LevelSelectionScreen(game)); // AGORA VAI PARA A TELA DE SELEÇÃO DE FASES
                dispose(); // Libera os recursos da tela atual
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Atualiza as posições e tamanhos dos elementos se a tela for redimensionada
        float buttonWidth = 150;
        float buttonHeight = 60;
        float buttonX = width / 2f - buttonWidth / 2f;
        float buttonY = height / 2f - buttonHeight / 2f;
        playButtonBounds.set(buttonX, buttonY, buttonWidth, buttonHeight);
    }

    @Override
    public void pause() {
        // Pausa a tela
    }

    @Override
    public void resume() {
        // Retoma a tela
    }

    @Override
    public void hide() {
        // Esconde a tela
    }

    @Override
    public void dispose() {
        // Libera os recursos quando a tela não é mais necessária
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
