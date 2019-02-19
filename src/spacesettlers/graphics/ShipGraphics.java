package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import spacesettlers.gui.JSpaceSettlersComponent;
import spacesettlers.objects.Flag;
import spacesettlers.objects.Ship;
import spacesettlers.utilities.Position;

/**
 * The ship drawing class, mostly re-used from spacewar 1 (but modified to allow
 * a ship to be any color and to show its number in the team)
 * 
 * @author amy
 */
public class ShipGraphics extends SpacewarGraphics {
    public static final Color THRUST_COLOR = new Color(255, 242, 23);
    public static final Color THRUST_SPUTTER_COLOR = new Color(193, 72, 8);
    public static final Color SHIELD_COLOR = new Color(190, 40, 140);
    public static final Shape SHIP_SHAPE = new Polygon(new int[]{54, 108, 100, 141, 85, 73, 106, 89, 80, 40, 54, -54,
            -40, -80, -89, -106, -73, -85, -141, -100, -108, -54, 54}, new int[]{-89, 3, 18, 89, 89, 69, 69, 38, 53, 53,
            75, 75, 53, 53, 38, 69, 69, 89, 89, 18, 3, -89, -89}, 23);
    public static final Shape THRUST_SHAPE = new Polygon(new int[]{44, -44, 0}, new int[]{65, 65, 200}, 3);
    public static final Shape THRUST_SPUTTER_SHAPE = new Polygon(new int[]{30, -30, 0}, new int[]{65, 65, 170}, 3);
	public static final Color SHIP_SHIELD_COLOR = Color.WHITE;

    private Ship ship;
    Color shipColor, idColor;

    /**
     * Create a new ship graphic and specify the ship and the color for the team
     * 
     * @param shipColor
     * @param ship
     */
    public ShipGraphics(Ship ship, Color shipColor) {
    	super(ship.getRadius(), ship.getRadius());
    	this.shipColor = shipColor;
    	this.ship = ship;
        this.idColor = new Color(255 - shipColor.getRed(), 255 - shipColor.getGreen(), 255 - shipColor.getBlue());

    }
    

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setStroke(JSpaceSettlersComponent.THIN_STROKE);
				
        final AffineTransform transform =
                AffineTransform.getTranslateInstance(drawLocation.getX(), drawLocation.getY());
        transform.rotate(ship.getPosition().getOrientation() + Math.PI / 2);
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

        Shape newShipShape = transform.createTransformedShape(SHIP_SHAPE);

        // color the ship to match the team
        graphics.setPaint(shipColor);
        graphics.fill(newShipShape);

        // now show the information about the ship
        graphics.setFont(JSpaceSettlersComponent.FONT12);
        
        // show the id of the ship inside the ship
       // graphics.setPaint(idColor);
        //graphics.drawString(ship.getId().toString(), (int) drawLocation.getX() - 3, (int) drawLocation.getY() + 3);

        String number = Integer.toString((int)ship.getEnergy());
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
                (int) (drawLocation.getX() + ship.getPosition().getTranslationalVelocityX()),
                (int) (drawLocation.getY() + ship.getPosition().getTranslationalVelocityY()));

//        number = Integer.toString(ship.getKills());
//        g.setPaint(Color.PINK);
//        g.drawString(number, ship.getPosition().getX() - 24, ship.getPosition().getY() - 10);
//
//        number = Integer.toString(ship.getHits());
//        g.setPaint(Color.GRAY);
//        g.drawString(number, ship.getPosition().getX() - 24, ship.getPosition().getY() + 1);
//
        // paint the number of beacons
        number = Integer.toString(ship.getNumBeacons());
        graphics.setPaint(BeaconGraphics.BEACON_COLOR);
        graphics.drawString(number, (int) drawLocation.getX() - 24, (int) drawLocation.getY() + 23);

        // paint the number of cores currently held by the ship
        number = Integer.toString(ship.getNumCores());
        graphics.setPaint(this.shipColor);
        graphics.drawString(number, (int) drawLocation.getX() + 24, (int) drawLocation.getY() + 23);
        
        
        // if the ship is shielded, show the shield around it
        if (ship.isShielded()) {
	        double shieldRadius = ship.getRadius() + 4;
	        final Ellipse2D.Double shieldShape = new Ellipse2D.Double(drawLocation.getX() - shieldRadius,
	        		drawLocation.getY() - shieldRadius, 2 * shieldRadius, 2 * shieldRadius);
	        graphics.setStroke(JSpaceSettlersComponent.THIN_STROKE);
	    	graphics.setColor(SHIP_SHIELD_COLOR);
	        graphics.draw(shieldShape);
        }

        // if the ship is frozen from an EMP, show a ring around it (in the ship's own color)
        if (ship.getFreezeCount() > 0) {
	        double shieldRadius = ship.getRadius() + 2;
	        final Ellipse2D.Double shieldShape = new Ellipse2D.Double(drawLocation.getX() - shieldRadius,
	        		drawLocation.getY() - shieldRadius, 2 * shieldRadius, 2 * shieldRadius);
	        graphics.setStroke(JSpaceSettlersComponent.THIN_STROKE);
	    	graphics.setColor(shipColor);
	        graphics.draw(shieldShape);
        }
        
        // if the ship has a flag, put a tiny flag inside the ship
        if (ship.isCarryingFlag()) {
        	AffineTransform transformFlag =
                    AffineTransform.getTranslateInstance(drawLocation.getX(), drawLocation.getY());
        	transformFlag.scale(Flag.FLAG_RADIUS / 2.0, Flag.FLAG_RADIUS / 2.0);
        	Shape tinyFlag = transformFlag.createTransformedShape(FlagGraphics.FLAG_SHAPE);
        	if (shipColor.equals(Color.WHITE)) {
            	graphics.setColor(Color.BLACK);
        	} else {
            	graphics.setColor(Color.WHITE);
        	}
        	graphics.fill(tinyFlag);
        }
	
	}

	/**
	 * Only draw a ship if it is alive and drawable (ships should always be drawable)
	 */
	public boolean isDrawable() {
		if (ship.isAlive() && ship.isDrawable()) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Return the location of the center of the ship
	 */
	public Position getActualLocation() {
		return ship.getPosition();
	}

}
