import java.util.ArrayList;


public class Traclus {

	private ArrayList<Trajectory> trajectories;
	
	//Parameters
	//Notion of E Neighbourhood
	//Notion of acceptable time distance
	//
	
	public Traclus() {
		// TODO Auto-generated constructor stub
	}
	
	//3 clear stages
	//Partition Phase
	//Input Set of Trajectories
	public ArrayList<Segment> partition(ArrayList<Trajectory> trajectories)
	{
		ArrayList<Segment> setOfSegments = new ArrayList<Segment>();
		
		for(Trajectory t:trajectories)
		{
			setOfSegments.addAll(t.divideTrajectoryInSegments());
		}
		
		return setOfSegments;
	}
	
	//********************HASHING STAGE*********************************
	
	
	//Do HASHING BEFORE THIS
	//Clustering Phase
	public ArrayList<Cluster> clusterSegments(ArrayList<Segment> setOfSegments, int eNeighborhood, int minLins, int cardinalityOfClusters)
	{
		ArrayList<Cluster> setOfClusters = new ArrayList<Cluster>();
		
		int clusterId = 0;
		
		for(Segment s: setOfSegments)
		{
			if(!s.isClassified())
			{
				//To compute eNeighboorhood is a hard part
				//Cost n^2 unless we used index like R-Tree which reduces complexity to (n log n)
				//where n is the number of segments in the database.
				//Find Out how to index this.
				//MOre info at the end of page 7 (599) of paper
				ArrayList<Segment> neighborSegments = computeNeighborhoodOfSegment(s);
				
				//Plus 1 cause my definition of neighborhood does not include core line itself
				if(neighborSegments.size()+1>= minLins)
				{
					Cluster c = new Cluster(clusterId, "Cluster " + clusterId);
					setOfClusters.add(c);
					
					//Before adding to cluster,
					//Should I set all segments of the neighborhood as classified??
					for(Segment s1:neighborSegments)
					{
						s1.setClassified(true);
						s1.setNoise(false);
						c.addSegment(s1);
					}
					
					//Add neighbours to cluster
					//My definition of neighborhood does not include the core line itself
					//This line is extra since I am already iterating, might be little more efficient?
					//c.setSegments(neighborSegments);
					
					//Add this core line to this cluster
					s.setNoise(false);
					s.setClassified(true);
					c.addSegment(s);
					
					//Insert Neighboors into a Queue (check this part)
					ArrayList<Segment> queue = new ArrayList<Segment>();
					queue.addAll(neighborSegments);
					
					//Now Expand Cluster
					expandCluster(queue, clusterId, eNeighborhood, minLins);
					clusterId++;
				}else{
					s.setClassified(true);
					s.setNoise(true);
				}
			}
		}
		
		//Step 3
		//Clusters are already allocated in my code, this differs from paper description of Traclus.
		//Check cardinality
		for(Cluster c:setOfClusters)
		{
			if(c.calculateCardinality()<cardinalityOfClusters)
			{
				setOfClusters.remove(c);
			}
		}
		
		//Cluster using Density-based method
		//Need a notion of distance and a notion of time difference
		
		return setOfClusters;
		
	}
	
	//Calculate Representative Trajectories.

}
