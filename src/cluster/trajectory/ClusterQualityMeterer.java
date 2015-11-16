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
	
	public static double silhouetteCoefficient(ArrayList<Cluster> setOfClusters) throws Exception
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
				
				Cluster similarCluster = findMostSimilarCluster(c, setOfClusters);
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

	private static Cluster findMostSimilarCluster(Cluster c, ArrayList<Cluster> setOfClusters) 
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
	
}
