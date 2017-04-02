package com.shamwerks.camwerks;

import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.jfree.ui.RefineryUtilities;

import com.shamwerks.arduino.Arduino;
import com.shamwerks.arduino.ArduinoException;
import com.shamwerks.camwerks.config.Config;
import com.shamwerks.camwerks.config.Constants.CamDirection;
import com.shamwerks.camwerks.config.Lang;
import com.shamwerks.camwerks.config.LangEntry;
import com.shamwerks.camwerks.gui.CwCamMeasure;
import com.shamwerks.camwerks.gui.CwFrame;
import com.shamwerks.camwerks.pojo.Camshaft;

public class CamWerks {

	private static CamWerks instance = new CamWerks();

	private Config   config;
	private Camshaft camshaft = null;
	private Camshaft camshaftCompareTo = null;
	private CwFrame  frame;

	//private Measure measure = new Measure();
	
	private Arduino arduino = new Arduino();
	
	public static CamWerks getInstance(){
		return instance;
	}

	
	public Camshaft getCamshaft() {
		return camshaft;
	}


	public void setCamshaft(Camshaft camshaft) {
		this.camshaft = camshaft;
	}


	public CwFrame getFrame() {
		return frame;
	}

	public Config getConfig() {
		return config;
	}

	public void setFrame(CwFrame frame) {
		this.frame = frame;
	}

	public static void main(String[] args) throws IOException {
		instance.config = new Config();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					getInstance().setFrame( new CwFrame() );
					RefineryUtilities.centerFrameOnScreen(getInstance().getFrame());
					getInstance().getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	
	public void runMeasureSequence(){
		boolean connected = arduino.initialize();
		
		if(connected) {
			try {
				arduino.doStep();
				arduino.setDirection( (camshaft.getDirection()==CamDirection.CLOCKWISE?-1:1) );
				arduino.doStep();
			} catch (ArduinoException e) {
				e.printStackTrace();
			}

			CwCamMeasure dialog = new CwCamMeasure();
			dialog.setLocationRelativeTo( CamWerks.getInstance().getFrame() );
			dialog.setVisible(true); //Blocking!!

			arduino.disconnect();
		}
		else {
			JOptionPane.showMessageDialog(frame,
				    					  Lang.getText(LangEntry.ERROR_ARDUINO_CONNECT_TEXT),
				    					  Lang.getText(LangEntry.ERROR_ARDUINO_CONNECT_TITLE),
				    					  JOptionPane.ERROR_MESSAGE);
		}
    }


	public Arduino getArduino() {
		return arduino;
	}


	public void setArduino(Arduino arduino) {
		this.arduino = arduino;
	}


	public Camshaft getCamshaftCompareTo() {
		return camshaftCompareTo;
	}


	public void setCamshaftCompareTo(Camshaft camshaftCompareTo) {
		this.camshaftCompareTo = camshaftCompareTo;
	}

}
