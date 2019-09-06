package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

public class BatEntity  extends EnemyEntity {
    private static SkeletonData skeletonData;
    private static AnimationStateData animationStateData;
    private static Animation hurtAnimation;
    private static Animation deathAnimation;
    private static final float MOVE_SPEED = 175;
    
    @Override
    public void create() {
        health = 30;
        score = 10;
        
        if (skeletonData == null) {
            skeletonData = core.assetManager.get("spine/bat.json");
            animationStateData = new AnimationStateData(skeletonData);
            animationStateData.setDefaultMix(0);
            hurtAnimation = skeletonData.findAnimation("hurt");
            deathAnimation = skeletonData.findAnimation("die");
        }
        
        setSkeletonData(skeletonData, animationStateData);
        animationState.setAnimation(0, "run", true);
        
        depth = Core.ENEMY_DEPTH;
        Utils.setRectToSkeletonBounds(getBbox(), skeletonBounds);
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        Utils.setRectToSkeletonBounds(bbox, skeletonBounds);
        
        if (health <= 0) {
            if (bbox.y + bbox.height < 0) {
                destroy = true;
            }
        } else {
            if (PlayerEntity.playerEntity != null && !PlayerEntity.playerEntity.dying) {
                moveTowards(MOVE_SPEED, PlayerEntity.playerEntity.x, PlayerEntity.playerEntity.y);
            }
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
