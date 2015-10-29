package cluster.trajectory;

import java.util.ArrayList;
import java.util.List;

import com.stromberglabs.cluster.Cluster;
import com.stromberglabs.cluster.Clusterable;
import com.stromberglabs.cluster.KMeansClusterer;

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
	
	public static ArrayList<cluster.trajectory.Cluster> executeVectorKmeans(List<? extends Clusterable> elementsToCluster, ArrayList<Trajectory> trajectories, int k) throws Exception
	{
		ArrayList<cluster.trajectory.Cluster> kmeansClusters = new ArrayList<cluster.trajectory.Cluster>();
		
		//here call kmeans
		com.stromberglabs.cluster.Cluster[] kmeansCluster = Kmeans.execute(elementsToCluster, k);
		
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
			System.out.println("Binary Vector cluster name: "+ tempMyCluster.getClusterName());
		}
		
		return kmeansClusters;
	}
}
