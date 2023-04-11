package org.newdawn.spaceinvaders.entity;

import javafx.beans.value.ObservableStringValue;
import org.newdawn.spaceinvaders.Game;

/**
 * The entity that represents the players ship
 * 
 * @author Kevin Glass
 */
public class ShipEntity extends Entity {
	/** The game in which the ship exists */
	private Game game;

	private int hp=5;

	private boolean gotHit;
	
	/**
	 * Create a new entity to represent the players ship
	 *  
	 * @param game The game in which the ship is being created
	 * @param ref The reference to the sprite to show for the ship
	 * @param x The initial x location of the player's ship
	 * @param y The initial y location of the player's ship
	 */
	public ShipEntity(Game game,String ref,int x,int y) {
		super(ref,x,y);
		
		this.game = game;
	}

	public int getHp(){return hp;}

	public void setHp(int Hp){this.hp += Hp;}
	
	/**
	 * Request that the ship move itself based on an elapsed ammount of
	 * time
	 * 
	 * @param delta The time that has elapsed since last move (ms)
	 */
	public void move(long delta) {
		// if we're moving left and have reached the left hand side
		// of the screen, don't move
		if ((dx < 0) && (x < 10)) {
			return;
		}
		// if we're moving right and have reached the right hand side
		// of the screen, don't move
		if ((dx > 0) && (x > 750)) {
			return;
		}
		
		super.move(delta);
	}

	public boolean getHit(){return  gotHit;}
	public void setHit(boolean hit){this.gotHit = hit;}
	/**
	 * Notification that the player's ship has collided with something
	 * 
	 * @param other The entity with which the ship has collided
	 */
	public void collidedWith(Entity other) {
		if (other instanceof BossShotEntity ) {
			this.hp--;
			gotHit =true;
			if(this.hp<=0){
				game.removeEntity(this);
				game.notifyDeath();
			}
		}
		else if(other instanceof ObstacleEntity){
			this.hp--;
			if(this.hp<=0){
				game.removeEntity(this);
				game.notifyDeath();
			}
		}
	}
}