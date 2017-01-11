package spacesettlers.objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import spacesettlers.clients.Team;
import spacesettlers.graphics.BaseGraphics;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.objects.resources.ResourceTypes;
import spacesettlers.utilities.Position;

/**
 * A base for a team
 * @author amy
 *
 */
public class Base extends AbstractActionableObject {
    public static final int BASE_RADIUS = 10;
    public static final int BASE_MASS = 1000;
    public static final int INITIAL_BASE_ENERGY = 5000;
    public static final int INITIAL_ENERGY_HEALING_INCREMENT = 1;
    
	/**
	 * The color of this team
	 */
	Color teamColor;
	
	/**
	 * The team that owns this base
	 */
	Team team;
	
	/**
	 * true if this is a home base for a team (which therefore can't be killed) or false if it is a supplementary
	 * base.
	 */
	boolean isHomeBase;
	
	/**
	 * How many units of energy heal at each time step (can be changed with power ups)
	 */
	int healingIncrement;
	
	
	public Base(Position location, String teamName, Team team, boolean isHomeBase) {
		super(BASE_MASS, BASE_RADIUS, location);
		this.teamName = teamName;
		teamColor = team.getTeamColor();
		graphic = new BaseGraphics(this, teamColor);
		energy = INITIAL_BASE_ENERGY;
		setAlive(true);
		setDrawable(true);
		this.isMoveable = false;
		this.team = team;
		this.isHomeBase = isHomeBase;
		this.maxEnergy = INITIAL_BASE_ENERGY;
		healingIncrement = INITIAL_ENERGY_HEALING_INCREMENT;
		resources = new ResourcePile();
	}

	/**
	 * Makes a deep copy (for security)
	 */
	public Base deepClone() {
		Base newBase = new Base(getPosition().deepCopy(), teamName, team.deepCopy(), isHomeBase);
		
		newBase.energy = energy;
		newBase.setAlive(isAlive);
		newBase.id = id;
		newBase.maxEnergy = maxEnergy;
		newBase.currentPowerups = new HashSet<SpaceSettlersPowerupEnum>(currentPowerups);
		newBase.weaponCapacity = weaponCapacity;
		newBase.healingIncrement = healingIncrement;
		newBase.resources = new ResourcePile();
		newBase.resources.add(resources);
		return newBase;
	}

	/**
	 * Get the team
	 * @return
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * Return half of the base's energy
	 * @return the healingEnergy
	 */
	public int getHealingEnergy() {
		return (energy / 2);
	}
	
	/**
	 * Returns true if this is a home base for a team and false if it is a secondary base
	 * @return
	 */
	public boolean isHomeBase() {
		return isHomeBase;
	}
	
	/**
	 * Get the speed at which this base heals (per time step)
	 * @return
	 */
	public int getHealingIncrement() {
		return healingIncrement;
	}

	/**
	 * Set the speed at which this base heals (done in the power up)
	 * @param healingIncrement
	 */
	public void setHealingIncrement(int healingIncrement) {
		this.healingIncrement = healingIncrement;
	}

	/**
	 * Change the resourcesAvailable amount by the specified amount.  Resources live
	 * at a base but are available to the whole team once they arrive at a base.  
	 * Method is overridden to add things to the team also.
	 * 
	 * @param difference
	 */
	public void addResources(ResourcePile newResources) {
		super.addResources(newResources);
		
		// and increment the resources for the entire team
		team.incrementTotalResources(newResources);
		team.incrementAvailableResources(newResources);
	}

	/**
	 * Change the healing energy (from a collision or just time healing it)
	 * @param difference
	 */
	public void updateEnergy(int difference) {
		energy += difference;
		
		if (energy < 0) {
			energy = 0;
			if (!isHomeBase) {
				setAlive(false);
			}
		}
		
		if (energy > maxEnergy) {
			energy = maxEnergy;
		}
	}

	public String toString() {
		String str = "Base id " + super.id + " team " + super.teamName + " at " + position + " resources " + resources;
		return str;
	}

}
