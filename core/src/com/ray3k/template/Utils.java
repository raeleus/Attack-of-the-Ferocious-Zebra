package com.ray3k.template;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonBounds;

public class Utils {
    public static Array<Actor> getActorsRecursive(Actor actor) {
        Array<Actor> actors = new Array<>();
        if (actor instanceof Group) {
            actors.addAll(((Group) actor).getChildren());
            
            for (int i = 0; i < ((Group) actor).getChildren().size; i++) {
                Actor child = ((Group) actor).getChild(i);
                Array<Actor> newActors = getActorsRecursive(child);
                actors.addAll(newActors);
            }
        }
        
        return actors;
    }
    
    public static Rectangle setRectToSkeletonBounds(Rectangle rectangle, SkeletonBounds skeletonBounds) {
        rectangle.x = skeletonBounds.getMinX();
        rectangle.y = skeletonBounds.getMinY();
        rectangle.width = skeletonBounds.getWidth();
        rectangle.height = skeletonBounds.getHeight();
        return rectangle;
    }
    
    private static final Vector3 v3Temp1 = new Vector3();
    private static final Vector3 v3Temp2 = new Vector3();
    private static final Vector2 temp1 = new Vector2();
    public static BoundingBox rectToBoundingBox(Rectangle rectangle, BoundingBox boundingBox) {
        v3Temp1.set(rectangle.x, rectangle.y, 0);
        v3Temp2.set(rectangle.x + rectangle.width, rectangle.y + rectangle.height, 0);
        boundingBox.set(v3Temp1, v3Temp2);
        return boundingBox;
    }
    
    private static final BoundingBox bboxTemp = new BoundingBox();
    private static final Ray rayTemp = new Ray();
    
    public static boolean rayIntersectRectangle(float x, float y, float direction, Rectangle rectangle, Vector3 intersection) {
        rectToBoundingBox(rectangle, bboxTemp);
        
        temp1.set(1,0);
        temp1.rotate(direction);
        
        rayTemp.set(x, y, 0, temp1.x, temp1.y, 0);
        return Intersector.intersectRayBounds(rayTemp, bboxTemp, intersection);
    }
    
    public static float pointDirection(float x1, float y1, float x2, float y2) {
        temp1.set(x2,  y2);
        temp1.sub(x1, y1);
        temp1.nor();
        return temp1.angle();
    }
}
