package spacesettlers.simulator;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import com.martiansoftware.jsap.JSAPResult;
import com.thoughtworks.xstream.XStream;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.clients.ImmutableTeamInfo;
import spacesettlers.clients.Team;
import spacesettlers.clients.TeamClient;
import spacesettlers.configs.*;
import spacesettlers.gui.SpaceSettlersGUI;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Flag;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

public final class SpaceSettlersSimulator {

	/**
	 * Max time allowed in MILLISECONDS for a team to return actions
	 */
	public static int TEAM_ACTION_TIMEOUT = 300;

	/**
	 * Max time allowed in MILLISECONDS for a getMovement to return
	 */
	public static int MOVEMENT_TIMEOUT = 300;

	/**
	 * Max time allowed in MILLISECONDS for a endAction to return
	 */
	public static int TEAM_END_ACTION_TIMEOUT = 300;

	/**
	 * Max time allowed in MILLISECONDS for a graphic generation to return
	 */
	public static int TEAM_GRAPHICS_TIMEOUT = 200;

	/**
	 * Probability that new asteroids spawn on any given turn
	 */
	public final static double ASTEROID_SPAWN_PROBABILITY = 0.01;

	/**
	 * A list of the clients (e.g. agents who can control ships) in the simulator. It is indexed by team name.
	 */
	HashMap<String,TeamClient> clientMap;

	/**
	 * A list of all teams that can control agents (not indexed)
	 */
	Set<Team> teams;

	/**
	 * If Graphics are turned on, this is a pointer to the main part of the GUI
	 */
	SpaceSettlersGUI gui = null;

	/**
	 * Global random number generator for the game
	 */
	Random random;

	/**
	 * The configuration for this simulation
	 */
	SpaceSettlersConfig simConfig;

	/**
	 * The physics engine
	 */
	Toroidal2DPhysics simulatedSpace;

	/**
	 * Current timestep of the simulator
	 */
	int timestep;

	/**
	 * If debug mode is true, then only run single threaded
	 */
	boolean debug = false;

	/**
	 * True if the simulation is paused and false otherwise
	 */
	boolean isPaused = false;

	/**
	 * Time to sleep between graphics updates (in milliseconds, set to 40 for default but can be changed in the GUI)
	 */
	int graphicsSleep = 40;

	/**
	 * Create a simulator with the command line arguments already parsed.  
	 * @param args
	 * @throws SimulatorException 
	 */
	public SpaceSettlersSimulator(JSAPResult parserConfig) throws SimulatorException {
		// load in all the configuration
		simConfig = loadConfigFiles(parserConfig);

		teams = new LinkedHashSet<Team>();
		clientMap = new HashMap<String, TeamClient>();

		if (simConfig.getRandomSeed() == 0) {
			random = new Random();
		} else {
			random = new Random(simConfig.getRandomSeed());
		}

		// and use it to make agents and the world
		initializeSimulation(parserConfig);

		// see if debug mode is on
		if (parserConfig.getBoolean("debug")) {
			debug = true;

			TEAM_ACTION_TIMEOUT = Integer.MAX_VALUE;
			MOVEMENT_TIMEOUT = Integer.MAX_VALUE;
			TEAM_END_ACTION_TIMEOUT = Integer.MAX_VALUE;
			TEAM_GRAPHICS_TIMEOUT = Integer.MAX_VALUE;
		}

		// create the GUI after everything is created in the simulator
		System.out.println(this);
		createGUI(parserConfig);
	}

	/**
	 * Initialize from an existing config file for the simulator (used by the ladder) 
	 * @param simConfig
	 * @param parserConfig
	 * @throws SimulatorException
	 */
	public SpaceSettlersSimulator(SpaceSettlersConfig simConfig, JSAPResult parserConfig) throws SimulatorException {
		// load in all the configuration
		this.simConfig = simConfig;

		teams = new LinkedHashSet<Team>();
		clientMap = new HashMap<String, TeamClient>();

		if (simConfig.getRandomSeed() == 0) {
			random = new Random();
		} else {
			random = new Random(simConfig.getRandomSeed());
		}

		// and use it to make agents and the world
		initializeSimulation(parserConfig);

		// create the GUI if the user asked for it
		if (parserConfig.getBoolean("graphics")) {
			gui = new SpaceSettlersGUI(simConfig, this);
		}

		// see if debug mode is on
		if (parserConfig.getBoolean("debug")) {
			debug = true;

			TEAM_ACTION_TIMEOUT = Integer.MAX_VALUE;
			MOVEMENT_TIMEOUT = Integer.MAX_VALUE;
			TEAM_END_ACTION_TIMEOUT = Integer.MAX_VALUE;
			TEAM_GRAPHICS_TIMEOUT = Integer.MAX_VALUE;
		}
		// create the GUI after everything is created in the simulator
		System.out.println(this);
		createGUI(parserConfig);
	}

	/**
	 * Create the GUI after the simulator has been initialize
	 * 
	 * @param parserConfig
	 */
	public void createGUI(JSAPResult parserConfig) {
		// create the GUI if the user asked for it
		if (parserConfig.getBoolean("graphics")) {
			gui = new SpaceSettlersGUI(simConfig, this);
		}
	}


	/**
	 * Sleep so the gui can update (From Andy Fagg's tree code)
	 * @param i
	 */
	static public void mySleep(int i) {
		try{
			Thread.sleep(i);
		}catch(Exception e)
		{
			System.out.println(e);
		}
	}

	/**
	 * Initialize the simulation given a configuration file.  Creates all the objects.
	 * @throws SimulatorException 
	 */
	void initializeSimulation(JSAPResult parserConfig) throws SimulatorException {
		simulatedSpace = new Toroidal2DPhysics(simConfig);

		// place the beacons
		for (int b = 0; b < simConfig.getNumBeacons(); b++) {
			Beacon beacon = new Beacon(simulatedSpace.getRandomFreeLocation(random, Beacon.BEACON_RADIUS * 2));
			//System.out.println("New beacon at " + beacon.getPosition());
			simulatedSpace.addObject(beacon);
		}

		// place any fixed location asteroids
		FixedAsteroidConfig[] fixedAsteroidConfigs = simConfig.getFixedAsteroids();
		if (fixedAsteroidConfigs != null) {
			for (FixedAsteroidConfig fixedAsteroidConfig : fixedAsteroidConfigs) {
				Asteroid asteroid = createNewFixedAsteroid(fixedAsteroidConfig);
				simulatedSpace.addObject(asteroid);
			}
		}

		// place the asteroids
		RandomAsteroidConfig randomAsteroidConfig = simConfig.getRandomAsteroids();
		for (int a = 0; a < randomAsteroidConfig.getNumberInitialAsteroids(); a++) {
			Asteroid asteroid = createNewRandomAsteroid(randomAsteroidConfig);
			simulatedSpace.addObject(asteroid);
		}


		// create the clients
		for (HighLevelTeamConfig teamConfig : simConfig.getTeams()) {
			// ensure this team isn't a duplicate
			if (clientMap.containsKey(teamConfig.getTeamName())) {
				throw new SimulatorException("Error: duplicate team name " + teamConfig.getTeamName());
			}

			TeamClientConfig teamClientConfig = getTeamClientConfig(teamConfig, parserConfig.getString("configPath"));

			// grab the home base config for this team (to get starting locations as needed)
			BaseConfig thisBaseConfig = null;
			for (BaseConfig baseConfig : simConfig.getBases()) {
				String teamName = baseConfig.getTeamName();
				if (teamName.equalsIgnoreCase(teamConfig.getTeamName())) {
					thisBaseConfig = baseConfig;
					break;
				}
			}

			// now either use the base config for the default region radius or the teamConfig file
			if (thisBaseConfig != null && thisBaseConfig.isFixedLocation()) {
				teamConfig.setInitialRegionULX(thisBaseConfig.getBoundingBoxULX());
				teamConfig.setInitialRegionULY(thisBaseConfig.getBoundingBoxULY());
				teamConfig.setInitialRegionLRX(thisBaseConfig.getBoundingBoxLRX());
				teamConfig.setInitialRegionLRY(thisBaseConfig.getBoundingBoxLRY());
				System.out.println("Initial provided for team " + teamConfig.getTeamName() 
				+ "UL (x,y) = " + teamConfig.getInitialRegionULX() + ", " +
					teamConfig.getInitialRegionULY() + " LR (x,y) = " + 
				teamConfig.getInitialRegionLRX() + ", " +  
					teamConfig.getInitialRegionLRY());
			
			} else {
				// if the team doesn't provide default radiii and bases, create one
				if (teamConfig.getInitialRegionULX() == 0 && teamConfig.getInitialRegionLRX() == 0) {
					teamConfig.setInitialRegionULX(random.nextInt(simConfig.getWidth()));
					teamConfig.setInitialRegionLRX(teamConfig.getInitialRegionULX() + simConfig.getWidth() / 4);
					teamConfig.setInitialRegionULY(random.nextInt(simConfig.getHeight()));
					teamConfig.setInitialRegionLRY(teamConfig.getInitialRegionULX() + simConfig.getHeight() / 4);

					System.out.println("Initial location not provided for team " + teamConfig.getTeamName() 
						+ "...generating: UL (x,y) = " + teamConfig.getInitialRegionULX() + ", " +
							teamConfig.getInitialRegionULY() + " LR (x,y) = " + 
						teamConfig.getInitialRegionLRX() + ", " +
							teamConfig.getInitialRegionLRY());
				}
			}

			TeamClient teamClient = createTeamClient(teamConfig, teamClientConfig);
			
			// make the team inside the simulator for this team
			Team team = createTeam(teamConfig, teamClient, teamClientConfig);

			for (Ship ship : team.getShips()) {
				simulatedSpace.addObject(ship);
			}

			clientMap.put(teamConfig.getTeamName(), teamClient);
		}

		// make sure the base count matches the team count
		if (simConfig.getTeams().length != simConfig.getBases().length) {
			throw new SimulatorException("Error: You specified " + simConfig.getTeams().length + 
					" teams and " + simConfig.getBases().length + " bases.  They must match.");
		}

		// create the bases and ensure there is a base for each team
		for (BaseConfig baseConfig : simConfig.getBases()) {
			String teamName = baseConfig.getTeamName();
			if (!clientMap.containsKey(teamName)) {
				throw new SimulatorException("Error: base is listed as team " + teamName + " but there is no corresponding team");
			} 

			TeamClient teamClient = clientMap.get(teamName);

			// find the team config for this team
			HighLevelTeamConfig thisTeamConfig = null;
			for (HighLevelTeamConfig teamConfig : simConfig.getTeams()) {
				if (teamConfig.getTeamName().equalsIgnoreCase(teamName)) {
					thisTeamConfig = teamConfig;
					break;
				}

			}

			// make the location based on fixed or random
			Position baseLocation;

			if (baseConfig.isFixedLocation()) {
				baseLocation = new Position(baseConfig.getX(), baseConfig.getY());
			} else {
				// make the base in the region specified for this team
				// ensure bases are not created right next to asteroids (free by 3 * base_radius for now)
				baseLocation = simulatedSpace.getRandomFreeLocationInRegion(random, 3 * Base.BASE_RADIUS, 
						thisTeamConfig.getInitialRegionULX(), thisTeamConfig.getInitialRegionULY(), 
						thisTeamConfig.getInitialRegionLRX(), thisTeamConfig.getInitialRegionLRY());
			}


			// get this team as well as the client
			Team thisTeam = null;
			for (Team team : teams) {
				if (team.getTeamName().equalsIgnoreCase(teamName)) {
					thisTeam = team;
					break;
				}
			}

			Base base = new Base(baseLocation, baseConfig.getTeamName(), thisTeam, true);
			simulatedSpace.addObject(base);
			thisTeam.addBase(base);
		}
		
		
		/**
		 * If there are flags specified (presumably for capture the flag games), create them
		 * and match their color to their team.  Randomly choose their starting location
		 * from the specified set of starting locations.
		 */
		if (simConfig.getFlags() != null) {
			for (FlagConfig flagConfig : simConfig.getFlags()) {
				// get the right team to match the flag
				Team thisTeam = null;
				for (Team team : teams) {
					if (team.getTeamName().equalsIgnoreCase(flagConfig.getTeamName())) {
						thisTeam = team;
						break;
					}
				}
				int[] startX = flagConfig.getStartX();
				int[] startY = flagConfig.getStartY();
				
				Position[] startingPositions = new Position[startX.length];
				for (int i = 0; i < startX.length; i++) {
					startingPositions[i] = new Position(startX[i], startY[i]);
				}
				//System.out.println("Starting Locations are " + startingPositions);
				Position flagPosition = startingPositions[random.nextInt(startingPositions.length)];
				//System.out.println("Chosen location is " + flagPosition);
				Flag flag = new Flag(flagPosition, flagConfig.getTeamName(), thisTeam, startingPositions);
				
				simulatedSpace.addObject(flag);
			}
		}
		

	}

	/**
	 * Create a new fixed location asteroid following all rules of the config files
	 * 
	 * Fixed asteroids specify a x, y location and a radius.  They are always non-mineable. 
	 * 
	 * @param asteroidConfig
	 * @return
	 */
	private Asteroid createNewFixedAsteroid(FixedAsteroidConfig asteroidConfig) {
		boolean mineable = false;
		boolean moveable = false;

		int radius = asteroidConfig.getRadius();

		// create the asteroid (no fuels in it either)
		Asteroid asteroid = new Asteroid(new Position(asteroidConfig.getX(), asteroidConfig.getY()), 
				mineable, radius, moveable, 0, 0, 0);

		return asteroid;
	}

	/**
	 * Create a new asteroid following all rules of the config files
	 * 
	 * Asteroids can either be fixed location or randomly generated.  If they are
	 * fixed, they need x, y, and radius.  
	 * 
	 * @param asteroidConfig
	 * @return
	 */
	private Asteroid createNewRandomAsteroid(RandomAsteroidConfig asteroidConfig) {
		// choose if the asteroid is mine-able
		double prob = random.nextDouble();

		boolean mineable = false;
		if (prob < asteroidConfig.getProbabilityMineable()) {
			mineable = true;
		}

		// asteroids 
		// choose the radius randomly for random asteroids
		int radius = random.nextInt(Asteroid.MAX_ASTEROID_RADIUS - Asteroid.MIN_ASTEROID_RADIUS) + Asteroid.MIN_ASTEROID_RADIUS;

		// choose if the asteroid is moving or stationary
		prob = random.nextDouble();
		boolean moveable = false;
		if (prob < asteroidConfig.getProbabilityMoveable()) {
			moveable = true;
		}

		// choose the asteroid mixture
		double fuel = random.nextDouble() * asteroidConfig.getProbabilityFuelType();
		double water = random.nextDouble() * asteroidConfig.getProbabilityWaterType(); 
		double metals = random.nextDouble() * asteroidConfig.getProbabilityMetalsType(); 

		// renormalize so it all adds to 1
		double normalize = fuel + water + metals;
		fuel = fuel / normalize;
		water = water / normalize;
		metals = metals / normalize;

		// create the asteroid
		Asteroid asteroid = new Asteroid(simulatedSpace.getRandomFreeLocation(random, radius * 2), 
				mineable, radius, moveable, fuel, water, metals);

		if (asteroid.isMoveable()) {
			Vector2D randomMotion = Vector2D.getRandom(random, asteroidConfig.getMaxInitialVelocity());
			asteroid.getPosition().setTranslationalVelocity(randomMotion);
		}

		return asteroid;
	}

	/**
	 * Make the actual team (holds a pointer to the client) 
	 * 
	 * @param teamConfig
	 * @param teamClient
	 * @return
	 */
	public Team createTeam(HighLevelTeamConfig teamConfig, TeamClient teamClient, TeamClientConfig teamClientConfig) {
		// it succeeded!  Now make the team ships
		int numShips = Math.min(simConfig.getMaximumInitialShipsPerTeam(), teamClientConfig.getNumberInitialShipsInTeam());

		Team team = new Team(teamClient, teamClientConfig.getLadderName(), simConfig.getMaximumShipsPerTeam());

		for (int s = 0; s < numShips; s++) {
			// put the ships in the initial region for the team
			Position freeLocation = simulatedSpace.getRandomFreeLocationInRegion(random, Ship.SHIP_RADIUS * 2, 
					teamConfig.getInitialRegionULX(), teamConfig.getInitialRegionULY(), 
					teamConfig.getInitialRegionLRX(), teamConfig.getInitialRegionLRY());
			System.out.println("Starting ship for team " + team.getTeamName() + " in location " + freeLocation);
			Ship ship = new Ship(teamConfig.getTeamName(), team.getTeamColor(), freeLocation);
			team.addShip(ship);
		}

		teams.add(team);
		return team;
	}


	/**
	 * Make the team client from the configuration file
	 * 
	 * @param teamConfig
	 * @return
	 * @throws SimulatorException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public TeamClientConfig getTeamClientConfig(HighLevelTeamConfig teamConfig, String configPath) throws SimulatorException {
		String fileName = configPath + teamConfig.getConfigFile();

		XStream xstream = new XStream();
		xstream.alias("TeamClientConfig", TeamClientConfig.class);
		TeamClientConfig lowLevelTeamConfig;

		try { 
			lowLevelTeamConfig = (TeamClientConfig) xstream.fromXML(new File(fileName));
		} catch (Exception e) {
			throw new SimulatorException("Error parsing config team config file " + fileName + " at string " + e.getMessage());
		}

		return lowLevelTeamConfig;
	}



	/**
	 * Make the team client from the configuration file
	 * 
	 * @param teamConfig
	 * @return
	 * @throws SimulatorException 
	 */
	@SuppressWarnings("unchecked")
	public TeamClient createTeamClient(HighLevelTeamConfig teamConfig, TeamClientConfig teamClientConfig) throws SimulatorException {
		try {
			// make a team client of the class specified in the config file
			Class<TeamClient> newTeamClass = (Class<TeamClient>) Class.forName(teamClientConfig.getClassname());
			TeamClient newTeamClient = (TeamClient) newTeamClass.newInstance();

			Color teamColor = new Color(teamClientConfig.getTeamColorRed(), teamClientConfig.getTeamColorGreen(), 
					teamClientConfig.getTeamColorBlue());
			newTeamClient.setTeamColor(teamColor);
			newTeamClient.setTeamName(teamConfig.getTeamName());
			newTeamClient.setKnowledgeFile(teamClientConfig.getKnowledgeFile());
			newTeamClient.setRandom(random);
			newTeamClient.setMaxNumberShips(simConfig.getMaximumShipsPerTeam());
			newTeamClient.initialize(simulatedSpace.deepClone());
			return newTeamClient;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SimulatorException("Unable to make a new team client " + teamClientConfig.getClassname());
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new SimulatorException("Unable to create a new instance of class " + teamClientConfig.getClassname());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new SimulatorException("Unable to create a new instance of class " + teamClientConfig.getClassname());
		}

	}

	/**
	 * @return all objects in the simulator
	 */
	public Set<AbstractObject> getAllObjects() {
		return simulatedSpace.allObjects;
	}

	/**
	 * Load in the configuration files
	 * @throws SimulatorException 
	 * 
	 */
	public SpaceSettlersConfig loadConfigFiles(JSAPResult parserConfig) throws SimulatorException {
		String configFile = parserConfig.getString("configPath") + parserConfig.getString("simulatorConfigFile");

		XStream xstream = new XStream();
		xstream.alias("SpaceSettlersConfig", SpaceSettlersConfig.class);
		xstream.alias("HighLevelTeamConfig", HighLevelTeamConfig.class);
		xstream.alias("BaseConfig", BaseConfig.class);
		xstream.alias("AsteroidConfig", RandomAsteroidConfig.class);
		xstream.alias("FixedAsteroidConfig", FixedAsteroidConfig.class);
		xstream.alias("FlagConfig", FlagConfig.class);

		try { 
			simConfig = (SpaceSettlersConfig) xstream.fromXML(new File(configFile));
		} catch (Exception e) {
			throw new SimulatorException("Error parsing config file at string " + e.getMessage());
		}
		return simConfig;
	}

	/**
	 * Advance time one step
	 */
	void advanceTime() {
		// update the team info (to send into the space for use by other teams)
		updateTeamInfo();

		ExecutorService teamExecutor;
		if (debug) {
			teamExecutor = Executors.newSingleThreadExecutor();
		} else {
			teamExecutor = Executors.newCachedThreadPool();
		}
		Map<Team, Future<Map<UUID,AbstractAction>>> clientActionFutures = 
				new HashMap<Team, Future<Map<UUID,AbstractAction>>>();

		// get the actions from each team
		for (Team team : teams) {
			clientActionFutures.put(team, teamExecutor.submit(new AdvanceTimeCallable(team)));
		}

		for (Team team : teams) {
			Map<UUID, AbstractAction> teamActions;

			try {
				teamActions = clientActionFutures.get(team).get();
			} catch (InterruptedException e) {
				//something went wrong...return empty map
				teamActions = new HashMap<UUID, AbstractAction>();
			} catch (ExecutionException e) {
				//something went wrong...return empty map
				teamActions = new HashMap<UUID, AbstractAction>();
			} 


			// get the actions for each ship
			for (Ship ship : team.getShips()) {
				// if the client forgets to set an action, set it to DoNothing
				if (teamActions == null || !teamActions.containsKey(ship.getId())) {
					teamActions.put(ship.getId(), new DoNothingAction());
				}
				ship.setCurrentAction(teamActions.get(ship.getId()));
			}


		}

		teamExecutor.shutdown();

		// get the power ups being used on this turn
		Map<UUID, SpaceSettlersPowerupEnum> allPowerups = new HashMap<UUID, SpaceSettlersPowerupEnum>();
		for (Team team : teams) {
			Map<UUID, SpaceSettlersPowerupEnum> powerups = team.getTeamPowerups(simulatedSpace);
			if (powerups != null) {
				for (UUID key : powerups.keySet()) {
					// verify power ups belong to this team
					if (!team.isValidTeamID(key)) {
						continue;
					}

					// get the object and ensure it can have a power up on it
					AbstractObject swObject = simulatedSpace.getObjectById(key);
					if (!(swObject instanceof AbstractActionableObject)) {
						continue;
					}

					// verify that the object has the power up associated with it
					AbstractActionableObject actionableObject = (AbstractActionableObject) swObject;
					if (actionableObject.isValidPowerup(powerups.get(key))) {
						allPowerups.put(key, powerups.get(key));
					}
				}
			}
		}

		// now update the physics on all objects
		simulatedSpace.advanceTime(this.getTimestep(), allPowerups);

		// and end any actions inside the team
		for (Team team : teams) {
			team.getTeamMovementEnd(simulatedSpace);
		}

		// handle purchases at the end of a turn (so ships will have movements next turn)
		for (Team team : teams) {
			// now get purchases for the team
			Map<UUID, PurchaseTypes> purchases = team.getTeamPurchases(simulatedSpace);
			handlePurchases(team, purchases);
		}

		// cleanup and remove dead weapons
		simulatedSpace.cleanupDeadWeapons();

		// respawn any objects that died (and that should respawn - this includes Flags)
		final double asteroidMaxVelocity = simConfig.getRandomAsteroids().getMaxInitialVelocity();
		simulatedSpace.respawnDeadObjects(random, asteroidMaxVelocity);

		// spawn new asteroids with a small probability (up to the maximum number allowed)
		int maxAsteroids = simConfig.getRandomAsteroids().getMaximumNumberAsteroids();
		int numAsteroids = simulatedSpace.getAsteroids().size();
		if (numAsteroids < maxAsteroids) {
			if (random.nextDouble() < ASTEROID_SPAWN_PROBABILITY) {
				//System.out.println("Spawning a new asteroid");
				Asteroid asteroid = createNewRandomAsteroid(simConfig.getRandomAsteroids());
				simulatedSpace.addObject(asteroid);
			}
		}

		

		updateScores();

		//		for (Team team : teams) {
		//			for (Ship ship : team.getShips()) {
		//				System.out.println("Ship " + ship.getTeamName() + ship.getId() + " has resourcesAvailable " + ship.getMoney());
		//			}
		//		}
	}

	/**
	 * Update the team infomation that is sharable
	 */
	private void updateTeamInfo() {
		LinkedHashSet<ImmutableTeamInfo> teamInfo = new LinkedHashSet<ImmutableTeamInfo>();
		for (Team team : teams) {
			teamInfo.add(new ImmutableTeamInfo(team));
		}

		simulatedSpace.setTeamInfo(teamInfo);
	}

	/**
	 * Handle purchases for a team
	 * 
	 * @param team
	 * @param purchases
	 */
	private void handlePurchases(Team team,	Map<UUID, PurchaseTypes> purchases) {
		// handle teams that don't purchase
		if (purchases == null) {
			return;
		}

		for (UUID key : purchases.keySet()) {
			PurchaseTypes purchase = purchases.get(key);
			// skip the purchase if there isn't enough resourcesAvailable
			if (!team.canAfford(purchase)) {
				continue;
			}

			// get the object where the item is to be purchased (on on whom it is to be purchased)
			AbstractActionableObject purchasingObject = (AbstractActionableObject) simulatedSpace.getObjectById(key);

			// can only make purchases for your team
			if (!purchasingObject.getTeamName().equalsIgnoreCase(team.getTeamName())) {
				continue;
			}

			switch (purchase) {
			case BASE:
				// only purchase if this is a ship (can't buy a base next to a base)
				if (purchasingObject instanceof Ship) {
					Ship ship = (Ship) purchasingObject;

					// set the base just away from the ship (to avoid a collision)
					Position newPosition = simulatedSpace.getRandomFreeLocationInRegion(random, 
							Base.BASE_RADIUS, (int) ship.getPosition().getX(), 
							(int) ship.getPosition().getY(), (2 * ship.getRadius() + Base.BASE_RADIUS));

					// make the new base and add it to the lists
					Base base = new Base(newPosition, team.getTeamName(), team, false);
					simulatedSpace.addObject(base);
					team.addBase(base);
					// charge the team for the purchase
					team.decrementAvailableResources(team.getCurrentCost(purchase));
					team.updateCost(purchase);
				}
				break;
			case SHIP:
				// can only buy if there are enough ships
				if (team.getShips().size() >= team.getMaxNumberShips())
					break;

				// Ships can only be purchased near a base (which launches them)
				if (purchasingObject instanceof Base) {
					Base base = (Base) purchasingObject;

					// set the new ship just away from the base (to avoid a collision)
					Position newPosition = simulatedSpace.getRandomFreeLocationInRegion(random, 
							Ship.SHIP_RADIUS, (int) base.getPosition().getX(), 
							(int) base.getPosition().getY(), (2 * base.getRadius() + Ship.SHIP_RADIUS));

					// make the new ship and add it to the lists
					Ship ship = new Ship(team.getTeamName(), team.getTeamColor(), newPosition);
					simulatedSpace.addObject(ship);
					team.addShip(ship);
					// charge the team for the purchase
					team.decrementAvailableResources(team.getCurrentCost(purchase));
					team.updateCost(purchase);
				}

				break;

			case POWERUP_SHIELD:
				purchasingObject.addPowerup(SpaceSettlersPowerupEnum.TOGGLE_SHIELD);
				// charge the team for the purchase
				team.decrementAvailableResources(team.getCurrentCost(purchase));
				team.updateCost(purchase);
				System.out.println("Buying a shield");
				break;

			case POWERUP_EMP_LAUNCHER:
				// only purchase if this is a ship (can't buy a base next to a base)
				if (purchasingObject instanceof Ship) {
					purchasingObject.addPowerup(SpaceSettlersPowerupEnum.FIRE_EMP);
					// charge the team for the purchase
					team.decrementAvailableResources(team.getCurrentCost(purchase));
					team.updateCost(purchase);
					System.out.println("Buying a emp launcher");
				}
				break;

			case POWERUP_DOUBLE_BASE_HEALING_SPEED:
				// this can only be purchased on bases
				if (purchasingObject instanceof Base) {
					purchasingObject.addPowerup(SpaceSettlersPowerupEnum.DOUBLE_BASE_HEALING_SPEED);
					// charge the team for the purchase
					team.decrementAvailableResources(team.getCurrentCost(purchase));
					team.updateCost(purchase);
					System.out.println("Buying a healing doubler for a base");
				}
				break;

			case POWERUP_DOUBLE_MAX_ENERGY:
				purchasingObject.addPowerup(SpaceSettlersPowerupEnum.DOUBLE_MAX_ENERGY);
				// charge the team for the purchase
				team.decrementAvailableResources(team.getCurrentCost(purchase));
				team.updateCost(purchase);
				System.out.println("Buying a energy doubler");
				break;

			case POWERUP_DOUBLE_WEAPON_CAPACITY:
				purchasingObject.addPowerup(SpaceSettlersPowerupEnum.DOUBLE_WEAPON_CAPACITY);
				// charge the team for the purchase
				team.decrementAvailableResources(team.getCurrentCost(purchase));
				team.updateCost(purchase);
				System.out.println("Buying a weapons doubler");
				break;

			case NOTHING:
				break;

			default:
				break;
			}


		}



	}

	/**
	 * Updates the scores for the teams
	 */
	private void updateScores() {
		if (simConfig.getScoringMethod().equalsIgnoreCase("Resources")) {
			for (Team team : teams) {
				team.setScore(team.getSummedTotalResources());
			}
		} else if (simConfig.getScoringMethod().equalsIgnoreCase("Beacons")) {
			for (Team team : teams) {
				int beacons = 0;
				for (Ship ship : team.getShips()) {
					beacons += ship.getNumBeacons();
				}
				team.setScore(beacons);
			}
		} else if (simConfig.getScoringMethod().equalsIgnoreCase("Kills")) {
			for (Team team : teams) {
				team.setScore(team.getTotalKillsInflicted());
			}
		} else if (simConfig.getScoringMethod().equalsIgnoreCase("Hits")) {
			for (Team team : teams) {
				team.setScore(team.getTotalHitsInflicted());
			}
		} else if (simConfig.getScoringMethod().equalsIgnoreCase("Damage")) {
			for (Team team : teams) {
				team.setScore(team.getTotalDamageInflicted());
			}
		} else if (simConfig.getScoringMethod().equalsIgnoreCase("DamageCorrected")) {
			for (Team team : teams) {
				// not subtracting damage received because it is a negative number (inflicted is positive)
				team.setScore(1000* team.getTotalKillsInflicted() + team.getTotalDamageInflicted() + team.getTotalDamageReceived());
			}
		} else if (simConfig.getScoringMethod().equalsIgnoreCase("Flags")) {
			// this scores by the raw number of flags collected (competitive ladder)
			for (Team team : teams) {
				team.setScore(team.getTotalFlagsCollected());
			}
		} else if (simConfig.getScoringMethod().equalsIgnoreCase("TotalFlags")) {
			// this score sums the flags for the two sides (cooperative ladder)
			int totalFlags = 0;
			for (Team team : teams) {
				totalFlags += team.getTotalFlagsCollected();
			}
			for (Team team : teams) {
				team.setScore(totalFlags);
			}
		} else {
			System.err.println("Error: Scoring method " + simConfig.getScoringMethod() + " is not recognized.  Scores will all be 0.");
		}

	}

	/**
	 * Main control loop of the simulator
	 * @throws SimulatorException
	 */
	public void run() throws SimulatorException {
		if (gui != null) {
			gui.redraw();
		}

		// run the game loop until the maximum time has elapsed
		// if the pause is activated, just wait
		for (timestep = 0; timestep < simConfig.getSimulationSteps(); timestep++) {
			while (isPaused()) {
				mySleep(50);
			}

			advanceTime();

			if (gui != null) {
				gui.redraw();
				mySleep(graphicsSleep);
			}

			if (timestep % 5000 == 0) {
				System.out.println("On time step " + timestep);
				
				// print out the score every 5000 steps for debugging
                for (Team team : teams) {
                    String str = "Team: " + team.getLadderName() + " scored " + team.getScore();
                    System.out.println(str);
                }

			}
		}

		// update the team info (to send into the space for use by other teams)
		updateTeamInfo();

		// shutdown all the teams
		shutdownTeams();
	}

	/**
	 * Called after the simulation ends so the clients all cleanly shutdown
	 */
	public void shutdownTeams() {
		for (Team team : teams) {
			team.shutdownClients(simulatedSpace);
		}
	}

	/**
	 * Returns the current timestep
	 * @return
	 */
	public int getTimestep() {
		return timestep;
	}

	/**
	 * Returns the list of teams
	 * @return
	 */
	public Set<Team> getTeams() {
		return teams;
	}

	/**
	 * Inner class to do parallel actions from clients
	 * @author amy
	 *
	 */
	class AdvanceTimeCallable implements  Callable<Map<UUID,AbstractAction>>{
		private Team team;

		AdvanceTimeCallable(Team team){
			this.team = team;
		}

		public Map<UUID,AbstractAction> call() throws Exception {
			if(this.team != null){
				return this.team.getTeamMovementStart(simulatedSpace);
			}else{
				//something went wrong...lets return empty map
				return new HashMap<UUID, AbstractAction>();
			}

		}
	}

	/**
	 * Returns the physics engine (should only be called outside of the clients because they don't have access to this for security)
	 * @return
	 */
	public Toroidal2DPhysics getSimulatedSpace() {
		return simulatedSpace;
	}

	/**
	 * Is the simulator paused?
	 * @return
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Set the paused state (called from the GUI)
	 * @param isPaused
	 */
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	/**
	 * Set the sleep (called by the GUI)
	 * @return
	 */
	public int getGraphicsSleep() {
		return graphicsSleep;
	}

	/**
	 * Get the sleep (for the GUI)
	 * @param graphicsSleep
	 */
	public void setGraphicsSleep(int graphicsSleep) {
		this.graphicsSleep = graphicsSleep;
	}




}
