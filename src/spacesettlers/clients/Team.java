package spacesettlers.clients;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.Base;
import spacesettlers.objects.Drone;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.SpaceSettlersSimulator;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * A team holds the ships and a pointer to the client
 * that controls the ships.  They are separated for security inside
 * the client (to keep the client from directly manipulating ships)
 * 
 * @author amy
 */
public class Team {
	/**
	 * The set of ships owned by this team
	 */
	Set<Ship> teamShips;
	
	/**
	 * herr0861 edit
	 * The set of drones owned by this team
	 */
	Set<UUID> teamDroneIDs;
	
	/**
	 * The set of bases associated with this team (bases are not
	 * stored directly because they point to team and then cloning
	 * causes a stack overflow)
	 */
	Set<UUID> teamBaseIDs;
	
	
	/**
	 * A set of all the ids associated with the team (used to verify
	 * power ups)
	 */
	Set<UUID> teamIDs;

	/**
	 * The team color (used by the GUI)
	 */
	Color teamColor;
	
	/**
	 * The name of the team
	 */
	String teamName;
	
	/**
	 * The client for this team
	 */
	TeamClient teamClient;
	
	/**
	 * current team score (set in the simulator, which knows how the team is being scored)
	 */
	double score;
	
	/**
	 * The number of flags collected by this team
	 */
	int totalFlagsCollected;

	/**
	 * The number of AiCores collected by this team
	 */
	int totalCoresCollected;
	
	/**
	 * The number of Stars collected by this team
	 */
	int totalStarsCollected;

	/**
	 * available (unspent) resourcesAvailable from the asteroids and the total resourcesAvailable earned
	 */
	ResourcePile availableResources, totalResources;
	
	/**
	 * Keep track of the total beacons collected (for the ladder)
	 */
	int totalBeaconsCollected;
	
	/**
	 * Keep track of the total killsInflicted for the team (for the ladder, if used)
	 */
	int totalKillsInflicted, totalKillsReceived;

	/**
	 * Keep track of the total assists on kills for the team (for the ladder, if used)
	 */
	int totalAssistsInflicted;

	/**
	 * Keep track of the total hitsInflicted for the team (for the ladder, if used)
	 */
	int totalHitsInflicted;
	
	/**
	 * Keep track of the total damageInflicted this ship has dealt out
	 */
	int totalDamageInflicted;
	
	/**
	 * The damageInflicted this team has received
	 */
	int totalDamageReceived;
	
	/**
	 * The name that shows up in the ladder
	 */
	String ladderName;
	
	/**
	 * The current costs for this team to buy items (some change
	 * as you buy more and more of them)
	 */
	PurchaseCosts costToPurchase;
	
	/**
	 * The maximum number of ships for this team
	 */
	int maxNumberShips;
	
	/**
	 * Thread for this team
	 */
	ExecutorService executor;
	
	/**
	 * Initialize the team client to have an empty list of ships and drones.
	 */
	public Team(TeamClient teamClient, String ladderName, int maxNumberShips) {
		this.teamShips = new LinkedHashSet<Ship>();
		this.teamDroneIDs = new LinkedHashSet<UUID>(); //herr0861 edit
		this.teamBaseIDs = new LinkedHashSet<UUID>();
		this.teamIDs = new LinkedHashSet<UUID>();
		this.teamClient = teamClient;
		this.teamColor = teamClient.getTeamColor();
		this.teamName = teamClient.getTeamName();
		this.ladderName = ladderName;
		costToPurchase = new PurchaseCosts();
		resetCostToPurchase();
		this.maxNumberShips = maxNumberShips;
		totalResources = new ResourcePile();
		availableResources = new ResourcePile();
		this.totalHitsInflicted = 0;
		this.totalKillsInflicted = 0;
		this.totalAssistsInflicted = 0;
		this.totalKillsReceived = 0;
		this.totalDamageInflicted = 0;
		this.totalDamageReceived = 0;
		this.totalFlagsCollected = 0;
		this.totalCoresCollected = 0; 
		executor = null;
	}
	
	/**
	 * Reset the costs to purchase new items
	 */
	private void resetCostToPurchase() {
		costToPurchase.reset();
	}

	/**
	 * Make a deep copy for security
	 * 
	 * @return
	 */
	public Team deepCopy() {
		Team newTeam = new Team(teamClient, ladderName, maxNumberShips);
		
		for (Ship ship : teamShips) {
			newTeam.addShip(ship.deepClone());
		}
		
		newTeam.teamDroneIDs.addAll(this.teamDroneIDs);
		newTeam.teamBaseIDs.addAll(this.teamBaseIDs);
		newTeam.costToPurchase = costToPurchase.deepCopy();
		newTeam.totalResources = new ResourcePile(totalResources);
		newTeam.availableResources = new ResourcePile(availableResources);
		newTeam.totalHitsInflicted = this.totalHitsInflicted;
		newTeam.totalKillsInflicted = this.totalKillsInflicted;
		newTeam.totalAssistsInflicted = this.totalAssistsInflicted;
		newTeam.totalKillsReceived = this.totalKillsReceived;
		newTeam.totalDamageInflicted = this.totalDamageInflicted;
		newTeam.totalDamageReceived = this.totalDamageReceived;
		newTeam.totalFlagsCollected = this.totalFlagsCollected;
		newTeam.totalCoresCollected = this.totalCoresCollected; 
		return newTeam;
	}
	
	/**
	 * Return the maximum number of ships for this team
	 * @return
	 */
	public int getMaxNumberShips() {
		return maxNumberShips;
	}

	
	/**
	 * Make a cloned list of the team's actionable objects (ships and bases)
	 * 
	 * @return
	 */
	private Set<AbstractActionableObject> getTeamActionableObjectsClone(Toroidal2DPhysics space) {
		Set<AbstractActionableObject> clones = new LinkedHashSet<AbstractActionableObject>();
		
		for (Ship ship : teamShips) {
			clones.add(ship.deepClone());
		}
		
		for (UUID baseId : teamBaseIDs) {
			clones.add((Base)space.getObjectById(baseId).deepClone());
		}
		
		for (UUID droneId : teamDroneIDs) {
			clones.add((Drone)space.getObjectById(droneId).deepClone()); //herr0861 edit
		}
		
		return clones;
	}

	
	
	/**
	 * Add a ship to the team
	 * @param ship
	 */
	public void addShip(Ship ship) {
		teamShips.add(ship);
		addTeamID(ship.getId());
	}
	
	/**
	 * Return the list of ships
	 * @return
	 */
	public Set<Ship> getShips() {
		return teamShips;
	}
	
	/**
	 * herr0861 edit
	 * Add a drone to the team
	 * @param drone
	 */
	public void addDrone (Drone drone) {
		teamDroneIDs.add(drone.getId());
		addTeamID(drone.getId());
	}
	
	/**
	 * herr0861 edit
	 * Return the list of drones.
	 * @return
	 */
	public Set<UUID> getDrones() {
		return teamDroneIDs;
	}
	
	/**
	 * herr0861 edit
	 * Remove the drone because it died
	 * @param drone
	 */
	public void removeDrone(Drone drone) {
		teamDroneIDs.remove(drone.getId());
		removeTeamID(drone.getId());
	}

	/**
	 * Add a base to the team's list of bases
	 * 
	 * @param base
	 */
	public void addBase(Base base) {
		teamBaseIDs.add(base.getId());
		addTeamID(base.getId());
	}
	
	/**
	 * Remove the base because it died
	 * @param base
	 */
	public void removeBase(Base base) {
		teamBaseIDs.remove(base.getId());
		removeTeamID(base.getId());
	}
	
	/**
	 * Add a team id to the list of ids for the team
	 * @param id
	 */
	private void addTeamID(UUID id) {
		teamIDs.add(id);
	}
	
	/**
	 * Remove an object from the list for the team
	 * @param id
	 */
	private void removeTeamID(UUID id) {
		teamIDs.remove(id);
	}
	
	/**
	 * Returns true if this UUID is associated with this team and false otherwise.
	 * Used to keep teams from calling power ups on other team's objects
	 * 
	 * @param id
	 * @return
	 */
	public boolean isValidTeamID(UUID id) {
		if (teamIDs.contains(id)) {
			return true;
		} else {
			return false;
		}
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
	 * Get the current cost of the specified type of item
	 * 
	 * @return the resources needed to purchase the specified item type
	 */
	public ResourcePile getCurrentCost(PurchaseTypes type) {
		return costToPurchase.getCost(type);
	}

	/**
	 * Double the cost of an item (done after a purchase)
	 */
	public void updateCost(PurchaseTypes type) {
		costToPurchase.doubleCosts(type);
	}

	/**
	 * Ask the team client for actions
	 * 
	 * @param simulator
	 * @param random
	 * @return
	 */
	public Map<UUID, AbstractAction> getTeamMovementStart(Toroidal2DPhysics space) {
        Map<UUID, AbstractAction> teamActions = new HashMap<UUID, AbstractAction>();

		// ask the client for its movement
		final Toroidal2DPhysics clonedSpace = space.deepClone();
		final Set<AbstractActionableObject> clonedActionableObjects = getTeamActionableObjectsClone(space);
		
		// if the previous thread call hasn't finished, then just return default
		if (executor == null || executor.isTerminated()) {
			executor = Executors.newSingleThreadExecutor();
		} else {
			return teamActions;
		}
		
        Future<Map<UUID, AbstractAction>> future = executor.submit(
        		new Callable<Map<UUID, AbstractAction>>(){
        			public Map<UUID, AbstractAction> call() {
        				Map<UUID, AbstractAction> teamActions = null;
    					teamActions = teamClient.getMovementStart(clonedSpace, clonedActionableObjects);
        				return teamActions;
        			}
        		});
        
        try {
            //start
            teamActions = future.get(SpaceSettlersSimulator.TEAM_ACTION_TIMEOUT, TimeUnit.MILLISECONDS);
            //finished in time
        } catch (TimeoutException e) {
            //was terminated
        	//return empty map, this will invoke default behavior of using DoNothingAction
        	teamActions = new HashMap<UUID, AbstractAction>();
        	System.err.println(getTeamName() + " timed out in getTeamMovementStart");
        } catch (InterruptedException e) {
        	//we were interrupted (should not happen but lets be good programmers) 
        	//return empty map, this will invoke default behavior of using DoNothingAction
        	teamActions = new HashMap<UUID, AbstractAction>();
			e.printStackTrace();
		} catch (ExecutionException e) {
			//the executor threw and exception (should not happen but lets be good programmers) 
        	//return empty map, this will invoke default behavior of using DoNothingAction
        	teamActions = new HashMap<UUID, AbstractAction>();
			e.printStackTrace();
		} catch (Exception e) {
			// we shouldn't do this but it seems necessary to make
			// the agent behave (do nothing) if it crashes
			System.err.println("Error in agent, stack trace to follow");
			e.printStackTrace();
        	teamActions = new HashMap<UUID, AbstractAction>();
		}


        executor.shutdownNow();
        
        return teamActions;
		
// 		HashMap<UUID, AbstractAction> teamActions = teamClient.getAction(clonedSpace, clonedTeamShips);
//		return teamActions;
	}

	/**
	 * Allows the client to do cleanup after an action and before
	 * the next one (if needed)
	 * 
	 * @param space
	 * @return
	 */
	public void getTeamMovementEnd(Toroidal2DPhysics space) {
		final Toroidal2DPhysics clonedSpace = space.deepClone();
		final Set<AbstractActionableObject> clonedActionableObjects = getTeamActionableObjectsClone(space);

		// if the previous thread call hasn't finished, then just return default
		if (executor == null || executor.isTerminated()) {
			executor = Executors.newSingleThreadExecutor();
		} else {
			return;
		}

		//System.out.println("exec " + executor.isTerminated());
        Future<Boolean> future = executor.submit(
        		new Callable<Boolean>(){
        			public Boolean call() throws Exception {
        				teamClient.getMovementEnd(clonedSpace, clonedActionableObjects);
        				return true;
        			}
        		});
        
        Boolean didReturn = false;
        try {
            //start
        	didReturn = future.get(SpaceSettlersSimulator.TEAM_END_ACTION_TIMEOUT, TimeUnit.MILLISECONDS);
            //finished in time
        } catch (TimeoutException e) {
            //was terminated
        	//set didReturn false
        	System.out.println(getTeamName() + " timed out in getTeamMovementEnd");
        	didReturn = false;
        } catch (InterruptedException e) {
        	//we were interrupted (should not happen but lets be good programmers) 
        	//set didReturn false
        	didReturn = false;
			e.printStackTrace();
		} catch (ExecutionException e) {
			//the executor threw and exception (should not happen but lets be good programmers) 
			//set didReturn false
        	didReturn = false;
			e.printStackTrace();
		} catch (RejectedExecutionException e) {
			System.err.println("exec" + executor.isTerminated());
			e.printStackTrace();
		}catch (Exception e) {
			// we shouldn't do this but it seems necessary to make
			// the agent behave (do nothing) if it crashes
        	didReturn = false;
        	System.err.println("Error in agent.  Printing stack trace.");
			e.printStackTrace();
		}

        executor.shutdownNow();
		
		// figure out how many beacons the team has collected
		// figure out how many hitsInflicted and killsInflicted the team has
		int beacons = 0;
		int hits = 0;
		int killsInflicted = 0;
		int killsReceived = 0;
		int damageInflicted = 0;
		int damagedReceived = 0;
		int totalAssists = 0;
		int starsCollected = 0;
		for (Ship ship : teamShips) {
			beacons += ship.getNumBeacons();
			hits += ship.getHitsInflicted();
			killsInflicted += ship.getKillsInflicted();
			killsReceived += ship.getKillsReceived();
			totalAssists += ship.getTotalAssistsInflicted();
			damageInflicted += ship.getDamageInflicted();
			starsCollected += ship.getNumStars();
			
			// check team ships for how much damageInflicted they have received
			damagedReceived += ship.getDamageReceived();
		}
		// add in the stars to team resources.  Have to do it here because otherwise we end up with a circular
		// pointer in star and team and then stack overflows when we deepClone
		if (this.totalStarsCollected != starsCollected) {
			this.incrementAvailableResources(new ResourcePile(0, 0, 0, starsCollected - this.totalStarsCollected));
		}
		
		// check the bases for how much damageInflicted they have received
		for (UUID baseID : teamBaseIDs) {
			Base base = (Base) space.getObjectById(baseID);
			damagedReceived += base.getDamageReceived();
			killsReceived += base.getKillsReceived();
		}
		
		setTotalBeacons(beacons);
		this.totalKillsInflicted = killsInflicted;
		this.totalKillsReceived = killsReceived;
		this.totalAssistsInflicted = totalAssists;
		this.totalHitsInflicted = hits;
		this.totalDamageInflicted = damageInflicted;
		this.totalDamageReceived = damagedReceived;
		this.totalStarsCollected = starsCollected;
	}
	
	/**
	 * Ask the team if they want to purchase anything this turn.  You can only 
	 * purchase one item per turn and only if you have enough resourcesAvailable.
	 * 
	 * @param space
	 * @return
	 */
	public Map<UUID,PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space) {
        Map<UUID,PurchaseTypes> purchase = new HashMap<UUID,PurchaseTypes>();

		final Toroidal2DPhysics clonedSpace = space.deepClone();
		final Set<AbstractActionableObject> clonedActionableObjects = getTeamActionableObjectsClone(space);
		final PurchaseCosts clonedPurchaseCost = getPurchaseCostClone();
		final ResourcePile clonedResources = new ResourcePile(availableResources);
		
        // if the previous thread call hasn't finished, then just return default
		if (executor == null || executor.isTerminated()) {
			executor = Executors.newSingleThreadExecutor();
		} else {
			return purchase;
		}

		//System.out.println("exec " + executor.isTerminated());
        Future<Map<UUID,PurchaseTypes>> future = executor.submit(
        		new Callable<Map<UUID,PurchaseTypes>>(){
        			public Map<UUID,PurchaseTypes> call() throws Exception {
        				return teamClient.getTeamPurchases(clonedSpace, 
        						clonedActionableObjects, clonedResources , clonedPurchaseCost);
        			}
        		});
        
        try {
            //start
        	purchase = future.get(SpaceSettlersSimulator.TEAM_ACTION_TIMEOUT, TimeUnit.MILLISECONDS);
            //finished in time
        } catch (TimeoutException e) {
            //was terminated
        	//return empty map, don't buy anything
        	System.out.println(getTeamName() + " timed out in getTeamPurchases");
        	purchase = new HashMap<UUID,PurchaseTypes>();
        } catch (InterruptedException e) {
        	//we were interrupted (should not happen but lets be good programmers) 
        	//return empty map, don't buy anything
        	purchase = new HashMap<UUID,PurchaseTypes>();
			e.printStackTrace();
		} catch (ExecutionException e) {
			//the executor threw and exception (should not happen but lets be good programmers) 
        	//return empty map, don't buy anything
        	purchase = new HashMap<UUID,PurchaseTypes>();
			e.printStackTrace();
		} catch (RejectedExecutionException e) {
			System.err.println("exec" + executor.isTerminated());
			e.printStackTrace();
		} catch (Exception e) {
        	purchase = new HashMap<UUID,PurchaseTypes>();
			e.printStackTrace();
		}

        executor.shutdownNow();
        
        return purchase;
	}

	/**
	 * Clones the purchase cost map so the client can't modify it
	 * @return
	 */
	private PurchaseCosts getPurchaseCostClone() {
		return costToPurchase.deepCopy();
	}

	/**
	 * Get the weapons or power ups for the team this turn
	 * 
	 * @param space
	 * @return
	 */
	public Map<UUID, SpaceSettlersPowerupEnum> getTeamPowerups(Toroidal2DPhysics space) {
        Map<UUID, SpaceSettlersPowerupEnum> powerups = new HashMap<UUID,SpaceSettlersPowerupEnum>();

		final Toroidal2DPhysics clonedSpace = space.deepClone();
		final Set<AbstractActionableObject> clonedActionableObjects = getTeamActionableObjectsClone(space);
		
        // if the previous thread call hasn't finished, then just return default
		if (executor == null || executor.isTerminated()) {
			executor = Executors.newSingleThreadExecutor();
		} else {
			return powerups;
		}

		//System.out.println("exec " + executor.isTerminated());
        Future<Map<UUID,SpaceSettlersPowerupEnum>> future = executor.submit(
        		new Callable<Map<UUID,SpaceSettlersPowerupEnum>>(){
        			public Map<UUID,SpaceSettlersPowerupEnum> call() throws Exception {
        				return teamClient.getPowerups(clonedSpace, clonedActionableObjects);
        			}
        		});
        
        try {
            //start
        	powerups = future.get(SpaceSettlersSimulator.TEAM_ACTION_TIMEOUT, TimeUnit.MILLISECONDS);
            //finished in time
        } catch (TimeoutException e) {
            //was terminated
        	//return empty map, don't buy anything
        	System.out.println(getTeamName() + " timed out in getTeamPowerups");
        	powerups = new HashMap<UUID,SpaceSettlersPowerupEnum>();
        } catch (InterruptedException e) {
        	//we were interrupted (should not happen but lets be good programmers) 
        	//return empty map, don't buy anything
        	powerups = new HashMap<UUID,SpaceSettlersPowerupEnum>();
			e.printStackTrace();
		} catch (ExecutionException e) {
			//the executor threw and exception (should not happen but lets be good programmers) 
        	//return empty map, don't buy anything
			powerups = new HashMap<UUID,SpaceSettlersPowerupEnum>();
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error in agent.  Printing stack trace");
        	powerups = new HashMap<UUID,SpaceSettlersPowerupEnum>();
			e.printStackTrace();
		}

        executor.shutdownNow();
        
        return powerups;
	}

	/**
	 * Get the searches for the team this turn
	 * 
	 * @param space
	 * @return
	 */
	public Map<UUID, AbstractGameAgent> getTeamSearches(Toroidal2DPhysics space) {
        Map<UUID, AbstractGameAgent> searches = new HashMap<UUID,AbstractGameAgent>();

		final Toroidal2DPhysics clonedSpace = space.deepClone();
		final Set<AbstractActionableObject> clonedActionableObjects = getTeamActionableObjectsClone(space);
		
        // if the previous thread call hasn't finished, then just return default
		if (executor == null || executor.isTerminated()) {
			executor = Executors.newSingleThreadExecutor();
		} else {
			return searches;
		}

		//System.out.println("exec " + executor.isTerminated());
        Future<Map<UUID,AbstractGameAgent>> future = executor.submit(
        		new Callable<Map<UUID,AbstractGameAgent>>(){
        			public Map<UUID,AbstractGameAgent> call() throws Exception {
        				return teamClient.getGameSearch(clonedSpace, clonedActionableObjects);
        			}
        		});
        
        try {
            //start
        	searches = future.get(SpaceSettlersSimulator.TEAM_ACTION_TIMEOUT, TimeUnit.MILLISECONDS);
            //finished in time
        } catch (TimeoutException e) {
            //was terminated
        	//return empty map, don't buy anything
        	System.out.println(getTeamName() + " timed out in getTeamPowerups");
        	searches = new HashMap<UUID,AbstractGameAgent>();
        } catch (InterruptedException e) {
        	//we were interrupted (should not happen but lets be good programmers) 
        	//return empty map, don't buy anything
        	searches = new HashMap<UUID,AbstractGameAgent>();
			e.printStackTrace();
		} catch (ExecutionException e) {
			//the executor threw and exception (should not happen but lets be good programmers) 
        	//return empty map, don't buy anything
			searches = new HashMap<UUID,AbstractGameAgent>();
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error in agent.  Printing stack trace");
        	searches = new HashMap<UUID,AbstractGameAgent>();
			e.printStackTrace();
		}

        executor.shutdownNow();
        
        return searches;
	}

	
	/**
	 * Get any graphics the team client wants to draw
	 * 
	 * @return  
	 */
	public Set<SpacewarGraphics> getGraphics() {
        Set<SpacewarGraphics> graphics = new LinkedHashSet<SpacewarGraphics>();

        // if the previous thread call hasn't finished, then just return default
		if (executor == null || executor.isTerminated()) {
			executor = Executors.newSingleThreadExecutor();
		} else {
			return graphics;
		}

		Future<Set<SpacewarGraphics>> future = executor.submit(
        		new Callable<Set<SpacewarGraphics>>(){
        			public Set<SpacewarGraphics> call() throws Exception {
        				return teamClient.getGraphics();
        			}
        		});
        
        try {
            //start
        	graphics = future.get(SpaceSettlersSimulator.TEAM_GRAPHICS_TIMEOUT, TimeUnit.MILLISECONDS);
            //finished in time
        } catch (TimeoutException e) {
            //was terminated
        	//set empty array of graphics
        	System.out.println(getTeamName() + " timed out in getTeamGraphics");
        	graphics = new LinkedHashSet<SpacewarGraphics>();
        } catch (InterruptedException e) {
        	//we were interrupted (should not happen but lets be good programmers) 
        	//set empty array of graphics
        	graphics = new LinkedHashSet<SpacewarGraphics>();
			e.printStackTrace();
		} catch (ExecutionException e) {
			//the executor threw and exception (should not happen but lets be good programmers) 
			//set empty array of graphics
        	graphics = new LinkedHashSet<SpacewarGraphics>();
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error in agent.  Printing stack trace");
        	graphics = new LinkedHashSet<SpacewarGraphics>();
			e.printStackTrace();
		}

        executor.shutdownNow();
        
		return graphics;
	}

	/**
	 * If the client wants to take input from the keyboard, they override this
	 * inside the client to return a proper key listener.  It has to be pushed up
	 * here to avoid the exploits.
	 * 
	 * @return a valid KeyAdapter
	 */
	public KeyAdapter getKeyAdapter() {
		return teamClient.getKeyAdapter();
	}

	/**
	 * If the client wants to take input from the mouse, they override this inside
	 * the client to return a proper mouse listener.  It has to be pushed up
	 * here to avoid the exploits.
	 *
	 * @param mouseTransform takes an affine transformation used to scale the graphics: must be used to 
	 * scale mouse clicks 
	 * 
	 * @return a valid MouseAdapter
	 */
	public MouseAdapter getMouseAdapter(AffineTransform mouseTransform) {
		return teamClient.getMouseAdapter(mouseTransform);
	}
	
	
	/**
	 * Get the current team score
	 * @return
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Set the current team score
	 * @param score
	 */
	public void setScore(double score) {
		this.score = score;
	}
	
	
	/**
	 * Increases the amount of cores collected by the team by the specified amount.
	 * @param numCores
	 */
	public void incrementCoresCollected (int numCores) {
		this.totalCoresCollected += numCores;
	}
	
	/**
	 * This returns the amount of cores that the team has collected as an integer.
	 * @return int
	 */
	public int getTotalCoresCollected() { 
		return this.totalCoresCollected;
	}

	/**
	 * This returns the amount of stars that the team has collected as an integer.
	 * @return int
	 */
	public int getTotalStarsCollected() { 
		return this.totalStarsCollected;
	}

	/**
	 * Increases the amount of stars collected by the team by the specified amount.
	 * @param numStars
	 */
	public void incrementStarsCollected (int numStars) {
		this.totalStarsCollected += numStars;
	}


	/**
	 * Return the currently available resources
	 * 
	 * @return
	 */
	public ResourcePile getAvailableResources() {
		return availableResources;
	}

	/**
	 * Increment the team's total resources
	 * 
	 * @param newResources a list of new resources for the team
	 */
	public void incrementTotalResources(ResourcePile newResources) {
		totalResources.add(newResources);
	}

	/**
	 * Increment the team's currently available resources (this is decremented through buying elsewhere)
	 *  
	 * @param newResources resources to add to the team's available set
	 */
	public void incrementAvailableResources(ResourcePile newResources) {
		availableResources.add(newResources);
	}

	/**
	 * Decrement the team's currently available resources (for purchasing or stealing)
	 *  
	 * @param removeResources resources to remove from the available pile
	 */
	public void decrementAvailableResources(ResourcePile removeResources) {
		availableResources.subtract(removeResources);
	}

	
	public int getTotalBeaconsCollected() {
		return totalBeaconsCollected;
	}

	public void setTotalBeacons(int totalBeacons) {
		this.totalBeaconsCollected = totalBeacons;
	}
	
	public void incrementTotalBeacons() {
		this.totalBeaconsCollected++;
	}
	
	/**
	 * Return the total damageInflicted inflicted by this team
	 * @return total damageInflicted inflicted by all ships on this team
	 */
	public int getTotalDamageInflicted() {
		return totalDamageInflicted;
	}
	
	/**
	 * Return the total damageInflicted suffered by this team
	 * 
	 * @return total damageInflicted this team has suffered (bases and ships)
	 */
	public int getTotalDamageReceived() {
		return totalDamageReceived;
	}

	public int getTotalKillsInflicted() {
		return totalKillsInflicted;
	}

	public int getTotalAssistsInflicted() {
		return this.totalAssistsInflicted;
	}

	public int getTotalKillsReceived() {
		return totalKillsReceived;
	}

	public int getTotalHitsInflicted() {
		return totalHitsInflicted;
	}

	public String getLadderName() {
		return ladderName;
	}

	/**
	 * Called at the end of a simulation to cleanup the clients
	 */
	public void shutdownClients(Toroidal2DPhysics space) {
		teamClient.shutDown(space.deepClone());
	}

	/**
	 * Can the team afford the purchase?
	 * 
	 * @param type the type of purchase the team is considering
	 * @return true if the team can afford it and false otherwise
	 */
	public boolean canAfford(PurchaseTypes type) {
		return costToPurchase.canAfford(type, availableResources);
	}

	/**
	 * Return the total of the resources collected so far 
	 * (used for scoring, can be adjusted to be a weighted sum later)
	 * 
	 * @return the total of totalResources
	 */
	public double getSummedTotalResources() {
		return (double) totalResources.getTotal();
	}

	/**
	 * Return the individual total resources in the pile
	 * 
	 * @return
	 */
	public ResourcePile getTotalResources() {
		return totalResources;
	}

	/**
	 * Get the total flags collected by this team
	 * @return
	 */
	public int getTotalFlagsCollected() {
		return totalFlagsCollected;
	}

	/**
	 * Increment the total flags collected by this team
	 */
	public void incrementTotalFlagsCollected() {
		this.totalFlagsCollected++;
	}

	
}
