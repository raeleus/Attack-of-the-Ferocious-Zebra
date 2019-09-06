package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.ray3k.template.screens.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Core extends JamGame {
    private static final int MAX_VERTEX_SIZE = 32767;
    public static Core core;
    public TwoColorPolygonBatch batch;
    public Skin skin;
    public SkeletonRenderer skeletonRenderer;
    public ShapeDrawer shapeDrawer;
    public Sound sndClick;
    public ChangeListener sndChangeListener;
    public static final int PROJECTILE_DEPTH = 0;
    public static final int PLAYER_DEPTH = 10;
    public static final int ENEMY_DEPTH = 100;
    public static final int BACKGROUND_PROP_DEPTH = 900;
    public static final int BACKGROUND_DEPTH = 1000;
    public float mouseX;
    public float mouseY;
    public Sound rifleSound;
    public Sound jumpSound;
    public Sound landSound;
    public Sound dieSound;
    public Sound goreSound;
    public Sound grabSound;
    public Sound grabBagSound;
    public Sound shellSound;
    public Sound reloadStartSound;
    public Sound reloadSound;
    public Sound errorSound;
    public Sound successSound;
    public Sound ricochetSound;
    public Sound dryFireSound;
    public Sound laserSound;
    public Sound pistolSound;
    public Sound ricochetMetalSound;
    public Sound explosionSound;
    public Sound popSound;
    public Sound unitySound;
    public Sound concedeSound;
    public Sound chargeSound;
    
    public Cursor reticleCursor;
    public Cursor noneCursor;
    public TextureAtlas textureAtlas;
    
    @Override
    public void create() {
        super.create();
        
        core = this;
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        
        batch = new TwoColorPolygonBatch(MAX_VERTEX_SIZE);
        sndChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                core.sndClick.play();
            }
        };
    
        setScreen(createLoadScreen());
    }
    
    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        
        super.dispose();
    }
    
    @Override
    public void loadAssets() {
        assetManager.load("ui/ui.json", Skin.class);
    
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
        SkeletonDataLoader.SkeletonDataLoaderParameter parameter = new SkeletonDataLoader.SkeletonDataLoaderParameter("libgdx-logo/libgdx.atlas");
        assetManager.load("libgdx-logo/libgdx.json", SkeletonData.class, parameter);
        
        assetManager.load("libgdx-logo/ahh.mp3", Sound.class);
        assetManager.load("libgdx-logo/libgdx.mp3", Sound.class);
        assetManager.load("libgdx-logo/please don't kill me.mp3", Sound.class);
        assetManager.load("libgdx-logo/shot.mp3", Sound.class);
    
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
        parameter = new SkeletonDataLoader.SkeletonDataLoaderParameter("ray3k-logo/ray3k.atlas");
        assetManager.load("ray3k-logo/ray3k.json", SkeletonData.class, parameter);
        
        assetManager.load("ray3k-logo/swoosh.mp3", Sound.class);
        assetManager.load("ray3k-logo/tv.mp3", Sound.class);
    
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
        parameter = new SkeletonDataLoader.SkeletonDataLoaderParameter("spine/Attack of the Ferocious Zebra.atlas");
        assetManager.load("spine/bat.json", SkeletonData.class, parameter);
        assetManager.load("spine/duck.json", SkeletonData.class, parameter);
        assetManager.load("spine/unicorn.json", SkeletonData.class, parameter);
        assetManager.load("spine/unity.json", SkeletonData.class, parameter);
        assetManager.load("spine/zebra.json", SkeletonData.class, parameter);
        assetManager.load("spine/room.json", SkeletonData.class, parameter);
        assetManager.load("spine/ricochet.json", SkeletonData.class, parameter);
        assetManager.load("spine/gore.json", SkeletonData.class, parameter);
        assetManager.load("spine/sun.json", SkeletonData.class, parameter);
        
        assetManager.load("sfx/click.mp3", Sound.class);
        assetManager.load("bgm/menu.mp3", Music.class);
        
        assetManager.load("sfx/audio-test.mp3", Music.class);
        assetManager.load("bgm/music-test.mp3", Music.class);
    
        assetManager.load("bgm/game.mp3", Music.class);
        assetManager.load("sfx/rifle.mp3", Sound.class);
        assetManager.load("sfx/die.mp3", Sound.class);
        assetManager.load("sfx/gore.mp3", Sound.class);
        assetManager.load("sfx/grab.mp3", Sound.class);
        assetManager.load("sfx/grab-bag.mp3", Sound.class);
        assetManager.load("sfx/jump.mp3", Sound.class);
        assetManager.load("sfx/land.mp3", Sound.class);
        assetManager.load("sfx/shell.mp3", Sound.class);
        assetManager.load("sfx/error.mp3", Sound.class);
        assetManager.load("sfx/reload.mp3", Sound.class);
        assetManager.load("sfx/reload-start.mp3", Sound.class);
        assetManager.load("sfx/success.mp3", Sound.class);
        assetManager.load("sfx/ricochet.mp3", Sound.class);
        assetManager.load("sfx/dry-fire.mp3", Sound.class);
        assetManager.load("sfx/laser.mp3", Sound.class);
        assetManager.load("sfx/pistol.mp3", Sound.class);
        assetManager.load("sfx/ricochet-metal.mp3", Sound.class);
        assetManager.load("sfx/explosion.mp3", Sound.class);
        assetManager.load("sfx/pop.mp3", Sound.class);
        assetManager.load("sfx/unity.mp3", Sound.class);
        assetManager.load("sfx/concede.mp3", Sound.class);
        assetManager.load("sfx/charge.mp3", Sound.class);
        
        assetManager.load("cursor/reticle.png", Pixmap.class);
        assetManager.load("cursor/reticle-none.png", Pixmap.class);
    }
    
    private Screen createLoadScreen() {
        return new LoadScreen(Actions.run(() -> {
            skin = assetManager.get("ui/ui.json");
            textureAtlas = assetManager.get("spine/Attack of the Ferocious Zebra.atlas", TextureAtlas.class);
            
            sndClick = assetManager.get("sfx/click.mp3");
            shapeDrawer = new ShapeDrawer(batch, skin.getRegion("white"));
            rifleSound = assetManager.get("sfx/rifle.mp3");
            dieSound = assetManager.get("sfx/die.mp3");
            goreSound = assetManager.get("sfx/gore.mp3");
            grabSound = assetManager.get("sfx/grab.mp3");
            grabBagSound = assetManager.get("sfx/grab-bag.mp3");
            jumpSound = assetManager.get("sfx/jump.mp3");
            landSound = assetManager.get("sfx/land.mp3");
            shellSound = assetManager.get("sfx/shell.mp3");
            reloadSound = assetManager.get("sfx/reload.mp3");
            reloadStartSound = assetManager.get("sfx/reload-start.mp3");
            errorSound = assetManager.get("sfx/error.mp3");
            successSound = assetManager.get("sfx/success.mp3");
            ricochetSound = assetManager.get("sfx/ricochet.mp3");
            dryFireSound = assetManager.get("sfx/dry-fire.mp3");
            laserSound = assetManager.get("sfx/laser.mp3");
            pistolSound = assetManager.get("sfx/pistol.mp3");
            ricochetMetalSound = assetManager.get("sfx/ricochet-metal.mp3");
            explosionSound = assetManager.get("sfx/explosion.mp3");
            popSound = assetManager.get("sfx/pop.mp3");
            unitySound = assetManager.get("sfx/unity.mp3");
            concedeSound = assetManager.get("sfx/concede.mp3");
            chargeSound = assetManager.get("sfx/charge.mp3");
            
            reticleCursor = Gdx.graphics.newCursor(assetManager.get("cursor/reticle.png"), 32, 32);
            noneCursor = Gdx.graphics.newCursor(assetManager.get("cursor/reticle-none.png"), 32, 32);
            Gdx.graphics.setCursor(reticleCursor);
            
            setScreen(createSplashScreen());
        }));
    }
    
    private Screen createSplashScreen() {
//        return new SplashScreen(Actions.run(() -> setScreen(createGameScreen())));
        return new SplashScreen(Actions.run(() -> setScreen(createLibgdxScreen())));
//        return new SplashScreen(Actions.run(() -> setScreen(createWinScreen())));
    }
    
    private Screen createLibgdxScreen() {
        return new LibgdxScreen(Actions.run(() -> setScreen(createLogoScreen())));
    }
    
    private Screen createLogoScreen() {
        return new LogoScreen(Actions.run(() -> setScreen(createMenuScreen())));
    }
    
    private Screen createMenuScreen() {
        return new MenuScreen(Actions.run(() -> setScreen(createInstructionsScreen())),
                Actions.run(() -> setScreen(createOptionsScreen())),
                Actions.run(() -> setScreen(createCreditsScreen())));
    }
    
    private Screen createInstructionsScreen() {
        return new InstructionsScreen(Actions.run(() -> setScreen(createGameScreen())));
    }
    
    private Screen createGameScreen() {
        return new GameScreen(Actions.run(() -> setScreen(createMenuScreen())), Actions.run(() -> setScreen(createWinScreen())), Actions.run(() -> setScreen(createCreditsScreen())));
    }
    
    private Screen createOptionsScreen() {
        return new OptionsScreen(Actions.run(() -> setScreen(createMenuScreen())));
    }
    
    private Screen createWinScreen() {
        return new WinScreen(Actions.run(() -> setScreen(createCreditsScreen())));
    }
    
    private Screen createCreditsScreen() {
        return new CreditsScreen(Actions.run(() -> setScreen(createMenuScreen())));
    }
}
