package spacesettlers.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spacesettlers.utilities.Vector2D;

public class TestVector2D {
	Vector2D oneTwo, twoTwo;
	
	@Before
	public void setUp() throws Exception {
		oneTwo = new Vector2D(1, 2);
		twoTwo = new Vector2D(2,2);
	}

	@After
	public void tearDown() throws Exception {
		oneTwo = null;
		twoTwo = null;
	}

	/**
	 * Ensure cloning makes a new object (so == fails) but the values
	 * are the same.
	 */
	@Test
	public void testClone() {
		Vector2D newOneTwo, newTwoTwo;
		
		newOneTwo = (Vector2D) oneTwo.clone();
		newTwoTwo = (Vector2D) twoTwo.clone();
		
		assertFalse(oneTwo == newOneTwo);
		assertFalse(twoTwo == newTwoTwo);
		
		assertTrue(oneTwo.equals(newOneTwo));
		assertTrue(twoTwo.equals(newTwoTwo));
	}

	/**
	 * Ensure the random vector never has a magnitude greater than the specified one
	 */
	@Test
	public void testgetRandom() {
		Vector2D newVec;
		Random rand = new Random();
		float maxMagnitude = 100f;
		
		for (int i = 0; i < 100; i++) {
			newVec = Vector2D.getRandom(rand, maxMagnitude);
		    assertTrue(newVec.getMagnitude() <= maxMagnitude);
		}
	}

	/**
	 * Tests the angle returned by get angle for a variety of vectors
	 * 
	 * (-10, -10, -3pi/4)	(0, -10, -pi/2)		(10, -10, -pi/4)
	 * 
	 * 						(0, 0, 0)			(10, 0, 0)
	 * 
	 * (-10, 10, 3pi/4)		(0, 10, pi/2)		(10, 10, pi/4)
	 */
	@Test
	public void testGetAngle() {
		// the center
		Vector2D vector = new Vector2D(0,0);
		assertEquals(vector.getAngle(), 0, 0.01);
		
		// now going around clockwise from the upper left corner
		vector = new Vector2D(-10, -10);
		assertEquals(vector.getAngle(), -(3 * Math.PI) / 4, 0.01);
		
		vector = new Vector2D(0, -10);
		assertEquals(vector.getAngle(), -Math.PI / 2, 0.01);

		vector = new Vector2D(10, -10);
		assertEquals(vector.getAngle(), -Math.PI / 4, 0.01);

		vector = new Vector2D(10, 0);
		assertEquals(vector.getAngle(), 0, 0.01);

		vector = new Vector2D(10, 10);
		assertEquals(vector.getAngle(), Math.PI / 4, 0.01);

		vector = new Vector2D(0, 10);
		assertEquals(vector.getAngle(), Math.PI / 2, 0.01);

		vector = new Vector2D(-10, 10);
		assertEquals(vector.getAngle(), (3 * Math.PI) / 4, 0.01);
	}

	@Test
	public void testVectorProjectAlongYLine() {
		Vector2D vector1 = new Vector2D(0, 10);
		Vector2D vector2 = new Vector2D(0, 10);
		
		Vector2D resultVector = vector1.vectorProject(vector2);
		assertEquals(resultVector.getXValue(), vector1.getXValue(), 0);
		assertEquals(resultVector.getYValue(), vector1.getYValue(), 0);
	}

	@Test
	public void testVectorProjectAlongXLine() {
		Vector2D vector1 = new Vector2D(10, 0);
		Vector2D vector2 = new Vector2D(10, 0);
		
		Vector2D resultVector = vector1.vectorProject(vector2);
		assertEquals(resultVector.getXValue(), vector1.getXValue(), 0);
		assertEquals(resultVector.getYValue(), vector1.getYValue(), 0);
	}

	@Test
	public void testVectorRejectAlongYLine() {
		Vector2D vector1 = new Vector2D(0, 10);
		Vector2D vector2 = new Vector2D(0, 10);
		
		Vector2D resultVector = vector1.vectorRejection(vector2);
		assertEquals(resultVector.getXValue(), 0, 0);
		assertEquals(resultVector.getYValue(), 0, 0);
	}

	@Test
	public void testVectorRejectAlongXLine() {
		Vector2D vector1 = new Vector2D(10, 0);
		Vector2D vector2 = new Vector2D(10, 0);
		
		Vector2D resultVector = vector1.vectorRejection(vector2);
		assertEquals(resultVector.getXValue(), 0, 0);
		assertEquals(resultVector.getYValue(), 0, 0);
	}

	@Test
	public void testVectorProjectRejectDiagonal() {
		Vector2D vector1 = new Vector2D(10, 10);
		Vector2D vector2 = new Vector2D(0, 10);
		
		Vector2D resultVector = vector1.vectorProject(vector2);
		assertEquals(resultVector.getXValue(), 0, 0);
		assertEquals(resultVector.getYValue(), 10, 0);
		
		Vector2D resultVector2 = vector1.vectorRejection(vector2);
		assertEquals(resultVector2.getXValue(), 10, 0);
		assertEquals(resultVector2.getYValue(), 0, 0);
	}

	@Test
	public void testDot() {
		Vector2D vector1 = new Vector2D(-3, 5);
		Vector2D vector2 = new Vector2D(2, 3);
		
		double dot = vector1.dot(vector2);
		assertEquals(dot, 9, 0);
	}

	@Test
	public void testGetMagnitude() {
		Vector2D vector1 = new Vector2D(3, -3);
		Vector2D vector2 = new Vector2D(4, 4);
		
		assertEquals(vector1.getMagnitude(), Math.sqrt(18), 0);
		assertEquals(vector2.getMagnitude(), Math.sqrt(32), 0);
	}


	
//	@Test
//	public void testFromAngle() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAngleBetween() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testUnit() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testNegate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAdd() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSubtract() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testMultiply() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDivide() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testCross() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testFastRotate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSubtractAndRotate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testRotate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testEqualsVector2D() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testMagnitudeCompareTo() {
//		fail("Not yet implemented");
//	}

}
