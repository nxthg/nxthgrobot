package de.nxthg.lift;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class HochRunterziehen {

static int hoehecargo= 2*360;          	//umdrehungen um auf cargo area höhe zu kommen??
static int cargodrehen = 3*360;			//umdrehungen damint alle kisten nach hinten gezogen werden
static int hoehekiste;
static int hoehekisteUnten = 2*360;
static int hoehekisteMitte = 2*360;
static int hoehekisteOben = 2*360;
static int hoehefahren = 3*360;
static int y = 0; 						////////////////////////////////
static int veränderunghoch=2 ; 				// Anzahl der cm pro umdrehung//
static NXTRegulatedMotor MTurmLinks = Motor.A ;/////////////////////////////////     
static NXTRegulatedMotor MTurmRechts = Motor.B ;        
static NXTRegulatedMotor MCargoArea = Motor.C ; 	
	
	
	public static void main(){
	while(true){
		aufladen();
		abladen();	
		
		
	}
}// end of main
	
	public static void aufladen () {
		// sobald signal von Fahrer zum beginnen kommt ausführen:
		
		// Auf Signal warten dass im sagt in welcher höhe die kiste steht 
		if(Button.RIGHT.isDown()){
			hoehekiste= hoehekisteMitte;
		}
		
		if (Button.LEFT.isDown()){				//Schritt 1
			MTurmLinks.rotate(hoehekiste,true);
			MTurmRechts.rotate(hoehekiste);
			y=y+(hoehekiste*veränderunghoch); 
		}
		
		if (Button.ENTER.isDown()){
		hochziehen(hoehecargo, true);			//Schritt 4
		// signal zu greifer senden, gleichzeitig:
		cargorein(cargodrehen);					//Schritt 5.1
			if (Button.ENTER.isDown()){
				MTurmRechts.stop();
				MTurmLinks.stop();
		
			}
		}
		
	}// end of aufladen
	
	public static void abladen (){
		// sobald signal von greifer zu hoch/runterziehen kommt ausführen:	
				if (Button.ESCAPE.isDown()){
					cargorein(-cargodrehen);	
					
					// signal zu greifer senden, gleichzeitig:
					//hochziehen(-hoehecargo, false); wird nicht gebraucht da pakete rausfallen sollen
				
				}			
	}
	
public static void hochziehen(int h, boolean t){
MTurmRechts.rotate(h, true);
MTurmLinks.rotate(h);
y=y+(h*veränderunghoch); 
}

public static void cargorein(int r){
	MCargoArea.rotate(r);
}


}// End of class Hochtiehen
