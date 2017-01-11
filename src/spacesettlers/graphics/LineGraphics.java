package spacesettlers.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * Draws a line on the screen from one point to another
 * 
 * @author amy
 */
public class LineGraphics extends SpacewarGraphics {
	public static final Color DEFAULT_LINE_COLOR = Color.CYAN;
	Position startPoint, endPoint;
	Color lineColor;
	float strokeWidth;
	
	/**
	 * Draw a line segment from the starting point to the ending point
	 * 
	 * @param startPoint starting point
	 * @param endPoint end point
	 * @param startToFinish vector pointing from start to end
	 */
    public LineGraphics(Position startPoint, Position endPoint, Vector2D startToFinish) {
    	// height/width for the line segment comes from the vector
    	super((int)Math.abs(startToFinish.getXValue()), (int)Math.abs(startToFinish.getYValue()));
    	
    	double regularDistance = Math.sqrt((startPoint.getX() - endPoint.getX()) * (startPoint.getX() - endPoint.getX()) +
    			(startPoint.getY() - endPoint.getY()) * (startPoint.getY() - endPoint.getY()));
    	//System.out.println("regular distance is " + regularDistance + " and shortest is " + startToFinish.getMagnitude());
    	
    	double newEndX = startPoint.getX() + startToFinish.getXValue();
    	double newEndY = startPoint.getY() + startToFinish.getYValue();
    	
    	this.startPoint = startPoint;
    	this.endPoint = new Position(newEndX, newEndY);
    	
    	lineColor = DEFAULT_LINE_COLOR;
		strokeWidth = 2f;
    }

	
	@Override
	public Position getActualLocation() {
		return startPoint;
	}

	/**
	 * This handles the toroidal space wrapping internally.  It probably shouldn't.
	 * 
	 * TODO: Fix it so it does the wrapping externally like the other graphics (but it is harder here)
	 */
	public void draw(Graphics2D graphics) {
		graphics.setColor(lineColor);
		graphics.setStroke(new BasicStroke(strokeWidth));
		
		graphics.drawLine((int)startPoint.getX(), (int)startPoint.getY(), 
				(int)endPoint.getX(), (int)endPoint.getY());
	}

	/**
	 * Lines are always drawn
	 */
	public boolean isDrawable() {
		return true;
	}


	/**
	 * Change the line color
	 * @param lineColor
	 */
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}


	/**
	 * Set the width of the line
	 * @param strokeWidth
	 */
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	
	
	

}
