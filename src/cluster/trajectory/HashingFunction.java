package cluster.trajectory;

public class HashingFunction {
	
	
	private Trajectory x1;
	private Trajectory x2;
	private float t1;
	private float t2;
	private double ds1s2 = 0;

	
	
	public HashingFunction(Trajectory x1, Trajectory x2,
			float t1, float t2) {
		this.x1 = x1;
		this.x2 = x2;
		this.t1 = t1;
		this.t2 = t2;
		this.ds1s2 = Trajectory.calculateDTWDistance(x1,x2);
	}
	

	public boolean hashMe(Trajectory x)
	{
		//error that gives projections
		//Why??? what to do with this, how to solve it?
		//Report to RAO
		//ds1s2 = Trajectory.calculateDTWDistance(x1,x2);
		
		double dts1 = Trajectory.calculateDTWDistance(x,x1);
		double dts2 = Trajectory.calculateDTWDistance(x,x2);
		
		double hash = (Math.pow(dts1, 2) + Math.pow(ds1s2, 2) - Math.pow(dts2, 2))/(2*ds1s2);
	
		//this should be 50% chance
		if(hash>=t1 && hash<=t2)
		{
			return false;
		}else{
			return true;
		}
		
	}
	

}
