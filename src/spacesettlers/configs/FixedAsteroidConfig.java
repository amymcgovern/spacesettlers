package spacesettlers.configs;

/**
 * Configuration for any fixed-location asteroids.  Fixed asteroids are non-mineable.
 * (mostly used for capture the flag)
 * 
 * @author amy
 *
 */
public class FixedAsteroidConfig {
	/**
	 * a fixed location asteroid is specified with an x and y location and the radius
	 */
	int x, y, radius;
	
	boolean isMoveable, isMineable, isGameable;
	
	double maxInitialVelocity;
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getRadius() {
		return radius;
	}

	public boolean isMoveable() {
		return isMoveable;
	}

	public boolean isMineable() {
		return isMineable;
	}
	
	public boolean isGameable() {
		return isGameable;
	}

	public double getMaxInitialVelocity() {
		return maxInitialVelocity;
	}
	
}
