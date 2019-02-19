package spacesettlers.objects.weapons;

import spacesettlers.graphics.MissileGraphics;
import spacesettlers.objects.Ship;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.utilities.Position;

/**
 * A bullet/missle
 * @author amy
 *
 */
public final class Missile extends AbstractWeapon {
	public static final int MISSILE_DAMAGE = -200;
	public static final int MISSILE_COST = -50;
	public static final int MISSILE_RADIUS = 3;
	public static final int MISSILE_MASS = 1;
	public static final int INITIAL_VELOCITY = 100;
	
	/**
	 * A new bullet with the specified position and shift
	 * Needs to be shifted so it doesn't hit the ship it is firing from
	 */
	public Missile(Position position, Ship firingShip) {
		super(MISSILE_MASS, MISSILE_RADIUS, position, MISSILE_DAMAGE, MISSILE_COST,firingShip);
		super.shiftWeaponFiringLocation(Math.abs(firingShip.getPosition().getTotalTranslationalVelocity()) + INITIAL_VELOCITY);
		graphic = new MissileGraphics(this);
		setDrawable(true);
		setAlive(true);
		this.isControllable = false;
		this.isMoveable = true;
	}

	/**
	 * Copy the bullet
	 */
	public Missile deepClone() {
		Missile newBullet = new Missile(position.deepCopy(), firingShip.deepClone());
		newBullet.setAlive(isAlive);
		newBullet.id = id;
		newBullet.position = position.deepCopy();
		return newBullet;
	}

	/**
	 * Bullets do not respawn
	 */
	public boolean canRespawn() {
		return false;
	}

	/**
	 * Mostly a placeholder for when power ups work
	 */
	public SpaceSettlersPowerupEnum getType() {
		return SpaceSettlersPowerupEnum.FIRE_MISSILE;
	}

	/**
	 * Apply the power up.  For a bullet,
	 * the means unshielding the ship if it is shielded and updating the energy of the ship.
	 */
	@Override
	public void applyPowerup(AbstractActionableObject actionableObject) {
		// can't fire with shields up so automatically drop them
		actionableObject.setShielded(false);
		
		// only ships fire bullets (TODO: fix this when/if bases can)
		Ship ship = (Ship) actionableObject;
		ship.updateEnergy(getCostToUse());
		ship.incrementWeaponCount();
	}


}
