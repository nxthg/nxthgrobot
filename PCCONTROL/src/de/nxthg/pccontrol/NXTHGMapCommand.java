package de.nxthg.pccontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.nxthg.pccontrol.NXTHGNavigationModel.NavEvent;

import lejos.pc.comm.SystemContext;

/**
 * MapCommand shows a mapped area and allow navigation commands to be sent to
 * the NXT.
 * 
 * The NXT must run the MapTest sample. ottos Test
 * 
 * @author Lawrie Griffiths
 */
@SuppressWarnings("deprecation")
public class NXTHGMapCommand extends NXTHGNavigationPanel {
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 1090;
	private static final int FRAME_HEIGHT = 1160;
	private static final int INITIAL_ZOOM = 180;
	private static final Point INITIAL_MAP_ORIGIN = new Point(-10, -10);
	private static final Dimension MAP_AREA_SIZE = new Dimension(800, 550);
	private static final String FRAME_TITLE = "NXJ Map Command";

	private NXTHGSliderPanel setHeading, rotate, travelSpeed, rotateSpeed;
	private NXTHGSliderPanel_Drehen Drehen;
	private NXTHGSliderPanel_Seitw�rts seitw�rts;
	private JPanel leftPanel = new JPanel();
	private JPanel rightPanel = new JPanel();
	private JButton stopNavigatorButton = new JButton();	
	private JButton startNavigatorButton = new JButton();
	private JButton stopAllButton = new JButton();
	private JButton abladenButton = new JButton();
	private JButton einziehButton = new JButton();
	private JButton stopEinziehButton = new JButton();
	private JButton FahrenButton = new JButton();
	/*
	 * Create a MapTest object and display it in a GUI frame. Then connect to
	 * the NXT.
	 */
	public static void main(String[] args) {
		NXTHGToolStarter.startSwingTool(NXTHGMapCommand.class, args);
	}

	public static int start(String[] args) {
		return new NXTHGMapCommand().run();
	}

	public NXTHGMapCommand() {
		setTitle(FRAME_TITLE);
		setDescription("MapCommand allows remote control of robots from the PC\n"
				+ "from a GUI application that displays a map of the area \n"
				+ "that the robot is moving in.\n\n"
				+ "It displays many types of navigation data such as paths \n"
				+ "calculate, paths followed, features detected etc.");
		buildGUI();
	}

	/**
	 * Build the specific GUI for this application
	 */
	@Override
	protected void buildGUI() {
		setLayout(new BorderLayout());

		// Choose which features to show
		showMoves = true;
		showMesh = false;
		showZoomLabels = true;

		buildPanels();

		// Set the size of the map panel, and the viewport origin
		setMapPanelSize(MAP_AREA_SIZE);
		setMapOrigin(INITIAL_MAP_ORIGIN);

		// Add the required panels, configure them, and set their sizes
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(eventPanel, BorderLayout.NORTH);
		loadPanel.setPreferredSize(new Dimension(300, 80));
		leftPanel.add(loadPanel);
		connectPanel.setPreferredSize(new Dimension(300, 80));
		leftPanel.add(connectPanel);
		setHeading = new NXTHGSliderPanel(model, NavEvent.SET_POSE,	"Set Heading:", "Set", 360);
		setHeading.setPreferredSize(new Dimension(280, 80));
		commandPanel.add(setHeading);
		
	    rotate = new NXTHGSliderPanel(model, NavEvent.ROTATE_TO,
	    "Rotate To:", "Go", 360); rotate.setPreferredSize(new Dimension(280, 80)); 
	    commandPanel.add(rotate);		

		travelSpeed = new NXTHGSliderPanel(model, NavEvent.TRAVEL_SPEED,
		"Travel Speed", "Set", Integer.parseInt(props.getProperty(KEY_MAX_TRAVEL_SPEED, "60")));		
		travelSpeed.setPreferredSize(new Dimension(280, 80));
		commandPanel.add(travelSpeed);
		
		rotateSpeed = new NXTHGSliderPanel(model, NavEvent.ROTATE_SPEED,
		"Rotate Speed", "Set", Integer.parseInt(props.getProperty(KEY_MAX_ROTATE_SPEED, "360")));
		rotateSpeed.setPreferredSize(new Dimension(280, 80));
		commandPanel.add(rotateSpeed);

		startNavigatorButton = new JButton("Start Navigator");
		startNavigatorButton.addActionListener(this);
		startNavigatorButton.setPreferredSize(new Dimension(300, 40));
		leftPanel.add(startNavigatorButton);

		stopNavigatorButton = new JButton("STOP Navigator!");	
		stopNavigatorButton.addActionListener(this);
		stopNavigatorButton.setPreferredSize(new Dimension(300, 60));
		stopNavigatorButton.setBackground(new Color(150, 150, 255));
		leftPanel.add(stopNavigatorButton);

		abladenButton = new JButton("Abladen");
		abladenButton.addActionListener(this);
		abladenButton.setPreferredSize(new Dimension(300, 40));
		leftPanel.add(abladenButton);

		einziehButton = new JButton("Einziehen");
		einziehButton.addActionListener(this);
		einziehButton.setPreferredSize(new Dimension(300, 40));
		leftPanel.add(einziehButton);
		
		stopEinziehButton = new JButton("Stop Einziehen");
		stopEinziehButton.addActionListener(this);
		stopEinziehButton.setPreferredSize(new Dimension(300, 40));
		leftPanel.add(stopEinziehButton);
		
		FahrenButton = new JButton("Fahren");
		FahrenButton.addActionListener(this);
		FahrenButton.setPreferredSize(new Dimension(300, 40));
		leftPanel.add(FahrenButton);
		
		stopAllButton = new JButton("!    K  I  L  L  E  R    !");	
		stopAllButton.addActionListener(this);
		stopAllButton.setPreferredSize(new Dimension(300, 80));								// KILLER !
		stopAllButton.setBackground(new Color(255, 0, 0));
		leftPanel.add(stopAllButton);
				
//		stopFahrenButton = new JButton("Stop Fahren");
//		stopFahrenButton.addActionListener(this);
//		stopFahrenButton.setPreferredSize(new Dimension(300, 50));
//		leftPanel.add(stopFahrenButton);

		Drehen = new NXTHGSliderPanel_Drehen(model, NavEvent.DREHEN, "Drehen", "Set", 30);
		Drehen.setPreferredSize(new Dimension(280, 80));
		commandPanel.add(Drehen);

		seitw�rts = new NXTHGSliderPanel_Seitw�rts(model, NavEvent.SEITWAERTS, "zur Seite (cm):", "Set", 5);
		seitw�rts.setPreferredSize(new Dimension(280, 80));
		commandPanel.add(seitw�rts);

		commandPanel.setPreferredSize(new Dimension(300, 540));
		leftPanel.add(commandPanel);
		rightPanel.add(mapPanel, BorderLayout.CENTER);
		leftPanel.add(controlPanel);
		rightPanel.add(statusPanel, BorderLayout.SOUTH);
		leftPanel.setPreferredSize(new Dimension(320, 700));
		add(leftPanel, BorderLayout.WEST);
		add(rightPanel, BorderLayout.CENTER);
		controlPanel.setPreferredSize(new Dimension(300, 80));
		zoomSlider.setValue(INITIAL_ZOOM);
		
	}

	/**
	 * Send the pose when connected
	 */
	@Override
	public void whenConnected() {
		super.whenConnected();
		model.setPose(model.getRobotPose());
	}

	/**
	 * Set the sliders when the pose is changed
	 */
	@Override
	public void eventReceived(NavEvent navEvent) {
		if (navEvent == NavEvent.SET_POSE) {
			int heading = (int) model.getRobotPose().getHeading();
			if (heading < 0)
				heading += 360;
			rotate.setValue(heading);
			setHeading.setValue(heading);
		}
	}

	/**
	 * Add the required context menu items
	 */
	@Override
	protected void popupMenuItems(Point p, JPopupMenu menu) {
		menu.add(new NXTHGMenuAction(NavEvent.GOTO, "Go To", p, model, this));
		menu.add(new NXTHGMenuAction(NavEvent.SET_POSE, "Place robot", p,
				model, this));
		menu.add(new NXTHGMenuAction(NavEvent.ADD_WAYPOINT, "Add Waypoint", p,
				model, this));
		menu.add(new NXTHGMenuAction(NavEvent.SET_TARGET, "Set target", p,
				model, this));
	}

	public int run() {
		// Set debugging on to get information of events being processed
		model.setDebug(true);

		// Use MapTest.nxj from the bin directory as the NXJ program
		String home = SystemContext.getNxjHome();
		File progFile = new File(home, "bin" + File.separator + "MapTest.nxj");
		try {
			program = progFile.getCanonicalPath();
		} catch (IOException e) {
			// leave as is
		}

		// Open the panel in a frame
		openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, FRAME_TITLE,
				SystemColor.controlShadow, menuBar);
		return 0;
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println("Event:" + e.toString());
		super.actionPerformed(e);
		if (e.getSource() == startNavigatorButton) {
			model.startNavigator();
		}
		if (e.getSource() == stopNavigatorButton) {
			model.stop();
		}
		if (e.getSource() == stopAllButton) {
			model.stopAll();
		}
		if (e.getSource() == abladenButton) {
			model.paketab();
		}
		if (e.getSource() == einziehButton) {
			model.einziehen();
		}
		if (e.getSource() == stopEinziehButton) {
			model.stopEinziehen();
		}
		if (e.getSource() == FahrenButton) {
			model.Fahren();
		}

	}

}
