package spacesettlers.clients;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.MoveAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.graphics.CircleGraphics;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.graphics.TargetGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.objects.weapons.AbstractWeapon;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
/**
 * A team of random agents
 * 
 * The agents pick a random location in space and aim for it.  They shoot somewhat randomly also.
 * @author amy
 *
 */
public class RandomTeamClient extends TeamClient {
	HashSet<SpacewarGraphics> graphics;
	boolean fired = false;
	Position currentTarget;
	
	public static int RANDOM_MOVE_RADIUS = 200;
	public static double SHOOT_PROBABILITY = 0.1;
	
	@Override
	public void initialize(Toroidal2DPhysics space) {
		graphics = new HashSet<SpacewarGraphics>();
		currentTarget = null;
	}

	@Override
	public void shutDown(Toroidal2DPhysics space) {
		// TODO Auto-generated method stub

	}


	@Override
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> randomActions = new HashMap<UUID, AbstractAction>();
		
		
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;
				AbstractAction current = ship.getCurrentAction();
				
				// if we finished, make a new spot in space to aim for
				if (current == null || current.isMovementFinished(space)) {
					Position currentPosition = ship.getPosition();
					Position newGoal = space.getRandomFreeLocationInRegion(random, Ship.SHIP_RADIUS, (int) currentPosition.getX(), 
							(int) currentPosition.getY(), RANDOM_MOVE_RADIUS);
					currentTarget = newGoal;
					MoveAction newAction = null;
					newAction = new MoveAction(space, currentPosition, newGoal);
					//System.out.println("Ship is at " + currentPosition + " and goal is " + newGoal);
					randomActions.put(ship.getId(), newAction);
				} else {
					randomActions.put(ship.getId(), ship.getCurrentAction());
				}
				
			} else {
				// it is a base and random doesn't do anything to bases
				randomActions.put(actionable.getId(), new DoNothingAction());
		}
			

		}
	
		return randomActions;
	
	}


	@Override
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {
	}

	@Override
	public Set<SpacewarGraphics> getGraphics() {
		HashSet<SpacewarGraphics> newGraphics = new HashSet<SpacewarGraphics>();  
		if (currentTarget != null) {
			SpacewarGraphics graphic = new TargetGraphics(20, getTeamColor(), this.currentTarget);
			newGraphics.add(graphic);
		}
		return newGraphics;
	}


	@Override
	/**
	 * Random never purchases 
	 */
	public Map<UUID, PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects, 
			ResourcePile resourcesAvailable, 
			PurchaseCosts purchaseCosts) {
		return new HashMap<UUID,PurchaseTypes>();

	}

	/**
	 * This is the new way to shoot (and use any other power up once they exist)
	 */
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		
		HashMap<UUID, SpaceSettlersPowerupEnum> powerupMap = new HashMap<UUID, SpaceSettlersPowerupEnum>();
		
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;
				if (random.nextDouble() < SHOOT_PROBABILITY) {
					AbstractWeapon newBullet = ship.getNewWeapon(SpaceSettlersPowerupEnum.FIRE_MISSILE);
					if (newBullet != null) {
						powerupMap.put(ship.getId(), SpaceSettlersPowerupEnum.FIRE_MISSILE);
						//System.out.println("Firing!");
					}
				}
			}
		}
		return powerupMap;
	}

	@Override
	public Map<UUID, AbstractGameAgent> getGameSearch(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		return null;
	}


}