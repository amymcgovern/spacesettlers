package spacesettlers.objects;

import java.awt.Color;

import spacesettlers.graphics.CoreGraphics;
import spacesettlers.utilities.Position;

/**
 * An AiCore is an object in the SpaceSettlers simulator that spawns when a ship dies.
 * The Ai Core takes damage by bouncing off objects and is restored when it touches an energy beacon.
 * It is collected when it collides with a base or a ship.
 * 
 * This uses elements of code from the SpaceSettlers simulator.
 * 
 * @author Josiah
 * @author amy
 *
 */
public class AiCore extends AbstractObject{
	
	/**
	 * The radius of an AiCore
	 */
	public static final int CORE_RADIUS = 8;
	
	public static final int CORE_MASS = 25;
	
	public static final int CORE_MAX_ENERGY = 500;

	/**
	 * The color of the team that owns this AiCore
	 */
	Color teamColor;
	
	/**
	 * The name of the team who owns this AiCore
	 */
	String teamName;
	
	/**
	 * The energy of the core
	 */
	int energy;

	
	public AiCore(Position location, String teamNameIn, Color teamColorIn) {
		super(CORE_MASS, CORE_RADIUS, location);
		this.teamName = teamNameIn;
		this.teamColor = teamColorIn;
		this.energy = CORE_MAX_ENERGY;
		setDrawable(true);
		setAlive(true);
		this.isMoveable = true;
		graphic = new CoreGraphics(this, teamColorIn);
	}
	
	/**
	 * Makes a copy used for security
	 */
	public AiCore deepClone() {
		AiCore newCore = new AiCore(getPosition().deepCopy(),this.teamName, this.teamColor);
		newCore.setAlive(isAlive);
		newCore.energy = energy;
		newCore.id = id;
		return newCore;
	}
	
	/**
	 * Resets the energy back to full for the core
	 */
	public void resetCoreEnergy() {
		this.energy = AiCore.CORE_MAX_ENERGY;
	}
	
	/**
	 * Update the energy of the core and kill it energy is depleted
	 */
	public void updateEnergy(int change) {
		this.energy += change;
		if (this.energy <= 0) {
			this.setAlive(false);
		}
	}
	
	/**
	 * Returns the current energy of the core.
	 * Can help avoid pursuing cores that are likely to die before the ship reaches them.
	 * @return
	 */
	public int getCoreEnergy() {
		return this.energy;
	}
	
	/**
	 * Returns the name of the team that owns this AiCore.
	 * @return
	 */
	public String getTeamName() {
		return teamName;
	}

	
	public String toString() {
		String str = "Core id: [" + id + "], Core Team: [" + this.teamName + "]";
		return str;
	}

}

