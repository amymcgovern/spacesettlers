package spacesettlers.objects.powerups;

import spacesettlers.objects.AbstractActionableObject;

/**
 * All power ups need to implement this interface.  Some power ups may need
 * to do even more (like a turret to aim it).
 * 
 * @author amy
 *
 */
public interface SpaceSettlersPowerup {
	/**
	 * Apply the power up to the object 
	 * @param actionableObject
	 */
	public void applyPowerup(AbstractActionableObject actionableObject);
	
	/**
	 * All power ups cost something to use
	 * @return
	 */
	public int getCostToUse();
	
	/**
	 * What type of power up is this?
	 * @return
	 */
	public SpaceSettlersPowerupEnum getType();

}
