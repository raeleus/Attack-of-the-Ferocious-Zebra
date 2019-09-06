package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

public class UnityAimEntity extends Entity {
    @Override
    public void create() {
        setMotion(500, MathUtils.random(360f));
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        boolean newDirection = false;
    
        if (x < 0) {
            x = 0;
            newDirection = true;
        }
    
        if (x > GameScreen.gameViewport.getWorldWidth()) {
            x = GameScreen.gameViewport.getWorldWidth();
            newDirection = true;
        }
    
        if (y < 0) {
            y = 0;
            newDirection = true;
        }
    
        if (y > GameScreen.gameViewport.getWorldHeight()) {
            y = GameScreen.gameViewport.getWorldHeight();
            newDirection = true;
        }
    
        if (newDirection) {
            if (PlayerEntity.playerEntity != null) {
                setMotion(500, Utils.pointDirection(x, y, PlayerEntity.playerEntity.x, PlayerEntity.playerEntity.y));
            }
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
