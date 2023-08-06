package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import spacesettlers.objects.Star;
import spacesettlers.utilities.Position;

/**
 * Draws a star in the specified location and using the specified color.
 * 
 * Star coordinates came from:
 * 
 * http://flylib.com/books/1/226/1/html/2/images/fig217_01.jpg
 * 
 * Translated to 0,0 in the middle by Amy
 * 
 * @author amy
 *
 */
public class StarGraphics extends SpacewarGraphics {
	private int radius;
	Star star = null;
    Color color;
    Position currentPosition;
    public static final int DEFAULT_RADIUS = 5;
    public static final Shape STAR_SHAPE = new Polygon(new int[]{-1, 4, 14, 7, 10, -1, -10, -7, -14, -4}, 
    		new int[]{-14, -4, -3, 4, 14, 9, 14, 4, -3, -4}, 10);

    public StarGraphics(Star star, int radius, Color color, Position position) {
        super(radius * 2, radius * 2);
		this.star = star;
        this.radius = radius;
        this.color = color;
        this.currentPosition = position;
    }

	public StarGraphics(int radius, Color color, Position position) {
        super(radius * 2, radius * 2);
        this.radius = radius;
        this.color = color;
        this.currentPosition = position;
    }

    public StarGraphics(Color color, Position position) {
        super(DEFAULT_RADIUS * 2, DEFAULT_RADIUS * 2);
        this.radius = DEFAULT_RADIUS;
        this.color = color;
        this.currentPosition = position;
    }

	public void setCurrentPosition(Position currentPosition) {
		this.currentPosition = currentPosition;
	}

	/**
	 * Change the color of the graphic
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Draws a star shape after translating it to the current location where it should appear
	 */
	public void draw(Graphics2D graphics) {
		final AffineTransform transform =
                AffineTransform.getTranslateInstance(drawLocation.getX(), drawLocation.getY());
        transform.scale(.6, .6);
        Shape newStarShape = transform.createTransformedShape(STAR_SHAPE);
		
        graphics.setColor(color);
        graphics.fill(newStarShape);
	}

	/**
	 * If the star is attached to a star object, only draw if that object is alive.  otherwise, assume alive
	 */
	public boolean isDrawable() {
		if (star != null) {
			if (star.isAlive() && star.isDrawable()) {
				return true;
			} else {
				return false;
			}
		} 
		return true;
	}

	/**
	 * Return the actual location of the star
	 */
	public Position getActualLocation() {
		if (star != null) {
			return star.getPosition();
		} else {
			return currentPosition;
		}
	}

}
