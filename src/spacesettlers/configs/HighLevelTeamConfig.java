package spacesettlers.configs;

/**
 * Team configuration information read in from xstream
 * 
 * @author amy
 */
public class HighLevelTeamConfig {
	/**
	 * The name of the team (will be used to match other items in the simulator)
	 */
	String teamName;
	
	/**
	 * The name (and path) of the configuration file
	 */
	String configFile;

	/**
	 * Bounding box for the initialization of the team
	 */
	int initialRegionULX, initialRegionULY, initialRegionLRX, initialRegionLRY;
		
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	/**
	 * @return the teamName
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * @return the configFile
	 */
	public String getConfigFile() {
		return configFile;
	}


	public int getInitialRegionULX() {
		return initialRegionULX;
	}

	public int getInitialRegionULY() {
		return initialRegionULY;
	}

	public int getInitialRegionLRX() {
		return initialRegionLRX;
	}

	public int getInitialRegionLRY() {
		return initialRegionLRY;
	}

	public void setInitialRegionULX(int initialRegionULX) {
		this.initialRegionULX = initialRegionULX;
	}

	public void setInitialRegionULY(int initialRegionULY) {
		this.initialRegionULY = initialRegionULY;
	}

	public void setInitialRegionLRX(int initialRegionLRX) {
		this.initialRegionLRX = initialRegionLRX;
	}

	public void setInitialRegionLRY(int initialRegionLRY) {
		this.initialRegionLRY = initialRegionLRY;
	}

	/**
	 * Nice printout for debugging
	 */
	public String toString() {
		String str = "Team name: " + this.teamName + " From file " + this.configFile;
		return str;
	}
	
	

}
