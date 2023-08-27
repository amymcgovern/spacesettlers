package spacesettlers.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.AbstractAction;
import spacesettlers.clients.ImmutableTeamInfo;
import spacesettlers.clients.Team;
import spacesettlers.configs.SpaceSettlersConfig;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Drone;
import spacesettlers.objects.Flag;
import spacesettlers.objects.Ship;
import spacesettlers.objects.Star;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.AiCore;
import spacesettlers.objects.powerups.*;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.objects.weapons.AbstractWeapon;
import spacesettlers.utilities.Movement;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * Physics engine for the spacewar simulator. The Toroidal part refers to
 * wrapping around the edges of the simulation.
 * 
 * @author amy
 */
public class Toroidal2DPhysics {
	/**
	 * Height and width of the simulation
	 */
	int height, width;

	float halfHeight, halfWidth;

	/**
	 * All objects in the space
	 */
	Set<AbstractObject> allObjects;

	/**
	 * The list of beacons
	 */
	Set<Beacon> beacons;

	/**
	 * The list of stars
	 */
	Set<Star> stars;

	/**
	 * The list of asteroids
	 */
	Set<Asteroid> asteroids;

	/**
	 * The list of current AI Cores
	 */
	Set<AiCore> cores;

	/**
	 * The list of bases
	 */
	Set<Base> bases;

	/**
	 * The list of ships
	 */
	Set<Ship> ships;

	/**
	 * The list of drones
	 */
	Set<Drone> drones;

	/**
	 * The ets of flags
	 */
	Set<Flag> flags;

	/**
	 * List of all weapons currently in play
	 */
	Set<AbstractWeapon> weapons;

	/**
	 * A hashmap of objects by their ID
	 */
	HashMap<UUID, AbstractObject> objectsById;

	/**
	 * The timestep used for simulation of physics
	 */
	double timeStep;

	/**
	 * The current timestep iteration
	 */
	int currentTimeStep;

	/**
	 * Maximum velocities (to keep things from going nuts)
	 */
	public static final double MAX_TRANSLATIONAL_VELOCITY = 200;
	public static final double MAX_ANGULAR_VELOCITY = Math.PI;

	/**
	 * Energy penalty is used for decreasing ship energy at every step based on intertia
	 * In the past, it has been
	 * public static final double ENERGY_PENALTY = 0.0005;
	 */
	public static final double ENERGY_PENALTY = 0.0005;

	/**
	 * Handles collisions between spacewar objects
	 */
	CollisionHandler collisionHandler;

	/**
	 * Maximum time step
	 */
	int maxTime;

	/**
	 * Information on all of the teams for sharing (set each time step)
	 */
	Set<ImmutableTeamInfo> teamInfo;

	/**
	 * Constructor for the regular game
	 * 
	 * @param simConfig
	 */
	public Toroidal2DPhysics(SpaceSettlersConfig simConfig) {
		height = simConfig.getHeight();
		width = simConfig.getWidth();
		halfHeight = height / 2.0f;
		halfWidth = width / 2.0f;
		allObjects = new LinkedHashSet<AbstractObject>();
		timeStep = simConfig.getSimulationTimeStep();
		collisionHandler = new CollisionHandler();
		beacons = new LinkedHashSet<Beacon>();
		stars = new LinkedHashSet<Star>();
		asteroids = new LinkedHashSet<Asteroid>();
		bases = new LinkedHashSet<Base>();
		ships = new LinkedHashSet<Ship>();
		drones = new LinkedHashSet<Drone>(); // herr0861 edit
		cores = new LinkedHashSet<AiCore>();
		flags = new LinkedHashSet<Flag>();
		weapons = new LinkedHashSet<AbstractWeapon>();
		objectsById = new HashMap<UUID, AbstractObject>();
		maxTime = simConfig.getSimulationSteps();
		teamInfo = new LinkedHashSet<ImmutableTeamInfo>();
	}

	/**
	 * Constructor for unit tests
	 * 
	 * @param height
	 * @param width
	 * @param timeStep
	 */
	public Toroidal2DPhysics(int height, int width, double timeStep) {
		super();
		this.height = height;
		this.width = width;
		this.timeStep = timeStep;
		halfHeight = height / 2.0f;
		halfWidth = width / 2.0f;
		allObjects = new LinkedHashSet<AbstractObject>();
		collisionHandler = new CollisionHandler();
		beacons = new LinkedHashSet<Beacon>();
		stars = new LinkedHashSet<Star>();
		asteroids = new LinkedHashSet<Asteroid>();
		bases = new LinkedHashSet<Base>();
		ships = new LinkedHashSet<Ship>();
		drones = new LinkedHashSet<Drone>(); // herr0861 edit
		flags = new LinkedHashSet<Flag>();
		cores = new LinkedHashSet<AiCore>();
		weapons = new LinkedHashSet<AbstractWeapon>();
		objectsById = new HashMap<UUID, AbstractObject>();
		teamInfo = new LinkedHashSet<ImmutableTeamInfo>();
	}

	/**
	 * Make a shallow copy of the space with just the settings copied over and new
	 * array lists created.
	 *
	 * This is used by the cloning (and should not be called otherwise)
	 * 
	 * @param other
	 */
	private Toroidal2DPhysics(Toroidal2DPhysics other) {
		super();
		this.height = other.height;
		this.width = other.width;
		this.timeStep = other.timeStep;
		this.currentTimeStep = other.currentTimeStep;
		halfHeight = height / 2.0f;
		halfWidth = width / 2.0f;
		allObjects = new LinkedHashSet<AbstractObject>();
		collisionHandler = new CollisionHandler();
		beacons = new LinkedHashSet<Beacon>();
		stars = new LinkedHashSet<Star>();
		asteroids = new LinkedHashSet<Asteroid>();
		bases = new LinkedHashSet<Base>();
		ships = new LinkedHashSet<Ship>();
		drones = new LinkedHashSet<Drone>(); // herr0861 edit
		flags = new LinkedHashSet<Flag>();
		cores = new LinkedHashSet<AiCore>();
		weapons = new LinkedHashSet<AbstractWeapon>();
		objectsById = new HashMap<UUID, AbstractObject>();
		maxTime = other.maxTime;
		teamInfo = new LinkedHashSet<ImmutableTeamInfo>(other.teamInfo);
	}

	/**
	 * Add an object to the physics simulation
	 * 
	 * @param obj
	 */
	public void addObject(AbstractObject obj) {
		allObjects.add(obj);

		if (obj instanceof Beacon) {
			beacons.add((Beacon) obj);
		}

		if (obj instanceof Star) {
			stars.add((Star) obj);
		}

		if (obj instanceof AiCore) {
			cores.add((AiCore) obj);
		}

		if (obj instanceof Asteroid) {
			asteroids.add((Asteroid) obj);
		}

		if (obj instanceof Base) {
			bases.add((Base) obj);
		}

		if (obj instanceof Ship) {
			ships.add((Ship) obj);
		}

		if (obj instanceof AbstractWeapon) {
			weapons.add((AbstractWeapon) obj);
		}

		if (obj instanceof Drone) { // herr0861 edit
			drones.add((Drone) obj);
		}

		if (obj instanceof Flag) {
			flags.add((Flag) obj);
		}

		objectsById.put(obj.getId(), obj);
	}

	/**
	 * Delete an object from the physics simulation
	 * 
	 * @param obj
	 */
	public void removeObject(AbstractObject obj) {
		allObjects.remove(obj);

		if (obj.getClass() == Beacon.class) {
			beacons.remove((Beacon) obj);
		}

		if (obj.getClass() == Star.class) {
			stars.remove((Star) obj);
		}

		if (obj.getClass() == Asteroid.class) {
			asteroids.remove((Asteroid) obj);
		}

		if (obj.getClass() == AiCore.class) {
			cores.remove((AiCore) obj);
		}

		if (obj.getClass() == Base.class) {
			bases.remove((Base) obj);
		}

		if (obj.getClass() == Ship.class) {
			ships.remove((Ship) obj);
		}

		if (obj instanceof AbstractWeapon) {
			weapons.remove((AbstractWeapon) obj);
		}

		if (obj instanceof Drone) {// herr0861 edit
			drones.remove((Drone) obj);
		}

		if (obj instanceof Flag) {
			flags.remove((Flag) obj);
		}

		objectsById.remove(obj.getId());
	}

	/**
	 * return object by its ID
	 * 
	 * @param id
	 * @return
	 */
	public AbstractObject getObjectById(UUID id) {
		return objectsById.get(id);
	}

	/**
	 * Return the list of asteroids
	 * 
	 * @return
	 */
	public Set<Asteroid> getAsteroids() {
		return asteroids;
	}

	/**
	 * Return the list of beacons
	 * 
	 * @return
	 */
	public Set<Beacon> getBeacons() {
		return beacons;
	}

	/**
	 * Return the list of stars
	 * 
	 * @return
	 */
	public Set<Star> getStars() {
		return stars;
	}

	/**
	 * Return the list of bases
	 * 
	 * @return
	 */
	public Set<Base> getBases() {
		return bases;
	}

	/**
	 * Return the list of ships
	 * 
	 * @return
	 */
	public Set<Ship> getShips() {
		return ships;
	}

	/**
	 * Return the list of cores currently in play
	 * 
	 * @return Set of AiCores
	 */
	public Set<AiCore> getCores() {
		return cores;
	}

	/**
	 * Return a list of weapons currently in play
	 * 
	 * @return
	 */
	public Set<AbstractWeapon> getWeapons() {
		return weapons;
	}

	/**
	 * Return a list of drones currently in play
	 * 
	 * @return
	 */
	public Set<Drone> getDrones() { // herr0861 edit
		return drones;
	}

	/**
	 * Return the list of flags currently in play
	 * 
	 * @return
	 */
	public Set<Flag> getFlags() {
		return flags;
	}

	/**
	 * Return the Environment height
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Return the Environment width
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Return the timestep duration
	 * 
	 * @return
	 */
	public double getTimestepDuration() {
		return timeStep;
	}

	/**
	 * Return the timestep
	 * 
	 * @return
	 */
	public int getCurrentTimestep() {
		return currentTimeStep;
	}

	/**
	 * Returns a new random free location in space
	 * 
	 * @param rand   Random number generator
	 * @param radius the radius around the new location that must be free
	 * @return
	 */
	public Position getRandomFreeLocation(Random rand, int radius) {
		Position randLocation = new Position(rand.nextFloat() * width, rand.nextFloat() * height);

		while (!isLocationFree(randLocation, radius)) {
			randLocation = new Position(rand.nextFloat() * width, rand.nextFloat() * height);
		}

		return randLocation;
	}

	/**
	 * Returns a new random free location in space
	 * 
	 * @param rand       Random number generator
	 * @param freeRadius the radius around the object that must be free
	 * @return
	 */
	public Position getRandomFreeLocationInRegion(Random rand, int freeRadius, int centerX, int centerY,
			double maxDistance) {
		Position centerPosition = new Position(centerX, centerY);
		double newX = ((2 * rand.nextDouble()) - 1) * maxDistance + centerX;
		double newY = ((2 * rand.nextDouble()) - 1) * maxDistance + centerY;
		Position randLocation = new Position(newX, newY);
		toroidalWrap(randLocation);

		while (!isLocationFree(randLocation, freeRadius)
				|| findShortestDistance(centerPosition, randLocation) > maxDistance) {
			newX = ((2 * rand.nextDouble()) - 1) * maxDistance + centerX;
			newY = ((2 * rand.nextDouble()) - 1) * maxDistance + centerY;
			randLocation = new Position(newX, newY);
			toroidalWrap(randLocation);
		}

		return randLocation;
	}

	/**
	 * Returns a new random free location inside the specified box of space
	 * 
	 * @param rand Random number generator
	 * @return
	 */
	public Position getRandomFreeLocationInRegion(Random rand, int freeRadius, int ULX, int ULY, int LRX, int LRY) {
		int boxWidth = LRX - ULX;
		int boxHeight = LRY - ULY;

		// System.out.println("Making a random location inside UL (x,y) " + ULX + ", " +
		// ULY +
		// " to LR (x,y) " + LRY + ", " + LRY);

		Position centerPosition = new Position(boxWidth / 2 + ULX, boxHeight / 2 + ULY);
		// System.out.println("Center position is " + centerPosition);
		double newX = ((2 * rand.nextDouble()) - 1) * (boxWidth / 2.0) + centerPosition.getX();
		double newY = ((2 * rand.nextDouble()) - 1) * (boxHeight / 2.0) + centerPosition.getY();
		Position randLocation = new Position(newX, newY);
		toroidalWrap(randLocation);

		while (!isLocationFree(randLocation, freeRadius)) {
			newX = ((2 * rand.nextDouble()) - 1) * (boxWidth / 2.0) + centerPosition.getX();
			newY = ((2 * rand.nextDouble()) - 1) * (boxHeight / 2.0) + centerPosition.getY();
			randLocation = new Position(newX, newY);
			toroidalWrap(randLocation);
		}

		// System.out.println("random location chosen is " + randLocation);
		return randLocation;
	}

	/**
	 * Is the specified location free (within the specified radius)?
	 * 
	 * @param location
	 * @param radius
	 * @return true if the location is free and false otherwise
	 */
	public boolean isLocationFree(Position location, int radius) {
		for (AbstractObject object : allObjects) {
			// fixed bug where it only checked radius and not diameter
			if (findShortestDistanceVector(object.getPosition(), location)
					.getMagnitude() <= (radius + (2 * object.getRadius()))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Public interface to find the shortest toroidal distance from one location to
	 * another. Returns a vector pointing from location1 to location2. Use a
	 * getMagnitude() call on the vector to get the distance.
	 * 
	 * @param location1
	 * @param location2
	 * @return shortest distance vector pointing from location1 to location2
	 */
	public Vector2D findShortestDistanceVector(Position location1, Position location2) {
		return findShortestDistanceVector(location1, location2, width, height, halfWidth, halfHeight);
	}

	/**
	 * Public interface to find the shortest toroidal distance from one location to
	 * another. Returns a double (the distance). Use findShortestDistanceVector to
	 * get the vector telling you which way to move along this path. Useful if you
	 * just care about distance.
	 * 
	 * @param location1
	 * @param location2
	 * @return shortest distance length (magnitude of the vector pointing from
	 *         location1 to location2)
	 */
	public double findShortestDistance(Position location1, Position location2) {
		Vector2D shortDist = findShortestDistanceVector(location1, location2, width, height, halfWidth, halfHeight);
		return shortDist.getMagnitude();
	}

	/**
	 * Finds the shortest distance in toroidal space. Returns a vector pointing from
	 * the start to the target location and getMagnitude can be used to find the
	 * distance and the angle.
	 * 
	 * @param location1
	 * @param location2
	 * @param width
	 * @param height
	 * @param halfWidth
	 * @param halfHeight
	 * @return
	 */
	private Vector2D findShortestDistanceVector(Position location1, Position location2, float width, float height,
			float halfWidth, float halfHeight) {
		double x = location2.getX() - location1.getX();
		double y = location2.getY() - location1.getY();
		if (x > halfWidth) {
			if (y > halfHeight) {
				return new Vector2D(x - width, y - height);
			} else if (y < -halfHeight) {
				return new Vector2D(x - width, y + height);
			} else {
				return new Vector2D(x - width, y);
			}
		} else if (x < -halfWidth) {
			if (y > halfHeight) {
				return new Vector2D(x + width, y - height);
			} else if (y < -halfHeight) {
				return new Vector2D(x + width, y + height);
			} else {
				return new Vector2D(x + width, y);
			}
		} else if (y > halfHeight) {
			return new Vector2D(x, y - height);
		} else if (y < -halfHeight) {
			return new Vector2D(x, y + height);
		} else {
			return new Vector2D(x, y);
		}
	}

	/**
	 * Move all moveable objects and handle power ups.
	 */
	public void advanceTime(Random rand, int currentTimeStep, Map<UUID, SpaceSettlersPowerupEnum> powerups) {

		this.currentTimeStep = currentTimeStep;

		// heal any base injuries
		for (Base base : bases) {
			base.updateEnergy(base.getHealingIncrement());
		}

		// heal any ships that have self-healing available
		for (Ship ship : ships) {
			if (ship.getHealingStepsRemaining() > 0) {
				ship.updateEnergy(ship.HEALING_INCREMENT);
				ship.setHealingStepsRemaining(ship.getHealingStepsRemaining()-1);
			}
		}

		// detect collisions across all objects
		detectCollisions();

		// get the power ups and create any objects (weapons) as necessary
		for (UUID key : powerups.keySet()) {
			AbstractObject swobject = getObjectById(key);
			// if the object is not alive or it is not actionable or is a drone, then ignore
			// this
			if (!swobject.isAlive() || (!(swobject instanceof AbstractActionableObject))
					|| (swobject instanceof Drone)) {// herr0861 edit
				continue;
			}

			// otherwise, handle the power up
			handlePowerup((AbstractActionableObject) swobject, powerups.get(key));
		}

		// now move all objects that are moveable (which may include weapons)
		for (AbstractObject object : allObjects) {
			// skip non-moveable objects or dead object
			if (!object.isMoveable() || !object.isAlive()) {
				continue;
			}

			Position currentPosition = object.getPosition();

			if (object instanceof Drone) { // herr0861 edit
				Drone drone = (Drone) object;
				if (drone.getCurrentAction() == null) {
					drone.setCurrentAction(this.deepClone());
				}

				AbstractAction action = drone.getCurrentAction();

				// AbstractAction action = drone.getDroneAction(this.deepClone());

				if (action == null) {
					action = new DoNothingAction(); // This should never happen, but I'll keep it just in case.
					System.out.println("Drone doing nothing!");

				}

				/*
				 * Don't actually need to clone this since the user can't modify it, but might
				 * as well to be consistent because maybe in the future people will be allowed
				 * to specify behavior for the drone.
				 */

				Movement actionMovement = action.getMovement(this.deepClone(), drone.deepClone());

				Position newPosition = applyMovement(currentPosition, actionMovement, timeStep);

				if (newPosition.isValid()) {
					drone.setPosition(newPosition);
				} else {
					newPosition = currentPosition;
				}

				// spend ship energy proportional to its acceleration (old formula used
				// velocity) and mass (new for space settlers
				// since resources cost mass) based on update to position (used
				// to be based on movement command, no result)
				// double penalty = ENERGY_PENALTY *
				// -Math.abs(ship.getPosition().getTotalTranslationalVelocity());
				double angularAccel = Math.abs(currentPosition.getAngularVelocity() - newPosition.getAngularVelocity())
						/ timeStep;
				double angularInertia = (3.0 * drone.getMass() * drone.getRadius() * angularAccel) / 2.0;
				//double linearAccel = Math.abs(currentPosition.getTranslationalVelocity().getMagnitude()
				//		- newPosition.getTranslationalVelocity().getMagnitude()) / timeStep;
				double linearAccel = currentPosition.getTranslationalVelocity().subtract(newPosition.getTranslationalVelocity()).getMagnitude() / timeStep;
				double linearInertia = drone.getMass() * linearAccel;
				int penalty = (int) Math.floor(0.7 * ENERGY_PENALTY * (angularInertia + linearInertia));
				drone.updateEnergy(-penalty);

			} else if (object.isControllable() && !(object instanceof Drone)) {

				Ship ship = (Ship) object;
				AbstractAction action = ship.getCurrentAction();

				// handle a null action
				if (action == null) {
					action = new DoNothingAction();
				}

				// need to clone the ship and space because otherwise the ship can affect
				// itself inside AbstractAction
				Movement actionMovement = action.getMovement(this.deepClone(), ship.deepClone());

				Vector2D currentVelocity = currentPosition.getTranslationalVelocity();
				double currentAngularVelocity = currentPosition.getAngularVelocity();
				Position newPosition = applyMovement(currentPosition, actionMovement, timeStep);

				// System.out.println("The old position is: [" + currentPosition + "] and the
				// new position is: [" + newPosition);
				if (newPosition.isValid()) {
					ship.setPosition(newPosition);
				} else {
					newPosition = currentPosition;
				}

				// spend ship energy proportional to its acceleration (old formula used
				// velocity) and mass (new for space settlers
				// since resources cost mass) based on update to position (used
				// to be based on movement command, no result)
				// double penalty = ENERGY_PENALTY *
				// -Math.abs(ship.getPosition().getTotalTranslationalVelocity());
				double angularAccel = Math.abs(currentPosition.getAngularVelocity() - newPosition.getAngularVelocity())
						/ timeStep;
				double angularInertia = (3.0 * ship.getMass() * ship.getRadius() * angularAccel) / 2.0;
				double linearAccel = currentPosition.getTranslationalVelocity().subtract(newPosition.getTranslationalVelocity()).getMagnitude() / timeStep;
				double linearInertia = ship.getMass() * linearAccel;
				int penalty = (int) Math.floor(ENERGY_PENALTY * (angularInertia + linearInertia));
				ship.updateEnergy(-penalty);

				// this isn't the most general fix but it will work for now (also has to be done
				// for bases)
				if (ship.isShielded()) {
					ship.updateEnergy(-PowerupToggleShield.SHIELD_STEP_COST);
				}

				// if (!ship.isAlive()) {
				// System.out.println("Ship " + ship.getTeamName() + ship.getId() + " is dead");
				// }

			} else {
				// move all other types of objects
				Position newPosition = moveOneTimestep(currentPosition);
				object.setPosition(newPosition);
				if (object instanceof Flag && Double.isNaN(newPosition.getX())) {
					System.out.println("alive: " + object.isAlive() + " draw: " + object.isDrawable() + " old pos is "
							+ currentPosition + " new position is " + newPosition);
					System.exit(-1);
				}
			}

			// if any ships or bases are frozen, decrement their frozen count
			if (object instanceof AbstractActionableObject && !object.isControllable()) {
				AbstractActionableObject actionable = (AbstractActionableObject) object;
				actionable.decrementFreezeCount();
			}

		}

		// go through and see if any bases have died
		Set<Base> basesClone = new LinkedHashSet<Base>(bases);
		for (Base base : basesClone) {
			// this isn't the most general fix but it will work for now (also has to be done
			// for bases)
			if (base.isShielded()) {
				base.updateEnergy(-PowerupToggleShield.SHIELD_STEP_COST);
			}

			if (!base.isAlive()) {
				// if the base died, increment kill and assist count
				Ship killShip = base.getKillTagTeam();
				Ship assistShip = base.getAssistTagTeam();

				if (killShip != null) killShip.incrementKillsInflicted();
				if (assistShip != null) assistShip.incrementAssistsInflicted();
				base.incrementKillsReceived();
				
				base.setAlive(false);
				removeObject(base);
				base.getTeam().removeBase(base);
			}
		}

		Set<Drone> dronesClone = new LinkedHashSet<Drone>(drones);
		for (Drone drone : dronesClone) {
			if (drone.getEnergy() <= 0 && drone.isAlive() == true) {// drone has died
				// if the drone died, increment kill and assist count
				Ship killShip = drone.getKillTagTeam();
				Ship assistShip = drone.getAssistTagTeam();

				if (killShip != null) killShip.incrementKillsInflicted();
				if (assistShip != null) assistShip.incrementAssistsInflicted();

				
				drone.setDeadAndDropObjects(rand, this); // kill the drone dropping the flag and all resources, but no
															// core. Should we make it have a chance to drop an AiCore?
															// Probably not.
				removeObject(drone);
				drone.getTeam().removeDrone(drone);
			}
		}

		// and see if any ships have died. Doing this here removes unintential side
		// effects
		// from when it was called inside updateEnergy
		for (Ship ship : ships) {
			if (ship.getEnergy() <= 0 && ship.isAlive() == true) {
				// if the drone died, increment kill and assist count
				Ship killShip = ship.getKillTagTeam();
				Ship assistShip = ship.getAssistTagTeam();

				if (killShip != null) killShip.incrementKillsInflicted();
				if (assistShip != null) assistShip.incrementAssistsInflicted();
				
				// mark the ship as having been killed
				ship.incrementKillsReceived();

				
				// Spawn a new AiCore with the same velocity magnitude and direction as its
				// parent ship.
				// handle dropping the core if the ship died
				Position corePosition = this.getRandomFreeLocationInRegion(rand, AiCore.CORE_RADIUS,
						(int) ship.getPosition().getX(), (int) ship.getPosition().getY(), 200);
				corePosition.setTranslationalVelocity(ship.getPosition().getTranslationalVelocity());
				corePosition.setAngularVelocity(ship.getPosition().getAngularVelocity());
				AiCore shipCore = new AiCore(corePosition, ship.getTeamName(), ship.getTeamColor());
				this.addObject(shipCore);

				// drop any resources that the ship was carrying - this was commented out due to
				// creating giant piles of resources
				ResourcePile resources = ship.getResources();

				if (resources.getTotal() > 0) {
					// Position newPosition = ship.getPosition();
					// newPosition.setTranslationalVelocity(new Vector2D(0,0));
					// newPosition.setAngularVelocity(0.0);
					// Asteroid newAsteroid = new Asteroid(newPosition, true, ship.getRadius(),
					// true, resources);
					// this.addObject(newAsteroid);

					// distributeResourcesToNearbyAsteroids(ship.getPosition(), resources);
					// System.out.println("Adding a new asteroid with resources " +
					// newAsteroid.getResources().getTotal() +
					// " due to death, total is " + asteroids.size());
					// System.out.println("Ship died and " + resources.getTotal() + " has been added
					// to an asteroid");
				}

				// set the ship to dead last (so we can grab its resources first)
				// this drops the flag
				ship.setDeadAndDropObjects(rand, this);
			}
		}
		
		// verify all tags are still accurate with the new energies
		for (Base base : bases) {
			base.updateTags();
		}

		for (Ship ship : ships) {
			ship.updateTags();
		}
	}

	/**
	 * Distribute the specified resources to nearby mineable asteroids (this happens
	 * when a ship dies) Right now it drops it on the single nearest asteroid but
	 * that may change if this ends up making massive asteroids
	 * 
	 * @param position
	 * @param resources
	 */
	private void distributeResourcesToNearbyAsteroids(Position position, ResourcePile resources) {
		double nearestDistance = Double.MAX_VALUE;
		Asteroid nearestAsteroid = null;

		// first find the nearest asteroid
		for (Asteroid asteroid : asteroids) {
			double dist = findShortestDistance(position, asteroid.getPosition());
			if (dist < nearestDistance) {
				nearestDistance = dist;
				nearestAsteroid = asteroid;
			}
		}

		// if it is mineable, just add the resources
		nearestAsteroid.addResources(resources);
		if (!nearestAsteroid.isMineable()) {
			// transform it to mineable
			nearestAsteroid.setMineable(true);
		}

	}

	/**
	 * Handle power ups for the specified object
	 * 
	 * @param swobject
	 * @param spacewarPowerup
	 */
	private void handlePowerup(AbstractActionableObject swobject, SpaceSettlersPowerupEnum spacewarPowerup) {
		switch (spacewarPowerup) {
		case FIRE_MISSILE:
			Ship ship = (Ship) swobject;
			AbstractWeapon weapon = ship.getNewWeapon(SpaceSettlersPowerupEnum.FIRE_MISSILE);
			if (weapon != null && weapon.isValidWeapon(ship)) {
				addObject(weapon);
				weapon.setFiringShip(ship);
				weapon.applyPowerup(ship);
			}
			break;

		case FIRE_EMP:
			ship = (Ship) swobject;
			weapon = ship.getNewWeapon(SpaceSettlersPowerupEnum.FIRE_EMP);
			if (weapon != null && weapon.isValidWeapon(ship)) {
				addObject(weapon);
				weapon.setFiringShip(ship);
				weapon.applyPowerup(ship);
			}
			break;

		case TOGGLE_SHIELD:
			PowerupToggleShield toggle = new PowerupToggleShield();
			toggle.applyPowerup(swobject);
			break;

		case DOUBLE_WEAPON_CAPACITY:
			PowerupDoubleWeapon weaponDoubler = new PowerupDoubleWeapon();
			weaponDoubler.applyPowerup(swobject);
			break;

		case DOUBLE_BASE_HEALING_SPEED:
			PowerupDoubleHealingBaseEnergy baseDoubler = new PowerupDoubleHealingBaseEnergy();
			baseDoubler.applyPowerup(swobject);
			break;

		case SET_SHIP_SELF_HEAL:
			PowerupSetShipSelfHeal shipHealer = new PowerupSetShipSelfHeal();
			shipHealer.applyPowerup(swobject);
			break;

		case DOUBLE_MAX_ENERGY:
			PowerupDoubleMaxEnergy maxEnergyDoubler = new PowerupDoubleMaxEnergy();
			maxEnergyDoubler.applyPowerup(swobject);
			break;

		default:
			break;

		}

	}

	/**
	 * Advances one time step using the set velocities
	 * 
	 * @param currentPosition
	 * @return
	 */
	private Position moveOneTimestep(Position position) {
		double angularVelocity = position.getAngularVelocity();
		double orientation = position.getOrientation() + (angularVelocity * timeStep);
		if (Double.isNaN(angularVelocity) || Double.isNaN(position.getTotalTranslationalVelocity())) {
			System.out.println("Help: velocity got set to Nan " + position);
		}

		// make sure orientation wraps correctly (-pi to pi)
		if (orientation > Math.PI) {
			orientation -= (2 * Math.PI);
		} else if (orientation < -Math.PI) {
			orientation += (2 * Math.PI);
		}

		// new x,y coordinates
		double newX = position.getX() + (position.getTranslationalVelocityX() * timeStep);
		double newY = position.getY() + (position.getTranslationalVelocityY() * timeStep);

		Position newPosition = new Position(newX, newY, orientation);
		newPosition.setAngularVelocity(angularVelocity);
		newPosition.setTranslationalVelocity(position.getTranslationalVelocity());
		toroidalWrap(newPosition);
		return newPosition;
	}

	/**
	 * Step through all the objects and ensure they are not colliding. If they are,
	 * call the collision handler for those objects. Sometimes you bounce
	 * (asteroids) and sometimes you pick the object up (beacons), etc.
	 */
	private void detectCollisions() {
		// would prefer to iterate over the set (as this is inefficient) but
		// the set iterator collides a with b and then b with a, allowing them to
		// pass through one another!
		AbstractObject[] allObjectsArray = (AbstractObject[]) allObjects.toArray(new AbstractObject[allObjects.size()]);

		// loop through all pairs of objects and see if they are colliding
		for (int i = 0; i < allObjectsArray.length; i++) {
			AbstractObject object1 = allObjectsArray[i];
			if (!object1.isAlive()) {
				continue;
			}

			for (int j = i + 1; j < allObjectsArray.length; j++) {
				AbstractObject object2 = allObjectsArray[j];

				if (!object2.isAlive()) {
					continue;
				}

				// skip them if they are the same object
				if (object1.equals(object2)) {
					continue;
				}

				double distance = findShortestDistance(object1.getPosition(), object2.getPosition());

				if (distance < (object1.getRadius() + object2.getRadius())) {
					collisionHandler.collide(object1, object2, this);
				}
			}
		}
	}

	/**
	 * Takes an acceleration and a simulation time step and moves the object
	 * 
	 * @param actionMovement
	 * @param timeStep
	 * @return
	 */
	public Position applyMovement(Position position, Movement movement, double timeStep) {
		Vector2D translationalAcceleration = movement.getTranslationalAcceleration();
		double angularAccel = movement.getAngularAccleration();

		// velocity is acceleration times time
		Vector2D translationalVelocity = position.getTranslationalVelocity().add(new Vector2D(
				translationalAcceleration.getXValue() * timeStep, translationalAcceleration.getYValue() * timeStep));
		double angularVelocity = position.getAngularVelocity() + (angularAccel * timeStep);

		// ensure the max/mins are respected
		if (translationalVelocity.getMagnitude() > MAX_TRANSLATIONAL_VELOCITY) {
			double ratio = translationalVelocity.getMagnitude() / MAX_TRANSLATIONAL_VELOCITY;
			translationalVelocity = translationalVelocity.multiply(1 / ratio);
		}

		angularVelocity = checkAngularVelocity(angularVelocity);

		Position newPosition = new Position(position.getX(), position.getY(), position.getOrientation());
		newPosition.setTranslationalVelocity(translationalVelocity);
		newPosition.setAngularVelocity(angularVelocity);

		return moveOneTimestep(newPosition);
	}

	/**
	 * Ensure the angular velocity doesn't exceed the max
	 * 
	 * @param angularVelocity
	 * @return
	 */
	private double checkAngularVelocity(double angularVelocity) {
		if (angularVelocity > MAX_ANGULAR_VELOCITY) {
			return MAX_ANGULAR_VELOCITY;
		} else if (angularVelocity < -MAX_ANGULAR_VELOCITY) {
			return -MAX_ANGULAR_VELOCITY;
		} else {
			return angularVelocity;
		}
	}

	/**
	 * Torridial wrap based on the height/width of the enviroment
	 * 
	 * @param position
	 */
	public void toroidalWrap(Position position) {
		while (position.getX() < 0) {
			position.setX(position.getX() + width);
		}

		while (position.getY() < 0) {
			position.setY(position.getY() + height);
		}

		position.setX(position.getX() % width);
		position.setY(position.getY() % height);
	}

	/**
	 * Respawns any dead objects in new random locations. Ships have a delay before
	 * they can respawn.  Asteroids do not respawn (they are re-created later randomly)
	 */
	public void respawnDeadObjects(Random random) {
		for (AbstractObject object : allObjects) {
			if (!object.isAlive() && object.canRespawn()) {
				Position newPosition = null;

				// flags should re-spawn at a randomly chosen starting location
				if (object instanceof Flag) {
					Flag flag = (Flag) object;
					newPosition = flag.getNewStartingPosition(random);

					// ensure their starting location is free (to handle the thought bug the class
					// introduced of putting a ship or a base where the flag should spawn)
					newPosition = getRandomFreeLocationInRegion(random, flag.getRadius() * 2, (int) newPosition.getX(),
							(int) newPosition.getY(), flag.getRadius() * 15);

				} else {
					// note this is times 4 in order to ensure objects don't spawn touching (and just
					// to get them a bit farther apart
					newPosition = getRandomFreeLocation(random, object.getRadius() * 4);
				}

				object.setPosition(newPosition);
				object.setAlive(true);
				object.setDrawable(true);

				// reset the UUID if it is a beacon or a star
				if (object instanceof Beacon || object instanceof Star) {
					object.resetId();
				}
			}
		}

	}

	/**
	 * Clones all the objects in space (used for security so the teams can't
	 * manipulate other ships) +
	 * 
	 * @return
	 */
	public Toroidal2DPhysics deepClone() {
		Toroidal2DPhysics newSpace = new Toroidal2DPhysics(this);

		for (AbstractObject swObject : allObjects) {
			AbstractObject newObject = swObject.deepClone();

			newSpace.addObject(newObject);
		}

		return newSpace;
	}

	/**
	 * Loop through all weapons and remove any dead ones
	 */
	public void cleanupDeadWeapons() {
		ArrayList<AbstractObject> deadObjects = new ArrayList<AbstractObject>();
		for (AbstractObject object : allObjects) {
			if (object instanceof AbstractWeapon && !object.isAlive()) {
				deadObjects.add(object);
			}
		}

		for (AbstractObject deadObject : deadObjects) {
			removeObject(deadObject);
		}

	}

	/**
	 * Loop through all cores and remove any dead ones
	 */
	public void cleanupDeadCores() {
		ArrayList<AbstractObject> deadObjects = new ArrayList<AbstractObject>();

		for (AiCore core : cores) {
			if (!core.isAlive()) {
				deadObjects.add(core);
			}
		}

		for (AbstractObject deadObject : deadObjects) {
			removeObject(deadObject);
		}

	}

	/**
	 * Loop through all drones and remove any dead ones. herr0861 edit
	 */
	public void cleanupDeadDrones() {
		ArrayList<AbstractObject> deadObjects = new ArrayList<AbstractObject>();

		for (Drone drone : drones) {
			if (!drone.isAlive()) {
				deadObjects.add(drone);
			}
		}

		for (AbstractObject deadObject : deadObjects) {
			removeObject(deadObject);
		}
	}

	/**
	 * Loop through all asteroids and remove any dead ones
	 */
	public int cleanupAllAndCountMineableDeadAsteroids() {
		ArrayList<AbstractObject> deadObjects = new ArrayList<AbstractObject>();
		int numMineable = 0;

		for (Asteroid asteroid : asteroids) {
			if (!asteroid.isAlive()) {
				deadObjects.add(asteroid);
				if (asteroid.isMineable()) {
					numMineable++;
				}
			}
		}

		for (AbstractObject deadObject : deadObjects) {
			removeObject(deadObject);
		}
		
		// return the number of mineable ones that were removed
		return numMineable;
	}

	/**
	 * Return the maximum number of time steps for the simulation
	 * 
	 * @return
	 */

	public int getMaxTime() {
		return maxTime;
	}

	/**
	 * Return all objects
	 * 
	 * @return
	 */
	public Set<AbstractObject> getAllObjects() {
		return allObjects;
	}

	/**
	 * Check to see if following a straight line path between two given locations
	 * would result in a collision with a provided set of obstructions
	 * 
	 * @author Andrew and Thibault
	 * 
	 * @param startPosition the starting location of the straight line path
	 * @param goalPosition  the ending location of the straight line path
	 * @param obstructions  an Set of AbstractObject obstructions (i.e., if you
	 *                      don't wish to consider mineable asteroids or beacons
	 *                      obstructions)
	 * @param freeRadius    used to determine free space buffer size
	 * @return Whether or not a straight line path between two positions contains
	 *         obstructions from a given set
	 */
	public boolean isPathClearOfObstructions(Position startPosition, Position goalPosition,
			Set<AbstractObject> obstructions, int freeRadius) {
		Vector2D pathToGoal = findShortestDistanceVector(startPosition, goalPosition); // Shortest straight line path
																						// from startPosition to
																						// goalPosition
		double distanceToGoal = pathToGoal.getMagnitude(); // Distance of straight line path

		boolean pathIsClear = true; // Boolean showing whether or not the path is clear

		// Calculate distance between obstruction center and path (including buffer for
		// ship movement)
		// Uses hypotenuse * sin(theta) = opposite (on a right hand triangle)
		Vector2D pathToObstruction; // Vector from start position to obstruction
		double angleBetween; // Angle between vector from start position to obstruction

		// Loop through obstructions
		for (AbstractObject obstruction : obstructions) {
			// If the distance to the obstruction is greater than the distance to the end
			// goal, ignore the obstruction
			// bug fix for radius of obstruction provided by Yo 
			pathToObstruction = findShortestDistanceVector(startPosition, obstruction.getPosition());
			if (pathToObstruction.getMagnitude() > distanceToGoal + obstruction.getRadius()) {
				continue;
			}

			// Ignore angles > 90 degrees
			angleBetween = Math.abs(pathToObstruction.angleBetween(pathToGoal));
			if (angleBetween > Math.PI / 2) {
				continue;
			}

			// Compare distance between obstruction and path with buffer distance
			if (pathToObstruction.getMagnitude() * Math.sin(angleBetween) < obstruction.getRadius()
					+ freeRadius * 1.5) {
				pathIsClear = false;
				break;
			}
		}

		return pathIsClear;

	}

	/**
	 * Set the team information for this time step
	 * 
	 * @param teamInfo
	 */
	public void setTeamInfo(Set<ImmutableTeamInfo> teamInfo) {
		this.teamInfo = teamInfo;
	}

	/**
	 * Get the team's information
	 * 
	 * @return
	 */
	public Set<ImmutableTeamInfo> getTeamInfo() {
		return teamInfo;
	}

}