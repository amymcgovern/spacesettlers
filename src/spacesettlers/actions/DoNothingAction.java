package spacesettlers.actions;

import spacesettlers.objects.Drone;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Movement;

/**
 * Don't move. 
 * 
 * @author amy
 */
public class DoNothingAction extends AbstractAction {
	/**
	 * This action never moves the ship
	 */
	public Movement getMovement(Toroidal2DPhysics space, Ship ship) {
		return new Movement();
	}
	
	/**
	 * This action never moves the drone
	 * Herr0861 edit
	 * This is an overloaded method to allow handling of an additional class. In this case, Drone.
	 */
	public Movement getMovement(Toroidal2DPhysics space, Drone drone) {
		return new Movement();
	}

	/**
	 * This action never moves the ship so it is always done immediately
	 */
	public boolean isMovementFinished(Toroidal2DPhysics space) {
		return true;
	}

}
