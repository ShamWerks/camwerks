package com.shamwerks.camwerks.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.shamwerks.camwerks.CamWerks;
import com.shamwerks.camwerks.config.Lang;
import com.shamwerks.camwerks.config.LangEntry;
import com.shamwerks.camwerks.pojo.Cam;
import com.shamwerks.camwerks.pojo.Camshaft;
import com.shamwerks.camwerks.pojo.Coord;
import com.shamwerks.camwerks.pojo.LinearFunction;


public class CwPanelCamTemp extends JPanel{

	private static final long serialVersionUID = -9125241701760001557L;

	final double baseCircle = 15.0;
	final double displayRadius = 25.0;

	private ChartPanel chartPanel;
	private JFreeChart chart;
	private XYSeriesCollection series = new XYSeriesCollection();

	public JFreeChart getChart() {
		return chart;
	}

	public CwPanelCamTemp() {
		super();

		chart = createChart( (XYDataset)series );
		chart.setBackgroundPaint(Color.white);
		chart.removeLegend();

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
		JFreeChart localJFreeChart = ChartFactory.createXYLineChart(null, Lang.getText(LangEntry.CHART_LEGEND_DEGREES), Lang.getText(LangEntry.CHART_LEGEND_LIFT), paramXYDataset);
		return localJFreeChart;
	}

	public void updateDatasetFromCamshaft(){
		Camshaft camshaft = CamWerks.getInstance().getCamshaft();
		series.removeAllSeries();

	
		//int i=0;
		for (String key : camshaft.getKeys() ) {
			Cam cam = camshaft.getCam(key);
			cam.normalizeValues();

			String legend = Lang.getText(LangEntry.CHART_LEGEND_CYL) +" " + cam.getCylNumber() + "/"+ Lang.getText(LangEntry.CHART_LEGEND_INT) + " " + cam.getCamNumber(); //(i+1)
			if(camshaft.getCam(key).isExhaust()){
				//                                   renderer.setSeriesPaint(i, Color.red);
				legend = Lang.getText(LangEntry.CHART_LEGEND_CYL) +" " + cam.getCylNumber() + "/" + Lang.getText(LangEntry.CHART_LEGEND_EXH) + " " + cam.getCamNumber(); //(i+1)
			}

			final double L = 20.0; //Longueur de la tangente
			
			List<LinearFunction> functions = new ArrayList<LinearFunction>();
			
			for (int j = 0; j < camshaft.getNbSteps() ; j=j+1) { 
				double R = cam.getValue(j)+baseCircle;

				double degA = j * (360.0F / camshaft.getNbSteps()) ; 
				double radA = Math.toRadians(degA);
				double radB = Math.atan(L/R);

				Coord p1 = new Coord(
											(R) * Math.sin(radA),
											(R) * Math.cos(radA)
									  	);
				Coord p2 = new Coord(
											(Math.sqrt( Math.pow(R, 2) + Math.pow(L, 2) )) * Math.sin(radA + radB),
											(Math.sqrt( Math.pow(R, 2) + Math.pow(L, 2) )) * Math.cos(radA + radB)
										);

				LinearFunction f1 = new LinearFunction( p1 , p2 );
				f1.angle = degA;
				functions.add(f1);
				
			    XYSeries  xySeries = new XYSeries(legend + j + "t");
				xySeries.add( p1.x - (p2.x-p1.x) , p1.y - (p2.y-p1.y));
				xySeries.add( p1.x , p1.y );
				xySeries.add( p2.x , p2.y );
			    series.addSeries(xySeries);
			    
			}

			for(int i = 0; i < series.getSeriesCount(); i++){
				chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(i, Boolean.FALSE);
			}

			/*
			//Rayons
			for (int i=0 ; i < functions.size() ; i++){
				int next = i+1;
				if(next >= functions.size()) next = next - functions.size();
				Coord c = functions.get(i).getIntersection(functions.get(next));
				XYSeries  xySeries = new XYSeries(legend + " internalcam " + i);
				xySeries.add( 0 , 0 );
				xySeries.add( c.x , c.y );
			    series.addSeries(xySeries);
			}
			*/


			break; //just 1 cam
		}

		/*
                               XYSeries basecircleXYSeries = new XYSeries("Base Circle");
                               for (int j = 0; j < camshaft.getNbSteps() ; j++) {
                                               basecircleXYSeries.add(j * (360.0F / camshaft.getNbSteps()) * 2 , baseCircle);
                               }
                               series.addSeries(basecircleXYSeries);

	 */

		chart.setTitle(camshaft.getName());

		chart.fireChartChanged();
	}

	/*
	private LinearFunction getTangent(Cam cam, int j){
	Camshaft camshaft = CamWerks.getInstance().getCamshaft();
		if(j>=camshaft.getNbSteps()) j = j - camshaft.getNbSteps();
		double angle = Math.toRadians(j * (360.0F / camshaft.getNbSteps()) - 90.0F); //not sure about the -90 yet
		LinearFunction f1 = new LinearFunction();
		f1.a = -1 / (Math.tan(angle));
		f1.b = cam.getValue(j) - (f1.a * (baseCircle + cam.getValue(j)) * Math.sin(angle));

		return f1;
	}

	private LinearFunction getLinearFunctionFrom2Points(double aX, double aY, double bX, double bY){
		LinearFunction f1 = new LinearFunction();
		
		f1.a = (bY - aY) / (bX - aX);
		f1.b = aY - (f1.a * aX);

		return f1;
	}
    */
}













