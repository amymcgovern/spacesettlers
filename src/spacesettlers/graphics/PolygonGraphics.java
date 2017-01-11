package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.List;

import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.utilities.Position;

/**
 * Polygon graphics provided by Chris Fenner
 * @author Chris Fenner, AI student spring 2013
 *
 */
public class PolygonGraphics extends SpacewarGraphics {
	Polygon polygon;
    Color color;
    int height, width;
    Position position;

    public static PolygonGraphics fromPositions(Color c, List<Position> points) {
    	Polygon myPolygon = new Polygon();
    	for (Position p : points) {
    		myPolygon.addPoint((int)p.getX(), (int)p.getY());
    	}
    	Rectangle2D bounds = myPolygon.getBounds2D();
        return new PolygonGraphics(c, myPolygon, bounds);
    }
    
    public PolygonGraphics(Color color, Polygon polygon, Rectangle2D bounds) {
    	super((int)bounds.getHeight(), (int)bounds.getWidth());
    	this.polygon = polygon;
    	this.height = (int)bounds.getHeight();
    	this.width = (int)bounds.getWidth();
        this.color = color;
        position = new Position(bounds.getCenterX(), bounds.getCenterY());
    }

	/**
	 * @return the halfHeight
	 */
    @Override
	public int getHalfHeight() {
		return height / 2;
	}

	/**
	 * @return the halfWidth
	 */
    @Override
	public int getHalfWidth() {
		return width / 2;
	}

	/**
	 * @return the height
	 */
    @Override
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
    @Override
	public int getWidth() {
		return width;
	}
    
	public Position getActualLocation() {
		return position;
	}

	public void setCurrentPosition(Position currentPosition) {
		this.position = currentPosition;
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
        graphics.setColor(color);
        if (!getDrawLocation().equals(position)) {
        	polygon.translate((int)(this.getDrawLocation().getX() - position.getX()),
        			(int)(this.getDrawLocation().getY() - position.getY()));
        	graphics.fill(polygon);
        	polygon.translate(- (int)(this.getDrawLocation().getX() - position.getX()),
        			- (int)(this.getDrawLocation().getY() - position.getY()));

        } else {
        	graphics.fill(polygon);
        }
	}

	public boolean isDrawable() {
		return true;
	}

}
