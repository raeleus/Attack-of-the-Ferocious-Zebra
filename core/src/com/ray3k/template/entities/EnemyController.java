package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.ray3k.template.screens.GameScreen;

public class EnemyController extends Entity {
    private GameScreen gameScreen;
    private Stage stage;
    public float batRate;
    public float batRateDelta;
    public float unicornRate;
    public float unicornRateDelta;
    public float ducklingRate;
    public float ducklingRateDelta;
    public boolean spawnBats;
    public boolean spawn;
    
    @Override
    public void create() {
        spawnBats = true;
        spawn = true;
        gameScreen = GameScreen.gameScreen;
        stage = gameScreen.stage;
    
        batRate = 2f;
        batRateDelta = .002f;
        
        unicornRate = 7f;
        unicornRateDelta = .002f;
    
        ducklingRate = 5f;
        unicornRateDelta = .002f;
        
        stage.addAction(createBatAction(14));
        stage.addAction(createUnicornAction(4));
        stage.addAction(createDucklingAction(25));
        stage.addAction(createUnityAction(50));
    
//        stage.addAction(createDucklingAction(4));
//        stage.addAction(createUnityAction(4));
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        batRate -= batRateDelta * delta;
        unicornRate -= unicornRateDelta * delta;
        ducklingRate -= ducklingRateDelta * delta;
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
    
    private Action createBatAction(float rate) {
        return Actions.delay(rate, Actions.run(() -> {
            if (spawnBats && spawn) {
                BatEntity batEntity = new BatEntity();
                float chance = MathUtils.random();
                if (chance > 2 / 3f) {
                    batEntity.setPosition(GameScreen.gameViewport.getWorldWidth(), MathUtils.random(GameScreen.gameViewport.getWorldHeight()));
                } else if (chance > 1 / 3f) {
                    batEntity.setPosition(0, MathUtils.random(GameScreen.gameViewport.getWorldHeight()));
                } else {
                    batEntity.setPosition(MathUtils.random(GameScreen.gameViewport.getWorldWidth()), GameScreen.gameViewport.getWorldHeight());
                }
                gameScreen.entityController.add(batEntity);
                gameScreen.enemies.add(batEntity);
            
                if (PlayerEntity.playerEntity != null && !PlayerEntity.playerEntity.dying) {
                    stage.addAction(createBatAction(batRate));
                }
            }
        }));
    }
    
    private Action createUnicornAction(float rate) {
        return Actions.delay(rate, Actions.run(() -> {
            if (spawn) {
                UnicornEntity entity = new UnicornEntity();
                entity.setPosition(MathUtils.random(GameScreen.gameViewport.getWorldWidth()), GameScreen.gameViewport.getWorldHeight());
                gameScreen.entityController.add(entity);
                gameScreen.enemies.add(entity);
    
                if (PlayerEntity.playerEntity != null && !PlayerEntity.playerEntity.dying) {
                    stage.addAction(createUnicornAction(unicornRate));
                }
            }
        }));
    }
    
    private Action createDucklingAction(float rate) {
        return Actions.delay(rate, Actions.run(() -> {
            if (spawn) {
                DucklingEntity entity = new DucklingEntity();
                entity.setPosition(MathUtils.random(GameScreen.gameViewport.getWorldWidth()), GameScreen.gameViewport.getWorldHeight());
                gameScreen.entityController.add(entity);
                gameScreen.enemies.add(entity);
    
                if (PlayerEntity.playerEntity != null && !PlayerEntity.playerEntity.dying) {
                    stage.addAction(createDucklingAction(ducklingRate));
                }
            }
        }));
    }
    
    private Action createUnityAction(float rate) {
        return Actions.delay(rate, Actions.run(() -> {
            if (spawn) {
                spawnBats = false;
                UnityEntity entity = new UnityEntity();
                entity.setPosition(MathUtils.random(GameScreen.gameViewport.getWorldWidth()), GameScreen.gameViewport.getWorldHeight());
                gameScreen.entityController.add(entity);
                gameScreen.enemies.add(entity);
            }
        }));
    }
}
