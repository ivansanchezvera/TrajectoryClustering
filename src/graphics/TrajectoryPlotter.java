package graphics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cluster.trajectory.*;
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
		
		for(int i = 1; i<setOfClustersDatasets.size(); i++)
		{
			int previousChartIndex = chart.getXYPlot().getSeriesCount();
			System.out.println("Trajectories so far: " + previousChartIndex);
			chart.getXYPlot().setDataset(i, (XYDataset) setOfClustersDatasets.get(i));
		}
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
