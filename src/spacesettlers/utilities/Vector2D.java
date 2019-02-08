package spacesettlers.utilities;

import java.io.Serializable;
import java.util.Random;

/**
 * Vector2D provides an immutable vector representation as well as a collection of useful vector
 * operations.  
 * 
 * Note, this class comes from the original spacewar code.
 */
@SuppressWarnings("serial")
public final class Vector2D implements Serializable, Cloneable {
    private final double x, y;

    public static final Vector2D ZERO_VECTOR = new Vector2D();
    public static final Vector2D X_UNIT_VECTOR = new Vector2D(1, 0);
    public static final Vector2D X_NEG_UNIT_VECTOR = new Vector2D(-1, 0);
    public static final Vector2D Y_UNIT_VECTOR = new Vector2D(0, 1);
    public static final Vector2D Y_NEG_UNIT_VECTOR = new Vector2D(0, -1);

    public static final double HALFPI = 0.5 * Math.PI;
    public static final double THREEHALFPI = 1.5 * Math.PI;
    public static final double TWOPI = 2.0 * Math.PI;
    
    /**
     * Cached (calculated on demand)
     */
    double magnitude;

    /**
     * Create a vector with the given x and y values.
     * @param x
     * @param y
     */
    public Vector2D(final double x, final double y) {
        this.x = x;
        this.y = y;
        magnitude = Double.NaN;
    }

    /**
     * Create a new vector from an old one;
     */
    public Vector2D(final Vector2D b){
    	x = b.x;
    	y = b.y;
        magnitude = Double.NaN;
    }
    
    /**
     * Initialize from a position
     * @param position
     */
    public Vector2D(Position position) {
    	x = position.getX();
    	y = position.getY();
        magnitude = Double.NaN;
    }

    /**
     * Create the zero vector
     */
    public Vector2D() {
        this(0, 0);
    }
    
    /**
     * Cloned copy for the Cloneable interface
     */
    protected Object clone() {
    	return new Vector2D(this);
    }

    /**
     * Create a vector from the given angle (in radians) and magnitude
     * @param angle In radians
     * @param magnitude
     * @return A new Vector2D
     */
    public static Vector2D fromAngle(double angle, double magnitude) {
        return new Vector2D((float) Math.cos(angle) * magnitude, (float) Math.sin(angle) * magnitude);
    }

    /**
     * Create a random vector with a magnitude no greater than specified
     * @param rand The source of randomness to use.
     * @param maxMagnitude
     * @return A new random Vector2D
     */
    public static Vector2D getRandom(Random rand, double maxMagnitude) {
        final double max = maxMagnitude * maxMagnitude;

        final double x2 = rand.nextDouble() * max;
        final double y2 = rand.nextDouble() * (max - x2);

        final double x = rand.nextBoolean() ? (double)Math.sqrt(x2) : -(double)Math.sqrt(x2);
        final double y = rand.nextBoolean() ? (double)Math.sqrt(y2) : -(double)Math.sqrt(y2);

        return new Vector2D(x, y);
    }

    /**
     * The X value of the vector.
     * @return The X value of the vector.
     */
    public double getXValue() {
        return x;
    }

    /**
     * The Y value of the vector.
     * @return The Y value of the vector.
     */
    public double getYValue() {
        return y;
    }
    
    /**
     * return the unit vector
     * @return
     */
    public Vector2D getUnitVector() {
    	return this.divide(getMagnitude());
    } 
    
    /**
     * The magnitude of the vector.  
     * @return The magnitude of the vector.
     */
    public double getMagnitude() {
    	magnitude = Math.sqrt(x*x + y*y);
    	return magnitude;
    }

    /**
     * Get the sum of the vector components
     * @return
     */
    public double getTotal() {
    	return x + y;
    }
    
    /**
     * The angle of the vector
     * @return The angle of the vector.
     */
    public double getAngle() {
        return Math.atan2(y, x);
    }

    /**
     * The angle (in radians) between this vector and the given vector.
     * The angle is positive if v is to the left of this vector, and
     * negative if v is to the right of this vector (right hand coords)
     *
     * @param v A given vector.
     * @return The angle between the two vectors in radians.
     */
    public double angleBetween(final Vector2D v) {
        double num, den, angle;

        num = (x * v.x + y * v.y);
        den = (getMagnitude() * v.getMagnitude());

        if(den == 0 ) {
            return 0;
        }

        if (Math.abs(num) > Math.abs(den)) {
            if(num > den) {
                num = den;
            } else {
                num = -den;
            }
        }

        angle = (float) Math.acos(num / den);

        return (cross(v) >= 0) ? angle : -angle;
    }

    /**
     * The unit vector derived from this vector, or an arbitrary unit vector if this is the zero vector
     * @return A unit vector with the same orientation as this vector.
     */
    public final Vector2D unit() {
    	double magnitude = getMagnitude();
        if (magnitude == 0)
            return X_UNIT_VECTOR;

        return divide(magnitude);
    }

    /**
     * Reverse this vector.
     * @return The reverse of this vector.
     */
    public final Vector2D negate() {
        return new Vector2D(-x, -y);
    }

    /**
     * Add these two vectors together.
     * @param vector2d Vector to add
     * @return The sum of the vectors.
     */
    public final Vector2D add(final Vector2D vector2d) {
        return new Vector2D(x + vector2d.x, y + vector2d.y);
    }

    /**
     * Subtract the other vector from this vector.
     * @param v
     * @return The vector resulting from subtracting the other vector from this vector.
     */
    public final Vector2D subtract(final Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    /**
     * Multiply this vector by the given scalar.
     * @param f
     * @return The scaled vector.
     */
    public final Vector2D multiply(double f) {
        return new Vector2D(x * f, y * f);
    }

    /**
     * Divide this vector by the given scalar.
     * @param f
     * @return The scaled vector
     */
    public final Vector2D divide(double f) {
        return new Vector2D(x / f, y / f);
    }

    /**
     * Get the dot product of the two vectors.
     * @param v
     * @return The dot product
     */
    public final double dot(final Vector2D v) {
        return x * v.x + y * v.y;
    }

    /**
     * Get the cross product of the two vectors.
     * @param v
     * @return The cross product.
     */
    public final double cross(final Vector2D v) {
        return x * v.y - y * v.x;
    }

    /**
     * Rotate this vector using the given sine and cosine values.
     * @param cos
     * @param sin
     * @return The rotated vector.
     */
    public final Vector2D fastRotate(final float cos, final float sin) {
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    /**
     * Subtract the other vector from this vector and rotate the result using the
     * given sine and cosine values.
     * @param v
     * @param cos
     * @param sin
     * @return The subtracted and rotated vector.
     */
    public final Vector2D subtractAndRotate(final Vector2D v, final float cos, final float sin) {
        final double x2 = x - v.x, y2 = y - v.y;
        return new Vector2D(x2 * cos - y2 * sin, x2 * sin + y2 * cos);
    }

    /**
     * Rotate this vector by the specified angle (in radians)
     * @param f
     * @return the rotated vector
     */
    public final Vector2D rotate(double f) {
        final double cos = Math.cos(f);
        final double sin =  Math.sin(f);
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    /**
     * Project the given vector onto this vector. 
     * 
     *  Math from:
     *  http://en.wikipedia.org/wiki/Vector_projection
     *
     * @param v
     * @return The scalar projection of the other vector onto this one
     */
    public double scalarProject(Vector2D other) {
        double a1 = (this.dot(other)) / (other.getMagnitude());
        return a1;
    }
    
    /**
     * Project the given vector onto this one
     * 
     * Math from:
     * 
     * http://en.wikipedia.org/wiki/Vector_projection
     * 
     * @param other
     * @return the vector project of the other vector onto this one
     */
    public Vector2D vectorProject(Vector2D other) {
    	double num = this.dot(other);
    	double den = other.dot(other);
    	
    	Vector2D a1 = other.multiply(num / den);

    	return a1;
    }
    
    /**
     * Project the given vector onto this one
     * 
     * Math from:
     * 
     * http://en.wikipedia.org/wiki/Vector_projection
     * 
     * @param other
     * @return the vector project of the other vector onto this one
     */
    public Vector2D vectorRejection(Vector2D other) {
    	Vector2D a2 = this.subtract(this.vectorProject(other));

    	return a2;
    }

    /**
     * Determine if two vectors are equal (have the same components)
     * @param v
     * @return True if the components match, false otherwise.
     */
    public final boolean equals(final Vector2D v) {
        return x == v.x && y == v.y;
    }

    /**
     * Compare the vectors on the basis of magnitude.
     * @param other
     * @return -1 if this is smaller than, 0 if equal, 1 if this is greater than
     */
    public int magnitudeCompareTo(final Vector2D other) {
        return ((Double)getMagnitude()).compareTo(other.getMagnitude());
    }

    /**
     * Equals comparison partially generated by eclipse.  Checks if the x/y are the same
     */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2D other = (Vector2D) obj;
		return this.equals(other);
	}

	/**
     * String representation of the vector for the user
     */
    public String toString() {
        final String str = x + " " + y;
        return str;
    }

    /**
     * Ensures all components of the vector are valid (finite and not NaN)
     * 
	 * @return true if the vector is valid (finite and a number, doesn't check world size) and false otherwise 
     */
	public boolean isValid() {
		if (Double.isFinite(x) && Double.isFinite(y)) {
			return true;
		} else {
			return false;
		}
	}

}