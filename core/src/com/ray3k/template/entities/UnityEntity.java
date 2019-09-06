package com.ray3k.template.entities;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.PointAttachment;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

public class UnityEntity extends EnemyEntity {
    private static SkeletonData skeletonData;
    private static AnimationStateData animationStateData;
    private static Animation deathAnimation;
    private static Animation attackAnimation;
    private static Animation runAnimation;
    private static final float MOVE_SPEED = 400;
    private static EventData explosionEvent;
    private static EventData pistolEvent;
    private static EventData popEvent;
    private Array<Vector2> targets;
    private Vector2 target;
    private float moveTimer;
    private static final float MOVE_DELAY = 10;
    private float shootTimer;
    private Vector2 aimTarget;
    private UnityAimEntity unityAimEntity;
    private Bone targetBone;
    private static final Vector2 temp = new Vector2();
    private boolean showTracer;
    private Vector2 tracerBegin;
    private Vector2 tracerEnd;
    private static Animation hurtAnimation;
    
    @Override
    public void create() {
        health = 1000;
        score = 1000;
        
        if (skeletonData == null) {
            skeletonData = core.assetManager.get("spine/unity.json");
            animationStateData = new AnimationStateData(skeletonData);
            animationStateData.setDefaultMix(0);
            deathAnimation = skeletonData.findAnimation("die");
            attackAnimation = skeletonData.findAnimation("attack");
            runAnimation = skeletonData.findAnimation("run");
            hurtAnimation = skeletonData.findAnimation("hurt");
            
            explosionEvent = skeletonData.findEvent("explosion");
            pistolEvent = skeletonData.findEvent("pistol");
            popEvent = skeletonData.findEvent("pop");
        }
        
        setSkeletonData(skeletonData, animationStateData);
        animationState.setAnimation(0, "run", true);
        
        depth = Core.ENEMY_DEPTH;
        Utils.setRectToSkeletonBounds(getBbox(), skeletonBounds);
        
        animationState.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                if (event.getData() == explosionEvent) {
                    core.explosionSound.play();
                } else if (event.getData() == pistolEvent) {
                    core.pistolSound.play();
                } else if (event.getData() == popEvent) {
                    core.popSound.play();
                    destroy = true;
                    GameScreen.gameScreen.stage.addAction(Actions.delay(2, Actions.run(() -> {
                        core.concedeSound.play();
                        GameScreen.gameScreen.createWinDialog();
                    })));
                }
            }
        });
        
        targets = new Array<>();
        targets.add(new Vector2(143, 445));
        targets.add(new Vector2(870, 445));
        targets.add(new Vector2(513, 203));
        targets.add(new Vector2(82, 84));
        targets.add(new Vector2(936, 84));
        targets.add(new Vector2(515, 445));
        targets.add(new Vector2(70, 299));
        targets.add(new Vector2(950, 299));
        
        aimTarget = new Vector2();
        
        core.unitySound.play();
        
        unityAimEntity = new UnityAimEntity();
        GameScreen.gameScreen.entityController.add(unityAimEntity);
        targetBone = skeleton.findBone("gun-target");
        gunSlot = skeleton.findSlot("gun");
        
        tracerBegin = new Vector2();
        tracerEnd = new Vector2();
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        if (health > 0) {
            moveTimer -= delta;
            if (moveTimer <= 0) {
                moveTimer = MOVE_DELAY;
                target = targets.random();
                shootTimer = 2f;
            }
    
            moveTowards(MOVE_SPEED, target.x, target.y, delta);
    
            temp.set(unityAimEntity.x, unityAimEntity.y);
            targetBone.getParent().worldToLocal(temp);
            targetBone.setPosition(temp.x, temp.y);
            
            shootTimer -= delta;
            if (shootTimer <= 0) {
                if (!PlayerEntity.playerEntity.dying) {
                    shootTimer = MathUtils.random(.25f, .5f);
                    animationState.setAnimation(0, attackAnimation, false);
                    bulletCollisionDetection();
                }
            }
        }
        
        Utils.setRectToSkeletonBounds(bbox, skeletonBounds);
    }
    
    private Slot gunSlot;
    private static final Vector3 v3Temp1 = new Vector3();
    private static final Vector3 v3Temp2 = new Vector3();
    private static final Ray rayTemp = new Ray(v3Temp1,  v3Temp2);
    private static final BoundingBox bboxTemp = new BoundingBox(v3Temp1, v3Temp2);
    
    private void bulletCollisionDetection() {
        showTracer = true;
        GameScreen.gameScreen.stage.addAction(Actions.delay(.25f, Actions.run(() -> {
            showTracer = false;
        })));
        temp.set(0, 0);
        ((PointAttachment) gunSlot.getAttachment()).computeWorldPosition(gunSlot.getBone(), temp);
        tracerBegin.x = temp.x;
        tracerBegin.y = temp.y;
        v3Temp1.set(temp, 0);
        v3Temp2.set(unityAimEntity.x, unityAimEntity.y, 0);
        v3Temp2.sub(temp.x, temp.y, 0);
        v3Temp2.nor();
        tracerEnd.x = v3Temp2.x;
        tracerEnd.y = v3Temp2.y;
        tracerEnd.setLength(2000);
        tracerEnd.add(tracerBegin);
        rayTemp.set(v3Temp1, v3Temp2);
        
        Entity ricochetEntity = null;
        EnemyEntity selectedEnemy = null;
        float distance = -1;
        
        for (Rectangle other : GameScreen.roomEntity.rects) {
            v3Temp1.set(other.x, other.y, 0);
            v3Temp2.set(other.x + other.width, other.y + other.height, 0);
            bboxTemp.set(v3Temp1, v3Temp2);
            if (Intersector.intersectRayBounds(rayTemp, bboxTemp, v3Temp1)) {
                v3Temp2.set(v3Temp1).sub(temp.x, temp.y, 0);
                if (distance == -1 || v3Temp2.len() < distance) {
                    distance = v3Temp2.len();
                    if (ricochetEntity == null) ricochetEntity = new RicochetEntity();
                    ricochetEntity.setPosition(v3Temp1.x, v3Temp1.y);
                    tracerEnd.set(v3Temp1.x, v3Temp1.y);
                }
            }
        }
        
        for (EnemyEntity enemyEntity : GameScreen.gameScreen.enemies) {
            Rectangle other = enemyEntity.bbox;
            v3Temp1.set(other.x, other.y, 0);
            v3Temp2.set(other.x + other.width, other.y + other.height, 0);
            bboxTemp.set(v3Temp1, v3Temp2);
            if (Intersector.intersectRayBounds(rayTemp, bboxTemp, v3Temp1)) {
                v3Temp2.set(v3Temp1).sub(temp.x, temp.y, 0);
                if (distance == -1 || v3Temp2.len() < distance) {
                    if (enemyEntity.health > 0) {
                        selectedEnemy = enemyEntity;
                        if (ricochetEntity != null && ricochetEntity instanceof RicochetEntity) ricochetEntity = null;
                        distance = v3Temp2.len();
                        if (ricochetEntity == null) {
                            ricochetEntity = enemyEntity instanceof UnityEntity ? new RicochetMetalEntity() : new GoreEntity();
                        }
                        ricochetEntity.setPosition(v3Temp1.x, v3Temp1.y);
                        tracerEnd.set(v3Temp1.x, v3Temp1.y);
                    }
                }
            }
        }
    
        Rectangle other = PlayerEntity.playerEntity.rect;
        v3Temp1.set(other.x, other.y, 0);
        v3Temp2.set(other.x + other.width, other.y + other.height, 0);
        bboxTemp.set(v3Temp1, v3Temp2);
        if (Intersector.intersectRayBounds(rayTemp, bboxTemp, v3Temp1)) {
            v3Temp2.set(v3Temp1).sub(temp.x, temp.y, 0);
            if (distance == -1 || v3Temp2.len() < distance) {
                selectedEnemy = null;
                ricochetEntity = new GoreEntity();
                ricochetEntity.setPosition(v3Temp1.x, v3Temp1.y);
                tracerEnd.set(v3Temp1.x, v3Temp1.y);
                PlayerEntity.playerEntity.kill();
            }
        }
        
        if (ricochetEntity != null) {
            GameScreen.gameScreen.entityController.add(ricochetEntity);
        }
        
        if (selectedEnemy != null && !selectedEnemy.equals(this)) {
            selectedEnemy.hurt(5000);
        }
    }
    
    @Override
    public void draw(float delta) {
        if (showTracer) {
            core.shapeDrawer.setColor(Color.RED);
            core.shapeDrawer.setDefaultLineWidth(2);
            core.shapeDrawer.line(tracerBegin.x, tracerBegin.y, tracerEnd.x, tracerEnd.y);
        }
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
        for (EnemyEntity enemyEntity : GameScreen.gameScreen.enemies) {
            if (!enemyEntity.equals(this)) {
                enemyEntity.hurt(1000);
                GameScreen.gameScreen.enemyController.spawn = false;
            }
        }
        animationState.setAnimation(0, deathAnimation, false);
        setSpeed(0);
        GameScreen.gameScreen.score *= score;
    }
}
