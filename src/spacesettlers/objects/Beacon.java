package spacesettlers.objects;

import spacesettlers.graphics.BeaconGraphics;
import spacesettlers.utilities.Position;

public class Beacon extends AbstractObject {
	/**
	 * The radius of a beacon
	 */
	public static final int BEACON_RADIUS = 10;
	
	public static final int BEACON_MASS = 0;
	
	public static final int BEACON_ENERGY_BOOST = 2500;
	
	public Beacon(Position location) {
		super(BEACON_MASS, BEACON_RADIUS, location);
		
		setDrawable(true);
		setAlive(true);
		this.isMoveable = false;
		graphic = new BeaconGraphics(this);
	}
	
	/**
	 * Makes a copy used for security
	 */
	public Beacon deepClone() {
		Beacon newBeacon = new Beacon(getPosition().deepCopy());
		newBeacon.setAlive(isAlive);
		newBeacon.id = id;
		return newBeacon;
	}

	public String toString() {
		String str = "Beacon id " + id + " energy boost " + BEACON_ENERGY_BOOST;
		return str;
	}

}
