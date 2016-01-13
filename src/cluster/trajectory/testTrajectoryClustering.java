package cluster.trajectory;
import graphics.TrajectoryPlotter;

import java.io.*;
import java.lang.reflect.Array;
import java.security.AlgorithmConstraints;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.jfree.date.EasterSundayRule;

import dataset.DatasetConfig;
import dataset.TrajectoryDatasets;
import extras.AuxiliaryFunctions;


public class testTrajectoryClustering {
	
	public static ArrayList<Double> silhouetteValues = new ArrayList<Double>();
	public static ArrayList<Float> NMIValues = new ArrayList<Float>();
	public static ArrayList<Double> timesDouglasPeucker = new ArrayList<Double>();
	public static ArrayList<Double> timesClustering = new ArrayList<Double>();
	
	static ArrayList<Integer> representedOriginalTraj = new ArrayList<Integer>();
	
	public testTrajectoryClustering() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Main method to test all the experiments.
	 * Basically this class glues everything together and allows the easy execution 
	 * of the different trajectory clustering methods and their respective configuration,
	 * the selection of different datasets and the option to plot resulting clusters.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			ClusteringMethod method = ClusteringMethod.KMEDOIDS_DTW;
			//ClusteringMethod method = ClusteringMethod.KMEANS_EUCLIDEAN;
			//starkeyElk93Experiment(method);
			boolean plotTrajectories = false;
			boolean simplifyTrajectories = true;
			boolean printDetailedClusters = true;
			boolean printOutputZayFile = false;
			boolean printOutputZayToScreen = false;
			boolean printConfusionMatrix = false;
			boolean printIntraClusterDistanceMatrix = false;
			boolean plotCompleteTrajectoriesEquivalentForSimplifiedClusters = false;
			boolean saveFeatureVectorsToFile = true;
			boolean calculateSilhouetteCoefficient = false;
			boolean normalize = false;
			
			SegmentationMethod simplificationMethod = SegmentationMethod.douglasPeucker;
			TrajectoryDatasets trajectoryDataset = TrajectoryDatasets.GEOLIFE;
			int numberOfPartitionsPerTrajectory = 25; //normal value = 8 //9 for tests with zay
			
			//For big data Experiment
			boolean veryBigData = false;
			int numTrajectoryBigDataset = 500;
			
			boolean runMultipleExperiments = true;
			int numberOfExperiments = 5;
			
			if(runMultipleExperiments)
			{
				
				for(int i = 0; i<numberOfExperiments; i++)
				{	
					System.out.println("");
					System.out.println("Start of iteration: " + i);
					CVRRExperiment(method, trajectoryDataset, plotTrajectories, plotCompleteTrajectoriesEquivalentForSimplifiedClusters, 
							simplifyTrajectories, simplificationMethod,numberOfPartitionsPerTrajectory, veryBigData, 
							numTrajectoryBigDataset, printOutputZayFile, printOutputZayToScreen, printConfusionMatrix, 
							printDetailedClusters, printIntraClusterDistanceMatrix, saveFeatureVectorsToFile, calculateSilhouetteCoefficient,
							normalize);
					System.out.println("End of iteration: " + i);
					System.out.println("");
					System.out.println("");
				}
				
				System.out.println("NMI Scores: " + testTrajectoryClustering.NMIValues);
				if(calculateSilhouetteCoefficient)
				{
					System.out.println("Silhouette Scores: " + testTrajectoryClustering.silhouetteValues);
				}else{
					System.out.println("Silhouette calculation deactivativated");
				}
				if(simplifyTrajectories)
				{
					System.out.println("Douglas Peucker Times: " + testTrajectoryClustering.timesDouglasPeucker);
				}else{
					System.out.println("Trajectory Simplification deactivativated");
				}
				System.out.println("Clustering Times: " + testTrajectoryClustering.timesClustering);
			}else{
				CVRRExperiment(method, trajectoryDataset, plotTrajectories, plotCompleteTrajectoriesEquivalentForSimplifiedClusters, 
							simplifyTrajectories, simplificationMethod,numberOfPartitionsPerTrajectory, veryBigData, 
							numTrajectoryBigDataset, printOutputZayFile, printOutputZayToScreen, printConfusionMatrix, 
							printDetailedClusters, printIntraClusterDistanceMatrix, saveFeatureVectorsToFile, calculateSilhouetteCoefficient,
							normalize);
			}
			//to evaluate the numbers of buckets produced by different numbers of hashing functions
			/*
			boolean plotHashFuntionsToNumBucketsGraph = true;
			int maxIterations = 20;
			int startingNumHashFunctions = 1;
			
			int totalHashesReached;
			int maxTotalOfHashesToReach = 6;
			do{
			totalHashesReached = evaluateProduceNumOfClusters(method, trajectoryDataset, plotHashFuntionsToNumBucketsGraph, simplifyTrajectories, simplificationMethod, 
					numberOfPartitionsPerTrajectory, maxIterations, startingNumHashFunctions);
			}while(totalHashesReached<maxTotalOfHashesToReach);
			*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param method 
	 * 
	 */
	private static void starkeyElk93Experiment(ClusteringMethod method, boolean calculateSilhouetteCoefficient) {
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
		ArrayList<Cluster> testClusters = traclus.executeKMeansDTW(13, minLins, calculateSilhouetteCoefficient);
		
		//Here print Real cluster data, we do not have those labels yet here

		printSetOfCluster(minLins, testClusters, false);
	}
	
	private static void CVRRExperiment(ClusteringMethod method, TrajectoryDatasets trajectoryDataset,
			boolean plotTrajectories, boolean plotCompleteTrajectoriesEquivalentForSimplifiedClusters, boolean simplifyTrajectories, 
			SegmentationMethod simplificationMethod, int fixNumberPartitionSegment, boolean veryBigData, int numTrajectoryBigDataset, 
			boolean printOutputZayToFile, boolean printOutputZayToScreen, boolean printConfusionMatrix, boolean printDetailedClusters, 
			boolean printIntraClusterDistanceMatrix, boolean saveFeatureVectorsToFile, boolean calculateSilhouetteCoefficient, 
			boolean normalize) throws Exception {

		//Make method to get dataset Values (number of clusters, if labeled, etc)
		DatasetConfig datasetConfig = new DatasetConfig(trajectoryDataset);
		
		//Make sure to initilize this for final version
		Traclus traclus = null;
		
		//Partition Parameters
		//Default DP unless stated otherwise
		SegmentationMethod segmentationMethod = (simplificationMethod==null? SegmentationMethod.douglasPeucker : simplificationMethod);
		
		//General Parameters, might be overwritten
		float eNeighborhoodParameter = (float) 27;
		int minLins = 8;
		int cardinalityOfClusters = 9;
		float MLDPrecision = (float) 1;
		boolean strictSimplification = true;
		
		
		String dataset = getDatasetVariable(trajectoryDataset);
		
		
		ArrayList<Cluster> testClusters = new ArrayList<Cluster>();
		
		ArrayList<Trajectory> originalCompleteTrajectories = getTrajectories(false, -1, dataset, trajectoryDataset, null);
		
		//Before clustering, lets simplify trajectories if we have to.
		//This have to be done here rather than in the clustering class to have a fair comparison.
		ArrayList<Trajectory> workingTrajectories = new ArrayList<Trajectory>();
		
		switch (trajectoryDataset) {
		case AUSSIGN:
			workingTrajectories = getTrajectories(simplifyTrajectories, fixNumberPartitionSegment, dataset, trajectoryDataset, originalCompleteTrajectories);
			break;
		case CROSS:
		case LABOMNI:
			workingTrajectories = getTrajectories(simplifyTrajectories, fixNumberPartitionSegment, dataset, trajectoryDataset, null);
			break;
		default:
			break;
		}
		
		//Get the original trajectories to work with complete trajectory plots (requested by Zay).
		if(plotCompleteTrajectoriesEquivalentForSimplifiedClusters && simplifyTrajectories){
			switch (trajectoryDataset) {
			case AUSSIGN:
				originalCompleteTrajectories = filterAusSignDataset(workingTrajectories, originalCompleteTrajectories);
				break;
			case CROSS:
			case LABOMNI:
				originalCompleteTrajectories = adjustCompleteTrajectoriesToSimplifiedIndex(workingTrajectories, originalCompleteTrajectories, representedOriginalTraj);
			default:
				break;
			}
			
		}
		
		if(veryBigData)
		{
			workingTrajectories = bigDataset(numTrajectoryBigDataset, workingTrajectories); 
		}
			
		if(method == ClusteringMethod.TRACLUS)
		{
			//Override Parameters for Starkey using traclus
			segmentationMethod = SegmentationMethod.traclus;
		
			eNeighborhoodParameter = (float) 27;
			minLins = 8;
			cardinalityOfClusters = 9;
		
			//For Original Traclus Trajectory Partition 		
			traclus = new Traclus(workingTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters, segmentationMethod);
			//End of trajectory Partition via Original Traclus Partition Phase
			
			/*
			 * 		//Parameter for DTW distance
			eNeighborhoodParameter = (float) 520000;
			minLins = 1;
			cardinalityOfClusters = 1;
			//end of douglas peucker ovewriten parameters for test
			traclus = new Traclus(workingTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters, epsilonDouglasPeucker, fixNumberPartitionSegment, segmentationMethod);
			*/
			
			//For previous Traclus implementation
			testClusters = traclus.executeTraclus();
		}
		
		if(method == ClusteringMethod.DBSCAN_DTW)
		{
		segmentationMethod = SegmentationMethod.douglasPeucker;
		//For Trajectory Partition using Douglas-Peucker
		double epsilonDouglasPeucker = 0.001;
		fixNumberPartitionSegment = 8;
		
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
		traclus = new Traclus(workingTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters, epsilonDouglasPeucker, fixNumberPartitionSegment, segmentationMethod);
		
		//Now working over Whole trajectory
		testClusters = traclus.executeDensityBasedClusterOverTrajectories();
		}
		//End of Douglas Peucker
		

		if(method == ClusteringMethod.KMEANS_EUCLIDEAN)
		{
			//For Kmeans for Whole trajectories
			int minNumElems = 8;
			int k = 15; //Number of Clusters to generate with Kmeans over the feature vector of the trajectories
				
			traclus = new Traclus(workingTrajectories);
	
			testClusters = traclus.executeKMeansDTW(k, minNumElems, calculateSilhouetteCoefficient);
		}

		if(method == ClusteringMethod.DBH_APPROXIMATION_DTW)
		{
			//Parameters only for DBH APPROXIMATION
			int minNumElems = 1;
			//float t1 = 0; //Find this parameter
			//float t2 = 1500; //Should be infinity
			int l = 1;
			int numBits = 1;//7; //5; //before was 9, but 10 bits produce crazy good results //Final value for old implementation settle to 12
			float mergeRatio = 1/2;
			boolean merge = false;
			
			traclus = new Traclus(workingTrajectories);
			
			//I need to establish better parameters
			testClusters = traclus.executeDBHApproximationOfClusterOverTrajectories(l, numBits, minNumElems, merge, mergeRatio);
		}
		
		if(method == ClusteringMethod.DBH_DTW_FEATURE_VECTOR_BINARY)
		{
			//TODO Implement the filtering for minNumElems
			int minNumElems = 1; //To Discriminate all those clusters that have less elements than this. Currently unused
			int numBits = 1; //Number of KBit functions to produce, that is the length of signature, thus lenght of feature vector
			int k = 2; //Number of Clusters to generate with Kmeans over the feature vector of the trajectories
			boolean isBinaryFeatureVector = true; //Cause we want a FV of 0's and 1's
			
			traclus = new Traclus(workingTrajectories);
			//I need to establish better parameters
			testClusters = traclus.executeDBHOverFeatureVectorTrajectories(numBits, minNumElems, k, isBinaryFeatureVector, saveFeatureVectorsToFile, calculateSilhouetteCoefficient, normalize);
		}
		
		if(method == ClusteringMethod.DBH_DTW_FEATURE_VECTOR_REAL_NUMBERS)
		{
			int minNumElems = 1; //To Discriminate all those clusters that have less elements than this. Currently unused
			int numBits = 10; //Number of KBit functions to produce, that is the length of signature, thus lenght of feature vector
			int k = 98; //Number of Clusters to generate with Kmeans over the feature vector of the trajectories
			boolean isBinaryFeatureVector = false; //Cause we want a FV of real numbers 
			
			traclus = new Traclus(workingTrajectories);
			testClusters = traclus.executeDBHOverFeatureVectorTrajectories(numBits, minNumElems, k, isBinaryFeatureVector, saveFeatureVectorsToFile, calculateSilhouetteCoefficient, normalize);
		}
		
		//KMedoids
		if(method == ClusteringMethod.KMEDOIDS_DTW)
		{
			int minNumElems = 1; //To Discriminate all those clusters that have less elements than this. Currently unused
			int k = 98; //Number of Clusters to generate with Kmeans over the feature vector of the trajectories

			traclus = new Traclus(workingTrajectories);
			testClusters = traclus.executeKMedoidsDTW(k, minNumElems);
		}
		
		//For K-MeansDTW
		if(method == ClusteringMethod.KMEANS_DTW)
		{
		//Call KMedoids here
			
			//For Trajectory Partition using Douglas-Peucker
			segmentationMethod = SegmentationMethod.douglasPeucker;
			
			//Parameters for Partition
			double epsilonDouglasPeucker = 0.001;
			
			//Parameters for K-Medoids
			int k = 15;
			int minNumElems = 1;
			
			traclus = new Traclus(workingTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters, epsilonDouglasPeucker, fixNumberPartitionSegment, segmentationMethod);
			

			
			//For Kmeans for Whole trajectories
			testClusters = traclus.executeKmeansDTW(k, minNumElems);
			
		}
		
		//For LSH Using Euclidean distance
		if(method == ClusteringMethod.LSH_EUCLIDEAN)
		{
			//Parameters for Partition
			double epsilonDouglasPeucker = 0.001;
			
			//Parameters for LSH
			int minNumElems = 1;
			int numHashingFunctions = 10;
			int windowSize = 1000;
			
			traclus = new Traclus(workingTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters, epsilonDouglasPeucker, fixNumberPartitionSegment, segmentationMethod);
			
			//For LSH EUCLIDEAN
			testClusters = traclus.executeLSHEuclidean(numHashingFunctions, windowSize, minNumElems);
			
		}
		
		//For LSH Using Euclidean distance
		if(method == ClusteringMethod.LSH_EUCLIDEAN_SLIDING_WIN)
		{
			//Parameters for LSH
			int minNumElems = 1;
			int numHashingFunctions = 5;
			int lshFunctionWindowSize = 100;
			int	slidingWindowSize = 4;
			int k = 15; //At the end we do K-Means over the feature vectors
			
			traclus = new Traclus(workingTrajectories);
			//For LSH EUCLIDEAN
			testClusters = traclus.executeLSHEuclideanSlidingWindow(numHashingFunctions, lshFunctionWindowSize, minNumElems, slidingWindowSize, k, calculateSilhouetteCoefficient);
			
		}
		
		//For KMeans using LCSS
		if(method == ClusteringMethod.KMEANS_LCSS)
		{
			//Parameters for Kmeans LCSS
			int minNumElems = 1; //To Discriminate all those clusters that have less elements than this. Currently unused
			int numBits = 10; //Number of KBit functions to produce, that is the length of signature, thus lenght of feature vector
			int k = 19; //Number of Clusters to generate with Kmeans over the feature vector of the trajectories
			
			traclus = new Traclus(workingTrajectories);
			testClusters = traclus.executeKmeansLCSS(numBits, minNumElems, k);	
		}
		
		//TODO Finish and test LCSS
		//For KMeans using LCSS
		if(method == ClusteringMethod.KMEDOIDS_LCSS)
		{
			//Parameters for Kmedoids LCSS
			int minNumElems = 1; //To Discriminate all those clusters that have less elements than this. Currently unused
			int k = 19; //Number of Clusters to generate with Kmeans over the feature vector of the trajectories
			
			traclus = new Traclus(workingTrajectories);
			testClusters = traclus.executeKmedoidsLCSS(minNumElems, k);	
		}
		
		
		
		ArrayList<Cluster> realClusters = getTrueClustersFromTrajectories(workingTrajectories);

		if(printDetailedClusters)
		{
			System.out.println("Real Clusters");
			printSetOfCluster(minLins, realClusters, false);
			System.out.println("Calculated Clusters: " + testClusters.size() + " Method: " + method );
			printSetOfCluster(minLins, testClusters, false);
		}
		

		//To calculate True negatives we need a HashSet of all trajectories in the initial
		//Dataset
		HashSet<Integer> allConsideredTrajectories = CommonFunctions.getHashSetAllTrajectories(workingTrajectories);

		compareClusters(realClusters, testClusters, allConsideredTrajectories, printConfusionMatrix);
		
		if(calculateSilhouetteCoefficient)
		{
			try {
				/*if(method==ClusteringMethod.DBH_APPROXIMATION_DTW || method==ClusteringMethod.DBH_DTW_FEATURE_VECTOR_BINARY 
						|| method == ClusteringMethod.DBH_DTW_FEATURE_VECTOR_REAL_NUMBERS || method==ClusteringMethod.DBSCAN_DTW 
						|| method == ClusteringMethod.KMEANS_DTW || method == ClusteringMethod.KMEDOIDS_DTW)
				{*/
					double silhouetteCoefficientGeneratedClusters = ClusterQualityMeterer.silhouetteCoefficientDTW(testClusters);
					System.out.println("Internal Silhouette Coefficient Generated Set of Clusters: " + silhouetteCoefficientGeneratedClusters);
					testTrajectoryClustering.silhouetteValues.add(silhouetteCoefficientGeneratedClusters);
					double silhouetteCoefficientRealClusters = ClusterQualityMeterer.silhouetteCoefficientDTW(realClusters);
					System.out.println("Internal Silhouette Coefficient Real Set of Clusters: " + silhouetteCoefficientRealClusters);
				/*}else{
					System.out.println("Silhoutte Coefficient only defined in Code for methods that use DTW distance.");
				}*/
			} catch (Exception e1) {
				System.out.println("Hey Error: " + e1.getMessage());
				e1.printStackTrace();
			}
		}
			
		//Print Output for Zay to run other cluster statistics in Phyton
		if(printOutputZayToFile)
		{
			printTrajectoryLabels(testClusters, printOutputZayToScreen);
		}
		
		//to Plot clusters
		if(plotTrajectories)
		{
			TrajectoryPlotter.drawAllClusters(testClusters, false, false);
			TrajectoryPlotter.drawAllClustersInSameGraph(testClusters, false, "");
			//TrajectoryPlotter.drawAllClusters(realClusters, false, false);
			TrajectoryPlotter.drawAllClustersInSameGraph(realClusters, true, "Real Clusters");
			if(plotCompleteTrajectoriesEquivalentForSimplifiedClusters && simplifyTrajectories)
			{
				try {
					plotOriginalTrajectories(testClusters, originalCompleteTrajectories);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.err.println(e);
					e.printStackTrace();
				}
			}
		}
	
		if(printIntraClusterDistanceMatrix)	printIntraClusterDistanceMatrix(method, testClusters);
		//System.out.println("Inverted Output");
		//compareClusters(testClusters, realClusters, allTrajectories);
	}

	private static ArrayList<Trajectory> filterAusSignDataset(ArrayList<Trajectory> workingTrajectories,
			ArrayList<Trajectory> originalCompleteTrajectories) {
		// TODO Auto-generated method stub
		ArrayList<Trajectory> originalTrajectoriesFiltered = new ArrayList<Trajectory>();
		ArrayList<Integer> trajectoryIdsToKeep = new ArrayList<Integer>();
		for(Trajectory t: workingTrajectories)
		{
			trajectoryIdsToKeep.add(t.getTrajectoryId());
		}
		originalCompleteTrajectories = Trajectory.filterTrajectoryListById(originalCompleteTrajectories, trajectoryIdsToKeep);
		return originalTrajectoriesFiltered;
	}

	/**
	 * This is to obtain the true clusters from the trajectory data with the true label.
	 * @param workingTrajectories
	 * @return Set of True Clusters
	 */
	public static ArrayList<Cluster> getTrueClustersFromTrajectories(
			ArrayList<Trajectory> workingTrajectories) {
		//PrintReal Cluster Data
		ArrayList<Cluster> realClusters = new ArrayList<Cluster>();
		for(Trajectory t:workingTrajectories)
		{
			int clusterID = t.getClusterIdPreLabel();
			if(realClusters!=null && (realClusters.size()<=clusterID )) //|| realClusters.size()==0 
			{
				for(int j = realClusters.size(); j<=clusterID; j++)
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
		
		//Just to make sure there are no clusters with empty elements
		realClusters = Cluster.keepClustersWithMinElements(realClusters, 1);
		return realClusters;
	}

	/**
	 * This method prints a matrix with a the pairwise distance for the elements of each cluster. Intracluster distances.
	 * @param method : The clustering method which generated the set of clusters. It is important because it 
	 * defines the notion of distance, so the it determines the distances to calculate and print for each pair of elements within a cluster.
	 * @param testClusters : The set of clusters to evaluate and print the matrix.
	 */
	private static void printIntraClusterDistanceMatrix(
			ClusteringMethod method, ArrayList<Cluster> testClusters) {
		if((method.equals(ClusteringMethod.DBH_APPROXIMATION_DTW) || method.equals(ClusteringMethod.KMEANS_DTW) || method.equals(ClusteringMethod.KMEDOIDS_DTW) || method.equals(ClusteringMethod.DBH_DTW_FEATURE_VECTOR_BINARY)))
		{
			try {
				ClusterQualityMeterer.intraClusterDistanceDTWForAllClustersInSet(testClusters, method);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println("No Intracluster distance matrix defined yet for the metric used in method: " + method.toString());
		}
		
		//TODO Create and print Matrixes for other distance measures.
	}

	/**
	 * This method matches the index of the original trajectories with the index of the simplified trajectories.
	 * It is used to plot complete trajectories from sets of clusters composed of simplified trajectories. 
	 * It is for evaluation purposes.
	 * @param workingTrajectories
	 * @param originalCompleteTrajectories
	 * @param representedOriginalTraj2
	 * @return List of Original trajectories with indexes that match those on the set of Simplified trajectories.
	 */
	private static ArrayList<Trajectory> adjustCompleteTrajectoriesToSimplifiedIndex(
			ArrayList<Trajectory> workingTrajectories,
			ArrayList<Trajectory> originalCompleteTrajectories,
			ArrayList<Integer> representedOriginalTraj2) {
		
		ArrayList<Trajectory> adjustedOriginalTrajectorySet = new ArrayList<Trajectory>();
		Collections.sort(representedOriginalTraj2);
		for(Integer i:representedOriginalTraj2)
		{
			adjustedOriginalTrajectorySet.add(originalCompleteTrajectories.get(i));
		}
		
		for(int i=0; i<workingTrajectories.size(); i++)
		{
			adjustedOriginalTrajectorySet.get(i).setTrajectoryId(i);
		}
		
		return adjustedOriginalTrajectorySet;
	}



	/**
	 * Print for each cluster, the truth and predicted cluster label
	 * @param testClusters
	 */
	private static void printTrajectoryLabels(ArrayList<Cluster> testClusters, boolean printZayOutputToScreen) {
		System.out.println();
		String zayOutput = "****OUTPUT FOR ZAY, PYTHON CLUSTER QUALITY METRICS****\n";
		zayOutput = zayOutput + "Trajectory, True Cluster, Predicted Cluster\n";
		for(Cluster c: testClusters)
		{
			for(Trajectory t: c.getElementsAsTrajectoryObjects())
			{
				zayOutput = zayOutput + t.getTrajectoryId() + ", " + t.getClusterIdPreLabel() + ", " + c.clusterID + "\n";
			}
		}
		if(printZayOutputToScreen) System.out.println(zayOutput);
		
		String path;
		try {
			path = extras.GetPropertyValues.getPropValues("Zay_Output_Exported");
			AuxiliaryFunctions.printStringToFile(zayOutput, "OutputForZay.txt", path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	/**
	 * This method prints a set of clusters
	 * @param minLins
	 * @param setOfClusterToPrint
	 */
	private static void printSetOfCluster(int minLins,
			ArrayList<Cluster> setOfClusterToPrint, boolean isTraclus) {
		//To print Clusters - Refactor this
		int totalTrajectories = 0;
		
		if(setOfClusterToPrint.isEmpty())
		{
			System.out.println("No clusters meet the parameters criteria");
		}
		else{
			for(Cluster c: setOfClusterToPrint)
			{
				if(isTraclus)
				c.calculateRepresentativeTrajectory(minLins, 0.00005);
				//System.out.println("Cluster: " + c.getClusterID());
				//System.out.println("Representative trajectory: " + c.getRepresentativeTrajectory().toString());
				System.out.println("Cluster: " + c.toString());
				totalTrajectories = totalTrajectories + c.elements.size();
			}

			System.out.println("Total Trajectories in Set of Clusters: " + totalTrajectories);
			System.out.println("*************************************************");
		}
	}

	/**
	 * This function compares 2 sets of clusters, one with labels considered baseline or gold standard
	 * and another generated by our trajectory clustering techniques.
	 * @param baselineSet
	 * @param testSet
	 * @param allTrajectories
	 * @param printConfusionMatrix 
	 */
	public static void compareClusters(ArrayList<Cluster> baselineSet, ArrayList<Cluster> testSet, HashSet<Integer> allTrajectories, boolean printConfusionMatrix)
	{
		//For Whole Method Statistics
		float methodPurity = 0;
		float methodCoverage = 0;
		float methodAccuracy = 0;
		float methodFMeasure = 0;
		float baselineCardinality = 0;
		float realClusterCardinality = 0;

		//Mutual Information
		float mutualInfo = 0;
		float methodNormalizedMutualInfo = 0;
		float entropyBaselineSet = 0;
		boolean testSetEntropyCalculated = false;
		float entropyTestSet = 0;
		
		System.out.println("Considered trajectories: " + allTrajectories.size());
		
		int numClustersNotProducedByMethod = 0;
		
		//Confusion matrix
		String confusionMatrix = "";
		
		//For confusion Matrix
		ArrayList<ArrayList<Integer>> confusionMatrixList = new ArrayList<ArrayList<Integer>>();
		
		for(Cluster cb: baselineSet)
		{
			
			int equivalentIndex=-1;
			float commonElements=0;
			float falsePositives=0;
			float falseNegatives=0;
			float trueNegatives=0;
		
			//For Entropy in Normalized Mutual Information
			float tempCBInfo = (float) cb.elements.size()/(float) allTrajectories.size();
			entropyBaselineSet -= tempCBInfo * Math.log10(tempCBInfo)/Math.log10(2);
			
			realClusterCardinality = cb.getElements().size();
			//true negatives(tni), i.e., the number of trajectories that do not belong to ci and they were
			//correctly assigned to a cluster different from ci
			
			//cb.calculateCardinality();
			
			//Just for testing purposes
			Cluster debugTestCluster = null;
			Cluster debugRealCluster = null;
			HashSet<Integer> commonDebug = new HashSet<Integer>();
			HashSet<Integer> notInAnySetDebug = new HashSet<Integer>();
			float totalElements = 0;
			
			//Row of confusion Matrix
			ArrayList<Integer> lineConfusionMatrix = new ArrayList<Integer>();
			lineConfusionMatrix.add(cb.getClusterID());
			
			for(Cluster ct: testSet)
			{
				ct.calculateCardinality();
				cb.calculateCardinality();
				HashSet<Integer> common = cb.getParentTrajectories();
				common.retainAll(ct.getParentTrajectories());
				
				//For Mutual Information
				if(common.size()>0)
				{
					double tempLogEntropy = Math.log10((allTrajectories.size()*common.size())/((double)(cb.getElements().size()*ct.cardinality)))/Math.log10(2);
					mutualInfo += ((double) common.size()/(double) allTrajectories.size())*tempLogEntropy;
				}
				
				//For Entropy in Normalized Mutual Information
				if(!testSetEntropyCalculated)
				{
					float tempCTInfo = (float) (ct.elements.size()/(float) allTrajectories.size());
					entropyTestSet -= tempCTInfo * Math.log10(tempCTInfo)/Math.log10(2);
				}
				
				//For Confusion Matrix
				int intersection = 0;
				
				if(commonElements<common.size())
				{
					commonElements = common.size();
					equivalentIndex = ct.getClusterID();
					cb.calculateCardinality();
					falsePositives = ct.cardinality - commonElements;
					falseNegatives = cb.cardinality - commonElements;
					
					//For trueNegatives
					HashSet<Integer> notInAnySet = new HashSet<Integer>();
					notInAnySet.addAll(allTrajectories);
					notInAnySet.removeAll(cb.getParentTrajectories());
					notInAnySet.removeAll(ct.getParentTrajectories());

					trueNegatives = notInAnySet.size();
					
					//For confusion Matrix
					intersection = (int) commonElements;
					
					//*****************************
					//For Debugging
					totalElements = commonElements + falseNegatives + falsePositives + trueNegatives;
					//if(totalElements>allTrajectories.size())
					if(falseNegatives>0)
					{
						cb.calculateCardinality();
					//debugging variable
					debugTestCluster = ct;
					debugRealCluster = cb;
					commonDebug.addAll(common);
					notInAnySetDebug.addAll(notInAnySet);
					}
					//***************************
				}
				//Also for Confusion Matrix
				lineConfusionMatrix.add((int) (intersection));
			}
			//For Normalized Mutual Information
			testSetEntropyCalculated = true;
			
			//For confusion Matrix
			//Save label of test Cluster
			if(cb.getClusterID()==0)
			{
				confusionMatrix = confusionMatrix + "Test Clusters->";
				for(Cluster tempTestCluster:testSet)
				{
					confusionMatrix = confusionMatrix + "\t|" + tempTestCluster.getClusterID();
				}
			}
			
			//For confusion Matrix
			confusionMatrixList.add(lineConfusionMatrix);
			
			System.out.println("\n");
			
			totalElements = commonElements + falseNegatives + falsePositives + trueNegatives;
			
			if(totalElements>allTrajectories.size())
			{
				System.err.println("Bug is here, check this out.");
				System.err.println("Total Elements: " + totalElements);
				System.err.println("Real cluster trajectories number: " + allTrajectories.size());
				System.err.println("Test Cluster: " + debugTestCluster.toStringComplete());
				System.err.println("Real Cluster: " + debugRealCluster.toStringComplete());
				System.err.println("Common Set: " + commonDebug);
				System.err.println("Not in any Set: " + notInAnySetDebug);
			}
				
			if(equivalentIndex>-1)
			{
			System.out.println("Real Cluster: " + cb.getClusterID() 
					+ " Equivalent test Cluster: " + equivalentIndex
					+ " Common Elements (TP): " + commonElements
					+ " False Positives (FP): " + falsePositives
					+ " False Negatives (FN): " + falseNegatives
					+ " True Negatives  (TN): " + trueNegatives
					+ " Total Elements : " + totalElements);
			
			float purity = commonElements/(commonElements + falsePositives);
			float coverage = commonElements/(commonElements + falseNegatives);
			float accuracy = (commonElements+trueNegatives)/(commonElements + trueNegatives + falsePositives + falseNegatives);
			float fMeasure = 10*(purity * coverage)/( 9* purity + coverage);
			System.out.println("Per Cluster Stats for Cluster " +  cb.getClusterID());
			System.out.println("Purity: 	" +  purity);
			System.out.println("Coverage: 	" +  coverage);
			System.out.println("Accuracy: 	" +  accuracy);
			System.out.println("F-Measure:	" +  fMeasure);
			
			/*
			methodPurity += purity;
			methodCoverage += coverage;
			methodAccuracy += accuracy;
			methodFMeasure += fMeasure;*/
			
			//New weighted score
			methodPurity += (purity  * realClusterCardinality);
			methodCoverage += (coverage * realClusterCardinality);
			methodAccuracy += (accuracy * realClusterCardinality);
			methodFMeasure += (fMeasure * realClusterCardinality);
			baselineCardinality += realClusterCardinality;
			
			}else{
				System.out.println("Real Cluster: " + cb.getClusterID() 
						+ " Equivalent test Cluster: NONE");
				System.out.println("Not produced by the clustering method, no intersects!!!");
				numClustersNotProducedByMethod++;
			}
		}
		
		int sizeNormalizer = baselineSet.size() - numClustersNotProducedByMethod;
		
		/*
		methodPurity = methodPurity/sizeNormalizer;
		methodCoverage = methodCoverage/sizeNormalizer;
		methodAccuracy = methodAccuracy/sizeNormalizer;
		methodFMeasure = methodFMeasure/sizeNormalizer;
		*/
		
		//For weighted score
		methodPurity = methodPurity/baselineCardinality;
		methodCoverage = methodCoverage/baselineCardinality;
		methodAccuracy = methodAccuracy/baselineCardinality;
		methodFMeasure = methodFMeasure/baselineCardinality;
		
		methodNormalizedMutualInfo = mutualInfo/((entropyBaselineSet+entropyTestSet)/2);
		
		System.out.println("\n");
		System.out.println("Per Method Statistics: ");
		System.out.println("Purity: 	" +  methodPurity);
		System.out.println("Coverage: 	" +  methodCoverage);
		System.out.println("Accuracy: 	" +  methodAccuracy);
		System.out.println("F-Measure: 	" +  methodFMeasure);
		System.out.println("Normalized Mutual Information (NMI): " +  methodNormalizedMutualInfo);
		testTrajectoryClustering.NMIValues.add(methodNormalizedMutualInfo);
		
		//Now print Confusion Matrix
		int lenghtOfRowLine = confusionMatrix.length();
		confusionMatrix = confusionMatrix + "\n";
		for(int j=0; j<lenghtOfRowLine; j++)
		{
			confusionMatrix = confusionMatrix + "--";
		}
		confusionMatrix = confusionMatrix + "-----\n";
		char[] realClusterLegend = " Real Clusters".toCharArray();
		
		int legendIndex = 0;
		for(ArrayList<Integer> RowConfMatrix:confusionMatrixList)
		{
			char tempLegend = ' ';
			if(realClusterLegend.length>legendIndex)
			{
			tempLegend = realClusterLegend[legendIndex];
			}
			
			confusionMatrix = confusionMatrix + tempLegend + "\t";
			for(Integer i:RowConfMatrix)
			{
				confusionMatrix = confusionMatrix + i + "\t|";
			}
			confusionMatrix = confusionMatrix +"\n";
			legendIndex++;
		}
		if(printConfusionMatrix)
		{
			System.out.println("\n");
			System.out.println("**************Confusion Matrix***********");
			System.out.println("Rows: \t\tReal Clusters");
			System.out.println("Columns: \tTest Clusters");
			System.out.println("Cells represent common elements between clusters.");
			System.out.println("");		
			System.out.println(confusionMatrix);
		}
	}
	
	//New experiments Rao
	
	/**
	 * Method to evaluate how hashing functions perform and when the produced clusters decay.
	 * @param method : The Clustering Method. Has to be a Hashing method (LSH or DBH)
	 * @param trajectoryDataset
	 * @param plotGraph
	 * @param simplifyTrajectories
	 * @param simplificationMethod
	 * @param fixNumberPartitionSegment
	 * @throws Exception 
	 */
	public static int evaluateProduceNumOfClusters(ClusteringMethod method, TrajectoryDatasets trajectoryDataset,
			boolean plotGraph, boolean simplifyTrajectories, SegmentationMethod simplificationMethod, int fixNumberPartitionSegment, int maxIterations, int startingNumHashFunctions) throws Exception
	{	
		int numOfNonZeroClusters = -1;
		int previousNumOfNonZeroClusters = -2;
		int numIterations=0;
		int numHashFunctions = startingNumHashFunctions;
		
		HashMap<Integer, Integer> hashesPerIteration = new HashMap<Integer, Integer>(); 
		HashMap<Integer, Integer> minNumElemNon0ClusterPerIteration = new HashMap<Integer, Integer>(); 
		
		SegmentationMethod segmentationMethod = (simplificationMethod==null? SegmentationMethod.douglasPeucker : simplificationMethod);
		String dataset = getDatasetVariable(trajectoryDataset);
		ArrayList<Trajectory> workingTrajectories = getTrajectories(simplifyTrajectories, fixNumberPartitionSegment, dataset, trajectoryDataset, null);

		do{
		previousNumOfNonZeroClusters = numOfNonZeroClusters;
		//Partition Parameters
		//Default DP unless stated otherwise
	
		ArrayList<Cluster> testClusters = new ArrayList<Cluster>();
				
		//Before clustering, lets simplify trajectories if we have to.
		//This have to be done here rather than in the clustering class to have a fair comparison.
		TrajectoryClustering tc = new TrajectoryClustering(workingTrajectories);
		int minNumElems = 1;
		
			if(method == ClusteringMethod.DBH_APPROXIMATION_DTW)
			{
				//Parameters only for DBH APPROXIMATION
				int l = 1;
				float mergeRatio = 1/2;
				boolean merge = false;
				
				
				testClusters = tc.executeDBHApproximationOfClusterOverTrajectories(l, numHashFunctions, minNumElems, merge, mergeRatio);
				
			}
			
			if(method == ClusteringMethod.LSH_EUCLIDEAN)
			{
				//Parameters for LSH
				int windowSize = 500;

				testClusters = tc.executeLSHEuclidean(numHashFunctions, windowSize, minNumElems);
			}
			
			//For Minimun Number of Elements in a Cluster from a set of Clusters per Number of hash Functions
			int minNumElementsCluster = Integer.MAX_VALUE;
			for(Cluster c:testClusters)
			{
				if(c.elements.size()<minNumElementsCluster)
				{
					minNumElementsCluster = c.elements.size();
				}
			}
			minNumElemNon0ClusterPerIteration.put(numHashFunctions, minNumElementsCluster);
			//*********************
			
			numOfNonZeroClusters = testClusters.size();
			//For total Number of Clusters in set of Clusters per Number of hash Functions
			hashesPerIteration.put(numHashFunctions, numOfNonZeroClusters);
			numHashFunctions++;
			numIterations++;
		}while(previousNumOfNonZeroClusters<numOfNonZeroClusters && numIterations<maxIterations);
		
		System.out.println("\n");
		System.out.println("Hashing Functions -> Number of Non-Zero Clusters");
		System.out.println(hashesPerIteration);
		
		System.out.println("\n");
		System.out.println("Hashing Functions -> Minimun Number of Elements in a Cluster (Non-Zero Cluster)");
		System.out.println(minNumElemNon0ClusterPerIteration);
		
		return hashesPerIteration.size();
	}

	/**
	 * Gets the trajectories, as an ArrayList of trajectories. If simplified, writes simplified trajectories to a path and rereads.
	 * Also writes a class variable so original trajectories can be match with the simplified trajectories in the reread (for plotting in order methods).
	 * @param simplifyTrajectories : Determine if trajectories are simplified
	 * @param fixNumberPartitionSegment : Determines the number of partitions on each of the return trajectories
	 * @param dataset : The dataset to work on
	 * @param trajectoryDataset 
	 * @param originalCompleteTrajectories 
	 * @return ArrayList of Trajectories.
	 * @throws Exception : Exception throw when original trajectory data parameter missing to calculate simplified trajectories of AUSSign language dataset
	 */
	private static ArrayList<Trajectory> getTrajectories(boolean simplifyTrajectories,
			int fixNumberPartitionSegment, String dataset, TrajectoryDatasets trajectoryDataset, ArrayList<Trajectory> originalCompleteTrajectories) throws Exception {
		
		ArrayList<Trajectory> workingTrajectories;
		
		switch (trajectoryDataset) {
		case AUSSIGN:
			if(simplifyTrajectories)
			{
				if(originalCompleteTrajectories!=null)
				{
					workingTrajectories = InputManagement.simplifyTrajectoriesFromDataset(fixNumberPartitionSegment, originalCompleteTrajectories);
				}else{
					System.err.println("Error: Cannot calcultate simplified trajectories for dataset " + trajectoryDataset.toString() + " if original dataset is null.");
					throw new Exception("Error: Cannot calcultate simplified trajectories for dataset " + trajectoryDataset.toString() + " if original dataset is null.");
				}
			}else{
				workingTrajectories = InputManagement.generateTestTrajectoriesFromDataSetAusSign();
			}
			break;
		case GEOLIFE:
			if(simplifyTrajectories)
			{
				if(originalCompleteTrajectories!=null)
				{
					workingTrajectories = InputManagement.simplifyTrajectoriesFromDataset(fixNumberPartitionSegment, originalCompleteTrajectories);
				}else{
					System.err.println("Error: Cannot calcultate simplified trajectories for dataset " + trajectoryDataset.toString() + " if original dataset is null.");
					throw new Exception("Error: Cannot calcultate simplified trajectories for dataset " + trajectoryDataset.toString() + " if original dataset is null.");
				}
			}else{
				workingTrajectories = InputManagement.generateTestTrajectoriesFromDataSetMicrosoftGeolife(150);
			}
			break;
		case CROSS:
		case LABOMNI:
			if(simplifyTrajectories)
			{
				String path = System.getProperty("user.dir") + "\\Simplified points\\";
				
				//For printing Original Trajectories
				representedOriginalTraj = OutputManagement.ExportReducedTrajectories(path, dataset, fixNumberPartitionSegment);
				String exported = "CVRR_Dataset_Exported";
				workingTrajectories = InputManagement.generateTestTrajectoriesFromDataSetCVRR(exported, simplifyTrajectories, dataset);
			}else{
				workingTrajectories = InputManagement.generateTestTrajectoriesFromDataSetCVRR(dataset, simplifyTrajectories, null);
			}
			break;
		default:
			workingTrajectories = InputManagement.generateTestTrajectoriesFromDataSetCVRR(dataset, simplifyTrajectories, null);
			break;
		}
		return workingTrajectories;
	}
	
	/**
	 * This method generates more trajectories based on an original dataset, by cloning trajectories in order in a loop
	 * just changing the trajectory Id. This method is used only for testing time and scalability and not for Cluster Quality.
	 * @param numberOfTrajectories : Number of Trajectories to generate
	 * @param previousTrajectories : Set of base trajectories to copy until the needed number of trajectories is reached.
	 * @return Big dataset of trajectories.
	 */
	private static ArrayList<Trajectory> bigDataset(int numberOfTrajectories, ArrayList<Trajectory> previousTrajectories)
	{
		ArrayList<Trajectory> workingTrajectories = new ArrayList<Trajectory>();
		workingTrajectories.addAll(previousTrajectories);
		int generatedTrajectoryId = previousTrajectories.size();
		while(generatedTrajectoryId<numberOfTrajectories)
		{
			for(Trajectory t: previousTrajectories)
			{
				Trajectory temp = (Trajectory) t.clone();
				temp.setTrajectoryId(generatedTrajectoryId);
				workingTrajectories.add(temp);
				generatedTrajectoryId++;
				if(generatedTrajectoryId == numberOfTrajectories)
				{
					break;
				}
			}
		}
		
		//For debugging Only
		/*
		System.out.println("**********BIG DATASET*********");
		System.out.println(workingTrajectories);
		*/
		
		return workingTrajectories;
	}

	/**
	 * This method returns the name of the Property value to lookup for the dataset path.
	 * @param trajectoryDataset
	 * @param dataset
	 * @return
	 */
	private static String getDatasetVariable(TrajectoryDatasets trajectoryDataset) 
	{
		String dataset = "";
		
		switch (trajectoryDataset) {
		case LABOMNI:
			dataset = "CVRR_Dataset_Labomni_Path";
			break;
		case CROSS:
			dataset = "CVRR_Dataset_Cross_Path";
			break;
		case ELK:
			//TODO Correct this, cause this is not the path.
			dataset = "GEOLIFE_Dataset";
			break;
		case AUSSIGN:
			dataset = "AUS_SIGN_Dataset";
			break;
		case GEOLIFE:
			dataset = "GEOLIFE_Dataset";
			break;
		default:
			dataset = "CVRR_Dataset_Labomni_Path";
			break;
		}
		
		return dataset;
	}
	
	/**
	 * This method allows to plot all trajectories from clusters with simplified trajectories. It is for comparison and
	 * visual aid, specially for LSH-Euclidean and K-Means Euclidean, which only use simplified trajectories.
	 * @param simpleTrajSetOfClusters
	 * @param originalTrajectories
	 * @throws Exception If the set of Cluster does not contain the same number of elements than the set of original trajectories
	 */
	private static void plotOriginalTrajectories(ArrayList<Cluster> simpleTrajSetOfClusters, ArrayList<Trajectory> originalTrajectories) throws Exception
	{
		//This could be refactored to be more efficient, other kind of checking peraphs.
		int totalTrajectoriesInSetOfClustersSimpleTraj = 0;
		for(Cluster c: simpleTrajSetOfClusters)
		{
			totalTrajectoriesInSetOfClustersSimpleTraj += c.elements.size();
		}
		
		if(totalTrajectoriesInSetOfClustersSimpleTraj!=originalTrajectories.size())
		{
			throw new Exception("Error: Different number of trajectories in Set of Cluster with Simplified trajectories and the original set of trajectories!!!. Could not plot Clusters with complete trajectories.");
		}
		
		ArrayList<Cluster> completeTrajSetOfClusters = new ArrayList<Cluster>();
		for(Cluster c: simpleTrajSetOfClusters)
		{
			Cluster tempClusterCompleteTraj = new Cluster(c.getClusterID(), c.getClusterID() + "CompleteTraj");
			for(Clusterable t: c.elements)
			{
				tempClusterCompleteTraj.addElement(originalTrajectories.get(t.id));
				/*
				int simplifiedTrajecId = t.id;
				int tempCompleteTrajId = -1;
				do{
					Trajectory tempCompleteTrajectory = originalTrajectories.get(simplifiedTrajecId);
					tempCompleteTrajId = tempCompleteTrajectory.getTrajectoryId();
					if(tempCompleteTrajectory.getTrajectoryId()==simplifiedTrajecId)
					{
						tempClusterCompleteTraj.addElement(tempCompleteTrajectory);
					}
				}while(tempCompleteTrajId>simplifiedTrajecId);
				*/
			}
			completeTrajSetOfClusters.add(tempClusterCompleteTraj);
		}
		TrajectoryPlotter.drawAllClusters(completeTrajSetOfClusters, false, true);
		TrajectoryPlotter.drawAllClustersInSameGraph(completeTrajSetOfClusters, true, "AllClusterCompleteTrajectories");
	}
}