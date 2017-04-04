package com.shamwerks.camwerks.gui;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

import com.shamwerks.camwerks.CamWerks;
import com.shamwerks.camwerks.config.Lang;
import com.shamwerks.camwerks.config.LangEntry;
import com.shamwerks.camwerks.pojo.Cam;
import com.shamwerks.camwerks.pojo.Camshaft;

public class CwPanelLineChart extends JPanel implements ChartMouseListener {

	private static final long serialVersionUID = -9125241701760001557L;
	private static final int SERIES_COUNT = 2;
	private ChartPanel chartPanel;
	private Crosshair xCrosshair;
	private Crosshair[] yCrosshairs;
	
	private JFreeChart chart;
	private XYSeriesCollection series = new XYSeriesCollection();
	
	public JFreeChart getChart() {
		return chart;
	}

	public CwPanelLineChart() {
		super();

        chart = createChart( (XYDataset)series );
        chart.setBackgroundPaint(Color.white); 

		this.chartPanel = new ChartPanel(chart);
		
		//To avoid changing size of the font on title/legends
		chartPanel.setMaximumDrawHeight(5000);
		chartPanel.setMinimumDrawHeight(0);
		chartPanel.setMaximumDrawWidth(5000);
		chartPanel.setMinimumDrawWidth(0);
		
		this.chartPanel.addChartMouseListener(this);
		CrosshairOverlay localCrosshairOverlay = new CrosshairOverlay();
		this.xCrosshair = new Crosshair((0.0D / 0.0D), Color.GRAY, new BasicStroke(0.0F));
		this.xCrosshair.setLabelVisible(true);
		localCrosshairOverlay.addDomainCrosshair(this.xCrosshair);
		this.yCrosshairs = new Crosshair[SERIES_COUNT];
		for (int i = 0; i < SERIES_COUNT; i++) {
			this.yCrosshairs[i] = new Crosshair((0.0D / 0.0D), Color.GRAY, new BasicStroke(0.0F));
			this.yCrosshairs[i].setLabelVisible(true);
			if (i % 2 != 0) {
				this.yCrosshairs[i].setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			}
			localCrosshairOverlay.addRangeCrosshair(this.yCrosshairs[i]);
		}
		this.chartPanel.addOverlay(localCrosshairOverlay);

        setLayout(new GridLayout(1, 0, 0, 0));
		add(this.chartPanel);
	}

	private JFreeChart createChart(XYDataset paramXYDataset) {
		JFreeChart localJFreeChart = ChartFactory.createXYLineChart(null, Lang.getText(LangEntry.CHART_LEGEND_DEGREES), Lang.getText(LangEntry.CHART_LEGEND_LIFT), paramXYDataset);
		return localJFreeChart;
	}

	public void updateDatasetFromCamshaft_OriginalOK(){
		Camshaft camshaft = CamWerks.getInstance().getCamshaft();
		series.removeAllSeries();
		XYPlot xyPlot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = xyPlot.getRenderer();
        xyPlot.setBackgroundPaint (Color.white);
        xyPlot.setDomainGridlinePaint(Color.lightGray);
        xyPlot.setRangeGridlinePaint(Color.lightGray);
        
        int i=0;
		for (String key : camshaft.getKeys() ) {
			Cam cam = camshaft.getCam(key);
			if(cam.isDisplay()) {
	        renderer.setSeriesPaint(i, Color.blue);
	        String legend = Lang.getText(LangEntry.CHART_LEGEND_CYL) +" " + cam.getCylNumber() + "/"+ Lang.getText(LangEntry.CHART_LEGEND_INT) + " " + cam.getCamNumber(); //(i+1)
			if(camshaft.getCam(key).isExhaust()){
		        renderer.setSeriesPaint(i, Color.red);
		        legend = Lang.getText(LangEntry.CHART_LEGEND_CYL) +" " + cam.getCylNumber() + "/" + Lang.getText(LangEntry.CHART_LEGEND_EXH) + " " + cam.getCamNumber(); //(i+1)
			}
			
			XYSeries localXYSeries = new XYSeries(legend);
			
			for (int j = 0; j < camshaft.getNbSteps() ; j++) {
				double x = j * (360.0F / camshaft.getNbSteps()) * 2;
				localXYSeries.add(x, cam.getValue(j));
			}
			series.addSeries(localXYSeries);
			
			
			/////////////
			//chart.getCategoryPlot().getRenderer(i).setSeriesPaint(0, Color.RED);
			///////////////
			i++;
			}
		}

		chart.setTitle(camshaft.getName());
		
		chart.fireChartChanged();
	}

	
	public void updateDatasetFromCamshaft(){
		series.removeAllSeries();
		updateDatasetFromCamshaft( CamWerks.getInstance().getCamshaft() , false );
		String chartTitle = CamWerks.getInstance().getCamshaft().getName();
		
		if(CamWerks.getInstance().getCamshaftCompareTo() != null){
		  updateDatasetFromCamshaft( CamWerks.getInstance().getCamshaftCompareTo() , true);
		  chartTitle += " vs. " +  CamWerks.getInstance().getCamshaftCompareTo().getName();
		}
		
		chart.setTitle(chartTitle);
		chart.fireChartChanged();
	}
	
	
	private void updateDatasetFromCamshaft(Camshaft camshaft, boolean isCompare){
		if(camshaft == null) return;

		XYPlot xyPlot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = xyPlot.getRenderer();
        xyPlot.setBackgroundPaint (Color.white);
        xyPlot.setDomainGridlinePaint(Color.lightGray);
        xyPlot.setRangeGridlinePaint(Color.lightGray);
        
        int seriesID=0;
        if(isCompare) seriesID=camshaft.getNbDisplayedCams();
		for (String key : camshaft.getKeys() ) {
			Cam cam = camshaft.getCam(key);
			if(cam.isDisplay()) {
				renderer.setSeriesPaint(seriesID, Color.blue);
				String legend = Lang.getText(LangEntry.CHART_LEGEND_CYL) +" " + cam.getCylNumber() + "/"+ Lang.getText(LangEntry.CHART_LEGEND_INT) + " " + cam.getCamNumber(); //(i+1)
				if(camshaft.getCam(key).isExhaust()){
					renderer.setSeriesPaint(seriesID, Color.red);
					legend = Lang.getText(LangEntry.CHART_LEGEND_CYL) +" " + cam.getCylNumber() + "/" + Lang.getText(LangEntry.CHART_LEGEND_EXH) + " " + cam.getCamNumber(); //(i+1)
				}
				if(isCompare){
					if(camshaft.getCam(key).isExhaust()){
						renderer.setSeriesPaint(seriesID, Color.orange);
					}
					else{
						renderer.setSeriesPaint(seriesID, Color.green);
					}
				}
				seriesID++;
			
				XYSeries localXYSeries = new XYSeries(legend + ((isCompare)?"_Compare":""));
			
				for (int j = 0; j < camshaft.getNbSteps() ; j++) {
					double x = j * (360.0d / camshaft.getNbSteps()) * 2;
					localXYSeries.add(x, cam.getValue(j));
				}
				series.addSeries(localXYSeries);
			
			}
		}
	}
	
	public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent) {}

	public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent) {
		Rectangle2D localRectangle2D = this.chartPanel.getScreenDataArea();
		JFreeChart localJFreeChart = paramChartMouseEvent.getChart();
		XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
		ValueAxis localValueAxis = localXYPlot.getDomainAxis();
		double d1 = localValueAxis.java2DToValue(paramChartMouseEvent.getTrigger().getX(), localRectangle2D, RectangleEdge.BOTTOM);
		this.xCrosshair.setValue(d1);
		for (int i = 0; i < SERIES_COUNT; i++)
		{ try{
			double d2 = DatasetUtilities.findYValue(localXYPlot.getDataset(), i, d1);
			this.yCrosshairs[i].setValue(d2);}
		catch(Exception e){}
		}
	}

}

