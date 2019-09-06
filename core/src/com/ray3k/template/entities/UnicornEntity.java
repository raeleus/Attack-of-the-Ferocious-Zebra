package com.ray3k.template.entities;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.esotericsoftware.spine.*;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

public class UnicornEntity extends EnemyEntity {
    private static SkeletonData skeletonData;
    private static AnimationStateData animationStateData;
    private static final float MOVE_SPEED = 250;
    private final static float GRAVITY = -1500f;
    public final static float BOTTOM_BORDER = 15;
    private static final Vector2 temp = new Vector2();
    private static final Rectangle rectTemp = new Rectangle();
    public static Animation deathAnimation;
    public static Animation runAnimation;
    public static Animation hurtAnimation;
    private boolean hitBottom;
    
    @Override
    public void create() {
        health = 70;
        score = 50;
        
        if (skeletonData == null) {
            skeletonData = core.assetManager.get("spine/unicorn.json");
            animationStateData = new AnimationStateData(skeletonData);
            animationStateData.setDefaultMix(0);
            
            deathAnimation = skeletonData.findAnimation("die");
            runAnimation = skeletonData.findAnimation("run");
            hurtAnimation = skeletonData.findAnimation("hurt");
        }
        
        setSkeletonData(skeletonData, animationStateData);
        skeletonBounds.update(skeleton, true);
        Utils.setRectToSkeletonBounds(getBbox(), skeletonBounds);
        animationState.setAnimation(0, runAnimation, true);
        depth = Core.ENEMY_DEPTH;
        
        if (x > GameScreen.gameViewport.getWorldWidth() / 2) deltaX = -MOVE_SPEED;
        else deltaX = MOVE_SPEED;
    }
    
    @Override
    public void actBefore(float delta) {
        if (health > 0) {
            movementPhysicsBefore(delta);
        }
    }
    
    @Override
    public void act(float delta) {
        bbox.set(skeletonBounds.getMinX(), skeletonBounds.getMinY(), skeletonBounds.getWidth(), skeletonBounds.getHeight());
        
        if (health > 0) {
            if (deltaX < 0) skeleton.setScaleX(-1);
            else skeleton.setScaleX(1);
            movementPhysics();
        } else {
            if (bbox.y + bbox.height < 0) {
                destroy = true;
            }
        }
    }
    
    private void movementPhysicsBefore(float delta) {
        if (deltaY < 0) {
            rectTemp.set(bbox);
            rectTemp.x += (deltaX + gravityX * delta) * delta;
            rectTemp.y += (deltaY + gravityY * delta) * delta;
        
            for (Rectangle other : GameScreen.roomEntity.rects) {
                if (bbox.y > other.y + other.height && rectTemp.overlaps(other)) {
                    y -= bbox.y - other.getY() - other.getHeight();
                    deltaY = 0;
                    gravityY = 0;
                
                    break;
                }
            }
        }
    }
    
    private void movementPhysics() {
        if (!hitBottom) {
            if (x < 0) {
                x = 0;
                deltaX *= -1;
            }
    
            if (x > GameScreen.gameViewport.getWorldWidth()) {
                x = GameScreen.gameViewport.getWorldWidth();
                deltaX *= -1;
            }
        } else {
            if (bbox.x + bbox.width < 0) {
                destroy = true;
            } else if (bbox.x > GameScreen.gameViewport.getWorldWidth()) {
                destroy = true;
            }
        }
    
        if (y < BOTTOM_BORDER) {
            y = BOTTOM_BORDER;
            hitBottom = true;
            deltaY = 0;
            gravityY = 0;
        }
    
        if (!MathUtils.isEqual(y, BOTTOM_BORDER)) {
            boolean inAir = true;
        
            rectTemp.set(bbox);
            rectTemp.y--;
            for (Rectangle other : GameScreen.roomEntity.rects) {
                if (other.overlaps(rectTemp)) {
                    inAir = false;
                    break;
                }
            }
        
            if (inAir) gravityY = GRAVITY;
        }
    }

    @Override
    public void draw(float delta) {

    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void hurtAction(float damage) {
        animationState.setAnimation(1, hurtAnimation, false);
    }
    
    @Override
    public void death() {
        animationState.setAnimation(0, deathAnimation, false);
        setMotion(500, MathUtils.random(70, 110));
        setGravity(1000, 270);
        GameScreen.gameScreen.score += score;
    }
}
