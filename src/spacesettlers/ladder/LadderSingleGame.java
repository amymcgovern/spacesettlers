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
import java.util.concurrent.Callable;

import spacesettlers.clients.Team;
import spacesettlers.configs.*;
import spacesettlers.simulator.SimulatorException;
import spacesettlers.simulator.SpaceSettlersSimulator;

import com.martiansoftware.jsap.JSAPResult;
import com.thoughtworks.xstream.XStream;

/**
 * Runs a single game of the the ladder (for multi-threading)
 * @author amy
 *
 */
public class LadderSingleGame implements Callable {
	LadderConfig ladderConfig;

	SpaceSettlersSimulator simulator;

	SpaceSettlersConfig simConfig;

	JSAPResult parserConfig;

	HashMap <String, Team> ladderResultsMap;

	ArrayList<String> ladderOutputString;
	
	int gameIndex, numGames;

	/**
	 * Make a new ladder
	 * @param config
	 * @throws SimulatorException 
	 */
	public LadderSingleGame(JSAPResult parserConfig) throws SimulatorException {
		loadConfigFiles(parserConfig);
		this.parserConfig = parserConfig;

		ladderResultsMap = new HashMap<String, Team>();
		ladderOutputString = new ArrayList<String>();
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
	 */
	public void initializeGame(HighLevelTeamConfig[] teamsForMatch, int gameIndex, int numGames) throws SimulatorException {
		// save the index for debugging and printouts
		this.gameIndex = gameIndex;
		this.numGames = numGames;
		
		// setup the simulator for this match
		simConfig.setTeams(teamsForMatch);

		// set the bases to match the teams for this game.  Read in the ones
		// from the config file first (and rename them)
		// only make new ones if we don't have enough
		BaseConfig[] defaultBases = simConfig.getBases();
		BaseConfig[] baseConfig = new BaseConfig[teamsForMatch.length];
		for (int i = 0; i < teamsForMatch.length; i++) {
			if (i < defaultBases.length) {
				baseConfig[i] = defaultBases[i];
				baseConfig[i].setTeamName(teamsForMatch[i].getTeamName());
			} else {
				baseConfig[i] = new BaseConfig(teamsForMatch[i].getTeamName());
			}
		}
		simConfig.setBases(baseConfig);

		// if there are flags, then set the flags to also match the teams for this game
		FlagConfig[] flagConfigs = simConfig.getFlags();
		if (flagConfigs != null && flagConfigs.length > 0) {
			if (flagConfigs.length != teamsForMatch.length) {
				throw new SimulatorException("Error: The number of flags in the config file doesn't match the number of teams for the match");
			}
			for (int i = 0; i < teamsForMatch.length; i++) {
				flagConfigs[i].setTeamName(teamsForMatch[i].getTeamName());
			}
		}
	}

	/**
	 * Actually run the single game 
	 */
	public Object call() throws Exception {
		// tell the user the match is about to begin
		String str = "***Game " + gameIndex + " / " + numGames + " with teams ";
		for (HighLevelTeamConfig team : simConfig.getTeams()) {
			str += (team.getTeamName() + " ");
		}
		str += "***";
		System.out.println(str);
		ladderOutputString.add(str);

		try {
			// try to make a simulator and run it
			simulator = new SpaceSettlersSimulator(simConfig, parserConfig);

			str = "***Game " + gameIndex + " / " + numGames + " with teams ";
			Set<Team> teams = simulator.getTeams();
			for (Team team : teams) {
				str += (team.getTeamName() + " = " + team.getLadderName() + " ");
			}
			str += "***";
			System.out.println(str);
			ladderOutputString.add(str);

			// run the game
			simulator.run();

			// get the teams and print out their scores
			str = "***Game " + gameIndex + " / " + numGames + " ended, scores follow ";

			for (Team team : teams) {
				str = "Team: " + team.getLadderName() + " scored " + team.getScore();
				ladderOutputString.add(str);
				System.out.println(str);

				ladderResultsMap.put(team.getLadderName(), team);
			}
		} catch (Exception e) {
			System.err.println("Error in match : skipping and moving to next one");
			ladderOutputString.add("Error in match : skipping and moving to next one");
			ladderOutputString.add(e.toString());
			e.printStackTrace();
		}
		
		str = "***Game " + gameIndex + " / " + numGames + " ended, returning from thread";
		return this;
	}

	public HashMap<String, Team> getLadderResultsMap() {
		return ladderResultsMap;
	}

	public ArrayList<String> getLadderOutputString() {
		return ladderOutputString;
	}

	


}
