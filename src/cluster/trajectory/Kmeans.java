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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
