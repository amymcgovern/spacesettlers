package spacesettlers.simulator;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Ship;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.objects.resources.ResourceTypes;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

public class TestCollisionHandler {
	CollisionHandler collisionHandler;
	Toroidal2DPhysics space;
	double timestep = 0.05;

	@Before
	public void setUp() throws Exception {
		collisionHandler = new CollisionHandler();
		space = new Toroidal2DPhysics(480, 640, timestep);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * This test comes from the example on wikipedia
	 * 
	 * http://en.wikipedia.org/wiki/Elastic_collision
	 */
	@Test
	public void test1DElasticCollisions() {
		int m1 = 3;
		int m2 = 5;

		int u1 = 4;
		int u2 = -6;
		
		CollisionHandler.CollisionData results = collisionHandler.elasticCollision1D(u1, m1, u2, m2);
		
		assertEquals(results.v1, -8.5, 0.01);
		assertEquals(results.v2, 1.5, 0.01);
		
	}
	
	@Test
	public void testElasticCollisionsShipsY() {
		Ship ship1, ship2;
		
		Position ship1Pos = new Position(0, 0, Math.PI / 4);
		ship1Pos.setTranslationalVelocity(new Vector2D(0, 20));
		ship1 = new Ship("team1", Color.BLUE, ship1Pos);

		Position ship2Pos = new Position(0, 10, -Math.PI/4);
		ship2Pos.setTranslationalVelocity(new Vector2D(0, -10));
		ship2 = new Ship("team2", Color.RED, ship2Pos);
		
		collisionHandler.collide(ship1, ship2, space);
		
		assertEquals(ship1.getPosition().getTranslationalVelocityX(), 0, 0.01);
		assertEquals(ship1.getPosition().getTranslationalVelocityY(), -10, 0.01);
		assertEquals(ship2.getPosition().getTranslationalVelocityX(), 0, 0.01);
		assertEquals(ship2.getPosition().getTranslationalVelocityY(), 20, 0.01);
		
	}

	@Test
	public void testElasticCollisionsShipsX() {
		Ship ship1, ship2;
		
		Position ship1Pos = new Position(0, 0, Math.PI / 4);
		ship1Pos.setTranslationalVelocity(new Vector2D(20, 0));
		ship1 = new Ship("team1", Color.BLUE, ship1Pos);

		Position ship2Pos = new Position(10, 0, -Math.PI/4);
		ship2Pos.setTranslationalVelocity(new Vector2D(-10, 0));
		ship2 = new Ship("team2", Color.RED, ship2Pos);
		
		collisionHandler.collide(ship1, ship2, space);
		
		assertEquals(ship1.getPosition().getTranslationalVelocityX(), -10, 0.01);
		assertEquals(ship1.getPosition().getTranslationalVelocityY(), 0, 0.01);
		assertEquals(ship2.getPosition().getTranslationalVelocityX(), 20, 0.01);
		assertEquals(ship2.getPosition().getTranslationalVelocityY(), 0, 0.01);
		
	}

	@Test
	public void testElasticCollisionsShipsDiagonal() {
		Ship ship1, ship2;
		
		Position ship1Pos = new Position(0, 0, Math.PI / 4);
		ship1Pos.setTranslationalVelocity(new Vector2D(2, 2));
		ship1 = new Ship("team1", Color.BLUE, ship1Pos);

		Position ship2Pos = new Position(10, 10, -Math.PI/4);
		ship2Pos.setTranslationalVelocity(new Vector2D(-10, -10));
		ship2 = new Ship("team2", Color.RED, ship2Pos);
		
		collisionHandler.collide(ship1, ship2, space);
		
		assertEquals(ship1.getPosition().getTranslationalVelocityX(), -10, 0.01);
		assertEquals(ship1.getPosition().getTranslationalVelocityY(), -10, 0.01);
		assertEquals(ship2.getPosition().getTranslationalVelocityX(), 2, 0.01);
		assertEquals(ship2.getPosition().getTranslationalVelocityY(), 2, 0.01);
		
	}
	
	@Test
	public void testElasticCollisionsShipToMoveableAsteroidY() {
		Ship ship1;
		Asteroid asteroid;
		
		Position ship1Pos = new Position(0, 0, Math.PI / 4);
		ship1Pos.setTranslationalVelocity(new Vector2D(0, 20));
		ship1 = new Ship("team1", Color.BLUE, ship1Pos);

		Position asteroid2Pos = new Position(0, 10, -Math.PI/4);
		asteroid2Pos.setTranslationalVelocity(new Vector2D(0, -10));
		asteroid = new Asteroid(asteroid2Pos, false, 10, true, .33, .33, .34);
		ship1.setMass(asteroid.getMass());
		
		collisionHandler.collide(ship1, asteroid, space);
		
		assertEquals(ship1.getPosition().getTranslationalVelocityX(), 0, 0.01);
		assertEquals(ship1.getPosition().getTranslationalVelocityY(), -10.0, 0.01);
		assertEquals(asteroid.getPosition().getTranslationalVelocityX(), 0, 0.01);
		assertEquals(asteroid.getPosition().getTranslationalVelocityY(), 20, 0.01);
		
	}

	@Test
	public void testElasticCollisionsShipToAsteroidX() {
		Ship ship1;
		Asteroid asteroid;

		Position ship1Pos = new Position(0, 0, Math.PI / 4);
		ship1Pos.setTranslationalVelocity(new Vector2D(20, 0));
		ship1 = new Ship("team1", Color.BLUE, ship1Pos);

		Position asteroid2Pos = new Position(10, 0, -Math.PI/4);
		asteroid2Pos.setTranslationalVelocity(new Vector2D(-10, 0));
		asteroid = new Asteroid(asteroid2Pos, false, 10, true, .33, .33, .34);
		ship1.setMass(asteroid.getMass());
		
		collisionHandler.collide(ship1, asteroid, space);
		
		assertEquals(ship1.getPosition().getTranslationalVelocityX(), -10, 0.01);
		assertEquals(ship1.getPosition().getTranslationalVelocityY(), 0, 0.01); 
		assertEquals(asteroid.getPosition().getTranslationalVelocityX(), 20, 0.01);
		assertEquals(asteroid.getPosition().getTranslationalVelocityY(), 0, 0.01);
		
	}


	@Test
	public void testElasticCollisionsAsteroidsMoveableToMoveableOnX() {
		Asteroid asteroid1, asteroid2;
		
		Position position1 = new Position(0, 0, Math.PI / 4);
		position1.setTranslationalVelocity(new Vector2D(20, 0));
		asteroid1 = new Asteroid(position1, false, 10, true, .33, .33, .34);

		Position position2 = new Position(10, 0, -Math.PI/4);
		position2.setTranslationalVelocity(new Vector2D(-10, 0));
		asteroid2 = new Asteroid(position2, false, 10, true, .33, .33, .34);
		
		collisionHandler.collide(asteroid1, asteroid2, space);
		
		assertEquals(asteroid1.getPosition().getTranslationalVelocityX(), -10, 0.01);
		assertEquals(asteroid1.getPosition().getTranslationalVelocityY(), 0, 0.01);
		assertEquals(asteroid2.getPosition().getTranslationalVelocityX(), 20, 0.01);
		assertEquals(asteroid2.getPosition().getTranslationalVelocityY(), 0, 0.01);
		
	}

	@Test
	public void testElasticCollisionsAsteroidsMoveableToNonMoveableOnX() {
		Asteroid asteroid1, asteroid2;
		
		Position position1 = new Position(0, 0, Math.PI / 4);
		position1.setTranslationalVelocity(new Vector2D(20, 0));
		asteroid1 = new Asteroid(position1, false, 10, true, .33, .33, .34);

		Position position2 = new Position(10, 0, -Math.PI/4);
		asteroid2 = new Asteroid(position2, false, 10, false, .33, .33, .34);
		
		collisionHandler.collide(asteroid1, asteroid2, space);
		
		assertEquals(asteroid1.getPosition().getTranslationalVelocityX(), -20, 0.01);
		assertEquals(asteroid1.getPosition().getTranslationalVelocityY(), 0, 0.01);
		assertEquals(asteroid2.getPosition().getTranslationalVelocityX(), 0, 0.01);
		assertEquals(asteroid2.getPosition().getTranslationalVelocityY(), 0, 0.01);
		
	}
	
	@Test
	public void testCollidingWithMineableAsteroid() {
		Ship ship1;
		Asteroid asteroid;

		Position ship1Pos = new Position(0, 0, Math.PI / 4);
		ship1Pos.setTranslationalVelocity(new Vector2D(20, 0));
		ship1 = new Ship("team1", Color.BLUE, ship1Pos);

		Position asteroid2Pos = new Position(10, 0, -Math.PI/4);
		asteroid2Pos.setTranslationalVelocity(new Vector2D(-10, 0));
		asteroid = new Asteroid(asteroid2Pos, true, 10, true, .33, .33, .34);
		
		ResourcePile asteroidResources = asteroid.getResources();
		
		collisionHandler.collide(ship1, asteroid, space);
		
		assertEquals(ship1.getResources().getResourceQuantity(ResourceTypes.FUEL), 
				asteroidResources.getResourceQuantity(ResourceTypes.FUEL), 0.0);
		assertEquals(ship1.getResources().getResourceQuantity(ResourceTypes.WATER), 
				asteroidResources.getResourceQuantity(ResourceTypes.WATER), 0.0);
		assertEquals(ship1.getResources().getResourceQuantity(ResourceTypes.METALS), 
				asteroidResources.getResourceQuantity(ResourceTypes.METALS), 0.0);
		
	}

	@Test
	public void testCollidingInsideRadius() {
		Ship ship;
		Asteroid asteroid;

		Position ship1Pos = new Position(100, 0, Math.PI / 4);
		ship1Pos.setTranslationalVelocity(new Vector2D(20, 0));
		ship = new Ship("team1", Color.BLUE, ship1Pos);

		Position asteroid2Pos = new Position(134, 0, -Math.PI/4);
		asteroid2Pos.setTranslationalVelocity(new Vector2D(-10, 0));
		asteroid = new Asteroid(asteroid2Pos, false, 20, true, .33, .33, .34);
		ship.setMass(asteroid.getMass());

		// ship radius is 15 and is at 100,0
		// asteroid radius is 20 here and at 134,0
		// ship should bounce back to -99.33,0 (edge of collision) and asteroid to 134.33 (edge of collision, based on velocities) 
		
		collisionHandler.collide(ship, asteroid, space);
		
		assertEquals(ship.getPosition().getTranslationalVelocityX(), -10, 0.01);
		assertEquals(ship.getPosition().getTranslationalVelocityY(), 0, 0.01);
		assertEquals(asteroid.getPosition().getTranslationalVelocityX(), 20, 0.01);
		assertEquals(asteroid.getPosition().getTranslationalVelocityY(), 0, 0.01);
		assertEquals(ship.getPosition().getX(), 99.33, 0.01);
		assertEquals(ship.getPosition().getY(), 0, 0.01);
		assertEquals(asteroid.getPosition().getX(), 134.33, 0.01);
		assertEquals(asteroid.getPosition().getY(), 0, 0.01);
		
	}

	
}
