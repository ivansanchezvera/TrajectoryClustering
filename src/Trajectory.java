import java.util.ArrayList;


public class Trajectory {

	//What composes a Trajectory
	//A series of Points
	private int trajectoryId;
	private ArrayList<Point> points;
	private boolean validTrajectory;
	
	public Trajectory(int trajectoryId, ArrayList<Point> points) {

		this.trajectoryId = trajectoryId;
		this.points = points;
		
		validTrajectory = validateTrajectory();
		//Validate trajectory, all points should be sequential in time
		
	}
	//Extra methods to add or remove points in trajectory?
	
	//Method to calculate total time in trajectory
	//something like, if trajectory has this duration plus start time and end time
	//are this time distance appart, then consider them suitable
	
	//Makes sure trajectory points are in order
	private boolean validateTrajectory() {
		// TODO Auto-generated method stub
		boolean validTrajectory = true;
		for(int i = 0; i<points.size()-1; i++)
		{
			if(points.get(i).getT().getTimestamp().getTime() >= points.get(i+1).getT().getTimestamp().getTime())
			{
				validTrajectory = false;
				break;
			}
		
		}
		return validTrajectory;
	}

	public ArrayList<Segment> divideTrajectoryInSegments()
	{
		ArrayList<Segment> segmentsFromTrajectory = new ArrayList<Segment>();
		
		//Now here divide Trajectory into segments
		//So segments are just a set of sequential Characteristic points
		ArrayList<Point> characteristicPoints = new ArrayList<Point>();
		
		//Add first Point to list of characteristic points
		characteristicPoints.add(points.get(0));
		
		int startIndex = 0;
		int length = 1;
		
		while(startIndex + length <= points.size())
		{
			int currentIndex = startIndex + length;
			float costAddCurrentToCharPoints = calculateMDLWithCharPoint(startIndex,currentIndex);
			float costKeepTrajectoryPath = calculateMDLRegularTrajectory(startIndex,currentIndex);
			
			if(costAddCurrentToCharPoints > costKeepTrajectoryPath)
			{
				characteristicPoints.add(points.get(currentIndex-1));
				startIndex = currentIndex - 1;
				length = 1;
			}else{
				length = length+1;
			}
		}
		
		//Add Final point to list of Characteristic Points
		characteristicPoints.add(points.get(points.size()-1));
		
		//Create segments from Characteristic Points
		for(int j=0; j<characteristicPoints.size()-1; j++)
		{
			Segment s = new Segment(characteristicPoints.get(j), characteristicPoints.get(j+1));
			s.setParentTrajectory(this.trajectoryId);
		}
			
		return segmentsFromTrajectory;
	}

	private float calculateMDLRegularTrajectory(int startIndex, int currentIndex) {
		// TODO Auto-generated method stub
		float regularTrajectoryCost=0;
		for(int i = startIndex; i<currentIndex-1; i++)
		{
			Segment s = new Segment(points.get(i), points.get(i+1));
			regularTrajectoryCost =+ s.getLength();
		}
		
		return regularTrajectoryCost;
	}
	private float calculateMDLWithCharPoint(int startIndex, int currentIndex) {
		// BAsed in the MDL Principle.
		// The best hypothesis to better explain a optimal trajectory,
		// is the one that maximixes the compression while keeping the maximun number of points
		
		//L(H) is the hypothesis, in this case the hypothetical path using char points
		//This measures conciseness, L(H) increases with the number of partitions
		float hypoteticalPathCost = (float) Math.log(points.get(startIndex).measureSpaceDistance(points.get(currentIndex)));
		
		float perpendicularDistanceFromTrajectoryToHypotheticalPath = 0;
		for(int i = startIndex; i<currentIndex-1; i++)
		{
			perpendicularDistanceFromTrajectoryToHypotheticalPath =+ calculatePerpendicularDistance(points.get(startIndex),points.get(currentIndex),points.get(i),points.get(i++));
		}
		
		float angularDistanceFromTrajectoryToHypotheticalPath = 0;
		for(int i = startIndex; i<currentIndex-1; i++)
		{
			angularDistanceFromTrajectoryToHypotheticalPath =+ calculateAngularDistance(points.get(startIndex),points.get(currentIndex),points.get(i),points.get(i++));
		}
		
		//Now L(D|H) measures the distance cost from the actual trajectory given the hypotetical path. 
		//This measures Preciseness, L(D|H) increases as a set of trajectory partitions deviates from the trajectory
		float distanceCostFromTrajectoryToHypoteticalPath =
				(float) (Math.log(perpendicularDistanceFromTrajectoryToHypotheticalPath)
		+ Math.log(angularDistanceFromTrajectoryToHypotheticalPath));
		
		return distanceCostFromTrajectoryToHypoteticalPath;
	}
	
	private float calculateAngularDistance(Point hPoint1, Point hPoint2, Point truePoint1, Point truePoint2) 
	{
		// TODO Auto-generated method stub
		float angularDistance = 0;
		
		//Assuming Trajectories with direction
		Segment newTrajectorySegment = new Segment(hPoint1, hPoint2);
		Segment originalTrajectorySegment = new Segment(truePoint1, truePoint2);
		
		float cosAngle = 
				(originalTrajectorySegment.calculateDotProduct(newTrajectorySegment))/
				(newTrajectorySegment.calculateLength()*originalTrajectorySegment.calculateLength());
		
		
		return angularDistance;
	}

	private float calculatePerpendicularDistance(Point hPoint1, Point hPoint2, Point truePoint1, Point truePoint2) {
		// TODO Auto-generated method stub
		float perpendicularDistance = 0;
		
		//Optional
		Segment newTrajectorySegment = new Segment(hPoint1, hPoint2);
		Segment originalTrajectorySegment = new Segment(truePoint1, truePoint2);
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
		
		Point projectionPointStart = findPerpendicularPoint(mainSegment.getStartPoint(), mainSegment.getEndPoint(), secondarySegment.getStartPoint());
		Point projectionPointEnd = findPerpendicularPoint(mainSegment.getStartPoint(), mainSegment.getEndPoint(), secondarySegment.getEndPoint());
		
		float perpendicularDistanceStart = Math.abs(projectionPointStart.measureSpaceDistance(secondarySegment.getStartPoint()));
		float perpendicularDistanceEnd = Math.abs(projectionPointEnd.measureSpaceDistance(secondarySegment.getEndPoint()));
		
		perpendicularDistance = (float) ((Math.pow(perpendicularDistanceStart, 2) + Math.pow(perpendicularDistanceEnd, 2))/(perpendicularDistanceStart + perpendicularDistanceEnd));
		
		return perpendicularDistance;
	}

	private Point findPerpendicularPoint(Point start, Point end, Point pointToProject)
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

	public int getTrajectoryId() {
		return trajectoryId;
	}

	public void setTrajectoryId(int trajectoryId) {
		this.trajectoryId = trajectoryId;
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public boolean isValidTrajectory() {
		return validTrajectory;
	}

	public void setValidTrajectory(boolean validTrajectory) {
		this.validTrajectory = validTrajectory;
	}

	
	
}
