package de.nxthg.pccontrol;

import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.*;

import de.nxthg.pccontrol.NXTHGNavigationModel.NavEvent;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

/**
 * Implements context menu by calling method to send events to the NXT.
 * 
 * @author Lawrie Griffiths
 */
public class NXTHGMenuAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	protected NavEvent navEvent;
	protected Point p;
	protected NXTHGPCNavigationModel model;
	protected NXTHGNavigationPanel panel;
	
	public NXTHGMenuAction(NavEvent navEvent, String name, Point p, NXTHGPCNavigationModel model, NXTHGNavigationPanel panel) {
		super(name);
		this.navEvent = navEvent;
		this.p = p;
		this.model = model;
		this.panel = panel;
	}
	
	public void actionPerformed(ActionEvent e) {
		float x = (p.x / panel.pixelsPerUnit + panel.mapPanel.viewStart.x) ;
		float y = ((panel.mapPanel.getHeight() - p.y) / panel.pixelsPerUnit + panel.mapPanel.viewStart.y) ;
		Waypoint wp = new Waypoint(x,y);
		
		switch (navEvent) {
		case SET_POSE:
			model.setPose(new Pose(x,y,0));
			panel.repaint();
			break;
		case GOTO:
			model.setTarget(wp);
			model.goTo(wp);
			break;
		case FIND_CLOSEST:
			model.findClosest(x,y);
			break;
		case ADD_WAYPOINT:
			model.addWaypoint(wp);
			break;
		case SET_TARGET:
			model.setTarget(wp);
			break;
		case FOLLOW_PATH:
			model.followPath();
			break;
		case START_NAVIGATOR:
			model.startNavigator();
			break;
		}
	}
}
