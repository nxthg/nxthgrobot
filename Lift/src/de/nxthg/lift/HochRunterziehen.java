package de.nxthg.lift;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class HochRunterziehen {

static int hoehecargo= 2*360;          	//umdrehungen um auf cargo area höhe zu kommen??
static int cargodrehen = 3*360;			//umdrehungen damint alle kisten nach hinten gezogen werden

static NXTRegulatedMotor MTurmLinks = Motor.A ;     
static NXTRegulatedMotor MTurmRechts = Motor.B ;        
static NXTRegulatedMotor MCargoArea = Motor.C ; 	
	
	
	public static void main(){
	while(true){
		aufladen();
		abladen();
	
	
	}
}// end of main


	public static void aufladen () {
		// sobald signal von greifer zu hochziehen kommt ausführen:	
		if (Button.ENTER.isDown()){
		hochziehen(hoehecargo, true);
		// signal zu greifer senden, gleichzeitig:
		cargorein(cargodrehen);	
	}
	}
	
	public static void abladen (){
		// sobald signal von greifer zu hochziehen kommt ausführen:	
				if (Button.ESCAPE.isDown()){
					cargorein(-cargodrehen);	
					
					// signal zu greifer senden, gleichzeitig:
					hochziehen(-hoehecargo, false);
				
				}			
	}
	
public static void hochziehen(int h, boolean t){
MTurmRechts.rotate(h, true);
MTurmLinks.rotate(h);
}

public static void cargorein(int r){
	MCargoArea.rotate(r);
}


}// End of class Hochtiehen
