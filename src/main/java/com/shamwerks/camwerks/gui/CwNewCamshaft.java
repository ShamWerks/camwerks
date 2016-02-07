package com.shamwerks.camwerks.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.shamwerks.camwerks.CamWerks;
import com.shamwerks.camwerks.config.Constants.CamDirection;
import com.shamwerks.camwerks.config.Lang;
import com.shamwerks.camwerks.config.LangEntry;
import com.shamwerks.camwerks.pojo.Camshaft;

public class CwNewCamshaft extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1488532284145448577L;
	
	private static final String ACTION_START_SEQUENCE = "ACTION_START_SEQUENCE";

	private JSpinner nbCylinders ;
	private JSpinner nbIntakeCamsPerCylinder ;
	private JSpinner nbExhaustCamsPerCylinder ;
	private JSpinner measureCycles;
	private JTextField txtCamshaftName;
	private JComboBox comboDirection; //<ComboItem>
	
	public CwNewCamshaft() {
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle(Lang.getText(LangEntry.NEWCAMSHAFT_TITLE));
		setMinimumSize(new Dimension(420, 230));
		setBounds(200, 200, 600, 364);
		setAlwaysOnTop(true);
		getContentPane().setLayout(null);
		
		txtCamshaftName = new JTextField();
		txtCamshaftName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtCamshaftName.setBounds(384, 11, 200, 33);
		txtCamshaftName.setText(Lang.getText(LangEntry.NEWCAMSHAFT_NAME));
		getContentPane().add(txtCamshaftName);
		txtCamshaftName.setColumns(10);
		
		JLabel lblCamshaftName = new JLabel(Lang.getText(LangEntry.NEWCAMSHAFT_LABEL_NAME) );
		lblCamshaftName.setBounds(10, 11, 364, 33);
		getContentPane().add(lblCamshaftName);
		
		JButton btnNewButton = new JButton("Start sequence");
		btnNewButton.setBounds(384, 279, 200, 45);
		btnNewButton.setActionCommand(ACTION_START_SEQUENCE);
		btnNewButton.addActionListener(this);
		getContentPane().add(btnNewButton);
		
		JLabel lblNumberIntake = new JLabel(Lang.getText(LangEntry.NEWCAMSHAFT_LABEL_NBINT));
		lblNumberIntake.setBounds(10, 55, 364, 33);
		getContentPane().add(lblNumberIntake);
		
		JLabel lblNumberExhaust = new JLabel(Lang.getText(LangEntry.NEWCAMSHAFT_LABEL_NBEXH));
		lblNumberExhaust.setBounds(10, 99, 364, 33);
		getContentPane().add(lblNumberExhaust);
		
		nbIntakeCamsPerCylinder = new JSpinner();
		nbIntakeCamsPerCylinder.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
		nbIntakeCamsPerCylinder.setFont(new Font("Tahoma", Font.PLAIN, 14));
		nbIntakeCamsPerCylinder.setBounds(384, 55, 63, 33);
		getContentPane().add(nbIntakeCamsPerCylinder);
		
		nbExhaustCamsPerCylinder = new JSpinner();
		nbExhaustCamsPerCylinder.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
		nbExhaustCamsPerCylinder.setFont(new Font("Tahoma", Font.PLAIN, 14));
		nbExhaustCamsPerCylinder.setBounds(384, 97, 63, 33);
		getContentPane().add(nbExhaustCamsPerCylinder);
		
		JLabel lblNbCylinders = new JLabel(Lang.getText(LangEntry.NEWCAMSHAFT_LABEL_NBCYLS));
		lblNbCylinders.setBounds(10, 145, 364, 33);
		getContentPane().add(lblNbCylinders);
		
		nbCylinders = new JSpinner();
		nbCylinders.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		nbCylinders.setFont(new Font("Tahoma", Font.PLAIN, 14));
		nbCylinders.setBounds(384, 143, 63, 33);
		getContentPane().add(nbCylinders);
		
		JLabel lblMeasureCycles = new JLabel(Lang.getText(LangEntry.NEWCAMSHAFT_LABEL_NBMEASURECYCLES) );
		lblMeasureCycles.setBounds(10, 191, 364, 33);
		getContentPane().add(lblMeasureCycles);
		
		measureCycles = new JSpinner();
		measureCycles.setModel(new SpinnerNumberModel(1, 1, 5, 1));
		measureCycles.setFont(new Font("Tahoma", Font.PLAIN, 14));
		measureCycles.setBounds(384, 189, 63, 33);
		getContentPane().add(measureCycles);
		
		JLabel lblDirection = new JLabel( Lang.getText(LangEntry.NEWCAMSHAFT_LABEL_DIRECTION) );
		lblDirection.setBounds(10, 237, 364, 33);
		getContentPane().add(lblDirection);
		
		ComboItem[] items = new ComboItem[]{
					new ComboItem(CamDirection.CLOCKWISE, Lang.getText(LangEntry.CAMSHAFT_DIRECTION_CLOCKWISE)), 
					new ComboItem(CamDirection.COUNTERCLOCKWISE, Lang.getText(LangEntry.CAMSHAFT_DIRECTION_COUNTERCLOCKWISE))
				}; 
		comboDirection = new JComboBox(items); //<ComboItem>
		comboDirection.setBounds(384, 233, 200, 33);
		getContentPane().add(comboDirection);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals(ACTION_START_SEQUENCE)){
			int nbIntCamsPerCyl = (Integer) nbIntakeCamsPerCylinder.getValue();
			int nbExhCamsPerCyl = (Integer) nbExhaustCamsPerCylinder.getValue();
			int nbCyls = (Integer) nbCylinders.getValue();
			int nbCycles = (Integer) measureCycles.getValue();
			CamDirection dir = ((ComboItem)comboDirection.getSelectedItem()).getValue(); 
			
			Camshaft camshaft = new Camshaft(txtCamshaftName.getText(), nbCyls, nbIntCamsPerCyl, nbExhCamsPerCyl, CamWerks.getInstance().getConfig().getNbSteps());
			camshaft.setDirection(dir);
			camshaft.setNbCycles(nbCycles);
			CamWerks.getInstance().setCamshaft( camshaft );
			dispose(); //closing the dialog window
			CamWerks.getInstance().runMeasureSequence();
		}
		
	}
}



class ComboItem {
    private CamDirection value;
    private String label;

    public ComboItem(CamDirection value, String label) {
        this.value = value;
        this.label = label;
    }

    public CamDirection getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return label;
    }
}