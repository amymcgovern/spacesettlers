package spacesettlers.utilities;

/**
 * Convenience class to allow actions to move in orientation and positional
 * space
 * 
 * @author amy
 *
 */
public final class Movement {
	/**
	 * Acceleration in orientation
	 */
	double angularAcceleration;

	/**
	 * Accelerating in x and y (x'' and y'' if you prefer)
	 */
	Vector2D translationalAcceleration;

	/**
	 * Maximum accelerations. If you specific one above (or below the negative), it
	 * will set to the max
	 */
	static public final double MAX_TRANSLATIONAL_ACCELERATION = 62;
	static public final double MAX_ANGULAR_ACCELERATION = Math.PI;

	public Movement() {
		super();
		translationalAcceleration = new Vector2D();
	}

	public Vector2D getTranslationalAcceleration() {
		return translationalAcceleration;
	}

	/**
	 * Set the acceleration and respect the max/mins
	 * 
	 * @param translationalAcceleration
	 */
	public void setTranslationalAcceleration(Vector2D translationalAcceleration) {
		this.translationalAcceleration = translationalAcceleration;

		if (translationalAcceleration.getMagnitude() > MAX_TRANSLATIONAL_ACCELERATION) {
			double ratio = translationalAcceleration.getMagnitude() / MAX_TRANSLATIONAL_ACCELERATION;
			this.translationalAcceleration = this.translationalAcceleration.multiply(1 / ratio);
		}
	}

	public double getAngularAcceleration() {
		return angularAcceleration;
	}

	/**
	 * Set the acceleration and respect the max/mins
	 * 
	 * @param angularAcceleration
	 */
	public void setAngularAcceleration(double angularAcceleration) {
		if (angularAcceleration > MAX_ANGULAR_ACCELERATION) {
			this.angularAcceleration = MAX_ANGULAR_ACCELERATION;
		} else if (angularAcceleration < -MAX_ANGULAR_ACCELERATION) {
			this.angularAcceleration = -MAX_ANGULAR_ACCELERATION;
		} else {
			this.angularAcceleration = angularAcceleration;
		}
	}

}
