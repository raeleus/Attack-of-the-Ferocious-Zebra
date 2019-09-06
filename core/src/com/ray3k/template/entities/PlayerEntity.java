package com.ray3k.template.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.PointAttachment;
import com.ray3k.template.screens.GameScreen;

public class PlayerEntity extends Entity {
    private static SkeletonData skeletonData;
    private static AnimationStateData animationStateData;
    private static Animation standAnimation;
    private static Animation runAnimation;
    private static Animation runBackwardAnimation;
    private static Animation jumpAnimation;
    private static Animation landAnimation;
    private static Animation dieAnimation;
    private static Animation reloadAnimation;
    private static Animation shootAnimation;
    private static EventData jumpEvent;
    private static EventData shootEvent;
    private static EventData rifleEvent;
    private static EventData jumpSoundEvent;
    private static EventData landEvent;
    private static Array<Skin> flashSkins;
    private static final float MOVE_SPEED = 200f;
    private  final static float JUMP_POWER = 700f;
    private final static float GRAVITY = -1500f;
    public final static float BOTTOM_BORDER = 15;
    public final static float TOP_BORDER = 561;
    private Bone aimBone;
    private static final Vector2 temp = new Vector2();
    private boolean inAir;
    public Rectangle rect;
    private static final Rectangle rectTemp = new Rectangle();
    private boolean canShoot;
    public int ammo;
    private float reloadTimer;
    public static final int MAX_AMMO = 30;
    public static final float RELOAD_TIME = 2f;
    public static final float FAST_RELOAD_SUCCESS = 1.0f;
    public static final float FAST_RELOAD_FAILURE = 1.0f;
    private boolean fastReload;
    private boolean lastRightButton;
    private float minFastReload;
    public static final float FAST_RELOAD_SPAN =.3f;
    private Slot gunSlot;
    private static final Vector3 v3Temp1 = new Vector3();
    private static final Vector3 v3Temp2 = new Vector3();
    private static final BoundingBox bboxTemp = new BoundingBox(v3Temp1, v3Temp2);
    private static final Ray rayTemp = new Ray(v3Temp1,  v3Temp2);
    public boolean dying;
    public static PlayerEntity playerEntity;
    public static TextureRegion whiteRegion;
    
    @Override
    public void create() {
        playerEntity = this;
        
        if (skeletonData == null) {
            skeletonData = core.assetManager.get("spine/zebra.json");
            standAnimation = skeletonData.findAnimation("stand");
            runAnimation = skeletonData.findAnimation("run");
            jumpAnimation = skeletonData.findAnimation("jump");
            landAnimation = skeletonData.findAnimation("land");
            runBackwardAnimation = skeletonData.findAnimation("run-backward");
            dieAnimation = skeletonData.findAnimation("die");
            reloadAnimation = skeletonData.findAnimation("reload");
            shootAnimation = skeletonData.findAnimation("shoot");
            
            jumpEvent = skeletonData.findEvent("jump");
            shootEvent = skeletonData.findEvent("shoot");
            rifleEvent = skeletonData.findEvent("rifle");
            jumpSoundEvent = skeletonData.findEvent("jump-sound");
            landEvent = skeletonData.findEvent("land");
            
            flashSkins = new Array<>();
            flashSkins.add(skeletonData.findSkin("flash-0"));
            flashSkins.add(skeletonData.findSkin("flash-1"));
            flashSkins.add(skeletonData.findSkin("flash-2"));
            flashSkins.add(skeletonData.findSkin("flash-3"));
            flashSkins.add(skeletonData.findSkin("flash-4"));
            
            animationStateData = new AnimationStateData(skeletonData);
            animationStateData.setDefaultMix(.25f);
            animationStateData.setMix(runAnimation, jumpAnimation, 0);
            animationStateData.setMix(standAnimation, jumpAnimation, 0);
            animationStateData.setMix(jumpAnimation, landAnimation, 0);
            animationStateData.setMix(shootAnimation, shootAnimation, 0);
            
            whiteRegion = core.textureAtlas.findRegion("white");
        }
        
        setSkeletonData(skeletonData, animationStateData);
        skeletonBounds.update(skeleton, true);
        rect = new Rectangle(skeletonBounds.getMinX(), skeletonBounds.getMinY(), skeletonBounds.getWidth(), skeletonBounds.getHeight());
        
        aimBone = skeleton.findBone("arm-front-ik");
        gunSlot = skeleton.findSlot("gun");
        
        animationState.setAnimation(0, standAnimation, true);
        skeleton.setSkin(flashSkins.random());
        depth = core.PLAYER_DEPTH;
        
        ammo = MAX_AMMO;
        reloadTimer = -1;
        
        animationState.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                if (event.getData() == jumpEvent) {
                    deltaY = JUMP_POWER;
                    gravityY = GRAVITY;
                } else if (event.getData() == shootEvent) {
                
                } else if (event.getData() == rifleEvent) {
                    core.rifleSound.play();
                } else if (event.getData() == jumpSoundEvent) {
                    core.jumpSound.play();
                } else if (event.getData() == landEvent) {
                    core.landSound.play();
                }
            }
    
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().equals(shootAnimation)) {
                    canShoot = true;
                }
            }
        });
        canShoot = true;
    }
    
    @Override
    public void actBefore(float delta) {
        if (!dying) {
            movementPhysicsBefore(delta);
        } else {
            movementPhysicsBeforeDead(delta);
        }
    }
    
    @Override
    public void act(float delta) {
        rect.set(skeletonBounds.getMinX(), skeletonBounds.getMinY(), skeletonBounds.getWidth(), skeletonBounds.getHeight());
        
        if (!dying) {
            mouseControls();
    
            movementPhysics();
    
            collisionDetection();
    
            gunControls(delta);
        }
    }
    
    private void mouseControls() {
        temp.set(core.mouseX, core.mouseY);
        aimBone.getParent().worldToLocal(temp);
        aimBone.setPosition(temp.x, temp.y);
        
        if (core.mouseX < x) {
            skeleton.setScaleX(-1);
        } else {
            skeleton.setScaleX(1);
        }
    }
    
    private void movementPhysicsBefore(float delta) {
        if (inAir) {
            if (deltaY < 0) {
                rectTemp.set(rect);
                rectTemp.x += (deltaX + gravityX * delta) * delta;
                rectTemp.y += (deltaY + gravityY * delta) * delta;
                
                for (Rectangle other : GameScreen.roomEntity.rects) {
                    if (rect.y > other.y + other.height && rectTemp.overlaps(other)) {
                        y -= rect.y - other.getY() - other.getHeight();
                        deltaY = 0;
                        gravityY = 0;
                        inAir = false;
                        animationState.setAnimation(0, landAnimation, false);
                        break;
                    }
                }
            }
        }
    }
    
    private void movementPhysicsBeforeDead(float delta) {
        if (deltaY < 0) {
            rectTemp.set(rect);
            rectTemp.x += (deltaX + gravityX * delta) * delta;
            rectTemp.y += (deltaY + gravityY * delta) * delta;
            
            for (Rectangle other : GameScreen.roomEntity.rects) {
                if (rect.y > other.y + other.height && rectTemp.overlaps(other)) {
                    y -= rect.y - other.getY() - other.getHeight();
                    deltaX = 0;
                    deltaY = 0;
                    gravityY = 0;
                    break;
                }
            }
        }
    }
    
    private void movementPhysics() {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (!inAir) {
                if (core.mouseX < x) {
                    if (!animationState.getCurrent(0).getAnimation().equals(runBackwardAnimation)) {
                        animationState.setAnimation(0, runBackwardAnimation, true);
                    }
                } else {
                    if (!animationState.getCurrent(0).getAnimation().equals(runAnimation)) {
                        animationState.setAnimation(0, runAnimation, true);
                    }
                }
            }
        
            deltaX = MOVE_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (!inAir) {
                if (core.mouseX < x) {
                    if (!animationState.getCurrent(0).getAnimation().equals(runAnimation)) {
                        animationState.setAnimation(0, runAnimation, true);
                    }
                } else {
                    if (!animationState.getCurrent(0).getAnimation().equals(runBackwardAnimation)) {
                        animationState.setAnimation(0, runBackwardAnimation, true);
                    }
                }
            }
            deltaX = -MOVE_SPEED;
        } else {
            if (!inAir) {
                animationState.setAnimation(0, "stand", true);
            }
            deltaX = 0;
        }
    
        if ((Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) && !inAir) {
            inAir = true;
            if (!animationState.getCurrent(0).getAnimation().equals(jumpAnimation)) {
                animationState.setAnimation(0, jumpAnimation, false);
            }
        }
    
        if (x < 0) {
            x = 0;
            deltaX = 0;
        }
    
        if (x > GameScreen.gameViewport.getWorldWidth()) {
            x = GameScreen.gameViewport.getWorldWidth();
            deltaX = 0;
        }
    
        if (inAir) {
            if (y < BOTTOM_BORDER) {
                y = BOTTOM_BORDER;
                deltaY = 0;
                gravityY = 0;
                inAir = false;
                animationState.setAnimation(0, landAnimation, false);
            } else if (y + rect.height > TOP_BORDER) {
                y = TOP_BORDER - rect.height;
                deltaY = 0;
            }
        } else {
            if (!MathUtils.isEqual(y, BOTTOM_BORDER)) {
                inAir = true;
            
                if (!Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.S)) {
                    rectTemp.set(rect);
                    rectTemp.y--;
                    for (Rectangle other : GameScreen.roomEntity.rects) {
                        if (other.overlaps(rectTemp)) {
                            inAir = false;
                            break;
                        }
                    }
                }
            
                if (inAir) gravityY = GRAVITY;
            }
        }
    }
    
    private void collisionDetection() {
        for (EnemyEntity enemyEntity : GameScreen.gameScreen.enemies) {
            if (enemyEntity.health > 0 && enemyEntity.bbox.overlaps(rect)) {
                kill();
                break;
            }
        }
    }
    
    public void kill() {
        dying = true;
        animationState.setAnimation(0, dieAnimation, false);
        setMotion(200, MathUtils.random(70, 110));
        setGravity(2000, 270);
        core.dieSound.play();
        GameScreen.gameScreen.createRetryDialog();
        Gdx.graphics.setCursor(core.reticleCursor);
        GameScreen.gameScreen.enemyController.spawn = false;
    }
    
    private void gunControls(float delta) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (canShoot && ammo > 0) {
                skeleton.setSkin(flashSkins.random());
                animationState.setAnimation(1, shootAnimation, false);
                canShoot = false;
                ammo--;
                GameScreen.gameScreen.refreshUI();
            
                bulletCollisionDetection();
            } else if (canShoot) {
                core.dryFireSound.play();
                canShoot = false;
                GameScreen.gameScreen.stage.addAction(Actions.delay(.25f, Actions.run(() -> {
                    canShoot = true;
                })));
            }
        }
    
        if (reloadTimer > 0) {
            reloadTimer -= delta;
        
            if (!fastReload && (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.R)) && !lastRightButton) {
                if (reloadTimer >= minFastReload && reloadTimer <= minFastReload + FAST_RELOAD_SPAN) {
                    reloadTimer -= FAST_RELOAD_SUCCESS;
                    fastReload = true;
                    core.successSound.play();
                } else {
                    reloadTimer += FAST_RELOAD_FAILURE;
                    fastReload = true;
                    core.errorSound.play();
                }
            }
        
            if (reloadTimer <= 0) {
                reloadTimer = -1;
                ammo = MAX_AMMO;
                GameScreen.gameScreen.refreshUI();
                core.reloadSound.play();
                Gdx.graphics.setCursor(core.reticleCursor);
            }
        } else {
            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.R)) {
                reloadTimer = RELOAD_TIME;
                ammo = 0;
                fastReload = false;
                minFastReload = MathUtils.random(FAST_RELOAD_SUCCESS, RELOAD_TIME - FAST_RELOAD_SPAN);
                GameScreen.gameScreen.refreshUI();
                core.reloadStartSound.play();
                Gdx.graphics.setCursor(core.noneCursor);
            }
        }
    
        lastRightButton = Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.R);
    }
    
    private void bulletCollisionDetection() {
        temp.set(0, 0);
        ((PointAttachment) gunSlot.getAttachment()).computeWorldPosition(gunSlot.getBone(), temp);
        v3Temp1.set(temp, 0);
        v3Temp2.set(core.mouseX, core.mouseY, 0);
        v3Temp2.sub(temp.x, temp.y, 0);
        v3Temp2.nor();
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
                    }
                }
            }
        }
        
        if (ricochetEntity != null) {
            GameScreen.gameScreen.entityController.add(ricochetEntity);
        }
        
        if (selectedEnemy != null) {
            selectedEnemy.hurt(10);
        }
    }
    
    @Override
    public void draw(float delta) {
//        core.shapeDrawer.setColor(Color.WHITE);
//        core.shapeDrawer.setDefaultLineWidth(2);
//        core.shapeDrawer.rectangle(rect);
        
        if (reloadTimer > 0) {
            core.shapeDrawer.setColor(Color.RED);
            core.shapeDrawer.setDefaultLineWidth(5);
            core.shapeDrawer.setTextureRegion(whiteRegion);
            float maxAngle = Math.min(reloadTimer / RELOAD_TIME, 1) * MathUtils.PI2;
            core.shapeDrawer.arc(core.mouseX, core.mouseY, 25, 0, maxAngle);
            if (!fastReload && reloadTimer > minFastReload) {
                core.shapeDrawer.setColor(Color.WHITE);
                core.shapeDrawer.arc(core.mouseX, core.mouseY, 25, minFastReload / RELOAD_TIME * MathUtils.PI2, Math.min(FAST_RELOAD_SPAN / RELOAD_TIME * MathUtils.PI2, (reloadTimer - minFastReload) / RELOAD_TIME * MathUtils.PI2));
            }
        }
    }
    
    @Override
    public void destroy() {
    
    }
}
