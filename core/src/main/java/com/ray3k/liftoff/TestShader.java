package com.ray3k.liftoff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import static com.ray3k.liftoff.Core.*;

public class TestShader implements Test {
    private Sprite head;
    private Animation<TextureRegion> headAnimation;
    private Sprite donutSprite;
    private float animationTime;
    private boolean chewed;
    private Texture mask;
    private ShaderProgram shader;
    private ShaderProgram defaultShader;
    
    @Override
    public void prep() {
        chewed = false;
    
        head = new Sprite(skin.getRegion("head-stationary"));
        
        Array<TextureRegion> textures = new Array<>(skin.getRegions("head"));
        textures.addAll(skin.getRegions("head"));
        headAnimation = new Animation<>(.05f, textures, PlayMode.LOOP);
        animationTime = Float.MAX_VALUE;
    
        donutSprite = new Sprite(skin.getRegion("donut"));
        
        mask = new Texture(Gdx.files.internal("donut-full-mask.png"));
        mask.bind(1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        
        shader =new ShaderProgram(Gdx.files.internal("vertex.glsl"), Gdx.files.internal("fragment.glsl"));
        if (!shader.isCompiled()) {
            Gdx.app.log("Shader", shader.getLog());
            Gdx.app.exit();
        }
        
        shader.bind();
        shader.setUniformi("u_texture", 0);
        shader.setUniformi("u_mask", 1);
        
        defaultShader = spriteBatch.getShader();
    }
    
    @Override
    public void act(float delta) {
        float x = Gdx.input.getX();
        float y = stage.getHeight() - Gdx.input.getY();
        
        head.setPosition(x - 185, y - 70);
    
        donutSprite.setPosition(stage.getWidth() - donutSprite.getRegionWidth(), stage.getHeight() / 2 - donutSprite.getRegionHeight() / 2f);
    
        animationTime += delta;
        if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
            if (donutSprite.getBoundingRectangle().contains(x, y)) {
                animationTime = 0;
                chewed = true;
                chomp.play();
            }
        }
    
        stage.act(delta);
        background.update(delta);
    }
    
    @Override
    public void draw() {
        ScreenUtils.clear(Color.BLACK);
        
        spriteBatch.begin();
        background.draw(stage.getBatch(), 0, 0, stage.getWidth(), stage.getHeight());
    
        if (!headAnimation.isAnimationFinished(animationTime)) {
            spriteBatch.draw(headAnimation.getKeyFrame(animationTime), Gdx.input.getX() - 185,
                    Gdx.graphics.getHeight() - Gdx.input.getY() - 70);
        } else {
            head.draw(spriteBatch);
        }
    
        if (!chewed) {
            donutSprite.draw(spriteBatch);
        } else {
            spriteBatch.flush();
            spriteBatch.setShader(shader);
            donutSprite.draw(spriteBatch);
        }
    
        spriteBatch.end();
        spriteBatch.setShader(defaultShader);
        stage.draw();
    }
}
