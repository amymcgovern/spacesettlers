package spacesettlers.configs;

public class TeamClientConfig {
	/**
	 * fully qualified class name for the team
	 */
	String classname;
	
	/**
	 * RGB components of the team colors (used for all the ships)
	 */
	int teamColorRed, teamColorGreen, teamColorBlue;

	/**
	 * The name that shows up in the ladder
	 */
	String ladderName;

	/**
	 * The number of ships in the team.  Note, if this is bigger than the maximum number of allowable ships per team, it is ignored
	 */
	int numberInitialShipsInTeam;
	
	/**
	 * Optional:  if the agent needs to read in from a file, it can specify the path to it here 
	 */
	String knowledgeFile;

	/**
	 * @return the classname
	 */
	public String getClassname() {
		return classname;
	}

	/**
	 * @return the teamColorRed
	 */
	public int getTeamColorRed() {
		return teamColorRed;
	}

	/**
	 * @return the teamColorGreen
	 */
	public int getTeamColorGreen() {
		return teamColorGreen;
	}

	/**
	 * @return the teamColorBlue
	 */
	public int getTeamColorBlue() {
		return teamColorBlue;
	}

	/**
	 * @return the ladderName
	 */
	public String getLadderName() {
		return ladderName;
	}

	/**
	 * @return the numberInitialShipsInTeam
	 */
	public int getNumberInitialShipsInTeam() {
		return numberInitialShipsInTeam;
	}

	/**
	 * @return the knowledgeFile
	 */
	public String getKnowledgeFile() {
		return knowledgeFile;
	}
	
	
	

}
