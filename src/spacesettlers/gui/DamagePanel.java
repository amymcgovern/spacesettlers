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
		
		damageReceived = new JLabel("0");
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
		
		damageInflicted.setText(team.getTotalDamageInflicted() + "");
		damageReceived.setText(team.getTotalDamageReceived() + "");
		killsInflicted.setText(team.getTotalKillsInflicted() + "");
		killsReceived.setText(team.getTotalKillsReceived() + "");

	}

	
}
