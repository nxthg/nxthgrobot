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
import lejos.robotics.mapping.NavigationModel.NavEvent;
import lejos.util.Delay;


public class AufAbladen {
	  static int ziehdrehungen = 2*360;                     // wie viele umdrehung um ein packet auf die kleine zwischenladefläche zu ziehen?????
	  static NXTRegulatedMotor MJustieren = Motor.C ;     // Antrieb der Ketten auf der Ladefläche
	  static NXTRegulatedMotor MArmLinks = Motor.A ;        // Antrieb für die Kette am linken Arm; forward ist rein/raus????
	  static NXTRegulatedMotor MArmRechts = Motor.B ;       // Antrieb für die Kette am rechten Arm; forward ist rein/raus????
	  static int greifenklein = 200;					//Wie viel um ein kleines Paket zu greifen auseinander????
	  static int greifenMittel = 300;						//Wie viel um ein mittleres Paket zu greifen auseinander ????
	  static int greifenGross = 400;						//Wie viel um ein großes Paket zu greifen auseinander????
	  static int greifen;
	  static int außen=500;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private static boolean running;
	  public static void main(String[] args) {       
	   while (true) {
		   LCD.drawInt(MJustieren.getTachoCount(), 0, 0);
		   run();
		}
		
	  
	  
	  } // end of main
public static void run() {
	NXTCommConnector connector = Bluetooth.getConnector();
	NXTConnection conn = connector.waitForConnection(0, NXTConnection.PACKET);
	dis = conn.openDataInputStream();
	dos = conn.openDataOutputStream();
	
	while(running) {
		try {
			byte event = dis.readByte();
			GreiferEvents gevent = GreiferEvents.values()[event];
			synchronized(this) {
				switch (gevent) {
				case :
					
					break;

				default:
					break;
				}
				
				
				
				
				
				}
			
		}
		}
		catch (IOException ioe) {
			fatal("IOException in receiver:");
		
		}
}
	  ////////////////////////////////
	  // AUF UND ABLADEN            //
	  ////////////////////////////////


	    
	  public static void aufladen (){
		// sobald signal von Fahrer zum beginnen kommt ausführen:
		  if (Button.LEFT.isDown()) {
				greifen=greifenMittel;	
			    justieren(greifen);				///Schritt 1 
			    // Signal zu lift dass er auf die kistenhöhe soll
			    //Warten auf signal von lift dass er auf kistenhöhe ist
			    drauf(ziehdrehungen);   		//Schritt 3
			    //Signal zum Hoch/runterziehen an Lift auf cargoarea
				}
			
	 if (Button.RIGHT.isDown()) {
					greifen=greifenGross;
				    justieren(greifen);			///Schritt 1
				    drauf(ziehdrehungen); 		// Schritt 3
				  //Siehe Button.Left.isDown
	 }
				    //Sobald signal komm dass hebebühne auf cargoarea-höhe ist:
				    if (Button.ENTER.isDown()) {  	 
						drauf(ziehdrehungen);		//Schritt 5.2
						//Signal senden dass vorgang abgeschlossen ist
						justieren(außen);		//Schritt 6.2
				    }
	  }
	  
	  
	  
	  
	  
	  public static void abladen(){
		//warten auf signal von Fahrer zum Abladen
		  if (Button.LEFT.isDown()) {
			    justieren(greifen);
			    
			    //Signal zum runterfahren und auswerfen an Lift
			    //Warten auf signal zum aufnehmen von Lift
			    drauf(-ziehdrehungen);
			    //Signal zum runterfahren an lift
			    //warten auf signal das hebebühne unten ist
			    drauf(-ziehdrehungen);
			    //(Signal) FERTIG
			    
				}
		  }
	  
	  

	 
	  /////////////////////////////////
	  
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
