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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class WinScreen extends JamScreen {
    private Action nextAction;
    private Stage stage;
    private Skin skin;
    private Core core;
    private Music music;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    private int step;
    
    public WinScreen(Action nextAction) {
        this.nextAction = nextAction;
    }
    
    @Override
    public void show() {
        step = 0;
        core = Core.core;
        skin = core.skin;
    
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
        table.pad(10);
        table.setBackground(skin.getDrawable("title-bg-ten"));
        root.add(table).size(500, 300);
    
        table.defaults().space(10);
        Image image = new Image(skin, "panda");
        image.setScaling(Scaling.none);
        table.add(image);
    
        TypingLabel typingLabel = new TypingLabel("You did it! You beat the Unity King. Unfortunately, he was only a pawn...", skin, "small-white");
        typingLabel.setWrap(true);
        table.add(typingLabel).growX();
        
        root.row();
        table = new Table();
        root.add(table);
        
        table.pad(30);
        table.defaults().uniform().space(3);
        TextButton textButton = new TextButton("Next", skin, "small");
        table.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                switch (step++) {
                    case 0:
                        typingLabel.setText("We want to get his financial backers. Who's really behind this madness? We must go to Edo period Japan to find out.");
                        typingLabel.restart();
                        break;
                    case 1:
                        typingLabel.setText("Please insert disc 2. Do not power down the console while the game is loading...");
                        typingLabel.restart();
                        textButton.setText("OK");
                        break;
                    case 2:
                        typingLabel.setText("Error. Insufficient memory. Please try again.");
                        typingLabel.restart();
                        textButton.setText("OK...");
                        break;
                    default:
                        Gdx.input.setInputProcessor(null);
                        fg.addAction(Actions.sequence(Actions.fadeIn(.3f), nextAction));
                        break;
                }
            }
        });
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
