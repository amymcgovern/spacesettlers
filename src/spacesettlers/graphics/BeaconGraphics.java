package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import spacesettlers.gui.JSpaceSettlersComponent;
import spacesettlers.objects.Beacon;
import spacesettlers.utilities.Position;

/**
 * The graphic for a beacon draws a beacon
 * on the screen.  The actual drawing code comes
 * from the spacewar1 simulator.
 * 
 * @author amy
 *
 */
public class BeaconGraphics extends SpacewarGraphics {
    public static final Color BEACON_COLOR = Color.YELLOW;
    public static final Color BEACON_LINE_COLOR = new Color(255,215,0);

    private final Beacon beacon;

    public BeaconGraphics(Beacon b) {
    	super(Beacon.BEACON_RADIUS, Beacon.BEACON_RADIUS);
        this.beacon = b;
    }

    public void draw(Graphics2D g) {
        float radius = Beacon.BEACON_RADIUS;
        float diameter = Beacon.BEACON_RADIUS * 2;
        
        Ellipse2D.Double shape = new Ellipse2D.Double(drawLocation.getX() - radius,
        		drawLocation.getY() - radius, diameter, diameter);

        g.setColor(BEACON_COLOR);
        g.fill(shape);

        g.setStroke(JSpaceSettlersComponent.THICK_STROKE);
        g.setColor(BEACON_LINE_COLOR);
        g.draw(shape);

        // add an E to make it clear it is an energy beacon
		g.setPaint(Color.BLACK);
		g.drawString("E", (int) drawLocation.getX()-3, (int) drawLocation.getY() + 4);

    }

	/**
	 * Only draw if the beacon is alive
	 */
	public boolean isDrawable() {
		if (beacon.isAlive() && beacon.isDrawable()) {
			return true;
		}
		return false;
	}

	/**
	 * Return the actual location of the beacon
	 */
	public Position getActualLocation() {
		return beacon.getPosition();
	}

	
	
}
