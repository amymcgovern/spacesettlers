package spacesettlers.objects.powerups;

import spacesettlers.objects.AbstractActionableObject;

/**
 * Doubles a ship or base's weapons capacity (then
 * it is removed from the list of valid power ups or else
 * it would be used over and over again)
 * 
 * @author amy
 *
 */
public final class PowerupDoubleWeapon implements SpaceSettlersPowerup {
	/**
	 * Double the weapon capacity on the actionable object
	 */
	@Override
	public void applyPowerup(AbstractActionableObject actionableObject) {
		actionableObject.setWeaponCapacity(actionableObject.getWeaponCapacity() * 2);
		actionableObject.removePowerup(getType());
	}

	/**
	 * This costs nothing to use (and is permanent)
	 */
	@Override
	public int getCostToUse() {
		return 0;
	}

	@Override
	public SpaceSettlersPowerupEnum getType() {
		return SpaceSettlersPowerupEnum.DOUBLE_WEAPON_CAPACITY;
	}
	
}
