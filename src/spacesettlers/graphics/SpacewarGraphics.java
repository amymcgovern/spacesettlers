package spacesettlers.graphics;

import java.awt.Graphics2D;
import spacesettlers.utilities.Position;

/**
 * Abstract class for all graphics in the spacesettlers simulator.
 * The idea is based on shadow  from the spacewar1 simulator 
 * 
 * @author amy
 */
abstract public class SpacewarGraphics {
	/**
	 * Temporary variable used to deal with toroidial worlds.  The main
	 * GUI can set this to handle the wrapping
	 */
	Position drawLocation;
	
	/**
	 * Used for the toroidal mapping.  Size information for the graphic
	 */
	int halfHeight, halfWidth, height, width;
	
	/**
	 * Initialize the size information for a graphic.
	 * Required of all graphics.
	 * 
	 * @param height
	 * @param width
	 */
	public SpacewarGraphics(int height, int width) {
		halfHeight = height / 2;
		halfWidth = width / 2;
		this.height = height;
		this.width = width;
	}
	
	/**
	 * Return the actual location of the object
	 * 
	 * @return the actualLocation
	 */
	abstract public Position getActualLocation();
	
	/**
	 * Get the drawing location
	 * 
	 * @return the drawLocation
	 */
	public Position getDrawLocation() {
		return drawLocation;
	}
	
	/**
	 * @return the halfHeight
	 */
	public int getHalfHeight() {
		return halfHeight;
	}

	/**
	 * @return the halfWidth
	 */
	public int getHalfWidth() {
		return halfWidth;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Set the drawing location (should only
	 * be called by the main GUI)
	 * 
	 * @param drawLocation the drawLocation to set
	 */
	public void setDrawLocation(Position drawLocation) {
		this.drawLocation = drawLocation;
	}

	/**
	 * All graphics must be able to draw themselves
	 * @param graphics Graphics object where the graphic draws
	 */
	abstract public void draw(Graphics2D graphics);
	
	/**
	 * Return true if the graphic is drawn then and false otherwise
	 * @return
	 */
	abstract public boolean isDrawable();
}
