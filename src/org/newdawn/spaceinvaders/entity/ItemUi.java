package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.UserDB;

/**
 * An entity representing a shot fired by the player's ship
 *
 * @author Kevin Glass
 */

public class ItemUi extends Entity {
    /** The vertical speed at which the players shot moves */
    private double moveSpeed = 300;
    /** The game in which this entity exists */
    private Game game;
    /** True if this shot has been "used", i.e. its hit something */
    private boolean used = false;
    enum Item{coin, other}
    Item item = Item.coin;


    /**
     * Create a new shot from the player
     *
     * @param game The game in which the shot has been created
     * @param sprite The sprite representing this shot
     * @param x The initial x location of the shot
     * @param y The initial y location of the shot
     */
    public ItemUi(Game game, String sprite, int x, int y) {
        super(sprite,x,y);

        this.game = game;
    }


    /**
     * Request that this shot moved based on time elapsed
     *
     * @param delta The time that has elapsed since last move
     */
    public void move(long delta) {

        dy = moveSpeed;
        // proceed with normal move
        super.move(delta);

        // if we shot off the screen, remove ourselfs
        if (y < -100) {
            game.removeEntity(this);
        }
    }

    /**
     * Notification that this shot has collided with another
     * entity
     *
     * @parma other The other entity with which we've collided
     */
    public void collidedWith(Entity other) {
        if (used) {
            return;
        }
        if (other instanceof ShipEntity) {
                switch (item){
                    case coin:
                        UserDB.coin++;
                        game.sp.playSE(12,5);
                        break;
                    case other:
                        break;
            }
            game.removeEntity(this);
            used =true;
        }
    }
}