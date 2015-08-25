package cluster.trajectory;
import java.awt.color.CMMException;
import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import javax.lang.model.type.IntersectionType;


public class testTraclus {
	
	public testTraclus() {
		// TODO Auto-generated constructor stub
	}


	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ClusteringMethod method = ClusteringMethod.DBH_APPROXIMATION;
		//starkeyElk93Experiment(method);
		CVRRExperiment(method);
	}

	/**
	 * @param method 
	 * 
	 */
	private static void starkeyElk93Experiment(ClusteringMethod method) {
		// TODO Auto-generated method stub
		
		ArrayList<Trajectory> testTrajectories;
		//For my dummy trajectories
		//ArrayList<Trajectory> testTrajectoriesDummy = testTraclus.generateTestTrajectories();
		//testTrajectories = testTrajectoriesDummy;
		
		/* 
		//Parameters for Dummy Trajectories
		float eNeighborhoodParameter = (float) 0.01;
		int minLins = 3;
		int cardinalityOfClusters = 3;
		float MLDPrecision = (float) 0.0001;
		*/
		
		//For Microsoft geolife trajectories
		//ArrayList<Trajectory> testTrajectoriesGeolife = testTraclus.generateTestTrajectoriesFromDataSetMicrosoftGeolife(2);
		//testTrajectories = testTrajectoriesGeolife;
		
		/* 
		//Parameters for Geolife
		float eNeighborhoodParameter = (float) 0.01;
		int minLins = 3;
		int cardinalityOfClusters = 3;
		float MLDPrecision = (float) 0.0001;
		*/
		
		//Make sure to initilize this for final version
		Traclus traclus = null;
		
		//Partition Parameters
		SegmentationMethod segmentationMethod = SegmentationMethod.douglasPeucker;
		//SegmentationMethod segmentationMethod = SegmentationMethod.traclus;
		
		//General Parameters, might be overwritten
		float eNeighborhoodParameter = (float) 27;
		int minLins = 8;
		int cardinalityOfClusters = 9;
		float MLDPrecision = (float) 1;
		
		//Generate trajectories from DATA
		//Should have an enum and switch for different cases (generated data, starkey, microsoft data, hurricane data *not available*, soccer Data *not available.
		//For Starkley Animal trajectories to compara with paper
		ArrayList<Trajectory> testTrajectoriesStarkey = InputManagement.generateTestTrajectoriesFromDataSetAnimalsStarkey("E", 1993,MLDPrecision);	
		testTrajectories = testTrajectoriesStarkey;
		
		if(segmentationMethod == SegmentationMethod.traclus)
		{
		//Override Parameters for Starkey using traclus
		eNeighborhoodParameter = (float) 27;
		minLins = 8;
		cardinalityOfClusters = 9;
		
		//For Original Traclus Trajectory Partition 		
		traclus = new Traclus(testTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters, segmentationMethod);
		//End of trajectory Partition via Original Traclus Partition Phase
		}
		
		if(segmentationMethod == SegmentationMethod.douglasPeucker)
		{
		//For Trajectory Partition using Douglas-Peucker
		double epsilonDouglasPeucker = 0.001;
		int fixNumberPartitionSegment = 32;
		
		//overwriting test parameters
		//eNeighborhoodParameter = (float) 27;
		
		//Parameter for DTW distance
		eNeighborhoodParameter = (float) 520000;
		minLins = 1;
		cardinalityOfClusters = 1;
		//end of douglas peucker ovewriten parameters for test
		traclus = new Traclus(testTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters, epsilonDouglasPeucker, fixNumberPartitionSegment, segmentationMethod);
		}
		//End of Douglas Peucker
		
		//For previous Traclus implementation
		//ArrayList<Cluster> testClusters = traclus.executeTraclus();
		
		//Now working over Whole trajectory
		//ArrayList<Cluster> testClusters = traclus.executeDensityBasedClusterOverTrajectories();
		
		//For Kmeans for Whole trajectories
		ArrayList<Cluster> testClusters = traclus.executeKMeansClusterOverTrajectories(13);
		
		//Here print Real cluster data, we do not have those labels yet here

		printSetOfCluster(minLins, testClusters, false);
	}
	
	private static void CVRRExperiment(ClusteringMethod method) {

		//Make sure to initilize this for final version
		Traclus traclus = null;
		
		//Partition Parameters
		//Default DP unless stated otherwise
		SegmentationMethod segmentationMethod = SegmentationMethod.douglasPeucker;
		//SegmentationMethod segmentationMethod = SegmentationMethod.traclus;
		
		//General Parameters, might be overwritten
		float eNeighborhoodParameter = (float) 27;
		int minLins = 8;
		int cardinalityOfClusters = 9;
		float MLDPrecision = (float) 1;
		
		
		//For CVRR trajectory data
		String CVRRdatasetName = "CSV Trajectories labomni";
		ArrayList<Trajectory> testTrajectoriesCVRR = InputManagement.generateTestTrajectoriesFromDataSetCVRR(CVRRdatasetName);
		
		ArrayList<Cluster> testClusters = new ArrayList<Cluster>();
		
		if(method == ClusteringMethod.TRACLUS)
		{
		segmentationMethod = SegmentationMethod.traclus;
		//Override Parameters for Starkey using traclus
		eNeighborhoodParameter = (float) 27;
		minLins = 8;
		cardinalityOfClusters = 9;
		
		//For Original Traclus Trajectory Partition 		
		traclus = new Traclus(testTrajectoriesCVRR, eNeighborhoodParameter, minLins, cardinalityOfClusters, segmentationMethod);
		//End of trajectory Partition via Original Traclus Partition Phase
		
		/*
		 * 		//Parameter for DTW distance
		eNeighborhoodParameter = (float) 520000;
		minLins = 1;
		cardinalityOfClusters = 1;
		//end of douglas peucker ovewriten parameters for test
		traclus = new Traclus(testTrajectoriesCVRR, eNeighborhoodParameter, minLins, cardinalityOfClusters, epsilonDouglasPeucker, fixNumberPartitionSegment, segmentationMethod);
		*/
		
		//For previous Traclus implementation
		testClusters = traclus.executeTraclus();
		}
		
		if(method == ClusteringMethod.DBSCAN)
		{
		segmentationMethod = SegmentationMethod.douglasPeucker;
		//For Trajectory Partition using Douglas-Peucker
		double epsilonDouglasPeucker = 0.001;
		int fixNumberPartitionSegment = 8;
		
		//overwriting test parameters
		//eNeighborhoodParameter = (float) 27;
		
		//Parameter for DTW distance
		eNeighborhoodParameter = (float) 182; //worked for 8 segments minLins 1
		//eNeighborhoodParameter = (float) 86; // produced 15 clusters for 8 segments minLins 3 but some trajectories classified as noise, so errors on comparison output
		//eNeighborhoodParameter = (float) 340;	//for complete trajectories (expensive) and minLins 3
		eNeighborhoodParameter = (float) 3550;	//for complete trajectories (expensive) and minLins 1
		minLins = 1;
		cardinalityOfClusters = 1;
		//end of douglas peucker ovewriten parameters for test
		traclus = new Traclus(testTrajectoriesCVRR, eNeighborhoodParameter, minLins, cardinalityOfClusters, epsilonDouglasPeucker, fixNumberPartitionSegment, segmentationMethod);
		
		//Now working over Whole trajectory
		testClusters = traclus.executeDensityBasedClusterOverTrajectories(false);
		}
		//End of Douglas Peucker
		

		if(method == ClusteringMethod.KMEANS)
		{
		segmentationMethod = SegmentationMethod.douglasPeucker;
		//For Trajectory Partition using Douglas-Peucker
		double epsilonDouglasPeucker = 0.001;
		int fixNumberPartitionSegment = 8;
		
		//overwriting test parameters
		//eNeighborhoodParameter = (float) 27;
		traclus = new Traclus(testTrajectoriesCVRR, eNeighborhoodParameter, minLins, cardinalityOfClusters, epsilonDouglasPeucker, fixNumberPartitionSegment, segmentationMethod);
		

		//For Kmeans for Whole trajectories
		testClusters = traclus.executeKMeansClusterOverTrajectories(15);
		}

		if(method == ClusteringMethod.DBH_APPROXIMATION)
		{
		segmentationMethod = SegmentationMethod.douglasPeucker;
		//For Trajectory Partition using Douglas-Peucker
		double epsilonDouglasPeucker = 0.001;
		int fixNumberPartitionSegment = 128;  //normal value = 8
		boolean simplifyTrajectories = true; //Normally set to true
		
		//Parameters only for DBH APPROXIMATION
		int minNumElems = 3;
		float t1 = 0;
		float t2 = 1500;
		int l = 15;
		int numBits = 12; //before was 9, but 10 bits produce crazy good results
		float mergeRatio = 1/2;
		boolean merge = false;
		
		traclus = new Traclus(testTrajectoriesCVRR, eNeighborhoodParameter, minLins, cardinalityOfClusters, epsilonDouglasPeucker, fixNumberPartitionSegment, segmentationMethod);
		
		
		//I need to establish better parameters
		testClusters = traclus.executeDBHApproximationOfClusterOverTrajectories(simplifyTrajectories, l, numBits, t1, t2, minNumElems, merge, mergeRatio);
		}
		
		//PrintReal Cluster Data
		ArrayList<Cluster> realClusters = new ArrayList<Cluster>();
		for(Trajectory t:testTrajectoriesCVRR)
		{
			int clusterID = t.getClusterIdPreLabel();
			
			if(realClusters!=null && realClusters.size()<clusterID)
			{
				for(int j = realClusters.size(); j<=clusterID+1; j++)
				{
					Cluster c = new Cluster(j, "Cluster"+j);
					realClusters.add(c);
				}
			}
			
			
			if(realClusters!=null && realClusters.get(clusterID) != null)
			{
				realClusters.get(clusterID).addElement(t);
			}else{
				Cluster c = new Cluster(clusterID, "Cluster"+clusterID);
				c.addElement(t);
				
				realClusters.add(c);
			}
		}

		System.out.println("Real Clusters");
		printSetOfCluster(minLins, realClusters, false);
		System.out.println("Calculated Clusters: " + testClusters.size() + " Method: " + method );
		printSetOfCluster(minLins, testClusters, false);
		
		//To calculate True negatives we need a HashSet of all trajectories in the initial
		//Dataset
		HashSet<Integer> allTrajectories = CommonFunctions.getHashSetAllTrajectories(testTrajectoriesCVRR);
		
		compareClusters(realClusters, testClusters, allTrajectories);
	}

	

	/**
	 * @param minLins
	 * @param testClusters
	 */
	private static void printSetOfCluster(int minLins,
			ArrayList<Cluster> testClusters, boolean isTraclus) {
		//To print Clusters - Refactor this
		if(testClusters.isEmpty())
		{
			System.out.println("No clusters meet the parameters criteria");
		}
		else{
			for(Cluster c: testClusters)
			{
				if(isTraclus)
				c.calculateRepresentativeTrajectory(minLins, 0.00005);
				//System.out.println("Cluster: " + c.getClusterID());
				//System.out.println("Representative trajectory: " + c.getRepresentativeTrajectory().toString());
				System.out.println("Cluster: " + c.toString());
			}
		}
	}


	private static void compareClusters(ArrayList<Cluster> baselineSet, ArrayList<Cluster> testSet, HashSet<Integer> allTrajectories)
	{
		//For Whole Method Statistics
		float methodPurity = 0;
		float methodCoverage = 0;
		float methodAccuracy = 0;
		float methodFMeasure = 0;
		
		int numClustersNotProducedByMethod = 0;
		
		for(Cluster cb: baselineSet)
		{
			int equivalentIndex=-1;
			float commonElements=0;
			float falsePositives=0;
			float falseNegatives=0;
			float trueNegatives=0;
			//true negatives(tni), i.e., the number of trajectories that do not belong to ci and they were
			//correctly assigned to a cluster different from ci
			
			cb.calculateCardinality();
			
			for(Cluster ct: testSet)
			{
				ct.calculateCardinality();
				cb.calculateCardinality();
				HashSet<Integer> common = cb.getParentTrajectories();
				common.retainAll(ct.getParentTrajectories());
				
				if(commonElements<common.size())
				{
					commonElements = common.size();
					equivalentIndex = ct.getClusterID();
					falsePositives = ct.cardinality - commonElements;
					falseNegatives = cb.cardinality - commonElements;
					
					//For trueNegatives
					HashSet<Integer> notInAnySet = new HashSet<Integer>();
					notInAnySet.addAll(allTrajectories);
					notInAnySet.removeAll(cb.getParentTrajectories());
					notInAnySet.removeAll(ct.getParentTrajectories());

					trueNegatives = notInAnySet.size();
					
				}
			}
			
			System.out.println("\n");
			
			if(equivalentIndex>-1)
			{
			System.out.println("Real Cluster: " + cb.getClusterID() 
					+ " Equivalent test Cluster: " + equivalentIndex
					+ " Common Elements (TP): " + commonElements
					+ " False Positives (FP): " + falsePositives
					+ " False Negatives (FN): " + falseNegatives
					+ " True Negatives  (TN): " + trueNegatives);
			
			float purity = commonElements/(commonElements + falsePositives);
			float coverage = commonElements/(commonElements + falseNegatives);
			float accuracy = (commonElements+trueNegatives)/(commonElements + trueNegatives + falsePositives + falseNegatives);
			float fMeasure = 10*(purity * coverage)/( 9* purity + coverage);
			System.out.println("Per Cluster Stats for Cluster " +  cb.getClusterID());
			System.out.println("Purity: 	" +  purity);
			System.out.println("Coverage: 	" +  coverage);
			System.out.println("Accuracy: 	" +  accuracy);
			System.out.println("F-Measure:	" +  fMeasure);
			
			methodPurity += purity;
			methodCoverage += coverage;
			methodAccuracy += accuracy;
			methodFMeasure += fMeasure;
			}else{
				System.out.println("Real Cluster: " + cb.getClusterID() 
						+ " Equivalent test Cluster: NONE");
				System.out.println("Not produced by the clustering method, no intersects!!!");
				numClustersNotProducedByMethod++;
			}
		}
		
		int sizeNormalizer = baselineSet.size() - numClustersNotProducedByMethod;
		
		methodPurity = methodPurity/sizeNormalizer;
		methodCoverage = methodCoverage/sizeNormalizer;
		methodAccuracy = methodAccuracy/sizeNormalizer;
		methodFMeasure = methodFMeasure/sizeNormalizer;
		
		System.out.println("\n");
		System.out.println("Per Method Statistics: ");
		System.out.println("Purity: 	" +  methodPurity);
		System.out.println("Coverage: 	" +  methodCoverage);
		System.out.println("Accuracy: 	" +  methodAccuracy);
		System.out.println("F-Measure: 	" +  methodFMeasure);
		
	}
}