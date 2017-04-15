package spacesettlers.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import spacesettlers.gui.JSpaceSettlersComponent;
import spacesettlers.objects.Flag;
import spacesettlers.utilities.Position;

/**
 * Graphics for a flag:  pretty simple, just draws a right-facing triangle and fills it in
 * with the right color
 * @author amy
 *
 */
public class FlagGraphics extends SpacewarGraphics {
    public static final Shape FLAG_SHAPE = new Polygon(new int[]{-1, 0, 1, -1}, new int[]{-1, 1, -1, -1}, 4);
    Color flagColor;
    Flag flag;
    
    double scale;
    
	public FlagGraphics(Flag flag, Color color) {
		super(flag.getRadius(), flag.getRadius());
		this.flagColor = color;
		this.flag = flag;
		// scale by half of the radius since it is a double unit triangle
		this.scale = flag.getRadius();
	}

	@Override
	public Position getActualLocation() {
		return flag.getPosition();
	}

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setStroke(JSpaceSettlersComponent.THIN_STROKE);
		
        final AffineTransform transform =
                AffineTransform.getTranslateInstance(drawLocation.getX(), drawLocation.getY());
        transform.rotate(-Math.PI / 2.0);

    	transform.scale(scale, scale);

        Shape newFlagShape = transform.createTransformedShape(FLAG_SHAPE);

        // color the flag to match the team
        graphics.setPaint(flagColor);
        graphics.fill(newFlagShape);
	}

	@Override
	public boolean isDrawable() {
		if (flag.isAlive() && flag.isDrawable()) {
			return true;
		} else {
			return false;
		}
	
	}

}
