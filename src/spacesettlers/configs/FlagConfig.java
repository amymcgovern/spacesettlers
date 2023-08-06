package spacesettlers.configs;

import spacesettlers.utilities.Position;

/**
 * Config file for the flags for Capture the Flag
 * 
 * @author amy
 *
 */
public class FlagConfig {
	String teamName;
	
	/**
	 * Optional: if we want the the flag to be in a fixed location, we can specify it. 
	 * It defaults to a randomly generated location.
	 */
	boolean fixedLocation = false;

	int [] startX;
	int [] startY;

	public String getTeamName() {
		return teamName;
	}

	public int[] getStartX() {
		return startX;
	}

	public int[] getStartY() {
		return startY;
	}

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

	

	
}
