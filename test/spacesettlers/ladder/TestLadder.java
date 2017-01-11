package spacesettlers.ladder;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spacesettlers.configs.HighLevelTeamConfig;
import spacesettlers.configs.LadderConfig;
import spacesettlers.ladder.Ladder;

public class TestLadder {
	LadderConfig ladderConfig;
	HighLevelTeamConfig[] variableTeamConfig, staticTeamConfig;
	Ladder ladder;
	
	@Before
	public void setUp() throws Exception {
		ladderConfig = new LadderConfig();
		variableTeamConfig = new HighLevelTeamConfig[3];
		variableTeamConfig[0] = new HighLevelTeamConfig();
		variableTeamConfig[0].setTeamName("A");

		variableTeamConfig[1] = new HighLevelTeamConfig();
		variableTeamConfig[1].setTeamName("B");

		variableTeamConfig[2] = new HighLevelTeamConfig();
		variableTeamConfig[2].setTeamName("C");
		
		ladderConfig.setVariableTeams(variableTeamConfig);

		staticTeamConfig = new HighLevelTeamConfig[2];
		staticTeamConfig[0] = new HighLevelTeamConfig();
		staticTeamConfig[0].setTeamName("S1");

		staticTeamConfig[1] = new HighLevelTeamConfig();
		staticTeamConfig[1].setTeamName("S2");
		
		ladderConfig.setStaticTeams(staticTeamConfig);
		
		ladder = new Ladder(ladderConfig);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetClientsForAllMatchesMax1() {
		ladderConfig.setMaximumNumberVariableTeams(1);

		// set up the expected results
		ArrayList<HighLevelTeamConfig[]> expectedClients = new ArrayList<HighLevelTeamConfig[]>();
		
		HighLevelTeamConfig[] result = new HighLevelTeamConfig[3];
		result[0] = new HighLevelTeamConfig();
		result[0].setTeamName("A");
		result[1] = new HighLevelTeamConfig();
		result[1].setTeamName("S1");
		result[2] = new HighLevelTeamConfig();
		result[2].setTeamName("S2");
		expectedClients.add(result);

		result = new HighLevelTeamConfig[3];
		result[0] = new HighLevelTeamConfig();
		result[0].setTeamName("B");
		result[1] = new HighLevelTeamConfig();
		result[1].setTeamName("S1");
		result[2] = new HighLevelTeamConfig();
		result[2].setTeamName("S2");
		expectedClients.add(result);

		result = new HighLevelTeamConfig[3];
		result[0] = new HighLevelTeamConfig();
		result[0].setTeamName("C");
		result[1] = new HighLevelTeamConfig();
		result[1].setTeamName("S1");
		result[2] = new HighLevelTeamConfig();
		result[2].setTeamName("S2");
		expectedClients.add(result);

		// get the results
		ArrayList<HighLevelTeamConfig[]> variableClients = ladder.getAllClientsForAllMatches();
		
		// make sure the list sizes are the same
		assertEquals(expectedClients.size(), variableClients.size(), 0);
		
		// now make sure the content of each list is the same
		for (int vcIndex = 0; vcIndex < variableClients.size(); vcIndex++) { 
			HighLevelTeamConfig[] varClient = variableClients.get(vcIndex);
			HighLevelTeamConfig[] expectedClient = expectedClients.get(vcIndex);
			
			for (int i = 0; i < varClient.length; i++) {
				assertTrue(varClient[i].getTeamName().equalsIgnoreCase(expectedClient[i].getTeamName()));
			}
		}
		
	}
	
	@Test
	public void testGetClientsForAllMatchesMax2() {
		ladderConfig.setMaximumNumberVariableTeams(2);

		// set up the expected results
		ArrayList<HighLevelTeamConfig[]> expectedClients = new ArrayList<HighLevelTeamConfig[]>();
		
		HighLevelTeamConfig[] result = new HighLevelTeamConfig[4];
		result[0] = new HighLevelTeamConfig();
		result[0].setTeamName("A");
		result[1] = new HighLevelTeamConfig();
		result[1].setTeamName("B");
		result[2] = new HighLevelTeamConfig();
		result[2].setTeamName("S1");
		result[3] = new HighLevelTeamConfig();
		result[3].setTeamName("S2");
		expectedClients.add(result);

		result = new HighLevelTeamConfig[4];
		result[0] = new HighLevelTeamConfig();
		result[0].setTeamName("A");
		result[1] = new HighLevelTeamConfig();
		result[1].setTeamName("C");
		result[2] = new HighLevelTeamConfig();
		result[2].setTeamName("S1");
		result[3] = new HighLevelTeamConfig();
		result[3].setTeamName("S2");
		expectedClients.add(result);

		result = new HighLevelTeamConfig[4];
		result[0] = new HighLevelTeamConfig();
		result[0].setTeamName("B");
		result[1] = new HighLevelTeamConfig();
		result[1].setTeamName("C");
		result[2] = new HighLevelTeamConfig();
		result[2].setTeamName("S1");
		result[3] = new HighLevelTeamConfig();
		result[3].setTeamName("S2");
		expectedClients.add(result);

		// get the results
		ArrayList<HighLevelTeamConfig[]> variableClients = ladder.getAllClientsForAllMatches();
		
		// make sure the list sizes are the same
		assertEquals(expectedClients.size(), variableClients.size(), 0);
		
		// now make sure the content of each list is the same
		for (int vcIndex = 0; vcIndex < variableClients.size(); vcIndex++) { 
			HighLevelTeamConfig[] varClient = variableClients.get(vcIndex);
			HighLevelTeamConfig[] expectedClient = expectedClients.get(vcIndex);
			
			for (int i = 0; i < varClient.length; i++) {
				assertTrue(varClient[i].getTeamName().equalsIgnoreCase(expectedClient[i].getTeamName()));
			}
		}
		
	}

	
	@Test
	public void testGetClientsForAllMatchesMax3() {
		ladderConfig.setMaximumNumberVariableTeams(3);

		// set up the expected results
		ArrayList<HighLevelTeamConfig[]> expectedClients = new ArrayList<HighLevelTeamConfig[]>();
		
		HighLevelTeamConfig[] result = new HighLevelTeamConfig[5];
		result[0] = new HighLevelTeamConfig();
		result[0].setTeamName("A");
		result[1] = new HighLevelTeamConfig();
		result[1].setTeamName("B");
		result[2] = new HighLevelTeamConfig();
		result[2].setTeamName("C");
		result[3] = new HighLevelTeamConfig();
		result[3].setTeamName("S1");
		result[4] = new HighLevelTeamConfig();
		result[4].setTeamName("S2");
		expectedClients.add(result);

		// get the results
		ArrayList<HighLevelTeamConfig[]> variableClients = ladder.getAllClientsForAllMatches();
		
		// make sure the list sizes are the same
		assertEquals(expectedClients.size(), variableClients.size(), 0);
		
		// now make sure the content of each list is the same
		for (int vcIndex = 0; vcIndex < variableClients.size(); vcIndex++) { 
			HighLevelTeamConfig[] varClient = variableClients.get(vcIndex);
			HighLevelTeamConfig[] expectedClient = expectedClients.get(vcIndex);
			
			for (int i = 0; i < varClient.length; i++) {
				assertTrue(varClient[i].getTeamName().equalsIgnoreCase(expectedClient[i].getTeamName()));
			}
		}
	}

	
	@Test
	public void testgetIndicesFor2Choose1() {
		ArrayList<int[]> expectedResults = new ArrayList<int[]>();
		
		expectedResults.add(new int[]{0});
		expectedResults.add(new int[]{1});
		
		ArrayList<int[]> results = ladder.getIndicesForNChooseK(2, 1);
		
		// ensure the results are the same size
		assertEquals(expectedResults.size(), results.size(), 0);
		
		for (int i = 0; i < expectedResults.size(); i++) {
			int[] expected = expectedResults.get(i);
			int[] observed = results.get(i);
			
			for (int j = 0; j < expected.length; j++) {
				assertEquals(expected[j], observed[j], 0);
			}
		}
	}
	
	
	@Test
	public void testgetIndicesFor1Choose1() {
		ArrayList<int[]> expectedResults = new ArrayList<int[]>();
		
		expectedResults.add(new int[]{0});
		
		ArrayList<int[]> results = ladder.getIndicesForNChooseK(1, 1);
		
		// ensure the results are the same size
		assertEquals(expectedResults.size(), results.size(), 0);
		
		for (int i = 0; i < expectedResults.size(); i++) {
			int[] expected = expectedResults.get(i);
			int[] observed = results.get(i);
			
			for (int j = 0; j < expected.length; j++) {
				assertEquals(expected[j], observed[j], 0);
			}
		}
	}

	@Test
	public void testgetIndicesFor0Choose0() {
		ArrayList<int[]> expectedResults = new ArrayList<int[]>();
		
		ArrayList<int[]> results = ladder.getIndicesForNChooseK(0, 0);
		
		// ensure the results are the same size
		assertEquals(expectedResults.size(), results.size(), 0);
		
		for (int i = 0; i < expectedResults.size(); i++) {
			int[] expected = expectedResults.get(i);
			int[] observed = results.get(i);
			
			for (int j = 0; j < expected.length; j++) {
				assertEquals(expected[j], observed[j], 0);
			}
		}
	}

	
	@Test
	public void testgetIndicesFor4Choose3() {
		ArrayList<int[]> expectedResults = new ArrayList<int[]>();
		
		expectedResults.add(new int[]{0, 1, 2});
		expectedResults.add(new int[]{0, 1, 3});
		expectedResults.add(new int[]{0, 2, 3});
		expectedResults.add(new int[]{1, 2, 3});
		
		ArrayList<int[]> results = ladder.getIndicesForNChooseK(4, 3);
		
		// ensure the results are the same size
		assertEquals(expectedResults.size(), results.size(), 0);
		
		for (int i = 0; i < expectedResults.size(); i++) {
			int[] expected = expectedResults.get(i);
			int[] observed = results.get(i);
			
			for (int j = 0; j < expected.length; j++) {
				assertEquals(expected[j], observed[j], 0);
			}
		}
	}

	
	@Test
	public void testNChooseK() {
		assertEquals(ladder.calculateNChooseK(2, 1), 2, 0);
		assertEquals(ladder.calculateNChooseK(4, 3), 4, 0);
		assertEquals(ladder.calculateNChooseK(1, 1), 1, 0);
		assertEquals(ladder.calculateNChooseK(0, 0), 0, 0);
	}

}
