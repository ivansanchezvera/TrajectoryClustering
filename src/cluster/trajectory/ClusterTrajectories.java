package cluster.trajectory;
import java.util.ArrayList;


public class ClusterTrajectories extends Cluster {

	private ArrayList<Trajectory> trajectories;
	
	public ClusterTrajectories(int clusterID, String clusterName) {
		super(clusterID, clusterName);
		this.trajectories = new ArrayList<Trajectory>(); 
		// TODO Auto-generated constructor stub
	}

	public void addTrajectory(Trajectory t)
	{
		trajectories.add(t);
		clusterSize = trajectories.size();
	}
	
	@Override
	public int calculateCardinality() {
		// TODO Auto-generated method stub
		this.cardinality = 0;
		for(Trajectory t:trajectories)
		{
				parentTrajectories.add(t.getTrajectoryId());
		}
		
		cardinality = parentTrajectories.size();
		return cardinality;
	}
	
	@Override
	public String toString() {
		return "Cluster [clusterID=" + clusterID + ", size=" + clusterSize
				+ ", trajectories=" + trajectories
				+ "]";
	}
}
