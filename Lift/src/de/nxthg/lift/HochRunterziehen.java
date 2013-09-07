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

public class HochRunterziehen {

	static int hoehecargo = 500; // umdrehungen um auf cargo area höhe zu
									// kommen??
	static int cargodrehen = 200; // umdrehungen damint alle kisten nach hinten
									// gezogen werden
	static int hoehekiste;
	static int hoehekisteUnten = 0;
	static int hoehekisteMitte = 800;
	static int hoehekisteOben = 3000;
	static int hoehefahren = 3 * 360;
	static NXTRegulatedMotor MTurmLinks = Motor.A;
	static NXTRegulatedMotor MTurmRechts = Motor.B;
	static NXTRegulatedMotor MCargoArea = Motor.C;
	private DataInputStream dis;
	private DataOutputStream dos;
	static boolean running;
	private static HochRunterziehen model;
	private Thread connectorLift;

	public HochRunterziehen() {
		connectorLift = new Thread(new ConnectorLift());
		connectorLift.start();
	}

	public static void main(String[] args) {

		LCD.drawInt(MTurmLinks.getTachoCount(), 0, 0);
		model = new HochRunterziehen();
		System.out.println("Warte auf Button");

		Button.waitForAnyPress();
		System.out.println("Button wurde gepresst");
		model.shutDown();
		System.out.println("Nach Shutdown");
		
		// Das kann weg, wenn die Schleife im runnable tut.
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public void abladen() {
		// sobald signal von greifer zum ausladen kommt ausführen:

		hochziehen(hoehecargo, true);

		cargorein(-cargodrehen);

		// signal zu greifer senden, dass er paket auf hebebühne aufnehmen
		// soll...

		// Auf signal von greifer warten dass er fertig ist
		hochziehen(0, true);
		// Signal zu greifer dass er paket ausladen soll...

	}

	public void hochziehen(int h, boolean t) {
		MTurmRechts.rotateTo(h, true);
		MTurmLinks.rotateTo(h, true);
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
			dis = conn.openDataInputStream();
			dos = conn.openDataOutputStream();
			running = true;
			while (running) {
				System.out.println("While Schleife lauft");
				try {
					byte event = dis.readByte();
					System.out.println("lese Byte");
					GreiferEvents gevent = GreiferEvents.values()[event];
					synchronized (this) {
						switch (gevent) {

						case AUF_KISTENHOEHE_FAHREN_UNTEN:
							hochziehen(hoehekisteUnten, true);
							dos.write(GreiferEvents.AUF_KISTENHOEHE_EINZIEHEN
									.ordinal());
							break;

						case AUF_KISTENHOEHE_FAHREN_MITTE:
							hochziehen(hoehekisteMitte, true);
							dos.write(GreiferEvents.AUF_KISTENHOEHE_EINZIEHEN
									.ordinal());
							break;

						case AUF_KISTENHOEHE_FAHREN_OBEN:
							hochziehen(hoehekisteOben, true);
							dos.write(GreiferEvents.AUF_KISTENHOEHE_EINZIEHEN
									.ordinal());
							break;

						case AUF_CARGOAREA_FAHREN:
							hochziehen(hoehecargo, true);
							dos.write(GreiferEvents.AUF_CARGOAREA_EINZIEHEN
									.ordinal());
							cargorein(cargodrehen);
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

}// End of class Hochtiehen