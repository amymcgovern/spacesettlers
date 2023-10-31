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
	Vector2D startToFinish;
	Color lineColor;
	float strokeWidth;

	/**
	 * Draw a ray from the starting point with dimensions of the vector
	 * 
	 * @param startPoint    starting point
	 * @param startToFinish vector pointing from start to end
	 */
	public LineGraphics(Position startPoint, Vector2D startToFinish) {
		// Height/width for the line segment comes from the vector
		super((int) Math.abs(startToFinish.getYValue()), (int) Math.abs(startToFinish.getXValue()));

		this.lineColor = DEFAULT_LINE_COLOR;
		this.strokeWidth = 2f;

		this.startToFinish = startToFinish;

		double endX = startPoint.getX() + startToFinish.getXValue();
		double endY = startPoint.getY() + startToFinish.getYValue();

		this.startPoint = startPoint;
		this.endPoint = new Position(endX, endY);
	}

	/**
	 * WARNING: OBSOLETE CONSTRUCTOR: Consider using an alternative.
	 * Draw a line segment from the starting point to the ending point
	 * 
	 * @param startPoint    starting point
	 * @param endPoint      UNUSED BY CONSTRUCTOR
	 * @param startToFinish vector pointing from start to end
	 */
	public LineGraphics(Position startPoint, Position endPoint, Vector2D startToFinish) {
		this(startPoint, startToFinish);
	}

	@Override
	/*
	 * Returns the midpoint (center) of the line, used for toroidal wraparound.
	 */
	public Position getActualLocation() {
		double x = (startPoint.getX() + endPoint.getX()) / 2;
		double y = (startPoint.getY() + endPoint.getY()) / 2;

		return new Position(x, y);
	}

	@Override
	/**
	 * Draws the line using the object's drawLocation and the startToFinish vector
	 */
	public void draw(Graphics2D graphics) {
		graphics.setColor(lineColor);
		graphics.setStroke(new BasicStroke(strokeWidth));

		// Get the start and end point of the line
		// using the midpoint (drawLocation) and vector
		double startX = drawLocation.getX() - 0.5 * startToFinish.getXValue();
		double startY = drawLocation.getY() - 0.5 * startToFinish.getYValue();
		double endX = drawLocation.getX() + 0.5 * startToFinish.getXValue();
		double endY = drawLocation.getY() + 0.5 * startToFinish.getYValue();

		graphics.drawLine(
				(int) startX, (int) startY,
				(int) endX, (int) endY);
	}

	/**
	 * Lines are always drawn
	 */
	public boolean isDrawable() {
		return true;
	}

	/**
	 * Change the line color
	 * 
	 * @param lineColor
	 */
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * Set the width of the line
	 * 
	 * @param strokeWidth
	 */
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

}
