package spacesettlers.actions;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spacesettlers.actions.MoveAction;
import spacesettlers.actions.SpaceSettlersActionException;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Movement;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * Ensure the pd control works
 * @author amy
 *
 */
public class TestMoveAction {
	MoveAction moveAction;
	double timestep = 0.05;
	Toroidal2DPhysics space;
	Vector2D targetVelocity;

	@Before
	public void setUp() throws Exception {
		space = new Toroidal2DPhysics(480, 640, timestep);
		targetVelocity = new Vector2D();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test orienting in both directions
	 * 
	 * (40, 40)
	 * 			(50,50)
	 * (40, 60)
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testpdControlOrientToGoal() throws SpaceSettlersActionException {
		// first to -3pi/4
		Position currentLoc = new Position(50,50);
		Position goalLoc = new Position(40,40);
		
		moveAction = new MoveAction();
	
		double accel = moveAction.pdControlOrientToGoal(space, goalLoc, currentLoc, 0);
		
		Movement movement = new Movement();
		
		while (Math.abs(accel) > MoveAction.TARGET_REACHED_ACCEL) {
			movement.setAngularAccleration(accel);
			currentLoc = space.applyMovement(currentLoc, movement, timestep);
			accel = moveAction.pdControlOrientToGoal(space, goalLoc, currentLoc, 0);
		}
		
		assertEquals(currentLoc.getOrientation(), -(3 * Math.PI) / 4, 0.01);

	
		// then to 3pi/4
		currentLoc = new Position(50,50);
		currentLoc.setOrientation(0);
		goalLoc = new Position(40,60);
	
		accel = moveAction.pdControlOrientToGoal(space, goalLoc, currentLoc, 0);
		
		movement = new Movement();
		
		while (Math.abs(accel) > MoveAction.TARGET_REACHED_ACCEL) {
			movement.setAngularAccleration(accel);
			currentLoc = space.applyMovement(currentLoc, movement, timestep);
			accel = moveAction.pdControlOrientToGoal(space, goalLoc, currentLoc, 0);
		}
		
		assertEquals(currentLoc.getOrientation(), (3 * Math.PI) / 4, 0.01);

	}

	/**
	 * Test moving to the goal
	 * 
	 * (40, 40)
	 * 			(50,50)
	 * (40, 60)
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testpdControlMoveToGoal() throws SpaceSettlersActionException {
		// first to -3pi/4
		Position currentLoc = new Position(50,50);
		Position goalLoc = new Position(40,40);
		currentLoc.setOrientation(-(3 * Math.PI) / 4);
		
		moveAction = new MoveAction();
	
		Vector2D accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		Movement movement = new Movement();
		
		while (accel.getMagnitude() > MoveAction.TARGET_REACHED_ACCEL) {
			movement.setTranslationalAcceleration(accel);
			currentLoc = space.applyMovement(currentLoc, movement, timestep);
			accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		}
		
		assertEquals(currentLoc.getOrientation(), -(3 * Math.PI) / 4, 0.01);
		assertEquals(currentLoc.getX(), 40, 0.05);
		assertEquals(currentLoc.getY(), 40, 0.05);

	
		// then to 3pi/4
		currentLoc = new Position(50,50);
		currentLoc.setOrientation(0);
		goalLoc = new Position(40,60);
		currentLoc.setOrientation((3 * Math.PI) / 4);
		
		accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		
		while (accel.getMagnitude() > MoveAction.TARGET_REACHED_ACCEL) {
			movement.setTranslationalAcceleration(accel);
			currentLoc = space.applyMovement(currentLoc, movement, timestep);
			accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		}
		
		assertEquals(currentLoc.getOrientation(), (3 * Math.PI) / 4, 0.01);
		assertEquals(currentLoc.getX(), 40, 0.05);
		assertEquals(currentLoc.getY(), 60, 0.05);

	}

	
	/**
	 * Test moving to the goal
	 * 
	 * (30, 50) 	(50,50)  (70, 50)
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testpdControlMoveToAlongX() throws SpaceSettlersActionException {
		// first positive x (60,50)
		Position currentLoc = new Position(50,50);
		Position goalLoc = new Position(70,50);
		currentLoc.setOrientation(0);
		
		moveAction = new MoveAction();
	
		Vector2D accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		Movement movement = new Movement();
		
		while (accel.getMagnitude() > MoveAction.TARGET_REACHED_ACCEL) {
			movement.setTranslationalAcceleration(accel);
			currentLoc = space.applyMovement(currentLoc, movement, timestep);
			accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		}
		
		assertEquals(currentLoc.getOrientation(), 0, 0.01);
		assertEquals(currentLoc.getX(), 70, 0.05);
		assertEquals(currentLoc.getY(), 50, 0.05);

	
		// then to the negative x
		currentLoc = new Position(50,50);
		currentLoc.setOrientation(0);
		goalLoc = new Position(30,50);
		currentLoc.setOrientation(-Math.PI);
		
		accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		
		while (accel.getMagnitude() > MoveAction.TARGET_REACHED_ACCEL) {
			movement.setTranslationalAcceleration(accel);
			currentLoc = space.applyMovement(currentLoc, movement, timestep);
			accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		}
		
		assertEquals(currentLoc.getOrientation(), -Math.PI, 0.01);
		assertEquals(currentLoc.getX(), 30, 0.05);
		assertEquals(currentLoc.getY(), 50, 0.05);

	}

	/**
	 * Test moving to the goal along the y dimension
	 * (50, 40)
	 * (50, 50)
	 * (50,60)
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testpdControlMoveToAlongY() throws SpaceSettlersActionException {
		// first positive y (50, 60)
		Position currentLoc = new Position(50,50);
		Position goalLoc = new Position(50,60);
		currentLoc.setOrientation(Math.PI / 2);
		
		moveAction = new MoveAction();
	
		Vector2D accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		Movement movement = new Movement();
		
		while (accel.getMagnitude() > MoveAction.TARGET_REACHED_ACCEL) {
			movement.setTranslationalAcceleration(accel);
			currentLoc = space.applyMovement(currentLoc, movement, timestep);
			accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		}
		
		assertEquals(currentLoc.getOrientation(), Math.PI / 2, 0.01);
		assertEquals(currentLoc.getX(), 50, 0.05);
		assertEquals(currentLoc.getY(), 60, 0.05);

	
		// then to the negative y
		currentLoc = new Position(50,50);
		currentLoc.setOrientation(0);
		goalLoc = new Position(50,40);
		currentLoc.setOrientation(-Math.PI / 2);
		
		accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		
		while (accel.getMagnitude() > MoveAction.TARGET_REACHED_ACCEL) {
			movement.setTranslationalAcceleration(accel);
			currentLoc = space.applyMovement(currentLoc, movement, timestep);
			accel = moveAction.pdControlMoveToGoal(space, goalLoc, currentLoc, targetVelocity);
		}
		
		assertEquals(currentLoc.getOrientation(), -Math.PI / 2, 0.01);
		assertEquals(currentLoc.getX(), 50, 0.05);
		assertEquals(currentLoc.getY(), 40, 0.05);

	}

	
	
}
