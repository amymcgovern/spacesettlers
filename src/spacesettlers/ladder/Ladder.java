package spacesettlers.ladder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import spacesettlers.clients.Team;
import spacesettlers.configs.*;
import spacesettlers.simulator.SimulatorException;
import spacesettlers.simulator.SpaceSettlersSimulator;

import com.martiansoftware.jsap.JSAPResult;
import com.thoughtworks.xstream.XStream;

/**
 * Runs the ladder
 * @author amy
 *
 */
public class Ladder {
	LadderConfig ladderConfig;

	SpaceSettlersSimulator simulator;

	SpaceSettlersConfig simConfig;

	JSAPResult parserConfig;

	HashMap <String, TeamRecord> ladderResultsMap;
	
	ArrayList<TeamRecord> sortedLadderResults;
	
	ArrayList<String> ladderOutputString;
	
	ExecutorService threadPool;

	/**
	 * Make a new ladder
	 * @param config
	 * @throws SimulatorException 
	 */
	public Ladder(JSAPResult parserConfig) throws SimulatorException {
		loadConfigFiles(parserConfig);
		this.parserConfig = parserConfig;

		ladderResultsMap = new HashMap<String, TeamRecord>();
		ladderOutputString = new ArrayList<String>();
	}

	/**
	 * For the unit test
	 */
	public Ladder(LadderConfig ladderConfig) {
		this.ladderConfig = ladderConfig;
	}

	/**
	 * Load in the configuration files
	 * @throws SimulatorException 
	 * 
	 */
	public void loadConfigFiles(JSAPResult parserConfig) throws SimulatorException {
		// load in the simulator config file
		String configFile = parserConfig.getString("configPath") + parserConfig.getString("simulatorConfigFile");

		XStream xstream = new XStream();
		xstream.alias("SpaceSettlersConfig", SpaceSettlersConfig.class);
		xstream.alias("HighLevelTeamConfig", HighLevelTeamConfig.class);
		xstream.alias("BaseConfig", BaseConfig.class);
		xstream.alias("AsteroidConfig", RandomAsteroidConfig.class);
		xstream.alias("FixedAsteroidConfig", FixedAsteroidConfig.class);
		xstream.alias("FlagConfig", FlagConfig.class);
		xstream.allowTypesByRegExp(new String[] { ".*" });


		try { 
			simConfig = (SpaceSettlersConfig) xstream.fromXML(new File(configFile));
		} catch (Exception e) {
			throw new SimulatorException("Error parsing config file at string " + e.getMessage());
		}

		// load in the ladder config file
		configFile = parserConfig.getString("configPath") + parserConfig.getString("ladderConfigFile");

		xstream = new XStream();
		xstream.alias("LadderConfig", LadderConfig.class);
		xstream.alias("HighLevelTeamConfig", HighLevelTeamConfig.class);
		xstream.allowTypesByRegExp(new String[] { ".*" });
		
		try { 
			ladderConfig = (LadderConfig) xstream.fromXML(new File(configFile));
			
			ladderConfig.makePlayerNamesUnique();
			
			
		} catch (Exception e) {
			throw new SimulatorException("Error parsing config file at string " + e.getMessage());
		}


	}

	/**
	 * Runs the ladder for the specified number of games
	 * @throws SimulatorException 
	 * @throws InterruptedException 
	 * @throws ExecutionException 
	 */
	public void run() throws SimulatorException, InterruptedException, ExecutionException {
		ArrayList<HighLevelTeamConfig[]>clientsPerMatch = getAllClientsForAllMatches();
		
		int numGames = clientsPerMatch.size() * ladderConfig.getNumRepeatMatches();
		System.out.println("Ladder will run " + numGames + " games");
		System.out.println("Variable teams are: ");
		for (HighLevelTeamConfig team : ladderConfig.getVariableTeams()) {
			System.out.println(team);
		}
		int gameIndex = 0;
		
		// create the thread pool
		int numThreads = Math.max(ladderConfig.getNumThreads(), 1);
		
		threadPool = Executors.newFixedThreadPool(numThreads);
		ArrayList<Future<LadderSingleGame>> ladderResults = new ArrayList<Future<LadderSingleGame>>();

		for (int repeat = 0; repeat < ladderConfig.getNumRepeatMatches(); repeat++) {
			for (HighLevelTeamConfig[] teamsForMatch : clientsPerMatch) {
				gameIndex++;

				// setup a new single game
				LadderSingleGame newGame = new LadderSingleGame(parserConfig);
				newGame.initializeGame(teamsForMatch, gameIndex, numGames);
				
				// run the game as threads are available
				Future<LadderSingleGame> gameResult = threadPool.submit(newGame);
				ladderResults.add(gameResult);
			}
		}
		
		// loop through all the games and see if they are done (no blocking)
		int numFinished = 0;
		
		while (numFinished != numGames) {
			numFinished = 0;
			
			// loop through all the games
			for (Future<LadderSingleGame> future : ladderResults) {
				if (future.isDone()) {
					numFinished++;
				} else {
					break;
				}
			}
			
			// sleep for a bit if not all the games are done
			if (numFinished != numGames) {
				Thread.sleep(Math.max(1000, 100 * (numGames - numFinished)));
			}
		}		

		System.out.println("Finished running games!  Now grabbing results from the threads.");
		
		// the games are over so combine their records and then sort them
		for (Future<LadderSingleGame> future : ladderResults) {
			LadderSingleGame game = future.get();
			
			// output the results of the match
			ladderOutputString.addAll(game.getLadderOutputString());
			//System.out.println("Output string is ");
			//System.out.println(game.getLadderOutputString());
			
			// merge the records for each game into the global list by team
			HashMap <String, Team> gameResults = game.getLadderResultsMap();
			for (String teamName : gameResults.keySet()) {
				Team thisTeam = gameResults.get(teamName);
				
				if (!ladderResultsMap.containsKey(teamName)) {
					ladderResultsMap.put(teamName, new TeamRecord(thisTeam));
				}
				ladderResultsMap.get(teamName).update(thisTeam);
			}
			//System.out.println(game.getLadderResultsMap());
		}
		
		// now sort the final results
		sortedLadderResults = new ArrayList<TeamRecord>();
		for (TeamRecord record : ladderResultsMap.values()) {
			sortedLadderResults.add(record);
		}
		//System.out.println("Sorted results are " + sortedLadderResults);
		Collections.sort(sortedLadderResults, new TeamRecordComparator());
		System.out.println("Overall team order: ");
		for (TeamRecord record : sortedLadderResults) {
			System.out.println(record.getTeamName() + " average score " + record.getAverageScore());
		}
	}


	/**
	 * Return the results of the ladder
	 * @return
	 */
	public ArrayList<TeamRecord> getSortedLadderResults() {
		return sortedLadderResults;
	}

	/**
	 * Takes the list of variable  and static clients and the number to play per game and returns the full
	 * list of combinations
	 * 
	 * @return
	 */
	protected ArrayList<HighLevelTeamConfig[]> getAllClientsForAllMatches() {
		ArrayList<HighLevelTeamConfig[]> allClients = new ArrayList<HighLevelTeamConfig[]>();

		// loop through all the variable teams
		HighLevelTeamConfig[] variableTeams = ladderConfig.getVariableTeams();

		int numVariableTeams = Math.min(ladderConfig.getMaximumNumberVariableTeams(), variableTeams.length);
		int numStaticTeams = ladderConfig.getStaticTeams().length;

		ArrayList<int[]> teamIndices = getIndicesForNChooseK(variableTeams.length, numVariableTeams);

		for (int t = 0; t < teamIndices.size(); t++) {
			// add the variable clients
			HighLevelTeamConfig[] teams = new HighLevelTeamConfig[numVariableTeams + numStaticTeams];

			int[] indices = teamIndices.get(t);
			for (int i = 0; i < indices.length; i++) {
				teams[i] = variableTeams[indices[i]];
			}


			// now add the static ones
			int index = indices.length;
			for (HighLevelTeamConfig staticTeam : ladderConfig.getStaticTeams()) {
				teams[index] = staticTeam;
				index++;
			}

			allClients.add(teams);
		}

		return allClients;

	}

	/**
	 * Returns the indices for n choose k (to be used to make the teams).  This isn't pretty
	 * but it recursively enumerates the full set of indices for n choose k, assuming
	 * indices are zero-based.
	 * 
	 * @param n
	 * @param k
	 * @return
	 */
	protected ArrayList<int[]> getIndicesForNChooseK(int n, int k) {
		ArrayList<int[]> indices = new ArrayList<int[]> ();

		// recursion worst case
		if (k == 0) {
			return indices;
		}
		
		// base case of the recursion
		if (k == 1 || n == 1) {
			for (int i = 0; i < n; i++) {
				indices.add(new int[]{i});
			}
			return indices;
		}

		// call this recursively
		for (int i = 0; i < (n - k + 1); i++) {
			ArrayList<int[]> recurseAnswers = getIndicesForNChooseK(n-1, k-1);

			// now add the recusive answer to this number
			for (int[] recurseAnswer : recurseAnswers) {
				int[] thisSet = new int[k];
				thisSet[0] = i;

				// only keep it if the number stays in bounds
				boolean valid = true;
				for (int j = 0; j < recurseAnswer.length; j++) {
					thisSet[j + 1] = recurseAnswer[j] + i + 1;
					if (thisSet[j + 1] >= n) {
						valid = false;
					}
				}
				if (valid) {
					indices.add(thisSet);
				}
			}

		}
		return indices;
	}

	/**
	 * Compute the number for n choose k.  There are more efficient
	 * implementations but this isn't called much.
	 * 
	 * @param n
	 * @param k
	 * @return
	 */
	protected int calculateNChooseK(int n, int k) {
		if (k == 0) {
			return 0;
		} else {
			return factorial(n) / (factorial(k) * factorial(n - k));
		}
	}

	/**
	 * Compute the factorial of n
	 * @param n
	 * @return
	 */
	private int factorial(int n) {
		if (n <= 1) {
			return 1;
		} else {
			return factorial(n - 1) * n;
		}
	}

	/**
	 * Saves out the results to HTML (as specified in the config file)
	 */
	public void printResultsToHTML() {
		String ladderName = parserConfig.getString("configPath") + ladderConfig.getOutputFileName();
		try {
			FileWriter writer = new FileWriter(ladderName, false);

			// write the top of the table and page
			String str = getHTMLHeader();
			writer.write(str);
			
			// write the results
			str = getHTMLTableResults();
			writer.write(str);

			str = getHTMLStringResults();
			writer.write(str);

			// end the table and page
			str = getHTMLFooter();
			writer.write(str);
			
			writer.close();
		} catch (IOException e) {
			System.err.println("Error writing ladder.");
			e.printStackTrace();
		}
		
		
		
		
	}

	/**
	 * Writes the average results to a HTML table
	 * 
	 * @return
	 */
	private String getHTMLTableResults() {
		String str = "<table border=\"2\">\n";
		str += "<tr>\n";
		str += "<th>Place</th>";
		str += "<th>Team</th>";
		str += "<th>Average Score</th>";
		str += "<th>Average Beacons</th>";
		str += "<th>Average Resources</th>";
		str += "<th>Average Deaths</th>";
		str += "<th>Average Kills</th>";
		str += "<th>Average Assists</th>";
		str += "<th>Average Cores</th>";
		str += "<th>Average Stars</th>";
		str += "</tr>";
		
		int place = 1;
		for (TeamRecord record : sortedLadderResults) {
			str += "<tr>\n";
			str += "<td>" + place + "</td>\n";
			str += "<td>" + record.getTeamName() + "</td>\n";
			str += "<td>" + record.getAverageScore() + "</td>\n";
			str += "<td>" + record.getAverageBeacons() + "</td>\n";
			str += "<td>" + record.getAverageResources() + "</td>\n";
			str += "<td>" + String.format("%02.02f", record.getAverageDeaths()) + "</td>\n";
			str += "<td>" + String.format("%02.02f", record.getAverageKills()) + "</td>\n";
			str += "<td>" + String.format("%02.02f", record.getAverageAssists()) + "</td>\n";
			str += "<td>" + String.format("%02.02f", record.getAverageCores()) + "</td>\n";
			str += "<td>" + String.format("%02.02f", record.getAverageStars()) + "</td>\n";
			str += "</tr>";
			place++;
		}
		
		str += "</table>";
		
		return str;
	}

	/**
	 * Writes out the game results into a string 
	 */
	private String getHTMLStringResults() {
		String str = "<ul>";
		
		for (String gameString : ladderOutputString) {
			str += "<li>" + gameString.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "\n";
		}
		
		str += "</ul>";
		
		return str;
	}

	/**
	 * Writes the header information for the ladder
	 * 
	 * @return
	 */
	private String getHTMLHeader() {
		// start the page
		String str = "<html>\n";
		str += "<title>Spacewar Ladder</title>\n";
		str += "<body bgcolor=\"white\">\n";
		
		return str;
	}

	/**
	 * Writes the footer information for the ladder
	 * 
	 * @return
	 */
	private String getHTMLFooter() {
		String str = "<hr>\n";
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		String dateString = dateFormat.format(new Date());
		str += "Last updated: " + dateString + "\n";
		str += "</body></html>\n";
		
		return str;
	}




}
