package de.nxthg.pccontrol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.nxthg.pccontrol.NXTHGNavigationModel.NavEvent;

public class NXTHGSliderPanel_Seitwärts extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	protected NXTHGPCNavigationModel model;
	protected String sliderLabelText;
	protected NavEvent event;
	protected JButton button;
	protected JLabel label;
	protected JSlider slider;
	
	public NXTHGSliderPanel_Seitwärts(NXTHGPCNavigationModel model, NavEvent event, String sliderLabel, String buttonLabel, int maxValue) {
		slider  = new JSlider(-5,5);
		this.model = model;
		this.event = event;
		sliderLabelText = sliderLabel;
		button = new JButton(buttonLabel);
		button.addActionListener(this);
		label = new JLabel(sliderLabel + " " + "0");
		slider.addChangeListener(this);
		
		slider.setMajorTickSpacing(maxValue/4);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		
		label.setPreferredSize(new Dimension(100,20));
		
		add(label);
		add(slider);
		add(button);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	public void setMaxValue(int value) {
		slider.setMaximum(value);
		slider.setMajorTickSpacing(value/4);		
	}

	public void actionPerformed(ActionEvent e) {
		if (event == NavEvent.SEITWAERTS) {
			model.drehen(slider.getValue());
		}
	}

	public void stateChanged(ChangeEvent e) {
		label.setText(sliderLabelText +  " " + slider.getValue());	
	}
	
	public void setValue(int value) {
		slider.setValue(value);
	}
}
