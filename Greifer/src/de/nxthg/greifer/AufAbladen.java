package de.nxthg.greifer;

import lejos.nxt.Button;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;
import lejos.util.Delay;


public class AufAbladen {
	  static int zusammendrehungen = 15*360;                 // wie viele grad umdrehung um die arme ganz zusammen zu mnachen????
	  static int ziehdrehungen = 2*360;                     // wie viele umdrehung um ein packet auf die kleine zwischenladefläche zu ziehen?????
	  static NXTRegulatedMotor MJustieren = Motor.C ;     // Antrieb der Ketten auf der Ladefläche
	  static NXTRegulatedMotor MArmLinks = Motor.A ;        // Antrieb für die Kette am linken Arm; forward ist rein/raus????
	  static NXTRegulatedMotor MArmRechts = Motor.B ;       // Antrieb für die Kette am rechten Arm; forward ist rein/raus????
	  static int greifenklein = 3*360;					//Wie viel um ein kleines Paket zu greifen????
	  static int greifenMittel = 2*360;						//Wie viel um ein mittleres Paket zu greifen????
	  static int greifenGross = 1*360;						//Wie viel um ein großes Paket zu greifen????
	  static int greifen;
	  
	  public static void main(String[] args) {        
	    Delay.msDelay(2000);
	   while (true) {
		aufladen();
	   // abladen();
		}
		
	  
	  
	  } // end of main
	  
	  ////////////////////////////////
	  // AUF UND ABLADEN            //
	  ////////////////////////////////
	  
	  public static void abladen(){
		//warten auf signal von Lift
		  if (Button.LEFT.isDown()) {
				greifen=-greifenMittel;	
			    justieren(greifen);
			    drauf(-ziehdrehungen); 
			    //Signal zum Hochtiehen an Lift
				}
			
		  if (Button.RIGHT.isDown()) {
					greifen=-greifenGross;
				    justieren(greifen);
				    drauf(-ziehdrehungen); 
				  //Signal zum Hochtiehen an Lift 
		  }
	  }
	  
	  public static void aufladen (){
		  if (Button.LEFT.isDown()) {
				greifen=-greifenMittel;	
			    justieren(greifen);
			    drauf(ziehdrehungen); 
			    //Signal zum Hochtiehen an Lift
				}
			
		  if (Button.RIGHT.isDown()) {
					greifen=-greifenGross;
				    justieren(greifen);
				    drauf(ziehdrehungen); 
				  //Signal zum Hochtiehen an Lift 
		  }
				    
				    if (Button.ENTER.isDown()) {   // Sobald Signal von Lift kommt, erneut einziehen
						drauf(ziehdrehungen);
						auseinander(greifen);
				    }
	  }
	 
	  /////////////////////////////////
	  
	  public static void justieren (int b){
		  MJustieren.rotate(b);
	  }
	  
	  public static void drauf(int d){
	    reinArme(d, false);
	  }
	  
	  
	  public static void reinArme(int a, boolean w){
	  MArmLinks.rotate(a, true);
	  MArmRechts.rotate(a, w);
	  }
	  
	                      
	  public static void auseinander (int z){
		 MJustieren.rotate(-z);
	  }
	  
}
