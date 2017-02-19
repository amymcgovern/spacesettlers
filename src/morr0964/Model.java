package morr0964;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * The model keeps track of what the agent is doing, so it doesn't get distracted by beacons when going
 * home, or change its target asteroid prematurely (wasting energy). It also has all the parent KnowledgeRep functionality
 *  
 * @author Harrison
 *
 */
public class Model extends KnowledgeRep {
	//stores the current goal object
	private AbstractObject currentGoal;
	
	/**
	 * Creates the model and initializes the current goal to null (no goal)
	 */
	public Model(){
		currentGoal=null;
	}
	
	/**
	 * Resets the goal to nothing
	 * @param space the space 
	 */
	public void reset(){
		currentGoal=null;
	}
	
	/**
	 * Sets the target to obj
	 * @param obj the new target
	 */
	public void setGoal(AbstractObject obj){
		currentGoal=obj;
	}
	
	/**
	 * Returns the current goal, with updated values
	 * @return the current goal
	 */
	public AbstractObject getGoal(Toroidal2DPhysics space){
		if(currentGoal!=null){
			currentGoal=space.getObjectById(currentGoal.getId());
		}
		return currentGoal;
	}
}
