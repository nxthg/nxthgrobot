package de.nxthg.pccontrol;

import lejos.robotics.navigation.Waypoint;

public class IssPathes {
	
		
 //ERSTER DURCHLAUF:
	
	// vom start zur ersten kiste
	static final Waypoint[] PATH1 = {new Waypoint(150.4, 146.6), new Waypoint(175.4, 146.6)};
	
    // von dort zur zweiten kiste
	static final Waypoint[] PATH2 = {new Waypoint(175.0, 180.2), new Waypoint(175.4, 180.2)};
    
    // von der zweiten kiste bis 5cm vor rampe
	static final Waypoint[] PATH3 = {new Waypoint(150.4, 202.7), new Waypoint(150.4, 230.0)};
	
	// ueber die Rampe
	static final Waypoint[] PATH4 = {new Waypoint(150.4, 355.0)};
	
	// ((ANLEITUNG: UEBER DIE RAMPE FAHREN!
	// Nach Path 3 manuell via "Travel" (z.B. 100cm) vorwärts fahren...
	// FALL 1 (er ist grade über die rampe gekommen): 
	//        SOBALD er mit den Hinterrädern von der Rampe runter ist, "STOP Navigator!" drücken!!
	//        Dann via Kontextmenu (rechte Maustaste) am Punkt (150|352) "Place Robot" wählen; Ausrichtung auf 90° --> Pfad 4
	// FALL 2 (er ist irgentwie vom Kurs abgekommen):
	//        dann muss die Position und Ausrichtung des Roboters per Kamera ungefähr bestimmmt werden --> "Place Robot" --> Pfad 4))
	
    // von hinter der Rampe durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH5 = {new Waypoint(150.4, 385.0), new Waypoint(191.3, 489.0), new Waypoint(273.2, 508.2)};
    
    // zum ersten abladen
	static final Waypoint[] PATH6 = {new Waypoint(273.2, 513.2)};
    	
	// zurueck durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH7 = {new Waypoint(191.3, 489.0), new Waypoint(150.4, 380.0)};
	
	// von nodeII ueber die rampe ins start-modul
	static final Waypoint[] PATH8 = {new Waypoint(150.4, 180.2)};
	

 //ZWEITER DURCHLAUF:	
	
	// vom Eingang zur dritten kiste
	static final Waypoint[] PATH9 = {new Waypoint(125.4, 180.2)};
	
    // von dort zur vierten kiste
	static final Waypoint[] PATH10 = {new Waypoint(125.4, 146.6)};
    
    // von der vierten kiste ueber die rampe
	static final Waypoint[] PATH11 = {new Waypoint(150.4, 202.7), new Waypoint(150.4, 380.0)};
	
    // durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH12 = {new Waypoint(191.3, 489.0), new Waypoint(273.2, 472.6)};
    
    // zum zweiten abladen
	static final Waypoint[] PATH13 = {new Waypoint(273.2, 467.7)};
    
	// zurueck durch nodeII, falls wir nicht eingreifen
	static final Waypoint[] PATH14 = {new Waypoint(191.3, 489.0), new Waypoint(150.4, 380.0)};
	
	// von nodeII ueber die rampe ins start-modul
	static final Waypoint[] PATH15 = {new Waypoint(150.4, 114.0)};
		
	public static final Waypoint[][] PATH = {PATH1, PATH2, PATH3, PATH4, PATH5, PATH6, PATH7, PATH8, PATH9, PATH10, PATH11, PATH12, PATH13, PATH14, PATH15};
}