package cluster.trajectory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import cluster.trajectory.*;

import com.stromberglabs.cluster.Clusterable;


public class Traclus {

	private ArrayList<Trajectory> trajectories;
	private ArrayList<Segment> segmentsCompleteSet;
	private ArrayList<Cluster> clusterOfTrajectories;
	private float eNeighborhoodParameter;
	private int minLins;
	private int cardinalityOfClusters;
	
	private double epsilonDouglasPeucker;
	private int fixedNumOfTrajectoryPartitionsDouglas;
	
	//To select Segmentation Method
	SegmentationMethod segmentationMethod;
	//Parameters
	//Notion of E Neighbourhood
	//Notion of acceptable time distance
	//
	ArrayList<Cluster> setOfClusters;
	
	//Constructor for Douglas Peucker Partition Traclus
	public Traclus(ArrayList<Trajectory> trajectories, float eNeighborhoodParameter, int minLins, int cardinalityOfClusters, 
			double epsilonDouglas, int fixedNumberTrajPartitionDouglas, SegmentationMethod segmentationMethod) {
		this.trajectories = trajectories;
		this.segmentsCompleteSet = new ArrayList<Segment>();
		this.clusterOfTrajectories = new ArrayList<Cluster>();
		this.eNeighborhoodParameter = eNeighborhoodParameter;
		this.minLins = minLins;
		this.cardinalityOfClusters = cardinalityOfClusters;
		this.fixedNumOfTrajectoryPartitionsDouglas = fixedNumberTrajPartitionDouglas;
		this.epsilonDouglasPeucker = epsilonDouglas;
		this.segmentationMethod = segmentationMethod;
	}
	
	//Constructor for Original Partition Method Traclus
	public Traclus(ArrayList<Trajectory> trajectories, float eNeighborhoodParameter, int minLins, int cardinalityOfClusters, SegmentationMethod segmentationMethod) {
		this.trajectories = trajectories;
		this.segmentsCompleteSet = new ArrayList<Segment>();
		this.clusterOfTrajectories = new ArrayList<Cluster>();
		this.eNeighborhoodParameter = eNeighborhoodParameter;
		this.minLins = minLins;
		this.cardinalityOfClusters = cardinalityOfClusters;
		this.segmentationMethod = segmentationMethod;
	}
	
	public ArrayList<Cluster> executeTraclus()
	{
		segmentsCompleteSet = partition(trajectories);
		
		//for Traclus clustering approach, cluster over segments
		clusterOfTrajectories = clusterSegments(segmentsCompleteSet, eNeighborhoodParameter, minLins, cardinalityOfClusters);
		
		return clusterOfTrajectories;
	}
	
	/**
	 * To do the density base clustering over the whole trajectory
	 * and using DTW as a distance (similarity metric).
	 * @return
	 */
	public ArrayList<Cluster> executeDensityBasedClusterOverTrajectories(boolean simplifyTrajectories)
	{
		ArrayList<Trajectory> simplifiedTrajectories = null;
		if(simplifyTrajectories)
		{
			simplifiedTrajectories = simplifyTrajectories(trajectories);
		}else{
			simplifiedTrajectories = trajectories;
		}
		
		
		//For new Rao Approach, do clustering over trajectories.
		//Form clustering over DTW
		clusterOfTrajectories = clusterTrajectoriesWithDTW(simplifiedTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters);
				
		return clusterOfTrajectories;
	}
	
	public ArrayList<Cluster> executeKMeansClusterOverTrajectories(int k)
	{
		ArrayList<Trajectory> simplifiedTrajectories = simplifyTrajectories(trajectories);
		
		
		//Export trajectories to visualize them
		exportPlotableCoordinatesForAllTrajectories(simplifiedTrajectories);
		
		//For new Rao Approach, do clustering over trajectories.
		//Form clustering over DTW
		clusterOfTrajectories = clusterTrajectoriesKMeans(simplifiedTrajectories, k);		
		
		return clusterOfTrajectories;
	}
	
	//3 clear stages
	//Partition Phase
	//Input Set of Trajectories
	public ArrayList<Segment> partition(ArrayList<Trajectory> trajectories)
	{
		ArrayList<Segment> setOfSegments = new ArrayList<Segment>();
		
		for(Trajectory t:trajectories)
		{
			
			//For Traclus Processing of Partition of trajectory into segments
			if(segmentationMethod == SegmentationMethod.traclus)
			setOfSegments.addAll(t.divideTrajectoryInSegmentsTraclus());
			
			if(segmentationMethod == SegmentationMethod.douglasPeucker)
			//For Douglas-Peucker Partition of trajectory into segments
			setOfSegments.addAll(t.divideTrajectoryInSegmentsDouglasPeucker(epsilonDouglasPeucker, fixedNumOfTrajectoryPartitionsDouglas));
		}
		
		return setOfSegments;
	}
	
	public ArrayList<Trajectory> simplifyTrajectories(ArrayList<Trajectory> trajectories)
	{
		ArrayList<Trajectory> setOfSimplifiedTrajectories = new ArrayList<Trajectory>();
		int error = 0;
		for(Trajectory t:trajectories)
		{
			if(segmentationMethod == SegmentationMethod.douglasPeucker)
			{
			//For Douglas-Peucker simplification of trajectories
				Trajectory simplifiedTrajectory = t.simplifyTrajectoryDouglasPeucker(epsilonDouglasPeucker, fixedNumOfTrajectoryPartitionsDouglas);
				
				if(simplifiedTrajectory.getPoints().size()>=fixedNumOfTrajectoryPartitionsDouglas+1)
				{
				setOfSimplifiedTrajectories.add(simplifiedTrajectory);
				
				//*****************Just to print the simplified trajectories****************
				//System.out.println(simplifiedTrajectory.printLocation());
				//simplifiedTrajectory.exportPlotableCoordinates();
				simplifiedTrajectory.exportPlotableCoordinatesCSV();
				//Just to print the simplified trajectories
				//interrupt();
				//*****************End of Print trajectories****************
				}else{
					
					System.out.println(error + " error is here in trajectory " + t.getTrajectoryId() + 
							", num points in simplified trajectory: " + simplifiedTrajectory.getPoints().size());
					error++;
					//interrupt();
				}
			}
		}
				
		return setOfSimplifiedTrajectories;
	}
	
	//Only to stop the flow of information in console
	private void interrupt()
	{
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void exportPlotableCoordinatesForAllTrajectories(ArrayList<Trajectory> trajectories)
	{
		try {
			String filename = "exportedTrajectoriesToPlot.txt";
			File file = new File(filename);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(Trajectory t:trajectories)
			{
					//*****************Just to print the simplified trajectories****************
					//System.out.println(simplifiedTrajectory.printLocation());
					bw.write(t.printToPlotWithOtherTrajectories());
					//Just to print the simplified trajectories
					//interrupt();
					//*****************End of Print trajectories***************
			}
			bw.close();
			//System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//********************HASHING STAGE*********************************
	
	
	//Do HASHING BEFORE THIS
	//Clustering Phase
	public ArrayList<Cluster> clusterSegments(ArrayList<Segment> setOfSegments, float eNeighborhoodParameter, int minLins, int cardinalityOfClusters)
	{
		setOfClusters = new ArrayList<Cluster>();
		
		int clusterId = 0;
		
		for(Segment s: setOfSegments)
		{
			if(!s.isClassified())
			{
				//To compute eNeighboorhood is a hard part
				//Cost n^2 unless we used index like R-Tree which reduces complexity to (n log n)
				//where n is the number of segments in the database.
				//Find Out how to index this.
				//MOre info at the end of page 7 (599) of paper
				ArrayList<Segment> neighborSegments = computeENeighborhoodOfSegment(s, eNeighborhoodParameter, setOfSegments);
				
				//Plus 1 cause my definition of neighborhood does not include core line itself
				if(neighborSegments.size()+1>= minLins)
				{
					ClusterSegments c = new ClusterSegments(clusterId, "Cluster " + clusterId);
					setOfClusters.add(c);
					
					//Before adding to cluster,
					//Should I set all segments of the neighborhood as classified??
					int i = 0;
					for(Segment s1:neighborSegments)
					{
						
						
						s1.setClassified(true);
						s1.setNoise(false);
						//neighborSegments.set(i, s1);
						c.addSegment(s1);
						i++;
					}
					
					//Add neighbours to cluster
					//My definition of neighborhood does not include the core line itself
					//This line is extra since I am already iterating, might be little more efficient?
					//c.setSegments(neighborSegments);
					
					//Add this core line to this cluster
					s.setNoise(false);
					s.setClassified(true);
					c.addSegment(s);
					
					//Insert Neighboors into a Queue (check this part)
					ArrayList<Segment> queue = new ArrayList<Segment>();
					queue.addAll(neighborSegments);
					
					//Now Expand Cluster
					expandClusterSegments(queue, clusterId, eNeighborhoodParameter, minLins, setOfSegments);
					clusterId++;
				}else{
					s.setClassified(true);
					s.setNoise(true);
				}
			}
		}
		
		//Step 3
		//Clusters are already allocated in my code, this differs from paper description of Traclus.
		//Check cardinality

		ArrayList<Cluster> filteredSetOfClusters = filterClustersByCardinality(cardinalityOfClusters, setOfClusters);
		
		//Cluster using Density-based method
		//Need a notion of distance and a notion of time difference
		
		return filteredSetOfClusters;
		
	}

	/**
	 * Can be refactored to use memory in a more efficient way
	 * @param cardinalityOfClusters
	 * @param setOfClusters2 
	 * @return 
	 */
	private ArrayList<Cluster> filterClustersByCardinality(int cardinalityOfClusters, ArrayList<Cluster> setOfClusters2) {
		ArrayList<Cluster> clustersWithDesiredCardinality = new ArrayList<Cluster>();
		for(Cluster c:setOfClusters2)
		{
			if(c.calculateCardinality()>=cardinalityOfClusters)
			{
				clustersWithDesiredCardinality.add(c);
			}
		}
		
		 return clustersWithDesiredCardinality;
	}

	public ArrayList<Cluster> clusterTrajectoriesWithDTW(ArrayList<Trajectory> simplifiedTrajectories, float eNeighborhoodParameter, int minLins, int cardinalityOfClusters)
	{
		setOfClusters = new ArrayList<Cluster>();
		
		int clusterId = 0;
		
		for(Trajectory t: simplifiedTrajectories)
		{
			if(!t.isClassified())
			{
				//This is the Traclus density based algorithm modified to 
				//cluster over whole trajectories, I assume this is similar to DBScan.
				
				ArrayList<Trajectory> neighborTrajectories = computeENeighborhoodOfTrajectory(t, eNeighborhoodParameter, simplifiedTrajectories);
				
				//Plus 1 cause my definition of neighborhood does not include core line itself
				//MinLins is in this case the minimun number of trajectories in the cluster
				if(neighborTrajectories.size()+1>= minLins)
				{
					ClusterTrajectories c = new ClusterTrajectories(clusterId, "Cluster " + clusterId);
					
					setOfClusters.add(c);
					
					//Before adding to cluster,
					//Should I set all segments of the neighborhood as classified??
					for(Trajectory t1:neighborTrajectories)
					{
						if(!t1.isClassified())
						{	
						t1.setClassified(true);
						t1.setNoise(false);
						//neighborSegments.set(i, s1);
						c.addTrajectory(t1);
						}
					}
					
					//Add neighbours to cluster
					//My definition of neighborhood does not include the core line itself
					//This line is extra since I am already iterating, might be little more efficient?
					//c.setSegments(neighborSegments);
					
					//Add this core line to this cluster
					t.setNoise(false);
					t.setClassified(true);
					c.addTrajectory(t);
					
					//Insert Neighboors into a Queue (check this part)
					ArrayList<Trajectory> queue = new ArrayList<Trajectory>();
					queue.addAll(neighborTrajectories);
					
					//Now Expand Cluster
					expandClusterTrajectories(queue, clusterId, eNeighborhoodParameter, minLins, simplifiedTrajectories);
					c.calculateCardinality();
					clusterId++;
				}else{
					t.setClassified(true);
					t.setNoise(true);
				}
			}
		}
		
		//For cluster cardinality
		//not so needed like in segments but I can do it,
		//For now comment it for tests
		ArrayList<Cluster> filteredSetOfClusters = filterClustersByCardinality(cardinalityOfClusters, setOfClusters);
		//ArrayList<Cluster> filteredSetOfClusters = setOfClusters;
		//Cluster using Density-based method
		//Need a notion of distance and a notion of time difference
		
		return filteredSetOfClusters;
	}

	private ArrayList<Segment> computeENeighborhoodOfSegment(Segment s, float eParameter, ArrayList<Segment> allSegments) {
		// TODO Auto-generated method stub'
		ArrayList<Segment> neighborSegments = new ArrayList<Segment>();
		
		for(Segment possibleNeighborSegment:allSegments)
		{
			if(Segment.calculateDistance(s, possibleNeighborSegment)<=eParameter)
			{
				if(s!=possibleNeighborSegment)
				{
				neighborSegments.add(possibleNeighborSegment);
				}
			}
		}
		return neighborSegments;
	}
	
	/**
	 * Could add parameter (int maxNumIterations) to make space for other iterations 
	 * @param simplifiedTrajectories
	 * @param k
	 * @return
	 */
	private  ArrayList<Cluster> clusterTrajectoriesKMeans(ArrayList<Trajectory> simplifiedTrajectories, int k)
	{
		ArrayList<Cluster> kmeansClusters = new ArrayList<Cluster>();
		
		//here call kmeans
		com.stromberglabs.cluster.Cluster[] kmeansCluster = Kmeans.execute(simplifiedTrajectories, k);
		
		for(com.stromberglabs.cluster.Cluster c:kmeansCluster)
		{
			ClusterTrajectories tempMyCluster = new ClusterTrajectories(c.getId(), "kmeans" + c.getId());
			List<Clusterable> items = c.getItems();
			for(Clusterable i: items)
			{
				Trajectory t = (Trajectory) i; 
				tempMyCluster.addTrajectory(t);
			}
			tempMyCluster.calculateCardinality();
			kmeansClusters.add(tempMyCluster);
		}
		
		return kmeansClusters;
	}

	//Step 2 - Clustering phase
	//To compute a density-connected set
	private void expandClusterSegments(ArrayList<Segment> queue, int clusterId,
			float eNeighborhoodParameter, int minLins, ArrayList<Segment> segmentsCompleteSet) {
		// TODO Auto-generated method stub
		while(!queue.isEmpty())
		{
			ArrayList<Segment> neighborhood = computeENeighborhoodOfSegment(queue.get(0), eNeighborhoodParameter, segmentsCompleteSet);
			
			if(Math.abs(neighborhood.size())>= minLins)
			{
				for(Segment s: neighborhood)
				{
					if(!s.isClassified() || s.isNoise())
					{
						//Cluster c = new Cluster(clusterId, "Cluster " + clusterId);
						ClusterSegments cTemp =  (ClusterSegments) setOfClusters.get(clusterId);
						s.setClassified(true);
						
						//This was before refactoring
						cTemp.addSegment(s);
						
						//Now it should be more generalized
						
						//setOfClusters.add(cTemp);
						setOfClusters.set(clusterId, cTemp);
					}
						//Check this part
					if(!s.isClassified())
					{
						//if element not in queue
						if(!queue.contains(s))
						{
						queue.add(s);
						}
					}
				}
			}
			queue.remove(0);
		}
	}
	
	//Step 2 - Clustering phase
		//To compute a density-connected set of trajectories
	//This should be refactored
		private void expandClusterTrajectories(ArrayList<Trajectory> queue, int clusterId,
				float eNeighborhoodParameter, int minLins, ArrayList<Trajectory> trajectoriesCompleteSet) {
			// TODO Auto-generated method stub
			while(!queue.isEmpty())
			{
				ArrayList<Trajectory> neighborhood = computeENeighborhoodOfTrajectory(queue.get(0), eNeighborhoodParameter, trajectoriesCompleteSet);
				
				if(Math.abs(neighborhood.size())>= minLins)
				{
					for(Trajectory t: neighborhood)
					{
						if(t.getTrajectoryId()==182)
						{
							//if element not in queue
							if(!queue.contains(t))
							{
							//queue.add(t);
							}
						}
						
						
						
						if(!t.isClassified() || t.isNoise())
						{
							//Cluster c = new Cluster(clusterId, "Cluster " + clusterId);
							ClusterTrajectories cTemp =  (ClusterTrajectories) setOfClusters.get(clusterId);
							t.setClassified(true);
							
							//This was before refactoring
							cTemp.addTrajectory(t);
							
							//Now it should be more generalized
							
							//setOfClusters.add(cTemp);
							setOfClusters.set(clusterId, cTemp);
						}
						
						if(t.getTrajectoryId()==182)
						{
							//if element not in queue
							if(!queue.contains(t))
							{
							//queue.add(t);
							}
						}
						
						
						//According to my analysis this code is useless
						/*
						//Check this part
						if(!t.isClassified())
						{
							//if element not in queue
							if(!queue.contains(t))
							{
							queue.add(t);
							}
						}
						*/
					}
				}
				queue.remove(0);
			}
		}


		/**
		 * This method should be taking a trajectory and calculating the neighborhood based in dtw
		 * @param trajectory
		 * @param eNeighborhoodParameter2 = the epsilon, in this case given by DTW
		 * @param trajectoriesCompleteSet = this is actually the set of reduced trajectorues
		 * @return
		 */
	private ArrayList<Trajectory> computeENeighborhoodOfTrajectory(Trajectory t, float eParameter, ArrayList<Trajectory> trajectoriesCompleteSet) 
	{

		ArrayList<Double> dtwValues = new ArrayList<Double>();
		ArrayList<Trajectory> neighborTrajectories = new ArrayList<Trajectory>();
		
		for(Trajectory possibleNeighborTrajectory:trajectoriesCompleteSet)
		{
			double tempDtwDistance = Trajectory.calculateDTWDistance(t, possibleNeighborTrajectory);
			dtwValues.add(tempDtwDistance);
			if(tempDtwDistance <=eParameter)
			{
				if(t!=possibleNeighborTrajectory)
				{
				neighborTrajectories.add(possibleNeighborTrajectory);
				}
			}
		}
		
		Double dtwAverage = calculateMeanDTW(dtwValues);
		//System.out.println(dtwAverage);
		t.setDtwAverage(dtwAverage);
		return neighborTrajectories;
	}

	private Double calculateMeanDTW(ArrayList<Double> dtwValues) {
			// TODO Auto-generated method stub
		double tempDTW = 0;
		for(double d: dtwValues)
		{
			tempDTW = tempDTW + d;
		}
		if(dtwValues!=null && dtwValues.size()>0)
		tempDTW = tempDTW/dtwValues.size();
			
		return tempDTW;
		}

	public double getEpsilonDouglasPeucker() {
		return epsilonDouglasPeucker;
	}

	public void setEpsilonDouglasPeucker(double epsilonDouglasPeucker) {
		this.epsilonDouglasPeucker = epsilonDouglasPeucker;
	}

	public int getFixedNumOfTrajectoryPartitionsDouglas() {
		return fixedNumOfTrajectoryPartitionsDouglas;
	}

	public void setFixedNumOfTrajectoryPartitionsDouglas(
			int fixedNumOfTrajectoryPartitionsDouglas) {
		this.fixedNumOfTrajectoryPartitionsDouglas = fixedNumOfTrajectoryPartitionsDouglas;
	}
	
	//Calculate Representative Trajectories.
	//This step is done in each individual cluster, so it is in the cluster class.
	
	

}
