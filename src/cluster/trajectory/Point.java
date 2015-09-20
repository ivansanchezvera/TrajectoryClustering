package cluster.trajectory;
import geo.openmap.Ellipsoid;
import geo.openmap.LatLonPoint;
import geo.openmap.UTMPoint;

import java.sql.Timestamp;




//Points on Trajectories have d Dimensions
//Lets start with 2 dimensions and Time
//This can obviously extended.
public class Point implements Comparable<Point> {

	private float x;
	private float y;
	private Timestamp t;
	
	//Only for stupid UTM points, damn you Starkey dataset
	private boolean isUTM;
	private int utmZone;
	private boolean northing;

	public Point(float x, float y, Timestamp t) {
		this.x = x;
		this.y = y;
		this.t = t;
		
		//Stupid default values, fix this, make it elegant
		this.isUTM = false;
		this.utmZone = -1;
		this.northing = true;
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
		String s = printVerbose();
		return s;
	}
	
	public String printVerbose() {
		// TODO Auto-generated method stub
		String s = "x: " + this.x + " y: " + this.y + " t: " + this.t;
		return s;
	}
	
	public String printToPlotOnMap() {
		// TODO Auto-generated method stub
		
		//This one was for plotting in map
		String s = this.y + "," + this.x + "|";
		return s;
	}
	
	public String printToPlot() {
		// TODO Auto-generated method stub
		String s = this.x + "," + this.y;
		return s;
	}
	
	public String printToPlotUTM() {
		// TODO Auto-generated method stub
		String s = this.y + "," + this.x + ",11";
		return s;
	}
	
	public String printToPlotUTMToCoordinates() {
		// TODO Auto-generated method stub
		
		Point pInCoordinates = Point.convertUTMToCoordinates(this);
		double lat = pInCoordinates.getY();
		double lon = pInCoordinates.getX();
		
		String s = lat + "," + lon;
		return s;
	}
	
	public static Point convertUTMToCoordinates(Point p)
	{
		float lat;
		float lon;
		
		LatLonPoint llPoint = UTMPoint.UTMtoLL(Ellipsoid.WGS_84, p.getY(), p.getX(), p.getUtmZone(), p.isNorthing(), null);
		
		Point pToReturn = new Point(llPoint.getLongitude(), llPoint.getLatitude(), p.getT());
		return pToReturn;
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

	public boolean isUTM() {
		return isUTM;
	}

	public void setUTM(boolean isUTM) {
		this.isUTM = isUTM;
	}

	public int getUtmZone() {
		return utmZone;
	}

	public void setUtmZone(int utmZone) {
		this.utmZone = utmZone;
	}

	public boolean isNorthing() {
		return northing;
	}

	public void setNorthing(boolean northing) {
		this.northing = northing;
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
