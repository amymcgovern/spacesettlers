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
	
	

	
}
