package spacesettlers.actions;

import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Movement;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * A move action specifies the setpoint to move to and the desired velocity and orientation.
 * 
 * This action moves the ship to the desired location but turns to the requested orientation
 * 
 * The action uses PD control to move the ship.  It does this by:
 * 
 * 1) Orienting the ship to the target location
 * 2) Accelerating to the target location
 * 
 * @author amy
 */
public class MoveActionWithOrientation extends MoveAction {
	/**
	 * Make a new move action and save the goal locations.  
	 * 
	 * @param space physics for spacewar movements
	 * @param currentLocation the current location of the ship
	 * @param targetLocation the target location of the ship - MAKE SURE THIS HAS AN ORIENTATION
	 * @param targetVelocity the velocity the ship should be at when it reaches its target location
	 * @throws SpaceSettlersActionException
	 */
	public MoveActionWithOrientation(Toroidal2DPhysics space, Position currentLocation, 
			Position targetLocation, Vector2D targetVelocity) {
		super(space, currentLocation, targetLocation, targetVelocity);
	}

	/**
	 * Make a new move action and save the goal locations.  Note the target velocity is assumed to be 0,0
	 * 
	 * @param space physics for spacewar movements
	 * @param currentLocation the current location of the ship
	 * @param targetLocation the target location of the ship - MAKE SURE THIS HAS AN ORIENTATION
	 */
	public MoveActionWithOrientation(Toroidal2DPhysics space, Position currentLocation, 
			Position targetLocation) {
		super(space, currentLocation, targetLocation);
	}

	
	/**
	 * This constructor assumes you will call the pd control functions directly so it does not initialize anything
	 * This is used by the unit tests but it could be used by others as well.
	 * 
	 */
	public MoveActionWithOrientation() {
		super();
	}

	/**
	 * Returns the error in orientation space (separate function so we can use it to decide
	 * when it has reached the goal)
	 * 
	 * @param space
	 * @param goalLoc
	 * @param currentLoc
	 * @return
	 */
	private double getOrientationError(Toroidal2DPhysics space, Position goalLoc, Position currentLoc) {
		// removed the vector computation as it was causing the bug where the ship would always
		// orient to the goal along this vector and not to the goal orientation the user specified
		//Vector2D shortdist = space.findShortestDistanceVector(currentLoc, goalLoc);
		//double angle = shortdist.getAngle();

		double error = (goalLoc.getOrientation() - currentLoc.getOrientation());

		if (error > Math.PI) {
			error -= 2 * Math.PI;
		} else if (error < -Math.PI) {
			error += 2 * Math.PI;
		}
		return error;
	}


	/**
	 * Orient to the goal location using pd control.  Returns angular acceleration.
	 * 
	 * @param goalLoc location that you are trying to get to
	 * @param currentLoc your current location
	 * @param goalAngularVelocity velocity you want to be at when you reach the goal location
	 * @param currentVelocity your current velocity
	 * @return angular acceleration needed to move to the goal
	 */
	public double pdControlOrientToGoal(Toroidal2DPhysics space, Position goalLoc,
			Position currentLoc, double goalAngularVelocity) {

		double error1 = this.getOrientationError(space, goalLoc, currentLoc);
		double error2  = (goalAngularVelocity - currentLoc.getAngularVelocity());

		return super.pdControlOrient(error1, error2);
	}


	/**
	 * Move the ship to the goal by turning and moving to the goal at the same time.
	 * 
	 */
	public Movement getMovement(Toroidal2DPhysics space, Ship ship) {
		// isOrientedToGoal, isAtGoal, isOrientedAtGoal, isOrientingAtGoal
		Movement movement = new Movement();

		if (isFinished) {
			return movement;
		}

		// set the angular and translational velocity at the same time
		double angularAccel = this.pdControlOrientToGoal(space, targetLocation, ship.getPosition(), 0);
		movement.setAngularAccleration(angularAccel);
		Vector2D goalAccel = super.pdControlMoveToGoal(space, targetLocation, ship.getPosition(), targetVelocity);
		movement.setTranslationalAcceleration(goalAccel);

		// figure out if it has reached the goal
		if ((goalAccel.getMagnitude() < TARGET_REACHED_ACCEL) ||
				(space.findShortestDistance(targetLocation, ship.getPosition()) < TARGET_REACHED_ERROR)) {
			isFinished = true;
		}

		return movement;
	}


	

}
