package spacesettlers.actions;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import spacesettlers.game.AbstractGameAgent;
import spacesettlers.objects.Drone;
import spacesettlers.objects.Ship;
import spacesettlers.objects.weapons.AbstractWeapon;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Movement;

/**
 * All Space Settlers actions must extend this class.  It is assumed
 * that most actions are focusing on movement and that weapon firing
 * is a single step action.  
 * 
 * @author amy
 */
abstract public class AbstractAction {
	/**
	 * The weapon for this time step
	 */
	AbstractWeapon weapon;
	
	/**
	 * The game search (used only for the gaming asteroids)
	 */
	AbstractGameAgent gameSearch;

	/**
	 * All actions must return a movement in (x,y) and orientation space.
	 * If the ship is not moving, simply return DoNothingAction.
	 *   
	 * @return
	 */
	abstract public Movement getMovement(Toroidal2DPhysics space, Ship ship);
	
	/**
	 * All actions must return a movement in (x,y) and orientation space.
	 * If the ship is not moving, simply return DoNothingAction.
	 *   
	 * @return
	 */
	abstract public Movement getMovement(Toroidal2DPhysics space, Drone drone);
	
	/**
	 * Calls the abstract getMovement with supplied timeout for response. If
	 * nothing is returned in that time, simple return DoNothingAction (new Movement())
	 *   
	 * @return
	 */
	public Movement getMovement(Toroidal2DPhysics space, Ship ship, int timeout){
		ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Movement> future = executor.submit(new SpacewarActionCallable(this, space, ship));
        
        Movement movement = null;
        try {
            //start
        	movement = future.get(timeout, TimeUnit.MILLISECONDS);
            //finished in time
        } catch (TimeoutException e) {
            //was terminated
        	//return no movement
        	movement = new Movement();
        } catch (InterruptedException e) {
        	//we were interrupted (should not happen but lets be good programmers) 
        	//return no movement
        	movement = new Movement();
			e.printStackTrace();
		} catch (ExecutionException e) {
			//the executor threw and exception (should not happen but lets be good programmers) 
			//return no movement
        	movement = new Movement();
		}

        executor.shutdownNow();
        
        return movement;
	}
	
	/**
	 * Calls the abstract getMovement with supplied timeout for response. If
	 * nothing is returned in that time, simple return DoNothingAction (new Movement())
	 *   
	 * @return
	 */
	public Movement getMovement(Toroidal2DPhysics space, Drone drone, int timeout){
		ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Movement> future = executor.submit(new SpacewarActionCallable(this, space, drone));
        
        Movement movement = null;
        try {
            //start
        	movement = future.get(timeout, TimeUnit.MILLISECONDS);
            //finished in time
        } catch (TimeoutException e) {
            //was terminated
        	//return no movement
        	movement = new Movement();
        } catch (InterruptedException e) {
        	//we were interrupted (should not happen but lets be good programmers) 
        	//return no movement
        	movement = new Movement();
			e.printStackTrace();
		} catch (ExecutionException e) {
			//the executor threw and exception (should not happen but lets be good programmers) 
			//return no movement
        	movement = new Movement();
		}

        executor.shutdownNow();
        
        return movement;
	}
	
	/**
	 * Each action returns true when it completes.  Some actions
	 * take more than one timestep to complete.  Others only take one.
	 * @return
	 */
	abstract public boolean isMovementFinished(Toroidal2DPhysics space);

	/**
	 * Inner class used to help ensure no one goes over on time
	 * @author amy
	 *
	 */
	class SpacewarActionCallable implements  Callable<Movement>{
		private AbstractAction action;
		private Toroidal2DPhysics space;
		private Ship ship;
		private Drone drone;
		
		SpacewarActionCallable(AbstractAction action, Toroidal2DPhysics space, Ship ship){
			this.action = action;
			this.space = space;
			this.ship = ship;
		}
		SpacewarActionCallable(AbstractAction action, Toroidal2DPhysics space, Drone drone){//herr0861 edit - Overload to handle multiple object types
			this.action = action;
			this.space = space;
			this.drone = drone;
		}
		
		public Movement call() throws Exception {
			if(this.action != null && this.ship != null && this.space != null){
				return this.action.getMovement(this.space, this.ship);
			} else if (this.action != null && this.drone != null && this.space != null) {//herr0861edit
				return this.action.getMovement(this.space, this.drone);
			} else {
				//something went wrong...lets return no movement
				System.out.println("Error, no movement");
				return new Movement();
			}
			
		}
	}
}

