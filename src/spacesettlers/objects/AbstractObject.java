package spacesettlers.objects;

import java.util.UUID;

import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.resources.ResourceFactory;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.utilities.Position;

/**
 * Superclass for all objects in the spacesettlers simulator.  
 * 
 * @author amy
 */
abstract public class AbstractObject {
	/**
	 * Position of the object in the simulator space 
	 */
	protected Position position;
	
	/**
	 * The radius of the object
	 */
	protected int radius;
	
	/**
	 * The mass of the object
	 */
	protected int mass, originalMass;
	
	/**
	 * Is the object alive?  Some objects can never die but some can.
	 */
	protected boolean isAlive;
	
	/**
	 * Is the object currently drawable on the GUI?
	 */
	boolean isDrawable;
	
	/**
	 * Is the object moveable?  Some objects can be flagged as not moveable and then
	 * they will not move even if things bounce into them.
	 */
	protected boolean isMoveable;
	
	/**
	 * The resources available from this object (if it is mineable) or that this object is holding (ship and base).
	 * Ignored for objects that don't use this (such as beacons).
	 */
	protected ResourcePile resources;
	
	/**
	 * Number of flags this ship or base has collected
	 */
	protected int numFlags;
	
	/**
	 * Number of cores this ship or base has collected
	 */
	protected int numCores;
	
	/**
	 * The graphics for this object in the GUI
	 */
	protected SpacewarGraphics graphic;
	
	/**
	 * Is the object controlled through actions?  Ships and teams are.
	 */
	protected boolean isControllable;

	/**
	 * Id to track over cloning
	 */
	protected UUID id;
	
	/**
	 * flag to allow an object to respawn or not
	 */
	protected boolean respawn;
	
	/**
	 * All objects start at rest
	 */
	public AbstractObject(int mass, int radius) {
		this.mass = mass;
		this.originalMass = mass;
		this.radius = radius;
		position = new Position(0,0);
		this.id = UUID.randomUUID();
		resources = new ResourcePile();
		this.numFlags = 0;
		this.respawn = true;
		this.numCores = 0;
	}

	/**
	 * All objects start at rest
	 */
	public AbstractObject(int mass, int radius, Position position) {
		this.mass = mass;
		this.originalMass = mass;
		this.radius = radius;
		this.position = position;
		this.id = UUID.randomUUID();
		resources = new ResourcePile();
		this.numFlags = 0;
		this.respawn = true;
		this.numCores = 0;
	}

	
	/**
	 * @param isAlive set to true if the object is alive
	 */
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	/**
	 * @param isDrawable set to true if the object is drawable. Note it is only 
	 * drawn if it is alive as well.
	 */
	public void setDrawable(boolean isDrawable) {
		this.isDrawable = isDrawable;
	}
	
	/**
	 * Is this object alive?
	 * 
	 * @return the isAlive
	 */
	public boolean isAlive() {
		return isAlive;
	}

	/**
	 * Is this object drawable?
	 * 
	 * @return the isDrawable
	 */
	public boolean isDrawable() {
		return isDrawable;
	}

	/**
	 * @return the location
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * Move the object (should only be called inside the simulator)
	 * 
	 * @param location
	 */
	public void setPosition(Position location) {
		this.position = location;
	}

	/**
	 * Return the radius of the object
	 * @return
	 */
	public int getRadius() {
		return radius;
	}
	
	/**
	 * Get the mass of the object
	 * @return the mass of the object
	 */
	public int getMass() {
		return mass;
	}

	/**
	 * Change the mass of the object to a new value
	 * 
	 * @param mass the new mass
	 */
	public void setMass(int mass) {
		this.mass = mass;
	}

	/**
	 * Add a list of new resources to the ship/base cargo bay
	 * 
	 * @param newResources new list of AbstractResource to add to the cargo bay
	 */
	public void addResources(ResourcePile newResources) {
		resources.add(newResources);
		setMass((int) (ResourceFactory.REFINED_RESOURCE_DENSITY_MULTIPLIER * resources.getMass() + originalMass));
	}
	
	/**
	 * Get the current available resources
	 * 
	 * @return the ResourcePile of resources held by this object
	 */
	public ResourcePile getResources() {
		return resources;
	}

	/**
	 * Reset the list of resources (probably because the ship died)
	 */
	public void resetResources() {
		resources.reset();
		setMass(originalMass);
	}
	

	/**
	 * @return the graphic, which is what is drawn in the graphics window
	 */
	public SpacewarGraphics getGraphic() {
		return graphic;
	}

	/**
	 * Is this object controlled by an external client?
	 * @return
	 */
	public boolean isControllable() {
		return isControllable;
	}

	/**
	 * is the object moveable? 
	 * @return
	 */
	public boolean isMoveable() {
		return isMoveable;
	}
	
	
	/**
	 * Return true if this object can respawn now and false otherwise.
	 * Assumes you can respawn immediately unless you override.
	 */
	public boolean canRespawn() {
		return respawn;
	}
	
	/**
	 * Set the respawn flag
	 * 
	 * @param newRespawn
	 * @return
	 */
	public void setRespawn(boolean newRespawn) {
		this.respawn = newRespawn;
	}
	
	/**
	 * Gets the unique id for this object
	 * @return
	 */
	public UUID getId() {
		return id;
	}

	
	/**
	 * Hash on the UUID
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * Equals on the UUID
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractObject other = (AbstractObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Make a deep copy of this object (deeply copying all pointers to avoid
	 * exploitation within the simulator)
	 * 
	 * @return
	 */
	abstract public AbstractObject deepClone();

	@Override
	public String toString() {
		return "AbstractObject at " + position;
	}
	
	/**
	 * Resets the UUID of the object (for respawning)
	 */
	public void resetId() {
		id = UUID.randomUUID();
	}

	/**
	 * Get the total number of flags owned by this object (ship or base)
	 * @return
	 */
	public int getNumFlags() {
		return numFlags;
	}
	
	/**
	 * Increment the number of flags held by this object
	 */
	public void incrementFlags() {
		numFlags++;
	}

	/**
	 * Get the total number of cores owned by this object (ship or base)
	 * @return
	 */
	public int getNumCores() {
		return numCores;
	}
	
	/**
	 * Increment the number of cores held by this object
	 */
	public void incrementCores(int number) {
		numCores+= number;
	}

}
