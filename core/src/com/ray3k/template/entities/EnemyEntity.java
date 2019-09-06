package com.ray3k.template.entities;

import com.badlogic.gdx.math.Rectangle;

public abstract class EnemyEntity extends Entity implements Collidable {
    public Rectangle bbox;
    public float health;
    public int score;
    
    public EnemyEntity() {
        this.bbox = new Rectangle();
    }
    
    @Override
    public Rectangle getBbox() {
        return bbox;
    }
    
    public void hurt(float damage) {
        health -= damage;
        
        if (health + damage > 0) {
            hurtAction(damage);
            
            if (health <= 0) {
                death();
            }
        }
    }
    
    public abstract void hurtAction(float damage);
    public abstract void death();
}
