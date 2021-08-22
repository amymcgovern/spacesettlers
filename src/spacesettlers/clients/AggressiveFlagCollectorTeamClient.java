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
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.AiCore;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Drone;
import spacesettlers.objects.Flag;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;

/**
 * An aggressive flag collector client that handles multiple agents in the team.  The heuristic works as follows:
 * 
 *   The nearest and healthy ship is assigned to go get the flag and bring it back.
 *   The other ships are assigned to resource collection.
 *   Resources are used to buy additional ships and bases (with the idea that bases are better to have near the 
 *   enemy flag locations).
 *   One ship is dispatched to harass the other team similar to AggressiveHeuritsicAsteroidCollector  
 *  
 * @author amy
 */
public class AggressiveFlagCollectorTeamClient extends TeamClient {
	HashMap <UUID, Ship> asteroidToShipMap;
	HashMap <UUID, Boolean> aimingForBase;
	HashMap <UUID, Boolean> huntingShip;
	HashMap <UUID, Boolean> justHitBase;
	HashMap <UUID, Boolean> goingForCore;
	double shootProb = 0.2;
	
	/**
	 * Assigns ships to asteroids and beacons, as described above
	 */
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();
		Ship flagShip;
		int numShips = 0;

		// get the flag carrier, if we have one
		flagShip = getFlagCarrier(space, actionableObjects);
		
		// we don't have a ship carrying a flag, so find the best choice (if it exists)
		if (flagShip == null) {
			flagShip = findHealthiestShipNearFlag(space, actionableObjects);
		}
		
		// count ships so we know if we can have a weapons ship
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				numShips++;
			}
		}

		// loop through each ship and assign it to either get energy (if needed for health) or
		// resources (as long as it isn't the flagShip)
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;

				AbstractAction action = null;

				if (flagShip != null && ship.equals(flagShip)) {
					if (flagShip.isCarryingFlag()) {
						//System.out.println("We have a flag carrier!");
						Base base = findNearestBase(space, ship);
						//System.out.println("Flag ship before computing action: " + flagShip);
						action = new MoveToObjectAction(space, ship.getPosition(), base);
						//System.out.println("Aiming for base with action " + action);
						aimingForBase.put(ship.getId(), true);
						//System.out.println("Flag ship after computing action: " + flagShip);
					} else {
						Flag enemyFlag = getEnemyFlag(space);
						action = new MoveToObjectAction(space, ship.getPosition(), enemyFlag, 
								enemyFlag.getPosition().getTranslationalVelocity());
					}
				} else {
					// we can only have a weapons ship if we have more than 2 ships (since we need
					// a ship to go get resources and help buy more ships/bases)
					if (numShips >= 2) {
						// see if we already have a hunting ship
						if (huntingShip.isEmpty()) {
							huntingShip.put(ship.getId(), true);
							System.out.println("Creating a hunting ship");
							// make one ship the hunter
							action = getWeaponShipAction(space, ship);
						} else {
							// if this ship is the current hunter, have it keep hunting
							if (huntingShip.containsKey(ship.getId())) {
								// make one ship the hunter
								action = getWeaponShipAction(space, ship);
								//System.out.println("Getting action for hunter");
							} else {
								// extra ship but not the hunter (likely a 4th ship)
								action = getAsteroidCollectorAction(space, ship);
							}
						}
					} else {
						// with 2 ships, we need to always collect resources so we can buy more ships for hunting
						// and resources later
					}
				}

				// save the action for this ship
				actions.put(ship.getId(), action);
			} else if(actionable instanceof Drone) {
				Drone drone = (Drone) actionable;
				AbstractAction action;

				action = drone.getDroneAction(space); //Or make up some action of your own! This just adds the default action back to the drone.
				actions.put(drone.getId(), action);
			} else {
				// bases do nothing
				actions.put(actionable.getId(), new DoNothingAction());
			}
		} 
		return actions;
	}

	/**
	 * Get the flag carrier (if there is one).  Return null if there isn't a current flag carrier
	 * 
	 * @param space
	 * @param actionableObjects
	 * @return
	 */
	private Ship getFlagCarrier(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;
				
				if (ship.isCarryingFlag()) {
					return ship;
				}
			}
		}
		return null;
	}
	
	/**
	 * Finds and returns the enemy flag
	 * @param space
	 * @return
	 */
	private Flag getEnemyFlag(Toroidal2DPhysics space) {
		Flag enemyFlag = null;
		for (Flag flag : space.getFlags()) {
			if (flag.getTeamName().equalsIgnoreCase(getTeamName())) {
				continue;
			} else {
				enemyFlag = flag;
			}
		}
		return enemyFlag;
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
			return newAction;
		}

		// if the ship has enough resourcesAvailable, take it back to base
		if (ship.getResources().getTotal() > 500) {
			Base base = findNearestBase(space, ship);
			AbstractAction newAction = new MoveToObjectAction(space, currentPosition, base);
			aimingForBase.put(ship.getId(), true);
			return newAction;
		}

		// did we bounce off the base?
		if (ship.getResources().getTotal() == 0 && ship.getEnergy() > 2000 && aimingForBase.containsKey(ship.getId()) && aimingForBase.get(ship.getId())) {
			current = null;
			aimingForBase.put(ship.getId(), false);
		}

		// otherwise aim for the nearest enemy ship
		if (current == null || current.isMovementFinished(space)) {
			aimingForBase.put(ship.getId(), false);
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
	 * Finds the ship with the highest health and nearest the flag
	 * 
	 * @param space
	 * @param actionableObjects
	 * @return
	 */
	private Ship findHealthiestShipNearFlag(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		double minDistance = Double.MAX_VALUE;
		double maxHealth = Double.MIN_VALUE;
		int minHealth = 2000;
		Ship bestShip = null;

		// first find the enemy flag
		Flag enemyFlag = getEnemyFlag(space);

		// now find the healthiest ship that has at least the required minimum energy 
		// if no ships meet that criteria, return null
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;
				
				double dist = space.findShortestDistance(ship.getPosition(), enemyFlag.getPosition());
				if (dist < minDistance && ship.getEnergy() > minHealth) {
					if (ship.getEnergy() > maxHealth) {
						minDistance = dist;
						maxHealth = ship.getEnergy();
						bestShip = ship;
					}
				}
			}
		}
		
		return bestShip;
		
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

		// if the ship has enough resourcesAvailable, take it back to base
		if (ship.getResources().getTotal() > 500  || ship.getNumCores() > 0) {
			Base base = findNearestBase(space, ship);
			AbstractAction newAction = new MoveToObjectAction(space, currentPosition, base);
			aimingForBase.put(ship.getId(), true);
			return newAction;
		}
		
		// aim for a beacon if there isn't enough energy
		if (ship.getEnergy() < 1000) {
			Beacon beacon = pickNearestBeacon(space, ship);
			AbstractAction newAction = null;
			// if there is no beacon, then just skip a turn
			if (beacon == null) {
				newAction = new DoNothingAction();
			} else {
				newAction = new MoveToObjectAction(space, currentPosition, beacon);
			}
			aimingForBase.put(ship.getId(), false);
			return newAction;
		}

		// if there is a nearby core, go get it
		AiCore nearbyCore = pickNearestCore(space, ship, 200);
		if (nearbyCore != null) {
			AbstractAction newAction = new MoveToObjectAction(space, currentPosition, nearbyCore);
			goingForCore.put(ship.getId(), true);
			aimingForBase.put(ship.getId(), false);
			return newAction;
		}


		// did we bounce off the base?
		if (current == null || current.isMovementFinished(space) ||
				(justHitBase.containsKey(ship.getId()) && justHitBase.get(ship.getId()))) {
			aimingForBase.put(ship.getId(), false);
			justHitBase.put(ship.getId(), false);			
			goingForCore.put(ship.getId(), false);
			current = null;
		}

		// otherwise aim for the asteroid
		if (current == null || current.isMovementFinished(space)) {
			aimingForBase.put(ship.getId(), false);
			justHitBase.put(ship.getId(), false);			
			goingForCore.put(ship.getId(), false);
			Asteroid asteroid = pickHighestValueNearestFreeAsteroid(space, ship);

			AbstractAction newAction = null;

			if (asteroid != null) {
				asteroidToShipMap.put(asteroid.getId(), ship);
				newAction = new MoveToObjectAction(space, currentPosition, asteroid, 
						asteroid.getPosition().getTranslationalVelocity());
			}
			
			return newAction;
		} 
		
		return ship.getCurrentAction();
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
				if (ship.getResources().getTotal() == 0 && ship.getNumFlags() == 0 && ship.getNumCores() == 0) {
					// we hit the base (or died, either way, we are not aiming for base now)
					//System.out.println("Hit the base and dropped off resources");
					aimingForBase.put(shipId, false);
					justHitBase.put(shipId, true);
					goingForCore.put(ship.getId(), false);
				}
			}
		}
	}

	/**
	 * Demonstrates one way to read in knowledge from a file
	 */
	@Override
	public void initialize(Toroidal2DPhysics space) {
		asteroidToShipMap = new HashMap<UUID, Ship>();
		aimingForBase = new HashMap<UUID, Boolean>();
		huntingShip = new HashMap<UUID, Boolean>();
		justHitBase = new HashMap<UUID, Boolean>();
		goingForCore = new HashMap<UUID, Boolean>();
		
	}

	/**
	 * Demonstrates saving out to the xstream file
	 * You can save out other ways too.  This is a human-readable way to examine
	 * the knowledge you have learned.
	 */
	@Override
	public void shutDown(Toroidal2DPhysics space) {
	}

	@Override
	public Set<SpacewarGraphics> getGraphics() {
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
		int numBases, numShips;

		// count the number of ships for the base/ship buying algorithm
		numShips = 0;
		for (AbstractActionableObject actionableObject : actionableObjects) {
			if (actionableObject instanceof Ship) {
				numShips++;
			}
		}
		
		// now see if we can afford a base or a ship.  We want a base but we also really want a 3rd ship
		// try to balance
		if (purchaseCosts.canAfford(PurchaseTypes.BASE, resourcesAvailable)) {
			for (AbstractActionableObject actionableObject : actionableObjects) {
				if (actionableObject instanceof Ship) {
					Ship ship = (Ship) actionableObject;
					Set<Base> bases = space.getBases();

					boolean boughtDrone = false;
					boolean boughtCore = false;

					if (!boughtDrone && ship.getNumCores() > 0 &&
							purchaseCosts.canAfford(PurchaseTypes.DRONE, resourcesAvailable)) { // Or some other criteria for buying a drone, depending on what user wants
						purchases.put(ship.getId(), PurchaseTypes.DRONE); //This spawns a drone within a certain radius of your ship
						boughtDrone = true;
						//System.out.println("Bought a drone!");
					}

					if (!boughtCore && ship.getNumCores() == 0 && 
							purchaseCosts.canAfford(PurchaseTypes.CORE, resourcesAvailable)) { //Or some other criteria for buying a core
						purchases.put(ship.getId(), PurchaseTypes.CORE); //This places a core in your ship’s inventory
						//System.out.println("Bought a core!!");
						boughtCore = true;
					}
					

					// how far away is this ship to a base of my team?
					boolean buyBase = true;
					numBases = 0;
					for (Base base : bases) {
						if (base.getTeamName().equalsIgnoreCase(getTeamName())) {
							numBases++;
							double distance = space.findShortestDistance(ship.getPosition(), base.getPosition());
							if (distance < BASE_BUYING_DISTANCE) {
								buyBase = false;
							}
						}
					}
					if (buyBase && numBases < numShips) {
						purchases.put(ship.getId(), PurchaseTypes.BASE);
						bought_base = true;
						System.out.println("Aggressive Flag Collector is buying a base!");
						break;
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
					System.out.println("Aggressive Flag Collector is buying a ship!");
					break;
				}

			}

		}


		return purchases;
	}

	/**
	 * The hunting ship needs to shoot if it is near its target
	 * @param space
	 * @param actionableObjects
	 * @return
	 */
	@Override
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, SpaceSettlersPowerupEnum> powerUps = new HashMap<UUID, SpaceSettlersPowerupEnum>();

		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;
				
				// only shoot if we are the hunter
				if (huntingShip.containsKey(ship.getId())) {
					// do we have missiles left to shoot?
					if (ship.isValidPowerup(SpaceSettlersPowerupEnum.FIRE_MISSILE)) {
						// only shoot some of the time
						if (random.nextDouble() < shootProb) {
							powerUps.put(ship.getId(), SpaceSettlersPowerupEnum.FIRE_MISSILE);
						}
					}
				}
				
				// launch the drone with the flag
				if (ship.isCarryingFlag()) {
					if (ship.isValidPowerup(SpaceSettlersPowerupEnum.DRONE)) {
						powerUps.put(ship.getId(), SpaceSettlersPowerupEnum.DRONE);
					}
				}
			}
		}
		
		return powerUps;
	}

	@Override
	public Map<UUID, AbstractGameAgent> getGameSearch(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		return null;
	}

}
