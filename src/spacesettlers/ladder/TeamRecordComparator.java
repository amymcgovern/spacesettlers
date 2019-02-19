package spacesettlers.ladder;

import java.util.Comparator;

public class TeamRecordComparator implements Comparator<TeamRecord> {

	@Override
	public int compare(TeamRecord team1, TeamRecord team2) {
		
		if (team1.getAverageScore() > team2.getAverageScore()) {
			return -1;
		} else if (team1.getAverageScore() < team2.getAverageScore()) {
			return 1;
		} else {
			return 0;
		}
		
	}

}
