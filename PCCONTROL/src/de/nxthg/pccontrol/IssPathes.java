package de.nxthg.pccontrol;

import lejos.robotics.navigation.Waypoint;

public class IssPathes {
	
		
	
	// vom start zur ersten kiste
	static final Waypoint[] PATH1 = {new Waypoint(470.0, 180.0), new Waypoint(470.0, 210.0)};
	
    // von der ersten zur zweiten kiste
	static final Waypoint[] PATH2 = {new Waypoint(470.0, 190.0), new Waypoint(440.0, 190.0), new Waypoint(440.0, 210.0)};
    
    // von der zweiten zur dritten kiste
	static final Waypoint[] PATH3 = {new Waypoint(440.0, 190.0), new Waypoint(390.0, 190.0), new Waypoint(390.0, 210.0)};
    
    // von der dritten kiste ueber die rampe
	static final Waypoint[] PATH4 = {new Waypoint(380.0, 160.0), new Waypoint(230.0, 160.0)};
	
    // durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH5 = {new Waypoint(200.0, 160.0), new Waypoint(77.0, 230.0), new Waypoint(77.0, 270.0)};
    
    // vom nodeII zum ersten abladen
	static final Waypoint[] PATH6 = {new Waypoint(23.0, 285.0), new Waypoint(20.0, 275.0)};
    
	// vom abladen zum nodeII
	static final Waypoint[] PATH7 = {new Waypoint(77.0, 260.0)};
	
	// zurueck durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH8 = {new Waypoint(77.0, 230.0), new Waypoint(200.0, 160.0), new Waypoint(230.0, 160.0)};
	
	// von nodeII ueber die rampe ins start-modul
	static final Waypoint[] PATH9 = {new Waypoint(380.0, 160.0)};
		
	public static final Waypoint[][] PATH = {PATH1, PATH2, PATH3, PATH4, PATH5, PATH6, PATH7, PATH8, PATH9};
	
}