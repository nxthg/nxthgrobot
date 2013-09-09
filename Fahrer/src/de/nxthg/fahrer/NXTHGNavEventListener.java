package de.nxthg.fahrer;

import de.nxthg.fahrer.NXTHGNavigationModel.NavEvent;


public interface NXTHGNavEventListener {
	public void whenConnected();
	
	public void eventReceived(NavEvent navEvent);
}
