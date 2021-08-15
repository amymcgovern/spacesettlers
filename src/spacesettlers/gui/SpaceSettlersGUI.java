package spacesettlers.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import spacesettlers.clients.Team;
import spacesettlers.configs.SpaceSettlersConfig;
import spacesettlers.objects.AbstractObject;
import spacesettlers.simulator.SpaceSettlersSimulator;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;

/**
 * Main GUI for the Space Settlers simulator
 * @author amy
 *
 */
public class SpaceSettlersGUI {
	JFrame mainFrame;
	
	JSpaceSettlersComponent mainComponent;
	
	JSpaceSettlersInfoPanel infoPanel;
	
	boolean isPaused = false;
	
	SpaceSettlersSimulator simulator;
	
	AffineTransform graphicsTransform, clickTransform;
	
	/**
	 * Make a new GUI
	 * @param config
	 */
	public SpaceSettlersGUI(SpaceSettlersConfig config, SpaceSettlersSimulator simulator) {
		super();
		this.simulator = simulator;
		
		mainFrame = new JFrame("Space Settlers");

		// make the inner panel and components
		infoPanel = new JSpaceSettlersInfoPanel(simulator);
		infoPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		mainComponent = new JSpaceSettlersComponent(config.getHeight(), config.getWidth());
		//JScrollPane mainScrollPane = new JScrollPane(mainComponent);
		JScrollPane infoScrollPane = new JScrollPane(infoPanel);
		infoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		// put them into the main layout
//		mainFrame.setLayout(new GridBagLayout());
//		GridBagConstraints constraints = new GridBagConstraints();
//		constraints.weightx = 1.0;
//		constraints.gridx = 0;
//		constraints.gridy = 0;
//		constraints.gridwidth = GridBagConstraints.RELATIVE;
//		constraints.fill = GridBagConstraints.BOTH;
//		mainFrame.add(mainComponent, constraints);
//		
//        constraints.gridx = 1;
//        constraints.gridy = 0;
//		constraints.weightx = 0.0;
//		constraints.gridwidth = GridBagConstraints.REMAINDER;
//		//mainFrame.add(infoPanel, constraints);
//		mainFrame.add(infoScrollPane);

		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(mainComponent, BorderLayout.CENTER);
		mainFrame.add(infoScrollPane, BorderLayout.EAST);
		
		/*
		mainFrame.setLayout(new GridLayout(1,2));
		mainFrame.add(mainComponent);
		mainFrame.add(infoPanel);
		*/

		
		// create a help menu
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Help");
		menuBar.add(menu);
		JMenuItem item = new JMenuItem("Interface help");
		item.setAccelerator(KeyStroke.getKeyStroke('h'));
		menu.add(item);
		item.addActionListener(new HelpMenuListener());
		
		mainFrame.setJMenuBar(menuBar);
		
		// get the screen size, code from:
		// http://stackoverflow.com/questions/3680221/how-can-i-get-the-monitor-size-in-java
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth() - 50;
		double screenHeight = screenSize.getHeight();
		//System.out.println("Screen width " + screenWidth);
		//System.out.println("Screen height " + screenHeight);
		Dimension infoSize = infoPanel.getPreferredSize();
		//System.out.println("info component size is " + infoSize);
		Dimension mainSize = mainFrame.getContentPane().getPreferredSize();
		//System.out.println("main component size is " + mainSize);
		//System.out.println("Playing field size height " + config.getHeight() + " width " + config.getWidth());
		int requestedWidth = (int) mainSize.getWidth() + (int) infoSize.getWidth();
		int requestedHeight = (int) mainSize.getHeight();// + (int) infoSize.getHeight();
		//System.out.println("requested width " + requestedWidth);
		//System.out.println("requested height " + requestedHeight);
		
		// if the size is too large, scale it
		if (screenWidth < requestedWidth || screenHeight < requestedHeight) {
			double scaleX = screenWidth / requestedWidth;
			double scaleY = screenHeight / requestedHeight;
			double scale = Math.min(scaleX, scaleY);
			
			// calculate the downscale for the graphics
			graphicsTransform = AffineTransform.getScaleInstance(scale, scale);

			// and the upscale for mouse clicks
			clickTransform = AffineTransform.getScaleInstance(1.0 / scale, 1.0 / scale);

			// this resizes the main component to just fit the scaled height/width
			mainComponent.setPreferredSize(new Dimension((int) (scale * config.getWidth()), 
					(int) (scale * config.getHeight())));
			
			//System.out.println("Original preferred size for info is " + infoPanel.getPreferredSize());
			infoScrollPane.setPreferredSize(new Dimension((int) infoSize.getWidth(), 
					(int) (scale * config.getHeight())));
			//System.out.println("Setting preferred size for info to " + infoPanel.getPreferredSize());

			System.out.println("transform is " + graphicsTransform);
		} else {
			// the screen size is small enough to fit so just set the scale to be 1 to 1
			graphicsTransform = AffineTransform.getScaleInstance(1.0, 1.0);
			clickTransform = AffineTransform.getScaleInstance(1.0, 1.0);
		}
		mainComponent.setScaleTransform(graphicsTransform);

		// if you do this, it fills the screen (which isn't what we want)
		//mainFrame.setPreferredSize(new Dimension(Math.min((int)screenWidth, requestedWidth), 
		//		Math.min((int)screenHeight, requestedHeight)));
		
		// add any client key & mouse listeners that want to be added
		for (Team team : simulator.getTeams()) {
			KeyAdapter listener = team.getKeyAdapter();
			if (listener != null) {
				mainFrame.addKeyListener(listener);
			}

			MouseAdapter mouseListen = team.getMouseAdapter(clickTransform);
			if (mouseListen != null) {
				mainComponent.addMouseListener(mouseListen);
				//mainComponent.addMouseMotionListener(mouseListen);
			}

		}
		
		// add the mouse listener for the info boxes
		mainComponent.addMouseListener(new GUIMouseListener());

		// add a key listener to handle pauses
		mainFrame.addKeyListener(new KeyAdapter() {
			/**
			 * Listen to the GUI level key commands
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				char key = e.getKeyChar();
				if (key == 'p' || key == 'P') {
					togglePause();
				} else if (key == '+') {
					doubleSpeed();
				} else if (key == '-') {
					slowSpeed();
				}
			}
		}
		);
		
		
		// finally draw it
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setResizable(false);

		mainFrame.pack();
		mainFrame.setVisible(true);
		//System.out.println("Info component width x height " + infoPanel.getWidth() + " x " + infoPanel.getHeight());
		//System.out.println("Main component width x height " + mainPanel.getWidth() + " x " + mainPanel.getHeight());
	}

	/**
	 * Main GUI mouse interface.  Pops up a info area in the info panel if the user LEFT clicks on an object
	 * 
	 * @author amy
	 *
	 */
	class GUIMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			Point point = e.getPoint();

			//System.out.println("Original x, y " + point.getX() + " , " + point.getY());
			Point2D newPoint = new Point2D.Double(0, 0);
			clickTransform.transform(point, newPoint); 
			Position clickPosition = new Position(newPoint.getX(), newPoint.getY());
			//System.out.println("Transformed x, y " + newPoint.getX() + " , " + newPoint.getY());
			
			// only listens to left clicks
			if (e.getButton() == MouseEvent.BUTTON1) {
				// get the set of all objects and figure out if the user clicked inside an object
				Set<AbstractObject> allObjects = simulator.getAllObjects();
				Toroidal2DPhysics space = simulator.getSimulatedSpace();
				//System.out.println("Received a click at " + clickPosition);
				
				for (AbstractObject obj : allObjects) {
					//System.out.println("Object " + obj + " distance to click " + space.findShortestDistance(clickPosition, obj.getPosition()) + " radius is " + obj.getRadius());
					if (space.findShortestDistance(clickPosition, obj.getPosition()) <= obj.getRadius()) {
						infoPanel.setClickedObject(obj);
						//System.out.println("Click matched object " + obj);
						return;
					}
				}
				
			}
		}
	}


	/**
	 * Toggle the paused state
	 */
	protected void togglePause() {
		isPaused = !isPaused;
		simulator.setPaused(isPaused);

		if (isPaused) {
			System.out.println("Pausing spacewar");
		} else {
			System.out.println("Unpausing spacewar");
		}
		
	}

	/**
	 * Double the sim speed (by halving graphics sleep)
	 */
	protected void doubleSpeed() {
		int newSpeed = Math.max(5, simulator.getGraphicsSleep() / 2);
		simulator.setGraphicsSleep(newSpeed);
	}

	/**
	 * Slow the sim speed (by doubling graphics sleep)
	 */
	protected void slowSpeed() {
		int newSpeed = Math.min(simulator.getGraphicsSleep() * 2, 240);
		simulator.setGraphicsSleep(newSpeed);
	}

	
	/**
	 * Redraws the graphics
	 * @param simulator
	 */
	public void redraw() {
		infoPanel.setSimulator(simulator);
		infoPanel.updateData();
		//mainFrame.paintComponents(getGraphics());
		mainComponent.setSimulator(simulator);
		mainFrame.repaint();
	}
	
	/**
	 * Listener for the help menu
	 * @author amy
	 *
	 */
	public class HelpMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			JFrame helpFrame = new JFrame("Keyboard and Mouse commands");
			
			String helpText = "Summary of all the keyboard commands.\n\n\n";
			helpText += " Main GUI commands:\n";
			helpText += "p/P  pauses and unpauses the simulation\n";
			helpText += "+  speeds up the simulation\n";
			helpText += "-  slows down the simulation\n";
			helpText += "h  brings up this menu\n\n";

			helpText +=	"Summary of the commands for the keyboard and mouse for the human client.\n\n\n";
			helpText += " Keyboard commands:\n";
			helpText += " Use the arrow keys to move in the associated direction.  Note that they give you acceleration in the direction of the arrow.\n";
			helpText += " The space bar will fire missiles.\n\n";
			helpText += " Mouse commands;\n";
			helpText += " Right click or alt-click in the GUI to have your agent fly to that location.  Don't forget the world is toroidal!";
			JTextArea helpTextArea = new JTextArea(helpText);
			helpTextArea.setEditable(false);
			
			helpFrame.add(helpTextArea);
			
			helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			helpFrame.setResizable(false);
			helpFrame.pack();
			helpFrame.setVisible(true);


		}

	}




}
