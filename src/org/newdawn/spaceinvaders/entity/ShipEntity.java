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
	

	public ShipEntity(Game game,String ref,int x,int y) {
		super(ref,x,y);
		
		this.game = game;
	}
	public int getHp(){return hp;}
	public void setHp(int Hp){this.hp += Hp;}
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
			game.sp.playSE(11,-10f);
			gotHit =true;
			if(this.hp<=0){
				game.removeEntity(this);
				game.notifyDeath();
			}
		}
		else if(other instanceof ObstacleEntity){
			this.hp--;
			game.sp.playSE(11,-10f);
			gotHit=true;
			if(this.hp<=0){
				game.removeEntity(this);
				game.notifyDeath();
			}
		}
	}
}