package spacesettlers.objects.powerups;

import spacesettlers.objects.AbstractActionableObject;

/**
 * Turns the shield on/off (applying this power up is a toggle)
 *  
 * @author amy
 *
 */
public final class PowerupToggleShield implements SpaceSettlersPowerup {
	public static final int SHIELD_STEP_COST = 5;
	
	/**
	 * Toggle the shield and if it is on, take the energy cost for using it
	 */
	@Override
	public void applyPowerup(AbstractActionableObject actionableObject) {
		actionableObject.setShielded(!actionableObject.isShielded());

		actionableObject.updateEnergy(-getCostToUse());
	}

	/**
	 * Get the cost to run the shield for a time step
	 */
	@Override
	public int getCostToUse() {
		return SHIELD_STEP_COST;
	}

	@Override
	public SpaceSettlersPowerupEnum getType() {
		return SpaceSettlersPowerupEnum.TOGGLE_SHIELD;
	}

}
