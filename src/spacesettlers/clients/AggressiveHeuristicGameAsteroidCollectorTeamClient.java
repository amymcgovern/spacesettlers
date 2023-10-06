package spacesettlers.clients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.MoveToObjectAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.game.*;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.AiCore;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;

/**
 * Collects nearby asteroids and brings them to the base, picks up beacons as needed for energy.
 * 
 * If there is more than one ship, only one ship is dedicated to picking up asteroids and
 * the other is dedicated to using weapons
 * 
 * @author amy
 */
public class AggressiveHeuristicGameAsteroidCollectorTeamClient extends TeamClient {
	HashMap <UUID, Ship> asteroidToShipMap;
	HashMap <UUID, Boolean> aimingForBase;
	HashMap <UUID, Boolean> goingForCore;
	HashMap <UUID, Boolean> justHitBase;
	
	UUID asteroidCollectorID;
	double weaponsProbability = 1;

	/**
	 * Assigns ships to asteroids and beacons, as described above
	 */
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();

		// loop through each ship
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;

				// the first time we initialize, decide which ship is the asteroid collector
				if (asteroidCollectorID == null) {
					asteroidCollectorID = ship.getId();
				}
				
				AbstractAction action;
				if (ship.getId().equals(asteroidCollectorID)) {
					// get the asteroids
					action = getAsteroidCollectorAction(space, ship);
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
	 * Gets the action for the asteroid collecting ship
	 * @param space
	 * @param ship
	 * @return
	 */
	private AbstractAction getAsteroidCollectorAction(Toroidal2DPhysics space,
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
			aimingForBase.put(ship.getId(), false);
			goingForCore.put(ship.getId(), true);
			return newAction;
		}


		// otherwise aim for the asteroid
		if (current == null || current.isMovementFinished(space) ||
				(justHitBase.containsKey(ship.getId()) && justHitBase.get(ship.getId()))) {
			aimingForBase.put(ship.getId(), false);
			goingForCore.put(ship.getId(), false);
			justHitBase.put(ship.getId(), false);			
			Asteroid asteroid = this.pickHighestValueNearestFreeAsteroidGamingIfPossible(space, ship);

			AbstractAction newAction = null;

			if (asteroid == null) {
				// there is no asteroid available so collect a beacon
				Beacon beacon = pickNearestBeacon(space, ship);
				// if there is no beacon, then just skip a turn
				if (beacon == null) {
					newAction = new DoNothingAction();
				} else {
					newAction = new MoveToObjectAction(space, currentPosition, beacon);
				}
			} else {
				asteroidToShipMap.put(asteroid.getId(), ship);
				newAction = new MoveToObjectAction(space, currentPosition, asteroid, 
						asteroid.getPosition().getTranslationalVelocity());
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
			if (dist < bestDistance) {
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
	 * Returns the asteroid of highest value that isn't already being chased by this team
	 * 
	 * @return
	 */
	private Asteroid pickHighestValueNearestFreeAsteroidGamingIfPossible(Toroidal2DPhysics space, Ship ship) {
		Set<Asteroid> asteroids = space.getAsteroids();
		int bestMoney = Integer.MIN_VALUE;
		Asteroid bestAsteroid = null;
		double minDistance = Double.MAX_VALUE;

		for (Asteroid asteroid : asteroids) {
			if (!asteroidToShipMap.containsKey(asteroid.getId())) {
				if (asteroid.isMineable() && asteroid.getResources().getTotal() > bestMoney && asteroid.isGameable()) {
					double dist = space.findShortestDistance(asteroid.getPosition(), ship.getPosition());
					if (dist < minDistance) {
						bestMoney = asteroid.getResources().getTotal();
						//System.out.println("Considering asteroid " + asteroid.getId() + " as a best one");
						bestAsteroid = asteroid;
						minDistance = dist;
					}
				}
			}
		}
		
		// if there was no gaming one available, then just return a regular one
		if (bestAsteroid == null) {
			return pickHighestValueNearestFreeAsteroid(space, ship);
		}
		
		//System.out.println("Best asteroid has " + bestMoney);
		return bestAsteroid;
	}
	
	/**
	 * Returns the asteroid of highest value that isn't already being chased by this team
	 * 
	 * @return
	 */
	private Asteroid pickHighestValueNearestFreeAsteroid(Toroidal2DPhysics space, Ship ship) {
		Set<Asteroid> asteroids = space.getAsteroids();
		int bestMoney = Integer.MIN_VALUE;
		Asteroid bestAsteroid = null;
		double minDistance = Double.MAX_VALUE;

		for (Asteroid asteroid : asteroids) {
			if (!asteroidToShipMap.containsKey(asteroid.getId())) {
				if (asteroid.isMineable() && asteroid.getResources().getTotal() > bestMoney) {
					double dist = space.findShortestDistance(asteroid.getPosition(), ship.getPosition());
					if (dist < minDistance) {
						bestMoney = asteroid.getResources().getTotal();
						//System.out.println("Considering asteroid " + asteroid.getId() + " as a best one");
						bestAsteroid = asteroid;
						minDistance = dist;
					}
				}
			}
		}
		//System.out.println("Best asteroid has " + bestMoney);
		return bestAsteroid;
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
		ArrayList<Asteroid> finishedAsteroids = new ArrayList<Asteroid>();

		for (UUID asteroidId : asteroidToShipMap.keySet()) {
			Asteroid asteroid = (Asteroid) space.getObjectById(asteroidId);
			if (asteroid != null && (!asteroid.isAlive() || asteroid.isMoveable())) {
				finishedAsteroids.add(asteroid);
				//System.out.println("Removing asteroid from map");
			}
		}

		for (Asteroid asteroid : finishedAsteroids) {
			asteroidToShipMap.remove(asteroid.getId());
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
		asteroidToShipMap = new HashMap<UUID, Ship>();
		asteroidCollectorID = null;
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
					
					if (!ship.getId().equals(asteroidCollectorID) && !ship.isValidPowerup(PurchaseTypes.POWERUP_EMP_LAUNCHER.getPowerupMap())) {
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
	 * The asteroid collector doesn't use power ups but the weapons one does (at random)
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
			
			Boolean gettingCore = false;
			if (goingForCore.containsKey(actionableObject.getId())) {
				gettingCore = goingForCore.get(actionableObject.getId());
			}
			if (!actionableObject.getId().equals(asteroidCollectorID) &&
					!gettingCore && 
					actionableObject.isValidPowerup(powerup) && random.nextDouble() < weaponsProbability){
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
