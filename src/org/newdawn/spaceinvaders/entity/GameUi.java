package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.Game;

/**
 * An entity which represents one of our space invader aliens.
 * 
 * @author Kevin Glass
 */
public class GameUi extends Entity {
	private Game game;
	public GameUi(Game game, String ref, int x, int y) {
		super(ref,x,y);
		this.game = game;
	}
	public void collidedWith(Entity other) {
		// collisions with aliens are handled elsewhere
	}
}