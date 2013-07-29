package de.nxthg.pccontrol;

import lejos.robotics.navigation.Waypoint;

public class IssPathes {

	float var1 = 0;
	float var2 = 0;                        // wo anderst gepeichert oder direkt hier eingegeben ???
	float var3 = 0;
	
	
	//vom start zur ersten kiste
	static final Waypoint[] PATH1 = {new Waypoint(470.0, 160.0), new Waypoint(470.0, 210.0)};
	
    // von der ersten zur zweiten kiste
    static final Waypoint[] PATH2 = {new Waypoint(470.0, 190.0), new Waypoint(440.0, 190.0), new Waypoint(440.0, 210.0)};
    
    // von der zweiten zur dritten kiste
    static final Waypoint[] PATH3 = {new Waypoint(440.0, 190.0), new Waypoint(390.0, 190.0), new Waypoint(390.0, 210.0)};
    
    // von der dritten kiste ueber die rampe
    static final Waypoint[] PATH4 = {new Waypoint(380.0, 160.0), new Waypoint(230.0, 160.0)};
	
    // durch nodeII, falls wir nicht eingreifen
    static final Waypoint[] PATH5 = {new Waypoint(200.0, 160.0), new Waypoint(77.0, 230.0)};
    
 // vom nodeII zum ersten abladen
    static final Waypoint[] PATH4 = {new Waypoint(70.0, 280.0), new Waypoint(70.0, 280.0)};
    
}