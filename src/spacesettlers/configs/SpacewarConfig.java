package spacesettlers.configs;

/**
 * The simulator configuration file read in from xstream 
 * 
 * @author amy
 */
public class SpacewarConfig {
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
	 * Configuration for the asteroids
	 */
	AsteroidConfig asteroids;
	
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
	public AsteroidConfig getAsteroids() {
		return asteroids;
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
