package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.ray3k.template.Core;

public class RicochetMetalEntity extends Entity {
    private static SkeletonData skeletonData;
    private static AnimationStateData animationStateData;
    public Array<Rectangle> rects;
    
    @Override
    public void create() {
        if (skeletonData == null) skeletonData = core.assetManager.get("spine/ricochet.json");
        if (animationStateData == null) animationStateData = new AnimationStateData(skeletonData);
        
        rects = new Array<>();
        
        setSkeletonData(skeletonData, animationStateData);
        skeleton.getRootBone().setRotation(MathUtils.random(360));
        animationState.setAnimation(0, "animation", true);
        depth = Core.PROJECTILE_DEPTH;
        
        if (MathUtils.randomBoolean(.5f)) {
            core.ricochetMetalSound.play();
        }
        
        animationState.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                destroy = true;
            }
        });
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
    }
    
    @Override
    public void destroy() {
    
    }
}
