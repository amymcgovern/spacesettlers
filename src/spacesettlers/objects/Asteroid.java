package spacesettlers.objects;

import spacesettlers.graphics.AsteroidGraphics;
import spacesettlers.objects.resources.ResourceFactory;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.objects.resources.ResourceTypes;
import spacesettlers.utilities.Position;

/**
 * An asteroid that can or cannot be mined
 * 
 * @author amy
 *
 */
public class Asteroid extends AbstractObject {
	public static final int MIN_ASTEROID_RADIUS = 5;
	public static final int MAX_ASTEROID_RADIUS = 15;
	public static final int MIN_ASTEROID_MASS = 2000;
	
	private double fuelProportion, waterProportion, metalsProportion;
	
    /**
     * Is the asteroid mineable?
     */
    boolean isMineable;
    
    /**
     * Is the asteroid gameable?
     */
    boolean isGameable;
    
    /**
     * Create an asteroid with proportions of resources (from config file)
     * 
     * @param location
     * @param mineable
     * @param gameable
     * @param radius
     * @param moveable
     * @param fuel
     * @param water
     * @param metals
     */
    public Asteroid(Position location, boolean mineable, boolean gameable, int radius, boolean moveable, double fuel, double water, double metals) {
		super(MIN_ASTEROID_MASS, radius, location);
		
		setDrawable(true);
		setAlive(true);
		isMineable = mineable;
		isGameable = gameable;
		graphic = new AsteroidGraphics(this);
		this.isMoveable = moveable;
		this.fuelProportion = fuel;
		this.waterProportion = water;
		this.metalsProportion = metals;
		
		// asteroids do not respawn
		super.setRespawn(false);
		
		if (isMineable) {
			resetResources();
		} 
		
		// reset the mass based on the created resources
		super.setMass(MIN_ASTEROID_MASS + resources.getMass());
	}

    /**
     * Create an asteroid with an initial resource pile (from a dead ship)
     * 
     * @param location
     * @param mineable
     * @param gameable
     * @param radius
     * @param moveable
     * @param initialResources
     */
    public Asteroid(Position location, boolean mineable, boolean gameable, int radius, boolean moveable, ResourcePile initialResources) {
		super(MIN_ASTEROID_MASS, radius, location);
		
		setDrawable(true);
		setAlive(true);
		isMineable = mineable;
		isGameable = gameable;
		graphic = new AsteroidGraphics(this);
		this.isMoveable = moveable;
		
    	resources.setResources(ResourceTypes.FUEL, initialResources.getResourceQuantity(ResourceTypes.FUEL));
    	resources.setResources(ResourceTypes.WATER, initialResources.getResourceQuantity(ResourceTypes.WATER));
    	resources.setResources(ResourceTypes.METALS, initialResources.getResourceQuantity(ResourceTypes.METALS));
		
    	double normalize = initialResources.getResourceQuantity(ResourceTypes.FUEL) + 
    			initialResources.getResourceQuantity(ResourceTypes.WATER) + initialResources.getResourceQuantity(ResourceTypes.METALS);
		this.fuelProportion = initialResources.getResourceQuantity(ResourceTypes.FUEL) / normalize;
		this.waterProportion = initialResources.getResourceQuantity(ResourceTypes.WATER) / normalize;
		this.metalsProportion = initialResources.getResourceQuantity(ResourceTypes.METALS) / normalize;

		// asteroids do not respawn
		super.setRespawn(false);
		
		// reset the mass based on the created resources
		super.setMass(MIN_ASTEROID_MASS + resources.getMass());
	}
    
    
    /**
     * Make a copy for security
     */
    public Asteroid deepClone() {
    	Asteroid newAsteroid = new Asteroid(getPosition().deepCopy(), isMineable, isGameable, radius, isMoveable, 
    			fuelProportion, waterProportion, metalsProportion);
    	newAsteroid.setAlive(isAlive);
    	newAsteroid.id = id;
    	return newAsteroid;
    }
    
    
    /**
     * Sets the resource value based on the radius
     */
    public void resetResources() {
    	resources.setResources(ResourceTypes.FUEL, ResourceFactory.getResourceQuantity(ResourceTypes.FUEL, radius));
    	resources.setResources(ResourceTypes.WATER, ResourceFactory.getResourceQuantity(ResourceTypes.WATER, radius));
    	resources.setResources(ResourceTypes.METALS, ResourceFactory.getResourceQuantity(ResourceTypes.METALS, radius));
    }
    
	/**
	 * @return the isMineable
	 */
	public boolean isMineable() {
		return isMineable;
	}

	/**
	 * @return the isGameable
	 */
	public boolean isGameable() {
		return isGameable;
	}
	
	/**
	 * Get the fuel proportion (used for graphics but maybe useful in other ways)
	 * @return the proportion of the asteroid dedicated to fuel
	 */
	public double getFuelProportion() {
		return fuelProportion;
	}

	/**
	 * Get the water proportion (used for graphics but maybe useful in other ways)
	 * @return the proportion of the asteroid dedicated to water
	 */
	public double getWaterProportion() {
		return waterProportion;
	}

	/**
	 * Get the metals proportion (used for graphics but maybe useful in other ways)
	 * @return the proportion of the asteroid dedicated to metals
	 */
	public double getMetalsProportion() {
		return metalsProportion;
	}
	
	/**
	 * Set an asteroid to be mineable or not (used with resource dropping with ships dying)
	 * 
	 * @param newMineable
	 */
	public void setMineable(boolean newMineable) {
		this.isMineable = newMineable;
	}

	public String toString() {
		String str = "Asteroid id " + super.id + " mass " + mass + " resources " + resources;
		return str;
		
	}
	
}
