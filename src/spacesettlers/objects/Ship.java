package spacesettlers.objects;

import java.awt.Color;
import java.util.LinkedHashSet;

import spacesettlers.actions.AbstractAction;
import spacesettlers.graphics.ShipGraphics;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.objects.weapons.AbstractWeapon;
import spacesettlers.objects.weapons.EMP;
import spacesettlers.objects.weapons.Missile;
import spacesettlers.utilities.Position;

/**
 * A spacewar ship is the main object in the simulator as it is the one the agents/clients control.
 * 
 * @author amy
 */
public class Ship extends AbstractActionableObject {
	public static final int SHIP_RADIUS = 15;
	public static final int SHIP_MASS = 200;
	public static final int SHIP_INITIAL_ENERGY = 5000;
	public static final int RESPAWN_INCREMENT = 10;
	public static final int MAX_RESPAWN_INTERVAL = 500;
	public static final int SHIP_MAX_ENERGY = 5000;

	/**
	 * The action the ship is currently executing
	 */
	AbstractAction currentAction;

	/**
	 * Time checks left until the ship can respawn
	 */
	int respawnCounter;

	/**
	 * Number of beacons picked up by this ship
	 */
	int numBeacons;

	/**
	 * Last time the respawn counter was used
	 */
	int lastRespawnCounter;

	/**
	 * The color for this team
	 */
	Color teamColor;

	/**
	 * Number of weapons in the air (this is reset by the sim
	 * when bullets explode or reach their target)
	 */
	int numWeaponsInAir;
	
	/**
	 * Does the ship currently have a flag?
	 */
	boolean carryingFlag;
	
	/**
	 * Reference to the flag the ship has (if it has one)
	 */
	Flag flag;


	/**
	 * Make a new ship for the specified team.  
	 * @param teamName
	 */
	public Ship(String teamName, Color teamColor, Position location) {
		super(SHIP_MASS, SHIP_RADIUS, location);
		setDrawable(true);
		setAlive(true);
		this.teamName = teamName;
		graphic = new ShipGraphics(this, teamColor);
		this.isControllable = true;
		this.isMoveable = true;
		respawnCounter = 0;
		numBeacons = 0;
		energy = SHIP_INITIAL_ENERGY;
		lastRespawnCounter = 0;
		resources = new ResourcePile();
		this.teamColor = teamColor;
		numWeaponsInAir = 0;
		maxEnergy = SHIP_MAX_ENERGY;
		currentPowerups.add(SpaceSettlersPowerupEnum.FIRE_MISSILE);
		this.carryingFlag = false;
		this.flag = null;
		this.numFlags = 0;
		this.numCores = 0;
	}

	/**
	 * Deep copy of a ship (used for security)
	 * @return
	 */
	public Ship deepClone() {
		Ship newShip = new Ship(teamName, teamColor, getPosition().deepCopy());

		newShip.setAlive(isAlive);
		newShip.resources = new ResourcePile();
		newShip.addResources(resources);
		newShip.lastRespawnCounter = lastRespawnCounter;
		newShip.numBeacons = numBeacons;
		newShip.energy = energy;
		newShip.respawnCounter = respawnCounter;
		newShip.graphic = new ShipGraphics(newShip, teamColor);
		newShip.currentAction = currentAction;
		newShip.numWeaponsInAir = numWeaponsInAir;
		newShip.id = id;
		newShip.maxEnergy = maxEnergy;
		newShip.currentPowerups = new LinkedHashSet<SpaceSettlersPowerupEnum>(currentPowerups);
		newShip.weaponCapacity = weaponCapacity;
		newShip.hitsInflicted = hitsInflicted;
		newShip.killsInflicted = killsInflicted;
		newShip.killsReceived = killsReceived;
		newShip.damageInflicted = damageInflicted;
		newShip.carryingFlag = carryingFlag;
		newShip.numFlags = numFlags;
		newShip.isShielded = isShielded;
		newShip.numCores = numCores; 
		
		if (this.flag != null){
			newShip.flag = flag.deepClone();
		}
		return newShip;
	}


	/**
	 * Resets the ship energy to the initial level
	 */
	public void resetEnergy() {
		energy = SHIP_INITIAL_ENERGY;
	}


	/**
	 * Is the ship carrying a flag right now?  True if so.  False otherwise.
	 * 
	 * @return
	 */
	public boolean isCarryingFlag() {
		return carryingFlag;
	}
	
	/**
	 * Get the flag (this is a deep clone from the client point of view) of the flag the ship is carrying
	 * @return
	 */
	public Flag getFlag() {
		return flag;
	}

	/**
	 * Add the flag to the ship's inventory
	 * 
	 * @param flag
	 */
	public void addFlag(Flag flag) {
		this.flag = flag;
		this.carryingFlag = true;
		this.incrementFlags();
		//System.out.println("Ship " + this + " has a flag now");
	}
	
	/**
	 * Get the color of this team
	 * @return
	 */
	public Color getTeamColor() {
		return teamColor;
	}

	/**
	 * Returns a new weapon of the requested type if the ship is allowed to
	 * fire a new one.  Otherwise returns null.
	 * @param weaponType weapon type to fire
	 * 
	 * @return a valid weapon or null if the ship is out of weapons at the moment
	 */
	public AbstractWeapon getNewWeapon(SpaceSettlersPowerupEnum weaponType) {
		if (numWeaponsInAir < weaponCapacity) {
			Position weaponPosition = getPosition().deepCopy();
			if (weaponType == SpaceSettlersPowerupEnum.FIRE_MISSILE) {
				return new Missile(weaponPosition, this);
			} else if (weaponType == SpaceSettlersPowerupEnum.FIRE_EMP) {
				return new EMP(weaponPosition, this);
			}
		} 
		return null;
	}

	/**
	 * Increment the weapons in play
	 */
	public void incrementWeaponCount(){
		numWeaponsInAir++;
	}

	/**
	 * Decrease the weapon count by one
	 */
	public void decrementWeaponCount() {
		numWeaponsInAir--;
	}



	/**
	 * Ships need to behave slightly differently when set to dead or respawned 
	 * so this is an override of the abstract class
	 */
	public void setAlive(boolean value) {
		if (value == false) {
			respawnCounter = Math.min(lastRespawnCounter + RESPAWN_INCREMENT, MAX_RESPAWN_INTERVAL);
			lastRespawnCounter = respawnCounter; 
			resetResources();
			resetPowerups();
			if (carryingFlag) {
				dropFlag();
			}
			resetAiCores(); 
			
			
		} else {
			resetEnergy();
		}

		super.setAlive(value);
	}
	
	/**
	 * Drop all the cores by resetting to 0
	 * Will also need code inside physics sim to drop all AiCores
	 */
	public void resetAiCores() {
		//Just erase the core count, as we are not currently tracking the specific cores held by a ship.
		numCores = 0;
		/*
		 * From Josiah: In the future the ship may drop any AiCores it is holding, however I don't currently
		 * have a good strategy for releasing the cores of a ship holding a very large number of cores.
		 * For example, if a ship is holding 8 cores and dies, spawning those cores leads to a bunch of
		 * interesting collisions and several of the cores tend to end up destroyed.
		 * Perhaps limit a ship to carrying a certain number of cores?
		 */
	}
	
	/**
	 * A ship has collided with a core and collected it.
	 */
	public void incrementCores() {
		super.incrementCores(1);//herr0861 edit
	}
	
	/**
	 * A ship drops the flag when it dies
	 */
	private void dropFlag() {
		carryingFlag = false;
		flag.dropFlag();
		flag = null;
	}
	

	/**
	 * When an item dies, its power ups disappear
	 */
	public void resetPowerups() {
		super.resetPowerups();
		currentPowerups.add(SpaceSettlersPowerupEnum.FIRE_MISSILE);
	}


	/**
	 * Return the number of beacons picked up by this ship
	 * @return
	 */
	public int getNumBeacons() {
		return numBeacons;
	}

	/**
	 * Increment the number of beacons for this ship
	 */
	public void incrementBeaconCount() {
		numBeacons++;
	}

	/**
	 * Get the action the ship is currently executing
	 * @return
	 */
	public AbstractAction getCurrentAction() {
		return currentAction;
	}

	/**
	 * Set the action the ship is currently executing
	 * Note, this should only be done within the simulator and not
	 * within the team client (where it will be ignored)
	 * 
	 * @param currentAction
	 */
	public void setCurrentAction(AbstractAction currentAction) {
		this.currentAction = currentAction;
	}

	public String toString() {
		String str = "Ship id " + id + " team " + teamName + " at " + position + " resources " + resources + 
				" flags: " + numFlags;
		return str;
	}

	/**
	 * Ships have to wait a certain amount of time until they can respawn
	 */
	public boolean canRespawn() {
		respawnCounter--;

		if (respawnCounter <= 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Ship has to call its own set alive when it updates its energy
	 */
	@Override
	public void updateEnergy(int difference) {
		energy += difference;

		if (energy > maxEnergy) {
			energy = maxEnergy;
		}
	}

	/**
	 * Deposit the flag by setting the ship to not carry it
	 */
	public void depositFlag() {
		this.carryingFlag = false;
		flag.depositFlag();
		flag = null;
	}		

}
