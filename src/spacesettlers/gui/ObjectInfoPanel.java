package spacesettlers.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import spacesettlers.objects.*;
import spacesettlers.simulator.SpaceSettlersSimulator;

public class ObjectInfoPanel extends JPanel {
	GridBagConstraints constraints;
	ResourcesPanel resourcesPanel;
	AbstractObject selectedObject;
	InnerObjectPanel innerPanel;
	JLabel objectName;
	
	class InnerObjectPanel extends JPanel {
		JLabel isAlive, mass, radius, flag, core;
		
		public InnerObjectPanel() {
			setLayout(new GridLayout(5,2));
			
			JLabel isA = new JLabel("Alive: ");
			add(isA);
			
			isAlive = new JLabel("unknown");
			add(isAlive);

			JLabel massL = new JLabel("Mass: ");
			add(massL);
			
			mass = new JLabel("unknown");
			add(mass);
			
			JLabel radiusL = new JLabel("Radius: ");
			add(radiusL);
			
			radius = new JLabel("unknown");
			add(radius);
			
			JLabel flagL = new JLabel("Flags: ");
			add(flagL);
			
			flag = new JLabel("0");
			add(flag);

			JLabel coreL = new JLabel("Cores: ");
			add(coreL);
			
			core = new JLabel("0");
			add(core);

		}
		
		public void updateData(SpaceSettlersSimulator simulator) {
			isAlive.setText(selectedObject.isAlive() + "");
			mass.setText(selectedObject.getMass() + "");
			radius.setText(selectedObject.getRadius() + "");
			flag.setText(selectedObject.getNumFlags() + "");
			core.setText(selectedObject.getNumCores() + "");
		}

		
	}
	
	public ObjectInfoPanel() {
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		constraints = new GridBagConstraints();
		//constraints.insets = new Insets(10, 10, 10, 10);
		setLayout(new GridBagLayout());
		
		JLabel name = new JLabel("Information on the selected object");
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(name, constraints);

		objectName = new JLabel("Object type/name");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(objectName, constraints);
		
		innerPanel = new InnerObjectPanel();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(innerPanel, constraints);
		
		resourcesPanel = new ResourcesPanel();
		constraints.gridx = 0;
		constraints.gridy = 3; 
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(resourcesPanel, constraints);
		
		selectedObject = null;
	}

	public void setSelectedObject(AbstractObject clickedObject) {
		this.selectedObject = clickedObject;
	}

	/**
	 * Update the GUI for this step using the simulator data as needed
	 * @param simulator
	 */
	public void updateData(SpaceSettlersSimulator simulator) {
		// do not update if there is no selected object
		if (this.selectedObject == null) {
			return;
		}
		
		String name = "";
		
		if (selectedObject.getClass() == Asteroid.class) {
			Asteroid asteroid = (Asteroid) selectedObject;
			if (asteroid.isMineable()) {
				name = "Mineable asteroid";
			} else {
				name = "Non-mineable asteroid";
			}
		} else if (selectedObject.getClass() == Base.class) {
			Base base = (Base) selectedObject;
			name = "Base for " + base.getTeamName();
		} else if (selectedObject.getClass() == Ship.class) {
			Ship ship = (Ship) selectedObject;
			name = "Ship for " + ship.getTeamName();
		} else if (selectedObject.getClass() == Beacon.class) {
			Beacon beacon = (Beacon) selectedObject;
			name = "Beacon";
		} else if (selectedObject.getClass() == Star.class) {
			Star star = (Star) selectedObject;
			name = "Star";
		}
		objectName.setText(name);
		
		innerPanel.updateData(simulator);
		
		resourcesPanel.updateData(selectedObject);
	}

	

}
