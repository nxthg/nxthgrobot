package de.nxthg.greifer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.util.Delay;


public class AufAbladenV2  {
	static int ziehdrehungen = 2*360;                     // wie viele umdrehung um ein packet auf die kleine zwischenladefläche zu ziehen?????
	static NXTRegulatedMotor MJustieren = Motor.C ;     // Antrieb der Ketten auf der Ladefläche
	static NXTRegulatedMotor MArmLinks = Motor.A ;        // Antrieb für die Kette am linken Arm; forward ist rein/raus????
	static NXTRegulatedMotor MArmRechts = Motor.B ;       // Antrieb für die Kette am rechten Arm; forward ist rein/raus????
	static int greifenKlein = 200;					//Wie viel um ein kleines Paket zu greifen auseinander????
	static int greifenMittel = 300;						//Wie viel um ein mittleres Paket zu greifen auseinander ????
	static int greifenGross = 400;						//Wie viel um ein großes Paket zu greifen auseinander????
	static int greifen;
	static int außen=500;
	private static DataInputStream disFahrer;
	private static DataOutputStream dosFahrer;
	private static DataInputStream disLift;
	private static DataOutputStream dosLift;
	private static boolean running;
	private Thread receiverFahrer;
	private Thread receiverLift;
	private static AufAbladenV2 model;
	
	
	public AufAbladenV2(){
		receiverFahrer = new Thread(new ReceiverFahrer());
		receiverFahrer.start();
		receiverLift = new Thread(new ReceiverLift());
		receiverLift.start();	
	}

	public static void main(String[] args) {       
		while (true) {
			LCD.drawInt(MJustieren.getTachoCount(), 0, 0);
			model= new AufAbladenV2();
		}

	} // end of main


	class ReceiverFahrer implements Runnable {
	public void run() {
		NXTCommConnector connector = Bluetooth.getConnector();
		NXTConnection conn = connector.waitForConnection(0, NXTConnection.PACKET);
		disFahrer = conn.openDataInputStream();
		dosFahrer = conn.openDataOutputStream();
		running=true;
		while(running) {
			try {
				byte event = disFahrer.readByte();
				GreiferEvents gevent = GreiferEvents.values()[event];
				synchronized(AufAbladenV2.class) {
					switch (gevent) {
					case KISTE_KLEIN_UNTEN: 									//vom Fahrer 
						justieren(greifenKlein);	  		 				
						dosLift.write(GreiferEvents.AUF_KISTENHOEHE_FAHREN_UNTEN.ordinal()); //Zu Lift
						
						break;
					case KISTE_MITTEL_UNTEN:
						justieren(greifenMittel);							// Signal zu lift dass er auf die kistenhöhe soll
						dosLift.write(GreiferEvents.AUF_KISTENHOEHE_FAHREN_UNTEN.ordinal());					
						break;
						
					case KISTE_MITTEL_MITTE:
						justieren(greifenMittel);							// Signal zu lift dass er auf die kistenhöhe soll
						dosLift.write(GreiferEvents.AUF_KISTENHOEHE_FAHREN_MITTE.ordinal());					
						break;
						
					case KISTE_GROSS_OBEN:
						justieren(greifenGross);									// Signal zu lift dass er auf die kistenhöhe soll
						dosLift.write(GreiferEvents.AUF_KISTENHOEHE_FAHREN_OBEN.ordinal());
						break;

					case STOP:
						dosLift.write(GreiferEvents.STOP.ordinal());
						System.exit(0);
						break;

					default:
						LCD.drawString("Unbekannter Befehl",1, 1);
						break;
					}								
				}			
			}
			catch (IOException ioe) {
				fatal("IOException in receiver:");
			}
		}
	}

	}


	class ReceiverLift implements Runnable {
		public void run() {
			NXTCommConnector connector = Bluetooth.getConnector();
			NXTConnection conn = connector.waitForConnection(0, NXTConnection.PACKET);
			disLift = conn.openDataInputStream();
			dosLift = conn.openDataOutputStream();
			running=true;
			while(running) {
				try {
					byte event = disLift.readByte();
					GreiferEvents gevent = GreiferEvents.values()[event];
					synchronized(AufAbladenV2.class) {
						switch (gevent) {
						

						case AUF_KISTENHOEHE_EINZIEHEN:
							drauf(ziehdrehungen); 	
							dosLift.write(GreiferEvents.AUF_CARGOAREA_FAHREN.ordinal());	//Warten auf signal von lift dass er auf kistenhöhe ist				
							break;									  		//Signal zum Hoch/runterziehen an Lift auf cargoarea


						case AUF_CARGOAREA_EINZIEHEN:					  	//Sobald signal komm dass hebebühne auf cargoarea-höhe ist:
							drauf(ziehdrehungen);
							justieren(außen);
							dosFahrer.write(GreiferEvents.KISTE_IST_DRAUF.ordinal());
							break;

							/*case STARTEN_ABLADEN:
						justieren(greifen);								//Signal zum runterfahren und auswerfen an Lift
						break;*/

						case AUF_CARGOAREA_AUSWERFEN:						//Signal zum runterfahren an lift
							drauf(-ziehdrehungen);
							break;

							/*case AUF_BODEN_AUSWERFEN:
						 drauf(-ziehdrehungen);
						 break;		*/									//Signal: Fertig
					

						default:
							LCD.drawString("Unbekannter Befehl",1, 1);
							break;
						}								
					}			
				}
				catch (IOException ioe) {
					fatal("IOException in receiver:");
				}
			}
		}

		}



	public static void justieren (int b){
		MJustieren.rotateTo(b);
		while ( MJustieren.isMoving()) {
			Delay.msDelay(10);
		}
	}

	public static void drauf(int d){
		reinArme(d, false);

	}


	public static void reinArme(int a, boolean w){
		MArmLinks.rotate(a, true);
		MArmRechts.rotate(a, w);
		while ( MArmLinks.isMoving()) {
			Delay.msDelay(10);
		}
	}

	public static void fatal(String message) {
		System.out.println(message);
		Delay.msDelay(5000);
		System.exit(1);
	}	                      

}
