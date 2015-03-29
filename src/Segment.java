
public class Segment {

	private Point startPoint;
	private Point endPoint;
	
	private float length; 
	private boolean classified;
	private boolean isNoise;
	private int parentTrajectory;
	//Put time Info or Direction Info.
	//What about precalculated distance, maybe not.
	
	
	public Segment(Point startPoint, Point endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		classified = false;
		// TODO Auto-generated constructor stub
	}
	
	public float calculateDotProduct(Segment otherSegment)
	{
		float dotProduct;
		dotProduct = Math.abs(this.getEndPoint().getX()-this.startPoint.getX())
				*Math.abs(otherSegment.getEndPoint().getX()-otherSegment.getStartPoint().getX())
				+Math.abs(this.getEndPoint().getY()-this.startPoint.getY())
				*Math.abs(otherSegment.getEndPoint().getY()-otherSegment.getStartPoint().getY());
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

}
