import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class Trajectory {

	//What composes a Trajectory
	//A series of Points
	private int trajectoryId;
	private ArrayList<Point> points;
	private boolean validTrajectory;
	private float MDLPrecision;
	
	private float log2Value;
	float precisionRegularizer;
	
	public Trajectory(int trajectoryId, ArrayList<Point> points) {

		this.trajectoryId = trajectoryId;
		this.points = points;
		
		validTrajectory = validateTrajectory();
		//Validate trajectory, all points should be sequential in time
		
		MDLPrecision = 1;
		
		calculateCommonLogValues();
	}
	
	private void calculateCommonLogValues()
	{
		log2Value = (float) Math.log10(2);
		precisionRegularizer = (float) Math.log10(MDLPrecision)/log2Value;
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
		
		//Calculate Common log values at this point just to make sure they are correct
		//This is an optimization to not calculate this in each step of the loop
		calculateCommonLogValues();
		
		
		//Add first Point to list of characteristic points
		characteristicPoints.add(points.get(0));
		
		int startIndex = 0;
		int length = 1;
		int indexForDebug=0;
		while(startIndex + length < points.size()) //possible index out of bounds
		{
			int currentIndex = startIndex + length;
			float costAddCurrentToCharPoints = calculateMDLWithCharPoint(startIndex,currentIndex);
			float costKeepTrajectoryPath = calculateMDLRegularTrajectory(startIndex,currentIndex);
			
			System.out.println("In iteration " + indexForDebug + " the values are:  current index: " + currentIndex + " lenght: " + length + " start index: " + startIndex);
			
			if(costAddCurrentToCharPoints > costKeepTrajectoryPath)
			{
				if(currentIndex-1 > 0)
				{
				characteristicPoints.add(points.get(currentIndex-1));
				System.out.println("ArrayList number of elements: " + characteristicPoints.size());
				//System.out.println("ArrayList size: " + characteristicPoints.toArray().length);
				startIndex = currentIndex - 1;
				length = 1;
				}else{
					length = length+1;
				}
				
			}else{
				System.out.println("Cost of char points route is less or equal to cost of keeping trajectory: " + costAddCurrentToCharPoints + " <= " + costKeepTrajectoryPath);
				length = length+1;
			}
			indexForDebug++;
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
		
		//here do the precision adjustment
			
		//becuase it needs to be log2.
		//According to paper, MDLnopar is the MDL of the whole trajectory
		//That is L(H) only, cause L(D|H) is 0.
		//L(H) is the sum of the values.
		regularTrajectoryCost = (float) (Math.log10(regularTrajectoryCost)/log2Value) - precisionRegularizer;
		
		//here do the precision adjustment
		
		return regularTrajectoryCost;
	}
	
	private float calculateMDLWithCharPoint(int startIndex, int currentIndex) {
		// BAsed in the MDL Principle.
		// The best hypothesis to better explain a optimal trajectory,
		// is the one that maximixes the compression while keeping the maximun number of points
		

		//L(H) is the hypothesis, in this case the hypothetical path using char points
		//This measures conciseness, L(H) increases with the number of partitions
		float euclideanDistanceBetweenPoints = points.get(startIndex).measureSpaceDistance(points.get(currentIndex));
		float hypoteticalPathCost = (float) Math.log10(euclideanDistanceBetweenPoints)/log2Value - precisionRegularizer;
		
		
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
		
		//Now L(D|H) measures the distance cost from the actual trajectory given the hypothetical path. 
		//This measures Preciseness, L(D|H) increases as a set of trajectory partitions deviates from the trajectory
		//MDL(L(H)+L(D|H)).
		float distanceCostFromTrajectoryToHypoteticalPath = 0;
		if(perpendicularDistanceFromTrajectoryToHypotheticalPath>0 && angularDistanceFromTrajectoryToHypotheticalPath>0)
		{
			//This should be log2, why log 2?
			//Apparently because this is the scale (log2) to measure smallest bit size
			//Because log2 gives the lenght in bits of the hypothesis, thanks Youhan XIA.
		
		distanceCostFromTrajectoryToHypoteticalPath =
				(float) ((Math.log10(perpendicularDistanceFromTrajectoryToHypotheticalPath)/log2Value - precisionRegularizer)
		+ (Math.log10(angularDistanceFromTrajectoryToHypotheticalPath)/log2Value) - precisionRegularizer) //This 2 is L(D|H)
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

	public float getMDLPrecision() {
		return MDLPrecision;
	}

	public void setMDLPrecision(float mDLPrecision) {
		MDLPrecision = mDLPrecision;
	}
}
