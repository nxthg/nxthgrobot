package de.nxthg.greifer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
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
	static NXTRegulatedMotor MArmRechts = Motor.B ; 
	static int aussen=200;
	static int greifenKlein = 200;					//Wie viel um ein kleines Paket zu greifen auseinander????
	static int greifenMittel = 300;						//Wie viel um ein mittleres Paket zu greifen auseinander ????
	static int greifenGross = 400;						//Wie viel um ein großes Paket zu greifen auseinander????
	static int greifen;
	static int außen=500;
	static int kisteBreite ;
	static int kisteTiefe;
	static int kisteTiefeMittel=30;
	static int kisteTiefeGross=50;
	private static DataInputStream disFahrer;
	private static DataOutputStream dosFahrer;
	private static DataInputStream disLift;
	private static DataOutputStream dosLift;
	private boolean running;
	private Thread receiverFahrer;
	private Thread receiverLift;
	private static AufAbladenV2 model;
	static boolean unverbunden = true;
	
	
	public AufAbladenV2(){
		
		receiverLift = new Thread(new ConnectorLift());
		receiverLift.start();	
		System.out.println("Verbinde zu Lift");
		while(unverbunden){
			Delay.msDelay(100);
		}
		System.out.println("Mit Lift Verbunden" +
				"Warte auf Verbindung zum Fahrer");
		receiverFahrer = new Thread(new ReceiverFahrer());
		receiverFahrer.start();
		
		
	}

	public static void main(String[] args) { 
			LCD.drawInt(MJustieren.getTachoCount(), 0, 0);
			model= new AufAbladenV2();
			
			Button.waitForAnyPress();
	    	model.shutDown();
	    	try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	} // end of main


	private void shutDown() {
		// TODO Auto-generated method stub
			running = false;	
	}


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
				synchronized(this) {
					switch (gevent) {
					case KISTE_KLEIN_UNTEN: 									//vom Fahrer 
						justieren(greifenKlein+5);
						kisteBreite= greifenKlein;
						kisteTiefe=kisteTiefeMittel; 
						dosLift.write(GreiferEvents.AUF_KISTENHOEHE_FAHREN_UNTEN.ordinal()); //Zu Lift
						
						break;
					case KISTE_MITTEL_UNTEN:
						justieren(greifenMittel+5);	
						kisteBreite= greifenMittel;
						kisteTiefe=kisteTiefeMittel; 
						dosLift.write(GreiferEvents.AUF_KISTENHOEHE_FAHREN_UNTEN.ordinal());					
						break;
						
					case KISTE_MITTEL_MITTE:
						justieren(greifenMittel+5);	
						kisteBreite= greifenMittel;
						kisteTiefe=kisteTiefeMittel; 
						dosLift.write(GreiferEvents.AUF_KISTENHOEHE_FAHREN_MITTE.ordinal());					
						break;
						
					case KISTE_GROSS_OBEN:
						justieren(greifenGross+5);	
						kisteBreite= greifenGross;
						kisteTiefe=kisteTiefeGross; 
						dosLift.write(GreiferEvents.AUF_KISTENHOEHE_FAHREN_OBEN.ordinal());
						break;
						
					case FAHRER_VORNE:
						justieren(kisteBreite);
						reinArme(kisteTiefe,true);
						dosFahrer.write(GreiferEvents.FAHR_ZURUECK.ordinal());
						
					case FAHRER_HINTEN:
						dosLift.write(GreiferEvents.AUF_CARGOAREA_FAHREN.ordinal());

					case STOP:
						dosLift.write(GreiferEvents.STOP.ordinal());
						System.exit(0);
						break;
						
					case ABLADEN:
						justieren(aussen);
						dosLift.write(GreiferEvents.ABLADEN.ordinal());
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
		System.out.println("ReceiverFahrer stopped");
	}

	}


	class ConnectorLift implements Runnable {
		public void run() {
			NXTCommConnector connector = Bluetooth.getConnector();
			System.out.println("Suche Lift");
			NXTConnection conn = connector.connect("Lift",NXTConnection.PACKET);
			System.out.println("Verbindung hergestellt");
			unverbunden=false;
			System.out.flush();
			disLift = conn.openDataInputStream();
			dosLift = conn.openDataOutputStream();
			running=true;
			while(running) {
				try {
					byte event = disLift.readByte();
					GreiferEvents gevent = GreiferEvents.values()[event];
					synchronized(this) {
						switch (gevent) {
						
						case AUF_KISTENHOEHE: 	
							dosFahrer.write(GreiferEvents.FAHR_VOR.ordinal());
							//dosLift.write(GreiferEvents.AUF_CARGOAREA_FAHREN.ordinal());			
							break;									  		//Signal zum Hoch/runterziehen an Lift auf cargoarea


						case AUF_CARGOAREA:					  	//Sobald signal komm dass hebebühne auf cargoarea-höhe ist:
							reinArme(kisteTiefe,true);
							dosLift.write(GreiferEvents.CARGOAREA_REIN.ordinal());
							//justieren(außen);
							break;

							case STARTEN_ABLADEN:
						justieren(greifen);								//Signal zum runterfahren und auswerfen an Lift
							break;

						case AUF_CARGOAREA_AUSWERFEN:						//Signal zum runterfahren an lift
							drauf(-ziehdrehungen);
							break;

							case AUF_BODEN_AUSWERFEN:
						 drauf(-ziehdrehungen);
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
			System.out.println("ReceiverLift stopped");
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
