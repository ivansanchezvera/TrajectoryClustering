import java.security.Timestamp;


//Points on Trajectories have d Dimensions
//Lets start with 2 dimensions and Time
//This can obviously extended.
public class Point {

	private float x;
	private float y;
	private Timestamp t;

	public Point(float x, float y, Timestamp t) {
		this.x = x;
		this.y = y;
		this.t = t;
	}

	//Time will be the criteria that finaly can help to organize Points
	@Override
	public boolean equals(Object other) {
		boolean result;
	    if((other == null) || (getClass() != other.getClass())){
	        result = false;
	    } // end if
	    else{
	        Point otherPoint = (Point)other;
	        if(this.t == otherPoint.getT())
	        {
	        	result = true;
	        }else{
	        	result = false;
	        }

	    } // end else

	    return result;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s = "x: " + this.x + " y: " + this.y + " t: " + this.t;
		return s;
	}
	
	//We need a way to measure distance between points.
	//*************************Distance Measures****************************
	public float measureSpaceDistance(Point otherPoint)
	{
		float distance = 0;
		
		//Calculate Distance
		distance = (float) Math.sqrt(Math.pow((this.x-otherPoint.getX()),2)+Math.pow((this.y-otherPoint.getY()),2));
		
		return distance;
	}
	
	//Since distances should be simmetric, it returns abs value of the euclidean distance of time points in space
	public float measureTimeDistance(Point otherPoint)
	{
		float distance = 0;
		
		//Calculate Distance
		distance = (float) Math.abs(this.t.getTimestamp().getTime() - otherPoint.t.getTimestamp().getTime());
		
		return distance;
	}

	//*************Getters and Setters*******************
	public float getX() {
		return x;
	}


	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Timestamp getT() {
		return t;
	}

	public void setT(Timestamp t) {
		this.t = t;
	}
}
