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
			if(points.get(i).getT().getTime() >= points.get(i+1).getT().getTime())
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
		
		while(startIndex + length < points.size()) //possible index out of bounds
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
			segmentsFromTrajectory.add(s);
		}
			
		return segmentsFromTrajectory;
	}

	private float calculateMDLRegularTrajectory(int startIndex, int currentIndex) {
		// TODO Auto-generated method stub
		float regularTrajectoryCost=0;
		for(int i = startIndex; i<currentIndex; i++)
		{
			Segment s = new Segment(points.get(i), points.get(i+1));
			regularTrajectoryCost += s.calculateLength();
		}
		
		//becuase it needs to be log2.
		//According to paper, MDLnopar is the MDL of the whole trajectory
		//That is L(H) only, cause L(D|H) is 0.
		//L(H) is the sum of the values.
		regularTrajectoryCost = (float) (Math.log10(regularTrajectoryCost)/Math.log10(2));
		return regularTrajectoryCost;
	}
	
	private float calculateMDLWithCharPoint(int startIndex, int currentIndex) {
		// BAsed in the MDL Principle.
		// The best hypothesis to better explain a optimal trajectory,
		// is the one that maximixes the compression while keeping the maximun number of points
		
		float log2 = (float) Math.log10(2);
		//L(H) is the hypothesis, in this case the hypothetical path using char points
		//This measures conciseness, L(H) increases with the number of partitions
		float hypoteticalPathCost = (float) Math.log10(points.get(startIndex).measureSpaceDistance(points.get(currentIndex)))/log2;
		
		
		float perpendicularDistanceFromTrajectoryToHypotheticalPath = 0;
		for(int i = startIndex; i<currentIndex; i++)
		{
			Segment hypoteticalCharacteristicSegment = new Segment(points.get(startIndex),points.get(currentIndex));
			Segment trajectoryPartialSegment = new Segment(points.get(i),points.get(i+1));
			perpendicularDistanceFromTrajectoryToHypotheticalPath += 
			Segment.calculatePerpendicularDistance(hypoteticalCharacteristicSegment, trajectoryPartialSegment);
		}
		
		float angularDistanceFromTrajectoryToHypotheticalPath = 0;
		for(int i = startIndex; i<currentIndex; i++)
		{
			Segment hypoteticalCharacteristicSegment = new Segment(points.get(startIndex),points.get(currentIndex));
			Segment trajectoryPartialSegment = new Segment(points.get(i),points.get(i+1));
			angularDistanceFromTrajectoryToHypotheticalPath += 
			Segment.calculateAngularDistance(hypoteticalCharacteristicSegment, trajectoryPartialSegment);
		}
		
		//Now L(D|H) measures the distance cost from the actual trajectory given the hypotetical path. 
		//This measures Preciseness, L(D|H) increases as a set of trajectory partitions deviates from the trajectory
		//MDL(L(H)+L(D|H)).
		float distanceCostFromTrajectoryToHypoteticalPath = 0;
		if(perpendicularDistanceFromTrajectoryToHypotheticalPath>0 && angularDistanceFromTrajectoryToHypotheticalPath>0)
		{
			//This should be log2, why log 2?
			//Apparently because this is the scale (log2) to measure smallest bit size
		
		distanceCostFromTrajectoryToHypoteticalPath =
				(float) (Math.log10(perpendicularDistanceFromTrajectoryToHypotheticalPath)/log2
		+ Math.log10(angularDistanceFromTrajectoryToHypotheticalPath)/log2) //This 2 is L(D|H)
		+ hypoteticalPathCost; //This is L(H)
		}else{
			distanceCostFromTrajectoryToHypoteticalPath = hypoteticalPathCost;
		}
		
		return distanceCostFromTrajectoryToHypoteticalPath;
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
