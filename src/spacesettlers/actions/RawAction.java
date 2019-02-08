package spacesettlers.actions;

import spacesettlers.objects.Drone;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Movement;
import spacesettlers.utilities.Vector2D;

/**
 * A raw action simply sets the acceleration (translational or rotational) directly
 * from the client (no PD control so the ship better know what it is doing).  This
 * was mainly intended to be used for the human controlled ship but can be used by any
 * client that wants to directly control acceleration.
 * 
 * @author amy
 *
 */
public class RawAction extends AbstractAction {
	/**
	 * The accelerations
	 */
	protected double angularAcceleration;
	protected Vector2D translationalAcceleration;

	/**
	 * Create the raw action
	 * @param translationalAcceleration desired translational acceleration (for both x and y)
	 * @param rotationalAcceleration desired rotational acceleration
	 */
	public RawAction(double translationalAcceleration,
			double rotationalAcceleration) {
		super();
		Vector2D translation = new Vector2D(translationalAcceleration, translationalAcceleration);
		this.translationalAcceleration = translation;
		this.angularAcceleration = rotationalAcceleration;
	}

	/**
	 * Create the raw action
	 * @param translationalAcceleration desired translational acceleration
	 * @param rotationalAcceleration desired rotational acceleration
	 */
	public RawAction(Vector2D translationalAcceleration,
			double rotationalAcceleration) {
		super();
		this.translationalAcceleration = translationalAcceleration;
		this.angularAcceleration = rotationalAcceleration;
	}

	/**
	 * Set the raw translational accelerations
	 */
	@Override
	public Movement getMovement(Toroidal2DPhysics space, Ship ship) {
		Movement movement = new Movement();
		movement.setAngularAccleration(angularAcceleration);
		movement.setTranslationalAcceleration(translationalAcceleration);
		return movement;
	}

	/**
	 * This is a single step movement so it is always finished after a step
	 */
	@Override
	public boolean isMovementFinished(Toroidal2DPhysics space) {
		return true;
	}

	/**
	 * Set the raw translational accelerations
	 */
	@Override
	public Movement getMovement(Toroidal2DPhysics space, Drone drone) {
		Movement movement = new Movement();
		movement.setAngularAccleration(angularAcceleration);
		movement.setTranslationalAcceleration(translationalAcceleration);
		return movement;
	}

}
