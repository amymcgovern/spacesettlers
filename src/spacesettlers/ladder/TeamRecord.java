package spacesettlers.ladder;

import spacesettlers.clients.Team;

/**
 * Stores all the information needed for a team in the ladder
 * 
 * @author amy
 */
public class TeamRecord {
	int numGames;
	
	double totalScore;
	
	double averageScore;
	
	int totalBeacons;
	
	double averageBeacons;
	
	int totalResources;
	
	double averageResources;
	
	String teamName;
	
	public TeamRecord(String teamName) {
		this.teamName = teamName;
		reset();
		
	}
	
	public TeamRecord(Team team) {
		this.teamName = team.getLadderName();
		reset();
	}
	
	/**
	 * Resets the record
	 */
	public void reset() {
		numGames = 0;
		totalScore = 0;
		averageScore = 0;
		totalBeacons = 0;
		averageBeacons = 0;
		totalResources = 0;
		averageResources = 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((teamName == null) ? 0 : teamName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TeamRecord other = (TeamRecord) obj;
		if (teamName == null) {
			if (other.teamName != null)
				return false;
		} else if (!teamName.equals(other.teamName))
			return false;
		return true;
	}

	/**
	 * Update the team record
	 * @param team
	 */
	public void update(Team team) {
		numGames++;
		
		totalScore += team.getScore();
		averageScore = totalScore / numGames;
		
		totalResources += team.getSummedTotalResources();
		averageResources = totalResources / numGames;
		
		totalBeacons += team.getTotalBeaconsCollected();
		averageBeacons = totalBeacons / numGames;
	}

	public double getAverageScore() {
		return averageScore;
	}

	/**
	 * Fixes bug where students can put in JavaScript in their team name to rearrange the ladder.  fix provided by Christopher Fenner, January 2013
	 * @return
	 */
	public String getTeamName() {
		return teamName.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	public double getAverageBeacons() {
		return averageBeacons;
	}

	public double getAverageResources() {
		return averageResources;
	}

	
	

}
