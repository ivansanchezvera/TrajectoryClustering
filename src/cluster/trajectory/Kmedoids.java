package cluster.trajectory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.stromberglabs.cluster.Clusterable;
import com.stromberglabs.cluster.KMeansClusterer;

public class Kmedoids {

	public Kmedoids() {
		// TODO Auto-generated constructor stub
	}

	public static ArrayList<Cluster> execute(List<Trajectory> trajectoryList, int k)
	{
		ArrayList<Cluster> kmenoidsClusters = new ArrayList<Cluster>();
		
		//Calculate clusters
		
		//1. Randomly select K-Points
		Random r = new Random();
		int maxSize = trajectoryList.size();
		
		//To keep track of elements already picked
		HashSet<Integer> hs = new HashSet<Integer>();
		
		for(int i=0; i<k; i++)
		{
			int index;
			if(hs.size()<1)
			{
				index = r.nextInt(maxSize);
				hs.add(index);
			}else
			{
				index = r.nextInt(maxSize);
				while(hs.contains(index))
				{
					index = r.nextInt(maxSize);
				}
				hs.add(index);
			}
		}
		
		for(Integer i:hs)
		{
			//Is it natural to name clusters by its centroid ID. Maybe I need to change this.
			Cluster c = new Cluster(i, "Cluster"+i);
			c.addElement(trajectoryList.get(i));
			c.setCentroidElement(trajectoryList.get(i));
			kmenoidsClusters.add(c);
		}
		
		//To compare with previous clusters
		ArrayList<Cluster> newClusters = new ArrayList<Cluster>();
		newClusters.addAll(kmenoidsClusters);
		ArrayList<Cluster> previousClusters = new ArrayList<Cluster>();
		
		int iterations = 0;
		//boolean clustersChanged = true; 
		do{
			previousClusters = new ArrayList<Cluster>();
			for(Cluster c: newClusters)
			{
				Cluster cloned = c.clone();
				previousClusters.add(cloned);
				
				//c = new Cluster(cloned.getClusterID(), cloned.getClusterName());
				int index = newClusters.indexOf(c);
				c = cloned.cloneStructureWithNoElements();
				newClusters.set(index, c);
			}
		
		
			//2. For each point find the closest center-point.
			for(Trajectory t:trajectoryList)
			{
				//Get DTW distance from center points
				double minDTWDistance = Double.POSITIVE_INFINITY;
				int clusterIndex = -1; //Verify that this does not fail
				for(Cluster c:newClusters)
				{
					//Refactor this to be more elegant
					//if(t.getTrajectoryId()!=c.getCentroidElement().getTrajectoryId())
					{
						double distance = Trajectory.calculateDTWDistance(t, (Trajectory) c.getCentroidElement());
						
						if(distance<minDTWDistance)
						{
							minDTWDistance = distance;
							clusterIndex = newClusters.indexOf(c);
						}
					}
				}
				
				//Add object to corresponding cluster
				if(clusterIndex>-1)
				newClusters.get(clusterIndex).addElement(t);
			}
			
			//3. Minimize within the cluster
			for(Cluster c: newClusters)
			{
				//Find the point that minimizes the overall square sum 
				//of distances to all other points and make it the new centroid
				
				double minInternalClusterDistance = Double.POSITIVE_INFINITY;
				Trajectory minimizingCentroid = null;
				for(Object o:c.getElements())
				{
					//Find the trajectory in the cluster that minimizes the sum of all trajectories
					double tempDist = 0;
					for(Object o2:c.getElements())
					{
						if(o2!=o)
						{
							tempDist = tempDist + Trajectory.calculateDTWDistance((Trajectory)o, (Trajectory)o2);
						}
					}
					
					if(tempDist<minInternalClusterDistance)
					{
						minInternalClusterDistance = tempDist;
						minimizingCentroid = (Trajectory) o;
					}
				}
				c.setCentroidElement(minimizingCentroid);
				c.setMinInternalVariation(minInternalClusterDistance);
			}
			iterations++;
			System.out.println("Number of Iteration till convergence: " + iterations);
		}while(detectChangesInClusters(newClusters,previousClusters));
		//4. Verify that there is no variation within the cluster.
		
		return newClusters;
	}
	
	public static boolean detectChangesInClusters(ArrayList<Cluster> newClusters, ArrayList<Cluster> previousClusters)
	{
		boolean clustersChanged = false;
		
		for(Cluster c:previousClusters)
		{
			Cluster correspondingNewCluster = newClusters.get(previousClusters.indexOf(c));
			if(c.getCentroidElement()!=correspondingNewCluster.getCentroidElement())
			{
				clustersChanged = true;
				return clustersChanged;
			}else{
				if(c.getMinInternalVariation()!=correspondingNewCluster.getMinInternalVariation())
				{
					clustersChanged = true;
					return clustersChanged;
				}
			}
		}
		
		return clustersChanged;
	}
}
