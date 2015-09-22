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
		//Just to debug
		//Print al trajectory dimensions
		int i=0;
		for(Clusterable c: trajectoryList)
		{
			Trajectory t = (Trajectory) c;
		System.err.println("Trajectory: " + t.getTrajectoryId() + " index: " + i + " Points: " + t.elements.size());
		i++;
		}
		
		KMeansClusterer kmeaner = new KMeansClusterer();
		Cluster[] km = kmeaner.cluster(trajectoryList, k);
		
		return km;
	}
}
