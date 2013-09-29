package de.nxthg.fahrer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import de.nxthg.greifer.GreiferEvents;

import lejos.nxt.Motor;
import lejos.nxt.comm.*;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RotatingRangeScanner;
import lejos.robotics.localization.*;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.Path;
import lejos.util.Delay;
import lejos.util.PilotProps;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;

/**
 * NXT version of the navigation model.
 * 
 * All local navigation objects, including pilots, navigators, path finders,
 * feature detectors, and range scanners can be added to the model.
 * 
 * Where possible, the model registers itself as an event listener and when the
 * event occurs, updates the model and sends the event and the updates to the
 * PC.
 * 
 * A receiver thread receives events from the PC, updates the local model, and
 * uses the navigation objects to implement the event if it involves robot
 * behaviour.
 * 
 * There are set methods to set various navigation parameters.
 * 
 * @author Lawrie Griffiths
 * 
 */
public class NXTHGNXTNavigationModel extends NXTHGNavigationModel implements
		MoveListener, NavigationListener, WaypointListener {
	protected Navigator navigator; // Only one navigator is allowed
	protected MoveController pilot; // Only one pilot is allowed
	protected PoseProvider pp; // Only one pose provider is allowed
	// protected ArrayList<FeatureDetector> detectors = new
	// ArrayList<FeatureDetector>(); // Multiple feature detectors allowed
	// protected PathFinder finder; // Only one local path finder is allowed
	protected RangeScanner scanner; // Only one scanner is allowed
	protected NXTHGNavEventListener listener;
	static int versatz;
	static final float[] versatzwinkel= {-200.0f, -175.0f,-150.0f, -125.0f, -100.0f,0.0f, 100.0f, 125.0f, 150.0f, 175.0f };
	protected float clearance = 10;
	protected float maxDistance = 40;
	protected boolean autoSendPose = true;
	protected boolean sendMoveStart = false, sendMoveStop = true;
	private Thread receiver;
	private boolean running = true;
	private Thread connectorGreifer;
	public DataInputStream disFahrer2Greifer;
	public DataOutputStream dosFahrer2Greifer;
	private DifferentialPilot robot;

	/**
	 * Create the model and start the receiver thread
	 */
	public NXTHGNXTNavigationModel() {
		receiver = new Thread(new Receiver());
		receiver.start();
		System.out.println("Erzeuge NXTHGNXTNavigationModel");
		System.out.flush();
		Delay.msDelay(1000);
		connectorGreifer = new Thread(new ConnectorGreifer());
		System.out.println("neuer Thread gemacht");
		System.out.flush();
//		Delay.msDelay(4000);
		connectorGreifer.start();
		System.out.println("Greifer gestartet");
		System.out.flush();
//		Delay.msDelay(4000);
	}

	/**
	 * Log a message
	 * 
	 * @param message
	 *            the message
	 */
	public void log(String message) {
		System.out.println(message);
	}

	/**
	 * Display an error message to the user
	 * 
	 * @param message
	 *            the error message
	 */
	public void error(String message) {
		System.out.println(message);
	}

	/**
	 * Display a fatal error and shut down the program
	 * 
	 * @param message
	 *            the error message
	 */
	public void fatal(String message) {
		System.out.println(message);
		Delay.msDelay(5000);
		System.exit(1);
	}

	/**
	 * Add a navigator to the model
	 * 
	 * @param navigator
	 *            the path controller
	 */

	public void addNavigator(Navigator navigator) {
		this.navigator = navigator;
		navigator.addNavigationListener(this);
		addPoseProvider(navigator.getPoseProvider());
		addPilot(navigator.getMoveController());
	}

	/**
	 * Add a pilot to the model
	 * 
	 * @param pilot
	 *            the move controller
	 */
	public void addPilot(MoveController pilot) {
		this.pilot = pilot;
		pilot.addMoveListener(this);
	}

	/**
	 * Add a pose provider (which might be MCL) to the model
	 * 
	 * @param pp
	 *            the pose provider
	 */
	@SuppressWarnings("hiding")
	public void addPoseProvider(PoseProvider pp) {
		this.pp = pp;
		/*if (pp instanceof MCLPoseProvider) {
			mcl = (MCLPoseProvider) pp;
			scanner = mcl.getScanner();
		}*/
	}

	/**
	 * Add a range scanner to the model
	 * 
	 * @param scanner
	 *            the range scanner
	 */
	@SuppressWarnings("hiding")
	public void addRangeScanner(RangeScanner scanner) {
		this.scanner = scanner;
	}

	/**
	 * Add a feature detector to the model
	 * 
	 * @param detector
	 *            the feature detector
	 */
	/*
	 * public void addFeatureDetector(FeatureDetector detector) {
	 * detectors.add(detector); detector.addListener(this); }
	 */

	/**
	 * Set parameters for a random move
	 * 
	 * @param maxDistance
	 *            the maximum distance of the move
	 * @param projection
	 *            the projection of the robot forward from its mid point
	 * @param border
	 *            the border around the wall that the robot should not move into
	 */
	public void setRandomMoveParameters(float maxDistance, float clearance) {
		this.maxDistance = maxDistance;
		this.clearance = clearance;
	}

	/**
	 * Set or unset automatic sending of the robot pose to the PC when a move
	 * stops
	 * 
	 * @param on
	 *            true if the pose is to be sent, else false
	 */
	public void setAutoSendPose(boolean on) {
		this.autoSendPose = on;
	}

	/**
	 * Sets whether events are sent to the PC when a move stops
	 * 
	 * @param on
	 *            true iff an event should be sent
	 */
	public void setSendMoveStart(boolean on) {
		sendMoveStart = on;
	}

	/**
	 * Sets whether events are sent to the PC when a move starts
	 * 
	 * @param on
	 *            true iff an event should be sent
	 */
	public void setSendMoveStop(boolean on) {
		sendMoveStop = on;
	}

	/**
	 * Shut down the receiver thread
	 */
	public void shutDown() {
		running = false;
	}

	@SuppressWarnings("hiding")
	public void addListener(NXTHGNavEventListener listener) {
		this.listener = listener;
	}

	/**
	 * The Receiver thread receives events from the PC
	 * 
	 * @author Lawrie Griffiths
	 * 
	 */
	class Receiver implements Runnable {
		@SuppressWarnings("incomplete-switch")
		public void run() {
			NXTCommConnector connector = Bluetooth.getConnector();
			NXTConnection conn = connector.waitForConnection(0,
					NXTConnection.PACKET);
			disPC2Fahrer = conn.openDataInputStream();
			dosPC2Fahrer = conn.openDataOutputStream();
			if (listener != null)
				listener.whenConnected();
			if (debug)
				log("Connected");
			while (running) {
				try {
					// Wait for any outstanding apply moves
					/*if (mcl != null && mcl.isBusy())
						Thread.yield();*/
					System.out.println("in PC schleife");
					byte event = disPC2Fahrer.readByte();
					NavEvent navEvent = NavEvent.values()[event];
					System.out.println(navEvent);
					System.out.flush();
					if (debug)
						log(navEvent.name());
					if (listener != null)
						listener.eventReceived(navEvent);

					synchronized (this) {
						switch (navEvent) {
						case LOAD_MAP: // Map sent from PC
							if (map == null)
								map = new LineMap();
							map.loadObject(disPC2Fahrer);
							/*if (mcl != null)
								mcl.setMap(map);*/
							break;
						case GOTO: // Update of target and request to go to the
									// new target
							System.out.println(Motor.A.getAcceleration());
							if (target == null)
								target = new Waypoint(0, 0);
							target.loadObject(disPC2Fahrer);
							if (navigator != null)
								navigator.goTo(target);
							break;
						case STOP: // Request to stop the robot
							if (navigator != null)
								navigator.stop();
							if (pilot != null)
								pilot.stop();
							break;
						case TRAVEL:
							
							float distance = disPC2Fahrer.readFloat();
							if (pilot != null)
								pilot.travel(distance);
							break;
						case ROTATE: // Request to rotate a given angle
							float angle = disPC2Fahrer.readFloat();
							if (pilot != null
									&& pilot instanceof RotateMoveController)
								((RotateMoveController) pilot).rotate(angle);
							break;
						case ARC: // Request to travel an arc og given radius
									// and angle
							float radius = disPC2Fahrer.readFloat();
							angle = disPC2Fahrer.readFloat();
							if (pilot != null
									&& pilot instanceof ArcMoveController)
								((ArcMoveController) pilot).arc(radius, angle);
							break;
						case ROTATE_TO: // Request to rotate to a given angle
							angle = disPC2Fahrer.readFloat();
							if (pp != null && pilot != null
									&& pilot instanceof RotateMoveController)
								((RotateMoveController) pilot)
										.rotate(angleTo(angle));
							break;
						case GET_POSE: // Request to get the pose and return it
										// to the PC
							if (pp == null)
								break;
							// Suppress sending moves to PC while taking
							// readings
							boolean saveSendMoveStart = sendMoveStart;
							boolean saveSendMoveStop = sendMoveStop;
							sendMoveStart = false;
							sendMoveStop = false;
							currentPose = pp.getPose();
							sendMoveStart = saveSendMoveStart;
							sendMoveStop = saveSendMoveStop;
							dosPC2Fahrer.writeByte(NavEvent.SET_POSE.ordinal());
							currentPose.dumpObject(dosPC2Fahrer);
							break;
						case SET_POSE: // Request to set the current pose of the                                TUT DAS? ? ? ES MUSS FUNKTIONIEREN ! ! !
										// robot
							if (currentPose == null)
								currentPose = new Pose(0, 0, 0);
							currentPose.loadObject(disPC2Fahrer);
							if (pp != null)
								pp.setPose(currentPose);
							break;
						case ADD_WAYPOINT: // Request to add a waypoint
							Waypoint wp = new Waypoint(0, 0);
							wp.loadObject(disPC2Fahrer);
							if (navigator != null)
								navigator.addWaypoint(wp);
							break;
						case FIND_CLOSEST: // Request to find particle by
											// co-ordinates and
											// send its details to the PC
							float x = disPC2Fahrer.readFloat();
							float y = disPC2Fahrer.readFloat();
							/*if (particles != null) {
								dos.writeByte(NavEvent.CLOSEST_PARTICLE
										.ordinal());
								particles.dumpClosest(readings, dos, x, y);
							}*/
							break;
						/*case PARTICLE_SET: // Particle set send from PC
							if (particles == null)
								particles = new MCLParticleSet(map, 0, 0);
							particles.loadObject(dis);
							mcl.setParticles(particles);
							break;*/
						case TAKE_READINGS: // Request to take range readings
											// and send them to the PC
							if (scanner != null) {
								readings = scanner.getRangeValues();
								dosPC2Fahrer.writeByte(NavEvent.RANGE_READINGS.ordinal());
								dosFahrer2Greifer.flush();
								readings.dumpObject(dosPC2Fahrer);
							}
							break;
						/*case GET_READINGS: // Request to send current readings
											// to the PC
							dos.writeByte(NavEvent.RANGE_READINGS.ordinal());
							if (mcl != null)
								readings = mcl.getRangeReadings();
							readings.dumpObject(dos);
							break;*/
						/*case GET_PARTICLES: // Request to send particles to the
											// PC
							if (particles == null)
								break;
							dos.writeByte(NavEvent.PARTICLE_SET.ordinal());
							particles.dumpObject(dos);
							break;
						case GET_ESTIMATED_POSE: // Request to send estimated
													// pose to the PC
							if (mcl == null)
								break;
							dos.writeByte(NavEvent.ESTIMATED_POSE.ordinal());
							mcl.dumpObject(dos);
							break;*/
						/*
						 * case FIND_PATH: // Find a path to the target if
						 * (target == null) target = new Waypoint(0,0);
						 * target.loadObject(dis); if (finder != null) {
						 * dos.writeByte(NavEvent.PATH.ordinal()); try { path =
						 * finder.findRoute(currentPose, target);
						 * path.dumpObject(dos); } catch
						 * (DestinationUnreachableException e) {
						 * dos.writeInt(0); } } break;
						 */
						case FOLLOW_PATH: // Follow a route sent from the PC
							if (path == null)
								path = new Path();
							path.loadObject(disPC2Fahrer);
							if (navigator != null)
								navigator.followPath(path);
							break;
						case START_NAVIGATOR:
//							System.out.println(Motor.A.getAcceleration());
//							System.out.flush();
//							Delay.msDelay(2000);
							if (navigator != null)
								navigator.followPath();
							break;
						case CLEAR_PATH: // Clear the current path in the
											// navigator
							if (navigator != null)
								navigator.clearPath();
							break;
						case RANDOM_MOVE: // Request to make a random move
							randomMove();
							break;
						/*case LOCALIZE:
							localize();
							break;*/
						case EXIT:
							System.exit(0);
						/*case SOUND:
							Sound.systemSound(false, dis.readInt());
							break;*/
						/*
						 * case GET_BATTERY:
						 * dos.writeByte(NavEvent.BATTERY.ordinal());
						 * dos.writeFloat(Battery.getVoltage()); dos.flush();
						 * break;
						 */
						case PILOT_PARAMS:
							float wheelDiameter = disPC2Fahrer.readFloat();
							float trackWidth = disPC2Fahrer.readFloat();
							int leftMotor = disPC2Fahrer.readInt();
							int rightMotor = disPC2Fahrer.readInt();
							boolean reverse = disPC2Fahrer.readBoolean();
							PilotProps props = new PilotProps();
							String[] motors = { "A", "B", "C" };
							props.setProperty(PilotProps.KEY_WHEELDIAMETER, ""
									+ wheelDiameter);
							props.setProperty(PilotProps.KEY_TRACKWIDTH, ""
									+ trackWidth);
							props.setProperty(PilotProps.KEY_LEFTMOTOR,
									motors[leftMotor]);
							props.setProperty(PilotProps.KEY_RIGHTMOTOR,
									motors[rightMotor]);
							props.setProperty(PilotProps.KEY_REVERSE, ""
									+ reverse);
							props.storePersistentValues();
							break;
						/*
						 * case RANGE_FEATURE_DETECTOR_PARAMS: int delay =
						 * dis.readInt(); float maxDist = dis.readFloat(); for
						 * (FeatureDetector detector : detectors) { if (detector
						 * instanceof RangeFeatureDetector) {
						 * ((RangeFeatureDetector) detector).setDelay(delay);
						 * ((RangeFeatureDetector)
						 * detector).setMaxDistance(maxDist); } } break;
						 */
						case RANGE_SCANNER_PARAMS:
							int gearRatio = disPC2Fahrer.readInt();
							int headMotor = disPC2Fahrer.readInt();
							RegulatedMotor[] regulatedMotors = { Motor.A,
									Motor.B, Motor.C };
							if (scanner instanceof RotatingRangeScanner) {
								((RotatingRangeScanner) scanner)
										.setGearRatio(gearRatio);
								((RotatingRangeScanner) scanner)
										.setHeadMotor(regulatedMotors[headMotor]);
							}
							break;
						case TRAVEL_SPEED:
							float travelSpeed = disPC2Fahrer.readFloat();
							if (pilot != null)
								pilot.setTravelSpeed(travelSpeed);
							break;
						case ROTATE_SPEED:
							float rotateSpeed = disPC2Fahrer.readFloat();
							if (pilot != null
									&& pilot instanceof RotateMoveController) {
								((RotateMoveController) pilot)
										.setRotateSpeed(rotateSpeed);
							}
							break;

						case RANDOM_MOVE_PARAMS:
							maxDistance = disPC2Fahrer.readFloat();
							clearance = disPC2Fahrer.readFloat();
							break;

						case AUFLADEN_PAKET:
							int groesse = disPC2Fahrer.readInt();
							int regal = disPC2Fahrer.readInt();
							if (groesse == 1) {
								if (regal == 1) {
									dosFahrer2Greifer.writeByte(GreiferEvents.KISTE_KLEIN_UNTEN
													.ordinal());
									dosFahrer2Greifer.flush();
									System.out.print("1 1 An Greifer gesendet");
								}
							}

							if (groesse == 2) {
								if (regal == 1) {
									dosFahrer2Greifer.writeByte(GreiferEvents.KISTE_MITTEL_UNTEN
													.ordinal());
									dosFahrer2Greifer.flush();
									System.out.print("2 1 An Greifer gesendet");
								}
							}

							if (groesse == 2) {
								if (regal == 2) {
									dosFahrer2Greifer.writeByte(GreiferEvents.KISTE_MITTEL_MITTE
											.ordinal());
							dosFahrer2Greifer.flush();
							System.out.print("2 2 An Greifer gesendet");

								}
							}

							if (groesse == 3) {
								if (regal == 3) {
									dosFahrer2Greifer.writeByte(GreiferEvents.KISTE_GROSS_OBEN
													.ordinal());
									dosFahrer2Greifer.flush();
									System.out.print("3 3 An Greifer gesendet");
								}
							}

							break;

							
						case EINZIEHEN:
							dosFahrer2Greifer.writeByte(GreiferEvents.EINZIEHEN.ordinal());
							dosFahrer2Greifer.flush();
//							Delay.msDelay(4000);
							System.out.println("Einziehen an Greifer weitergesendet");
							break;
							
						case STOP_EINZIEHEN:
							dosFahrer2Greifer.writeByte(GreiferEvents.STOP_EINZIEHEN.ordinal());
							break;
							
						case ABLADEN_PAKET:
							System.out.println("  Befehl ABLADEN_PAKET bekommen ");
//							Delay.msDelay(4000);
							dosFahrer2Greifer.writeByte(GreiferEvents.ABLADEN.ordinal());
							dosFahrer2Greifer.flush();
//							Delay.msDelay(4000);
							System.out.println(" Befehl ABLADEN_PAKET an greifer gesendet");
							break;
							
							
						case SEITWAERTS:
							versatz = disPC2Fahrer.readByte();
							new Thread (new Seitwaerts()).start();
							System.out.println("Seitwärts gelesen, thread gestartet");
							System.out.flush();
							break;
							
						case DREHEN:
							int winkel = disPC2Fahrer.readInt();
							robot.rotate(winkel, true);
							break;

						}
					}
				} catch (IOException ioe) {
					fatal("IOException in receiver:");
				}
			}
		}
	}
	
	
	
	class Seitwaerts implements Runnable {
		public void run() {
			
		robot.rotate(versatzwinkel[versatz+5]);
		navigator.getMoveController().travel(-15);
		robot.rotate(-2*versatzwinkel[versatz+5]);
		navigator.getMoveController().travel(15);
		robot.rotate(versatzwinkel[versatz+5]);
		}
	}

	class ConnectorGreifer implements Runnable {
		@SuppressWarnings("incomplete-switch")
		public void run() {
			String name = "Greifer";
			System.out.println("Vor RS485");
			Delay.msDelay(1000);
			NXTConnection con = RS485.getConnector().connect(name,
					NXTConnection.PACKET);
			System.out.println("Connected ");

			disFahrer2Greifer = con.openDataInputStream();
			dosFahrer2Greifer = con.openDataOutputStream();
			running = true;
			while (running){
				try {
					System.out.println("In Greifer Schleife");
					byte event = disFahrer2Greifer.readByte();
					System.out.println("Befehl empfangen");
//					Delay.msDelay(2000);
					GreiferEvents gevent = GreiferEvents.values()[event];
					System.out.println(gevent);
					System.out.flush();
				//	synchronized (this) {
						switch (gevent) {
						case FAHR_ZURUECK_5CM:
							new Thread(new FahrZurueck5CM()).start();
							break;
						case FAHR_VOR_5CM:
							new Thread(new FahrVor5CM()).start();
							break;
						
						case FAHR_VOR:
							new Thread(new FahrVor()).start();
							break;

						case FAHR_ZURUECK:
							new Thread(new FahrZurueck()).start();
							break;
						
						case STOP:
							System.exit(0);
							break;
						//}
					}
				} catch (IOException ioe) {
					fatal("IOException in receiver:");
				}
			}

			// @Override

			// TODO Auto-generated method stub
			System.out.println("ReceiverGreifer stopped");
		}

		
		class FahrZurueck5CM implements Runnable {
			public void run() {

				navigator.getMoveController().travel(-5,true);
				warten();
				try {
					dosFahrer2Greifer.writeByte(GreiferEvents.FAHRER_HINTEN_5CM
							.ordinal());
					dosFahrer2Greifer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
					

			}
		}
		
		class FahrVor5CM implements Runnable {
			public void run() {

				navigator.getMoveController().travel(10,true);
				warten();
				try {
					dosFahrer2Greifer.writeByte(GreiferEvents.FAHRER_VORNE_5CM.ordinal());
					dosFahrer2Greifer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					

			}
		}
		
		class FahrVor implements Runnable {
			public void run() {
				navigator.getMoveController().travel(10,true);
				warten();
				try {
					dosFahrer2Greifer.writeByte(GreiferEvents.FAHRER_VORNE
							.ordinal());
					dosFahrer2Greifer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
		
		class FahrZurueck implements Runnable {
			public void run() {
				navigator.getMoveController().travel(-10,true);
				warten();
				try {
					dosFahrer2Greifer.writeByte(GreiferEvents.FAHRER_HINTEN
							.ordinal());
					dosFahrer2Greifer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
		
		private void warten() {
			while (navigator.getMoveController().isMoving()){
				Delay.msDelay(200);
			}
		}

	}

	private void randomMove() {
		if (pilot != null && pilot instanceof RotateMoveController) {
			float angle = (float) Math.random() * 360;
			float distance = (float) Math.random() * maxDistance;
			// readings = mcl.getReadings();

			if (angle > 180f)
				angle -= 360f;

			float forwardRange;
			// Get forward range
			try {
				forwardRange = readings.getRange(0f); // Range for angle 0
														// (forward)
			} catch (Exception e) {
				forwardRange = 0;
			}

			// Don't move forward if we are near a wall
			if (forwardRange < 0 || distance + clearance < forwardRange)
				pilot.travel(distance);

			((RotateMoveController) pilot).rotate(angle);
			if (debug)
				log("Random moved done");
		}
	}

	/*private void localize() {
		boolean saveSendMoveStart = sendMoveStart;
		boolean saveSendMoveStop = sendMoveStop;
		sendMoveStart = false;
		sendMoveStop = false;
		while (true) {
			try {
				mcl.getPose();
				dos.writeByte(NavEvent.PARTICLE_SET.ordinal());
				particles.dumpObject(dos);
				readings = mcl.getReadings();
				dos.writeByte(NavEvent.RANGE_READINGS.ordinal());
				readings.dumpObject(dos);
				if (goodEstimate()) {
					// Send the estimate to the PC
					dos.writeByte(NavEvent.ESTIMATED_POSE.ordinal());
					mcl.dumpObject(dos);
					dos.writeByte(NavEvent.LOCATED.ordinal());
					dos.flush();
					break;
				}
				randomMove();
				dos.writeByte(NavEvent.PARTICLE_SET.ordinal());
				particles.dumpObject(dos);
			} catch (IOException ioe) {
				fatal("IOException in localize");
			}
		}
		sendMoveStart = saveSendMoveStart;
		sendMoveStop = saveSendMoveStop;
	}*/

	/*private boolean goodEstimate() {
		// float sx = mcl.getSigmaX();
		// float sy = mcl.getSigmaY();
		float xr = mcl.getXRange();
		float yr = mcl.getYRange();
		return xr < 50 && yr < 50;
	}
*/
	// Calculate the angle for ROTATE_TO
	private int angleTo(float angle) {
		int angleTo = ((int) (angle - pp.getPose().getHeading())) % 360;
		return (angleTo < 180 ? angleTo : angleTo - 360);
	}

	/**
	 * Called when the pilot starts a move
	 */
	public void moveStarted(Move event, MoveProvider mp) {
		if (!sendMoveStart)
			return;
		try {
			synchronized (receiver) {
				if (debug)
				//	log("Sending move started");
				dosPC2Fahrer.writeByte(NavEvent.MOVE_STARTED.ordinal());
				event.dumpObject(dosPC2Fahrer);
			}
		} catch (IOException ioe) {
			fatal("IOException in moveStarted");
		}
	}

	/**
	 * Called when a move stops
	 */
	public void moveStopped(Move event, MoveProvider mp) {
		if (!sendMoveStop)
			return;
		try {
			synchronized (receiver) {
				if (debug)
					//log("Sending move stopped");
				dosPC2Fahrer.writeByte(NavEvent.MOVE_STOPPED.ordinal());
				event.dumpObject(dosPC2Fahrer);
				if (pp != null && autoSendPose) {
					if (debug)
						//log("Sending set pose");
					dosPC2Fahrer.writeByte(NavEvent.SET_POSE.ordinal());
					pp.getPose().dumpObject(dosPC2Fahrer);
				}
			}
		} catch (IOException ioe) {
			fatal("IOException in moveStopped");
		}
	}

	/**
	 * Called when a feature is detected. Only range features currently
	 * supported
	 */
	@SuppressWarnings("hiding")
	/*
	 * public void featureDetected(Feature feature, FeatureDetector detector) {
	 * if (dos == null) return; if (!(feature instanceof RangeFeature)) return;
	 * float range = ((RangeFeature) feature).getRangeReading().getRange(); if
	 * (range < 0) return; if (pilot == null || !pilot.isMoving()) { if (range
	 * == oldRange) return; } oldRange = range; try { synchronized(receiver) {
	 * dos.writeByte(NavEvent.FEATURE_DETECTED.ordinal()); ((RangeFeature)
	 * feature).dumpObject(dos); } } catch (IOException ioe) {
	 * fatal("IOException in featureDetected"); } }
	 */
	/**
	 * Send a waypoint generated on the NXT to the PC
	 */
	public void addWaypoint(Waypoint wp) {
		try {
			synchronized (receiver) {
				dosPC2Fahrer.writeByte(NavEvent.ADD_WAYPOINT.ordinal());
				// TODO: send waypoint to the PC
				dosPC2Fahrer.flush();
			}
		} catch (IOException ioe) {
			fatal("IOException in addWaypoint");
		}
	}

	/**
	 * Called when a waypoint has been reached
	 */
	public void atWaypoint(Waypoint waypoint, Pose pose, int sequence) {
		try {
			synchronized (receiver) {
				dosPC2Fahrer.writeByte(NavEvent.WAYPOINT_REACHED.ordinal());
				waypoint.dumpObject(dosPC2Fahrer);
			}
		} catch (IOException ioe) {
			fatal("IOException in atWaypoint");
		}
	}

	/**
	 * Called when a path has been completed
	 */
	public void pathComplete(Waypoint waypoint, Pose pose, int sequence) {
		try {
			synchronized (receiver) {
				dosPC2Fahrer.writeByte(NavEvent.PATH_COMPLETE.ordinal());
				dosPC2Fahrer.flush();
			}
		} catch (IOException ioe) {
			fatal("IOException in pathComplete");
		}
	}

	/**
	 * Called when a path has been interrupted
	 */
	public void pathInterrupted(Waypoint waypoint, Pose pose, int sequence) {
		try {
			synchronized (receiver) {
				dosPC2Fahrer.writeByte(NavEvent.PATH_INTERRUPTED.ordinal());
				dosPC2Fahrer.flush();
			}
		} catch (IOException ioe) {
			fatal("IOException in pathInterrupted");
		}
	}

	/**
	 * Called when a path finder has finished generating a path
	 */
	public void pathGenerated() {
		try {
			synchronized (receiver) {
				dosPC2Fahrer.writeByte(NavEvent.PATH_GENERATED.ordinal());
				dosPC2Fahrer.flush();
			}
		} catch (IOException ioe) {
			fatal("IOException in pathGenerated");
		}
	}
}
