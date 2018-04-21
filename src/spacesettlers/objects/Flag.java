package spacesettlers.objects;

import java.awt.Color;
import java.util.Random;

import spacesettlers.clients.Team;
import spacesettlers.graphics.BaseGraphics;
import spacesettlers.graphics.FlagGraphics;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;

/**
 * Flag object - used for capture the flag.  Teams can:
 * 1) touch their own flag and cause it to move to a different starting location in its list
 * 2) pick up the flag of the other team and put it in their ship.
 * 
 * If a flag is being carried inside a ship that dies, the flag drops where the ship died.  
 * If the flag is carried to the other team's base, it restarts back at one of its starting locations.
 * 
 * @author amy
 */
public class Flag extends AbstractObject {
    public static final int FLAG_RADIUS = 10;
    public static final int FLAG_MASS = 100;

    /**
	 * The name of the team that this flag belongs to 
	 */
	String teamName;
	
	/**
	 * The color of this team
	 */
	Color teamColor;
	
	/**
	 * The team that owns this base
	 */
	Team team;
	
	/**
	 * The list of starting locations for the flag
	 */
	Position[] startingLocations;

	/**
	 * Is the flag being carried?  Changes its graphics
	 */
	boolean beingCarried;
	
	/**
	 * Reference to the ship/drone carrying the flag (if it is being carried)
	 */
	AbstractObject carryingShipOrDrone;
	
	/**
	 * Create a new flag
	 * 
	 * @param position
	 * @param teamName
	 * @param team
	 * @param startingLocations
	 */
	public Flag(Position position, String teamName, Team team, Position[] startingLocations) {
		super(FLAG_MASS, FLAG_RADIUS, position);
		this.teamName = teamName;
		this.team = team;
		teamColor = team.getTeamColor();
		graphic = new FlagGraphics(this, teamColor);
		setAlive(true);
		setDrawable(true);
		this.isMoveable = true;
		// copy the locations to avoid exploits
		this.startingLocations = new Position[startingLocations.length];
		for (int i = 0; i < startingLocations.length; i++) { 
			this.startingLocations[i] = startingLocations[i].deepCopy();
		}
		this.beingCarried = false;
		this.carryingShipOrDrone = null;
	}

	@Override
	/**
	 * Make a deep copy of the current flag's state
	 */
	public Flag deepClone() {
		Flag newFlag = new Flag(this.position.deepCopy(), this.teamName, team, this.startingLocations);
		newFlag.setAlive(isAlive);
		newFlag.setDrawable(isDrawable);
		newFlag.beingCarried = this.beingCarried;
		if (newFlag.carryingShipOrDrone != null) {
			newFlag.carryingShipOrDrone = this.carryingShipOrDrone.deepClone();
		}
		newFlag.id = id;
		return newFlag;
	}

	/**
	 * Return the team that this flag belongs to
	 * 
	 * @return
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * Is the flag being carried?
	 * 
	 * @return
	 */
	public boolean isBeingCarried() {
		return beingCarried;
	}
	
	/**
	 * Set the flag to being carried by this drone
	 * Doing so removes it from any ship that may be carrying it.
	 * 
	 * @param ship
	 */
	public void pickupFlag(Drone drone) {
		this.beingCarried = true;
		this.carryingShipOrDrone = drone;
		setPosition(drone.getPosition().deepCopy());
		this.setDrawable(false);
		this.setRespawn(false);
		this.setAlive(false);
		
	}
	
	/**
	 * Set the flag to being carried by this ship
	 * 
	 * @param ship
	 */
	public void pickupFlag(Ship ship) {
		this.beingCarried = true;
		this.carryingShipOrDrone = ship;
		setPosition(ship.getPosition().deepCopy());
		this.setDrawable(false);
		this.setRespawn(false);
		this.setAlive(false);
		
	}
	
	/**
	 * Drop the flag (likely the ship died)
	 */
	public void dropFlag(Random rand, Toroidal2DPhysics space) {
		//System.out.println("Flag being dropped at " + carryingShip.getPosition());
		this.beingCarried = false;
		this.setDrawable(true);
		this.setAlive(true);
		this.setRespawn(false);
		
		Position newPosition = space.getRandomFreeLocationInRegion(rand, this.getRadius(), 
				(int) carryingShipOrDrone.getPosition().getX(), 
				(int) carryingShipOrDrone.getPosition().getY(), 200);
		newPosition.setAngularVelocity(carryingShipOrDrone.getPosition().getAngularVelocity());
		newPosition.setTranslationalVelocity(carryingShipOrDrone.getPosition().getTranslationalVelocity());
		this.setPosition(newPosition);
		this.carryingShipOrDrone = null;
	}

	/**
	 * Deposit a flag at the base.  For now, that kills a flag
	 * and sets it to not being carried.  It will regenerate on the next timestep.
	 */
	public void depositFlag() {
		this.beingCarried = false;
		this.setAlive(false);
		this.setDrawable(false);
		this.setRespawn(true);
	}
	
	/**
	 * Get a new random Starting location
	 * @param random
	 * @return
	 */
	public Position getNewStartingPosition(Random random) {
		return startingLocations[random.nextInt(startingLocations.length)].deepCopy();
	}

	/**
	 * Get the starting locations for the flag
	 * 
	 * @return
	 */
	public Position[] getStartingLocations() {
		return startingLocations;
	}
	
	
	

}
