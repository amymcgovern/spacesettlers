package spacesettlers.ladder;

import java.util.Comparator;

public class TeamRecordComparator implements Comparator {

	@Override
	public int compare(Object obj1, Object obj2) {
		TeamRecord team1 = (TeamRecord) obj1;
		TeamRecord team2 = (TeamRecord) obj2;
		
		if (team1.getAverageScore() > team2.getAverageScore()) {
			return -1;
		} else if (team1.getAverageScore() < team2.getAverageScore()) {
			return 1;
		} else {
			return 0;
		}
		
	}

}
