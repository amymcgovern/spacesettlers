package spacesettlers.configs;

/**
 * Configuration for radomly generated asteroids
 * @author amy
 *
 */
public class RandomAsteroidConfig {
	double probabilityMineable;
	
	int numberInitialAsteroids;
	
	int maximumNumberAsteroids;
	
	double probabilityMoveable;
	
	double probabilityGameable;
	
	double maxInitialVelocity;
	
	double probabilityFuelType;
	
	double probabilityWaterType;
	
	double probabilityMetalsType;
	
	/**
	 * @return the probabilityMineable
	 */
	public double getProbabilityMineable() {
		return probabilityMineable;
	}

	/**
	 * @return the numberInitialAsteroids
	 */
	public int getNumberInitialAsteroids() {
		return numberInitialAsteroids;
	}

	/**
	 * @return the maximumNumberAsterids
	 */
	public int getMaximumNumberAsteroids() {
		return maximumNumberAsteroids;
	}

	public double getProbabilityMoveable() {
		return probabilityMoveable;
	}

	public double getMaxInitialVelocity() {
		return maxInitialVelocity;
	}

	public double getProbabilityFuelType() {
		return probabilityFuelType;
	}

	public double getProbabilityWaterType() {
		return probabilityWaterType;
	}

	public double getProbabilityMetalsType() {
		return probabilityMetalsType;
	}

	public double getProbabilityGameable() {
		return probabilityGameable;
	}
	
}
