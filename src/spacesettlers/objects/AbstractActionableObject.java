package spacesettlers.objects;

import java.util.LinkedHashSet;
import java.util.Set;

import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.utilities.Position;

/**
 * This class is the super class for all Space Settlers objects that can take actions (right now
 * that is ships and bases).  It contains the power ups for these objects.
 * 
 * @author amy
 *
 */
abstract public class AbstractActionableObject extends AbstractObject {
	/**
	 * Maximum number of bullets (or other weapons) simultaneously in the simulator.  
	 * This keeps ships from simply firing at every step 
	 */
	public static final int INITIAL_WEAPON_CAPACITY = 5;
	
	/** Maximum energy for any tags (if it goes above this we drop any tags) */
	public static final int TAG_MAX_ENERGY = 1500;

	/**
	 * Does the item have a shield and is it using it?
	 */
	boolean isShielded;
	
	/**
	 * The set of current power ups for this agent
	 */
	Set<SpaceSettlersPowerupEnum> currentPowerups;
	
    /**
     * Actionable objects all have energy (which power ups and moving depletes)
     */
    int energy, maxEnergy;
    
    /**
     * Actionable objects have a weapon capacity (which can be changed using power ups)
     */
    int weaponCapacity;
    
    /**
     * Number of steps left that this ship is frozen (not frozen if less than or equal to 0)
     */
    int freezeCount;
    
	/**
	 * The name of the team this ship belongs to
	 */
	String teamName;
	
	/**
	 * Hits and killsInflicted and damageInflicted for this object
	 */
	int hitsInflicted, killsInflicted, damageInflicted, damageReceived, killsReceived, assistsInflicted;

	/**
	 * Reference to the last two teams that shot this ship (keep one for kill and one for assist)
	 * These tags drop when the health goes back up and are only added if health is low enough.
	 * 
	 */
	Ship killTagTeam;
	Ship assistTagTeam;
	int healthAtKillTag;
	int healthAtAssistTag;


	/**
	 * Call the super constructor on objects
	 * @param mass
	 * @param radius
	 * @param position
	 */
	public AbstractActionableObject(int mass, int radius, Position position) {
		super(mass, radius, position);
		currentPowerups = new LinkedHashSet<SpaceSettlersPowerupEnum>();
		weaponCapacity = INITIAL_WEAPON_CAPACITY;
		hitsInflicted = killsInflicted = damageInflicted = damageReceived = killsReceived = assistsInflicted = 0;
	}

	/**
	 * Call the super constructor on objects
	 * @param mass
	 * @param radius
	 */
	public AbstractActionableObject(int mass, int radius) {
		super(mass, radius);
		currentPowerups = new LinkedHashSet<SpaceSettlersPowerupEnum>();
	}
	
	/**
	 * Get the current weapon capacity
	 * @return
	 */
	public int getWeaponCapacity() {
		return weaponCapacity;
	}

	/**
	 * Set the new one (called inside power ups)
	 * @param weaponCapacity
	 */
	public void setWeaponCapacity(int weaponCapacity) {
		this.weaponCapacity = weaponCapacity;
	}

	/**
	 * Is this a valid power up on this actionable object?
	 * 
	 * @param powerup
	 * @return
	 */
	public boolean isValidPowerup(SpaceSettlersPowerupEnum powerup) {
		if (currentPowerups.contains(powerup)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Add the power up to the current list of things this item can use
	 * @param powerup
	 */
	public void addPowerup(SpaceSettlersPowerupEnum powerup) {
		currentPowerups.add(powerup);
	}
	
	/**
	 * When an item dies, its power ups disappear
	 */
	public void resetPowerups() {
		currentPowerups.clear();
	}
	
	/**
	 * Remove the current power up from the set of allowed ones
	 * @param powerup
	 */
	public void removePowerup(SpaceSettlersPowerupEnum powerup) {
		currentPowerups.remove(powerup);
	}
	
	/**
	 * Is the object currently shielded
	 * @return
	 */
	public boolean isShielded() {
		return isShielded;
	}

	/**
	 * Set the shielding
	 * @param isShielded
	 */
	public void setShielded(boolean isShielded) {
		this.isShielded = isShielded;
	}

	/**
	 * Return the amount of energy
	 * @return
	 */
	public double getEnergy() {
		return energy;
	}
	
	/**
	 * Return the maximum energy this base can hold
	 * @return
	 */
	public int getMaxEnergy() {
		return maxEnergy;
	}
	
	/**
	 * Set the max energy (used by power ups)
	 * @param maxEnergy
	 */
	public void setMaxEnergy(int maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

	/**
	 * Update the energy of the ship.  If it falls below 0, mark the ship as dead
	 * 
	 * @param difference
	 */
	abstract public void updateEnergy(int difference);

	/**
	 * Get the number of steps it is frozen.  Not frozen
	 * if less than or equal to 0.
	 * @return
	 */
	public int getFreezeCount() {
		return freezeCount;
	}

	/**
	 * Set the freeze count 
	 * @param freezeCount
	 */
	public void setFreezeCount(int freezeCount) {
		this.freezeCount = freezeCount;
		if (freezeCount > 0) {
			super.isControllable = false;
		}
	}
	
	/**
	 * Decrement the freeze count (if needed)
	 * and set the ship back to controllable when it gets to 0
	 */
	public void decrementFreezeCount() {
		freezeCount--;
		if (freezeCount <= 0) {
			super.isControllable = true;
		}
	}

	/**
	 * @param teamName the teamName to set
	 */
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}


	/**
	 * @return the teamName
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * Get the hitsInflicted this object has made
	 * @return the hitsInflicted this object has made
	 */
	public int getHitsInflicted() {
		return hitsInflicted;
	}
	
	/**
	 * increment the hitsInflicted for this ship
	 */
	public void incrementHitsInflicted() {
		this.hitsInflicted++;
	}

	/**
	 * Get the killsInflicted this object has made
	 * @return the killsInflicted this object has made
	 */
	public int getKillsInflicted() {
		return killsInflicted;
	}
	
	/**
	 * Return the total assists this object did
	 * @return
	 */
	public int getTotalAssistsInflicted() {
		return assistsInflicted;
	}

	/**
	 * Get the killsReceived this object has made
	 * @return the killsReceived this object has made
	 */
	public int getKillsReceived() {
		return killsReceived;
	}

	/**
	 * increment the kill for this object
	 */
	public void incrementKillsInflicted() {
		this.killsInflicted++;
	}

	/**
	 * increment the assists for this object
	 */
	public void incrementAssistsInflicted() {
		this.assistsInflicted++;
	}

	/**
	 * increment the kill received for this object
	 */
	public void incrementKillsReceived() {
		this.killsReceived++;
	}

	/**
	 * Get the damageInflicted this object has made
	 * @return the damageInflicted this object has made
	 */
	public int getDamageInflicted() {
		return damageInflicted;
	}

	/**
	 * increment the damageReceived for this object
	 */
	public void incrementDamageReceived(int increment) {
		this.damageReceived += increment;
	}
	
	/**
	 * Get the damageReceived this object has made
	 * @return the damageReceived this object has gotten
	 */
	public int getDamageReceived() {
		return damageReceived;
	}

	/**
	 * increment the damageInflicted for this ship
	 */
	public void incrementDamageInflicted(int increment) {
		this.damageInflicted += increment;
	}
	

	/**
	 * Get the current set of powerups for the object
	 * 
	 * @return current powerups (as a set, there is no order)
	 */
	public Set<SpaceSettlersPowerupEnum> getCurrentPowerups() {
		return currentPowerups;
	}
	
	/**
	 * Tag the current shooter in case of kill 
	 * 
	 * @param firingShip
	 */
	public void tagShooter(Ship firingShip) {
		if (killTagTeam != null) {
			// if there was already a kill tag team, move it to assist
			assistTagTeam = killTagTeam;
			healthAtAssistTag = healthAtKillTag;
		}
		// current team that shot becomes the kill team
		killTagTeam = firingShip;
		healthAtKillTag = energy;
	}

	public Ship getKillTagTeam() {
		return killTagTeam;
	}

	public Ship getAssistTagTeam() {
		return assistTagTeam;
	}		
	
	public void updateTags() {
		if (this.getEnergy() > TAG_MAX_ENERGY) {
			this.killTagTeam = null;
			this.assistTagTeam = null;
		}
	}

}
