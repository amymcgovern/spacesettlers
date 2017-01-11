package spacesettlers.objects.powerups;

import spacesettlers.objects.AbstractActionableObject;

/**
 * Double the maximum energy available on the actionable object
 * It is free to use (no energy penalty).  After it is used, it is 
 * removed from the list of valid power ups for that object (or it
 * would be used again and again)
 * @author amy
 *
 */
public final class PowerupDoubleMaxEnergy implements SpaceSettlersPowerup {

	/**
	 * Double the max energy
	 */
	@Override
	public void applyPowerup(AbstractActionableObject actionableObject) {
		actionableObject.setMaxEnergy(actionableObject.getMaxEnergy() * 2);
		actionableObject.removePowerup(getType());
	}

	/**
	 * It doesn't cost anything to use
	 */
	@Override
	public int getCostToUse() {
			return 0;
	}

	@Override
	public SpaceSettlersPowerupEnum getType() {
		return SpaceSettlersPowerupEnum.DOUBLE_MAX_ENERGY;
	}

}
