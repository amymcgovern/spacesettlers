package spacesettlers.actions;

import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;

/**
 * Enum with the types of purchases that a team can make and a map to the right powerup.
 *
 * @author amy
 */
public enum PurchaseTypes {
	BASE(),
	SHIP(),
	POWERUP_SHIELD(SpaceSettlersPowerupEnum.TOGGLE_SHIELD),
	POWERUP_EMP_LAUNCHER(SpaceSettlersPowerupEnum.FIRE_EMP),
	POWERUP_DOUBLE_BASE_HEALING_SPEED(SpaceSettlersPowerupEnum.DOUBLE_BASE_HEALING_SPEED),
	POWERUP_DOUBLE_MAX_ENERGY(SpaceSettlersPowerupEnum.DOUBLE_MAX_ENERGY),
	POWERUP_DOUBLE_WEAPON_CAPACITY(SpaceSettlersPowerupEnum.DOUBLE_WEAPON_CAPACITY),
	POWERUP_SET_SHIP_SELF_HEAL(SpaceSettlersPowerupEnum.SET_SHIP_SELF_HEAL),
	CORE(),
	DRONE(),
	NOTHING();

	/**
	 * Map to the power up (if it exists)
	 */
	SpaceSettlersPowerupEnum powerupMap;
	
	PurchaseTypes() {
		this.powerupMap = null;
	}

	PurchaseTypes(SpaceSettlersPowerupEnum powerupMap) {
		this.powerupMap = powerupMap;
	}

	public SpaceSettlersPowerupEnum getPowerupMap() {
		return powerupMap;
	}
}
