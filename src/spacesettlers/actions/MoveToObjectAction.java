package spacesettlers.actions;

import spacesettlers.objects.AbstractObject;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * Calls MoveAction for the actual movements but allows you to aim for a spacewar object
 * and to stop when the object dies (e.g. someone (maybe you) reached it)
 * 
 * @author amy
 */
public class MoveToObjectAction extends MoveAction {
	protected AbstractObject goalObject;
	protected Position originalGoalLocation;
	
	/**
	 * Initialize with your location and the goal object 
	 * 
	 * @param space
	 * @param currentLocation
	 * @param goalObject
	 */
	public MoveToObjectAction(Toroidal2DPhysics space, Position currentLocation, AbstractObject goalObject) {
		super(space, currentLocation, goalObject.getPosition());
		this.goalObject = goalObject;
		this.originalGoalLocation = goalObject.getPosition().deepCopy();
	}

	/**
	 * Initialize with your location and the goal object 
	 * 
	 * @param space
	 * @param currentLocation
	 * @param goalObject
	 */
	public MoveToObjectAction(Toroidal2DPhysics space, Position currentLocation, AbstractObject goalObject, 
			Vector2D goalVelocity) {
		super(space, currentLocation, goalObject.getPosition(), goalVelocity);
		this.goalObject = goalObject;
		this.originalGoalLocation = goalObject.getPosition().deepCopy();
	}

	/**
	 * Return the goal object (and remember it is a clone so use its UUID!)
	 * @return
	 */
	public AbstractObject getGoalObject() {
		return goalObject;
	}




	/**
	 * Returns true if the movement finished or the goal object died or moved
	 * 
	 */
	public boolean isMovementFinished(Toroidal2DPhysics space) {
		if (super.isMovementFinished(space)) {
			//System.out.println("Super movement finished");
			return true;
		}
		
		AbstractObject newGoalObj = space.getObjectById(goalObject.getId());
		
		// goal object disappeared
		if (newGoalObj == null) {
			//System.out.println("Goal object disappeared");
			return true;
		}
		
		// goal object died
		if (!newGoalObj.isAlive()) {
			//System.out.println("Goal object dead");
			return true;
		} 

		// goal object moved
		if (!newGoalObj.getPosition().equalsLocationOnly(originalGoalLocation)) {
			//System.out.println("Goal object moved");
			return true;
		}
		
		return false;
	}
	

}
