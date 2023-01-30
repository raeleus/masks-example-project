package com.ray3k.liftoff;

import static com.ray3k.liftoff.Core.background;
import static com.ray3k.liftoff.Core.skin;
import static com.ray3k.liftoff.Core.spriteBatch;
import static com.ray3k.liftoff.Core.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TestBlendingFunctionMoreThanOneSprite implements Test {
    private Sprite head;
    private Sprite donutSprite;
    private Sprite white, mummy, mask;
    private FrameBuffer frameBuffer;
    private ScreenViewport frameBufferViewport;
    
    @Override
    public void prep() {
        head = new Sprite(skin.getRegion("head-stationary"));
        
        Array<TextureRegion> textures = new Array<>(skin.getRegions("head"));
        textures.addAll(skin.getRegions("head"));
    
        donutSprite = new Sprite(skin.getRegion("donut"));
        mask = new Sprite(skin.getRegion("bite"));
        white = new Sprite(new TextureRegion(new Texture("whitepixel.jpg")));
        mummy = new Sprite(new TextureRegion(new Texture("mummy.png")));
        frameBuffer = new FrameBuffer(Format.RGBA4444, donutSprite.getRegionWidth(), donutSprite.getRegionHeight(), false);
        frameBufferViewport = new ScreenViewport();
        frameBufferViewport.update(frameBuffer.getWidth(), frameBuffer.getHeight(), true);
    }
    
    @Override
    public void act(float delta) {
        float x = Gdx.input.getX();
        float y = stage.getHeight() - Gdx.input.getY();
        
        head.setPosition(x - 185, y - 70);
        
        donutSprite.setPosition(stage.getWidth() - donutSprite.getRegionWidth(), stage.getHeight() / 2 - donutSprite.getRegionHeight() / 2f);
        frameBufferViewport.getCamera().position.set(donutSprite.getX() + donutSprite.getWidth() / 2, donutSprite.getY() + donutSprite.getHeight() / 2, 0);
    
        stage.act(delta);
        background.update(delta);
    }
    
    @Override
    public void draw() {
        stage.getViewport().apply();
        spriteBatch.setProjectionMatrix(stage.getCamera().combined);

        spriteBatch.begin();
        ScreenUtils.clear(Color.CLEAR);
        DrawBackgroundElements();
        DrawMask();
        DrawMasked();
        spriteBatch.end();

        DrawUI();
    }

    private void DrawBackgroundElements() {
        spriteBatch.flush();

        Gdx.gl.glColorMask(true, true, true, true);
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        background.draw(stage.getBatch(), 0, 0, stage.getWidth(), stage.getHeight());
        mummy.setCenter(250, 250);
        mummy.draw(spriteBatch);
        head.draw(spriteBatch);
    }

    private void DrawMask() {
        spriteBatch.flush();

        Gdx.gl.glColorMask(false, false, false, true);
        spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        white.setCenter(Gdx.input.getX(), stage.getHeight() - Gdx.input.getY());
        white.setSize(50, 50);
        white.setColor(Color.CLEAR);
        white.draw(spriteBatch);

        spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_ALPHA);
        white.setCenter(250, 250);
        white.setSize(250, 250);
        white.setColor(Color.WHITE);
        white.draw(spriteBatch);
    }

    private void DrawMasked() {
        spriteBatch.flush();

        Gdx.gl.glColorMask(true, true, true, true);
        spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
        white.setCenter(250, 250);
        white.setSize(250, 250);
        white.setColor(Color.WHITE);
        white.draw(spriteBatch);
        spriteBatch.flush();
    }

    private void DrawUI() {
        Gdx.gl.glColorMask(true, true, true, true);
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stage.draw();
    }


}
