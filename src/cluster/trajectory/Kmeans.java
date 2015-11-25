package cluster.trajectory;

import java.util.ArrayList;
import java.util.List;

import com.stromberglabs.cluster.Cluster;
import com.stromberglabs.cluster.Clusterable;
import com.stromberglabs.cluster.KMeansClusterer;

import extras.TimeKeeping;

public class Kmeans {

	public Kmeans() {
		// TODO Auto-generated constructor stub
	}
	
	public static Cluster[] execute(List<? extends Clusterable> trajectoryList, int k)
	{	
		KMeansClusterer kmeaner = new KMeansClusterer();		
		Cluster[] km = kmeaner.cluster(trajectoryList, k);
		
		return km;
	}
	
	public static ArrayList<cluster.trajectory.Cluster> executeVectorKmeans(List<? extends Clusterable> elementsToCluster, ArrayList<Trajectory> trajectories, int k, boolean calculateSilhouetteCoefficient) throws Exception
	{
		long startTimeKmeans = System.nanoTime();
		
		ArrayList<cluster.trajectory.Cluster> kmeansClusters = new ArrayList<cluster.trajectory.Cluster>();
		
		//here call kmeans
		com.stromberglabs.cluster.Cluster[] kmeansCluster = Kmeans.execute(elementsToCluster, k);
		
		//This is the output of the L2 SilhouetteCoefficient
		long startTimeKmeansSilhouette = 0;
		long totalSilhouetteTime = 0;
		if(calculateSilhouetteCoefficient)
		{
			startTimeKmeansSilhouette = System.nanoTime();
			double L2silhouetteCoefficientGeneratedFVClusters = ClusterQualityMeterer.silhoutteCoefficientFeatureVector(kmeansCluster);
			System.out.println("Internal L2 Silhouette Coefficient Generated Set of Clusters as Feature Vectors: " + L2silhouetteCoefficientGeneratedFVClusters);
			long stopTimeSilhouette = System.nanoTime();
			totalSilhouetteTime = stopTimeSilhouette - startTimeKmeansSilhouette;
			TimeKeeping.wastedTime += totalSilhouetteTime;
		}
		
		for(com.stromberglabs.cluster.Cluster c:kmeansCluster)
		{
			cluster.trajectory.Cluster tempMyCluster = new cluster.trajectory.Cluster(c.getId(), "kmeans" + c.getId());
			List<com.stromberglabs.cluster.Clusterable> items = c.getItems();
			for(com.stromberglabs.cluster.Clusterable i: items)
			{
				//TODO verify if this code will be ever executed, it seems to me that IT wont and it should
				//be deleted
				
				Trajectory t;
				if(i.getClass()== Trajectory.class)
				{
				t = (Trajectory) i;
				
				}else{
					if(i.getClass()==FeatureVector.class)
					{
						FeatureVector fv = (FeatureVector) i;
						t = trajectories.get(fv.getId());
						tempMyCluster.setClusterName(fv.featuresToString());
						if(fv.getId()!=t.getTrajectoryId())
						{
							System.err.println("MegaFatal Error: Trajectory Id does not match the vector");
							throw new Exception("MegaFatal Error: Trajectory Id does not match the vector");
						}
					}else{
						t=null;
						throw new Exception("Not a recognizable object to cast to Trajectory");
					}
				}
				tempMyCluster.addElement(t);
			}
			tempMyCluster.calculateCardinality();
			kmeansClusters.add(tempMyCluster);
			
			//TODO enable this in debug mode only.
			//System.out.println("Binary Vector cluster name: "+ tempMyCluster.getClusterName());
		}
		
		long stopTimeKmeans = System.nanoTime();
		long totalKmeansTime = stopTimeKmeans - startTimeKmeans - totalSilhouetteTime;
		System.out.println("Time spent in Silhouette Coefficient Calculation: " + totalSilhouetteTime/1000000000.0);
		System.out.println("Kmeans Over Feature Vector time in seconds (excluding Silhouette time): " + totalKmeansTime/1000000000.0);
		
		return kmeansClusters;
	}
}
