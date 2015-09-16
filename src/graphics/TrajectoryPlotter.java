package graphics;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cluster.trajectory.*;
import extras.AuxiliaryFunctions;
import extras.GetPropertyValues;

public class TrajectoryPlotter {

	public TrajectoryPlotter() {
		// TODO Auto-generated constructor stub
	}
	
	public static void drawAllClusters(ArrayList<Cluster> clusters)
	{
		for(Cluster c:clusters)
		{
			drawCluster(c);
		}
	}
	
	public static void drawAllClustersInSameGraph(ArrayList<Cluster> clusters)
	{
		String chartName = "SetOfAllClusters";
		//JFreeChart unifiedChart = new JFreeChart(null);
		ArrayList<Dataset> setOfClustersDatasets = new  ArrayList<Dataset>();
		for(Cluster c:clusters)
		{
			Dataset tempDataset = getClusterChartDataset(c);
			setOfClustersDatasets.add(tempDataset);
		}
		//DatasetGroup 
				//Generate the graph
		
		JFreeChart chart = ChartFactory.createXYLineChart(chartName, "x", "y", (XYDataset) setOfClustersDatasets.get(0), 
						PlotOrientation.VERTICAL, true, true, false);
		
		XYPlot plot = chart.getXYPlot();
		HashSet<Color> usedColors = new HashSet<Color>();
		
		for(int i = 0; i<setOfClustersDatasets.size(); i++)
		{
			int previousChartIndex = plot.getSeriesCount();
			System.out.println("Trajectories so far: " + previousChartIndex);

			plot.setDataset(i, (XYDataset) setOfClustersDatasets.get(i));
			
			//Lets Paint each dataset of just one color for visualization purposes.
			Color color;
			do{
			color = AuxiliaryFunctions.generateRandomColor();
			}while(usedColors.contains(color));
			usedColors.add(color);
			
			XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();
			r.setPaint(color);
			
			//To change the Legend to show only cluster info.
			r.setSeriesVisibleInLegend(false);
			//r.setLegendItemLabelGenerator();
			//Title title = new LegendTitle();
			//chart.addSubtitle(i, );
			
			//This is to turn off the points showing in the plot
			//So we show just the lines (with no points)
			r.setBaseShapesVisible(false);
			//r.setBaseShapesFilled(false);
			
			plot.setRenderer(i, r);
		}
		
		
		//chart.getXYPlot().setDataset(index, dataset)
		try {
			String chartPath = GetPropertyValues.getPropValues("ChartPath");
			//String chartFilename = chartPath + "\\" + chartName + ".jpg";
			String chartFilename = "C:\\GraphClusterOutput\\" + chartName + ".png";
			
			ChartUtilities.saveChartAsPNG(new File(chartFilename), chart, 2000, 1000);
			} catch (IOException e) {
		System.err.println("Problem occurred creating chart: " + e.getMessage());
		}
	
	}
	
	public static Dataset getClusterChartDataset(Cluster c)
	{
		String chartName = "ClusterGraph" + c.getClusterID();
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		for(Trajectory t: c.getElementsAsTrajectoryObjects())
		{
			String trajectoryName = "Trajectory " + t.getTrajectoryId();
			XYSeries series = new XYSeries(trajectoryName);
			for(Point p:t.elements)
			{
				series.add(p.getX(),p.getY());
			}	
			dataset.addSeries(series);
		}
		return dataset;
		
	}
	
	public static void drawCluster(Cluster c)
	{
		String chartName = "ClusterGraph" + c.getClusterID();
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		for(Trajectory t: c.getElementsAsTrajectoryObjects())
		{
			String trajectoryName = "Trajectory " + t.getTrajectoryId();
			XYSeries series = new XYSeries(trajectoryName);
			for(Point p:t.elements)
			{
				series.add(p.getX(),p.getY());
			}	
			dataset.addSeries(series);
		}
		
		//DatasetGroup 
		//Generate the graph
				JFreeChart chart = ChartFactory.createXYLineChart(chartName, "x", "y", dataset, 
						PlotOrientation.VERTICAL, true, true, false);
				
				/*
				//This is to have same ranges in all cluster data and same size in x and y axis
				ValueAxis xAxis = chart.getXYPlot().getRangeAxis();
				ValueAxis yAxis = chart.getXYPlot().getDomainAxis();
				Range range = new Range(250, 300);
				yAxis.setRange(range);
				xAxis.setRange(range);
				*/
				//chart.getXYPlot().setDataset(index, dataset)
				try {
					String chartPath = GetPropertyValues.getPropValues("ChartPath");
					//String chartFilename = chartPath + "\\" + chartName + ".jpg";
					String chartFilename = "C:\\GraphClusterOutput\\" + chartName + ".jpg";
					ChartUtilities.saveChartAsJPEG(new File(chartFilename), chart, 2000, 1000);
					} catch (IOException e) {
					System.err.println("Problem occurred creating chart: " + e.getMessage());
					}
		
	}

	public static void drawTrajectory(Trajectory t)
	{
		String chartName = "TrajectoryGraph"+t.getTrajectoryId();
		XYSeries series = new XYSeries(chartName);
		for(Point p:t.elements)
		{
			series.add(p.getX(),p.getY());
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		
		//Generate the graph
		JFreeChart chart = ChartFactory.createXYLineChart(chartName, "x", "y", dataset, 
				PlotOrientation.VERTICAL, true, true, false);
	
		
		try {
			String chartPath = GetPropertyValues.getPropValues("ChartPath");
			String chartFilename = chartPath + "\\" + chartName + ".jpg";
			//String chartFilename = "C:\\GraphClusterOutput\\" + chartName + ".jpg";
			ChartUtilities.saveChartAsJPEG(new File(chartFilename), chart, 2000, 1000);
			} catch (IOException e) {
				System.err.println("Problem occurred creating chart: " + e.getMessage());
			}
		
	}
}
