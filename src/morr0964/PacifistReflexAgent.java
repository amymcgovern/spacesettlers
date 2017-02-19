package morr0964;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.MoveAction;
import spacesettlers.actions.MoveToObjectAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.clients.TeamClient;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * Collects asteroids and brings them to the base, picks up beacons as needed for energy.
 * Dynamically chooses thresholds for collecting energy and base return based on proximity.
 * Has basic collision avoidance (not 100% guarantee)
 * Built for use with only 1 ship
 * 
 * @author Brad (based on amy's code)
 */
public class PacifistReflexAgent extends TeamClient {
	//used to gain access knowledge of space
	KnowledgeRep knowledge;
	
	/**
	 * Sets up the AI by initializing knowledge representation
	 */
	@Override
	public void initialize(Toroidal2DPhysics space) {
		knowledge=new KnowledgeRep();
	}
	
	/**
	 * Sets the ship to go hunt an asteroid, collect a beacon, or return to base based on knowledge
	 * representation's input
	 */
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		//set up the hashmap of actions for each actionable item
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();

		// loop through each actionale item and get their corresponding action
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				//if its a ship, make it do a ship action
				Ship ship = (Ship) actionable;

				AbstractAction action;
				action = getAsteroidCollectorAction(space,ship);
				actions.put(ship.getId(), action);	
			} else {
				//it is a base, which does nothing
				actions.put(actionable.getId(), new DoNothingAction());
			}
		} 
		return actions;
	}
	
	/**
	 * Gets the action for the asteroid collecting ship
	 * 
	 * @param ship the ship who gets the action
	 * @return the action the ship should take
	 */
	private AbstractAction getAsteroidCollectorAction(Toroidal2DPhysics space, Ship ship) {
		//current current action/position
		AbstractAction current = ship.getCurrentAction();
		Position currentPosition = ship.getPosition();
		
		//get location of base and nearest beacon to help determine later actions
		Beacon beacon = knowledge.pickNearestBeacon(space,ship);
		Base base = knowledge.pickNearestBase(space,ship);
		double beaconDist=10000;//in case no beacons, set value to arbitrarily large
		if(beacon!=null){
			beaconDist=knowledge.findDistance(space,ship,beacon);
		}
		double baseDist=knowledge.findDistance(space,ship,base);
		
		// aim for a beacon if there isn't enough energy, or if there is a beacon pretty close and energy is low
		if (ship.getEnergy() < (2000-2*beaconDist)) {
			return new FastMoveToObjectAction(space, currentPosition, beacon);
		}

		// if the ship has enough resources available, take it back to base if it isn't doing anything right now
		if (ship.getResources().getTotal() > (5.0*baseDist+1000)) {
			AbstractAction newAction = new MoveToObjectAction(space, currentPosition, base);
			return newAction;
		}
		// if the ship reached the base, end the action
		if (ship.getResources().getTotal() == 0 && ship.getEnergy() > 2000) {
			current = null;
		}

		// otherwise aim for the best asteroid
		Asteroid asteroid = knowledge.pickHighestValueAsteroid(space);
		AbstractAction newAction = null;
		
		if (asteroid != null) {
			//knowledge.isPathClear(space, ship, asteroid.getPosition());
			newAction = new FastMoveToObjectAction(space, currentPosition, asteroid);
			//check if there is a asteroid in front of us
			boolean pathclear = knowledge.isPathClear(space, ship, asteroid.getPosition());
			if(pathclear){
				//if clear, go for it
				newAction = new FastMoveToObjectAction(space, currentPosition, asteroid);
			}
			else{
				//otherwise turn left a bit
				Vector2D pathDir=knowledge.findDistanceVector(space,ship,asteroid).unit().multiply(50);
				pathDir=pathDir.rotate(.25);
				Position evasivePos=new Position(currentPosition.getX()+pathDir.getXValue(),currentPosition.getY()+pathDir.getYValue());
				//handle toroidal stuff
				while (evasivePos.getX() < 0) {
					evasivePos.setX(evasivePos.getX() + space.getWidth());
				}
				while (evasivePos.getY() < 0) {
					evasivePos.setY(evasivePos.getY() + space.getHeight());
				}

				evasivePos.setX(evasivePos.getX() % space.getWidth());
				evasivePos.setY(evasivePos.getY() % space.getHeight());
				newAction=new MoveAction(space,currentPosition,evasivePos,pathDir);
				System.out.println("evasive");
			}
		}
		
		return newAction;
		
	}


	



	@Override
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {

	}


	/**
	 * Demonstrates saving out to the xstream file
	 * You can save out other ways too.  This is a human-readable way to examine
	 * the knowledge you have learned.
	 */
	@Override
	public void shutDown(Toroidal2DPhysics space) {
		return;
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
		double BASE_BUYING_DISTANCE = 400;
		boolean bought_base = false;

		if (purchaseCosts.canAfford(PurchaseTypes.BASE, resourcesAvailable)) {
			for (AbstractActionableObject actionableObject : actionableObjects) {
				if (actionableObject instanceof Ship) {
					Ship ship = (Ship) actionableObject;
					Set<Base> bases = space.getBases();

					// how far away is this ship to a base of my team?
					boolean buyBase = true;
					for (Base base : bases) {
						if (base.getTeamName().equalsIgnoreCase(getTeamName())) {
							double distance = space.findShortestDistance(ship.getPosition(), base.getPosition());
							if (distance < BASE_BUYING_DISTANCE) {
								buyBase = false;
							}
						}
					}
					if (buyBase) {
						purchases.put(ship.getId(), PurchaseTypes.BASE);
						bought_base = true;
						//System.out.println("Buying a base!!");
						break;
					}
				}
			}		
		} 


		return purchases;
	}

	/**
	 * The pacifist asteroid collector doesn't use power ups 
	 * @param space
	 * @param actionableObjects
	 * @return
	 */
	@Override
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, SpaceSettlersPowerupEnum> powerUps = new HashMap<UUID, SpaceSettlersPowerupEnum>();

		
		return powerUps;
	}

}
