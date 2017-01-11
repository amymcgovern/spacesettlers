package spacesettlers.simulator;

import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.objects.AbstractObject;
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
		if (object1.getClass() == Beacon.class) {
			beaconCollision((Beacon) object1, object2);
			return;
		} else if (object2.getClass() == Beacon.class) {
			beaconCollision((Beacon) object2, object1);
			return;
		}

		// if either object is a missile, handle that (and don't elastically collide)
		if (object1.getClass() == Missile.class) {
			missileCollision((Missile) object1, object2);
			return;
		} else if (object2.getClass() == Missile.class) {
			missileCollision((Missile) object2, object1);
			return;
		}
		
		// if either object is a EMP, handle that (and don't elastically collide)
		if (object1.getClass() == EMP.class) {
			EMPCollision((EMP) object1, object2);
			return;
		} else if (object2.getClass() == EMP.class) {
			EMPCollision((EMP) object2, object1);
			return;
		}
		
		// handle mineable asteroid collisions (e.g. mine them if needed and don't elastically collide, e.g
		// no damage for mining)
		if (object1.getClass() == Asteroid.class && object2.getClass() == Ship.class) {
			if (((Asteroid) object1).isMineable()) {
				mineAsteroid((Asteroid) object1, (Ship) object2);
				return;
			}
		} else if (object2.getClass() == Asteroid.class && object1.getClass() == Ship.class) {
			if (((Asteroid) object2).isMineable()) {
				mineAsteroid((Asteroid) object2, (Ship) object1);
				return;
			}
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

		
		// handle base collisions
		if (object1.getClass() == Base.class) {
			baseCollision((Base) object1, object2);
		} else if (object2.getClass() == Base.class) {
			baseCollision((Base) object2, object1);
		}
		
	}
	
	/**
	 * Collide with a missile
	 * @param object1
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
				ship.updateEnergy(missile.getDamage());
				ship.incrementDamageReceived(missile.getDamage());
				firingShip.incrementDamageInflicted(-missile.getDamage());

				// it hit a ship
				firingShip.incrementHitsInflicted();
			}
			
			// if the bullet killed the ship, credit the ship that hit it
			if (ship.getEnergy() <= 0) {
				//System.out.println("ship " + firingShip.getTeamName() + " stealing resourcesAvailable " + shipMoney + " from " + ship.getTeamName() + ship.getId());
				
				// it killed a ship
				firingShip.incrementKillsInflicted();
				ship.incrementKillsReceived();
			}

		}
		
		// did it hit a base?
		if (object2.getClass() == Base.class) {
			Base base = (Base) object2;
			
			// only take damageInflicted if not shielded
			if (!base.isShielded()) {
				base.updateEnergy(missile.getDamage());
				base.incrementDamageReceived(missile.getDamage());
				firingShip.incrementDamageInflicted(-missile.getDamage());

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
		
		// make the missile die
		missile.setAlive(false);
	}

	/**
	 * Collide with a EMP
	 * @param object1
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
		
		emp.setAlive(false);
		
	}

	
	/**
	 * Collide with an asteroid
	 * 
	 * @param asteroid
	 * @param object
	 */
	public void mineAsteroid(Asteroid asteroid, Ship ship) {
		// if the asteroid isn't mineable, nothing changes
		if (!asteroid.isMineable()) {
			return;
		}
		
		// if a ship ran into it, it "mines" the asteroid
		ship.addResources(asteroid.getResources());
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
				base.addResources(ship.getResources());
				ship.resetResources();
				double origEnergy = ship.getEnergy();
				ship.updateEnergy(base.getHealingEnergy());
				double energyChange = ship.getEnergy() - origEnergy;
				base.updateEnergy(-(int)energyChange);
				//System.out.println("ship " + ship.getTeamName() + ship.getId() + " left resourcesAvailable at base and now has resourcesAvailable " + ship.getMoney());
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
		object.getPosition().getTranslationalVelocity().reset();
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
		double time = 0;
		if(Math.abs(tPlus) < space.getTimestep())
			time = tPlus;
		else if(Math.abs(tMinus) < space.getTimestep())
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
