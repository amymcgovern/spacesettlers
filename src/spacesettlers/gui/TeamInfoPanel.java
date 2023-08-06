package spacesettlers.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import spacesettlers.clients.Team;
import spacesettlers.simulator.SpaceSettlersSimulator;

/**
 * Shows the information for a team in the GUI
 * 
 * @author amy
 */
public class TeamInfoPanel extends JPanel {
	Team team;
	GridBagConstraints constraints;
	ResourcesPanel resourcesPanel;
	DamagePanel damagePanel;
	JLabel score, flags, cores, stars;
	
	public TeamInfoPanel(Team team) {
		this.team = team;
		
		setBorder(BorderFactory.createLineBorder(team.getTeamColor(), 3));
		constraints = new GridBagConstraints();
		//constraints.insets = new Insets(10, 10, 10, 10);
		setLayout(new GridBagLayout());
		
		JLabel name = new JLabel(team.getLadderName());
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(name, constraints);

		JLabel scoreText = new JLabel("Score");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = GridBagConstraints.RELATIVE;
		add(scoreText, constraints);

		score = new JLabel("0");
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(score, constraints);

		JLabel CoreText = new JLabel("Cores");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = GridBagConstraints.RELATIVE;
		add(CoreText, constraints);

		cores = new JLabel("0");
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(cores, constraints);

		JLabel FlagText = new JLabel("Flags");
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = GridBagConstraints.RELATIVE;
		add(FlagText, constraints);

		flags = new JLabel("0");
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(flags, constraints);
		
		JLabel StarText = new JLabel("Stars");
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = GridBagConstraints.RELATIVE;
		add(StarText, constraints);

		stars = new JLabel("0");
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		add(stars, constraints);

		resourcesPanel = new ResourcesPanel();
		constraints.gridx = 0;
		constraints.gridy = 5;
		add(resourcesPanel, constraints);

		damagePanel = new DamagePanel();
		constraints.gridx = 0;
		constraints.gridy = 6;
		add(damagePanel, constraints);

	}

	public void updateData(SpaceSettlersSimulator simulator) {
		resourcesPanel.updateData(simulator, team.getLadderName());
		damagePanel.updateData(simulator, team.getLadderName());
		score.setText(team.getScore() + "");
		flags.setText(team.getTotalFlagsCollected() + "");
		cores.setText(team.getTotalCoresCollected() + "");
		stars.setText(team.getTotalStarsCollected() + "");
	}
	
}
