package spacesettlers.utilities;

/**
 * Position in space (x,y,orientation)
 * @author amy
 *
 */
public class Position {
	double x, y, orientation, angularVelocity;
	Vector2D velocity;

	public Position(double x, double y) {
		super();
		this.x = x;
		this.y = y;
		velocity = new Vector2D();
	}

	public Position(double x, double y, double orientation) {
		super();
		this.x = x;
		this.y = y;
		this.orientation = orientation;
		velocity = new Vector2D();
	}
	
	public Position(Vector2D vec) {
		super();
		this.x = vec.getXValue();
		this.y = vec.getYValue();
		orientation = 0;
		velocity = new Vector2D();
	}
	
	public Position deepCopy() {
		Position newPosition = new Position(x, y, orientation);
		newPosition.velocity = new Vector2D(velocity);
		newPosition.angularVelocity = angularVelocity;
		
		return newPosition;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getOrientation() {
		return orientation;
	}
	
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getTotalTranslationalVelocity() {
		return velocity.getTotal();
	}
	
	public double getTranslationalVelocityX() {
		return velocity.getXValue();
	}
	
	public double getTranslationalVelocityY() {
		return velocity.getYValue();
	}
	
	public Vector2D getTranslationalVelocity() {
		return velocity;
	}
	
	public void setTranslationalVelocity(Vector2D newVel) {
		this.velocity = newVel;
	}
	

	public double getAngularVelocity() {
		return angularVelocity;
	}
	
	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}
	
	public double getxVelocity() {
		return velocity.getXValue();
	}

	public void setxVelocity(double xVelocity) {
		velocity.setX(xVelocity);
	}

	public double getyVelocity() {
		return velocity.getYValue();
	}

	public void setyVelocity(double yVelocity) {
		velocity.setY(yVelocity);
	}

	public void setAngularVelocity(double angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public String toString() {
		String str = "(" + x + " , " + y + ", " + orientation + ") velocity: " + velocity + ", " + angularVelocity;
		return str;
	}


	/**
	 * Compares positions on location (x,y) only and ignores orientation and velocities
	 * 
	 * @param newPosition
	 * @return
	 */
	public boolean equalsLocationOnly(Position newPosition) {
		if (newPosition.getX() == x && newPosition.getY() == y) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Verifies that all components of the position are finite and not NaN
	 * @return true if the position is valid (finite and a number, doesn't check world size) and false otherwise 
	 */
	public boolean isValid() {
		if (Double.isFinite(x) && Double.isFinite(y) && 
				Double.isFinite(angularVelocity) && Double.isFinite(orientation)  &&
				velocity.isValid()) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
