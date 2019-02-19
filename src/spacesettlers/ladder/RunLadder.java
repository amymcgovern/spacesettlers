package spacesettlers.ladder;

import spacesettlers.simulator.SimulatorException;

import java.util.concurrent.ExecutionException;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;

public class RunLadder {
	JSAPResult config;

	/**
	 * Make a new ladder
	 * @throws SimulatorException 
	 */
	public RunLadder(String[] args) throws SimulatorException {
		SimpleJSAP parser = initializeParser(args);
		config = parseArgs(args, parser);
		verifyArguments();
	}

	/**
	 * Actually parse the command line arguments
	 * @param args
	 * @param parser
	 * @return
	 */
	private JSAPResult parseArgs(String[] args, SimpleJSAP parser) {
		// parse the arguments
		JSAPResult parserConfig = parser.parse(args);
		// exit if it failed to parse
		if (parser.messagePrinted()) {
			System.exit(-1);
		}
		return parserConfig;
	}

	/**
	 * Verify that the arguments are valid.  
	 */
	private void verifyArguments() throws SimulatorException {
		String configName = config.getString("simulatorConfigFile");
		
		int xmlIndex = configName.indexOf("xml");
		// verify that the argument ended in .xml
		if (xmlIndex != (configName.length() - 3)) {
			throw new SimulatorException("Error: invalid simulator config file name " + configName + " It must end in .xml");
		}

		configName = config.getString("ladderConfigFile");
		
		xmlIndex = configName.indexOf("xml");
		// verify that the argument ended in .xml
		if (xmlIndex != (configName.length() - 3)) {
			throw new SimulatorException("Error: invalid ladder config file name " + configName + " It must end in .xml");
		}

	}


	/**
	 * Initialize the parser with all of the command line arguments
	 * @param args
	 * @return
	 */
	private SimpleJSAP initializeParser(String[] args) {
		SimpleJSAP parser = null;

		// create the parser and specify all the command line arguments
		try {
			parser = new SimpleJSAP(
					"Simulator",
					"Spacewar simulator",
					new Parameter[] {
						new FlaggedOption("simulatorConfigFile",
								JSAP.STRING_PARSER, null, JSAP.REQUIRED,
								JSAP.NO_SHORTFLAG, "simulatorConfigFile",
								"configuration file for the simulator (xml)"), 
						new FlaggedOption("ladderConfigFile",
								JSAP.STRING_PARSER, null, JSAP.REQUIRED,
								JSAP.NO_SHORTFLAG, "ladderConfigFile",
								"configuration file for the simulator (xml)"), 
						new FlaggedOption("configPath",
								JSAP.STRING_PARSER, null, JSAP.REQUIRED,
								JSAP.NO_SHORTFLAG, "configPath",
								"path to the configuration files"), 
						new FlaggedOption("graphics",
								JSAP.BOOLEAN_PARSER, "false", JSAP.REQUIRED,
								JSAP.NO_SHORTFLAG, "graphics",
								"boolean stating if graphics are to be shown (true) or not (false)"),
						new FlaggedOption("debug",
								JSAP.BOOLEAN_PARSER, "false", JSAP.REQUIRED,
								JSAP.NO_SHORTFLAG, "debug",
								"boolean stating if debugging mode (single threading) is on (true) or not (false)"),
					});
		} catch (JSAPException e) {
			e.printStackTrace();
			System.err.println("Error in the parser - exiting");
			System.exit(-1);
		}
		return parser;
	}

	
	
	/**
	 * @param args
	 * @throws SimulatorException 
	 * @throws InterruptedException 
	 * @throws ExecutionException 
	 */
	public static void main(String[] args) throws SimulatorException, InterruptedException, ExecutionException {
		RunLadder runLadder = new RunLadder(args);
		
		Ladder ladder = new Ladder(runLadder.config);
		
		ladder.run();

		// TODO: save the final results to HTML
		ladder.printResultsToHTML();
		
		System.out.println("Ladder finished!");
		System.exit(0);
	}

}
