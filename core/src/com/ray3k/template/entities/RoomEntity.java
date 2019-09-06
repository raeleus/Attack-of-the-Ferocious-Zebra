package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.ray3k.template.screens.GameScreen;

public class RoomEntity extends Entity {
    private static SkeletonData skeletonData;
    private static AnimationStateData animationStateData;
    public Array<Rectangle> rects;
    
    @Override
    public void create() {
        if (skeletonData == null) skeletonData = core.assetManager.get("spine/room.json");
        if (animationStateData == null) animationStateData = new AnimationStateData(skeletonData);
        
        rects = new Array<>();
        
        setSkeletonData(skeletonData, animationStateData);
        for (BoundingBoxAttachment bbox : skeletonBounds.getBoundingBoxes()) {
            float[] verts = bbox.getVertices();
            float left = GameScreen.gameViewport.getWorldWidth();
            float right = 0;
            float top = 0;
            float bottom = GameScreen.gameViewport.getWorldHeight();
            
            for (int i = 0; i + 1 < verts.length; i += 2) {
                if (verts[i] < left) left = verts[i];
                if (verts[i] > right) right = verts[i];
                
                if (verts[i+1] < bottom) bottom = verts[i+1];
                if (verts[i+1] > top) top = verts[i+1];
            }
            
            rects.add(new Rectangle(left, bottom, right - left, top - bottom));
        }
        animationState.setAnimation(0, "animation", true);
        depth = core.BACKGROUND_DEPTH;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
//        core.shapeDrawer.setColor(Color.WHITE);
//        core.shapeDrawer.setDefaultLineWidth(2);
//        for (Rectangle rect : rects) {
//            core.shapeDrawer.rectangle(rect);
//        }
    }
    
    @Override
    public void destroy() {
    
    }
}
