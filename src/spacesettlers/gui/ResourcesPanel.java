package spacesettlers.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import spacesettlers.clients.Team;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.objects.resources.ResourceTypes;
import spacesettlers.simulator.SpaceSettlersSimulator;

/**
 * Displays the resources in a nice gridded format for a team
 * 
 * @author amy
 */
public class ResourcesPanel extends JPanel {
	JLabel waterAvail, waterTotal, fuelAvail, fuelTotal, metalsAvail, metalsTotal;

	public ResourcesPanel() {
		setLayout(new GridLayout(4,3, 4, 1));
		
		// row 1: the titles
		JLabel resources = new JLabel("Resources");
		add(resources);

		JLabel avail = new JLabel("Available");
		add(avail);

		JLabel total = new JLabel("Total");
		add(total);
		
		// the data: next row is water
		JLabel water = new JLabel("Water: ");
		add(water);
		
		waterAvail = new JLabel("0");
		add(waterAvail);
		
		waterTotal = new JLabel("0");
		add(waterTotal);

		// fuel row
		JLabel fuel = new JLabel("Fuel: ");
		add(fuel);

		fuelAvail = new JLabel("0");
		add(fuelAvail);
		
		fuelTotal = new JLabel("0");
		add(fuelTotal);
		
		// metals row
		JLabel metals = new JLabel("Metals: ");
		add(metals);

		metalsAvail = new JLabel("0");
		add(metalsAvail);
		
		metalsTotal = new JLabel("0");
		add(metalsTotal);
	}
	
	public void updateData(SpaceSettlersSimulator simulator, String teamName) {
		Team team = null;
		for (Team curTeam : simulator.getTeams()) {
			if (curTeam.getLadderName().equalsIgnoreCase(teamName)) {
				team = curTeam;
				break;
			}
		}
		
		ResourcePile avail = team.getAvailableResources();
		ResourcePile total = team.getTotalResources();
		
		waterAvail.setText("" + avail.getResourceQuantity(ResourceTypes.WATER));
		fuelAvail.setText("" + avail.getResourceQuantity(ResourceTypes.FUEL));
		metalsAvail.setText("" + avail.getResourceQuantity(ResourceTypes.METALS));
		waterTotal.setText("" + total.getResourceQuantity(ResourceTypes.WATER));
		fuelTotal.setText("" + total.getResourceQuantity(ResourceTypes.FUEL));
		metalsTotal.setText("" + total.getResourceQuantity(ResourceTypes.METALS));
		
	}

	public void updateData(AbstractObject object) {
		ResourcePile avail = null;
		ResourcePile total = null;

		if (object.getClass() == Asteroid.class) {
			Asteroid asteroid = (Asteroid) object;
			avail = asteroid.getResources();
			total = avail;
		} else if (object.getClass() == Ship.class) {
			Ship ship = (Ship) object;
			avail = ship.getResources();
			total = avail;
		} else if (object.getClass() == Base.class) {
			Base base = (Base) object;
			avail = base.getResources();
			total = base.getResources();
		} else if (object.getClass() == Beacon.class) {
			Beacon beacon = (Beacon) object;
		}
			
		
		
		if (avail != null) {	
			waterAvail.setText("" + avail.getResourceQuantity(ResourceTypes.WATER));
			fuelAvail.setText("" + avail.getResourceQuantity(ResourceTypes.FUEL));
			metalsAvail.setText("" + avail.getResourceQuantity(ResourceTypes.METALS));
			waterTotal.setText("" + total.getResourceQuantity(ResourceTypes.WATER));
			fuelTotal.setText("" + total.getResourceQuantity(ResourceTypes.FUEL));
			metalsTotal.setText("" + total.getResourceQuantity(ResourceTypes.METALS));
		} else {
			waterAvail.setText("0");
			fuelAvail.setText("0");
			metalsAvail.setText("0");
			waterTotal.setText("0");
			fuelTotal.setText("0");
			metalsTotal.setText("0");
		}
		
	}

	
}
