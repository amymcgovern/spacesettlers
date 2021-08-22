package spacesettlers.clients;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.MoveToObjectAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.AiCore;
import spacesettlers.objects.Base;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;

/**
 * Assigns each member of the team to collect a core.  If there 
 * are more core than team members, the remaining ones do nothing.
 *  
 * @author amy
 */
public class CoreCollectorTeamClient extends TeamClient {
	/**
	 * Map of the core to which ship is aiming for it
	 */
	HashMap<AiCore, Ship> coreToShipMap;
	HashMap<Ship, AiCore> shipToCoreMap;

	/**
	 * Send each ship to a beacon
	 */
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();

		// loop through each ship
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;
				AbstractAction current = ship.getCurrentAction();
				
				// if the ship has a core, take it to base
				if (ship.getNumCores() > 0) {
					Base base = findNearestBase(space, ship);
					AbstractAction newAction = new MoveToObjectAction(space, ship.getPosition(), base);
					actions.put(ship.getId(), newAction);
				} else {
					// go find a core (or keep aiming for one if you already are)
					if (current == null || !shipToCoreMap.containsKey(ship)) {
						Position currentPosition = ship.getPosition();
						AiCore core = pickNearestFreeCore(space, ship);
	
						AbstractAction newAction = null;
	
						if (core == null) {
							// there is no core available so do nothing
							newAction = new DoNothingAction();
						} else {
							coreToShipMap.put(core, ship);
							shipToCoreMap.put(ship, core);
							Position newGoal = core.getPosition();
							newAction = new MoveToObjectAction(space, currentPosition, core);
						}
						actions.put(ship.getId(), newAction);
					} else {
						// update the goal location since cores move
						UUID myCoreId = shipToCoreMap.get(ship).getId();
						AiCore myCore = (AiCore) space.getObjectById(myCoreId);
						Position currentPosition = ship.getPosition();
						AbstractAction newAction = null;
						newAction = new MoveToObjectAction(space, currentPosition, myCore);
						
						actions.put(ship.getId(), newAction);
					}
				}
			} else {
				// it is a base and core collector doesn't do anything to bases
				actions.put(actionable.getId(), new DoNothingAction());
			}
		}

		return actions;
	}

	/**
	 * Find the base for this team nearest to this ship
	 * 
	 * @param space
	 * @param ship
	 * @return
	 */
	private Base findNearestBase(Toroidal2DPhysics space, Ship ship) {
		double minDistance = Double.MAX_VALUE;
		Base nearestBase = null;

		for (Base base : space.getBases()) {
			if (base.getTeamName().equalsIgnoreCase(ship.getTeamName())) {
				double dist = space.findShortestDistance(ship.getPosition(), base.getPosition());
				if (dist < minDistance) {
					minDistance = dist;
					nearestBase = base;
				}
			}
		}
		return nearestBase;
	}


	/**
	 * Find the nearest free core to this ship
	 * @param space
	 * @param ship
	 * @return
	 */
	private AiCore pickNearestFreeCore(Toroidal2DPhysics space, Ship ship) {
		Set<AiCore> cores = space.getCores();

		AiCore closestCore = null;
		double bestDistance = Double.POSITIVE_INFINITY;

		for (AiCore core : cores) {
			if (coreToShipMap.containsKey(core)) {
				continue;
			}

			double dist = space.findShortestDistance(ship.getPosition(), core.getPosition());
			if (dist < bestDistance) {
				bestDistance = dist;
				closestCore = core;
			}
		}

		return closestCore;
	}




	/**
	 * Clean up data structure including core maps
	 */
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {

		// once a core has been picked up, remove it from the list 
		// of core being pursued (so it can be picked up at its
		// new location)
		for (AiCore core : space.getCores()) {
			if (!core.isAlive()) {
				shipToCoreMap.remove(coreToShipMap.get(core));
				coreToShipMap.remove(core);
			}
		}

	}

	@Override
	public void initialize(Toroidal2DPhysics space) {
		coreToShipMap = new HashMap<AiCore, Ship>();
		shipToCoreMap = new HashMap<Ship, AiCore>();
	}

	@Override
	public void shutDown(Toroidal2DPhysics space) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<SpacewarGraphics> getGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * Beacon collector never purchases
	 */
	public Map<UUID, PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects, 
			ResourcePile resourcesAvailable, 
			PurchaseCosts purchaseCosts) {
		return new HashMap<UUID,PurchaseTypes>();
	}


	@Override
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<UUID, AbstractGameAgent> getGameSearch(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		return null;
	}


}
