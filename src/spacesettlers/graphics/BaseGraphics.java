package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import spacesettlers.gui.JSpaceSettlersComponent;
import spacesettlers.objects.Base;
import spacesettlers.utilities.Position;

/**
 * Draw a base. The base's transparency is based on its available healing
 * energy.  The graphics is mostly based on the spacewar 1 shadow for bases.
 * 
 * @author amy
 *
 */
public class BaseGraphics extends SpacewarGraphics {
	Base base;
	Color teamColor;
	public static final Color BASE_SHIELD_COLOR = Color.WHITE;
	
	/**
	 * Make a new base graphic for a specific team
	 * @param base
	 * @param teamColor
	 */
	public BaseGraphics(Base base, Color teamColor) {
		super(Base.BASE_RADIUS, Base.BASE_RADIUS);
		this.base = base;
		this.teamColor = teamColor;
	}

	/**
	 * Return the location of the base
	 */
	public Position getActualLocation() {
		return base.getPosition();
	}

	@Override
	public void draw(Graphics2D graphics) {
		float radius = Base.BASE_RADIUS;
		float diameter = Base.BASE_RADIUS * 2;

		// change the base's transparency based upon the energy level
		int alpha =  (int) (((float) base.getEnergy() / base.getMaxEnergy()) * 255.0);
		Color energyColor = new Color(teamColor.getRed(), teamColor.getGreen(), teamColor.getBlue(), alpha);
		graphics.setColor(energyColor);
		
		graphics.fillOval((int)(drawLocation.getX() - radius), (int)(drawLocation.getY() - radius), 
					(int)diameter, (int)diameter);
		
		// draw an outline around the base (in case its healing energy goes to 0)
        final Ellipse2D.Double shape = new Ellipse2D.Double(drawLocation.getX() - radius,
        		drawLocation.getY() - radius, diameter, diameter);
        graphics.setStroke(JSpaceSettlersComponent.STROKE);
		Color outlineColor = new Color(teamColor.getRed(), teamColor.getGreen(), teamColor.getBlue());
    	graphics.setColor(outlineColor);
        graphics.draw(shape);

		// if the base is shielded, put a white circle around the outside
        if (base.isShielded()) {
	        double shieldRadius = radius + 8;
	        final Ellipse2D.Double shieldShape = new Ellipse2D.Double(drawLocation.getX() - shieldRadius,
	        		drawLocation.getY() - shieldRadius, 2 * shieldRadius, 2 * shieldRadius);
	        graphics.setStroke(JSpaceSettlersComponent.THIN_STROKE);
	    	graphics.setColor(BASE_SHIELD_COLOR);
	        graphics.draw(shieldShape);
        }
        
		// show the healing energy level of the base
		final Font font = new Font("Arial", Font.BOLD, 12);
		graphics.setFont(font);

		String number = Integer.toString(base.getHealingEnergy());
		graphics.setPaint(JSpaceSettlersComponent.TEXT_COLOR);
		graphics.drawString(number, (int) drawLocation.getX() + 12, (int) drawLocation.getY() + 12);
		
		// show the resourcesAvailable collected by the team at this base
		//number = Integer.toString(base.getMoney());
		//graphics.setPaint(JSpaceSettlersComponent.TEXT_COLOR);
		//graphics.drawString(number, (int) drawLocation.getX() + 12, (int) drawLocation.getY() - 12);
		
		// if it is a home base, put a H inside it
		if (base.isHomeBase()) {
			graphics.setPaint(Color.BLACK);
			graphics.drawString("H", (int) drawLocation.getX()-4, (int) drawLocation.getY() + 4);
		}
		
        //Paint the number of cores held by the base.
        number = Integer.toString(base.getNumCores()); //Merge this with number
        graphics.setPaint(this.teamColor);
        graphics.drawString(number, (int) drawLocation.getX() - 24, (int) drawLocation.getY() + 23);
		
		
	}

	@Override
	public boolean isDrawable() {
		return (base.isAlive() && base.isDrawable());
	}

}
