package cluster.trajectory;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
		if(!(cm.equals(ClusteringMethod.DBH_APPROXIMATION_DTW) || cm.equals(ClusteringMethod.KMEANS_DTW) || cm.equals(ClusteringMethod.KMEDOIDS_DTW) || cm.equals(ClusteringMethod.DBH_DTW_FEATURE_VECTOR)))
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
		if(!(cm.equals(ClusteringMethod.DBH_APPROXIMATION_DTW) || cm.equals(ClusteringMethod.KMEANS_DTW) || cm.equals(ClusteringMethod.KMEDOIDS_DTW) ||  cm.equals(ClusteringMethod.DBH_DTW_FEATURE_VECTOR)))
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
}
