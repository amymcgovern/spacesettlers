package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import spacesettlers.gui.JSpaceSettlersComponent;
import spacesettlers.objects.AiCore;
import spacesettlers.utilities.Position;

/**
 * The graphic for an AiCore draws an AiCore
 * on the screen.  The actual drawing code comes
 * from the spacewar1 simulator.
 * 
 * This is a modification of previous graphics.
 * 
 * @author amy
 * @author Josiah
 *
 */
public class CoreGraphics extends SpacewarGraphics {
    public static final Color CORE_COLOR = Color.GRAY;
    private Color coreColor;
    

    private final AiCore core;

    public CoreGraphics(AiCore aiCore, Color teamColor) {
    	super(AiCore.CORE_RADIUS, AiCore.CORE_RADIUS);
    	this.coreColor = teamColor;
    	
        this.core = aiCore;
    }

    public void draw(Graphics2D g) {
        float radius = AiCore.CORE_RADIUS;
        float diameter = AiCore.CORE_RADIUS * 2;
        
        Ellipse2D.Double shape = new Ellipse2D.Double(drawLocation.getX() - radius,
        		drawLocation.getY() - radius, diameter, diameter);

        g.setColor(CORE_COLOR);
        g.fill(shape);

        g.setStroke(JSpaceSettlersComponent.STROKE);
        g.setColor(coreColor);
        g.draw(shape);

        // add the text "Ai" to make it clear it is an AiCore
		g.setPaint(Color.BLACK);
		g.drawString("AI", (int) drawLocation.getX()-5, (int) drawLocation.getY() + 4);

    }

	/**
	 * Only draw if the AiCore is alive
	 */
	public boolean isDrawable() {
		if (core.isAlive() && core.isDrawable()) {
			return true;
		}
		return false;
	}

	/**
	 * Return the actual location of the AiCore
	 */
	public Position getActualLocation() {
		return core.getPosition();
	}

	
	
}
