package spacesettlers.objects.weapons;

import spacesettlers.graphics.EMPGraphics;
import spacesettlers.objects.Ship;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.powerups.SpaceSettlersPowerup;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.utilities.Position;

/**
 * Electro-magnetic pulse (EMP) that freezes any ship it hitsInflicted for a specified
 * amount of time.  To use this, you have to buy the POWERUP_EMP_LAUNCHER in purchases. 
 * 
 * @author amy
 *
 */
public final class EMP extends AbstractWeapon implements SpaceSettlersPowerup {
	public static final int EMP_DAMAGE = 0;
	public static final int EMP_COST = -100;
	public static final int EMP_RADIUS = 5;
	public static final int EMP_MASS = 1;
	public static final int INITIAL_VELOCITY = 100;
	public static final int FREEZE_STEPS = 40;
	
	/**
	 * Number of steps to freeze the ship it hitsInflicted (not a static since maybe a powerup
	 * count change it, if we add that)
	 */
	int freezeCount;
	
	public EMP(Position position, Ship firingShip) {
		super(EMP_MASS, EMP_RADIUS, position, EMP_DAMAGE, EMP_COST, firingShip);
		super.shiftWeaponFiringLocation(INITIAL_VELOCITY);
		graphic = new EMPGraphics(this);
		setDrawable(true);
		setAlive(true);
		this.isControllable = false;
		this.isMoveable = true;
		this.freezeCount = FREEZE_STEPS;
	}

	/**
	 * Make a deep copy
	 */
	@Override
	public AbstractObject deepClone() {
		EMP newEMP = new EMP(position.deepCopy(), firingShip.deepClone());
		newEMP.setAlive(isAlive);
		newEMP.id = id;
		newEMP.freezeCount = freezeCount;
		newEMP.position = position.deepCopy();
		return newEMP;
	}
	
	/**
	 * Return the number of steps the EMP freezes a ship for
	 * @return
	 */
	public int getFreezeCount() {
		return freezeCount;
	}

	/**
	 * Can't fire with shields on so drop them.
	 * Then give it the cost of use
	 */
	@Override
	public void applyPowerup(AbstractActionableObject actionableObject) {
		// can't fire with shields up so automatically drop them
		actionableObject.setShielded(false);
		
		// only ships fire weapons (right now) (TODO: fix this when/if bases can)
		Ship ship = (Ship) actionableObject;
		ship.updateEnergy(getCostToUse());
		ship.incrementWeaponCount();
	}

	/**
	 * Return the type
	 */
	@Override
	public SpaceSettlersPowerupEnum getType() {
		return SpaceSettlersPowerupEnum.FIRE_MISSILE;
	}


}
