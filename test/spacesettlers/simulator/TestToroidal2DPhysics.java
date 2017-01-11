package spacesettlers.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Movement;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

public class TestToroidal2DPhysics {
	Position position;
	double timestep = 1;
	Toroidal2DPhysics space;
	int height, width;
	
	@Before
	public void setUp() throws Exception {
		position= new Position(0,0,0);
		height = 480;
		width = 640;
		space = new Toroidal2DPhysics(height, width, timestep);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testApplyOrientationMoveFromStop() {
		Movement movement = new Movement();
		double angularAccel = Math.PI / 60.0;
		movement.setAngularAccleration(angularAccel);
		movement.setTranslationalAcceleration(new Vector2D());
		
		double expectedOrientation = angularAccel;
		
		Position newPosition = space.applyMovement(position, movement, timestep);
		
		assertEquals(newPosition.getOrientation(), expectedOrientation, 0.01);
		assertEquals(newPosition.getAngularVelocity(), angularAccel, 0.01);
		assertEquals(newPosition.getX(), 0, 0.0);
		assertEquals(newPosition.getY(), 0, 0.0);
		assertEquals(newPosition.getTotalTranslationalVelocity(), 0, 0);
	}

	@Test
	public void testApplyOrientationMoveFromTurning() {
		Movement movement = new Movement();
		movement.setAngularAccleration(Math.PI / 180.0);
		movement.setTranslationalAcceleration(new Vector2D());
		
		double expectedOrientation = (3.0 * Math.PI) / 180.0;

		// first time is from stop
		Position newPosition = space.applyMovement(position, movement, timestep);
		
		// second time it is moving already
		newPosition = space.applyMovement(newPosition, movement, timestep);
		
		assertEquals(newPosition.getOrientation(), expectedOrientation, 0.01);
		assertEquals(newPosition.getAngularVelocity(), Math.PI / 90.0, 0.01);
		assertEquals(newPosition.getX(), 0, 0.0);
		assertEquals(newPosition.getY(), 0, 0.0);
		assertEquals(newPosition.getTranslationalVelocityX(), 0, 0);
		assertEquals(newPosition.getTranslationalVelocityY(), 0, 0);
}

	@Test
	public void testApplyTranslationalMovementHorizontalFromStop() {
		Movement movement = new Movement();
		movement.setAngularAccleration(0);
		movement.setTranslationalAcceleration(new Vector2D(10, 0));

		double expectedX = 10;
		double expectedY = 0;
		
		Position newPosition = space.applyMovement(position, movement, timestep);
		
		assertEquals(newPosition.getX(), expectedX, 0.01);
		assertEquals(newPosition.getY(), expectedY, 0.01);
		assertEquals(newPosition.getOrientation(), 0, 0.01);
		assertEquals(newPosition.getAngularVelocity(), 0, 0.01);
		assertEquals(newPosition.getTranslationalVelocityX(), 10, 0.01);
		assertEquals(newPosition.getTranslationalVelocityY(), 0, 0.01);
	}

	@Test
	public void testApplyTranslationalMovementVerticalFromStop() {
		Movement movement = new Movement();
		movement.setAngularAccleration(0);
		movement.setTranslationalAcceleration(new Vector2D(0,10));
		position.setOrientation(Math.PI / 2);

		double expectedX = 0;
		double expectedY = 10;
		
		Position newPosition = space.applyMovement(position, movement, timestep);
		
		assertEquals(newPosition.getX(), expectedX, 0.01);
		assertEquals(newPosition.getY(), expectedY, 0.01);
		assertEquals(newPosition.getOrientation(), Math.PI / 2, 0.01);
		assertEquals(newPosition.getAngularVelocity(), 0, 0.01);
		assertEquals(newPosition.getTranslationalVelocityX(), 0, 0.01);
		assertEquals(newPosition.getTranslationalVelocityY(), 10, 0.01);
	}

	@Test
	public void testApplyTranslationalMovementAngledFromStop() {
		Movement movement = new Movement();
		movement.setAngularAccleration(0);
		movement.setTranslationalAcceleration(new Vector2D(10,10));
		position.setOrientation(Math.PI / 4);

		double expectedX = 10;
		double expectedY = 10;
		
		Position newPosition = space.applyMovement(position, movement, timestep);
		
		assertEquals(newPosition.getX(), expectedX, 0.01);
		assertEquals(newPosition.getY(), expectedY, 0.01);
		assertEquals(newPosition.getOrientation(), Math.PI / 4, 0.01);
		assertEquals(newPosition.getAngularVelocity(), 0, 0.01);
		assertEquals(newPosition.getTranslationalVelocityX(), 10, 0.01);
		assertEquals(newPosition.getTranslationalVelocityY(), 10, 0.01);
	}

	@Test
	public void testApplyNoMovementFromStop() {
		Movement movement = new Movement();
		movement.setAngularAccleration(0);
		movement.setTranslationalAcceleration(new Vector2D());

		Position newPosition = space.applyMovement(position, movement, timestep);
		
		assertEquals(newPosition.getX(), 0, 0.01);
		assertEquals(newPosition.getY(), 0, 0.01);
		assertEquals(newPosition.getOrientation(), 0, 0.01);
		assertEquals(newPosition.getAngularVelocity(), 0, 0.01);
		assertEquals(newPosition.getTranslationalVelocityX(), 0, 0.01);
		assertEquals(newPosition.getTranslationalVelocityY(), 0, 0.01);
	}

	@Test
	public void testApplyMovementTurnAndMoveFromStop() {
		Movement movement = new Movement();
		movement.setAngularAccleration(Math.PI / 60);
		movement.setTranslationalAcceleration(new Vector2D(10, 10));
		position.setOrientation(Math.PI / 4);

		double expectedOrientation = (Math.PI / 4) + (Math.PI / 60);
		double expectedX = 10;
		double expectedY = 10;
		
		Position newPosition = space.applyMovement(position, movement, timestep);
		
		assertEquals(newPosition.getX(), expectedX, 0.01);
		assertEquals(newPosition.getY(), expectedY, 0.01);
		assertEquals(newPosition.getOrientation(), expectedOrientation, 0.01);
		assertEquals(newPosition.getAngularVelocity(), Math.PI / 60, 0.01);
		assertEquals(newPosition.getTranslationalVelocityX(), 10, 0.01);
		assertEquals(newPosition.getTranslationalVelocityY(), 10, 0.01);
	}
	
	/**
	 * Tests points all around the current point
	 * 
	 * 
	 * (40, 40)			(60, 40)
	 * 
	 * 			(50,50)
	 * 
	 * (40, 60)			(60, 60)
	 * 
	 */
	@Test
	public void testfindShortestDistance() {
		Position pos2;
		
		position.setX(50);
		position.setY(50);
		
		// start with the same location so the vector should be 0,0
		pos2 = new Position(50,50);
		Vector2D shortestDist = space.findShortestDistanceVector(position, pos2);
		
		assertEquals(shortestDist.getMagnitude(), 0, 0.01);
		assertEquals(shortestDist.getXValue(), 0, 0.01);
		assertEquals(shortestDist.getYValue(), 0, 0.01);
		assertEquals(shortestDist.getAngle(), 0, 0.01);
		
		// start in the upper left corner (on the diagram in the comments)
		double expectedMagnitude = Math.sqrt(200);
		pos2.setX(40);
		pos2.setY(40);
		shortestDist =  space.findShortestDistanceVector(position, pos2);
		
		assertEquals(shortestDist.getMagnitude(), expectedMagnitude, 0.01);
		assertEquals(shortestDist.getXValue(), -10, 0.01);
		assertEquals(shortestDist.getYValue(), -10, 0.01);
		assertEquals(shortestDist.getAngle(), -(3 * Math.PI) / 4, 0.01);
		
		// upper right quadrant
		pos2.setX(60);
		pos2.setY(40);
		shortestDist =  space.findShortestDistanceVector(position, pos2);
		
		assertEquals(shortestDist.getMagnitude(), expectedMagnitude, 0.01);
		assertEquals(shortestDist.getXValue(), 10, 0.01);
		assertEquals(shortestDist.getYValue(), -10, 0.01);
		assertEquals(shortestDist.getAngle(), -(Math.PI / 4), 0.01);
		
		// lower right quadrant
		pos2.setX(60);
		pos2.setY(60);
		shortestDist =  space.findShortestDistanceVector(position, pos2);
		
		assertEquals(shortestDist.getMagnitude(), expectedMagnitude, 0.01);
		assertEquals(shortestDist.getXValue(), 10, 0.01);
		assertEquals(shortestDist.getYValue(), 10, 0.01);
		assertEquals(shortestDist.getAngle(), Math.PI / 4, 0.01);
		
		// lower left quadrant
		pos2.setX(40);
		pos2.setY(60);
		shortestDist =  space.findShortestDistanceVector(position, pos2);
		
		assertEquals(shortestDist.getMagnitude(), expectedMagnitude, 0.01);
		assertEquals(shortestDist.getXValue(), -10, 0.01);
		assertEquals(shortestDist.getYValue(), 10, 0.01);
		assertEquals(shortestDist.getAngle(), (3 * Math.PI) / 4, 0.01);

	}
	
	
	@Test
	public void testGetRandomFreeLocationInRegion() {
		int numTries = 1000;
		Random rand = new Random();
		double maxDistance = 100;
		int freeRadius = 10;
		Position center = new Position(0, 0);
		
		for (int i = 0; i < numTries; i++) {
			Position position = space.getRandomFreeLocationInRegion(rand, freeRadius, 0, 0, maxDistance);
			double dist = space.findShortestDistance(center, position);
			if (dist > maxDistance) {
				System.out.println(position);
			}
			assertTrue(dist <= maxDistance);
			
		}
	}

	@Test
	public void testToroidalWrap() {
		Position position = new Position(0,0);

		// try from the 0,0 position (which should not wrap)
		space.toroidalWrap(position);
		assertEquals(position.getX(), 0, 0);
		assertEquals(position.getY(), 0, 0);
		
		// now wrap from negative
		position.setX(-100);
		position.setY(-100);
		space.toroidalWrap(position);
		assertEquals(position.getX(), width - 100, 0);
		assertEquals(position.getY(), height - 100, 0);
		
		// wrap from the right
		position.setX(width + 100);
		position.setY(height / 2);
		space.toroidalWrap(position);
		assertEquals(position.getX(), 100, 0);
		assertEquals(position.getY(), height / 2, 0);
		
		// wrap over the top
		position.setX(width / 2);
		position.setY(height + 100);
		space.toroidalWrap(position);
		assertEquals(position.getX(), width / 2, 0);
		assertEquals(position.getY(), 100, 0);
	}
	
	/**
	 * Tests the distances around a square using known distances from a triangle.
	 * Ensures wrap-around works
	 * 
	 * -10, -10		0, -10		10, -10
	 * -10, 0		0, 0		10, 0
	 * -10, 10		0, 10		10, 10	
	 * 
	 * 
	 */
	@Test
	public void testFindShortestDistance() {
		Position center = new Position(0, 0);
		
		// do all the diagonals
		double expectedDist = Math.sqrt(200);
		Position other = new Position(-10, -10);
		double dist = space.findShortestDistance(center, other);
		assertEquals(dist, expectedDist, 0.01);

		other = new Position(10, -10);
		dist = space.findShortestDistance(center, other);
		assertEquals(dist, expectedDist, 0.01);
		
		other = new Position(10, 10);
		dist = space.findShortestDistance(center, other);
		assertEquals(dist, expectedDist, 0.01);

		other = new Position(-10, 10);
		dist = space.findShortestDistance(center, other);
		assertEquals(dist, expectedDist, 0.01);

		// now the square ones
		expectedDist = 10;
		other = new Position(0, -10);
		dist = space.findShortestDistance(center, other);
		assertEquals(dist, expectedDist, 0.01);

		other = new Position(10, 0);
		dist = space.findShortestDistance(center, other);
		assertEquals(dist, expectedDist, 0.01);
		
		other = new Position(0, 10);
		dist = space.findShortestDistance(center, other);
		assertEquals(dist, expectedDist, 0.01);

		other = new Position(-10, 0);
		dist = space.findShortestDistance(center, other);
		assertEquals(dist, expectedDist, 0.01);
		
		// and itself
		dist = space.findShortestDistance(center, center);
		assertEquals(dist, 0, 0.01);
	}
	

}
