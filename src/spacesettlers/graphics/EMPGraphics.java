package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import spacesettlers.objects.weapons.EMP;
import spacesettlers.utilities.Position;

/**
 * Draw a EMP (looks like a missile but different color, and with stripes) 
 * @author amy
 *
 */
public class EMPGraphics extends SpacewarGraphics {
	EMP emp;
	//public static final Color EMP_OUTER_COLOR = new Color(200, 0, 200);
	public static final Color EMP_INNER_COLOR = new Color(200, 200, 200);
	Color outerColor;
	
	public EMPGraphics(EMP emp) {
        super((int)(emp.getRadius() * 2), (int)(emp.getRadius()  * 2));
        outerColor = emp.getFiringShip().getTeamColor();
        
        this.emp = emp;
	}

	/**
	 * Return the position of the emp
	 */
	public Position getActualLocation() {
		return emp.getPosition();
	}

	@Override
	public void draw(Graphics2D graphics) {
        float radius = emp.getRadius();
        float diameter = radius * 2;

        // inner ring
        graphics.setColor(outerColor);
        graphics.fill(new Ellipse2D.Double(drawLocation.getX() - radius,
                drawLocation.getY() - radius, diameter, diameter));

        radius = radius - 2;
        diameter = radius * 2;

        graphics.setColor(EMP_INNER_COLOR);
        graphics.fill(new Ellipse2D.Double(drawLocation.getX() - radius,
                drawLocation.getY() - radius, diameter, diameter));
        
	}

	@Override
	public boolean isDrawable() {
		return emp.isAlive();
	}

}
