package spacesettlers.objects;

import java.awt.Color;

import spacesettlers.graphics.CircleGraphics;
import spacesettlers.graphics.StarGraphics;
import spacesettlers.utilities.Position;

/**
 * A star is just a collectable object used for initial agent testing (it may eventually give you something useful for powerups too)
 */
public class Star extends AbstractObject {
	/**
	 * The radius of a beacon
	 */
	public static final int STAR_RADIUS = 20;
	
	public static final int STAR_MASS = 0;
	
	public Star(Position location) {
		super(STAR_MASS, STAR_RADIUS, location);
		
		setDrawable(true);
		setAlive(true);
		this.isMoveable = false;
		graphic = new StarGraphics(this, STAR_RADIUS, Color.ORANGE, location);
	}
	
	/**
	 * Makes a copy used for security
	 */
	public Star deepClone() {
		Star newStar = new Star(getPosition().deepCopy());
		newStar.setAlive(isAlive);
		newStar.id = id;
		return newStar;
	}

	public String toString() {
		String str = "Star id " + id;
		return str;
	}

}
