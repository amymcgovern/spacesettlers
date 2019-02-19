package spacesettlers.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import spacesettlers.simulator.SpaceSettlersSimulator;

public class GlobalInfoPanel extends JPanel {
	GridBagConstraints constraints;
	JLabel timestepData;
	
	public GlobalInfoPanel() {
        setFont(new Font("SansSerif", Font.PLAIN, 12));

		constraints = new GridBagConstraints();
		//constraints.fill = GridBagConstraints.BOTH;
		//constraints.insets = new Insets(10, 10, 10, 10);
		setLayout(new GridBagLayout());

        JLabel timestepLabel = new JLabel("Timestep: ");
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(timestepLabel, constraints);
        
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        timestepData = new JLabel("Foo");
        add(timestepData, constraints);
	}

	public void updateData(SpaceSettlersSimulator simulator) {
		timestepData.setText("" + simulator.getTimestep());
	}
	
}
