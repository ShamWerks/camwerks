package com.shamwerks.camwerks.gui;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

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
import com.shamwerks.camwerks.pojo.Coord;
import com.shamwerks.camwerks.pojo.LinearFunction;

public class CwPanelCamRadialChart extends JPanel{

	private static final long serialVersionUID = -9125241701760001557L;

	double baseCircle = 15.0;
	
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private XYSeriesCollection series = new XYSeriesCollection();
	
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
				                                 //.createXYLineChart(null, Lang.getText(Lang.CHART_LEGEND_DEGREES), Lang.getText(Lang.CHART_LEGEND_LIFT), paramXYDataset);
		return localJFreeChart;
	}

	public void updateDatasetFromCamshaft(){
		Camshaft camshaft = CamWerks.getInstance().getCamshaft();
		series.removeAllSeries();
		
	    //   Rendering serie ( handeling null values )
	    DefaultPolarItemRenderer ren = new DefaultPolarItemRenderer () {
			private static final long serialVersionUID = 1L;

			@Override
	        public void drawSeries (Graphics2D g2, Rectangle2D dataArea,
	            PlotRenderingInfo info, PolarPlot plot, XYDataset dataset, int seriesIndex)
	        {
	            boolean newPath = true;
	            GeneralPath polyline = new GeneralPath ();
	            int numPoints = dataset.getItemCount (seriesIndex);
	            for (int i = 0 ; i < numPoints - 1 ; i++)
	            {
	                double theta = dataset.getXValue (seriesIndex, i);
	                double radius = dataset.getYValue (seriesIndex, i);

	                Point p = plot.translateToJava2D(theta, radius, plot.getAxis(), dataArea);
	                
	                if (p.x == 0 && p.y == 0)
	                {
	                    newPath = true;
	                }
	                else
	                {
	                    if (newPath)
	                    {
	                        polyline.moveTo (p.x, p.y);
	                        newPath = false;
	                    }
	                    else
	                    {
	                        polyline.lineTo (p.x, p.y);
	                    }
	                }
	            }
	            g2.setPaint (lookupSeriesPaint (seriesIndex));
	            g2.setStroke (lookupSeriesStroke (seriesIndex));
	            g2.draw (polyline);
	        }
	    };

		
		PolarPlot polarPlot = (PolarPlot) chart.getPlot();
		polarPlot.setRenderer(ren);
        //PolarItemRenderer renderer = xyPlot.getRenderer();
        polarPlot.setBackgroundPaint (Color.white);
        polarPlot.setAngleGridlinePaint (Color.lightGray);
        polarPlot.setRadiusGridlinePaint(Color.lightGray);
        //polarPlot.
       
        //int i=0; 
		for (String key : camshaft.getKeys() ) {
			Cam cam = camshaft.getCam(key);
			cam.normalizeValues();
//	        renderer.setSeriesPaint(i, Color.blue);
	        String legend = Lang.getText(LangEntry.CHART_LEGEND_CYL) +" " + cam.getCylNumber() + "/"+ Lang.getText(LangEntry.CHART_LEGEND_INT) + " " + cam.getCamNumber(); //(i+1)
			if(camshaft.getCam(key).isExhaust()){
//		        renderer.setSeriesPaint(i, Color.red);
		        legend = Lang.getText(LangEntry.CHART_LEGEND_CYL) +" " + cam.getCylNumber() + "/" + Lang.getText(LangEntry.CHART_LEGEND_EXH) + " " + cam.getCamNumber(); //(i+1)
			}

			
			/////////////////////////////////////////////
			double L = 20.0; //Longueur de la tangente
			
			List<LinearFunction> functions = new ArrayList<LinearFunction>();
			
			for (int j = 0; j < camshaft.getNbSteps() ; j=j+1) { //camshaft.getNbSteps()
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
			}

			XYSeries  xySeries = new XYSeries(legend);
			
			for (int i=0 ; i < functions.size() ; i++){
				int next = i+1;
				if(next >= functions.size()) next = next - functions.size();
				Coord c = functions.get(i).getIntersection(functions.get(next));
				
				double angle = Math.toDegrees((Math.atan(c.x/c.y)))   ;//Math.abs(   
				double dist = Math.sqrt( Math.pow(c.y, 2) + Math.pow(c.x, 2) );

				/*
				int next2 = i+2;
				if(next2 >= functions.size()) next2 = next2 - functions.size();
				Coord c2 = functions.get(i).getIntersection(functions.get(next2));
				dist = Math.min( dist, Math.sqrt( Math.pow(c2.y, 2) + Math.pow(c2.x, 2) ) );
				*/
				
				
			  //if(functions.get(i).angle > 180) angle = 180-angle;
				double angleBefore = angle;
				if(c.x > 0 && c.y < 0) angle = 180+angle;
				if(c.x < 0 && c.y < 0) angle = 180+angle;
		        if(c.x < 0 && c.y > 0) angle = 360+angle;
				
System.out.println("angleBefore="+angleBefore +"  ==>  angle=" + angle + "   f.angle="+functions.get(i).angle+" / dist = " + dist + "        (coord x="+c.x+" y="+c.y+")");				
				
				xySeries.add( angle, dist );
			}
		    series.addSeries(xySeries);



		    //--------------------------------------------
		    // NEW ALGORITHM
		    //Let's give it a shot
			
			XYSeries  xys = new XYSeries(legend + " NEW ALGO");
			System.out.println("====================================================================");
			
			for (int a=0 ; a < 360 ; a=a+1){
				double minDist = Double.MAX_VALUE;

				LinearFunction f = new LinearFunction( Math.tan(a) , 0);
				
				for (int i=0 ; i < functions.size() ; i++){
					Coord c = f.getIntersection(functions.get(i));

			        if (Math.abs(functions.get(i).angle-a) < 45){
			        	minDist = Math.min(minDist,  Math.sqrt( Math.pow(c.y, 2) + Math.pow(c.x, 2) ));
						System.out.println("             a=" + a + "  angle="+functions.get(i).angle+ "==>  minDist=" + minDist + "(i=" + i + ")");				
			        }
				}
				System.out.println("a=" + a + "  ==>  minDist=" + minDist);
				//if(minDist != Double.MAX_VALUE) 
				xys.add( a, 10 );
				xys.add( a, minDist );
				xys.add( a, 10 );
			}
		    series.addSeries(xys);
		    
		    //--------------------------------------------
		    
		    
		    
		    
		    
			//break;  //just 1 cam			
			/////////////////////////////////////////////
			
		    
		    
		    
		    
		    
			//XYSeries localXYSeries = new XYSeries(legend);

			/*
			for (int j = 0; j < camshaft.getNbSteps() ; j++) {
				double x = j * (360.0F / camshaft.getNbSteps()); 
						// radians!!!!!!;
				System.out.println("x=" + x + " / val=" + cam.getValue(j) + " / calc="+(cam.getValue(j)/Math.sin(Math.toRadians(x))));
				//localXYSeries.add(x, Math.abs(cam.getValue(j)/Math.sin(Math.toRadians(x))) + baseCircle);
				localXYSeries.add(x, cam.getValue(j) + baseCircle);
			}
			*/
		    /*
			for (int j = 0; j < camshaft.getNbSteps() ; j++) {
				double angle = (j * (360.0F / camshaft.getNbSteps()) - 90); //not sure about the -90 yet 
				
				LinearFunction f1 = getTangent(cam, j);
				LinearFunction f2 = getTangent(cam, j+1);

				Coord intersect = f1.getIntersection(f2);
				
				double hyp = Math.sqrt(Math.pow(intersect.x, 2) + Math.pow(intersect.y, 2));
				
				System.out.println("j=" + j + " / angle=" + angle + " / f=" + f1 + " f2=" + f2 + "/ hyp=" + hyp );

				localXYSeries.add(angle, hyp);
			}

			
			series.addSeries(localXYSeries);
			*/
			
			/////////////
			//chart.getCategoryPlot().getRenderer(i).setSeriesPaint(0, Color.RED);
			///////////////
			//i++;
		}

		/*
		//Drawing the base circle :
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
		
		double angle = Math.toRadians(j * (360.0F / camshaft.getNbSteps()) - 90); //not sure about the -90 yet 
		
		LinearFunction f1 = new LinearFunction();
		f1.a = -1 / (Math.tan(angle));
		f1.b = cam.getValue(j) - (f1.a * (baseCircle + cam.getValue(j)) * Math.sin(angle));

		return f1;
	}

	private LinearFunction getTangent(double aX, double aY, double bX, double bY){
		LinearFunction f1 = new LinearFunction();
		
		f1.a = (bY - aY) / (bX - aX);
		f1.b = aY - (f1.a * aX);

		return f1;
	}
	*/
}

