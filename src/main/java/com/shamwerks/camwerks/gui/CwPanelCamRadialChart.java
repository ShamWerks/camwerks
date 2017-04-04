package com.shamwerks.camwerks.gui;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.shamwerks.camwerks.CamWerks;
import com.shamwerks.camwerks.config.Lang;
import com.shamwerks.camwerks.config.LangEntry;
import com.shamwerks.camwerks.pojo.Cam;
import com.shamwerks.camwerks.pojo.Camshaft;

public class CwPanelCamRadialChart extends JPanel{

	private static final long serialVersionUID = -9125241701760001557L;

	double baseCircle = 12.0;
	
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private XYSeriesCollection series = new XYSeriesCollection();
	
	DefaultPolarItemRenderer renderer = new DefaultPolarItemRenderer () {
		private static final long serialVersionUID = 1L;

		@Override
        public void drawSeries (Graphics2D g2, Rectangle2D dataArea,
                				PlotRenderingInfo info, PolarPlot plot, 
                				XYDataset dataset, int seriesIndex) {
            boolean newPath = true;
            GeneralPath polyline = new GeneralPath ();
            int numPoints = dataset.getItemCount (seriesIndex);
            for (int i = 0 ; i < numPoints - 1 ; i++) {
                double theta = dataset.getXValue (seriesIndex, i);
                double radius = dataset.getYValue (seriesIndex, i);

                Point p = plot.translateToJava2D(theta, radius, plot.getAxis(), dataArea);
                
                if (p.x == 0 && p.y == 0) {
                    newPath = true;
                }
                else {
                    if (newPath) {
                        polyline.moveTo (p.x, p.y);
                        newPath = false;
                    }
                    else {
                        polyline.lineTo (p.x, p.y);
                    }
                }
            }
            g2.setPaint (lookupSeriesPaint (seriesIndex));
            g2.setStroke (lookupSeriesStroke (seriesIndex));
            g2.draw (polyline);
        }
    };	
	
	public JFreeChart getChart() {
		return chart;
	}

	public CwPanelCamRadialChart() {
		super();

        chart = createChart( (XYDataset)series );
        chart.setBackgroundPaint(Color.white); 

		this.chartPanel = new ChartPanel(chart);

		//To avoid changing size of the font on title/legends
		chartPanel.setMaximumDrawHeight(5000);
		chartPanel.setMinimumDrawHeight(0);
		chartPanel.setMaximumDrawWidth(5000);
		chartPanel.setMinimumDrawWidth(0);
		
        setLayout(new GridLayout(1, 0, 0, 0));
		add(this.chartPanel);
	}

	private JFreeChart createChart(XYDataset paramXYDataset) {
		JFreeChart localJFreeChart = ChartFactory.createPolarChart(null, paramXYDataset, true, true, true);
		return localJFreeChart;
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

		PolarPlot polarPlot = (PolarPlot) chart.getPlot();
		polarPlot.setRenderer(renderer);
        polarPlot.setBackgroundPaint (Color.white);
        polarPlot.setAngleGridlinePaint (Color.lightGray);
        polarPlot.setRadiusGridlinePaint(Color.lightGray);

        int seriesID=0; 
        if(isCompare) seriesID=camshaft.getNbDisplayedCams();
		for (String key : camshaft.getKeys() ) {
			Cam cam = camshaft.getCam(key);
			if(cam.isDisplay()) {
				cam.normalizeValues();
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

				//sliding window average algorithm :
				double[] filtered = new double[camshaft.getNbSteps()];
				int slidingWindowSize = 9; //must be an odd/uneven number : 1 3 5 7 9... 1 means no average, just raw data
				for (int j = 0; j < camshaft.getNbSteps() ; j++){ 

					double total = 0;
					for (int k=0 ; k<slidingWindowSize; k++){
						total += cam.getValueCycle( j - ((slidingWindowSize-1)/2) + k);
					}
					filtered[j] = total / slidingWindowSize;
				}
			
				double dth = (360.0d / camshaft.getNbSteps());
				XYSeries  xys = new XYSeries(legend + ((isCompare)?"_Compare":""));
				for (int j = 0; j < camshaft.getNbSteps() ; j++) { 

					//double l = cam.getValue(j)+baseCircle;
					double l = filtered[j]+baseCircle;

					int prev_j = j-1;
					if (prev_j<0) prev_j=camshaft.getNbSteps()-1;

					double th = Math.toRadians(j * dth) ; 
					double prev_th = Math.toRadians(prev_j * dth) ; 

					double dl =  (filtered[j] - filtered[prev_j]) / (th - prev_th);
					double cth = Math.cos(th);
					double sth = Math.sin(th);
                
					// enveloppe of a familly of lines...
					double x = l * cth - dl * sth;
					double y = l * sth + dl * cth;

					// now, let's convert this cartesian shit into a nice polar diagram...
					double distance = Math.sqrt(x*x + y*y);
					double angle = Math.toDegrees( Math.atan2(y,x) );

					xys.add( angle, distance );
				}
				series.addSeries(xys);
			}
		}
		
		//chart.setTitle(camshaft.getName());
		//chart.fireChartChanged();
	}

}

