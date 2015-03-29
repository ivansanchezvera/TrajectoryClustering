import java.util.ArrayList;


public class Traclus {

	private ArrayList<Trajectory> trajectories;
	private ArrayList<Segment> segmentsCompleteSet;
	private ArrayList<Cluster> clusterOfTrajectories;
	private float eNeighborhoodParameter;
	private int minLins;
	private int cardinalityOfClusters;
	//Parameters
	//Notion of E Neighbourhood
	//Notion of acceptable time distance
	//
	ArrayList<Cluster> setOfClusters;
	
	public Traclus(ArrayList<Trajectory> trajectories, float eNeighborhoodParameter, int minLins, int cardinalityOfClusters) {
		this.trajectories = trajectories;
		this.segmentsCompleteSet = new ArrayList<Segment>();
		this.clusterOfTrajectories = new ArrayList<Cluster>();
		this.eNeighborhoodParameter = eNeighborhoodParameter;
		this.minLins = minLins;
		this.cardinalityOfClusters = cardinalityOfClusters;
		
		
	}
	
	public ArrayList<Cluster> execute()
	{
		segmentsCompleteSet = partition(trajectories);
		clusterOfTrajectories = clusterSegments(segmentsCompleteSet, eNeighborhoodParameter, minLins, cardinalityOfClusters);
		return clusterOfTrajectories;
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
	public ArrayList<Cluster> clusterSegments(ArrayList<Segment> setOfSegments, float eNeighborhoodParameter, int minLins, int cardinalityOfClusters)
	{
		setOfClusters = new ArrayList<Cluster>();
		
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
				ArrayList<Segment> neighborSegments = computeENeighborhoodOfSegment(s, eNeighborhoodParameter, setOfSegments);
				
				//Plus 1 cause my definition of neighborhood does not include core line itself
				if(neighborSegments.size()+1>= minLins)
				{
					Cluster c = new Cluster(clusterId, "Cluster " + clusterId);
					setOfClusters.add(c);
					
					//Before adding to cluster,
					//Should I set all segments of the neighborhood as classified??
					int i = 0;
					for(Segment s1:neighborSegments)
					{
						
						
						s1.setClassified(true);
						s1.setNoise(false);
						//neighborSegments.set(i, s1);
						c.addSegment(s1);
						i++;
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
					expandCluster(queue, clusterId, eNeighborhoodParameter, minLins, setOfSegments);
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

	private ArrayList<Segment> computeENeighborhoodOfSegment(Segment s, float eParameter, ArrayList<Segment> allSegments) {
		// TODO Auto-generated method stub'
		ArrayList<Segment> neighborSegments = new ArrayList<Segment>();
		
		for(Segment possibleNeighborSegment:allSegments)
		{
			if(Segment.calculateDistance(s, possibleNeighborSegment)<=eParameter)
			{
				if(s!=possibleNeighborSegment)
				{
				neighborSegments.add(possibleNeighborSegment);
				}
			}
		}
		return neighborSegments;
	}

	//Step 2 - Clustering phase
	//To compute a density-connected set
	private void expandCluster(ArrayList<Segment> queue, int clusterId,
			float eNeighborhoodParameter, int minLins, ArrayList<Segment> segmentsCompleteSet) {
		// TODO Auto-generated method stub
		while(!queue.isEmpty())
		{
			ArrayList<Segment> neighborhood = computeENeighborhoodOfSegment(queue.get(0), eNeighborhoodParameter, segmentsCompleteSet);
			
			if(Math.abs(neighborhood.size())>= minLins)
			{
				for(Segment s: neighborhood)
				{
					if(!s.isClassified() || s.isNoise())
					{
						//Cluster c = new Cluster(clusterId, "Cluster " + clusterId);
						Cluster cTemp =  setOfClusters.get(clusterId);
						s.setClassified(true);
						cTemp.addSegment(s);
						//setOfClusters.add(cTemp);
						setOfClusters.set(clusterId, cTemp);
					}
						//Check this part
					if(!s.isClassified())
					{
						//if element not in queue
						if(!queue.contains(s))
						{
						queue.add(s);
						}
					}
				}
			}
			queue.remove(0);
		}
	}
	
	//Calculate Representative Trajectories.
	
	

}
