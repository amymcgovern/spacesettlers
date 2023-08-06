package spacesettlers.configs;

/**
 * The simulator configuration file read in from xstream 
 * 
 * @author amy
 */
public class SpaceSettlersConfig {
	/**
	 * Width and height of the virtual environment
	 */
	int height, width;

	/**
	 * Size of a simulation time step for the physics engine
	 */
	double simulationTimeStep;
	
	/**
	 * Number of simulated steps taken within the simulator
	 */
	int simulationSteps;
	
	/**
	 * Initial random seed.
	 */
	long randomSeed;
	
	/**
	 * Number of beacons in the spacewar environment
	 */
	int numBeacons;
	
	/**
	 * Number of stars in the spacewar environment
	 */
	int numStars;

	/**
	 * Maximum number of ships in a team (can be 1)
	 */
	int maximumShipsPerTeam;
	
	/**
	 * Maximum number of initial ships in a team (can be 1)
	 */
	int maximumInitialShipsPerTeam;


	/**
	 * The team configuration for each team
	 */
	HighLevelTeamConfig[] teams;
	
	/**
	 * The base configuration for each base
	 */
	BaseConfig[] bases;
	
	/**
	 * Configuration for the random asteroids
	 */
	RandomAsteroidConfig randomAsteroids;
	
	/**
	 * Configuration for the fixed asteroids (if any)
	 */
	FixedAsteroidConfig[] fixedAsteroids;
	
	/**
	 * Used for capture the flag (can be not specified in other environments)
	 */
	FlagConfig[] flags;
	
	
	/**
	 * Scoring method for this game
	 */
	String scoringMethod;
	
	/**
	 * @return the height of the simulation environment.  This is measured in pixels.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width of the simulation environment.  This is measured in pixels.
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * @return the simulationTimeStep
	 */
	public double getSimulationTimeStep() {
		return simulationTimeStep;
	}

	/**
	 * @return the simulationSteps
	 */
	public int getSimulationSteps() {
		return simulationSteps;
	}

	/**
	 * @return the randomSeed
	 */
	public long getRandomSeed() {
		return randomSeed;
	}

	/**
	 * @return the numBeacons
	 */
	public int getNumBeacons() {
		return numBeacons;
	}

	/**
	 * @return the numStars
	 */
	public int getNumStars() {
		return numStars;
	}

	/**
	 * @return the maximumShipsPerTeam
	 */
	public int getMaximumShipsPerTeam() {
		return maximumShipsPerTeam;
	}

	public int getMaximumInitialShipsPerTeam() {
		return maximumInitialShipsPerTeam;
	}

	/**
	 * @return the teams
	 */
	public HighLevelTeamConfig[] getTeams() {
		return teams;
	}

	/**
	 * @return the bases
	 */
	public BaseConfig[] getBases() {
		return bases;
	}

	/**
	 * @return the asteroids
	 */
	public RandomAsteroidConfig getRandomAsteroids() {
		return randomAsteroids;
	}

	/**
	 * Return the fixed location asteroids (if any)
	 * @return
	 */
	public FixedAsteroidConfig[] getFixedAsteroids() {
		return fixedAsteroids;
	}
	
	
	/**
	 * Return the flag configs
	 * @return
	 */

	public FlagConfig[] getFlags() {
		return flags;
	}

	/**
	 * 
	 * @return the scoring method
	 */
	public String getScoringMethod() {
		return scoringMethod;
	}

	public void setTeams(HighLevelTeamConfig[] teams) {
		this.teams = teams;
	}

	public void setBases(BaseConfig[] bases) {
		this.bases = bases;
	}
}
