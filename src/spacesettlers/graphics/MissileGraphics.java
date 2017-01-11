package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import spacesettlers.objects.weapons.Missile;
import spacesettlers.utilities.Position;

/**
 * Draw a missile 
 * @author amy
 *
 */
public class MissileGraphics extends SpacewarGraphics {
	Missile missile;
	//Color missile_color = new Color(200, 200, 200);
	Color firingShipColor;
	
	public MissileGraphics(Missile missle) {
        super((int)(missle.getRadius() * 2), (int)(missle.getRadius()  * 2));

        firingShipColor = missle.getFiringShip().getTeamColor();
        
        this.missile = missle;
	}

	/**
	 * Return the position of the bullet
	 */
	public Position getActualLocation() {
		return missile.getPosition();
	}

	@Override
	public void draw(Graphics2D graphics) {
        float radius = missile.getRadius();
        float diameter = radius * 2;

        graphics.setColor(firingShipColor);
        graphics.fill(new Ellipse2D.Float((float)drawLocation.getX() - radius,
                (float)drawLocation.getY() - radius, diameter, diameter));

	}

	@Override
	public boolean isDrawable() {
		return missile.isAlive();
	}

}
