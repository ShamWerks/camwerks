/* -------------------
 * RingChartDemo1.java
 * -------------------
 * (C) Copyright 2014, by Object Refinery Limited.
 * 
 * http://www.object-refinery.com

  * Launch from start folder with :
  java -cp target/camwerks-0.1-jar-with-dependencies.jar  org.jfree.chart.demo.RingChartDemo1
 */

package com.shamwerks.camwerks.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlotState;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * A simple demonstration application showing how to create a ring chart using
 * data from a {@link DefaultPieDataset}.
 */
public class RingChartDemo1 extends ApplicationFrame {

    private static final long serialVersionUID = 1L;

    /**
     * A subclass of RingPlot that adds a text entry in the middle of the 
     * ring showing the value of the first data item.
     */
    static class CustomRingPlot extends RingPlot {

        /** The font. */
        private Font centerTextFont; 
        
        /** The text color. */
        private Color centerTextColor;
        
        /**
         * Creates a new ring plot for the specified dataset.
         * 
         * @param dataset  the dataset. 
         */
        public CustomRingPlot(PieDataset dataset) {
            super(dataset);
            this.centerTextFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);
            this.centerTextColor = Color.LIGHT_GRAY;
        }
        
        /**
         * Draws one item for the plot and, when drawing the first section,
         * adds the center text.
         * 
         * @param g2  the graphics target (null not permitted).
         * @param section  the section index.
         * @param dataArea  the data area (null not permitted).
         * @param state  the plot state.
         * @param currentPass  the current pass index.
         */
        @Override
        protected void drawItem(Graphics2D g2, int section, 
                Rectangle2D dataArea, PiePlotState state, int currentPass) {
            super.drawItem(g2, section, dataArea, state, currentPass);
            if (currentPass == 1 && section == 0) {
                Number n = this.getDataset().getValue(section);
                g2.setFont(this.centerTextFont);
                g2.setPaint(this.centerTextColor);
                TextUtilities.drawAlignedString(n.toString(), g2, 
                        (float) dataArea.getCenterX(), 
                        (float) dataArea.getCenterY(),  
                        TextAnchor.CENTER);
            }
        }
        
    }
    
    /**
     * Default constructor.
     *
     * @param title  the frame title.
     */
    public RingChartDemo1(String title) {
        super(title);
        setContentPane(createDemoPanel());
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private static PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("A", new Double(210));
        dataset.setValue("B", new Double(150));
        return dataset;
    }

    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return A chart.
     */
    private static JFreeChart createChart(PieDataset dataset) {
        CustomRingPlot plot = new CustomRingPlot(dataset);
        JFreeChart chart = new JFreeChart("Custom Ring Chart", 
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setBackgroundPaint(new GradientPaint(new Point(0, 0), 
                new Color(20, 20, 20), new Point(400, 200), Color.DARK_GRAY));

        // customise the title position and font
        TextTitle t = chart.getTitle();
        t.setHorizontalAlignment(HorizontalAlignment.LEFT);
        t.setPaint(new Color(240, 240, 240));
        t.setFont(new Font("Arial", Font.BOLD, 26));

        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(null);
        plot.setSectionPaint("A", Color.ORANGE);
        plot.setSectionPaint("B", new Color(100, 100, 100));
        plot.setSectionDepth(0.05);
        plot.setSectionOutlinesVisible(false);
        plot.setShadowPaint(null);

        return chart;

    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        chart.setPadding(new RectangleInsets(4, 8, 2, 2));
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(600, 300));
        return panel;
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        RingChartDemo1 demo = new RingChartDemo1("JFreeChart: Ring Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}
