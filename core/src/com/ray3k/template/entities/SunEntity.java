package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.ray3k.template.Core;

public class SunEntity extends Entity {
    private static SkeletonData skeletonData;
    private static AnimationStateData animationStateData;
    
    @Override
    public void create() {
        if (skeletonData == null) {
            skeletonData = core.assetManager.get("spine/sun.json");
            animationStateData = new AnimationStateData(skeletonData);
        }
        
        setSkeletonData(skeletonData, animationStateData);
        animationState.setAnimation(0, "animation", true);
        depth = Core.BACKGROUND_PROP_DEPTH;
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
