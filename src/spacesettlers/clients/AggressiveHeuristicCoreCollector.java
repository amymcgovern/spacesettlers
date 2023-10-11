package spacesettlers.clients;

import spacesettlers.actions.*;
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.game.HeuristicGameAgent;
import spacesettlers.game.HeuristicTicTacToe3DGameAgent;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.*;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;

import java.util.*;

/**
 * Kills ships, collects cores and tries not to die
 * 
 * @author William McGovern-Fagg
 */
public class AggressiveHeuristicCoreCollector extends TeamClient {
	HashMap <Ship, Ship> shipToSelfMap;
	HashMap <UUID, Boolean> aimingForBase;
	HashMap <UUID, Boolean> goingForCore;
	HashMap <UUID, Boolean> justHitBase;

	Ship target;
	Boolean isHunting;

	UUID coreCollectorID;
	double weaponsProbability = 1;

	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();

		// loop through each ship
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;

				// the first time we initialize, decide which ship is the core collector
				if (coreCollectorID == null) {
					coreCollectorID = ship.getId();
				}
				
				AbstractAction action;
				if (ship.getId().equals(coreCollectorID)) {
					// get the cores
					action = getCoreCollectorAction(space, ship);
				} else {
					// this ship will try to shoot other ships so its movements take it towards the nearest other ship not on our team
					action = getWeaponShipAction(space, ship);
				}

				actions.put(ship.getId(), action);
				
			} else {
				// it is a base.  Heuristically decide when to use the shield (TODO)
				actions.put(actionable.getId(), new DoNothingAction());
			}
		} 
		return actions;
	}
	
	/**
	 * Gets the action for the core collecting ship
	 * @param space
	 * @param ship
	 * @return
	 */
	private AbstractAction getCoreCollectorAction(Toroidal2DPhysics space,
			Ship ship) {
		AbstractAction current = ship.getCurrentAction();
		Position currentPosition = ship.getPosition();

		// aim for a beacon if there isn't enough energy
		if (ship.getEnergy() < 2000) {
			isHunting = false;
			Beacon beacon = pickNearestBeacon(space, ship);
			AbstractAction newAction = null;
			// if there is no beacon, then just skip a turn
			if (beacon == null) {
				newAction = new DoNothingAction();
			} else {
				newAction = new MoveToObjectAction(space, currentPosition, beacon);
			}
			aimingForBase.put(ship.getId(), false);
			goingForCore.put(ship.getId(), false);
			return newAction;
		}

		// if the ship has enough resourcesAvailable, take it back to base
		if (ship.getNumCores() > 0) {
			isHunting = false;
			Base base = findNearestBase(space, ship);
			AbstractAction newAction = new MoveToObjectAction(space, currentPosition, base);
			aimingForBase.put(ship.getId(), true);
			goingForCore.put(ship.getId(), false);
			return newAction;
		}

		// if there is a nearby core, go get it
		AiCore nearbyCore = pickNearestCore(space, ship, 10000);
		if (nearbyCore != null) {
			isHunting = false;
			Position newGoal = nearbyCore.getPosition();
			AbstractAction newAction = new MoveToObjectAction(space, currentPosition, nearbyCore);
			aimingForBase.put(ship.getId(), false);
			goingForCore.put(ship.getId(), true);
			return newAction;
		}


		// otherwise aim for a ship
		if (current == null || current.isMovementFinished(space) ||
				(justHitBase.containsKey(ship.getId()) && justHitBase.get(ship.getId()))) {
			isHunting = true;
			aimingForBase.put(ship.getId(), false);
			goingForCore.put(ship.getId(), false);
			justHitBase.put(ship.getId(), false);			
			target = pickNearestEnemyShip(space, ship);
			AbstractAction newAction = null;
			if (target == null) {
				// there is no core available so collect a beacon
				Beacon beacon = pickNearestBeacon(space, ship);
				// if there is no beacon, then just skip a turn
				if (beacon == null) {
					newAction = new DoNothingAction();
				} else {
					newAction = new MoveToObjectAction(space, currentPosition, beacon);
				}
			} else {
				shipToSelfMap.put(target, ship);
				newAction = new MoveToObjectAction(space, currentPosition, target,
						target.getPosition().getTranslationalVelocity());
			}
			return newAction;
		} else {
			return ship.getCurrentAction();
		}
	}

	/**
	 * Gets the action for the weapons based ship
	 * @param space
	 * @param ship
	 * @return
	 */
	private AbstractAction getWeaponShipAction(Toroidal2DPhysics space,
			Ship ship) {
		AbstractAction current = ship.getCurrentAction();
		Position currentPosition = ship.getPosition();

		// aim for a beacon if there isn't enough energy
		if (ship.getEnergy() < 2000) {
			Beacon beacon = pickNearestBeacon(space, ship);
			AbstractAction newAction = null;
			// if there is no beacon, then just skip a turn
			if (beacon == null) {
				newAction = new DoNothingAction();
			} else {
				newAction = new MoveToObjectAction(space, currentPosition, beacon);
			}
			aimingForBase.put(ship.getId(), false);
			goingForCore.put(ship.getId(), false);
			return newAction;
		}

		// if the ship has enough resourcesAvailable, take it back to base
		if (ship.getResources().getTotal() > 500 || ship.getNumCores() > 0) {
			Base base = findNearestBase(space, ship);
			AbstractAction newAction = new MoveToObjectAction(space, currentPosition, base);
			aimingForBase.put(ship.getId(), true);
			goingForCore.put(ship.getId(), false);
			return newAction;
		}

		// if there is a nearby core, go get it
		AiCore nearbyCore = pickNearestCore(space, ship, 200);
		if (nearbyCore != null) {
			Position newGoal = nearbyCore.getPosition();
			AbstractAction newAction = new MoveToObjectAction(space, currentPosition, nearbyCore);
			goingForCore.put(ship.getId(), true);
			aimingForBase.put(ship.getId(), false);
			return newAction;
		}

		// otherwise aim for the nearest enemy ship
		if (current == null || current.isMovementFinished(space) || 
				(justHitBase.containsKey(ship.getId()) && justHitBase.get(ship.getId()))) {
			aimingForBase.put(ship.getId(), false);
			goingForCore.put(ship.getId(), false);
			justHitBase.put(ship.getId(), false);			

			Ship enemy = pickNearestEnemyShip(space, ship);

			AbstractAction newAction = null;

			if (enemy == null) {
				// there is no enemy available so collect a beacon
				Beacon beacon = pickNearestBeacon(space, ship);
				// if there is no beacon, then just skip a turn
				if (beacon == null) {
					newAction = new DoNothingAction();
				} else {
					newAction = new MoveToObjectAction(space, currentPosition, beacon);
				}
			} else {
				newAction = new MoveToObjectAction(space, currentPosition, enemy,
						enemy.getPosition().getTranslationalVelocity());
			}
			return newAction;
		} else {
			return ship.getCurrentAction();
		}
	}

	/**
	 * Find the nearest core to this ship that falls within the specified minimum distance
	 * @param space
	 * @param ship
	 * @return
	 */
	private AiCore pickNearestCore(Toroidal2DPhysics space, Ship ship, int minimumDistance) {
		Set<AiCore> cores = space.getCores();

		AiCore closestCore = null;
		double bestDistance = minimumDistance;

		for (AiCore core : cores) {
			double dist = space.findShortestDistance(ship.getPosition(), core.getPosition());
			if (dist < bestDistance && !core.getTeamName().equals(ship.getTeamName())) {
				bestDistance = dist;
				closestCore = core;
			}
		}

		return closestCore;
	}	
	

	/**
	 * Find the nearest ship on another team and aim for it
	 * @param space
	 * @param ship
	 * @return
	 */
	private Ship pickNearestEnemyShip(Toroidal2DPhysics space, Ship ship) {
		double minDistance = Double.POSITIVE_INFINITY;
		Ship nearestShip = null;
		for (Ship otherShip : space.getShips()) {
			// don't aim for our own team (or ourself)
			if (otherShip.getTeamName().equals(ship.getTeamName())) {
				continue;
			}
			
			double distance = space.findShortestDistance(ship.getPosition(), otherShip.getPosition());
			if (distance < minDistance) {
				minDistance = distance;
				nearestShip = otherShip;
			}
		}
		
		return nearestShip;
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
	 * Find the nearest beacon to this ship
	 * @param space
	 * @param ship
	 * @return
	 */
	private Beacon pickNearestBeacon(Toroidal2DPhysics space, Ship ship) {
		// get the current beacons
		Set<Beacon> beacons = space.getBeacons();

		Beacon closestBeacon = null;
		double bestDistance = Double.POSITIVE_INFINITY;

		for (Beacon beacon : beacons) {
			double dist = space.findShortestDistance(ship.getPosition(), beacon.getPosition());
			if (dist < bestDistance) {
				bestDistance = dist;
				closestBeacon = beacon;
			}
		}

		return closestBeacon;
	}



	@Override
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {

		for (Ship ship : shipToSelfMap.keySet()) {
			if (target != null && (!target.isAlive() || target.isMoveable())) {
				continue;
			}
		}

		
		// check to see who bounced off bases
		for (UUID shipId : aimingForBase.keySet()) {
			if (aimingForBase.get(shipId)) {
				Ship ship = (Ship) space.getObjectById(shipId);
				if (ship.getResources().getTotal() == 0 ) {
					// we hit the base (or died, either way, we are not aiming for base now)
					//System.out.println("Hit the base and dropped off resources");
					aimingForBase.put(shipId, false);
					justHitBase.put(shipId, true);
					goingForCore.put(ship.getId(), false);
				}
			}
		}
		


	}

	@Override
	public void initialize(Toroidal2DPhysics space) {
		target = null;
		isHunting = false;
		shipToSelfMap = new HashMap<Ship, Ship>();
		coreCollectorID = null;
		aimingForBase = new HashMap<UUID, Boolean>();
		goingForCore = new HashMap<UUID, Boolean>();
		justHitBase = new HashMap<UUID, Boolean>();
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
	 * If there is enough resourcesAvailable, buy a base.  Place it by finding a ship that is sufficiently
	 * far away from the existing bases
	 */
	public Map<UUID, PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects, 
			ResourcePile resourcesAvailable, 
			PurchaseCosts purchaseCosts) {

		HashMap<UUID, PurchaseTypes> purchases = new HashMap<UUID, PurchaseTypes>();
		double BASE_BUYING_DISTANCE = 200;
		boolean bought_base = false;

		if (purchaseCosts.canAfford(PurchaseTypes.BASE, resourcesAvailable)) {
			for (AbstractActionableObject actionableObject : actionableObjects) {
				if (actionableObject instanceof Ship) {
					Ship ship = (Ship) actionableObject;
					Set<Base> bases = space.getBases();

					// how far away is this ship to a base of my team?
					double maxDistance = Double.MIN_VALUE;
					for (Base base : bases) {
						if (base.getTeamName().equalsIgnoreCase(getTeamName())) {
							double distance = space.findShortestDistance(ship.getPosition(), base.getPosition());
							if (distance > maxDistance) {
								maxDistance = distance;
							}
						}
					}

					if (maxDistance > BASE_BUYING_DISTANCE) {
						purchases.put(ship.getId(), PurchaseTypes.BASE);
						bought_base = true;
						//System.out.println("Buying a base!!");
						break;
					}
				}
			}		
		} 
		
		// see if you can buy EMPs
		if (purchaseCosts.canAfford(PurchaseTypes.POWERUP_EMP_LAUNCHER, resourcesAvailable)) {
			for (AbstractActionableObject actionableObject : actionableObjects) {
				if (actionableObject instanceof Ship) {
					Ship ship = (Ship) actionableObject;
					
					if (!ship.getId().equals(coreCollectorID) && !ship.isValidPowerup(PurchaseTypes.POWERUP_EMP_LAUNCHER.getPowerupMap())) {
						purchases.put(ship.getId(), PurchaseTypes.POWERUP_EMP_LAUNCHER);
					}
				}
			}		
		} 
		

		// can I buy a ship?
		if (purchaseCosts.canAfford(PurchaseTypes.SHIP, resourcesAvailable) && bought_base == false) {
			for (AbstractActionableObject actionableObject : actionableObjects) {
				if (actionableObject instanceof Base) {
					Base base = (Base) actionableObject;
					
					purchases.put(base.getId(), PurchaseTypes.SHIP);
					break;
				}

			}

		}


		return purchases;
	}

	/**
	 * The core collector doesn't use power ups but the weapons one does (at random)
	 * @param space
	 * @param actionableObjects
	 * @return
	 */
	@Override
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, SpaceSettlersPowerupEnum> powerUps = new HashMap<UUID, SpaceSettlersPowerupEnum>();

		for (AbstractActionableObject actionableObject : actionableObjects){
			SpaceSettlersPowerupEnum powerup = SpaceSettlersPowerupEnum.values()[random.nextInt(SpaceSettlersPowerupEnum.values().length)];

			if (isHunting && actionableObject.isValidPowerup(powerup) && space.findShortestDistance(actionableObject.getPosition(), target.getPosition()) < 100){
				powerUps.put(actionableObject.getId(), powerup);
			}
		}
		
		
		return powerUps;
	}

	/**
	 * Plays the game randomly (just to show how to use the interface)
	 * 
	 */
	@Override
	public Map<UUID, AbstractGameAgent> getGameSearch(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HeuristicGameAgent agent = new HeuristicGameAgent();

		HashMap<UUID, AbstractGameAgent> actions = new HashMap<UUID, AbstractGameAgent>();

		// loop through each ship
		for (AbstractObject actionable :  actionableObjects) {
			actions.put(actionable.getId(), agent);
		}
		return actions;
	}

}
