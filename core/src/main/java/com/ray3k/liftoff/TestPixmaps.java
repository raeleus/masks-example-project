package com.ray3k.liftoff;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import static com.ray3k.liftoff.Core.*;

public class TestPixmaps implements Test {
    private Sprite head;
    private Animation<TextureRegion> headAnimation;
    private Sprite donutSprite;
    private float animationTime;
    Pixmap donutPixmap;
    private Pixmap result;
    private Pixmap mask;
    private Array<Point> points;
    
    @Override
    public void prep() {
        head = new Sprite(skin.getRegion("head-stationary"));
        
        Array<TextureRegion> textures = new Array<>(skin.getRegions("head"));
        textures.addAll(skin.getRegions("head"));
        headAnimation = new Animation<>(.05f, textures, PlayMode.LOOP);
        animationTime = Float.MAX_VALUE;
        
        donutSprite = new Sprite(skin.getRegion("donut"));
    
        points = new Array<>();
    
        FileHandle fileHandle = Gdx.files.internal("donut.png");
        donutPixmap = new Pixmap(fileHandle);
        mask = new Pixmap(Gdx.files.internal("bite-black.png"));
        result = new Pixmap(donutPixmap.getWidth(), donutPixmap.getHeight(), Format.RGBA4444);
        updateDonut();
    }
    
    private void updateDonut() {
        result.setBlending(Blending.SourceOver);
        result.setColor(new Color(0f, 0f, 0f, 0f));
        result.fill();
        for (Point point : points) {
            result.drawPixmap(mask, point.x - mask.getWidth() / 2, point.y - mask.getHeight() / 2);
        }

        result.setBlending(Blending.None);
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                result.drawPixel(x, y, donutPixmap.getPixel(x, y) & ~result.getPixel(x, y));
            }
        }

        Sprite newDonutSprite = new Sprite(new Texture(result));
        if (donutSprite != null) newDonutSprite.setPosition(donutSprite.getX(), donutSprite.getY());
        donutSprite = newDonutSprite;
    }
    
    @Override
    public void act(float delta) {
        int x = Gdx.input.getX();
        int y = MathUtils.round(stage.getHeight()) - Gdx.input.getY();
        
        head.setPosition(x - 185, y - 70);
        
        donutSprite.setPosition(stage.getWidth() - donutSprite.getRegionWidth(),
                stage.getHeight() / 2 - donutSprite.getRegionHeight() / 2f);
        
        animationTime += delta;
        if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
            if (donutSprite.getBoundingRectangle().contains(x, y)) {
                animationTime = 0;
                chomp.play();
                points.add(new Point(x - MathUtils.round(donutSprite.getX()), Gdx.input.getY() - MathUtils.round(donutSprite.getY())));
                updateDonut();
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
        
        donutSprite.draw(spriteBatch);
        
        spriteBatch.end();
        stage.draw();
    }
    
    private static class Point {
        public int x;
        public int y;
        
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
