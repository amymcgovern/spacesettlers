package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import spacesettlers.gui.JSpaceSettlersComponent;
import spacesettlers.utilities.Position;

/**
 * Contributed by Michael Short,  spring 2013
 * 
 */
public class RectangleGraphics extends SpacewarGraphics {

  /** Width of the rectangle */
  private int width;
  /** Height of the rectangle */
  private int height;
  /** Color of the graphic */
  private Color color;
  /** (x,y) location in the 2D plane */
  private Position currentPosition;
  /** Whether to fill or just outline the shape when drawn */
  private boolean fill;
  
  public static final int DEFAULT_SIZE = 10;


  /**
   * @param width The width of the rectangle
   * @param height The height of the rectangle
   * @param color Color of the graphic
   * @param position (x,y) position in the 2D plane
   */
  public RectangleGraphics(int width, int height, Color color, Position position) {
    super(width, height);
    this.width = width;
    this.height = height;
    this.color = color;
    this.currentPosition = position;
  }

  /**
   * Shorthand for making a square.
   * @param size The width and height of the rectangle
   * @param color Color of the graphic
   * @param position (x,y) position in the 2D plane
   */
  public RectangleGraphics(int size, Color color, Position position) {
    super(size, size);
    this.width = size;
    this.height = size;
    this.color = color;
    this.currentPosition = position;
  }
  
  /**
   * Default constructor with size = 10 pixels
   * @param color Color of the graphic
   * @param position (x,y) position in the 2D plane
   */
  public RectangleGraphics(Color color, Position position) {
    super(DEFAULT_SIZE, DEFAULT_SIZE);
    this.width = DEFAULT_SIZE;
    this.height = DEFAULT_SIZE;
    this.color = color;
    this.currentPosition = position;
  }

  public Position getActualLocation() {
    return currentPosition;
  }

  public void setCurrentPosition(Position currentPosition) {
    this.currentPosition = currentPosition;
  }

  public void draw(Graphics2D graphics) {
    Rectangle2D.Double shape = new Rectangle2D.Double(drawLocation.getX(), drawLocation.getY(), width, height);
    graphics.setColor(color);
    if(fill)
      graphics.fill(shape);
    graphics.setStroke(JSpaceSettlersComponent.STROKE);
    graphics.draw(shape);

  }

  public boolean isDrawable() {
    return true;
  }

  /**
   * Whether to fill the graphic or just outline it when drawing
   * @param fill True if rectangle should be filled
   */
  public void setFill (boolean fill) {
    this.fill = fill;
  }

}
