package com.shamwerks.camwerks.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import com.shamwerks.arduino.ArduinoException;
import com.shamwerks.camwerks.CamWerks;
import com.shamwerks.camwerks.config.Lang;
import com.shamwerks.camwerks.config.LangEntry;
import com.shamwerks.camwerks.pojo.Cam;
import com.shamwerks.camwerks.pojo.Camshaft;

public class CwCamMeasure extends JDialog implements ActionListener{ //, Observer 

	private static final long serialVersionUID = 1488532284145448577L;

	private static final String ACTION_START_MEASURE = "ACTION_START_MEASURE";

	JLabel lblStep, lblValue, lblCycle, lblPlease;
	JProgressBar progressBar; 
	JButton btnStartButton;

	private int measureSequenceIdx = 0;
	private double normalizeOffset = 0.0;
	
	public CwCamMeasure() {
		Camshaft camshaft = CamWerks.getInstance().getCamshaft();
		
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("New CamShaft");
		setMinimumSize(new Dimension(420, 230));
		setBounds(200, 200, 559, 269);
		setAlwaysOnTop(true);
		getContentPane().setLayout(null);

		lblPlease = new JLabel(Lang.getText( LangEntry.CAM_MEASURE_PLEASE_MSG ) + " " + camshaft.getCam(measureSequenceIdx).getDescription() );
		lblPlease.setBounds(10, 11, 533, 33);
		getContentPane().add(lblPlease);

		btnStartButton = new JButton("Start measure");
		btnStartButton.setBounds(343, 185, 200, 45);
		btnStartButton.setActionCommand(ACTION_START_MEASURE);
		btnStartButton.addActionListener(this);
		getContentPane().add(btnStartButton);

		JLabel lblCurrCam = new JLabel( "" );
		lblCurrCam.setBounds(188, 11, 180, 33);
		getContentPane().add(lblCurrCam);

		progressBar = new JProgressBar();
		progressBar.setMaximum( CamWerks.getInstance().getConfig().getNbSteps() * CamWerks.getInstance().getCamshaft().getNbCycles() );
		progressBar.setBounds(10, 97, 533, 33);
		getContentPane().add(progressBar);

		lblStep = new JLabel("Step : ");
		lblStep.setBounds(10, 141, 130, 33);
		getContentPane().add(lblStep);

		lblValue = new JLabel("Value : ");
		lblValue.setBounds(150, 141, 130, 33);
		getContentPane().add(lblValue);

		lblCycle = new JLabel("Cycle : ");
		lblCycle.setBounds(290, 141, 130, 33);
		getContentPane().add(lblCycle);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(ACTION_START_MEASURE)){
			Thread t = new Thread(new ProcessNextCamThread(this));
			t.start();
		}
	}

	public void processNextCam(){
		CamWerks.getInstance().getFrame().setTab(1); //selecting curves tab
		
		Camshaft camshaft = CamWerks.getInstance().getCamshaft();
		//disable button
		
		Cam cam = camshaft.getCam( measureSequenceIdx );
		
		btnStartButton.setEnabled(false);

		double[][] tabMEasures = new double[camshaft.getNbCycles()][camshaft.getNbSteps()];
		
		try {
			for(int c=0 ; c<camshaft.getNbCycles() ; c++){ //cycle
				for(int i=0 ; i<camshaft.getNbSteps() ; i++){
					CamWerks.getInstance().getArduino().doStep();
					Thread.sleep(20); // waiting a bit to avoid vibrations in the bench
					
					double measure = CamWerks.getInstance().getArduino().getMeasure();
					tabMEasures[c][i] = measure;

					cam.setValue(i, measure - normalizeOffset);
					CamWerks.getInstance().getFrame().updateCamshaftDisplay();//.getPanelLineChart().updateDatasetFromCamshaft();

					lblStep.setText( "Step : "   + (i+1) + " / " + camshaft.getNbSteps() );
					lblCycle.setText( "Cycle : " + (c+1) + " / " + camshaft.getNbCycles() );
					lblValue.setText( "Value : " + measure );
					progressBar.setValue((i+1) + (c*camshaft.getNbSteps()) );
				}//end for i
			}
		} catch (ArduinoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//averaging measures...
		for(int i=0 ; i<camshaft.getNbSteps() ; i++){
			double avg = 0;
			for(int c=0 ; c<camshaft.getNbCycles() ; c++){ //cycle
				avg = avg + tabMEasures[c][i];
			}
			avg = avg / camshaft.getNbCycles();
			cam.setValue(i, avg);
		}

		normalizeOffset = cam.normalizeValues();
		
		CamWerks.getInstance().getFrame().updateCamshaftDisplay();//.getPanelLineChart().updateDatasetFromCamshaft();
		
		measureSequenceIdx++;
		if(measureSequenceIdx >= camshaft.getNbCams() ) {
			dispose();
			JOptionPane.showMessageDialog(CamWerks.getInstance().getFrame(),
					  Lang.getText(LangEntry.CAM_MEASURE_SUCESS_TEXT),
					  Lang.getText(LangEntry.CAM_MEASURE_SUCESS_TITLE),
					  JOptionPane.INFORMATION_MESSAGE);
		}
		else{
			progressBar.setValue(0);
			btnStartButton.setEnabled(true);
			lblPlease.setText( Lang.getText( LangEntry.CAM_MEASURE_PLEASE_MSG ) + " " + camshaft.getCam(measureSequenceIdx).getDescription() );
		}

	}

}





class ProcessNextCamThread implements Runnable{
	private CwCamMeasure dialog;

	ProcessNextCamThread(CwCamMeasure dialog){
		this.dialog = dialog;
	}

	@Override
	public void run() {
		dialog.processNextCam();
	}
}