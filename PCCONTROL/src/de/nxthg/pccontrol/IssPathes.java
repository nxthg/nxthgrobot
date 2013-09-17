package de.nxthg.pccontrol;

import lejos.robotics.navigation.Waypoint;

public class IssPathes {
	
		
 //ERSTER DURCHLAUF:
	
	// vom start zur ersten kiste
	static final Waypoint[] PATH1 = {new Waypoint(150.4, 146.6), new Waypoint(175.4, 146.6)};
	
    // von dort zur zweiten kiste
	static final Waypoint[] PATH2 = {new Waypoint(175.4, 180.2)};
    
    // von der zweiten kiste ueber die rampe
	static final Waypoint[] PATH3 = {new Waypoint(150.4, 202.7), new Waypoint(150.4, 380.0)};
	
    // durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH4 = {new Waypoint(191.3, 489.0), new Waypoint(273.2, 508.2)};
    
    // zum ersten abladen
	static final Waypoint[] PATH5 = {new Waypoint(273.2, 513.2)};
    	
	// zurueck durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH6 = {new Waypoint(191.3, 489.0), new Waypoint(150.4, 380.0)};
	
	// von nodeII ueber die rampe ins start-modul
	static final Waypoint[] PATH7 = {new Waypoint(150.4, 180.2)};
	

 //ZWEITER DURCHLAUF:	
	
	// vom Eingang zur dritten kiste
	static final Waypoint[] PATH8 = {new Waypoint(125.4, 180.2)};
	
    // von dort zur vierten kiste
	static final Waypoint[] PATH9 = {new Waypoint(125.4, 146.6)};
    
    // von der vierten kiste ueber die rampe
	static final Waypoint[] PATH10 = {new Waypoint(150.4, 202.7), new Waypoint(150.4, 380.0)};
	
    // durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH11 = {new Waypoint(191.3, 489.0), new Waypoint(273.2, 472.6)};
    
    // zum zweiten abladen
	static final Waypoint[] PATH12 = {new Waypoint(273.2, 467.7)};
    
	// zurueck durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH13 = {new Waypoint(191.3, 489.0), new Waypoint(150.4, 380.0)};
	
	// von nodeII ueber die rampe ins start-modul
	static final Waypoint[] PATH14 = {new Waypoint(150.4, 114.0)};
		
	public static final Waypoint[][] PATH = {PATH1, PATH2, PATH3, PATH4, PATH5, PATH6, PATH7, PATH8, PATH9, PATH10, PATH11, PATH12, PATH13, PATH14};
}