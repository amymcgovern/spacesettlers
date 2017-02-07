package morr0964;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * The model keeps track of asteroid velocities in order to predict their future locations.
 * This can help agents target future asteroid position instead of chasing after them if they are mobile.
 *  
 * @author Harrison
 *
 */
public class AsteroidModel extends KnowledgeRep {
	//stores velocities of asteroids
	HashMap <UUID, Vector2D> asteroidToVelocityMap;
	//stores old position of asteroids
	HashMap <UUID, Position> asteroidToPositionMap;
	//time step of last position
	double oldTime;
	

	/**
	 * Initializes the hash maps for future use
	 */
	public AsteroidModel(Toroidal2DPhysics space){
		//create HashMaps
		asteroidToVelocityMap=new HashMap<UUID, Vector2D>();
		asteroidToPositionMap=new HashMap<UUID, Position>();
		
		//populate position map
		Set<Asteroid> asteroids=space.getAsteroids();
		for(Asteroid a:asteroids){
			asteroidToPositionMap.put(a.getId(),a.getPosition());
		}
		
		//set time step
		oldTime=space.getTimestep();
	}
	
	/**
	 * updates the asteroid velocities
	 * @param space the space with the asteroids
	 */
	public void update(Toroidal2DPhysics space){
		//get all current asteroids and current timestep
		Set<Asteroid> asteroids=space.getAsteroids();
		double currentTime=space.getTimestep();
		//clear old velocities
		asteroidToVelocityMap.clear();
		
		//compare to old positions and calculate velocity
		Position pos1, pos2;
		Vector2D vel;
		for(Asteroid a:asteroids){
			pos1=asteroidToPositionMap.get(a.getId());
			pos2=a.getPosition();
			
			//If old asteroid position is known, calculate velocity and store it
			if(pos1!=null){
				vel=new Vector2D((pos2.getX()-pos1.getX())/(currentTime-oldTime),(pos2.getY()-pos1.getY())/(currentTime-oldTime));
				asteroidToVelocityMap.put(a.getId(),vel);
			}
			//otherwise, assume 0 velocity
			else{
				asteroidToVelocityMap.put(a.getId(),new Vector2D());
			}
		}
		
		//update old positions and timestamp
		asteroidToPositionMap.clear();
		for(Asteroid a:asteroids){
			asteroidToPositionMap.put(a.getId(),a.getPosition());
		}
		oldTime=space.getTimestep();
	}
	
	/**
	 * Predicts the location of an asteroid with UUID id, time time units from now
	 * @param time how far in the future it should predict
	 * @param id the id of the asteroid for prediction
	 * @return the position at that time (null if invalid id)
	 */
	public Position getFutureLocation(double time, UUID id){
		Position pos=null;
		
		return pos;
	}
}
