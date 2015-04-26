import java.sql.Timestamp;




//Points on Trajectories have d Dimensions
//Lets start with 2 dimensions and Time
//This can obviously extended.
public class Point implements Comparable<Point> {

	private float x;
	private float y;
	private Timestamp t;

	public Point(float x, float y, Timestamp t) {
		this.x = x;
		this.y = y;
		this.t = t;
	}
	
	public Point(Point otherPoint)
	{
		this.x = otherPoint.getX();
		this.y = otherPoint.getY();
		this.t = otherPoint.getT();
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
		distance = (float) Math.abs(this.t.getTime() - otherPoint.t.getTime());
		
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

	@Override
	public int compareTo(Point otherPoint) {
		// TODO Auto-generated method stub
		int comparedByX = Float.compare(this.x, otherPoint.getX());
		return (comparedByX !=0 ? comparedByX : Float.compare(this.y, otherPoint.getY()));
	}

	public static Point undoRotation(Point averagePoint, double rotationAngleRadians ) {
		// TODO Auto-generated method stub
		//Correct Find inverse matrix
		//Rotation Matrix is:			Inverse Matrix is:
		// [cos a	-sin a]				[cos a		sin a]
		// [sin a	cos a ]				[-sin a		cos a]
		double unrotatedStartPointX = Math.cos(rotationAngleRadians) * averagePoint.getX() - Math.sin(rotationAngleRadians) * averagePoint.getY();
		double unrotatedStartPointY = -Math.sin(rotationAngleRadians) * averagePoint.getX() + Math.cos(rotationAngleRadians) * averagePoint.getY();
		Point unrotatedPoint = new Point((float) unrotatedStartPointX, (float) unrotatedStartPointY, averagePoint.getT());
		
		return unrotatedPoint;
	}
}
