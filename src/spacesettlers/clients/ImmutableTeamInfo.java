package spacesettlers.clients;

import spacesettlers.objects.resources.ResourcePile;

/**
 * Immutable class that holds the necessary team info to share with
 * other teams.
 * 
 * @author amy
 *
 */
public class ImmutableTeamInfo {
	/**
	 * current team score (set in the simulator, which knows how the team is being scored)
	 */
	double score;

	/**
	 * Name of the team 
	 */
	String teamName;
	
	/**
	 * available (unspent) resourcesAvailable from the asteroids and the total resourcesAvailable earned
	 */
	ResourcePile availableResources, totalResources;
	
	/**
	 * Keep track of the total beacons collected 
	 */
	int totalBeaconsCollected;
	
	/**
	 * Keep track of the total AiCores collected 
	 * herr0861 Edit
	 */
	int totalCoresCollected;
	
	/**
	 * Keep track of the total killsInflicted for the team (for the ladder, if used)
	 */
	int totalKillsInflicted;
	
	/**
	 * Keep track of the total hitsInflicted for the team (for the ladder, if used)
	 */
	int totalHitsInflicted;
	
	/**
	 * Keep track of the total damageInflicted this ship has dealt out
	 */
	int totalDamageInflicted;
	
	/**
	 * The damageInflicted this team has received
	 */
	int totalDamageReceived;
	
	/**
	 * Total kills received
	 */
	int totalKillsReceived;
	
	/**
	 * Total flags collected
	 */
	int totalFlagsCollected;
	
	
	/**
	 * The name that shows up in the ladder
	 */
	String ladderName;

	public ImmutableTeamInfo(Team team) {
		score = team.score;
		teamName = team.teamName;
		availableResources = new ResourcePile(team.availableResources);
		totalResources = new ResourcePile(team.totalResources);
		totalBeaconsCollected = team.totalBeaconsCollected;
		ladderName = team.ladderName;
		this.totalDamageInflicted = team.totalDamageInflicted;
		this.totalDamageReceived = team.totalDamageReceived;
		this.totalHitsInflicted = team.totalHitsInflicted;
		this.totalKillsInflicted = team.totalKillsInflicted;
		this.totalCoresCollected = team.totalCoresCollected;//herr0861 edit
		this.totalKillsReceived = team.totalKillsReceived;
		this.totalFlagsCollected = team.totalFlagsCollected;
		
	}

	public double getScore() {
		return score;
	}

	public String getTeamName() {
		return teamName;
	}

	public ResourcePile getAvailableResources() {
		return availableResources;
	}

	public ResourcePile getTotalResources() {
		return totalResources;
	}

	public int getTotalBeacons() {
		return totalBeaconsCollected;
	}

	public String getLadderName() {
		return ladderName;
	}

	public int getTotalBeaconsCollected() {
		return totalBeaconsCollected;
	}
	
	public int getTotalCoresCollected() {
		return totalCoresCollected;
	} //herr0861 edit

	public int getTotalKillsInflicted() {
		return totalKillsInflicted;
	}

	public int getTotalHitsInflicted() {
		return totalHitsInflicted;
	}

	public int getTotalDamageInflicted() {
		return totalDamageInflicted;
	}

	public int getTotalDamageReceived() {
		return totalDamageReceived;
	}

	public int getTotalKillsReceived() {
		return totalKillsReceived;
	}
	
	public int getTotalFlagsCollected() {
		return totalFlagsCollected;
	}
	
	
}
