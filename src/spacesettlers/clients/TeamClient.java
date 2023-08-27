package spacesettlers.clients;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.geom.AffineTransform;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * All clients must inherit from the abstract client
 * 
 * @author amy
 */
abstract public class TeamClient {
	/**
	 * The team color (used by the GUI)
	 */
	Color teamColor;
	
	/**
	 * The name of the team
	 */
	String teamName;
	
	/**
	 * Optional:  if the agent needs to read in from a file, it can specify the path to it here 
	 */
	String knowledgeFile;
	
	/**
	 * Save the random number generator for future use
	 */
	protected Random random;
	
	/**
	 * Maximum number of ships (copy of what is stored in Team, since the individual clients can't access Team)
	 */
	int maxNumberShips;
	
	/**
	 * Transformation for mouse clicks
	 */
	AffineTransform mouseTransform;
	
	/**
	 * Sets the random number generator to the global one (for repeatability)
	 * @param random
	 */
	public void setRandom(Random random) {
		this.random = random;
	}

	/**
	 * @return the teamColor
	 */
	public Color getTeamColor() {
		return teamColor;
	}

	/**
	 * @param teamColor the teamColor to set
	 */
	public void setTeamColor(Color teamColor) {
		this.teamColor = teamColor;
	}

	/**
	 * @return the teamName
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * @param teamName the teamName to set
	 */
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
	/**
	 * Get the maximum number of ships for this team
	 * @return
	 */
	public int getMaxNumberShips() {
		return maxNumberShips;
	}

	/**
	 * Set the maximum number of ships (just a copy from Team, setting this inside your
	 * client will have NO effect on the simulator)
	 * 
	 * @param maxNumberShips
	 */
	public void setMaxNumberShips(int maxNumberShips) {
		this.maxNumberShips = maxNumberShips;
	}

	/**
	 * Called when the team is created from the config file
	 * @param knowledgeFile
	 */
	public void setKnowledgeFile(String knowledgeFile) {
		this.knowledgeFile = knowledgeFile;
	}
	
	/**
	 * Called before an action begins.  Return a hash map of ids to SpacewarActions  
	 * 
	 * @param space physics
	 * @param actionableObjects the ships and bases for this team
	 * @return
	 */
	abstract public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space, 
			Set<AbstractActionableObject> actionableObjects);
	
	/**
	 * Called when actions end but before time advances.  Can be used to see
	 * if a ship died or other useful physics checks.
	 * 
	 * @param space
	 * @param actionableObjects the ships and bases for this team
	 * @param ships
	 */
	abstract public void getMovementEnd(Toroidal2DPhysics space, 
			Set<AbstractActionableObject> actionableObjects);
	
	/**
	 * Called each time step to get the power up or weapon for each actionable object 
	 * 
	 * @param space physics
	 * @param actionableObjects the ships and bases for this team
	 * @return
	 */
	abstract public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space, 
			Set<AbstractActionableObject> actionableObjects);

	/**
	 * Called each time step to get the game search for each ship
	 * 
	 * @param space physics
	 * @param actionableObjects the shipsfor this team
	 * @return
	 */
	abstract public Map<UUID, AbstractGameAgent> getGameSearch(Toroidal2DPhysics space, 
			Set<AbstractActionableObject> actionableObjects);

	
	/**
	 * Called once per turn to see if the team wants to purchase anything with its
	 * existing currently available resources.  Can only purchase one item per turn.
	 * 
	 * @param space
	 * @param actionableObjects
	 * @param resourcesAvailable how much resourcesAvailable you have
	 * @param purchaseCosts how much each type of purchase currently costs for this team
	 * @return
	 */
	abstract public Map<UUID, PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space, 
			Set<AbstractActionableObject> actionableObjects, 
			ResourcePile resourcesAvailable, 
			PurchaseCosts purchaseCosts);
	
	/**
	 * Called when the client is created
	 */
	abstract public void initialize(Toroidal2DPhysics space);
	
	/**
	 * Called when the client is shut down (which is at the end of a game)
	 */
	abstract public void shutDown(Toroidal2DPhysics space);

	/**
	 * Return any graphics that the team client wants to draw
	 * @return a set of objects that extend the SpacewarGraphics class
	 */
	abstract public Set<SpacewarGraphics> getGraphics();

	/**
	 * Get the knowledge file name
	 * @return
	 */
	public String getKnowledgeFile() {
		return knowledgeFile;
	}

	/**
	 * If the client wants to take input from the keyboard, they need to override this
	 * to return a proper key listener.  This shouldn't count as an exploit
	 * since it can't be used on the ladder.  It can be useful for debugging so it is
	 * allowed for any client to override.  The keys are only handled inside the client 
	 * (the global GUI keys are handled by a separate listener)
	 * 
	 * @return a valid KeyAdapter
	 */
	public KeyAdapter getKeyAdapter() {
		return null;
	}

	/**
	 * If the client wants to take input from the mouse, they need to override this
	 * to return a proper mouse listener.  This shouldn't count as an exploit
	 * since it can't be used on the ladder.  It can be useful for debugging so it is
	 * allowed for any client to override.  The mouse movements are only handled inside the client 

	 * @param mouseTransform takes an affine transformation used to scale the graphics: must be used to 
	 * scale mouse clicks 
	 * 
	 * @return a valid KeyAdapter
	 */
	public MouseAdapter getMouseAdapter(AffineTransform mouseTransform) {
		this.mouseTransform = mouseTransform;

		return null;
	}
	
	
}
