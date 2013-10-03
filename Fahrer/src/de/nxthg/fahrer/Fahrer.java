package de.nxthg.fahrer;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import de.nxthg.fahrer.NXTHGNavigationModel.NavEvent;
import de.nxthg.greifer.GreiferEvents;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.util.Delay;
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
	private DifferentialPilot robot;
	private Navigator navigator;
	
	public static void main(String[] args) throws Exception {
//		UltrasonicSensor us1 = new UltrasonicSensor(SensorPort.S1);
//		UltrasonicSensor us2 = new UltrasonicSensor(SensorPort.S2);
//		UltrasonicSensor us3 = new UltrasonicSensor(SensorPort.S3);
		System.out.println("starte Programm");
		Fahrer myFahrer = new Fahrer();
		System.out.println("Fahrer erzeugt");
		System.out.flush();
		Delay.msDelay(4000);
		myFahrer.run();
	}
	
	
public void run() throws Exception {
		System.out.println("in run");
		System.out.flush();
		Delay.msDelay(4000);
    	model = new NXTHGNXTNavigationModel();
    	System.out.println("model erzeugt");
    	System.out.flush();
    	Sound.beep();
    //	Delay.msDelay(4000);
    	model.addListener(this);
    	model.setDebug(true);
    	model.setSendMoveStart(true);
	}

	public void whenConnected() {
    	PilotProps pp = new PilotProps();
    	try {
    		pp.loadPersistentValues();
    	} catch (IOException ioe) {
    		System.exit(1);
    	}
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "8.1"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "23.6"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
    	System.out.println("Beschleunigung auf 5");
    	System.out.flush();
    	Delay.msDelay(2000);
    	robot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
    	navigator = new Navigator(robot);
    	
    	
    	// UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
    	// RangeFeatureDetector detector = new RangeFeatureDetector(sonic, MAX_DISTANCE, DETECTOR_DELAY);
		
    	// Adding the navigator, adds the pilot and pose provider as well
    	model.addNavigator(navigator);
    	
    	
    	robot.setTravelSpeed(100);
    	leftMotor.setAcceleration(500);
    	rightMotor.setAcceleration(500);
    	
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
