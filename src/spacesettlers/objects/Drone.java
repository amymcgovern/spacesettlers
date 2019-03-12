package spacesettlers.objects;

import java.awt.Color;
import java.util.Random;

import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.MoveToObjectAction;
import spacesettlers.clients.Team;
import spacesettlers.graphics.DroneGraphics;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;

/**
 * A drone is a powerup that can be purchased by a ship in the simulator.
 * It can then be issued commands or left on default.
 * 
 *  The default action is to fly towards the nearest base when the drone is holding any resources, flags, or AiCores and
 *  to fly towards the nearest friendly ship when not holding anything.
 * 
 * @author amy
 * @author josiah
 */
public class Drone extends AbstractActionableObject {
	public static final int DRONE_RADIUS = 8;
	public static final int DRONE_MASS = 20;
	public static final int DRONE_INITIAL_ENERGY = 500;
	public static final int DRONE_MAX_ENERGY = 500;

	/**
	 * The action the drone is currently executing
	 */
	AbstractAction currentAction;

	/**
	 * The color for this team
	 */
	Color teamColor;
	
//	/**
//	 * The team that owns this drone
//	 */
//	Team team;
	
	/**
	 * Does the drone currently have a flag?
	 */
	boolean carryingFlag;
	
	/**
	 * Reference to the flag the drone has (if it has one)
	 */
	Flag flag;

	/**
	 * Reference to the team for this Drone
	 */
	Team team;

	
	
	/**
	 * Make a new drone for the specified team.  
	 * @param teamName
	 */
	public Drone(String teamName, Color teamColor, Team team, Position location, ResourcePile resources) {
		super(DRONE_MASS, DRONE_RADIUS, location);
		this.position = location;
		this.team = team;
		setDrawable(true);
		setAlive(true);
		this.teamName = teamName;
		graphic = new DroneGraphics(this, teamColor);
		this.isControllable = true;
		this.isMoveable = true;
		energy = DRONE_INITIAL_ENERGY;
		this.resources = new ResourcePile(resources); //herr0861 edit
		this.teamColor = teamColor;
		maxEnergy = DRONE_MAX_ENERGY;
		this.carryingFlag = false;
		this.flag = null;
		this.numFlags = 0;
		this.currentAction = null;
		killTagTeam = null;
		assistTagTeam = null;
		healthAtKillTag = 0;
		healthAtAssistTag = 0;
	}

	/**
	 * Deep copy of a drone (used for security)
	 * @return
	 */
	public Drone deepClone() {
		Drone newDrone = new Drone(teamName, teamColor, team, getPosition().deepCopy(), resources);

		newDrone.setAlive(isAlive);
		newDrone.resources = new ResourcePile();
		newDrone.addResources(resources);
		newDrone.energy = energy;
		newDrone.graphic = new DroneGraphics(newDrone, teamColor);
		newDrone.currentAction = currentAction;
		newDrone.id = id;
		newDrone.maxEnergy = maxEnergy;
		newDrone.weaponCapacity = weaponCapacity;
		newDrone.hitsInflicted = hitsInflicted;
		newDrone.killsInflicted = killsInflicted;
		newDrone.killsReceived = killsReceived;
		newDrone.damageInflicted = damageInflicted;
		newDrone.carryingFlag = carryingFlag;
		newDrone.numFlags = numFlags;
		newDrone.isShielded = isShielded;
		
		if (this.killTagTeam != null) {
			newDrone.killTagTeam = killTagTeam.deepClone();
		} else {
			newDrone.killTagTeam = null;
		}

		if (this.assistTagTeam != null) {
			newDrone.assistTagTeam = assistTagTeam.deepClone();
		} else {
			newDrone.assistTagTeam = null;
		}
		newDrone.healthAtAssistTag = healthAtAssistTag;
		newDrone.healthAtKillTag = healthAtKillTag;
		if (this.flag != null){
			newDrone.flag = flag.deepClone();
		}
		return newDrone;
	}


	/**
	 * Resets the drone's energy to the initial level
	 */
	public void resetEnergy() {
		energy = DRONE_INITIAL_ENERGY;
	}

	/**
	 * Is the drone carrying a flag right now?  True if so.  False otherwise.
	 * 
	 * @return
	 */
	public boolean isCarryingFlag() {
		return carryingFlag;
	}
	
	/**
	 * Get the flag (this is a deep clone from the client point of view) of the flag the drone is carrying
	 * @return
	 */
	public Flag getFlag() {
		return flag;
	}

	/**
	 * Add the flag to the drone's inventory
	 * 
	 * @param flag
	 */
	public void addFlag(Flag flag) {
		this.flag = flag;
		this.carryingFlag = true;
		this.incrementFlags();
	}
	
	/**
	 * Get the color of this team
	 * @return
	 */
	public Color getTeamColor() {
		return teamColor;
	}

	/**
	 * Get the team
	 * @return
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * Set the drone to dead and properly handle dropping the flag
	 * 
	 * @param rand
	 * @param space
	 */
	public void setDeadAndDropObjects(Random rand, Toroidal2DPhysics space) {
		resetResources();
		resetPowerups();
		resetAiCores(); 
		if (carryingFlag) {
			dropFlag(rand, space);
		}
		super.setAlive(false);

	}
	
	
	/**
	 * Drones need to behave slightly differently when set to dead or respawned 
	 * so this is an override of the abstract class.
	 */
	public void setAlive(boolean value) {
		if (value == false) {
			resetResources();
			resetPowerups();
			resetAiCores(); 

		} else {
			resetEnergy();
		}

		super.setAlive(value);
	}
	
	/**
	 * Drop all the cores by resetting to 0
	 * Will also need code inside physics sim to drop all AiCores
	 */
	public void resetAiCores() {
		//Just erase the core count, as we are not currently tracking the specific cores held by a ship.
		numCores = 0;
		/*
		 * From Josiah: In the future the drone may drop any AiCores it is holding, however I don't currently
		 * have a good strategy for releasing the cores of a craft holding a very large number of cores.
		 * For example, if a drone is holding 8 cores and dies, spawning those cores leads to a bunch of
		 * interesting collisions and several of the cores tend to end up destroyed.
		 * Perhaps limit a craft to carrying a certain number of cores?
		 */
	}
	
	/**
	 * A drone has received cores.
	 */
	public void setCores(int numCores) {
		
		super.incrementCores(numCores);//herr0861 edit
	}
	
	/**
	 * A drone drops the flag when it dies
	 */
	private void dropFlag(Random rand, Toroidal2DPhysics space) {
		carryingFlag = false;
		flag.dropFlag(rand, space);
		flag = null;
	}
	
	/**
	 * Returns the current action
	 * @return
	 */
	public AbstractAction getCurrentAction() {
		return currentAction;
	}
	
	/**
	 * This method takes a Toroidal2DPhysics object and calculates the default drone action.
	 * 
	 * This will tell it to return to the nearest friendly base if it is holding any resources, cores, or flags.
	 * If it is not, it will fly to the nearest friendly ship.
	 * 
	 * TODO: Add "allowsDrone" property to Ship object allowing drone to be rejected by the allied ship not engaged
	 * with collecting resources or to allow clever use of one drone to ferry resources from multiple ships.
	 * @param space
	 * @return
	 */
	public AbstractAction getDroneAction(Toroidal2DPhysics space) {
		//TODO Make this method call setCurrentAction with space and returning the currentAction afterwards.
		AbstractAction tempAction = null;
		
	
		if (this.resources.getTotal() > 0 || this.carryingFlag || this.getNumCores() > 0) { //if we are carrying resources, a flag, or cores
			//Return to the closest friendly base.
			Base tempBase = null;
			double shortestDistance = Double.POSITIVE_INFINITY;
			for (Base xBase : space.getBases()) {
				if (xBase.getTeamName().equalsIgnoreCase(this.getTeamName())) { //if the base is friendly
					double distance = space.findShortestDistance(xBase.getPosition(), this.getPosition());
					if (distance < shortestDistance) { //and if the base is closer than any previously found friendly base, choose this base
						shortestDistance = distance;
						tempBase = xBase;
					}
				}
			} //end baseSearch
			
			if (tempBase != null) {
				tempAction = new MoveToObjectAction(space, this.getPosition(), tempBase, tempBase.getPosition().getTranslationalVelocity());
				
			} else {
					System.out.println("Drone is null");//herr0861DELETE
			}
			
			//System.out.println("Going to base");
			
			
		} else { //If we are not carrying anything, return to the nearest friendly ship.
			
			double minDistance = Double.POSITIVE_INFINITY;
			Ship nearestShip = null;
			for (Ship otherShip : space.getShips()) {

				if (otherShip.getTeamName().equalsIgnoreCase(this.getTeamName())) {//Determine if this ship is on our team or not
					nearestShip = otherShip;
					double distance = space.findShortestDistance(this.getPosition(), otherShip.getPosition());
					if (distance < minDistance) {
						minDistance = distance;
						nearestShip = otherShip;
					}//end if statement comparing distances

				}//End if statement comparing teams
				
				
			}//End for loop going through all ships
			
			if (nearestShip != null) {
				tempAction = new MoveToObjectAction(space, this.getPosition(), nearestShip, nearestShip.getPosition().getTranslationalVelocity());
			} else {
					//System.out.println("Drone is null");//herr0861DELETE
			}
		}
		
		return tempAction;
		
	}//end getDroneAction
	
	/**
	 * This method takes a Toroidal2DPhysics object and calculates the default drone action.
	 * It then sets it as this drone's current action.
	 * 
	 * This will tell it to return to the nearest friendly base if it is holding any resources, cores, or flags.
	 * If it is not, it will fly to the nearest friendly ship.
	 * 
	 * @return
	 */
	public void setCurrentAction(Toroidal2DPhysics space) {
		//TODO Make this method call the 
		
		if (this.resources.getTotal() > 0 || this.carryingFlag || this.getNumCores() > 0) { //if we are carrying resources, a flag, or cores
			//return to nearest friendly base
			Base tempBase = null;
			double shortestDistance = Double.POSITIVE_INFINITY;
			for (Base xBase : space.getBases()) {
				if (xBase.getTeamName().equalsIgnoreCase(this.getTeamName())) { //if the base is friendly
					double distance = space.findShortestDistance(xBase.getPosition(), this.getPosition());
					if (distance < shortestDistance) { //and if the base is closer than any previously found friendly base, choose this base
						shortestDistance = distance;
						tempBase = xBase;
					}
				}
			} //end baseSearch
			if (tempBase != null) {
				currentAction = new MoveToObjectAction(space, this.getPosition(), tempBase, tempBase.getPosition().getTranslationalVelocity());
				
			} else {
					System.out.println("Ship is null");
			}
			
		} else { //If we are not carrying anything, return to the nearest friendly ship.
			
			double minDistance = Double.POSITIVE_INFINITY;
			Ship nearestShip = null;
			for (Ship otherShip : space.getShips()) {

				if (otherShip.getTeamName().equalsIgnoreCase(this.getTeamName())) {//Determine if this ship is on our team or not
					nearestShip = otherShip;
					double distance = space.findShortestDistance(this.getPosition(), otherShip.getPosition());
					if (distance < minDistance) {
						minDistance = distance;
						nearestShip = otherShip;
					}//end if statement comparing distances
				}//End if statement comparing teams
			}//End for loop going through all ships
			
			if (nearestShip != null) {
				currentAction = new MoveToObjectAction(space, this.getPosition(), nearestShip, nearestShip.getPosition().getTranslationalVelocity());
			} else {
					System.out.println("Ship is null");
			}	
		}
	}//end set action

	/**
	 * Set the action the drone is currently executing
	 * Note, this should only be done within the simulator and not
	 * within the team client (where it will be ignored)
	 * 
	 * @param currentAction
	 */
	public void setCurrentAction(AbstractAction currentAction) {
		this.currentAction = currentAction;
	}

	public String toString() {
		String str = "Drone id " + id + " team " + teamName + " at " + position + " resources " + resources + 
				" flags: " + numFlags + " cores: " + numCores;
		return str;
	}

	/**
	 * Drones cannot respawn.
	 */
	public boolean canRespawn() {
		return false;

	}


	/**
	 * Drone has to call its own set alive when it updates its energy
	 */
	@Override
	public void updateEnergy(int difference) {
		energy += difference;

		if (energy > maxEnergy) {
			energy = maxEnergy;
		}
	}

	/**
	 * Deposit the flag by setting the ship to not carry it
	 */
	public void depositFlag() {
		this.carryingFlag = false;
		flag.depositFlag();
		flag = null;
	}		

}
