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
	
	
	
}
