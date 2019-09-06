package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;
import com.ray3k.template.entities.*;

import java.util.Iterator;

public class GameScreen extends JamScreen {
    private Action quitAction;
    private Action winAction;
    private Action loseAction;
    public Stage stage;
    private Skin skin;
    private Core core;
    private Image fg;
    private Music music;
    public EntityController entityController;
    private final static Color BG_COLOR = new Color(Color.WHITE);
    public static FitViewport gameViewport;
    public static RoomEntity roomEntity;
    private final static Vector3 tempVector3 = new Vector3();
    public static GameScreen gameScreen;
    private PlayerEntity playerEntity;
    public Table root;
    public Array<EnemyEntity> enemies;
    public EnemyController enemyController;
    public int score;
    public int previousScore;
    public Label scoreLabel;
    
    public GameScreen(Action quitAction, Action winAction, Action loseAction) {
        this.quitAction = quitAction;
        this.winAction = winAction;
        this.loseAction = loseAction;
    }
    
    @Override
    public void show() {
        enemies = new Array<>();
        
        gameScreen = this;
        entityController = new EntityController();
        
        gameViewport = new FitViewport(1024, 576);
        
        core = Core.core;
        skin = core.skin;
    
        Gdx.graphics.setCursor(core.reticleCursor);
        
        music = core.assetManager.get("bgm/game.mp3");
        music.setLooping(true);
        music.setVolume(.3f);
        if (!music.isPlaying()) {
            music.play();
        }
        
        stage = new Stage(new ScreenViewport(), core.batch);
        Gdx.input.setInputProcessor(stage);
        
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        fg = new Image(skin, "white");
        fg.setColor(Color.BLACK);
        fg.setFillParent(true);
        fg.setTouchable(Touchable.disabled);
        stage.addActor(fg);
        fg.addAction(Actions.sequence(Actions.fadeOut(.3f)));
        
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    showPauseMenu();
                }
                return super.keyDown(event, keycode);
            }
        });
        
        playerEntity = new PlayerEntity();
        playerEntity.setPosition(70, 400);
        entityController.add(playerEntity);
        
        roomEntity = new RoomEntity();
        entityController.add(roomEntity);
        
        SunEntity sunEntity = new SunEntity();
        sunEntity.setPosition(933, 488);
        entityController.add(sunEntity);
        
        enemyController = new EnemyController();
        entityController.add(enemyController);
        
        refreshUI();
        
        Label label = new Label("Room One", skin, "level");
        stage.addActor(label);
        label.setPosition((int) stage.getWidth() / 2, (int) stage.getHeight() / 2, Align.center);
        label.addAction(Actions.sequence(Actions.delay(1), Actions.run(() -> {
            label.setText("Plushie Paradiese");
            label.pack();
            label.setPosition((int) stage.getWidth() / 2, (int) stage.getHeight() / 2, Align.center);
        }), Actions.delay(1), Actions.run(() -> {
            label.setText("FIGHT");
            label.pack();
            label.setPosition((int) stage.getWidth() / 2, (int) stage.getHeight() / 2, Align.center);
        }), Actions.delay(1), Actions.run(() -> {
            label.remove();
        })));
    }
    
    
    @Override
    public void act(float delta) {
        Iterator<EnemyEntity> iter = enemies.iterator();
        while (iter.hasNext()) {
            EnemyEntity enemy = iter.next();
            if (enemy.destroy) {
                iter.remove();
            }
        }
        
        tempVector3.x = Gdx.input.getX();
        tempVector3.y = Gdx.input.getY();
        gameViewport.unproject(tempVector3);
        core.mouseX = tempVector3.x;
        core.mouseY = tempVector3.y;
        
        if (score != previousScore) {
            updateScoreLabel();
            previousScore = score;
        }
        
        stage.act(delta);
        entityController.act(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            core.setScreen(new GameScreen(quitAction, winAction, loseAction));
        }
    }
    
    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        gameViewport.apply(true);
        core.batch.setProjectionMatrix(gameViewport.getCamera().combined);
        core.batch.begin();
        core.batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        entityController.draw(delta);
        core.batch.end();
    
        core.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        gameViewport.update(width, height, true);
    }
    
    private void showPauseMenu() {
        Dialog dialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    Gdx.input.setInputProcessor(null);
                    fg.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(.3f), new TemporalAction(.3f) {
                        float volume = music.getVolume();
                        
                        @Override
                        protected void update(float percent) {
                            music.setVolume(volume * (1 - percent));
                            if (isComplete()) {
                                music.stop();
                            }
                        }
                    }), quitAction));
                }
            }
        };
        dialog.text("Quit to main menu?", skin.get("small-white", Label.LabelStyle.class));
        
        TextButton textButton = new TextButton("Resume Game", skin, "small");
        textButton.addListener(core.sndChangeListener);
        dialog.button(textButton, false);
        textButton = new TextButton("Quit to Menu", skin, "small");
        textButton.addListener(core.sndChangeListener);
        dialog.button(textButton, true);
        dialog.key(Input.Keys.ESCAPE, false).key(Input.Keys.ENTER, true);
        dialog.show(stage);
    }
    
    public void refreshUI() {
        root.clearChildren();
        Table table = new Table();
        root.add(table).top().expandY().padTop(15).uniform();
        
        for (int i = 0; i < playerEntity.ammo; i++) {
            Image image = new Image(skin, "round-rifle");
            if (i == 0) {
                table.add(image).padLeft(10);
            } else {
                table.add(image);
            }
            table.row();
        }
        
        scoreLabel = new Label(Integer.toString(score), skin, "score");
        root.add(scoreLabel).expand().top();
        
        root.add().uniform();
    }
    
    public void createRetryDialog() {
        Dialog dialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    core.setScreen(new GameScreen(quitAction, winAction, loseAction));
                } else {
                    Gdx.input.setInputProcessor(null);
                    fg.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(.3f), new TemporalAction(.3f) {
                        float volume = music.getVolume();
        
                        @Override
                        protected void update(float percent) {
                            music.setVolume(volume * (1 - percent));
                            if (isComplete()) {
                                music.stop();
                            }
                        }
                    }), quitAction));
                }
            }
        };
        Label label = new Label("Final Score: " + score + "\nYou're DEAD! Retry?", skin, "small-white");
        label.setAlignment(Align.center);
        dialog.text(label);
    
        TextButton textButton = new TextButton("Yes", skin, "small");
        textButton.addListener(core.sndChangeListener);
        dialog.button(textButton, true);
        textButton = new TextButton("No, I'm a wimp", skin, "small");
        textButton.addListener(core.sndChangeListener);
        dialog.button(textButton, false);
        dialog.key(Input.Keys.ESCAPE, false).key(Input.Keys.ENTER, true);
        stage.addAction(Actions.delay(3, Actions.run(() -> {
            dialog.show(stage);
        })));
    }
    
    public void createWinDialog() {
        Dialog dialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(.3f), new TemporalAction(.3f) {
                    float volume = music.getVolume();
        
                    @Override
                    protected void update(float percent) {
                        music.setVolume(volume * (1 - percent));
                        if (isComplete()) {
                            music.stop();
                        }
                    }
                }), winAction));
            }
        };
        Label label = new Label("Final Score: " + score + "\nYou beat the level!", skin, "small-white");
        label.setAlignment(Align.center);
        dialog.text(label);
        
        TextButton textButton = new TextButton("Load up Room 2", skin, "small");
        textButton.addListener(core.sndChangeListener);
        dialog.button(textButton, true);
        dialog.key(Input.Keys.ESCAPE, false).key(Input.Keys.ENTER, true);
        stage.addAction(Actions.delay(3, Actions.run(() -> {
            dialog.show(stage);
        })));
    }
    
    public void updateScoreLabel() {
        scoreLabel.setText(Integer.toString(score));
    }
}
