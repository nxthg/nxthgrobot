package de.nxthg.lift;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.util.Delay;
public class HochRunterziehen {

static int hoehecargo=500;          	//umdrehungen um auf cargo area höhe zu kommen??
static int cargodrehen =200;			//umdrehungen damint alle kisten nach hinten gezogen werden
static int hoehekiste;
static int hoehekisteUnten =0;
static int hoehekisteMitte = 800;
static int hoehekisteOben = 3000;
static int hoehefahren = 3*360;					
static NXTRegulatedMotor MTurmLinks = Motor.A ;   
static NXTRegulatedMotor MTurmRechts = Motor.B ;        
static NXTRegulatedMotor MCargoArea = Motor.C ; 	
	
	
	public static void main(){
	while(true){
		LCD.drawInt(MTurmLinks.getTachoCount(), 0, 0);
		LCD.drawInt(MTurmRechts.getTachoCount(), 0, 3);
		aufladen();
		abladen();	
		
		
	}
}// end of main
	
	public static void aufladen () {
		
		// Auf Signal warten dass im sagt in welcher höhe die kiste steht 
		if(Button.RIGHT.isDown()){
			hoehekiste= hoehekisteMitte;
		}
		
		if (Button.LEFT.isDown()){				//Schritt 2
			MTurmLinks.rotateTo(hoehekiste,true);
			MTurmRechts.rotateTo(hoehekiste);
			//Signal senden dass er auf Kistenhöhe ist
			
		}
		//warten dass signal zum hoch/runterziehen auf cargoarea kommt
		if (Button.ENTER.isDown()){
		hochziehen(hoehecargo, true);			//Schritt 4
		// signal zu greifer senden, gleichzeitig:
		cargorein(cargodrehen);					//Schritt 5.1
			if (Button.ENTER.isDown()){
				MTurmRechts.stop();
				MTurmLinks.stop();
		
			}
		}
	//sobald signal kommt, dass abgescholssen:	
	hochziehen(hoehefahren,true);  //Schritt 6.2
	
	}// end of aufladen
	
	public static void abladen (){
		// sobald signal von greifer zum ausladen kommt ausführen:	
				if (Button.ESCAPE.isDown()){
					hochziehen(hoehecargo,true);
					
					
					cargorein(-cargodrehen);	
					
					// signal zu greifer senden, dass er paket auf hebebühne aufnehmen soll...	
					
					//Auf signal von greifer warten dass er fertig ist
					hochziehen(0,true);					
					//Signal zu greifer dass er paket ausladen soll... 
				
				}			
	}
	
public static void hochziehen(int h, boolean t){
MTurmRechts.rotateTo(h, true);
MTurmLinks.rotateTo(h,true); 
while ( MTurmRechts.isMoving()) {
	Delay.msDelay(10);
	}
}

public static void cargorein(int r){
	MCargoArea.rotate(r,true);
	while ( MCargoArea.isMoving()) {
		Delay.msDelay(10);
		}
}


}// End of class Hochtiehen
