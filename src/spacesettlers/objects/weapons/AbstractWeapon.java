package spacesettlers.objects.weapons;

import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerup;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * All weapons should subclass this main weapon class.
 * 
 * @author amy
 */
abstract public class AbstractWeapon extends AbstractObject implements SpaceSettlersPowerup {
	/**
	 * The amount of damageInflicted this weapon inflicts
	 */
	int damage;
	
	/**
	 * The cost to fire this weapon
	 */
	int costToUse;
	
	/**
	 * The ship that fired this weapon
	 */
	Ship firingShip;
	
	/**
	 * Make a new weapon with the required information
	 * 
	 * @param mass
	 * @param radius
	 * @param position
	 * @param damageInflicted
	 * @param costToUse
	 * @param teamName Needed to ensure this weapon belongs to the ship that fired it
	 * @param firingShipId Needed to ensure this weapon belongs to the ship that fired it
	 */
	public AbstractWeapon(int mass, int radius, Position position, int damage, int costToUse, Ship firingShip) {
		super(mass, radius, position);
		this.damage = damage;
		this.costToUse = costToUse;
		this.firingShip = firingShip;
	}
	
	/**
	 * Shift the weapon firing position to be in front of the ship, not hitting it,
	 * at the appropriate initial speed
	 */
	public void shiftWeaponFiringLocation(double initialSpeed) 
	{
		int radiusToShift = firingShip.getRadius() + radius * 2;
		position.setX(position.getX() + (radiusToShift * Math.cos(position.getOrientation())));
		position.setY(position.getY() + (radiusToShift * Math.sin(position.getOrientation())));
		Vector2D newVelocity = new Vector2D(initialSpeed * Math.cos(position.getOrientation()), 
				initialSpeed * Math.sin(position.getOrientation()));
		position.setTranslationalVelocity(newVelocity);
	}
	
	/**
	 * Returns the damageInflicted for the weapon
	 * @return
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * Return the cost for using the weapon
	 * @return
	 */
	public int getCostToUse() {
		return costToUse;
	}
	
	/**
	 * Get the ship that fired this weapon
	 * @return
	 */
	public Ship getFiringShip() {
		return firingShip;
	}

	/**
	 * Set the firing ship (should only be inside the simulator)
	 * @param firingShip
	 */
	public void setFiringShip(Ship firingShip) {
		this.firingShip = firingShip;
	}
	
	
	/**
	 * Return true if the weapon can be fired by the ship and team and false otherwise
	 * 
	 * @param teamName
	 * @param shipId
	 * @return
	 */
	public boolean isValidWeapon(Ship ship) {
		if (firingShip.equals(ship)) {
			return true;
		} 
		return false;
	}
	
}
