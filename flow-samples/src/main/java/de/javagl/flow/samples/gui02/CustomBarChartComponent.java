package de.javagl.flow.samples.gui02;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

@SuppressWarnings("javadoc")
final class CustomBarChartComponent extends JPanel
{
    private final DefaultCategoryDataset dataset;
    
    public CustomBarChartComponent()
    {
        super(new BorderLayout());
        dataset = new DefaultCategoryDataset();
        
        JFreeChart chart = ChartFactory.createBarChart(
            null, null, null, dataset, PlotOrientation.VERTICAL, 
            false, false, false);
        chart.setBackgroundPaint(Color.WHITE);
        
        // Set basic colors
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Only show integer ticks on the y-axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // Set the bar color and (flat, non-gradient) painter
        BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
        barRenderer.setSeriesPaint(0, new Color(128,255,128));
        barRenderer.setSeriesPaint(1, new Color(128,128,255));
        barRenderer.setBarPainter(new StandardBarPainter());
        
        // Avoid empty spaces 
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(0.01);
        domainAxis.setUpperMargin(0.01);
        domainAxis.setCategoryMargin(0.1);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400,300));
        add(chartPanel, BorderLayout.CENTER);
    }
    
    void update(List<Integer> currentData)
    {
        System.out.println("Updating custom bar chart with " + currentData);
        dataset.clear();
        if (currentData == null)
        {
            return;
        }
        for (int i=0; i<currentData.size(); i++)
        {
            Integer value = currentData.get(i);
            dataset.addValue(value, "R", "C" + i);
        }
    }
}
