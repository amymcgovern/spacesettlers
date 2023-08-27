package spacesettlers.objects.resources;

import java.util.HashMap;
import java.util.Map;

/**
 * A grouping of resources to be used for holding and for purchasing items
 * 
 * @author amy
 */
public class ResourcePile {
	Map<ResourceTypes, Integer> resources;

	/**
	 * Initialize with zero resources (can be set using setResource)
	 */
	public ResourcePile() {
		super();
		resources = new HashMap<ResourceTypes, Integer>();
		reset();
	}
	
	/**
	 * Initialize resources using the specified amounts 
	 * 
	 * @param water initial water
	 * @param fuel initial fuel
	 * @param metals initial metals
	 */
	public ResourcePile(int water, int fuel, int metals) {
		super();
		resources = new HashMap<ResourceTypes, Integer>();
		resources.put(ResourceTypes.WATER, water);
		resources.put(ResourceTypes.FUEL, fuel);
		resources.put(ResourceTypes.METALS, metals);
		resources.put(ResourceTypes.STARS, 0);
	}

	/**
	 * Initialize resources using the specified amounts
	 *
	 * @param water initial water
	 * @param fuel initial fuel
	 * @param metals initial metals
	 * @param stars initial stars
	 */
	public ResourcePile(int water, int fuel, int metals, int stars) {
		super();
		resources = new HashMap<ResourceTypes, Integer>();
		resources.put(ResourceTypes.WATER, water);
		resources.put(ResourceTypes.FUEL, fuel);
		resources.put(ResourceTypes.METALS, metals);
		resources.put(ResourceTypes.STARS, stars);
	}

	/**
	 * Make a new pile with the same resources as the specified pile
	 * 
	 * @param other resource pile to copy
	 */
	public ResourcePile(ResourcePile other) {
		super();
		resources = new HashMap<ResourceTypes, Integer>();
		resources.put(ResourceTypes.WATER, other.getResourceQuantity(ResourceTypes.WATER));
		resources.put(ResourceTypes.FUEL, other.getResourceQuantity(ResourceTypes.FUEL));
		resources.put(ResourceTypes.METALS, other.getResourceQuantity(ResourceTypes.METALS));
		resources.put(ResourceTypes.STARS, other.getResourceQuantity(ResourceTypes.STARS));
	}

	
	/**
	 * Set the resources 
	 * @param type the type of resources
	 * @param value the new value
	 */
	public void setResources(ResourceTypes type, int value) {
		resources.put(type, value);
	}


	/**
	 * Returns the resources currently held in this pile
	 * 
	 * @return the current resources
	 */
	public Map<ResourceTypes, Integer> getResources() {
		return resources;
	}
	
	/**
	 * Return the quantity of the specified resource
	 * 
	 * @param type the type of resource to query
	 * @return the amount of that resource available
	 */
	public int getResourceQuantity(ResourceTypes type) {
		return resources.get(type);
	}
	
	/**
	 * Adds two resource piles together
	 * 
	 * @param otherPile new resource to add in
	 */
	public void add(ResourcePile otherPile) {
		for (ResourceTypes type : resources.keySet()) {
			resources.put(type, resources.get(type) + otherPile.getResourceQuantity(type));
		}
	}

	/**
	 * Resets the resources available
	 */
	public void reset() {
		resources.put(ResourceTypes.WATER, 0);
		resources.put(ResourceTypes.FUEL, 0);
		resources.put(ResourceTypes.METALS, 0);
		resources.put(ResourceTypes.STARS, 0);
	}

	/**
	 * Double the costs (e.g. how much an item costs, which piles are also used for, in addition to storage).
	 * Note that stars do not double on purpose since they are used for a different kind of purchase.
	 */
	public void doubleCosts() {
		resources.put(ResourceTypes.WATER, resources.get(ResourceTypes.WATER) * 2);
		resources.put(ResourceTypes.FUEL, resources.get(ResourceTypes.FUEL) * 2);
		resources.put(ResourceTypes.METALS, resources.get(ResourceTypes.METALS) * 2);
	}

	/**
	 * Compares this pile to the other pile to see if this pile is greater (used for purchasing)
	 * 
	 * @param otherPile the pile of costs 
	 * @return true if this resource pile is greater in all resources than the cost pile
	 */
	public boolean greaterThan(ResourcePile otherPile) {
		for (ResourceTypes type : resources.keySet()) {
			if (resources.get(type) < otherPile.getResourceQuantity(type)) {
				return false;
			}
		}
		// it never failed so it was true
		return true;
	}

	/**
	 * Remove the resources from the team (don't go below zero)
	 * 
	 * @param removeResources
	 */
	public void subtract(ResourcePile removeResources) {
		for (ResourceTypes type : resources.keySet()) {
			resources.put(type, Math.max(resources.get(type) - removeResources.getResourceQuantity(type), 0));
		}
	}

	/**
	 * Computes and returns the mass of the resources
	 * 
	 * @return the mass of the resources
	 */
	public int getMass() {
		int mass = (int) (resources.get(ResourceTypes.FUEL) * ResourceFactory.FUEL_DENSITY +
				resources.get(ResourceTypes.WATER) * ResourceFactory.WATER_DENSITY + 
				resources.get(ResourceTypes.METALS) * ResourceFactory.METALS_DENSITY);
		return mass;
	}
	
	/**
	 * Returns the total resources (just a sum, used for scoring)
	 * 
	 * @return the total resources collected (type doesn't matter)
	 */
	public int getTotal() {
		int sum = 0;
		for (ResourceTypes type : resources.keySet()) {
			sum += resources.get(type);
		}
		return sum;
	}
	
	public String toString() {
		String str = "Water: " + resources.get(ResourceTypes.WATER) + " Fuel: " + resources.get(ResourceTypes.FUEL) + 
				" Metals: " + resources.get(ResourceTypes.METALS) + " Stars: " + resources.get(ResourceTypes.STARS);
		return str;
	}

}
