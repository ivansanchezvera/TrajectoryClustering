import java.util.ArrayList;
import java.util.HashSet;


public class Cluster {

	private int clusterID;
	private String clusterName;
	private Trajectory representativeTrajectory;
	private ArrayList<Segment> segments;
	int cardinality;
	HashSet<Integer> parentTrajectories;
	
	public Cluster(int clusterID, String clusterName) {
		this.clusterID = clusterID;
		parentTrajectories = new HashSet<Integer>();
	}
	
	public void addSegment(Segment s)
	{
		segments.add(s);
	}
	
	public Trajectory calculateRepresentativeTrajectory()
	{
		Trajectory representativeTrajectory = null;
		//do stuff
		
		return representativeTrajectory;
	}

	public int getClusterID() {
		return clusterID;
	}

	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public Trajectory getRepresentativeTrajectory() {
		return representativeTrajectory;
	}

	public void setRepresentativeTrajectory(Trajectory representativeTrajectory) {
		this.representativeTrajectory = representativeTrajectory;
	}

	public ArrayList<Segment> getSegments() {
		return segments;
	}

	public void setSegments(ArrayList<Segment> segments) {
		this.segments = segments;
	}

	public int calculateCardinality() {
		// TODO Auto-generated method stub
		this.cardinality = 0;
		for(Segment s:segments)
		{
			parentTrajectories.add(s.getParentTrajectory());
			
			//I dont think this is needed since set dont allow repeated elements
			/*
			if(!parentTrajectories.contains(s.getParentTrajectory()))
			{
				parentTrajectories.add(s.getParentTrajectory);
			}*/
		}
		
		cardinality = parentTrajectories.size();
		return cardinality;
	}

	
}
