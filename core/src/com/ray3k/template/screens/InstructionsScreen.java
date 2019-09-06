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
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class InstructionsScreen extends JamScreen {
    private Action nextAction;
    private Stage stage;
    private Skin skin;
    private Core core;
    private Music music;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    private int step;
    
    public InstructionsScreen(Action nextAction) {
        this.nextAction = nextAction;
    }
    
    @Override
    public void show() {
        step = 0;
        core = Core.core;
        skin = core.skin;
    
        music = core.assetManager.get("bgm/menu.mp3");
        
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
    
        TypingLabel typingLabel = new TypingLabel("GET IT TOGETHER ZEBRA! The Unity King and his unstoppable army of Plushies are at the gates. Now, if you're done grabassing, listen up!", skin, "small-white");
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
                        typingLabel.setText("Use WASD or the ARROW KEYS to move around. Press LEFT CLICK to shoot. Press RIGHT CLICK to reload.");
                        typingLabel.restart();
                        break;
                    case 1:
                        image.setDrawable(skin, "reload-preview");
                        typingLabel.setText("To quick reload, press RIGHT CLICK again when the reload progress is on the white bar. ARE YOU PAYING ATTENTION!?");
                        typingLabel.restart();
                        break;
                    case 2:
                        image.setDrawable(skin, "panda");
                        typingLabel.setText("Remember, SHORT, CONTROLLED BURSTS! I expect you to die quickly, but you always manage to surprise me. Good luck soldier!");
                        typingLabel.restart();
                        textButton.setText("PLAY");
                        break;
                    default:
                        Gdx.input.setInputProcessor(null);
                        fg.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(.3f), new TemporalAction(.3f) {
                            @Override
                            protected void update(float percent) {
                                music.setVolume(.5f - .5f * percent);
                                if (isComplete()) {
                                    music.stop();
                                }
                            }
                        }), nextAction));
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
