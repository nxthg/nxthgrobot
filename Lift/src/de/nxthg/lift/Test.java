package de.nxthg.lift;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;




public class Test {
	
static NXTRegulatedMotor MA = Motor.A ;     
static NXTRegulatedMotor MB = Motor.B ;        
static NXTRegulatedMotor MC = Motor.C ;

public static void main(){
	drehen();	
	
}



public static void drehen(){
	MA.rotate(2*360,true );	
	MB.rotate(2*360, true);	
	MC.rotate(2*360);
}
}
