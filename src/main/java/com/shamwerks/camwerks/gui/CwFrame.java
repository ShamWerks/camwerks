package com.shamwerks.camwerks.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import com.shamwerks.camwerks.CamWerks;
import com.shamwerks.camwerks.config.Config;
import com.shamwerks.camwerks.config.Lang;
import com.shamwerks.camwerks.config.LangEntry;
import com.shamwerks.camwerks.config.Toolbox;
import com.shamwerks.camwerks.pojo.Camshaft;

public class CwFrame extends JFrame {

	private static final long serialVersionUID = -2332662835630012789L;

	private JTable table;
	private CwPanelDetails cwPanelDetails;
	private CwPanelLineChart panelLineChart;
	private CwPanelCamRadialChart panelCamRadialChart;
    private CwPanelCamTemp panelCamTemp;
    private JTabbedPane tabbedPane; 
    
	public CwPanelLineChart getPanelLineChart() {
		return panelLineChart;
	}

	public CwFrame() {
    	setMinimumSize(new Dimension(800, 600));
        initialize();
    }

    private void initialize() {
        setTitle("CamWerks v." + Config.VERSION);
        setBounds(100, 100, 1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenuBar menuBar = new CwMenu();
        setJMenuBar(menuBar); 
        
        getContentPane().setLayout(new MigLayout("", "[grow]", "[grow]"));
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, "cell 0 0,grow");

        //adding the DETAILS tabbed panel
        cwPanelDetails = new CwPanelDetails();
        tabbedPane.addTab(Lang.getText(LangEntry.TAB_DETAILS), null, cwPanelDetails, null);

        //adding the LINE CHART tabbed panel
        JSplitPane splitPaneCurves = new JSplitPane();
        splitPaneCurves.setDividerLocation(250);
        tabbedPane.addTab(Lang.getText(LangEntry.TAB_CHART_LINE), null, splitPaneCurves, null);
        table = new JTable(){
        	public TableCellRenderer getCellRenderer(int row, int column) {
        		TableColumn tableColumn = getColumnModel().getColumn(column);
        		TableCellRenderer renderer = tableColumn.getCellRenderer();
        		if (renderer == null) {
        			Class c = getColumnClass(column);
        			if( c.equals(Object.class) )
        			{
        				Object o = getValueAt(row,column);
        				if( o != null )
        					c = getValueAt(row,column).getClass();
        			}
        			renderer = getDefaultRenderer(c);
        		}
        		return renderer;
        	}

        	public TableCellEditor getCellEditor(int row, int column) {
        		TableColumn tableColumn = getColumnModel().getColumn(column);
        		TableCellEditor editor = tableColumn.getCellEditor();
        		if (editor == null) {
        			Class c = getColumnClass(column);
        			if( c.equals(Object.class) )
        			{
        				Object o = getValueAt(row,column);
        				if( o != null )
        					c = getValueAt(row,column).getClass();
        			}
        			editor = getDefaultEditor(c);
        		}
        		return editor;
        	}

        };
        
        table.setDefaultRenderer( JComponent.class, new JComponentCellRenderer() );
        table.setDefaultEditor( JComponent.class, new JComponentCellEditor() );

        JScrollPane sp = new JScrollPane(table);

        JScrollPane scrollPane = new JScrollPane(table);
        splitPaneCurves.setLeftComponent(scrollPane);
        panelLineChart= new CwPanelLineChart();
        splitPaneCurves.setRightComponent(panelLineChart);

        //adding the CAMS RADIAL DIAGRAM tabbed panel
        panelCamRadialChart=new CwPanelCamRadialChart();
        tabbedPane.addTab(Lang.getText(LangEntry.TAB_CHART_RADIALCAM), null, panelCamRadialChart, null);

        //adding the CAMS TEMP DIAGRAM tabbed panel
        //panelCamTemp=new CwPanelCamTemp();
        //tabbedPane.addTab("TEMP", null, panelCamTemp, null);

        //adding the CIRCULAR CHART tabbed panel
        //tabbedPane.addTab(Lang.getText(Lang.TAB_CHART_CIRC), null, new CwPanelTest(), null);
    }

	
	public void updateCamshaftDisplay(){
		Camshaft camshaft = CamWerks.getInstance().getCamshaft();
		String[] columnNames = new String[ (camshaft.getNbCylinders() * (camshaft.getNbIntakeCamPerCylinder() + camshaft.getNbExhaustCamPerCylinder()))  + 2];
		columnNames[0] = "ID";
		columnNames[1] = Lang.getText(LangEntry.TABLE_COLUMN_ANGLE);
		
		Object[][] data = new Object[camshaft.getNbSteps() + 1][ (camshaft.getNbCylinders() * (camshaft.getNbIntakeCamPerCylinder() + camshaft.getNbExhaustCamPerCylinder()))  + 2 ];

		//First, let's find out the column names...
		int colId=2;
		for(String key : camshaft.getKeys()){
			JCheckBox check = new JCheckBox("", camshaft.getCam(key).isDisplay());
			check.setName(key);
			check.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					JCheckBox check = (JCheckBox)e.getSource();
			        CamWerks.getInstance().getCamshaft().getCam(check.getName()).setDisplay( check.isSelected() );
			        CamWerks.getInstance().getFrame().updateCamshaftDisplay();
			      }
			    });
			
			data[0][colId] = check;
			
			columnNames[colId] = camshaft.getCam(key).getColumnName();
			colId++;
		}

		//now, for the data :
		for(int i=0 ; i<camshaft.getNbSteps() ; i++){
			data[i+1][0] = new Integer(i);
			data[i+1][1] = new Double( Toolbox.round(i * (360D / camshaft.getNbSteps()) * 2, 2) );

			int j = 2;
			for(String key : camshaft.getKeys()){
				data[i+1][j] = camshaft.getCam(key).getValue(i);
				j++;
			}
		}

		//CellSpan cellAtt =(CellSpan)fixedModel.getCellAttribute();
	    //cellAtt.combine(new int[] {0}    ,new int[] {0,1});
		
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		//table = new JTable( model );
		table.setModel(model);
		//model.fireTableStructureChanged();
		table.revalidate();
		
		cwPanelDetails.updateCamshaftDetails();
		panelLineChart.updateDatasetFromCamshaft();
		panelCamRadialChart.updateDatasetFromCamshaft();
		
		//panelCamTemp.updateDatasetFromCamshaft();
	}

	public void setTab(int i){
		tabbedPane.setSelectedIndex(i);
	}    
    
}

class JComponentCellRenderer implements TableCellRenderer
{
public Component getTableCellRendererComponent(JTable table, Object value,
boolean isSelected, boolean hasFocus, int row, int column) {
return (JComponent)value;
}
}