package de.nxthg.greifer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.USB;
import lejos.util.Delay;
import lejos.util.TextMenu;

public class Greifer {
	static int ziehdrehungen = 2 * 360; // wie viele umdrehung um ein packet auf
										// die kleine zwischenladefläche zu
										// ziehen?????
	static NXTRegulatedMotor MJustieren = Motor.C; // Antrieb der Ketten auf der
													// Ladefläche
	static NXTRegulatedMotor MArmLinks = Motor.A; // Antrieb für die Kette am
													// linken Arm; forward ist
													// rein/raus????
	static NXTRegulatedMotor MArmRechts = Motor.B;
	static int aussen = -21000;
	static int greifenKlein = 20; // Wie viel um ein kleines Paket zu greifen
									// auseinander????
	static int greifenMittel = 50; // Wie viel um ein mittleres Paket zu
									// greifen auseinander ????
	static int greifenGross = -19000; // Wie viel um ein großes Paket zu greifen
										// auseinander????
	static int greifen;
	static int außen = 500;
	static int kisteBreite;
	static int kisteTiefe;
	static int kisteTiefeMittel = 30;
	static int kisteTiefeGross = 50;
	private static DataInputStream disGreifer2Fahrer;
	private static DataOutputStream dosGreifer2Fahrer;
	private static DataInputStream disGreifer2Lift;
	private static DataOutputStream dosGreifer2Lift;
	private boolean running;
	private Thread receiverFahrer;
	private Thread receiverLift;
	private static Greifer model;
	static boolean unverbunden = true;

	public Greifer() {

		receiverLift = new Thread(new ConnectorLift());
		receiverLift.start();
		System.out.println("erzeuge greifer");
		System.out.flush();
		Delay.msDelay(4000);
		/*
		 * while (unverbunden) { Delay.msDelay(100); }
		 * System.out.println("Mit Lift Verbunden" +
		 * "Warte auf Verbindung zum Fahrer");
		 */
		receiverFahrer = new Thread(new ReceiverFahrer());
		System.out.println("neuer Thread gemacht");
		System.out.flush();
		Delay.msDelay(4000);
		receiverFahrer.start();
		System.out.println("Fahrer gestartet");
		System.out.flush();
		Delay.msDelay(4000);
	}

	public static void main(String[] args) {
		Motor.C.setSpeed(3000);
		LCD.drawInt(MJustieren.getTachoCount(), 0, 0);
		model = new Greifer();

	} // end of main

	private void shutDown() {
		// TODO Auto-generated method stub
		running = false;
	}

	class ReceiverFahrer implements Runnable {
		public void run() {
			System.out.println("run in Receiver");
			NXTConnection con = RS485.getConnector().waitForConnection(0,
					NXTConnection.PACKET);

			System.out.println(" Mit Fahrer Verbunden ");
			disGreifer2Fahrer = con.openDataInputStream();
			dosGreifer2Fahrer = con.openDataOutputStream();
			System.out.println(" dis und dos erstellt ");
			running = true;
			while (running) {
				try {
					 System.out.println("In fahrer schleife");
					// Delay.msDelay(4000);
					byte event = disGreifer2Fahrer.readByte();
					// Delay.msDelay(4000);
					// System.out.println("warte auf gevent");
					// System.out.flush();
					GreiferEvents gevent = GreiferEvents.values()[event];
					System.out.println(gevent);
					System.out.flush();
					// synchronized (this) {
					switch (gevent) {

					case KISTE_KLEIN_UNTEN: // vom Fahrer
						new Thread(new KisteKleinUnten()).start();
						// Zu Lift

						break;
					case KISTE_MITTEL_UNTEN:
						new Thread(new KisteMittelUnten()).start();
						break;

					case KISTE_MITTEL_MITTE:
						new Thread(new KisteMittelMitte()).start();
						break;

					case KISTE_GROSS_OBEN:
						new Thread(new KisteGrossOben()).start();
						break;
						

					case FAHRER_VORNE:
						new Thread(new FahrerVorne()).start();
						break;

					case FAHRER_HINTEN_5CM:
						new Thread(new FahrerHinten5CM()).start();
						break;

					case FAHRER_VORNE_5CM:
						new Thread(new FahrerVorne5CM()).start();
						break;

						//					case STOP_EINZIEHEN:
//						stopArme();
//						dosGreifer2Fahrer.writeByte(GreiferEvents.FAHR_ZURUECK
//								.ordinal());
//						dosGreifer2Fahrer.flush();
//						break;

					case FAHRER_HINTEN:
						dosGreifer2Lift
								.writeByte(GreiferEvents.AUF_CARGOAREA_FAHREN
										.ordinal());
						dosGreifer2Lift.flush();
						break;

					case STOP:
						dosGreifer2Lift.writeByte(GreiferEvents.STOP.ordinal());
						dosGreifer2Lift.flush();
						System.exit(0);
						break;

					case ABLADEN:
						new Thread(new Abladen()).start();
						break;

					default:
						LCD.drawString("Unbekannter Befehl", 1, 1);
						break;
					// }
					}
				} catch (IOException ioe) {
					fatal("IOException in receiver:");
				}
			}
			System.out.println("ReceiverFahrer stopped");
		}

		class KisteKleinUnten implements Runnable {
			public void run() {

				System.out.println("  Befehl Aufladen bekommen ");
				justieren(greifenMittel + 5);
				kisteBreite = greifenMittel;
				kisteTiefe = kisteTiefeMittel;
				try {
					dosGreifer2Lift
							.writeByte(GreiferEvents.AUF_KISTENHOEHE_FAHREN_UNTEN
									.ordinal());
					dosGreifer2Lift.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

			}
		}
		
		class KisteMittelUnten implements Runnable {
			public void run() {

				System.out.println("  Befehl Aufladen bekommen ");
				justieren(greifenKlein + 5);
				kisteBreite = greifenKlein;
				kisteTiefe = kisteTiefeMittel;
				try {
					dosGreifer2Lift
							.writeByte(GreiferEvents.AUF_KISTENHOEHE_FAHREN_UNTEN
									.ordinal());
					dosGreifer2Lift.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		
		class KisteMittelMitte implements Runnable {
			public void run() {

				System.out.println("  Befehl Aufladen bekommen ");
				justieren(greifenMittel + 5);
				kisteBreite = greifenMittel;
				kisteTiefe = kisteTiefeMittel;
				try {
					dosGreifer2Lift
							.writeByte(GreiferEvents.AUF_KISTENHOEHE_FAHREN_MITTE
									.ordinal());
					dosGreifer2Lift.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				

			}
		}
		
		class KisteGrossOben implements Runnable {
			public void run() {

				System.out.println("  Befehl Aufladen bekommen ");
				System.out.flush();
				justieren(aussen);
				kisteBreite = greifenGross;
				kisteTiefe = kisteTiefeGross;
				try {
					dosGreifer2Lift
							.writeByte(GreiferEvents.AUF_KISTENHOEHE_FAHREN_OBEN
									.ordinal());
					dosGreifer2Lift.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				

			}
		}
		
		class FahrerVorne implements Runnable {
			public void run() {

				justieren(kisteBreite);
				// reinArme(9999999,true);
				try {
					dosGreifer2Fahrer
							.writeByte(GreiferEvents.FAHR_ZURUECK_5CM
									.ordinal());
					dosGreifer2Fahrer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				

			}
		}
		
		class FahrerHinten5CM implements Runnable {
			public void run() {

				justieren(kisteBreite + 10);
				try {
					dosGreifer2Fahrer.writeByte(GreiferEvents.FAHR_VOR_5CM
							.ordinal());
					dosGreifer2Fahrer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
				

			}
		}
		
		class FahrerVorne5CM implements Runnable {
			public void run() {

				justieren(kisteBreite);
				reinArme(kisteTiefe, true);
				
				 try {
					 dosGreifer2Fahrer.writeByte(GreiferEvents.FAHR_ZURUECK.ordinal());
					dosGreifer2Fahrer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
				

			}
		}
		
		class Abladen implements Runnable {
			public void run() {

				System.out.println("Befehl ABLADEN bekommen");
				justieren(aussen);
				//System.out.println("Nach justieren");
				//System.out.flush();
				try {
					dosGreifer2Lift.writeByte(GreiferEvents.ABLADEN
							.ordinal());
					dosGreifer2Lift.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
				System.out.println(" Befehl ABLADEN an lift gesendet");
				System.out.flush();
			}
		}
	}

	class ConnectorLift implements Runnable {
		public void run() {
			NXTCommConnector connector = Bluetooth.getConnector();
			System.out.println("Suche Lift");
			NXTConnection conn = connector
					.connect("Lift", NXTConnection.PACKET);
			System.out.println("Verbindung hergestellt");
			unverbunden = false;
			System.out.flush();
			disGreifer2Lift = conn.openDataInputStream();
			dosGreifer2Lift = conn.openDataOutputStream();

			running = true;
			while (running) {
				try {
					System.out.println("In lift Schleife");
					byte event = disGreifer2Lift.readByte();
					GreiferEvents gevent = GreiferEvents.values()[event];
					System.out.println(gevent);
					System.out.flush();
					synchronized (this) {
						switch (gevent) {

						case AUF_KISTENHOEHE:
							System.out.println("Lift auf Kistenhöhe");
							dosGreifer2Fahrer.writeByte(GreiferEvents.FAHR_VOR
									.ordinal());
							dosGreifer2Fahrer.flush();
							System.out.println("Fahrer soll vor");
							System.out.flush();
							// dosLift.writeByte(GreiferEvents.AUF_CARGOAREA_FAHREN.ordinal());
							break; // Signal zum Hoch/runterziehen an Lift auf
									// cargoarea

						case AUF_CARGOAREA: // Sobald signal komm dass hebebühne
											// auf cargoarea-höhe ist:
							reinArme(kisteTiefe, true);
							dosGreifer2Lift
									.writeByte(GreiferEvents.CARGOAREA_REIN
											.ordinal());
							dosGreifer2Fahrer.flush();
							// justieren(außen);
							break;

						case STARTEN_ABLADEN:
							reinArme(-20*kisteTiefe,true); // Signal zum runterfahren und
												// auswerfen an Lift
							break;

//						case AUF_CARGOAREA_AUSWERFEN: // Signal zum runterfahren
//														// an lift
//							drauf(-ziehdrehungen);
//							break;

//						case AUF_BODEN_AUSWERFEN:
//							drauf(-ziehdrehungen);
//							break;

						case STOP:
							try {
								dosGreifer2Fahrer.writeByte(GreiferEvents.STOP
										.ordinal());
								dosGreifer2Fahrer.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.exit(0);
							break;

						default:
							LCD.drawString("Unbekannter Befehl", 1, 1);
							break;
						}
					}
				} catch (IOException ioe) {
					fatal("IOException in receiver:");
				}
			}
			System.out.println("ReceiverLift stopped");
		}

	}

	public static void justieren(int b) {
		MJustieren.rotateTo(b);
		}
	

	public static void drauf(int d) {
		reinArme(d, false);

	}

	public static void reinArme(int a, boolean w) {
		MArmLinks.rotate(a, true);
		MArmRechts.rotate(a, w);
		/*
		 * while (MArmLinks.isMoving()) { Delay.msDelay(10); }
		 */
	}

	public static void stopArme() {
		MArmLinks.stop();
		MArmRechts.stop();
	}

	public static void fatal(String message) {
		System.out.println(message);
		Delay.msDelay(5000);
		System.exit(1);
	}

}
