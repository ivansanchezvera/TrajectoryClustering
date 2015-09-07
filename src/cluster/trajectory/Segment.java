package cluster.trajectory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Segment extends cluster.trajectory.Clusterable {

	private Point startPoint;
	private Point endPoint;
	
	private float length; 
	private boolean classified;
	private boolean isNoise;
	private int parentTrajectory;
	
	//To adapt the douglas-pecker implementation
	private double sxey;
	private double exsy;
    private double dx;
    private double dy;
    
    //To compare objects between clusters
    private int id;
    
	//Put time Info or Direction Info.
	//What about precalculated distance, maybe not.
	
	
	public Segment(Point startPoint, Point endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		classified = false;
		
		//Precalculate Distances for Partitions
		//Specially for Douglas Pecker
		precalculateDistances(startPoint, endPoint);
		// TODO Auto-generated constructor stub
	}
	
	public Segment(int id, Point startPoint, Point endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		classified = false;
		
		//Precalculate Distances for Partitions
		//Specially for Douglas Pecker
		precalculateDistances(startPoint, endPoint);
		// TODO Auto-generated constructor stub
		this.id = id;
	}


	
	public Segment(Segment otherSegment)
	{
		this.startPoint = new Point(otherSegment.getStartPoint());
		this.endPoint = new Point(otherSegment.getEndPoint());
		this.classified = otherSegment.classified;
		this.isNoise = otherSegment.isNoise;
		this.parentTrajectory = otherSegment.parentTrajectory;

		//Precalculate Distances for Partitions
		//Specially for Douglas Pecker
		precalculateDistances(startPoint, endPoint);
		
	}
	
	public Object clone()
	{
		Segment s = new Segment(this);
		return s;
	}
	
	/**
	 * @param startPoint
	 * @param endPoint
	 */
	private void precalculateDistances(Point startPoint, Point endPoint) {
		sxey = startPoint.getX() * endPoint.getY();
        exsy = endPoint.getX() * startPoint.getY();
        dx = startPoint.getX() - endPoint.getX();
        dy = startPoint.getY() - endPoint.getY();
        length = calculateLength();
	}
	
	public float calculateDotProduct(Segment otherSegment)
	{
		float dotProduct;
		dotProduct = (this.getEndPoint().getX()-this.startPoint.getX())
				*(otherSegment.getEndPoint().getX()-otherSegment.getStartPoint().getX())
				+(this.getEndPoint().getY()-this.startPoint.getY())
				*(otherSegment.getEndPoint().getY()-otherSegment.getStartPoint().getY());
		return dotProduct;
	}
	
	public float calculateLength()
	{
		this.length = Math.abs(startPoint.measureSpaceDistance(endPoint));
		return length;		
	}

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	public Point getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public boolean isClassified() {
		return classified;
	}

	public void setClassified(boolean classified) {
		this.classified = classified;
	}

	public boolean isNoise() {
		return isNoise;
	}

	public void setNoise(boolean isNoise) {
		this.isNoise = isNoise;
	}

	public Integer getParentTrajectory() {
		// TODO Auto-generated method stub
		return parentTrajectory;
	}
	
	public void setParentTrajectory(int parentTrajectory)
	{
		this.parentTrajectory = parentTrajectory;
	}	
	
	public static float calculateAngularDistance(Segment s1, Segment s2) 
	{
		// TODO Auto-generated method stub
		float angularDistance = 0;
		
		//Assuming Trajectories with direction
		Segment newTrajectorySegment = s1;
		Segment originalTrajectorySegment = s2;
		
		float cosAngle = 
				(originalTrajectorySegment.calculateDotProduct(newTrajectorySegment))/
				(newTrajectorySegment.calculateLength()*originalTrajectorySegment.calculateLength());
		
		//Recheck this, why do I need it?
		if(cosAngle!=1)
		{
		cosAngle = Math.nextDown(cosAngle);
		}
		
		//Verify that a negative cosine means trajectories go in opposite direction
		if(cosAngle<0)
		{
			angularDistance = s2.calculateLength();
		}else{
			float sinAngle = (float) (1 - Math.pow(cosAngle,2));
			angularDistance = sinAngle * s2.calculateLength(); 
		}
		
		return angularDistance;
	}

	public static float calculatePerpendicularDistance(Segment s1, Segment s2) {
		// TODO Auto-generated method stub
		float perpendicularDistance = 0;
		
		//Optional
		Segment newTrajectorySegment = s1;
		Segment originalTrajectorySegment = s2;
		Segment mainSegment;
		Segment secondarySegment;
		
		if(originalTrajectorySegment.calculateLength()>=newTrajectorySegment.calculateLength())
		{
			mainSegment = originalTrajectorySegment;
			secondarySegment = newTrajectorySegment;
		}else
		{
			mainSegment = newTrajectorySegment;
			secondarySegment = originalTrajectorySegment;
		}
		
		Point projectionPointStart = findProjectionPoint(mainSegment.getStartPoint(), mainSegment.getEndPoint(), secondarySegment.getStartPoint());
		Point projectionPointEnd = findProjectionPoint(mainSegment.getStartPoint(), mainSegment.getEndPoint(), secondarySegment.getEndPoint());
		
		float perpendicularDistanceStart = Math.abs(projectionPointStart.measureSpaceDistance(secondarySegment.getStartPoint()));
		float perpendicularDistanceEnd = Math.abs(projectionPointEnd.measureSpaceDistance(secondarySegment.getEndPoint()));
		
		if(perpendicularDistanceStart + perpendicularDistanceEnd>0)
		{
		perpendicularDistance = (float) ((Math.pow(perpendicularDistanceStart, 2) + Math.pow(perpendicularDistanceEnd, 2))/(perpendicularDistanceStart + perpendicularDistanceEnd));
		}
		
		return perpendicularDistance;
	}
	
	public static float calculateParallelDistance(Segment s1, Segment s2) {
		// TODO Auto-generated method stub
		float parallelDistance = 0;
		
		//Optional
		Segment newTrajectorySegment = s1;
		Segment originalTrajectorySegment = s2;
		Segment mainSegment;
		Segment secondarySegment;
		
		if(originalTrajectorySegment.calculateLength()>=newTrajectorySegment.calculateLength())
		{
			mainSegment = originalTrajectorySegment;
			secondarySegment = newTrajectorySegment;
		}else
		{
			mainSegment = newTrajectorySegment;
			secondarySegment = originalTrajectorySegment;
		}
		
		Point projectionPointStart = findProjectionPoint(mainSegment.getStartPoint(), mainSegment.getEndPoint(), secondarySegment.getStartPoint());
		Point projectionPointEnd = findProjectionPoint(mainSegment.getStartPoint(), mainSegment.getEndPoint(), secondarySegment.getEndPoint());
		
		float projectionDistanceStart = Math.min(projectionPointStart.measureSpaceDistance(mainSegment.getStartPoint()), projectionPointStart.measureSpaceDistance(mainSegment.getEndPoint()));
		float projectionDistanceEnd = Math.min(projectionPointEnd.measureSpaceDistance(mainSegment.getStartPoint()), projectionPointEnd.measureSpaceDistance(mainSegment.getEndPoint()));
		parallelDistance = (float) Math.min(projectionDistanceStart,projectionDistanceEnd);
		
		return parallelDistance;
	}

	public static float calculateDistance(Segment s1, Segment s2)
	{
		float distance = -1;
		
		distance = Segment.calculatePerpendicularDistance(s1, s2) + 
				Segment.calculateParallelDistance(s1, s2) + 
				Segment.calculateAngularDistance(s1, s2);
				
		return distance;
		
	}

	private static Point findProjectionPoint(Point start, Point end, Point pointToProject)
	{
		boolean isValidPoint = false;
		Point projectedPoint = null;
		
		float u = (pointToProject.getX() - start.getX()) * (end.getX() - start.getX()) + (pointToProject.getY() - start.getY()) * (end.getY() - start.getY()); 
		
		float udenom = (float) (Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2));
		
		u = u/udenom;
		
		float projectedPointXVal = start.getX() + (u * (end.getX() - start.getX()));
		float projectedPointYVal = start.getY() + (u * (end.getY() - start.getY()));
		
		projectedPoint = new Point(projectedPointXVal, projectedPointYVal, null);
		
		return projectedPoint;
		
	}
	
	public void rotatePointsCoordinates(double rotationAngleRadians)
	{
		//http://en.wikipedia.org/wiki/Rotation_matrix
		double rotatedStartPointX = Math.cos(rotationAngleRadians) * this.getStartPoint().getX() - Math.sin(rotationAngleRadians) * this.getStartPoint().getY();
		double rotatedStartPointY = +Math.sin(rotationAngleRadians) * this.getStartPoint().getX() + Math.cos(rotationAngleRadians) * this.getStartPoint().getY();
		Point rotatedStartPoint = new Point((float) rotatedStartPointX, (float) rotatedStartPointY, this.getStartPoint().getT());
		
		double rotatedEndPointX = Math.cos(rotationAngleRadians) * this.getEndPoint().getX() - Math.sin(rotationAngleRadians) * this.getEndPoint().getY();
		double rotatedEndPointY = +Math.sin(rotationAngleRadians) * this.getEndPoint().getX() + Math.cos(rotationAngleRadians) * this.getEndPoint().getY();
		Point rotatedEndPoint = new Point((float) rotatedEndPointX, (float) rotatedEndPointY, this.getEndPoint().getT());
		
		this.startPoint = rotatedStartPoint;
		this.endPoint = rotatedEndPoint;
	}
	//It would be nice to implement clone
	//http://books.google.com.au/books?id=ka2VUBqHiWkC&pg=PA55&lpg=PA55&dq=effective%2Bjava%2Bclone&source=bl&ots=yXGhLnv4O4&sig=zvEip5tp5KGgwqO1sCWgtGyJ1Ns&hl=en&ei=CYANSqygK8jktgfM-JGcCA&sa=X&oi=book_result&ct=result&redir_esc=y#v=onepage&q=effective%2Bjava%2Bclone&f=false

	public boolean SegmentTraverseThisXCoordinate(float x) {
		// TODO Auto-generated method stub
		boolean segmentIsTraversedByPointInX = false;
		
		if(!(x>this.getEndPoint().getX() && x>this.getStartPoint().getX()))
		{
			if(!(x<this.getEndPoint().getX()&&x<this.getStartPoint().getX()))
			{
				segmentIsTraversedByPointInX = true;
			}
		}
		
		return segmentIsTraversedByPointInX;
	}

	//Fix this method to support vertical segments (undefined slope)
	//http://www.mathopenref.com/coordslope.html
	public float getYCoodinateInX(float x) {
		//http://www.math.washington.edu/~king/coursedir/m445w04/notes/vector/equations.html
		//http://www.mathopenref.com/coordslope.html
		float y = 0;
		
		//Slope m 
		// m = (Ay - By)/(Ax-By)
		float slope;
		if(this.getEndPoint().getX()-this.getStartPoint().getX()!=0)
		{
		slope = (this.getEndPoint().getY()-this.getStartPoint().getY())/(this.getEndPoint().getX()-this.getStartPoint().getX());
		}else{
			slope = Float.POSITIVE_INFINITY;
		}
		//intercept b
		//b = y - mx
		float intercept =  this.getEndPoint().getY() - slope * this.getEndPoint().getX();
		
		//y = mx +b;
		y = slope * x + intercept;
		return y;
	}

	@Override
	public String toString() {
		return "Segment [startPoint=" + startPoint + ", endPoint=" + endPoint
				+ ", length=" + length + ", parentTrajectory="
				+ parentTrajectory + "]";
	}

	/**
	 * Helps to calculate the perpendicular distance from a segment
	 * to a given Point. this is ultra useful for Douglas - Peucker Algorithm, to determine
	 * if the distance to a point is greater than a given threshold.
	 * @param p
	 * @return
	 */
	public double perpedicularDistanceToPoint(Point p) {
		// TODO Auto-generated method stub

		return Math.abs(dy * p.getX() - dx * p.getY() + sxey - exsy) / length;
	}
	
    public List<Point> asList() {
        return Arrays.asList(startPoint, endPoint);
    }
	

}
