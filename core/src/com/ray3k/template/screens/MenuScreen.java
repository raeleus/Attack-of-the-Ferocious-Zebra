package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class MenuScreen extends JamScreen {
    private Action gameAction;
    private Action optionsAction;
    private Action creditsAction;
    private Stage stage;
    private Skin skin;
    private Core core;
    private Music music;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    
    public MenuScreen(Action gameAction, Action optionsAction, Action creditsAction) {
        this.gameAction = gameAction;
        this.optionsAction = optionsAction;
        this.creditsAction = creditsAction;
    }
    
    @Override
    public void show() {
        core = Core.core;
        skin = core.skin;
    
        Gdx.graphics.setCursor(core.reticleCursor);
        
        music = core.assetManager.get("bgm/menu.mp3");
        music.setLooping(true);
        music.setVolume(.5f);
        if (!music.isPlaying()) {
            music.play();
        }
        
        stage = new Stage(new ScreenViewport(), core.batch);
        Gdx.input.setInputProcessor(stage);
    
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(skin.getDrawable("bg-ten"));
        stage.addActor(root);
        
        final Image fg = new Image(skin, "white");
        fg.setColor(Color.BLACK);
        fg.setFillParent(true);
        fg.setTouchable(Touchable.disabled);
        stage.addActor(fg);
        fg.addAction(Actions.sequence(Actions.fadeOut(.3f)));
    
        Table table = new Table();
        table.setBackground(skin.getDrawable("title-bg-ten"));
        root.add(table);
    
        Image image = new Image(skin, "logo");
        image.setScaling(Scaling.none);
        table.add(image);
        
        root.row();
        table = new Table();
        root.add(table);
        
        table.pad(30);
        table.defaults().uniform().space(3);
        TextButton textButton = new TextButton("Play", skin);
        table.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), gameAction));
            }
        });
    
        textButton = new TextButton("Credits", skin);
        table.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), creditsAction));
            }
        });
        
        root.row();
        Label label = new Label("Copyright Raymond \"Raeleus\" Buckley © 2019", skin, "small-white");
        root.add(label);
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void act(float delta) {
        stage.act(delta);
    }
    
    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
