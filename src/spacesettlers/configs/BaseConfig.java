package spacesettlers.configs;

/**
 * Configuration for the bases 
 * @author amy
 */
public class BaseConfig {
	/**
	 * The name of the team to whom this base belongs
	 */
	String teamName;
	
	/**
	 * Optional: if we want the home-base to be in a fixed location, we can specify it. 
	 * It defaults to a randomly generated location.
	 */
	boolean fixedLocation = false;
	int x, y;
	
	/**
	 * Used to define starting locations for the team's ships (only used if fixedLocation mode)
	 */
	int boundingBoxULX, boundingBoxULY, boundingBoxLRX, boundingBoxLRY;


	public BaseConfig(String teamName) {
		super();
		this.teamName = teamName;
	}

	/**
	 * @return the teamName
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * Sets the name - used by the ladder
	 * @param teamName
	 */
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	/**
	 * True if it is a fixed location (defaults to false if not specified)
	 * @return
	 */
	public boolean isFixedLocation() {
		return fixedLocation;
	}

	/**
	 * Only used if the fixedLocation is true
	 * @return
	 */
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getBoundingBoxULX() {
		return boundingBoxULX;
	}

	public int getBoundingBoxULY() {
		return boundingBoxULY;
	}

	public int getBoundingBoxLRX() {
		return boundingBoxLRX;
	}

	public int getBoundingBoxLRY() {
		return boundingBoxLRY;
	}
	
	
	
	
}
