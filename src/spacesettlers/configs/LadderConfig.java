package spacesettlers.configs;

public class LadderConfig {
	/**
	 * The team configuration for each team
	 */
	HighLevelTeamConfig[] staticTeams, variableTeams;

	/**
	 * The maximum number of variable (e.g. can change each match) teams in a game
	 */
	int maximumNumberVariableTeams;
	
	/**
	 * The number of times each match is repeated
	 */
	int numRepeatMatches;
	
	/**
	 * Number of threads
	 */
	int numThreads;
	
	/**
	 * Write out the results to this file
	 */
	String outputFileName;

	public void setVariableTeams(HighLevelTeamConfig[] variableTeams) {
		this.variableTeams = variableTeams;
	}

	public void setMaximumNumberVariableTeams(int maximumNumberVariableTeams) {
		this.maximumNumberVariableTeams = maximumNumberVariableTeams;
	}
	
	public int getNumThreads() {
		return numThreads;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setStaticTeams(HighLevelTeamConfig[] staticTeams) {
		this.staticTeams = staticTeams;
	}

	public HighLevelTeamConfig[] getStaticTeams() {
		return staticTeams;
	}

	public HighLevelTeamConfig[] getVariableTeams() {
		return variableTeams;
	}

	public int getMaximumNumberVariableTeams() {
		return maximumNumberVariableTeams;
	}

	public int getNumRepeatMatches() {
		return numRepeatMatches;
	}

	/**
	 * Make the player names automatically generated for the ladder unique
	 */
	public void makePlayerNamesUnique() {
		int id = 1;
		for (HighLevelTeamConfig varTeam : variableTeams) {
			if (varTeam.getTeamName().equalsIgnoreCase("Player")) {
				varTeam.setTeamName(varTeam.getTeamName() + id);
				id++;
			}
		}
	}
}
