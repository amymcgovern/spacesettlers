package spacesettlers.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.gui.JSpaceSettlersComponent;
import spacesettlers.utilities.Position;

/**
 * Draws a target on the specified location with a specified radius
 * @author Contributed by Chris Fenner
 */
public class TargetGraphics extends SpacewarGraphics {
	
	private double radius;
	
	private Position currentPosition;
	
	Color color;

	/**
	 * Default target is drawn in red
	 * @param radius
	 * @param position
	 */
    public TargetGraphics(int radius, Position position) {
        super(radius * 2, radius * 2);
        this.radius = radius;
        this.currentPosition = position;
		this.color = new Color(1f, 0f, 0f, 1f);
    }

    public TargetGraphics(int radius, Color color, Position position) {
        super(radius * 2, radius * 2);
        this.radius = radius;
        this.currentPosition = position;
        this.color = color;
    }

	@Override
	public Position getActualLocation() {
		return currentPosition;
	}
	
	@Override
	public int getWidth() {
		return (int)(radius * 2);
	}
	
	@Override
	public int getHeight() {
		return (int)(radius * 2);
	}
	
	@Override
	public int getHalfWidth() {
		return (int)(radius);
	}
	
	@Override
	public int getHalfHeight() {
		return (int)(radius);
	}

	@Override
	public void draw(Graphics2D graphics) {
		double[] radii = {.8 * radius, .5 * radius, .2 * radius};
		graphics.setColor(color);
		graphics.setStroke(new BasicStroke((float)(radius/6), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		Ellipse2D.Double circle = null;
		for (double drawRadius : radii) {			
			circle = new Ellipse2D.Double(currentPosition.getX() - drawRadius,
						currentPosition.getY() - drawRadius, 2 * drawRadius, 2 * drawRadius);
			graphics.draw(circle);
		}
		graphics.fill(circle);
		Line2D.Double line = new Line2D.Double(
				currentPosition.getX() - radius,
				currentPosition.getY(),
				currentPosition.getX() + radius,
				currentPosition.getY());
		graphics.setStroke(JSpaceSettlersComponent.THIN_STROKE);
		graphics.draw(line);
		line = new Line2D.Double(
				currentPosition.getX(),
				currentPosition.getY() - radius,
				currentPosition.getX(),
				currentPosition.getY() + radius);
		graphics.draw(line);
	}

	@Override
	public boolean isDrawable() {
		return true;
	}
    
}
