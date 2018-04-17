package spacesettlers.actions;

import spacesettlers.objects.Drone;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Movement;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * A move action specifies the setpoint to move to and the desired velocity 
 * 
 * Note this action IGNORES the orientation inside the targetLocation!  If you want to move one way
 * with your ship but point in a different way, you should use MoveActionSeparateOrientation. 
 * 
 * The action uses PD control to move the ship.  It does this by:
 * 
 * 1) Orienting the ship to the target location
 * 2) Accelerating to the target location
 * 
 * @author amy
 */
public class MoveAction extends AbstractAction {
	/**
	 * If the acceleration is less than this number, the target is assumed to be reached
	 */
	public static double TARGET_REACHED_ACCEL = 0.0001;

	/**
	 * allowable error in the target error before you say it is there
	 */
	public static double TARGET_REACHED_ERROR = 2.0;

	/**
	 * Target location
	 */
	protected Position targetLocation;

	/**
	 * Target velocity when the location is reached
	 */
	protected Vector2D targetVelocity;

	/**
	 * Target orientation when the location is reached
	 */
	double targetOrientation;

	/**
	 * Internal variables to know which controller needs to be called
	 */
	protected boolean isFinished;

	/**
	 * Constants for pd control
	 */
	double KpTranslational, KvTranslational, KpRotational, KvRotational;

	/**
	 * Make a new move action and save the goal locations.  
	 * 
	 * @param space physics for spacewar movements
	 * @param currentLocation the current location of the ship
	 * @param targetLocation the target location of the ship
	 * @param targetVelocity the velocity the ship should be at when it reaches its target location
	 * @throws SpaceSettlersActionException
	 */
	public MoveAction(Toroidal2DPhysics space, Position currentLocation, 
			Position targetLocation, Vector2D targetVelocity) {
		super();
		
		if (Double.isFinite(targetLocation.getX()) && Double.isFinite(targetLocation.getY())) {
			this.targetLocation = targetLocation;
		} else {
			System.out.println("Error: you called MoveAction with a target location that is not finite. Using current instead.");
			this.targetLocation = currentLocation;
		}
		
		if (Double.isFinite(targetVelocity.getXValue()) && Double.isFinite(targetVelocity.getYValue())) {
			this.targetVelocity = targetVelocity;
		} else {
			System.out.println("Error: you called MoveAction with a target velocity that is not finite. Using zero instead.");
			this.targetVelocity = new Vector2D();
		}
		this.isFinished = false;
		KvRotational = 2.53;
		KpRotational = 1.6;
		KvTranslational = 0.56f;
		KpTranslational = 0.08f;
	}

	/**
	 * Make a new move action and save the goal locations.  Note the target velocity is assumed to be 0,0
	 * 
	 * @param space physics for spacewar movements
	 * @param currentLocation the current location of the ship
	 * @param targetLocation the target location of the ship
	 */
	public MoveAction(Toroidal2DPhysics space, Position currentLocation, 
			Position targetLocation) {
		super();

		if (Double.isFinite(targetLocation.getX()) && Double.isFinite(targetLocation.getY())) {
			this.targetLocation = targetLocation;
		} else {
			System.out.println("Error: you called MoveAction with a target location that is not finite. Using current instead.");
			this.targetLocation = currentLocation;
		}

		this.targetVelocity = new Vector2D();
		this.isFinished = false;
		KvRotational = 2.53;
		KpRotational = 1.6;
		KvTranslational = 0.56f;
		KpTranslational = 0.08f;
	}

	
	/**
	 * This constructor assumes you will call the pd control functions directly so it does not initialize anything
	 * This is used by the unit tests but it could be used by others as well.
	 * 
	 */
	public MoveAction() {
		super();
		KvRotational = 2.53;
		KpRotational = 1.6;
		KvTranslational = 0.56f;
		KpTranslational = 0.08f;
	}

	/**
	 * Be careful setting these.  They can break control.  
	 * To be critically damped, the parameters must satisfy:
	 * 2 * sqrt(Kp) = Kv
	 * @param kpTranslational
	 */
	public void setKpTranslational(double kpTranslational) {
		KpTranslational = kpTranslational;
	}

	public void setKvTranslational(double kvTranslational) {
		KvTranslational = kvTranslational;
	}

	public void setKpRotational(double kpRotational) {
		KpRotational = kpRotational;
	}

	public void setKvRotational(double kvRotational) {
		KvRotational = kvRotational;
	}

	/**
	 * Proportional derivative controller.  The equation is:
	 * acceleration = k_p * position error + k_v * velocity error
	 * 
	 * To be critically damped, the parameters must satisfy:
	 * 2 * sqrt(Kp) = Kv
	 *
	 * @param positionError the error in position (can be orientation too) 
	 * @param velocityError the error in the velocity between the current and target
	 */
	protected double pdControlOrient(double positionError, double velocityError) {
		// tunable parameters; should be in relationship to one another
		// 2 sqrt(Kp) = Kv
		//double Kv = 2.53;
		//double Kp = 1.6;

		// take care of wrap-around
		double error1 = positionError * KpRotational;

		double error2  = velocityError * KvRotational;

		double accel = error1 + error2;

		return accel;
	}

	/**
	 * Proportional derivative controller.  The equation is:
	 * acceleration = k_p * position error + k_v * velocity error
	 * 
	 * To be critically damped, the parameters must satisfy:
	 * 2 * sqrt(Kp) = Kv
	 *
	 * @param positionError the error in position (can be orientation too) 
	 * @param velocityError the error in the velocity between the current and target
	 */
	private double pdControlTranslate(double positionError, double velocityError) {
		// tunable parameters; should be in relationship to one another
		// 2 sqrt(Kp) = Kv
//		float Kv = 0.8f;
//		float Kp = 0.16f;
		//float Kv = 0.56f;
		//float Kp = 0.08f;

		// take care of wrap-around
		double error1 = positionError * KpTranslational;
		double error2  = velocityError * KvTranslational;

		double accel = error1 + error2;

		return accel;
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
		Vector2D shortdist = space.findShortestDistanceVector(currentLoc, goalLoc);
		double angle = shortdist.getAngle();

		double error = (angle - currentLoc.getOrientation());

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

		double error1 = getOrientationError(space, goalLoc, currentLoc);
		double error2  = (goalAngularVelocity - currentLoc.getAngularVelocity());

		return pdControlOrient(error1, error2);
	}


	/**
	 * Accelerate to the goal location using pd control.  Assumes you are already pointed at the goal. Returns
	 * the linear/translational acceleration needed to move to the goal.
	 * 
	 * @param goalLoc location that you are trying to get to
	 * @param currentLoc your current location
	 * @param goalVelocity velocity you want to be at when you reach the goal location
	 * @return the accleration vector needed to move to the goal
	 */
	public Vector2D pdControlMoveToGoal(Toroidal2DPhysics space, Position goalLoc,
			Position currentLoc, Vector2D goalVelocity) {
		// take care of wrap-around
		Vector2D shortestDist = space.findShortestDistanceVector(currentLoc, goalLoc);
		
		double xError = shortestDist.getXValue();
		double yError = shortestDist.getYValue();
		//System.out.println("xerror is " + xError + " yError is " + yError);
		
		//System.out.println("Goal velocity is " + goalVelocity);
		//System.out.println("Current velocity is " + currentLoc.getTranslationalVelocity());
		double velocityErrorX = (goalVelocity.getXValue() - currentLoc.getTranslationalVelocityX());
		double velocityErrorY = (goalVelocity.getYValue() - currentLoc.getTranslationalVelocityY());
		//System.out.println("Velocity error is " + velocityErrorX + " ," + velocityErrorY);

		double xAccel = pdControlTranslate(xError, velocityErrorX);
		double yAccel = pdControlTranslate(yError, velocityErrorY);
		
		//System.out.println("Translation accel is " + xAccel + ", " + yAccel);
		//System.out.println("Orienting to goal with " + xAccel + ", " + yAccel);
		return new Vector2D(xAccel, yAccel);
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
		double angularAccel = pdControlOrientToGoal(space, targetLocation, ship.getPosition(), 0);
		movement.setAngularAccleration(angularAccel);
		Vector2D goalAccel = pdControlMoveToGoal(space, targetLocation, ship.getPosition(), targetVelocity);
		movement.setTranslationalAcceleration(goalAccel);

		// figure out if it has reached the goal
		if ((goalAccel.getMagnitude() < TARGET_REACHED_ACCEL) ||
				(space.findShortestDistance(targetLocation, ship.getPosition()) < TARGET_REACHED_ERROR)) {
			isFinished = true;
		}

		return movement;
	}
	
	/**
	 * Herr0861 edit.
	 * Overloading getMovement() to allow for handling of multiple object types. In this case, Drone.
	 * Move the drone to the goal by turning and moving to the goal at the same time.
	 * 
	 */
	public Movement getMovement(Toroidal2DPhysics space, Drone drone) {
		// isOrientedToGoal, isAtGoal, isOrientedAtGoal, isOrientingAtGoal
		Movement movement = new Movement();

		if (isFinished) {
			return movement;
		}

		// set the angular and translational velocity at the same time
		double angularAccel = pdControlOrientToGoal(space, targetLocation, drone.getPosition(), 0);
		movement.setAngularAccleration(angularAccel);
		Vector2D goalAccel = pdControlMoveToGoal(space, targetLocation, drone.getPosition(), targetVelocity);
		movement.setTranslationalAcceleration(goalAccel);

		// figure out if it has reached the goal
		if ((goalAccel.getMagnitude() < TARGET_REACHED_ACCEL) ||
				(space.findShortestDistance(targetLocation, drone.getPosition()) < TARGET_REACHED_ERROR)) {
			isFinished = true;
		}

		return movement;
	}


	@Override
	public boolean isMovementFinished(Toroidal2DPhysics space) {
		return isFinished;
	}

	

}
