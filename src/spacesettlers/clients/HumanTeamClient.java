package spacesettlers.clients;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.MoveAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.actions.RawAction;
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.graphics.LineGraphics;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.graphics.StarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;
/**
 * The human client (parses key strokes to fly a single ship)
 * @author amy
 *
 */
public class HumanTeamClient extends TeamClient {
	public enum HumanKeyPressed {
		UP, DOWN, RIGHT, LEFT, FIRE, NONE;
	}
	
	/**
	 * The last key pressed by the human (to be used in getting the action)
	 */
	HumanKeyPressed lastKeyPressed;
	
	private static double HUMAN_ACCEL = 5.0;
	private static double HUMAN_TURN_ACCEL = 0.5;
	
	/**
	 * The keyboard listener for the human client
	 */
	HumanClientKeyListener humanKeyListener;
	
	/**
	 * The mouse listener for the human client
	 */
	HumanMouseListener humanMouseListener;
	
	/**
	 * Last place the human clicked
	 */
	Position lastMouseClick;
	
	/**
	 * The action to move to the last click
	 */
	AbstractAction mouseClickMove;
	
	/**
	 * Minimum distance clicks need to be apart before it will move the ship again
	 */
	public double CLICK_DISTANCE = 5;
	
	/**
	 * graphics to add in each step (if graphics are on, otherwise it is ignored)
	 */
	private ArrayList<SpacewarGraphics> graphicsToAdd;
	
	
	@Override
	public void initialize(Toroidal2DPhysics space) {
		humanKeyListener = new HumanClientKeyListener();
		humanMouseListener = new HumanMouseListener();
		lastMouseClick = null;
		mouseClickMove = null;
		graphicsToAdd = new ArrayList<SpacewarGraphics>();
	}

	@Override
	public void shutDown(Toroidal2DPhysics space) {
		// TODO Auto-generated method stub

	}

	/**
	 * Look at the last key pressed by the human and do its movement
	 */
	@Override
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();
		for (AbstractObject actionable : actionableObjects) {
			if (actionable instanceof Ship) {
				// get the current position
				Ship ship = (Ship) actionable;
				Position myPosition = ship.getPosition();
				Vector2D currentVelocity = myPosition.getTranslationalVelocity();
				RawAction action = null;
				double angularVel = myPosition.getAngularVelocity();
				
				// if the key was up or down, accelerate along the current line
				if (lastKeyPressed == HumanKeyPressed.UP) {
					Vector2D newVel = new Vector2D(HUMAN_ACCEL * Math.cos(myPosition.getOrientation()), 
							HUMAN_ACCEL * Math.sin(myPosition.getOrientation()));
					newVel.add(currentVelocity);
					action = new RawAction(newVel, 0);
				} else if (lastKeyPressed == HumanKeyPressed.DOWN) {
					Vector2D newVel = new Vector2D(-HUMAN_ACCEL * Math.cos(myPosition.getOrientation()), 
							-HUMAN_ACCEL * Math.sin(myPosition.getOrientation()));
					newVel.add(currentVelocity);
					action = new RawAction(newVel, 0);
				}
				
				// if the key was right or left, turn 
				if (lastKeyPressed == HumanKeyPressed.RIGHT) {
					action = new RawAction(0, HUMAN_TURN_ACCEL);
				} else if (lastKeyPressed == HumanKeyPressed.LEFT) {
					action = new RawAction(0, -HUMAN_TURN_ACCEL);
				}
				
				// was the mouse clicked?
				if (lastMouseClick != null) {
					if (mouseClickMove == null || mouseClickMove.isMovementFinished(space) || space.findShortestDistance(lastMouseClick, myPosition) > CLICK_DISTANCE) {
						mouseClickMove = new MoveAction(space, myPosition, lastMouseClick);
						
						graphicsToAdd.add(new StarGraphics(3, super.teamColor, lastMouseClick));
						LineGraphics line = new LineGraphics(myPosition, lastMouseClick, 
								space.findShortestDistanceVector(myPosition, lastMouseClick));
						line.setLineColor(super.teamColor);
						graphicsToAdd.add(line);
						
					}
					actions.put(actionable.getId(), mouseClickMove);
				} else {
					actions.put(actionable.getId(), action);
				}
				
			} else {
				// can't really control anything but the ship
				actions.put(actionable.getId(), new DoNothingAction());
			}
		}
		return actions;
	}

	@Override
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {
		// reset so the human has to press again to move again (otherwise
		// it does strange things like fly when you don't tell it anything on
		// acceleration!)
		lastKeyPressed = HumanKeyPressed.NONE;
		for (AbstractObject actionable : actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;
				if (!ship.isAlive()) {
					lastMouseClick = null;
					mouseClickMove = null;
				}
			}
		}
	}

	@Override
	public Set<SpacewarGraphics> getGraphics() {
		HashSet<SpacewarGraphics> graphics = new HashSet<SpacewarGraphics>();
		graphics.addAll(graphicsToAdd);
		graphicsToAdd.clear();
		return graphics;
	}

	@Override
	/**
	 * Human purchases (right now it never purchases, this will be added to the UI later)
	 */
	public Map<UUID, PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects,
			ResourcePile resourcesAvailable, 
			PurchaseCosts purchaseCosts) {
		// TODO Auto-generated method stub
		return new HashMap<UUID,PurchaseTypes>();
	}

	@Override
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, SpaceSettlersPowerupEnum> powerUps = new HashMap<UUID, SpaceSettlersPowerupEnum>();

		if (lastKeyPressed == HumanKeyPressed.FIRE) {
			for (AbstractActionableObject actionableObject : actionableObjects){
				SpaceSettlersPowerupEnum powerup = SpaceSettlersPowerupEnum.FIRE_MISSILE;
				powerUps.put(actionableObject.getId(), powerup);
			}
		}
		
		return powerUps;
	
	}

	@Override
	public KeyAdapter getKeyAdapter() {
		return humanKeyListener;
	}
	

	/**
	 * The key listener for the human client (internal class)
	 * 
	 * using any arrow keys (numeric or not):
	 * 		up means accelerate forward
	 * 		down means decelerate
	 * 		right means turn right
	 * 		left means turn left
	 * 
	 * space means fire
	 * 
	 * @author amy
	 *
	 */
	class HumanClientKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			//System.out.println("human pressed " + key);
			
			if (key == KeyEvent.VK_UP || key == KeyEvent.VK_KP_UP) {
				// UP means accelerate forward
				lastKeyPressed = HumanKeyPressed.UP;
				//System.out.println("human pressed UP ");
			} else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_KP_DOWN) {
				// DOWN means decelerate
				lastKeyPressed = HumanKeyPressed.DOWN;
			} else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_KP_RIGHT) {
				// turn RIGHT
				lastKeyPressed = HumanKeyPressed.RIGHT;
			} else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_KP_LEFT) {
				// turn LEFT
				lastKeyPressed = HumanKeyPressed.LEFT;
			} else if (key == KeyEvent.VK_SPACE) {
				// fire
				lastKeyPressed = HumanKeyPressed.FIRE;
			}
			
			if (lastKeyPressed != null) {
				lastMouseClick = null;
			}
		}
	}

	@Override
	public MouseAdapter getMouseAdapter(AffineTransform mouseTransform) {
		this.mouseTransform = mouseTransform;

		return humanMouseListener;
	}

	/**
	 * Human mouse interface.  To move, click somewhere on the screen and the ship does a MoveAction to that point.
	 * Don't forget space is toroidal!
	 * 
	 * @author amy
	 *
	 */
	class HumanMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			Point point = e.getPoint();
			
			Point2D newPoint = new Point2D.Double(0, 0);
			mouseTransform.transform(point, newPoint); 
			
			// only listen to the right button
			if (e.getButton() == MouseEvent.BUTTON3 || e.isAltDown()) {
				//System.out.println("User right clicked at " + point.x + ", " + point.y);
				lastMouseClick = new Position(newPoint.getX(), newPoint.getY());
			}
		}
	}
	
	@Override
	public Map<UUID, AbstractGameAgent> getGameSearch(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
