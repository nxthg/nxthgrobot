package de.nxthg.pccontrol;

import lejos.robotics.navigation.Waypoint;

public class IssPathes {
	
		
 //ERSTER DURCHLAUF:
	
	// vom start zur ersten/zweiten kiste
	static final Waypoint[] PATH1 = {new Waypoint(150.4, 118.2), new Waypoint(175.4, 118.2)};
	
    // von dort zur dritten kiste
	static final Waypoint[] PATH2 = {new Waypoint(150.4, 157.0), new Waypoint(175.4, 157.0)};
    
    // von der driten kiste ueber die rampe
	static final Waypoint[] PATH3 = {new Waypoint(150.4, 202.7), new Waypoint(150.4, 380.0)};
	
    // durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH4 = {new Waypoint(191.7, 490.0), new Waypoint(255.6, 490.0)};
    
    // vom nodeII zum ersten abladen
	static final Waypoint[] PATH5 = {new Waypoint(323.9, 543.2) /*  SEPERATER ABLADEVORGANG (RÜCKWÄRTS!!) new Waypoint(269.1, 543.2)*/};
    
	// vom abladen zum nodeII
	static final Waypoint[] PATH6 = {new Waypoint(255.6, 490.2)};
	
	// zurueck durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH7 = {new Waypoint(191.7, 490.0), new Waypoint(150.4, 380.0)};
	
	// von nodeII ueber die rampe ins start-modul
	static final Waypoint[] PATH8 = {new Waypoint(150.4, 182.5)};
	

 //ZWEITER DURCHLAUF:	
	
	// vom start zur ersten/zweiten kiste
	static final Waypoint[] PATH11 = {new Waypoint(125.4, 118.2)};
	
    // von dort zur dritten kiste
	static final Waypoint[] PATH12 = {new Waypoint(150.4, 144.0), new Waypoint(125.4, 157.0)};
    
    // von der dritten kiste ueber die rampe
	static final Waypoint[] PATH13 = {new Waypoint(150.4, 380.0)};
	
    // durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH14 = {new Waypoint(191.7, 490.0), new Waypoint(255.6, 490.0)};
    
    // vom nodeII zum zweiten abladen
	static final Waypoint[] PATH15 = {new Waypoint(323.9, 437.2) /*  SEPERATER ABLADEVORGANG (RÜCKWÄRTS!!) new Waypoint(269.1, 437.2)*/};
    
	// vom abladen zum nodeII
	static final Waypoint[] PATH16 = {new Waypoint(255.6, 490.2)};
	
	// zurueck durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH17 = {new Waypoint(191.7, 490.0), new Waypoint(150.4, 380.0)};
	
	// von nodeII ueber die rampe ins start-modul
	static final Waypoint[] PATH18 = {new Waypoint(150.4, 182.5)};
		
	public static final Waypoint[][] PATH = {PATH1, PATH2, PATH3, PATH4, PATH5, PATH6, PATH7, PATH8, PATH11, PATH12, PATH13, PATH14, PATH15, PATH16, PATH17, PATH18};
}