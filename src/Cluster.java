import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class Cluster {

	private int clusterID;
	private String clusterName;
	private Trajectory representativeTrajectory;
	private int clusterSize;
	private ArrayList<Segment> segments;
	int cardinality;
	HashSet<Integer> parentTrajectories;
	
	public Cluster(int clusterID, String clusterName) {
		this.clusterID = clusterID;
		parentTrajectories = new HashSet<Integer>();
		segments = new ArrayList<Segment>();
		clusterSize = 0;
	}
	
	public void addSegment(Segment s)
	{
		segments.add(s);
		clusterSize = segments.size();
	}
	
	public Trajectory calculateRepresentativeTrajectory(int minLines, double smoothingParameter)
	{
		Trajectory representativeTrajectory = null;
		//do stuff
		
		//1. Compute average direction of vector V
		float tempX = 0;
		float tempY = 0;
		
		//Fix this to include Time dimension
		Timestamp minTime = null;
		Timestamp maxTime = null;
		
		for(Segment s: segments)
		{
			//**************This seems to be wrong, verify this is the way to add vectors
			tempX += (s.getEndPoint().getX() - s.getStartPoint().getX());
			tempY += (s.getEndPoint().getY() - s.getStartPoint().getY());
			//tempX += Math.abs(s.getEndPoint().getX() - s.getStartPoint().getX());
			//tempY += Math.abs(s.getEndPoint().getY() - s.getStartPoint().getY());
			
			//to have a time component
			if(minTime==null)
			{
				minTime= s.getStartPoint().getT();
			}else if(s.getStartPoint().getT().compareTo(minTime)<0){
				minTime = s.getStartPoint().getT();
			}
			
			if(maxTime==null)
			{
				maxTime= s.getEndPoint().getT();
			}else if(s.getEndPoint().getT().compareTo(maxTime)>0){
				maxTime = s.getEndPoint().getT();
			}
			
		}
		int vectorCardinality = segments.size();
		float averageDirectionVectorX = (tempX+(segments.get(0).getStartPoint().getX()))/vectorCardinality; 
		float averageDirectionVectorY = (tempY+(segments.get(0).getStartPoint().getY()))/vectorCardinality;
		
		Point averageDirectionEndPoint = new Point(averageDirectionVectorX, averageDirectionVectorY, maxTime);
		//Vector does not have to start in the origin
		//Point averageDirectionStartPoint = new Point(0,0, minTime);
		Point averageDirectionStartPoint = new Point(segments.get(0).getStartPoint());
		
		Segment averageDirectionVector = new Segment(averageDirectionStartPoint, averageDirectionEndPoint);
		
		//2. Rotate Axes so x is parallel to V
		// http://mathworld.wolfram.com/UnitVector.html
		Segment unitVector = new Segment(new Point(0, 0, minTime), new Point(1, 0, minTime));
		float dotProduct = averageDirectionVector.calculateDotProduct(unitVector);
		
		// http://answers.unity3d.com/questions/317648/angle-between-two-vectors.html
		float cosineRotationAngle = dotProduct/(unitVector.calculateLength()*averageDirectionVector.calculateLength());
		double rotationAngleRadians = Math.acos(cosineRotationAngle);
		
		
		//float rotatedX = Math.cos(rotationAngleRadians) * 
		
		//calculate the starting and ending points by the coordinate of the rotated axis
		//*****************This method is inverting the points somehow
		ArrayList<Segment> rotatedSegments = new ArrayList<Segment>();
		ArrayList<Point> rotatedPoints = new ArrayList<Point>();
		for(Segment s: segments)
		{
			Segment tempSegment = new Segment(s);
			tempSegment.rotatePointsCoordinates(rotationAngleRadians);
			rotatedSegments.add(tempSegment);
			
			//Extra list of point to sort it
			rotatedPoints.add(tempSegment.getStartPoint());
			rotatedPoints.add(tempSegment.getEndPoint());
		}
		
		//sort the starting and ending points by the coordinate of the rotated axis (in this case X)
		Collections.sort(rotatedPoints);
		
		ArrayList<Point> averageTrajectoryPoints = new ArrayList<Point>(); 
		
		//Lets work on the trotated points
		int rotatedPointsProccessed = 0;
		for(Point p: rotatedPoints)
		{
			ArrayList<Segment> traversedSegments = new ArrayList<Segment>();
			//Count the line segments that contain the point p
			//This for can obviously be optimized by discarding segments that are way too far
			int numSegmentsTraversedInX = 0;
			for(Segment s: rotatedSegments)
			{
				//This can be optimized since segments are ordered
				if(s.SegmentTraverseThisXCoordinate(p.getX()))
				{
					traversedSegments.add(s);
					numSegmentsTraversedInX++;
				}
			}
			
			if(numSegmentsTraversedInX >= minLines)
			{
				double differenceWithPreviousPoint = 0;
				if(rotatedPointsProccessed>0){
					differenceWithPreviousPoint = p.getX() - rotatedPoints.get(rotatedPointsProccessed-1).getX();
				}else{
					differenceWithPreviousPoint = smoothingParameter;
				}
					if(differenceWithPreviousPoint>= smoothingParameter)
					{
						float averagePointYCoordinate = 0;
						
						for(Segment sTraversed:traversedSegments)
						{
							averagePointYCoordinate = sTraversed.getYCoodinateInX(p.getX()); 
						}
						
						averagePointYCoordinate = averagePointYCoordinate/traversedSegments.size();
						
						Point averagePointRotated = new Point(p.getX(), averagePointYCoordinate, p.getT());
						
						//undo rotation
						//first multiply by inverse and then 
						//new cordinate vector = rotation matrix . real coordinate vector
						//new coordinate vector * rotation matrix inverted = real coordinate vector
						Point averagePoint = Point.undoRotation(averagePointRotated, rotationAngleRadians);
						averageTrajectoryPoints.add(averagePoint);
					
				}
			}
			
			rotatedPointsProccessed++;
		}
		
		representativeTrajectory = new Trajectory(clusterID, averageTrajectoryPoints);
		this.representativeTrajectory = representativeTrajectory;
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

	@Override
	public String toString() {
		return "Cluster [clusterID=" + clusterID + ", clusterName="
				+ clusterName + ", representativeTrajectory="
				+ representativeTrajectory  + ", size="
						+ clusterSize + ", segments=" + segments
				+ ", cardinality=" + cardinality + ", parentTrajectories="
				+ parentTrajectories + "]";
	}
}
