package cluster.trajectory;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.lang.model.element.Element;

import extras.AuxiliaryFunctions;

public class ClusterQualityMeterer {

	private Cluster clusterToEvaluate; 
	private Cluster optionalBaselineCluster;
	
	public ClusterQualityMeterer(Cluster clusterToEvaluate) {
		// TODO Auto-generated constructor stub
		this.clusterToEvaluate = clusterToEvaluate;
	}
	
	public static String intraClusterDistanceDTW(Cluster clusterToEvaluate, ClusteringMethod cm) throws Exception
	{
		if(!(cm.equals(ClusteringMethod.DBH_APPROXIMATION_DTW) || cm.equals(ClusteringMethod.KMEANS_DTW) || cm.equals(ClusteringMethod.KMEDOIDS_DTW) || cm.equals(ClusteringMethod.DBH_DTW_FEATURE_VECTOR_BINARY)))
		{
			throw new Exception("This comparison method is only suitable for clusters generated under DTW distance approach or approximation");
		}

		DecimalFormat df = new DecimalFormat("#.00"); 
		
		String evaluationMatrixHeader = "DTW Distance Matrix for Elements of Cluster: " + clusterToEvaluate.getClusterID() + "\n\t\t";
		String evaluationMatrix = "";
		for(Clusterable cl1: clusterToEvaluate.getElements())
		{
			Trajectory t = (Trajectory) cl1;
			evaluationMatrixHeader = evaluationMatrixHeader + t.getTrajectoryId() + "\t";
			evaluationMatrix = evaluationMatrix + "Tra " + t.getTrajectoryId() + "\t\t";
			
			for(Clusterable cl2: clusterToEvaluate.getElements())
			{
				Trajectory t2 = (Trajectory) cl2;
				Double dtwDistance = Trajectory.calculateDTWDistance(t, t2);
				evaluationMatrix = evaluationMatrix + df.format(dtwDistance) + "\t";
			}
			evaluationMatrix = evaluationMatrix + "\n";		
		}
		evaluationMatrixHeader = evaluationMatrixHeader + "\n";
		
		String finalEvaluationMatrix = evaluationMatrixHeader + evaluationMatrix;
		System.out.println(finalEvaluationMatrix);
		return finalEvaluationMatrix;
	}

	public static void intraClusterDistanceDTWForAllClustersInSet(ArrayList<Cluster> setOfClusters, ClusteringMethod cm) throws Exception
	{
		if(!(cm.equals(ClusteringMethod.DBH_APPROXIMATION_DTW) || cm.equals(ClusteringMethod.KMEANS_DTW) || cm.equals(ClusteringMethod.KMEDOIDS_DTW) ||  cm.equals(ClusteringMethod.DBH_DTW_FEATURE_VECTOR_BINARY)))
		{
			throw new Exception("This comparison method is only suitable for clusters generated under DTW distance approach or approximation");
		}
		
		System.out.println("********************************************");
		String setOfClustersIntraClusterDistanceMatrix = "Set Of Clustesrs produced with method: " + cm.toString() + "\n";
		for(Cluster c: setOfClusters)
		{
			setOfClustersIntraClusterDistanceMatrix = setOfClustersIntraClusterDistanceMatrix + intraClusterDistanceDTW(c, cm) + "\n";
		}
		
		String path = extras.GetPropertyValues.getPropValues("IntraCluster_Distance_Matrix_File");
		String filename = "IntraClusterDistancesForMethod" + cm.toString() + ".txt";
		AuxiliaryFunctions.printStringToFile(setOfClustersIntraClusterDistanceMatrix, filename, path);
	}
	
	//Sigan sigan sigan bailando, sigan sigan, con don Medardo...
	
	/**
	 * This method calculates the Silhoutte Coefficient over the DTW distance.
	 * @param setOfClusters : Cluster of Trajectories in the Trajectory Representation
	 * @return
	 * @throws Exception
	 */
	public static double silhouetteCoefficientDTW(ArrayList<Cluster> setOfClusters) throws Exception
	{
		double overallSetSilhoutteCoefficient = 0;
		int totalElementsInSet = 0;
		//Calculate internal distance

		for(Cluster c: setOfClusters)
		{
			totalElementsInSet += c.elements.size();
			for(Clusterable element:c.getElements())
			{
				double silhouetteInternalIndexForElement = 0;
				//Convert Clusterable to Trajectory
				Trajectory t = Clusterable.convertToTrajectory(element);
				
				for(Clusterable element2:c.getElements())
				{
						Trajectory tempTraj = Clusterable.convertToTrajectory(element2);
						if(!element.equals(element2))
						{
							silhouetteInternalIndexForElement += Trajectory.calculateDTWDistance(t, tempTraj);
						}
				}
				//This is the mean distrance from the element to the other elements within its cluster 
				//We Substracted one cause element is already in the cluster.
				double finalSilhouetteInternalIndexForElement = silhouetteInternalIndexForElement/(c.elements.size());
				
				Cluster similarCluster = findMostSimilarClusterDTW(c, setOfClusters);
				if(similarCluster==null) throw new Exception("Error finding similar Cluster, null element");
				
				double distanceToExternalElements = 0;
				for(Clusterable externalElement: similarCluster.elements)
				{
					Trajectory externalElementTrajectory = Clusterable.convertToTrajectory(externalElement);
					distanceToExternalElements += Trajectory.calculateDTWDistance(t, externalElementTrajectory);
				}
				double finalDistanceToExternalClusterForElement = distanceToExternalElements/similarCluster.elements.size();
				
				double silhouetteIndexForElement =  (finalDistanceToExternalClusterForElement - finalSilhouetteInternalIndexForElement)/Math.max(finalSilhouetteInternalIndexForElement, finalDistanceToExternalClusterForElement);
				overallSetSilhoutteCoefficient += silhouetteIndexForElement; 
			}
		}
		overallSetSilhoutteCoefficient = overallSetSilhoutteCoefficient/totalElementsInSet;
		return overallSetSilhoutteCoefficient;
	}

	/**
	 * Finds the cluster which its centroid is closer to the Query cluster in DTW distance using DBA centroid method.
	 * @param c
	 * @param setOfClusters
	 * @return
	 */
	private static Cluster findMostSimilarClusterDTW(Cluster c, ArrayList<Cluster> setOfClusters) 
	{
		double minDistanceToOtherCluster = Double.POSITIVE_INFINITY;
		Cluster similarCluster = null;
		try {
			c.calculateCentroid();

			for(Cluster tempCluster: setOfClusters)
			{
				if(c.clusterID==tempCluster.clusterID) continue;
					
				tempCluster.calculateCentroid();

				Trajectory tempCentroid = tempCluster.getCentroidElement();
				
				double distanceToOtherCluster = Trajectory.calculateDTWDistance(tempCentroid, c.getCentroidElement());
				
				if(distanceToOtherCluster<minDistanceToOtherCluster)
				{
					minDistanceToOtherCluster = distanceToOtherCluster;
					similarCluster = tempCluster;
				}			
			}
		} catch (Exception e) {
			System.out.println("Error, could not Calculate Centroid");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return similarCluster;
	}
	
	public static double silhoutteCoefficientFeatureVector(com.stromberglabs.cluster.Cluster[] featureVectorClusters) throws Exception
	{
		double totalSilhouetteCoeficient = 0;
		double allElementsCounter = 0;
		for(com.stromberglabs.cluster.Cluster c: featureVectorClusters)
		{
			allElementsCounter += c.getItems().size();
			for(com.stromberglabs.cluster.Clusterable item1 : c.getItems())
			{
				double itemInternalSilhouetteCoefficient = 0;
				float[] item1FeatureVector = item1.getLocation();
				
				for(com.stromberglabs.cluster.Clusterable item2 : c.getItems())
				{
					float[] item2FeatureVector = item2.getLocation();
					itemInternalSilhouetteCoefficient = FeatureVector.L2distanceFrom(item1FeatureVector, item2FeatureVector);
				}
				//Solve what happens when a cluster has a single element.
				itemInternalSilhouetteCoefficient = itemInternalSilhouetteCoefficient/c.getItems().size();
				
				double itemExternalSilhouetteCoefficient = 0;
				//Find the most similar cluster
				com.stromberglabs.cluster.Cluster closestExternalCluster = findClosestExternalClusterToFeatureVector(item1FeatureVector, c, featureVectorClusters);
				for(com.stromberglabs.cluster.Clusterable externalItem: closestExternalCluster.getItems())
				{
					itemExternalSilhouetteCoefficient += FeatureVector.L2distanceFrom(item1FeatureVector, externalItem.getLocation());
				}
				itemExternalSilhouetteCoefficient = itemExternalSilhouetteCoefficient/closestExternalCluster.getItems().size();
				
				double itemSilhuetteCoefficient = (itemExternalSilhouetteCoefficient - itemInternalSilhouetteCoefficient)/Math.max(itemInternalSilhouetteCoefficient, itemExternalSilhouetteCoefficient);
				totalSilhouetteCoeficient += itemSilhuetteCoefficient;
			}
		}
		totalSilhouetteCoeficient = totalSilhouetteCoeficient/allElementsCounter;
		return totalSilhouetteCoeficient;
	}
	
	/**
	 * Finds the closest EXTERNAL cluster to a given feature vector
	 * @param fv The query Feature Vector
	 * @param c  The cluster to which the query Feature Vector belongs to
	 * @param featureVectorClusters The list of all Clusters of Feature Vectors
	 * @return ClosestExternalCluster The closest external Feature Vector Cluster with respect to the query Feature Vector
	 * @throws Exception When feature vectors are of different Dimensions
	 */
	public static com.stromberglabs.cluster.Cluster findClosestExternalClusterToFeatureVector(float[] fv, com.stromberglabs.cluster.Cluster c, com.stromberglabs.cluster.Cluster[] featureVectorClusters) throws Exception
	{
		com.stromberglabs.cluster.Cluster closestExternalCluster = null;
		double minimumDistanceToExternalClusterCentroid = Double.POSITIVE_INFINITY;
		for(com.stromberglabs.cluster.Cluster externalCluster: featureVectorClusters)
		{
			if(!c.equals(externalCluster))
			{
					double distanceToExternalClusterCentroid = FeatureVector.L2distanceFrom(fv, externalCluster.getClusterMean());
					if(distanceToExternalClusterCentroid<minimumDistanceToExternalClusterCentroid)
					{
						minimumDistanceToExternalClusterCentroid = distanceToExternalClusterCentroid;
						closestExternalCluster = externalCluster;
					}
			}
		}
		
		return closestExternalCluster;
	}
	
}
