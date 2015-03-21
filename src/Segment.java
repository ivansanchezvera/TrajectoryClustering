
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
}
