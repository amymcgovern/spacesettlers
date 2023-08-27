package spacesettlers.actions;

import java.util.HashMap;
import java.util.Map;

import spacesettlers.objects.resources.ResourcePile;
/**
 * Keeps track of the costs of buying each item for a team.  Costs go up as a team makes purchases.
 * 
 * @author amy
 */
public class PurchaseCosts {
	Map<PurchaseTypes, ResourcePile> costs;

	/**
	 * Create an empty purchase cost object (costs set to fixed initial values)
	 */
	public PurchaseCosts() {
		super();
		costs = new HashMap<PurchaseTypes, ResourcePile>();
		reset();
	}

	/**
	 * Put the costs back to their initial values
	 */
	public void reset() {
		costs.put(PurchaseTypes.BASE, new ResourcePile(250, 300, 450));
		costs.put(PurchaseTypes.SHIP, new ResourcePile(500, 1000, 500));
		costs.put(PurchaseTypes.CORE, new ResourcePile(400, 850, 400));//herr0861 edit (see below comment)
		costs.put(PurchaseTypes.DRONE, new ResourcePile(100,100,100));
		costs.put(PurchaseTypes.NOTHING, new ResourcePile(0,0,0));
		costs.put(PurchaseTypes.POWERUP_DOUBLE_BASE_HEALING_SPEED, new ResourcePile(1000,1000,500));
		costs.put(PurchaseTypes.POWERUP_DOUBLE_MAX_ENERGY, new ResourcePile(750, 1000, 500));
		costs.put(PurchaseTypes.POWERUP_DOUBLE_WEAPON_CAPACITY, new ResourcePile(250, 1000, 1000));
		costs.put(PurchaseTypes.POWERUP_EMP_LAUNCHER, new ResourcePile(0, 750, 250));
		costs.put(PurchaseTypes.POWERUP_SHIELD, new ResourcePile(0,1500,500));
		costs.put(PurchaseTypes.POWERUP_SET_SHIP_SELF_HEAL, new ResourcePile(0,0,0, 5));

		/*
		 * The major expense of the drone should be the core. Compete gets lots of cores, but this serves little purpose there.
		 * The Core plus the drone costs about as much as a ship. I honestly think it might be too expensive, but we can play with the value.
		 */
	}
	
	/**
	 * Make a copy of the current costs (for security)
	 * 
	 * @return a copy of the current costs
	 */
	public PurchaseCosts deepCopy() {
		PurchaseCosts newCosts = new PurchaseCosts();
		
		for (PurchaseTypes type : costs.keySet()) {
			newCosts.costs.put(type, new ResourcePile(costs.get(type)));
		}
		return newCosts;
	}
	
	
	/**
	 * Get the cost of the specified type of item
	 * @param type the type of item
	 * @return the cost in resources
	 */
	public ResourcePile getCost(PurchaseTypes type) {
		return costs.get(type);
	}
	
	/**
	 * Double the cost of the specified item (team made a purchase)
	 * 
	 * @param type
	 */
	public void doubleCosts(PurchaseTypes type) {
		costs.get(type).doubleCosts();
	}
	
	/**
	 * Checks if the specified item type can be afforded to be purchased
	 * 
	 * @param type the type of item to purchase
	 * @param resources the current pile of resources
	 * @return true if the item can be afforded and false otherwise
	 */
	public boolean canAfford(PurchaseTypes type, ResourcePile resources) {
		if (resources.greaterThan(costs.get(type))) {
			return true;
		} else {
			return false;
		}
	}

}
