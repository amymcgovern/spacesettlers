package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import spacesettlers.gui.JSpaceSettlersComponent;
import spacesettlers.objects.Drone;
import spacesettlers.objects.Flag;
import spacesettlers.utilities.Position;

/**
 * The ship drawing class, mostly re-used from spacewar 1 (but modified to allow
 * a ship to be any color and to show its number in the team)
 * 
 * @author amy
 */
public class DroneGraphics extends SpacewarGraphics {
    public static final Color THRUST_COLOR = new Color(255, 242, 23);
    public static final Color THRUST_SPUTTER_COLOR = new Color(193, 72, 8);
    public static final Color SHIELD_COLOR = new Color(190, 40, 140);
    public static final Shape DRONE_SHAPE = new Polygon(new int[]{81, 81, 41, 41, 51, 51, -51, -51, -41, -41, -81, -81, 81}, new int[]{-93, 93, 93, 71, 71, -22, -22, 71, 71, 93, 93, -93, -93}, 13);
    public static final Shape THRUST_SHAPE = new Polygon(new int[]{44, -44, 0}, new int[]{65, 65, 200}, 3);
    public static final Shape THRUST_SPUTTER_SHAPE = new Polygon(new int[]{30, -30, 0}, new int[]{65, 65, 170}, 3);
	public static final Color DRONE_SHIELD_COLOR = Color.WHITE;

    private Drone drone;
    Color droneColor, idColor;

    /**
     * Create a new drone graphic and specify the drone and the color for the team
     * 
     * @param droneColor
     * @param drone
     */
    public DroneGraphics(Drone drone, Color droneColor) {
    	super(drone.getRadius(), drone.getRadius());
    	this.droneColor = droneColor;
    	this.drone = drone;
        this.idColor = new Color(255 - droneColor.getRed(), 255 - droneColor.getGreen(), 255 - droneColor.getBlue());

    }
    

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setStroke(JSpaceSettlersComponent.THIN_STROKE);
				
        final AffineTransform transform =
                AffineTransform.getTranslateInstance(drawLocation.getX(), drawLocation.getY());
        transform.rotate(drone.getPosition().getOrientation() + Math.PI / 2);
        transform.scale(.10, .10);

//        if (ship.getActiveCommand().thrust) {
//            final Shape newThrustShape = transform.createTransformedShape(THRUST_SHAPE);
//            g.setPaint(THRUST_COLOR);
//            g.fill(newThrustShape);
//        } else if (ship.getActiveCommand().thrust) {
//            final Shape newThrustShape = transform.createTransformedShape(THRUST_SPUTTER_SHAPE);
//            g.setPaint(THRUST_SPUTTER_COLOR);
//            g.fill(newThrustShape);
//        }

        Shape newDroneShape = transform.createTransformedShape(DRONE_SHAPE);

        // color the drone to match the team
        graphics.setPaint(droneColor);
        graphics.fill(newDroneShape);

        // now show the information about the drone
        graphics.setFont(JSpaceSettlersComponent.FONT12);
        
        // show the id of the drone inside the drone
       // graphics.setPaint(idColor);
        //graphics.drawString(ship.getId().toString(), (int) drawLocation.getX() - 3, (int) drawLocation.getY() + 3);

        String number = Integer.toString((int)drone.getEnergy());
        graphics.setPaint(idColor);
        graphics.drawString(number, (int) drawLocation.getX() + 12, (int) drawLocation.getY() + 12);
        
//        number = Integer.toString(ship.getDeaths());
//        g.drawString(number, ship.getPosition().getX() + 12, ship.getPosition().getY() + 1);
//
//        if (ship.getName() != null) {
//			g.drawString(ship.getName(), ship.getPosition().getX() + 12, ship.getPosition().getY() - 10);
//		}
//
        // show the velocity
        graphics.setStroke(JSpaceSettlersComponent.THIN_STROKE);
        graphics.drawLine(
                (int) drawLocation.getX(),
                (int) drawLocation.getY(),
                (int) (drawLocation.getX() + drone.getPosition().getTranslationalVelocityX()),
                (int) (drawLocation.getY() + drone.getPosition().getTranslationalVelocityY()));

//        number = Integer.toString(ship.getKills());
//        g.setPaint(Color.PINK);
//        g.drawString(number, ship.getPosition().getX() - 24, ship.getPosition().getY() - 10);
//
//        number = Integer.toString(ship.getHits());
//        g.setPaint(Color.GRAY);
//        g.drawString(number, ship.getPosition().getX() - 24, ship.getPosition().getY() + 1);

        // paint the number of cores currently held by the drone
        number = Integer.toString(drone.getNumCores());
        graphics.setPaint(this.droneColor);
        graphics.drawString(number, (int) drawLocation.getX() + 24, (int) drawLocation.getY() + 23);
        
        
        // if the drone is shielded, show the shield around it
        if (drone.isShielded()) {
	        double shieldRadius = drone.getRadius() + 4;
	        final Ellipse2D.Double shieldShape = new Ellipse2D.Double(drawLocation.getX() - shieldRadius,
	        		drawLocation.getY() - shieldRadius, 2 * shieldRadius, 2 * shieldRadius);
	        graphics.setStroke(JSpaceSettlersComponent.THIN_STROKE);
	    	graphics.setColor(DRONE_SHIELD_COLOR);
	        graphics.draw(shieldShape);
        }

        // if the drone is frozen from an EMP, show a ring around it (in the drone's own color)
        if (drone.getFreezeCount() > 0) {
	        double shieldRadius = drone.getRadius() + 2;
	        final Ellipse2D.Double shieldShape = new Ellipse2D.Double(drawLocation.getX() - shieldRadius,
	        		drawLocation.getY() - shieldRadius, 2 * shieldRadius, 2 * shieldRadius);
	        graphics.setStroke(JSpaceSettlersComponent.THIN_STROKE);
	    	graphics.setColor(droneColor);
	        graphics.draw(shieldShape);
        }
        
        // if the drone has a flag, put a tiny flag inside the drone
        if (drone.isCarryingFlag()) {
        	AffineTransform transformFlag =
                    AffineTransform.getTranslateInstance(drawLocation.getX(), drawLocation.getY());
        	transformFlag.scale(Flag.FLAG_RADIUS / 2.0, Flag.FLAG_RADIUS / 2.0);
        	Shape tinyFlag = transformFlag.createTransformedShape(FlagGraphics.FLAG_SHAPE);
        	if (droneColor.equals(Color.WHITE)) {
            	graphics.setColor(Color.BLACK);
        	} else {
            	graphics.setColor(Color.WHITE);
        	}
        	graphics.fill(tinyFlag);
        }
	
	}

	/**
	 * Only draw a drone if it is alive and drawable (drones should always be drawable)
	 */
	public boolean isDrawable() {
		if (drone.isAlive() && drone.isDrawable()) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Return the location of the center of the drone
	 */
	public Position getActualLocation() {
		return drone.getPosition();
	}

}
