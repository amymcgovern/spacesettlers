package spacesettlers.clients.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.clients.TeamClient;
import spacesettlers.game.AbstractGameAgent;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * Demonstrates one idea on implementing Genetic Algorithms/Evolutionary Computation within the space settlers framework.
 * 
 * @author amy
 *
 */

public class ExampleGAClient extends TeamClient {
	/**
	 * The current policy for the team
	 */
	private ExampleGAChromosome currentPolicy;
	
	/**
	 * The current population (either being built or being evaluated)
	 */
	private ExampleGAPopulation population;
	
	/**
	 * How many steps each policy is evaluated for before moving to the next one
	 */
	private int evaluationSteps = 2000;
	
	/**
	 * How large of a population to evaluate
	 */
	private int populationSize = 25;
	
	/**
	 * Current step
	 */
	private int steps = 0;
	
	@Override
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// make a hash map of actions to return
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();

		// loop through each ship and send it the current policy from the chromosome.  If the chromosome
		// hasn't seen a state before, it will pick an abstract action (you should make more interesting actions!
		// this agent choses only between doNothing and moveToNearestAsteroid)
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;

				AbstractAction action;
				
				if (ship.getCurrentAction() == null || ship.getCurrentAction().isMovementFinished(space)) {
					ExampleGAState currentState = new ExampleGAState(space, ship);
					action = currentPolicy.getCurrentAction(space, ship, currentState, random);
					//System.out.println("New random action is " + action);
				} else {
					action = ship.getCurrentAction();
				}
				actions.put(ship.getId(), action);
			} else {
				// it is a base.  Heuristically decide when to use the shield (TODO)
				actions.put(actionable.getId(), new DoNothingAction());
			}
		}
		//System.out.println("actions are " + actions);
		return actions;

	}

	@Override
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {
		// increment the step counter
		steps++;

		// if the step counter is modulo evaluationSteps, then evaluate this member and move to the next one
		if (steps % evaluationSteps == 0) {
			// note that this method currently scores every policy as zero as this is part of 
			// what the student has to do
			population.evaluateFitnessForCurrentMember(space);

			// move to the next member of the population
			currentPolicy = population.getNextMember();

			if (population.isGenerationFinished()) {
				// note that this is also an empty method that a student needs to fill in
				population.makeNextGeneration();
				
				currentPolicy = population.getNextMember();
			}
			
		}
		
	}

	@Override
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<UUID, PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects, ResourcePile resourcesAvailable,
			PurchaseCosts purchaseCosts) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Initialize the population by either reading it from the file or making a new one from scratch
	 * 
	 * @param space
	 */
	@Override
	public void initialize(Toroidal2DPhysics space) {
		XStream xstream = new XStream();
		xstream.alias("ExampleGAPopulation", ExampleGAPopulation.class);

		// try to load the population from the existing saved file.  If that failes, start from scratch
		try { 
			population = (ExampleGAPopulation) xstream.fromXML(new File(getKnowledgeFile()));
		} catch (XStreamException e) {
			// if you get an error, handle it other than a null pointer because
			// the error will happen the first time you run
			System.out.println("No existing population found - starting a new one from scratch");
			population = new ExampleGAPopulation(populationSize);
		}

		currentPolicy = population.getFirstMember();
	}

	@Override
	public void shutDown(Toroidal2DPhysics space) {
		XStream xstream = new XStream();
		xstream.alias("ExampleGAPopulation", ExampleGAPopulation.class);

		try { 
			// if you want to compress the file, change FileOuputStream to a GZIPOutputStream
			xstream.toXML(population, new FileOutputStream(new File(getKnowledgeFile())));
		} catch (XStreamException e) {
			// if you get an error, handle it somehow as it means your knowledge didn't save
			System.out.println("Can't save knowledge file in shutdown ");
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			// file is missing so start from scratch (but tell the user)
			System.out.println("Can't save knowledge file in shutdown ");
			System.out.println(e.getMessage());
		}
	}

	@Override
	public Set<SpacewarGraphics> getGraphics() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<UUID, AbstractGameAgent> getGameSearch(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		// TODO Auto-generated method stub
		return null;
	}


}
