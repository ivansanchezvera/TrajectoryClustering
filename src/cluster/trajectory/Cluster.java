package cluster.trajectory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class Cluster {

	protected int clusterID;
	protected String clusterName;
	protected Trajectory representativeTrajectory;
	protected int clusterSize;
	protected ArrayList elements;
	protected int cardinality;
	
	protected HashSet<Integer> parentTrajectories;
	
	public Cluster(int clusterID, String clusterName) {
		this.clusterID = clusterID;
		parentTrajectories = new HashSet<Integer>();
		elements = new ArrayList<>();
		clusterSize = 0;
	}
	
	public void addElement(Object o)
	{
		elements.add(o);
		clusterSize = elements.size();
	}
	
	//Refactor this
	public Trajectory calculateRepresentativeTrajectory(int minLines, double smoothingParameter)
	{
		//Should be overwritten
		return null;
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

	public ArrayList<Segment> getElements() {
		return elements;
	}

	public void setElements(ArrayList<Object> elements) {
		this.elements = elements;
	}

	public HashSet<Integer> getParentTrajectories() {
		return parentTrajectories;
	}

	public void setParentTrajectories(HashSet<Integer> parentTrajectories) {
		this.parentTrajectories = parentTrajectories;
	}

	public int calculateCardinality() {
		// TODO Auto-generated method stub
		this.cardinality = 0;
		for(Object o:elements)
		{
			if(o.getClass().equals(Segment.class))
			{
				
			Segment tempSegment = (Segment) o;
			parentTrajectories.add(tempSegment.getParentTrajectory());
			}else if(o.getClass().equals(Trajectory.class)){
				Trajectory tempTrajectory = (Trajectory) o;
				parentTrajectories.add(tempTrajectory.getTrajectoryId());
			}else{
				//Handle error here
				System.out.println("Wrong casting in cluster class, not trajectory or segment");
			}
		}
		
		cardinality = parentTrajectories.size();
		return cardinality;
	}

	@Override
	public String toString() {
		return "Cluster [clusterID=" + clusterID + ", size="
						+ clusterSize + ", elements=" + elements
				+ ", cardinality=" + cardinality + ", parentTrajectories="
				+ parentTrajectories + "]";
	}
	
	
	public String toStringComplete() {
		return "Cluster [clusterID=" + clusterID + ", clusterName="
				+ clusterName + ", representativeTrajectory="
				+ representativeTrajectory  + ", size="
						+ clusterSize + ", elements=" + elements
				+ ", cardinality=" + cardinality + ", parentTrajectories="
				+ parentTrajectories + "]";
	}
}
