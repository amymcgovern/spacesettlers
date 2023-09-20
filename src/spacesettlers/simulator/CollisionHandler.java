package spacesettlers.simulator;

import java.util.concurrent.ThreadLocalRandom;

import spacesettlers.game.*;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.AiCore;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Drone;
import spacesettlers.objects.Flag;
import spacesettlers.objects.Ship;
import spacesettlers.objects.Star;
import spacesettlers.objects.weapons.EMP;
import spacesettlers.objects.weapons.Missile;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * Handles collisions between all types of objects.  Implements the rules of spacewar for 
 * bouncing off objects versus picking them up, etc.  Implemented in a central place
 * to make it easy to change as need be.
 * 
 * Note: people have observed ships "sticking" to asteroids or bases when they hit them at low
 * velocity. This isn't a bug in the physics simulator.  If you have a lower mass object hitting 
 * an object of much higher mass at a low velocity, you would expect the low mass one to essentially
 * stick to the higher mass one.  The solution is to move quickly away from the objects after you collide.
 * 
 * 
 * @author amy
 */
public class CollisionHandler {
    public static final double COLLISION_PENALTY = 2.0;

	class CollisionData {
		double v1, v2;
	}
	
	/**
	 * Collide the objects and then take care of side effects based on object type
	 * 
	 * @param object1
	 * @param object2
	 * @param space
	 */
	public void collide(AbstractObject object1, AbstractObject object2, Toroidal2DPhysics space) {
		// if either object is a beacon, handle that (and don't elastically collide)
		if (object1 instanceof Beacon) {
			beaconCollision((Beacon) object1, object2);
			return;
		} else if (object2 instanceof Beacon) {
			beaconCollision((Beacon) object2, object1);
			return;
		}

		// if either object is a star, handle that (and don't elastically collide)
		if (object1 instanceof Star) {
			starCollision((Star) object1, object2);
			return;
		} else if (object2 instanceof Star) {
			starCollision((Star) object2, object1);
			return;
		}

		// if either object is a missile, handle that (and don't elastically collide)
		if (object1 instanceof Missile) {
			missileCollision((Missile) object1, object2);
			return;
		} else if (object2 instanceof Missile) {
			missileCollision((Missile) object2, object1);
			return;
		}
		
		// if either object is a EMP, handle that (and don't elastically collide)
		if (object1 instanceof EMP) {
			EMPCollision((EMP) object1, object2);
			return;
		} else if (object2 instanceof EMP) {
			EMPCollision((EMP) object2, object1);
			return;
		}
		
		// if the object is a flag, handle either moving it (same team) or picking it up (opposite team)
		// and don't elastically collide
		if (object1 instanceof Flag && object2 instanceof Ship) {
			flagCollision((Flag) object1, (Ship) object2);
			return;
		} else if (object2 instanceof Flag && object1 instanceof Ship) {
			flagCollision((Flag) object2, (Ship) object1);
			return;
		}
		
		// handle mineable asteroid collisions (e.g. mine them if needed and don't elastically collide, e.g
		// no damage for mining)
		if (object1 instanceof Asteroid && object2 instanceof Ship) {
			if (((Asteroid) object1).isMineable()) {
				mineAsteroid((Asteroid) object1, (Ship) object2);
				return;
			} else {
				countCollision((Ship) object2);
			}
		} else if (object2 instanceof Asteroid && object1 instanceof Ship) {
			if (((Asteroid) object2).isMineable()) {
				mineAsteroid((Asteroid) object2, (Ship) object1);
				return;
			} else {
				countCollision((Ship) object1);
			}
		}
		
		//Handle AiCore Collisions with ships (Destroy them if same team, collect them if different team) and don't elastically collide
		if (object1 instanceof AiCore && object2 instanceof Ship) {
			collectCore((AiCore)object1, (Ship)object2);
			return;
		} else if (object2 instanceof AiCore && object1 instanceof Ship) {
			collectCore((AiCore)object2, (Ship)object1);
			return;
		}
		
		//Handle AiCore collisions with Beacons (Restoring the energy of the AiCore) and don't elastically collide
		if (object1 instanceof AiCore && object2 instanceof Beacon) {
			healAiCore((AiCore)object1, (Beacon)object2);
			return;
		} else if (object2 instanceof AiCore && object1 instanceof Beacon) {
			healAiCore((AiCore)object2, (Beacon)object1);
			return;
		}
		
		//Handle AiCore collisions with bases (core is collected) and then collides if it is an enemy
		if (object1 instanceof AiCore && object2 instanceof Base) {
			baseCoreCollide((AiCore)object1, (Base)object2);
		} else if (object2 instanceof AiCore && object1 instanceof Base) {
			baseCoreCollide((AiCore)object2, (Base)object1);
		}
		
		//Handle AiCore collisions with Asteroids (Damaging the energy of the AiCore)
		if (object1 instanceof AiCore && object2 instanceof Asteroid) {
			damageAiCore((AiCore)object1);
		} else if (object2 instanceof AiCore && object1 instanceof Asteroid) {
			damageAiCore((AiCore)object2);
			//no return because we still want to collide
		}

		if (object1 instanceof Ship && object2 instanceof  Ship) {
			countCollision((Ship) object1);
			countCollision((Ship) object2);
		}
		
		//Handle Drones colliding with ships - herr0861 edit
		if (object1 instanceof Drone && object2 instanceof Ship) {
			droneCollision((Drone)object1, (Ship)object2);
//			Drone drone = (Drone) object1;
//			Ship ship = (Ship) object2;
//			if (drone.getTeamName().equalsIgnoreCase(ship.getTeamName())) {
//				return;
//			}
		} else if (object2 instanceof Drone && object1 instanceof Ship) {
			droneCollision((Drone)object2, (Ship)object1);
			//no return because we still want to collide
		}
		
		//Handle AiCore collisions with AiCores (Damaging the energy of the AiCore)
		if (object1 instanceof AiCore && object2 instanceof AiCore) {
			damageAiCore((AiCore)object1);
			damageAiCore((AiCore)object2);
			//no return because we still want to collide
		}
		

		// only elastically collide if it isn't a beacon, missile, or other weapon
		if (!object1.isMoveable()) {
			elasticCollision2DWithNonMoveableObject(object2, object1, space);
		} else if (!object2.isMoveable()) {
			elasticCollision2DWithNonMoveableObject(object1, object2, space);
		} else {
			elasticCollision2D(object1, object2, space);
		}

		// if it is a ship, give it an energy penalty for running into the object
		if (object1.getClass() == Ship.class) {
			shipCollision((Ship) object1);
		} 

		// handle ships running into ships
		if (object2.getClass() == Ship.class) {
			shipCollision((Ship) object2);
		}
		
		//If it is a drone, damage it for running into an object - herr0861 edit
		if (object1.getClass() == Drone.class) {
			droneCollision((Drone)object1);
		}
		
		//Drone on drone violence is no laughing matter.
		if (object2.getClass() == Drone.class) {
			droneCollision((Drone)object2);
		}

		
		// handle base collisions
		if (object1 instanceof Base) {
			baseCollision((Base) object1, object2);
		} else if (object2 instanceof Base) {
			baseCollision((Base) object2, object1);
		}
		
	}
	
	/**
	 * Collide with a Flag.  The rules are as follows:
	 * 1) If a ship touches a flag from their own team, it disappears and reappears at the next time 
	 * step in a random alcove
	 * 2) If a ship touches a flag from a team other than their own, that ship picks up the flag.
	 * 
	 * Anything else that touches a flag is ignored or just bounces off
	 *  
	 * @param flag
	 * @param ship
	 */
	private void flagCollision(Flag flag, Ship ship) {
		if (flag.getTeamName().equalsIgnoreCase(ship.getTeamName())) {
			flag.setAlive(false);
			flag.setRespawn(true);
		} else {
			ship.addFlag(flag);
			flag.pickupFlag(ship);
		}
		
	}

	private void countCollision(Ship ship) {
		ship.incrementCollisions();
		return;
	}
	
	/**
	 * Collide a ship and a drone.
	 * If it is a friendly ship, the friendly ship will beam over their resources, cores, and held flags.
	 * @param drone
	 * @param ship
	 */
	private void droneCollision(Drone drone, Ship ship) { //herr0861 edit
		if (ship.getTeamName().equalsIgnoreCase(drone.getTeamName())) {
			//This is a friendly ship, so we should offload resources and cores.
			
			//add cores and resources to the drone and remove from the ship
			drone.incrementCores(ship.getNumCores()); 
			ship.resetAiCores();
			drone.addResources(ship.getResources());
			ship.resetResources();
			
			//Transfer the flag if the ship has it
			if (ship.isCarryingFlag()) {
				drone.addFlag(ship.getFlag());
				ship.depositFlag(); //make the ship drop the flag
				drone.getFlag().pickupFlag(drone); //the pickup method in the flag will automatically cause it to not be carried by the ship when the drone has it
			}
		
		}
	}

	/**
	 * Collide with a missile
	 * @param missile
	 * @param object2
	 */
	private void missileCollision(Missile missile, AbstractObject object2) {
		// get the ship that fired this
		Ship firingShip = missile.getFiringShip();
		firingShip.decrementWeaponCount();

		// did it hit a ship?
		if (object2.getClass() == Ship.class) {
			Ship ship = (Ship) object2;
			
			// only take damageInflicted if not shielded
			if (!ship.isShielded()) {
				double initialEnergy = ship.getEnergy();
				ship.updateEnergy(missile.getDamage());				
				
				// kill/assist tags for missiles to ships
				ship.tagShooter(missile.getFiringShip());
				
				if (ship.getEnergy() <= 0) {
					// if you killed the ship, only count the final amount of damage needed to kill it 
					firingShip.incrementDamageInflicted((int) initialEnergy);
					ship.incrementDamageReceived(-(int) initialEnergy);
				} else {
					// otherwise a missile is a fixed amount of damage
					firingShip.incrementDamageInflicted(-missile.getDamage());
					ship.incrementDamageReceived(missile.getDamage());
				}

				// it hit a ship
				firingShip.incrementHitsInflicted();
			}
			
			// if the bullet killed the ship, credit the ship that hit it
			if (ship.getEnergy() <= 0) {
				//System.out.println("ship " + firingShip.getTeamName() + " stealing resourcesAvailable " + shipMoney + " from " + ship.getTeamName() + ship.getId());
				
				// it killed a ship
				//firingShip.incrementKillsInflicted();
				// removed increment of kills here to handle in simulator with assists now - Amy 03/10/2019
				//ship.incrementKillsReceived();
			}

		}
		
		if (object2.getClass() == Drone.class) {//herr0861
			Drone drone = (Drone) object2;
			
			double initialEnergy = drone.getEnergy();
			drone.updateEnergy(missile.getDamage());	
			
			// kill/assist tags for missiles 
			drone.tagShooter(missile.getFiringShip());
			
			if (drone.getEnergy() <= 0) {
				// if you killed the drone, only count the final amount of damage needed to kill it 
				firingShip.incrementDamageInflicted((int) initialEnergy);
			} else {
				// otherwise a missile is a fixed amount of damage
				firingShip.incrementDamageInflicted(-missile.getDamage());
			}

			// count hits
			firingShip.incrementHitsInflicted();

			// count kills if the drone is dead
			if (drone.getEnergy() <= 0) {
				// removed increment of kills here to handle in simulator with assists now - Amy 03/10/2019
				//firingShip.incrementKillsInflicted();
			}
		}
		
		// did it hit a base?
		if (object2.getClass() == Base.class) {
			Base base = (Base) object2;
			
			// only take damageInflicted if not shielded
			if (!base.isShielded()) {
				double initialEnergy = base.getEnergy();
				base.updateEnergy(missile.getDamage());
				
				// kill/assist tags for missiles 
				base.tagShooter(missile.getFiringShip());
				
				if (base.getEnergy() <= 0) {
					// if the base is dead, you can only count the energy it had prior to being dead
					firingShip.incrementDamageInflicted((int) initialEnergy);
					base.incrementDamageReceived(-(int) initialEnergy);
					//System.out.println("Firing at a dead base - should give only " + (int) -initialEnergy + " in damage");
				} else {
					// otherwise the missles count constant
					firingShip.incrementDamageInflicted(-missile.getDamage());
					base.incrementDamageReceived(missile.getDamage());
				}

				// it hit a base
				firingShip.incrementHitsInflicted();
			}
			
		}
		
		// Handle a bullet hitting a bullet
		if (object2.getClass() == Missile.class) {
			object2.setAlive(false);
			Ship otherFiringShip = ((Missile) object2).getFiringShip();
			otherFiringShip.decrementWeaponCount();
		}
		
		//Did the missile hit an AiCore? If so, damage the AiCore.
		if(object2.getClass() == AiCore.class) {
			AiCore core = (AiCore) object2;
			core.updateEnergy(-missile.getDamage());
		}
		
		// make the missile die
		missile.setAlive(false);
	}

	/**
	 * Collide with a EMP
	 * @param emp
	 * @param object2
	 */
	private void EMPCollision(EMP emp, AbstractObject object2) {
		// get the ship that fired
		Ship firingShip = emp.getFiringShip();
		firingShip.decrementWeaponCount();
		
		if (object2.getClass() == Ship.class) {
			Ship ship = (Ship) object2;
			
			// only take a hit if not shielded
			if (!ship.isShielded()) {
				ship.updateEnergy(emp.getDamage());
				ship.incrementDamageReceived(emp.getDamage());
				ship.setFreezeCount(emp.getFreezeCount());

				ship.tagShooter(emp.getFiringShip());

				// it hit a ship
				firingShip.incrementHitsInflicted();
			}
			
		}
		
		if (object2.getClass() == Base.class) {
			Base base = (Base) object2;
			
			// only take a hit if not shielded
			if (!base.isShielded()) {
				base.updateEnergy(emp.getDamage());
				base.incrementDamageReceived(emp.getDamage());
				base.setFreezeCount(emp.getFreezeCount());

				// kill/assist tags for missiles 
				base.tagShooter(emp.getFiringShip());				
				
				// it hit a base
				firingShip.incrementHitsInflicted();
			}
			
		}
		
		// Handle a emp hitting a emp (no damageInflicted but both weapons die)
		if (object2.getClass() == EMP.class) {
			object2.setAlive(false);
			Ship otherFiringShip = ((EMP) object2).getFiringShip();
			otherFiringShip.decrementWeaponCount();
		}
		
		//Did the EMP hit an AiCore? If so, destroy the AiCore.
		if(object2.getClass() == AiCore.class) {
			AiCore core = (AiCore) object2;
			core.setAlive(false); //Kill the core!
		}
		
		//Did the EMP hit an Drone? If so, destroy the Drone.
		if(object2.getClass() == Drone.class) {
			Drone drone = (Drone) object2;
			drone.setAlive(false); //Kill the drone!
		}
		
		emp.setAlive(false);
		
	}

	/**
	 * Collide with an AI Core
	 * 
	 * @param core
	 * @param ship
	 */
	public void collectCore(AiCore core, Ship ship) {
		if (ship.getTeamName().equalsIgnoreCase(core.getTeamName())) {
			core.setAlive(false); //Destroy your own core to prevent it from being captured
		} else {
			// someone else's core so collect it
			core.setAlive(false);
			
			//Bonus energy of random amount between 0 and the core's energy;		
			ship.updateEnergy(ThreadLocalRandom.current().nextInt(core.getCoreEnergy()));
			
			ship.incrementCores();
			ship.setMass(ship.getMass() + core.getMass());
		}
	}
	
	/**
	 * If a base and core collide, the core destroyed if it is a friendly core 
	 * and elastically collides if it is an enemy.  Collision is handled higher up in the function
	 * @param core
	 * @param base
	 */
	public void baseCoreCollide(AiCore core, Base base) {
		if (base.getTeamName().equalsIgnoreCase(core.getTeamName())) {
			core.setAlive(false); //Destroy your core to prevent it from being captured
		} else {
			//core.setAlive(false);
			//base.incrementCores(); //Collect the core.
			
			damageAiCore(core);
		}
		
	}
	
	/**
	 * If a beacon collides with an AiCore, the energy of the core is restored.
	 * @param core
	 * @param beacon
	 */
	public void healAiCore (AiCore core, Beacon beacon) {
		core.resetCoreEnergy();
		beacon.setAlive(false);
	}
	
	/**
	 * If something solid collides with an AiCore, it takes damage.
	 * @param core
	 */
	public void damageAiCore(AiCore core) {
		//double penalty = -Math.abs(COLLISION_PENALTY * core.getPosition().getTotalTranslationalVelocity());
		double penalty = -AiCore.CORE_MAX_ENERGY * 0.2;
		core.updateEnergy((int)(penalty));
	}

	/**
	 * Play a game with a ship and an asteroid
	 * 
	 * @return true if the winner was the ship and false if it was the asteroid (aka simulator)
	 */
	public boolean playGame(AbstractGameAgent opponent) {
		
		if (opponent == null) {
			System.out.println("Gaming asteroid reached and no player specified by opponent: Winner is asteroid.");
			return false;
		} else {
			System.out.println("Gaming asteroid reached and proceeding with game");
		}

		// create the heuristic player for the asteroid
		HeuristicGameAgent myPlayer = new HeuristicGameAgent();

		// choose the game from the gaming factory

		final AbstractGame game = GameFactory.generateNewGame(myPlayer, opponent, ThreadLocalRandom.current());

		// and set the player correctly so we try to win
		if (game.getPlayer1() == myPlayer) {
			System.out.println("Setting player 1 to the asteroid");
			myPlayer.setPlayer(1);
		} else {
			System.out.println("Setting player 1 to the ship");
			myPlayer.setPlayer(2);
		}

		// play the game
		while (!game.isGameOver()) {
			game.playAction(game.getCurrentPlayer().getNextMove(game));
		}

		// returns true if the winner was the ship agent
		if (game.getWinner() == myPlayer.getPlayer()) {
			System.out.println("Gaming asteroid reached: Winner is asteroid.");
			return false;
		} else {
			System.out.println("Gaming asteroid reached: Winner is ship.");
			return true;
		}
	}
	
	
	/**
	 * Collide with an asteroid
	 * 
	 * @param asteroid
	 * @param ship
	 */
	public void mineAsteroid(Asteroid asteroid, Ship ship) {
		// if the asteroid isn't mineable, nothing changes
		if (!asteroid.isMineable()) {
			ship.incrementCollisions();
			return;
		}
		
		// if the asteroid is gameable, the ship needs to play against it before it can get the resources
		if (asteroid.isGameable()) {
			boolean win = playGame(ship.getCurrentGameAgent());
			if (win) {
				// if a ship ran into it, it "mines" the asteroid
				ship.incrementNumMineableAsteroids();
				ship.addResources(asteroid.getResources());
			}
		} else {
			// if a ship ran into it, it "mines" the asteroid
			ship.incrementNumMineableAsteroids();
			ship.addResources(asteroid.getResources());
		}

		// no matter if you win the game or not, the asteroid disappears if we touched it
		asteroid.setAlive(false);
		//System.out.println("ship " + ship.getTeamName() + ship.getId() +" now has resourcesAvailable " + ship.getMoney());
	}

	/**
	 * Give the ship an energy penalty for running into the object
	 * 
	 * @param ship
	 */
	public void shipCollision(Ship ship) {
		double penalty = -Math.abs(COLLISION_PENALTY * ship.getPosition().getTotalTranslationalVelocity());
		ship.updateEnergy((int)(penalty));
		ship.incrementDamageReceived((int)(penalty));
	}
	
	/**
	 * Give the drone an energy penalty for running into the object.
	 * 
	 * @param drone
	 */
	public void droneCollision(Drone drone) {
		double penalty = -Math.abs((COLLISION_PENALTY / 3) * drone.getPosition().getTotalTranslationalVelocity()); //The drone is made of materials that can withstand low mass impacts much better than ships. Less of a penalty for colliding.
		drone.updateEnergy((int)(penalty));
		//Currently no need to track how much damage the drone took for now, but  we should implement it into damage done for aggressive clients.
		drone.incrementDamageReceived((int)(penalty));
	}

	/**
	 * Collide with a beacon
	 * @param beacon
	 * @param object
	 */
	public void beaconCollision(Beacon beacon, AbstractObject object) {
		// beacons die when they are touched (respawned elsewhere)
		beacon.setAlive(false);

		if (object.getClass() == Ship.class) {
			Ship ship = (Ship) object;
			ship.incrementBeaconCount();
			ship.updateEnergy(Beacon.BEACON_ENERGY_BOOST);
		} else if(object.getClass() == Drone.class) {//herr0861
			Drone drone = (Drone) object;
			drone.updateEnergy(Beacon.BEACON_ENERGY_BOOST);
		}
	}

	/**
	 * Collide with a star
	 * @param star
	 * @param object
	 */
	public void starCollision(Star star, AbstractObject object) {
		// stars die when they are touched (respawned elsewhere)
		star.setAlive(false);

		//System.out.println("Star hit an object of type " + object);
		if (object.getClass() == Ship.class) {
			Ship ship = (Ship) object;
			ship.incrementStarCount();
		}
	}
	
	/**
	 * Collide into the base
	 * 
	 * @param base
	 * @param object
	 */
	public void baseCollision(Base base, AbstractObject object) {
		if (object.getClass() == Ship.class) {
			Ship ship = (Ship) object;
			
			if (ship.getTeamName().equalsIgnoreCase(base.getTeamName())) {
				// deposit the resources
				base.addResources(ship.getResources());
				ship.resetResources();
				
				// deposit the flag (if there is one)
				if (ship.isCarryingFlag()) {
					base.addFlag(ship.getFlag());
					ship.depositFlag();
				}

				// deposit any AI Cores
				base.incrementCores(ship.getNumCores());
				ship.resetAiCores();

				// heal the ship 
				double origEnergy = ship.getEnergy();
				ship.updateEnergy(base.getHealingEnergy());
				double energyChange = ship.getEnergy() - origEnergy;
				base.updateEnergy(-(int)energyChange);
				//System.out.println("ship " + ship.getTeamName() + ship.getId() + " left resourcesAvailable at base and now has resourcesAvailable " + ship.getMoney());
			} else {
				countCollision(ship);
			}
		} else if(object.getClass() == Drone.class) {//herr0861 edit
			Drone drone = (Drone) object;
			if (drone.getTeamName().equalsIgnoreCase(base.getTeamName())) {
				base.addResources(drone.getResources());
				drone.resetResources();
				
				if (drone.isCarryingFlag()) {
					base.addFlag(drone.getFlag());
					drone.depositFlag();
				}
				
				//deposit AI Cores
				base.incrementCores(drone.getNumCores());
				drone.resetAiCores();
				
				//heal the drone
				double origEnergy = drone.getEnergy();
				drone.updateEnergy(base.getHealingEnergy());
				double energyChange = drone.getEnergy() - origEnergy;
				base.updateEnergy(-(int)(energyChange));
			}
		}
	}
	

	
	/**
	 * Elastically collide all objects following the vector formulation found at:
	 * 
	 * http://www.vobarian.com/collisions/2dcollisions2.pdf
	 * 
	 * @param object1 first object in the collision
	 * @param object2 second object in the collision
	 * @param space handle to space for distance calculations
	 */
	private void elasticCollision2D(AbstractObject object1,
			AbstractObject object2, Toroidal2DPhysics space) {

		// handle overlapping objects
		adjustCentersAtCollision(object1, object2, space);
		
		// get the masses
		double m1 = object1.getMass();
		double m2 = object2.getMass();
		
		// now get the vector from the first to the second, get the unit normal and tangent
		Vector2D distanceVec = space.findShortestDistanceVector(object1.getPosition(), object2.getPosition());
		Vector2D unitNormal = distanceVec.getUnitVector();
		Vector2D unitTangent = new Vector2D(-unitNormal.getYValue(), unitNormal.getXValue());
		
		// get the velocity vectors
		Vector2D velocity1 = object1.getPosition().getTranslationalVelocity();
		Vector2D velocity2 = object2.getPosition().getTranslationalVelocity();

		// get the scalars in each direction
		double u1 = velocity1.dot(unitNormal);
		double u2 = velocity2.dot(unitNormal);
		double t1 = velocity1.dot(unitTangent);
		double t2 = velocity2.dot(unitTangent);
		
		// elastically collide in the one dimension
		CollisionData result = elasticCollision1D(u1, m1, u2, m2);
		
		// now get it back to the original space
		Vector2D vel1Normal = unitNormal.multiply(result.v1);
		Vector2D vel2Normal = unitNormal.multiply(result.v2);
		Vector2D vel1Tangent = unitTangent.multiply(t1);
		Vector2D vel2Tangent = unitTangent.multiply(t2);
		
		// add the normal and tangential parts
		Vector2D newVelocity1 = vel1Normal.add(vel1Tangent);
		Vector2D newVelocity2 = vel2Normal.add(vel2Tangent);
		
		object1.getPosition().setTranslationalVelocity(newVelocity1);
		object2.getPosition().setTranslationalVelocity(newVelocity2);
	}
	
	/**
	 * Elastically collide all objects following the vector formulation found at:
	 * 
	 * http://www.vobarian.com/collisions/2dcollisions2.pdf
	 * 
	 * @param object1 first object in the collision
	 * @param object2 second object in the collision
	 * @param space handle to space for distance calculations
	 */
	private void elasticCollision2DWithNonMoveableObject(AbstractObject movingObject,
			AbstractObject stationaryObject, Toroidal2DPhysics space) {
		// handle overlapping objects
		adjustCentersAtCollision(movingObject, stationaryObject, space);

		// get the masses
		double m1 = movingObject.getMass();
		double m2 = stationaryObject.getMass();
		
		// now get the vector from the first to the second, get the unit normal and tangent
		Vector2D distanceVec = space.findShortestDistanceVector(movingObject.getPosition(), stationaryObject.getPosition());
		Vector2D unitNormal = distanceVec.getUnitVector();
		Vector2D unitTangent = new Vector2D(-unitNormal.getYValue(), unitNormal.getXValue());
		
		// get the velocity vectors
		Vector2D velocity1 = movingObject.getPosition().getTranslationalVelocity();

		// get the scalars in each direction
		double u1 = velocity1.dot(unitNormal);
		double t1 = velocity1.dot(unitTangent);
		
		// now just reverse the velocity for the first object
		double v1 = -u1;
		
		// now get it back to the original space
		Vector2D vel1Normal = unitNormal.multiply(v1);
		Vector2D vel1Tangent = unitTangent.multiply(t1);
		
		// add the normal and tangential parts
		Vector2D newVelocity1 = vel1Normal.add(vel1Tangent);
		
		movingObject.getPosition().setTranslationalVelocity(newVelocity1);
		
		//ensureObjectsNotStillColliding(movingObject, stationaryObject, space);
	}


	/**
	 * Set the movement in all directions to be 0
	 * 
	 * @param object
	 */
	public void resetMovement(AbstractObject object) {
		object.getPosition().setAngularVelocity(0);
		object.getPosition().setTranslationalVelocity(Vector2D.ZERO_VECTOR);
	}
	
	
	/**
	 * Elastic collisions in 1 dimenson, solved using the equations from wikipedia:
	 * 
	 * http://en.wikipedia.org/wiki/Elastic_collision
	 * 
	 * let
	 * u1 = velocity of item 1 before collision, m1 = mass of item 1
	 * u2 = velocity of item 2 before collision, m2 = mass of item 2
	 * v1 = velocity of item 1 after collision
	 * v2 = velocity of item 2 after collision
	 * 
	 * v1 = ((u1 * (m1 - m2)) + (2 * m2 * u2)) / (m1 + m2)
	 * v2 = ((u2 * (m2 - m1)) + (2 * m1 * u1)) / (m1 + m2)
	 * 
	 */
	public CollisionData elasticCollision1D(double u1, double m1, double u2, double m2) {
		double v1 = ((u1 * (m1 - m2)) + (2 * m2 * u2)) / (m1 + m2);
		double v2 = ((u2 * (m2 - m1)) + (2 * m1 * u1)) / (m1 + m2);

		CollisionData data = new CollisionData();
		data.v1 = v1;
		data.v2 = v2;
		
		return data;
	}

	/**
	 * Adjusts the centers of object1 and object2 so that their 
	 * collision occurs when they are touching, not overlapping
	 * 
	 * This code is a bug fix and is credited to Troy Southard, Spring 2016.  
	 * 
	 * @param object1
	 * @param object2
	 * @param space
	 */
	private void adjustCentersAtCollision(AbstractObject object1, AbstractObject object2, Toroidal2DPhysics space){
		//position reference
		Position pos1 = object1.getPosition();
		Position pos2 = object2.getPosition();

		//x,y positions
		double x1 = pos1.getX();
		double x2 = pos2.getX();
		double y1 = pos1.getY();
		double y2 = pos2.getY();
		
		//x,y velocities
		//negative because time is being reversed
		double u1 = -pos1.getTranslationalVelocityX();
		double u2 = -pos2.getTranslationalVelocityX();
		double v1 = -pos1.getTranslationalVelocityY();
		double v2 = -pos2.getTranslationalVelocityY();
		
		//object radii
		double r1 = object1.getRadius();
		double r2 = object2.getRadius();
		
		// 	Find when radius1 + radius2 == Distance(object1, object2)
		// 	==>
		//	Solve[r1 + r2 == Sqrt[((x2 + u2*t)-(x1 + u1*t))^2 + ((y2 + v2*t)-(y1 + v1*t))^2], t]
		//	==>
		//  Gives two results for t
		 
		double tPlus = 	(-2*u1*x1+2*u2*x1+2*u1*x2-2*u2*x2-2*v1*y1+2*v2*y1+2*v1*y2-2*v2*y2 
							+ Math.sqrt( //+
									Math.pow(2*u1*x1-2*u2*x1-2*u1*x2+2*u2*x2+2*v1*y1-2*v2*y1-2*v1*y2+2*v2*y2,2)
									- 4*(Math.pow(u1,2)-2*u1*u2+Math.pow(u2,2)+Math.pow(v1,2)-2*v1*v2+Math.pow(v2,2))
									*(Math.pow(x1,2)+Math.pow(x2,2)+Math.pow(y1,2)+Math.pow(y2,2)-Math.pow(r1,2)
										-Math.pow(r2,2)-2*r1*r2-2*x1*x2-2*y1*y2)
							)
						)/(2*(Math.pow(u1,2)-2*u1*u2+Math.pow(u2,2)+Math.pow(v1,2)-2*v1*v2+Math.pow(v2,2)));
		
		double tMinus = (-2*u1*x1+2*u2*x1+2*u1*x2-2*u2*x2-2*v1*y1+2*v2*y1+2*v1*y2-2*v2*y2 
							- Math.sqrt( //-
									Math.pow(2*u1*x1-2*u2*x1-2*u1*x2+2*u2*x2+2*v1*y1-2*v2*y1-2*v1*y2+2*v2*y2,2)
									- 4*(Math.pow(u1,2)-2*u1*u2+Math.pow(u2,2)+Math.pow(v1,2)-2*v1*v2+Math.pow(v2,2))
									*(Math.pow(x1,2)+Math.pow(x2,2)+Math.pow(y1,2)+Math.pow(y2,2)-Math.pow(r1,2)
										-Math.pow(r2,2)-2*r1*r2-2*x1*x2-2*y1*y2)
							)
						)/(2*(Math.pow(u1,2)-2*u1*u2+Math.pow(u2,2)+Math.pow(v1,2)-2*v1*v2+Math.pow(v2,2)));
		
		//determine which solution is correct
		//t must lie between 0 and the length of a simulator time step
		// Amy McGovern: added 2.0 * duration instead of just duration because of double precision issues
		double time = 0;
		//System.out.println("Time adjustment solutions tplus = " + tPlus + " tminus = " + tMinus);
		if(Math.abs(tPlus) < (2.0 * space.getTimestepDuration()))
			time = tPlus;
		else if(Math.abs(tMinus) < (2.0* space.getTimestepDuration()))
			time = tMinus;
		
		//System.out.println("time adjustment is " + time);
			
		//adjusted object centers
		Position adjustedPos1 = translatePosition(space, pos1, -time);
		Position adjustedPos2 = translatePosition(space, pos2, -time);
		
		//set adjusted object centers
		object1.setPosition(adjustedPos1);
		object2.setPosition(adjustedPos2);
	}
	
	/**
	 * Translates a position by its velocity*timeStep
	 * 
	 * This code is a bug fix and is credited to Troy Southard, Spring 2016.  
	 * 
	 * @param space
	 * @param position position to be translated
	 * @param timestep how long to translate
	 * @return the translated position
	 */
	private Position translatePosition(Toroidal2DPhysics space, Position position, double timeStep) {
		// new x,y coordinates
		double newX = position.getX() + (position.getTranslationalVelocityX() * timeStep);
		double newY = position.getY() + (position.getTranslationalVelocityY() * timeStep);

		Position newPosition = new Position(newX, newY, position.getOrientation());
		newPosition.setAngularVelocity(position.getAngularVelocity());
		newPosition.setTranslationalVelocity(position.getTranslationalVelocity());
		space.toroidalWrap(newPosition);
		return newPosition;
	}
	
}
