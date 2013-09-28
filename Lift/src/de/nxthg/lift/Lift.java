package de.nxthg.lift;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.nxthg.greifer.GreiferEvents;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.util.Delay;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.Touch;

public class Lift {

	static int hoehecargo = 500; // umdrehungen um auf cargo area höhe zu
									// kommen??
	static int cargodrehen = 200; // umdrehungen damint alle kisten nach hinten
									// gezogen werden
	static int hoehekiste;
	static int hoehekisteUnten = 0;
	static int hoehekisteMitte = 50;
	static int hoehekisteOben = 5000;
	static int hoehefahren = 3 * 360;
	static NXTRegulatedMotor MTurmLinks = Motor.A;
	static NXTRegulatedMotor MTurmRechts = Motor.B;
	static NXTRegulatedMotor MCargoArea = Motor.C;
	private static DataInputStream disLift2Greifer;
	private static DataOutputStream dosLift2Greifer;
	static boolean running;
	private static Lift model;
	private Thread connectorLift;

	public Lift() {
		connectorLift = new Thread(new ConnectorLift());
		connectorLift.start();
	}

	public static void main(String[] args) {
		TouchSensor ts = new TouchSensor(SensorPort.S3);
		LCD.drawInt(MTurmLinks.getTachoCount(), 0, 0);
		model = new Lift();
		System.out.println("Warte auf Button");

		while (true){
			if (ts.isPressed()){
				try {
					System.out.println("Button pressed");
					dosLift2Greifer.writeByte(GreiferEvents.STOP.ordinal());
					dosLift2Greifer.flush();
					System.out.println("nach flush");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Delay.msDelay(10);
				System.exit(0);}
			else {
				Delay.msDelay(20);
			}
		}



	}// end of main

	private void shutDown() {
		// TODO Auto-generated method stub
		running = false;
		System.exit(0);
	}

	/*
	 * public static void aufladen () {
	 * 
	 * // Auf Signal warten dass im sagt in welcher höhe die kiste steht
	 * if(Button.RIGHT.isDown()){ hoehekiste= hoehekisteMitte; }
	 * 
	 * if (Button.LEFT.isDown()){ //Schritt 2
	 * 
	 * //Signal senden dass er auf Kistenhöhe ist
	 * 
	 * } //warten dass signal zum hoch/runterziehen auf cargoarea kommt if
	 * (Button.ENTER.isDown()){ hochziehen(hoehecargo, true); //Schritt 4 //
	 * signal zu greifer senden, gleichzeitig: cargorein(cargodrehen); //Schritt
	 * 5.1 if (Button.ENTER.isDown()){ MTurmRechts.stop(); MTurmLinks.stop();
	 * 
	 * } } //sobald signal kommt, dass abgescholssen:
	 * hochziehen(hoehefahren,true); //Schritt 6.2
	 * 
	 * }// end of aufladen
	 */
	
	
	/*public void abladen() {
		// sobald signal von greifer zum ausladen kommt ausführen:

		hochziehen(hoehecargo, true);

		cargorein(-cargodrehen);

		// signal zu greifer senden, dass er paket auf hebebühne aufnehmen
		// soll...

		// Auf signal von greifer warten dass er fertig ist
		hochziehen(0, true);
		// Signal zu greifer dass er paket ausladen soll...

	} 
	*/

	public void hochziehen(int h) {
		MTurmRechts.rotateTo(h, true);
		MTurmLinks.rotateTo(h,true);
		while (MTurmRechts.isMoving()) {
		Delay.msDelay(10);
		}
	}

	public void cargorein(int r) {
		MCargoArea.rotate(r, true);
		while (MCargoArea.isMoving()) {
			Delay.msDelay(10);
		}
	}

	public void fatal(String message) {
		System.out.println(message);
		Delay.msDelay(5000);
		System.exit(1);
	}

	class ConnectorLift implements Runnable {
		@SuppressWarnings("incomplete-switch")
		public void run() {
			NXTCommConnector connector = Bluetooth.getConnector();
			System.out.println("Warte auf Greifer");
			NXTConnection conn = connector.waitForConnection(0,
					NXTConnection.PACKET);
			System.out.println("gefunden");
			disLift2Greifer = conn.openDataInputStream();
			dosLift2Greifer = conn.openDataOutputStream();
			running = true;
			while (running) {
				//System.out.println("While Schleife lauft");
				try {
					byte event = disLift2Greifer.readByte();
					System.out.println("lese Byte");
					System.out.println(event); 
					System.out.flush();
					GreiferEvents gevent = GreiferEvents.values()[event];
					synchronized (this) {
						switch (gevent) {
						case STOP:
							System.out.print("Befehl empfangen");
							Delay.msDelay(2000);
							break;
						case AUF_KISTENHOEHE_FAHREN_UNTEN:
							hochziehen(hoehekisteUnten);
							dosLift2Greifer.writeByte(GreiferEvents.AUF_KISTENHOEHE
									.ordinal());
							dosLift2Greifer.flush();
							
							break;

						case AUF_KISTENHOEHE_FAHREN_MITTE:
							hochziehen(hoehekisteMitte);
							dosLift2Greifer.writeByte(GreiferEvents.AUF_KISTENHOEHE
									.ordinal());
							dosLift2Greifer.flush();
							break;

						case AUF_KISTENHOEHE_FAHREN_OBEN:
							hochziehen(hoehekisteOben);
							dosLift2Greifer.writeByte(GreiferEvents.AUF_KISTENHOEHE
									.ordinal());
							dosLift2Greifer.flush();
							System.out.println("Befehl an Greifer gesendet");
							
							break;

						case AUF_CARGOAREA_FAHREN:
							hochziehen(hoehecargo);
							dosLift2Greifer.writeByte(GreiferEvents.AUF_CARGOAREA
									.ordinal());
							dosLift2Greifer.flush();
							break;
							
						case CARGOAREA_REIN:
							cargorein(cargodrehen);
							//dos.writeByte(GreiferEvents.KISTE_IST_DRAUF.ordinal());
							hochziehen(hoehefahren);
							
						case ABLADEN:
							System.out.println("Befehl ABLADEN bekommen");
							System.out.flush();
							hochziehen(50000);
							System.out.println("Hochgezogen");
							System.out.flush();
							cargorein(-20*cargodrehen);
							break;
							
							

						}
					}
				} catch (IOException ioe) {
					fatal("IOException in receiver:");
				}
				System.out.println("stopped");
			}
		}
	}

}// End of class Hochziehen