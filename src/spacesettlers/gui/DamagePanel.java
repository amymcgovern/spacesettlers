package spacesettlers.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import spacesettlers.clients.Team;
import spacesettlers.simulator.SpaceSettlersSimulator;

/**
 * Shows the damage received and inflicted for a team
 * 
 * @author amy
 */
public class DamagePanel extends JPanel {
	JLabel damageInflicted, damageReceived, killsInflicted, killsReceived;
	Boolean showPositiveDamageTaken = null;
	
	public DamagePanel() {
		setLayout(new GridLayout(4, 2));

		// row 1: the titles
		JLabel blank = new JLabel("");
		add(blank);
		
		JLabel damageInflictedText = new JLabel("Inflicted");
		add(damageInflictedText);
		
		JLabel damageReceivedText = new JLabel("Received");
		add(damageReceivedText);
		
		// row 2: the damage
		JLabel damageText = new JLabel("Damage");
		add(damageText);
		
		damageInflicted = new JLabel("0");
		add(damageInflicted);
		
		
		// feature improvement: Could look to customize the color of the labels with the config file
		// Could add a graphics configuration window to change the colors of labels plus lots of game objects
		damageReceived = new JLabel("0");
		// Set the color to red to indicate receiving damage is bad..
		damageReceived.setForeground(java.awt.Color.RED);
		add(damageReceived);
		
		// row 3: the kills
		JLabel killText = new JLabel("Kills");
		add(killText);
		
		killsInflicted = new JLabel("0");
		add(killsInflicted);
		
		killsReceived = new JLabel("0");
		add(killsReceived);

	}
	
	public void updateData(SpaceSettlersSimulator simulator, String teamName) {
		Team team = null;
		for (Team curTeam : simulator.getTeams()) {
			if (curTeam.getLadderName().equalsIgnoreCase(teamName)) {
				team = curTeam;
				break;
			}
		}

		// Set the damage display to show positive damage received
		// based on config file value. Set this once for the lifetime of damage panel. 
		if (showPositiveDamageTaken == null) {
			showPositiveDamageTaken = simulator.getDisplayPositiveDamageRecieved();
		}

		
		damageInflicted.setText(team.getTotalDamageInflicted() + "");
		killsInflicted.setText(team.getTotalKillsInflicted() + "");
		killsReceived.setText(team.getTotalKillsReceived() + "");
		if (showPositiveDamageTaken) {
			damageReceived.setText(Math.abs(team.getTotalDamageReceived()) + "");
		}
		else {
			damageReceived.setText(team.getTotalDamageReceived() + "");
		}
	}
}
