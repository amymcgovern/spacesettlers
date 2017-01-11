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
	int initialRegionCenterX, initialRegionCenterY, initialRegionRadius;
		
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

	/**
	 * @return the initialRegionCenterX
	 */
	public int getInitialRegionCenterX() {
		return initialRegionCenterX;
	}

	/**
	 * @return the initialRegionCenterY
	 */
	public int getInitialRegionCenterY() {
		return initialRegionCenterY;
	}

	/**
	 * @return the initialRegionRadius
	 */
	public int getInitialRegionRadius() {
		return initialRegionRadius;
	}

	public void setInitialRegionCenterX(int initialRegionCenterX) {
		this.initialRegionCenterX = initialRegionCenterX;
	}

	public void setInitialRegionCenterY(int initialRegionCenterY) {
		this.initialRegionCenterY = initialRegionCenterY;
	}

	public void setInitialRegionRadius(int initialRegionRadius) {
		this.initialRegionRadius = initialRegionRadius;
	}
	
	
	

}
