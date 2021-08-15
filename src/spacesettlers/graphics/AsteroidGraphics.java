package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import spacesettlers.objects.Asteroid;
import spacesettlers.utilities.Position;

/**
 * Asteroid graphics - mostly taken from the obstacle shadow in spacewar1
 * 
 * @author amy
 */
public class AsteroidGraphics extends SpacewarGraphics {
    public static final Color REGULAR_ASTEROID_COLOR = new Color(126, 96, 58);
    public static final Color REGULAR_LINE_COLOR = new Color(162, 124, 76);
    public static final Color WATER_ASTEROID_COLOR = new Color(0,0,255);
    public static final Color FUEL_ASTEROID_COLOR = new Color(0,255,0);
    public static final Color METALS_ASTEROID_COLOR = new Color(192, 192, 192);
    public static final Color MOVEABLE_LINE_COLOR = new Color(1, 124, 76);

    Asteroid asteroid;
    
    public AsteroidGraphics(Asteroid asteroid) {
		super(asteroid.getRadius(), asteroid.getRadius());
		this.asteroid = asteroid;
	}


	@Override
	public void draw(Graphics2D graphics) {
        final float radius = asteroid.getRadius();
        final float diameter = asteroid.getRadius() * 2;


        // show minable asteroids in a different color
        if (asteroid.isMineable()) {
        	// mineable asteroids have concentric circles showing the proportion of the different resources
        	
        	double fuel = asteroid.getFuelProportion();
        	double water = asteroid.getWaterProportion();
        	double metals = asteroid.getMetalsProportion();
        	
        	double fuelDiameter = fuel * diameter;
            double waterDiameter = (water + fuel) * diameter;
            double metalsDiameter = Math.round((water + fuel + metals) * diameter);

            Ellipse2D.Double shape = new Ellipse2D.Double(drawLocation.getX() - (metalsDiameter / 2),
            		drawLocation.getY() - (metalsDiameter / 2), metalsDiameter, metalsDiameter);
            graphics.setColor(METALS_ASTEROID_COLOR);
            graphics.fill(shape);

            shape = new Ellipse2D.Double(drawLocation.getX() - (waterDiameter / 2),
            		drawLocation.getY() - (waterDiameter / 2), waterDiameter, waterDiameter);
            graphics.setColor(WATER_ASTEROID_COLOR);
            graphics.fill(shape);

            shape = new Ellipse2D.Double(drawLocation.getX() - (fuelDiameter / 2),
            		drawLocation.getY() - (fuelDiameter / 2), fuelDiameter, fuelDiameter);
            graphics.setColor(FUEL_ASTEROID_COLOR);
            graphics.fill(shape);

            // if the asteroid is gameable, it should have a G inside it
            if (asteroid.isGameable()) {
            	graphics.setPaint(Color.WHITE);
            	graphics.drawString("G", (int) drawLocation.getX()-3, (int) drawLocation.getY() + 4);
            }
            
        } else {
        	// non-mineable asteroid is just a brown circle
            Ellipse2D.Double fullShape = new Ellipse2D.Double(drawLocation.getX() - radius,
            		drawLocation.getY() - radius, diameter, diameter);

            graphics.setColor(REGULAR_ASTEROID_COLOR);
            graphics.fill(fullShape);
        }
        
	}

	/**
	 * Only draw the asteroid if it alive and drawable
	 */
	public boolean isDrawable() {
		return (asteroid.isAlive() && asteroid.isDrawable());
	}

	/**
	 * Return the actual location of the asteroid
	 */
	public Position getActualLocation() {
		return asteroid.getPosition();
	}

}
