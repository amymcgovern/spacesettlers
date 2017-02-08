package morr0964;

import java.util.HashSet;
import java.util.Set;

import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * This class includes methods that extract useful information for navigating the space,
 * such as finding the closest bases, beacons, and best asteroid targets.
 * It was built to be used by the PacifistReflexAgent
 * 
 * @author Brad (based partially on amy's code)
 */
public class KnowledgeRep {	
	/**
	 * establishes the knowledge rep
	 */
	public KnowledgeRep(){
	}
	
	/**
	 * Find the base for this team nearest to this ship
	 * 
	 * @param ship the ship you want information for
	 * @return the closest base (null if no bases)
	 */
	public Base pickNearestBase(Toroidal2DPhysics space, Ship ship) {
		double minDistance = Double.MAX_VALUE;
		Base nearestBase = null;
		
		//look through all bases and find the one with the min distance
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
	 * 
	 * @param ship the ship you want information on
	 * @return the closest beacon (null if no beacons)
	 */
	public Beacon pickNearestBeacon(Toroidal2DPhysics space, Ship ship) {
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

	/**
	 * Returns the highest value asteroid in the space
	 * 
	 * @return the highest value asteroid in the space
	 */
	public Asteroid pickHighestValueAsteroid(Toroidal2DPhysics space) {
		Set<Asteroid> asteroids = space.getAsteroids();
		int bestMoney = Integer.MIN_VALUE;
		Asteroid bestAsteroid = null;

		//loop through asteroids and find most valuable one
		for (Asteroid asteroid : asteroids) {
			if (asteroid.isMineable() && asteroid.getResources().getTotal() > bestMoney) {
				bestMoney = asteroid.getResources().getTotal();
				bestAsteroid = asteroid;
			}
		}
		return bestAsteroid;
	}
	
	/**
	 * Returns the asteroid closest to the ship
	 * 
	 * @param ship the ship you want information on
	 * @return the closest asteroid to ship
	 */
	public Asteroid pickClosestAsteroid(Toroidal2DPhysics space, Ship ship) {
		Set<Asteroid> asteroids = space.getAsteroids();
		double mindistance = Double.POSITIVE_INFINITY;
		Asteroid bestAsteroid = null;

		//loop through asteroids and find the closest one
		for (Asteroid asteroid : asteroids) {
			double asteroidDist=space.findShortestDistance(ship.getPosition(), asteroid.getPosition());
			if (asteroid.isMineable() && asteroidDist < mindistance) {
				mindistance = asteroidDist;
				bestAsteroid = asteroid;
			}
		}
		return bestAsteroid;
	}
	
	/**
	 * Returns the distance to the object from the ship
	 * @param space the space that is being operated in
	 * @param ship the ship 
	 * @param obj the object
	 * @return the distance
	 */
	public double findDistance(Toroidal2DPhysics space, Ship ship, AbstractObject obj){
		if(obj!=null){
			return space.findShortestDistance(ship.getPosition(), obj.getPosition());
		}
		return 0;
	}
	
	/**
	 * Returns the distance vecotr to the object from the ship
	 * @param space the space that is being operated in
	 * @param ship the ship 
	 * @param obj the object
	 * @return the distance vecotr (null if invalid)
	 */
	public Vector2D findDistanceVector(Toroidal2DPhysics space, Ship ship, AbstractObject obj){
		if(obj!=null){
			return space.findShortestDistanceVector(ship.getPosition(), obj.getPosition());
		}
		return null;
	}
	
	/**
	 * Returns true if the (straight line) path is clear, false otherwise
	 * @param space the space
	 * @param ship the ship that is moving
	 * @param objectGoal the goal location
	 * @return is the path clear?
	 */
	public boolean isPathClear(Toroidal2DPhysics space, Ship ship, Position objectGoal){
		//gather obstructions
		Position currentPosition = ship.getPosition();
		Set<AbstractObject> allObjects = space.getAllObjects();
		Set<AbstractObject> obstacles = new HashSet<AbstractObject>();
		for (AbstractObject obj: allObjects){
			if(obj instanceof Asteroid && !(((Asteroid)obj).isMineable()))
				obstacles.add(obj);
			if(obj instanceof Base)
				obstacles.add(obj);
		}
		//check if the path is clear of them
		boolean pathclear = space.isPathClearOfObstructions(currentPosition, objectGoal, obstacles, 50);
		return pathclear;
	}
	
}
