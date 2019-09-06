package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.*;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

public class DucklingEntity extends EnemyEntity {
    private static SkeletonData skeletonData;
    private static AnimationStateData animationStateData;
    private static final float MOVE_SPEED = 100;
    private final static float GRAVITY = -1500f;
    private final static float BOTTOM_BORDER = 15;
    private final static float DAMAGE = 5000;
    private static final Vector2 temp = new Vector2();
    private static final Rectangle rectTemp = new Rectangle();
    private static Animation deathAnimation;
    private static Animation runAnimation;
    private static Animation hurtAnimation;
    private static Animation attackAnimation;
    private boolean hitBottom;
    private boolean attacking;
    private Bone attackBone;
    private static EventData attackEvent;
    private static EventData chargeEvent;
    
    @Override
    public void create() {
        gravityY = GRAVITY;
        health = 50;
        score = 70;
        
        if (skeletonData == null) {
            skeletonData = core.assetManager.get("spine/duck.json");
            animationStateData = new AnimationStateData(skeletonData);
            animationStateData.setDefaultMix(0);
            
            deathAnimation = skeletonData.findAnimation("die");
            runAnimation = skeletonData.findAnimation("run");
            hurtAnimation = skeletonData.findAnimation("hurt");
            attackAnimation = skeletonData.findAnimation("attack");
            
            attackEvent = skeletonData.findEvent("attack");
            chargeEvent = skeletonData.findEvent("charge");
        }
        
        setSkeletonData(skeletonData, animationStateData);
        skeletonBounds.update(skeleton, true);
        Utils.setRectToSkeletonBounds(getBbox(), skeletonBounds);
        animationState.setAnimation(0, runAnimation, true);
        depth = Core.ENEMY_DEPTH;
        
        if (x > GameScreen.gameViewport.getWorldWidth() / 2) deltaX = -MOVE_SPEED;
        else deltaX = MOVE_SPEED;
        
        attackBone = skeleton.findBone("attack-point");
        
        animationState.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                if (event.getData() == attackEvent) {
                    attack();
                } else if (event.getData() == chargeEvent) {
                    core.chargeSound.play();
                } else if (event.getData().getAudioPath() != null) {
                    core.laserSound.play();
                }
            }
    
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation() == attackAnimation) {
                    deltaX = skeleton.getScaleX() > 0 ? MOVE_SPEED : -MOVE_SPEED;
                    attacking = false;
                }
            }
        });
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
            if (!attacking) {
                if (deltaX < 0) skeleton.setScaleX(-1);
                else skeleton.setScaleX(1);
            }
            movementPhysics();
            checkForAttack();
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
            if (bbox.x < 0) {
                x += -bbox.x;
                deltaX *= -1;
            }
    
            if (bbox.x + bbox.width > GameScreen.gameViewport.getWorldWidth()) {
                x += GameScreen.gameViewport.getWorldWidth() - (bbox.x + bbox.width);
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
                        gravityY = 0;
                        deltaY = 0;
                        break;
                    }
                }
        
                if (inAir) gravityY = GRAVITY;
            }
    }
    
    private void checkForAttack() {
        if (PlayerEntity.playerEntity != null && !PlayerEntity.playerEntity.dying && !attacking) {
            Vector2 temp = new Vector2();
            temp.set(0, 0);
            attackBone.localToWorld(temp);
            if (Utils.rayIntersectRectangle(temp.x, temp.y, attackBone.getWorldRotationX(), PlayerEntity.playerEntity.rect, null)) {
                deltaX = 0;
                animationState.setAnimation(0, attackAnimation, false);
                animationState.addAnimation(0, runAnimation, true, 0);
                attacking = true;
            }
        }
    }
    
    private void attack() {
        Vector2 temp = new Vector2();
        temp.set(0, 0);
        attackBone.localToWorld(temp);
        
        if (PlayerEntity.playerEntity != null && !PlayerEntity.playerEntity.dying) {
            if (Utils.rayIntersectRectangle(temp.x, temp.y, attackBone.getWorldRotationX(), PlayerEntity.playerEntity.rect, null)) {
                PlayerEntity.playerEntity.kill();
            }
        }
        
        for (int i = 0; i < GameScreen.gameScreen.enemies.size; i ++) {
            EnemyEntity enemyEntity = GameScreen.gameScreen.enemies.get(i);
            if (!enemyEntity.equals(this) && Utils.rayIntersectRectangle(temp.x, temp.y, attackBone.getWorldRotationX(), enemyEntity.bbox, null)) {
                enemyEntity.hurt(DAMAGE);
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
