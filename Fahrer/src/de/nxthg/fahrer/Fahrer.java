package de.nxthg.fahrer;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.robotics.RegulatedMotor;
import de.nxthg.fahrer.NXTHGNavigationModel.NavEvent;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.util.PilotProps;

/**
 * Used with the MapTest or PathTest PC samples, or other PC mapping applications.
 * 
 * Sends moves, updated poses, features detected, waypoints reached and other navigation events
 * to the PC.
 * 
 * Receives instructions as events from the PC.
 * 
 * @author Lawrie Griffiths
 */
public class Fahrer implements NXTHGNavEventListener {
	//public static final float MAX_DISTANCE = 50f;
	//public static final int DETECTOR_DELAY = 1000;
	
	private NXTHGNXTNavigationModel model;
	
	public static void main(String[] args) throws Exception {
		(new Fahrer()).run();
	}
	
public void run() throws Exception {
    	model = new NXTHGNXTNavigationModel();
    	model.addListener(this);
    	model.setDebug(true);
    	model.setSendMoveStart(true);

    	Button.waitForAnyPress();
    	model.shutDown();
	}

	public void whenConnected() {
    	PilotProps pp = new PilotProps();
    	try {
    		pp.loadPersistentValues();
    	} catch (IOException ioe) {
    		System.exit(1);
    	}
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.96"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "13.0"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
    	final DifferentialPilot robot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
    	final Navigator navigator = new Navigator(robot);
    	// UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
    	// RangeFeatureDetector detector = new RangeFeatureDetector(sonic, MAX_DISTANCE, DETECTOR_DELAY);
		
    	// Adding the navigator, adds the pilot and pose provider as well
    	model.addNavigator(navigator);
    	
    	// Add the feature detector and start it. 
    	// Give it a pose provider, so that it records the pose when a feature was detected
    	/*model.addFeatureDetector(detector);
    	detector.enableDetection(true);
    	detector.setPoseProvider(navigator.getPoseProvider());
    	
    	// Stop if an obstacle is detected, unless doing a rotate
    	detector.addListener(new FeatureListener() {
			public void featureDetected(Feature feature, FeatureDetector detector) {
				if (robot.isMoving() && robot.getMovement().getMoveType() != MoveType.ROTATE) {
					robot.stop();
					if (navigator.isMoving()) navigator.stop();
				}					
			}		
    	});*/
	} 

	public void eventReceived(NavEvent navEvent) {
		// Nothing
	}
}
