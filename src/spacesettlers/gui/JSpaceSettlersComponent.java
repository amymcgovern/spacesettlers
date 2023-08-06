package spacesettlers.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JComponent;

import spacesettlers.clients.Team;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.graphics.StarGraphics;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Star;
import spacesettlers.simulator.SpaceSettlersSimulator;
import spacesettlers.utilities.Position;

/**
 * Main Space Settlers window component.  Note some of this code is from the original
 * spacewar gui.
 * 
 * @author amy
 */
@SuppressWarnings("serial")
public class JSpaceSettlersComponent extends JComponent {
    public static final Color TEXT_COLOR = new Color(0, 218, 159);
    public static final Font FONT12 = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT10 = new Font("SansSerif", Font.BOLD, 10);
    public static final Font FONT8 = new Font("SansSerif", Font.BOLD, 8);
    
    public static final BasicStroke THIN_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final BasicStroke STROKE = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final BasicStroke THICK_STROKE = new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	int height, width;
	
	AffineTransform scaleTransform;
	
	/**
	 * Current state of the simulator
	 */
	SpaceSettlersSimulator simulator;
	
	public JSpaceSettlersComponent(int height, int width) {
		super();
		this.height = height;
		this.width = width;

        //setMaximumSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));
        //System.out.println("Setting preferred width " + width + " and height " + height);
        //setMinimumSize(new Dimension(width, height));
        //setSize(new Dimension(width, height));
	}

	/**
	 * Draw the space background and all the sub components
	 */
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

        final Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        		RenderingHints.VALUE_ANTIALIAS_ON);

        // handle a race condition in the GUI
        if (scaleTransform == null) {
        	return;
        }
        
        graphics.transform(scaleTransform);

        // draw space
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.width, this.height);

        // handle an annoying race condition in the GUI
        if (simulator == null) {
        	return;
        }

        // draw graphic for all the objects
        Set<AbstractObject> allObjects = new LinkedHashSet<AbstractObject>(simulator.getAllObjects());

        for (AbstractObject object : allObjects) {
        	SpacewarGraphics graphic = object.getGraphic();
        	if (graphic != null) {
            	if (graphic.isDrawable()) {
            		drawShadow(graphic, graphics);
            	}
        	}
        }
        
        // and draw any team graphics from this round
        for (Team team : simulator.getTeams()) {
        	Set<SpacewarGraphics>teamShadows = team.getGraphics();
        	if (teamShadows != null) {
        		for (SpacewarGraphics graphic : teamShadows) {
                	if (graphic.isDrawable()) {
                		drawShadow(graphic, graphics);
                	}
        		}
        	}
        }
	}
	
	/**
	 * Handles drawing things in a tororodially wrapped world.  Code
	 * comes from the spacewar 1 simulator.
	 * 
	 * @param graphic SpacewarGraphic to be drawn
	 * @param graphics java level Graphics object
	 */
    private void drawShadow(final SpacewarGraphics graphic, final Graphics2D graphics) {
        Position position = graphic.getActualLocation();
        
        // don't draw a graphic at a bad position
        if (position == null) {
        	return;
        }
        
        double x = position.getX();
        double y = position.getY();

        if (x < graphic.getHalfWidth()) {
            //goes off screen to left
            graphic.setDrawLocation(new Position(x + width, y));
            graphic.draw(graphics);
            if (y < graphic.getHalfHeight()) {
                //also goes off screen to the bottom
                graphic.setDrawLocation(new Position(x, y + height));
                graphic.draw(graphics);
                graphic.setDrawLocation(new Position(x + width, y + height));
                graphic.draw(graphics);
            } else if (y >= height - graphic.getHalfHeight()) {
                //also goes off screen to the top
                graphic.setDrawLocation(new Position(x, y - height));
                graphic.draw(graphics);
                graphic.setDrawLocation(new Position(x + width, y - height));
                graphic.draw(graphics);
            }
        } else if (x >= width - graphic.getHalfWidth()) {
            //goes off screen to right
            graphic.setDrawLocation(new Position(x - width, y));
            graphic.draw(graphics);
            if (y < graphic.getHalfHeight()) {
                //goes off screen to bottom
                graphic.setDrawLocation(new Position(x, y + height));
                graphic.draw(graphics);
                graphic.setDrawLocation(new Position(x - width, y + height));
                graphic.draw(graphics);
            } else if (y >= height - graphic.getHalfHeight()) {
                //also goes off screen to the top
                graphic.setDrawLocation(new Position(x, y - height));
                graphic.draw(graphics);
                graphic.setDrawLocation(new Position(x - width, y - height));
                graphic.draw(graphics);
            }
        } else if (y < graphic.getHalfHeight()) {
            //goes off screen to bottom
            graphic.setDrawLocation(new Position(x, y + height));
            graphic.draw(graphics);
        } else if (y >= height - graphic.getHalfHeight()) {
            //goes off screen to top
            graphic.setDrawLocation(new Position(x, y - height));
            graphic.draw(graphics);
        }

        graphic.setDrawLocation(position);
        graphic.draw(graphics);
    }

    /**
     * Set the current sim
     * @param simulator
     */
	public void setSimulator(SpaceSettlersSimulator spacewarSimulator) {
		this.simulator = spacewarSimulator;
	}

	/**
	 * Set the transformation for scale for the main (space settlers) GUI
	 * 
	 * @param graphicsTransform
	 */
	public void setScaleTransform(AffineTransform scaleTransform) {
		this.scaleTransform = scaleTransform;
	}

	
}
