package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import spacesettlers.gui.JSpaceSettlersComponent;
import spacesettlers.utilities.Position;

/**
 * Draws a circle in the specified location and using the specified color
 * 
 * @author amy
 *
 */
public class CircleGraphics extends SpacewarGraphics {
	private int radius;
    Color color;
    Position currentPosition;
    public static final int DEFAULT_RADIUS = 5;

    public CircleGraphics(int radius, Color color, Position position) {
        super(radius * 2, radius * 2);
        this.radius = radius;
        this.color = color;
        this.currentPosition = position;
    }

    public CircleGraphics(Color color, Position position) {
        super(DEFAULT_RADIUS * 2, DEFAULT_RADIUS * 2);
        this.radius = DEFAULT_RADIUS;
        this.color = color;
        this.currentPosition = position;
    }
    
	public Position getActualLocation() {
		return currentPosition;
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
	 * Drawing code from the old CircleShadow in spacewar1
	 */
	public void draw(Graphics2D graphics) {
        Ellipse2D.Double shape = new Ellipse2D.Double(drawLocation.getX() - radius,
        		drawLocation.getY() - radius, 2 * radius, 2 * radius);

        graphics.setColor(color);
        graphics.fill(shape);
        graphics.setStroke(JSpaceSettlersComponent.THICK_STROKE);
        graphics.draw(shape);

	}

	public boolean isDrawable() {
		return true;
	}

}
