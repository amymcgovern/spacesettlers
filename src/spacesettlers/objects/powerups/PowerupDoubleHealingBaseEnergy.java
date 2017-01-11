package spacesettlers.objects.powerups;

import spacesettlers.objects.Base;
import spacesettlers.objects.AbstractActionableObject;

/**
 * Doubles a base's healing energy increment on each time step.
 * It costs 0 to use.
 * It is removed from the list of available power ups to prevent it being used over and over.
 * 
 * @author amy
 *
 */
public final class PowerupDoubleHealingBaseEnergy implements SpaceSettlersPowerup {

	/**
	 * Double the healing increment
	 */
	@Override
	public void applyPowerup(AbstractActionableObject actionableObject) {
		Base base = (Base) actionableObject;
		base.setHealingIncrement(base.getHealingIncrement() * 2);
		base.removePowerup(getType());
	}

	/**
	 * Costs 0
	 */
	@Override
	public int getCostToUse() {
		return 0;
	}

	/**
	 * Heals base
	 */
	@Override
	public SpaceSettlersPowerupEnum getType() {
		return SpaceSettlersPowerupEnum.DOUBLE_BASE_HEALING_SPEED;
	}

}
