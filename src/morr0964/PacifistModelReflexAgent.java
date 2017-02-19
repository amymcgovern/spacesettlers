package morr0964;

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

/**
 * Collects asteroids and brings them to the base, picks up beacons as needed for energy.
 * Dynamically chooses thresholds for collecting energy and base return based on proximity.
 * Also keeps track of current goal, so the AI does not get distracted. 
 * 
 * Built for use with only 1 ship
 * 
 * @author Brad (based on amy's code)
 */
public class PacifistModelReflexAgent extends TeamClient {
	//used to gain access knowledge of space
	Model knowledge;
	
	/**
	 * Sets up the AI by initializing knowledge representation
	 */
	@Override
	public void initialize(Toroidal2DPhysics space) {
		knowledge=new Model();
	}
	
	/**
	 * Sets the ship to go hunt an asteroid, collect a beacon, or return to base based on knowledge
	 * representation's input
	 */
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		//set up the hashmap of actions for each actionable item
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();
		
		// loop through each actionable item and get their corresponding action
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
		
		//there is a goal, go for it
		AbstractObject goal=knowledge.getGoal(space);
		if(goal!=null){
			//if its a base, check if we've reached it, and gently approach if not
			if(goal instanceof Base){
				// if the ship reached the base, end the goal
				if (ship.getResources().getTotal() == 0 && ship.getEnergy() > 2000) {
					knowledge.reset();
				}
				else{
					return new MoveToObjectAction(space, currentPosition, goal);
				}
			}
			//otherwise, check if it still exists, and  quickly move to it if it does (reset otherwise)
			//also don't continue if critically low on energy
			else if(ship.getEnergy()>1000||(goal instanceof Beacon)){
				return new FastMoveToObjectAction(space,currentPosition,goal);
			}
		}
		//if the goal is empty, or over, find a new goal
		//get location of base and nearest beacon to help determine later actions
		Beacon beacon = knowledge.pickNearestBeacon(space,ship);
		Base base = knowledge.pickNearestBase(space,ship);
		double beaconDist=10000;//in case no beacons, set value to arbitrarily large
		if(beacon!=null){
			beaconDist=knowledge.findDistance(space,ship,beacon);
		}
		double baseDist=knowledge.findDistance(space,ship,base);
		
		// aim for a beacon if there isn't enough energy, or if there is a beacon pretty close and energy is low
		if (ship.getEnergy() < (2000-beaconDist)||ship.getEnergy() < 1000) {
			knowledge.setGoal(beacon);
		}

		// if the ship has enough resources available, take it back to base if it isn't doing anything right now
		if (ship.getResources().getTotal() > (5.0*baseDist+1000)) {
			knowledge.setGoal(base);
		}

		// otherwise aim for the best asteroid
		if(goal==null){
			Asteroid asteroid = knowledge.pickHighestValueAsteroid(space);
			knowledge.setGoal(asteroid);
		}
		
		//if reached here, recall method with new goal
		return getAsteroidCollectorAction(space,ship);
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
