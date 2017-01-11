package spacesettlers.objects.resources;

/**
 * A resource factory creates resources for asteroids based on the specified radius 
 * and type and the density of the type
 * 
 * @author amy
 *
 */
public class ResourceFactory {
	public final static double FUEL_DENSITY = 0.35;
	public final static double METALS_DENSITY = 0.45;
	public final static double WATER_DENSITY = 0.25;

	public static final double REFINED_RESOURCE_DENSITY_MULTIPLIER = 0.25;
	
	public static int getResourceQuantity(ResourceTypes type, int radius) {
		double area = Math.PI * radius * radius;
		int resource = 0;

		switch (type) {
			case FUEL:
				resource = (int) (area * FUEL_DENSITY);
				break;
			
			case WATER:
				resource = (int) (area * WATER_DENSITY); 
				break;
				
			case METALS:
				resource = (int) (area * METALS_DENSITY);
				break;
		}
		
		return resource;
	}

	
}
