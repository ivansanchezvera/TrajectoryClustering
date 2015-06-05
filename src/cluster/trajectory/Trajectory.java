package cluster.trajectory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fastdtw.com.dtw.DTW;
import fastdtw.com.timeseries.TimeSeries;
import fastdtw.com.util.DistanceFunction;
import fastdtw.com.util.DistanceFunctionFactory;

import com.stromberglabs.cluster.Clusterable;

public class Trajectory implements Clusterable{

	//What composes a Trajectory
	//A series of Points
	private int trajectoryId;
	private String trajectoryUser;
	private ArrayList<Point> points;
	private boolean validTrajectory;
	private float MDLPrecision;
	private boolean classified;
	private boolean isNoise;
	
	private double dtwAverage;
	
	private float log2Value;
	float precisionRegularizer;
	
	private int clusterIdPreLabel;
	
	public Trajectory(int trajectoryId, ArrayList<Point> points) {

		this.trajectoryId = trajectoryId;
		//this.trajectoryUser = Integer.toString(trajectoryId);
		this.points = points;
		
		//validTrajectory = validateTrajectory();
		
		//cause of new trajectories with no time data, we cannot validate the trajectory
		validTrajectory = true;
		
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

	public ArrayList<Segment> divideTrajectoryInSegmentsTraclus()
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
			
			//System.out.println("In iteration " + indexForDebug + " the values are:  current index: " + currentIndex + " lenght: " + length + " start index: " + startIndex);
			
			if(costAddCurrentToCharPoints > costKeepTrajectoryPath)
			{
				if(currentIndex-1 > 0)
				{
				characteristicPoints.add(points.get(currentIndex-1));
				//System.out.println("ArrayList number of elements: " + characteristicPoints.size());
				//System.out.println("ArrayList size: " + characteristicPoints.toArray().length);
				startIndex = currentIndex - 1;
				length = 1;
				}else{
					length = length+1;
				}
				
			}else{
				//System.out.println("Cost of char points route is less or equal to cost of keeping trajectory: " + costAddCurrentToCharPoints + " <= " + costKeepTrajectoryPath);
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

	public ArrayList<Segment> divideTrajectoryInSegmentsDouglasPeucker(double epsilon, int fixNumberOfPoints)
	{
		ArrayList<Point> characteristicPointsFromTrajectory = new ArrayList<Point>();
		//If we are not asking for more points than what we actually have
		if(!(fixNumberOfPoints>=points.size()))
		{
			characteristicPointsFromTrajectory = findCharacteristicPointsDouglasPeucker(points, epsilon, fixNumberOfPoints);
		}else{
			characteristicPointsFromTrajectory = points;
		}
			//Now compose segments from characteristic points obtain with Douglas-Peucker Algorithm
			ArrayList<Segment> characteristicSegmentsFromTrajectory = new ArrayList<Segment>();
			for(int w=0; w<characteristicPointsFromTrajectory.size()-1; w++)
			{
				Point currentPoint = characteristicPointsFromTrajectory.get(w);
				Point nextPoint = characteristicPointsFromTrajectory.get(w+1);
				Segment s = new Segment(currentPoint, nextPoint);
				s.setParentTrajectory(this.trajectoryId);
				characteristicSegmentsFromTrajectory.add(s);
			}
			
			return characteristicSegmentsFromTrajectory;
	}
	
	public Trajectory simplifyTrajectoryDouglasPeucker(double epsilon, int fixNumberOfPoints)
	{
		ArrayList<Point> characteristicPointsFromTrajectory = new ArrayList<Point>();
		//If we are not asking for more points than what we actually have
		if(!(fixNumberOfPoints>=points.size()))
		{
			characteristicPointsFromTrajectory = findCharacteristicPointsDouglasPeucker(points, epsilon, fixNumberOfPoints);
		}else{
			characteristicPointsFromTrajectory = points;
		}
		
		Trajectory simplifiedTrajectory = new Trajectory(getTrajectoryId(), characteristicPointsFromTrajectory);
		return simplifiedTrajectory;
	}
	
	/**
	 * Douglas Peucker implementation taken from: https://github.com/LukaszWiktor/series-reducer
	 * This divides a trajectory into a given number of points (when in parametric mode),
	 * 
	 * @param points = this is the points that conform the original trajectory
	 * @param epsilon = this is the cost function, the threshold distance to consider a new partition
	 * @return Points that conform the new simplified trajectory
	 */
	public ArrayList<Point> findCharacteristicPointsDouglasPeucker(List<Point> points, double epsilon, int minPoints)
	{
		 if (epsilon < 0) {
	            throw new IllegalArgumentException("Epsilon cannot be less then 0.");
	        }
	        double furthestPointDistance = 0.0;
	        int furthestPointIndex = 0;
	        Segment line = new Segment(points.get(0), points.get(points.size() - 1));
	        for (int i = 1; i < points.size() - 1; i++) {
	            double distance = line.perpedicularDistanceToPoint(points.get(i));
	            if (distance > furthestPointDistance ) {
	                furthestPointDistance = distance;
	                furthestPointIndex = i;
	            }
	        }
	        
	       //minPoints--;
	        
	        if (furthestPointDistance > epsilon && minPoints>1) {
	        	
	        	int remainingPoints = minPoints/2;
	        	int otherRemainingPoints = minPoints - remainingPoints;
	        	
	        	//Check that size will produce correct number of points
	        	int pointsToTheLeft = furthestPointIndex+1;
	        	int pointsToTheRight = points.size() - furthestPointIndex;
	        	
	        	ArrayList<Point> reduced1 = null;
	        	ArrayList<Point> reduced2 = null;
	        	
	        	if(pointsToTheLeft>remainingPoints && pointsToTheRight>otherRemainingPoints)
	        	{
		        	//why furthest points + 1, it is because second parameter in non inclusive, so it captures up to furthest point, this is correct
 		            reduced1 = findCharacteristicPointsDouglasPeucker(points.subList(0, furthestPointIndex+1), epsilon, remainingPoints);
		            reduced2 = findCharacteristicPointsDouglasPeucker(points.subList(furthestPointIndex, points.size()), epsilon, otherRemainingPoints);
		
	        	}else
	        	{
	        		int balancePointsLeft = pointsToTheLeft - remainingPoints;
	        		int balancePointsRight = pointsToTheRight - otherRemainingPoints;
	        		
	        		if(balancePointsLeft==0 || balancePointsRight-balancePointsLeft>0)
	        		{
	        			balancePointsRight++;
	        			balancePointsLeft--;
	        		}
	        		
	        		if(balancePointsRight==0 || balancePointsLeft - balancePointsRight>0)
	        		{
	        			balancePointsLeft++;
	        			balancePointsRight--;
	        		}
	        		
 	        		if(balancePointsLeft>0 && balancePointsRight<=0)
	        		{
	        			if(balancePointsLeft + balancePointsRight>=0)
	        			{
	        				reduced1 = findCharacteristicPointsDouglasPeucker(points.subList(0, furthestPointIndex+1), epsilon, (remainingPoints + Math.abs(balancePointsRight)));
	    		            reduced2 = findCharacteristicPointsDouglasPeucker(points.subList(furthestPointIndex, points.size()), epsilon, pointsToTheRight);
	        			}else{
	        				
	        				int finalPointsToTheLeft = remainingPoints + balancePointsLeft;
	        				int finalPointsToTheRight = otherRemainingPoints - balancePointsLeft;
	        				reduced1 = findCharacteristicPointsDouglasPeucker(points.subList(0, furthestPointIndex+1), epsilon, finalPointsToTheLeft);
	    		            reduced2 = findCharacteristicPointsDouglasPeucker(points.subList(furthestPointIndex, points.size()), epsilon, finalPointsToTheRight);
	        			}
	        		}
	        		
	        		if(balancePointsRight>0 && balancePointsLeft<=0)
	        		{
	        			if(balancePointsLeft + balancePointsRight>=0)
	        			{
	        				reduced1 = findCharacteristicPointsDouglasPeucker(points.subList(0, furthestPointIndex+1), epsilon, pointsToTheLeft);
	    		            reduced2 = findCharacteristicPointsDouglasPeucker(points.subList(furthestPointIndex, points.size()), epsilon, (otherRemainingPoints + Math.abs(balancePointsLeft)));
	        			}else{
	        				int finalPointsToTheLeft = remainingPoints - balancePointsRight;
	        				int finalPointsToTheRight = otherRemainingPoints + balancePointsRight;
	        				reduced1 = findCharacteristicPointsDouglasPeucker(points.subList(0, furthestPointIndex+1), epsilon, finalPointsToTheLeft);
	    		            reduced2 = findCharacteristicPointsDouglasPeucker(points.subList(furthestPointIndex, points.size()), epsilon, finalPointsToTheRight);
	        			}
	        		}        			        		
	        	}
	        	
	        	/* For Debugging only!!!
	        	if(minPoints == 14 || minPoints == 7)
	        	{
	        		System.out.print("Here!");
	        		System.out.print("Check");
	        	}
	        	 */
	            ArrayList<Point> result = new ArrayList<Point>(reduced1);
	            result.addAll(reduced2.subList(1, reduced2.size()));
	            return result;
	        } else {
	        	List<Point> tempPointsList = line.asList();
	        	ArrayList<Point> remainingPointsList = new ArrayList<Point>(tempPointsList);
	            return remainingPointsList;
	        }
	}
	
	//LSH over trajectories reduce by douglas peucker algorithm
	private void LSH(ArrayList<Segment> characteristicSegmentsFromTrajectory, ArrayList<Point> characteristicPointsFromTrajectory)
	{
		ArrayList<Segment>  listDouglasSegments = characteristicSegmentsFromTrajectory;
		
		//Point representation is better than segment representation
		 ArrayList<Point> listDouglasPointsFromTrajectory = characteristicPointsFromTrajectory;
		
		//For Random numbers
		Random rand = new Random();
		
		//Hash here - LSH
		//Map each trajectory from a set of trajectories to the set of real numbers
		for(Segment s: listDouglasSegments)
		{
			//Hash
			
			//Get to random numbers in the range of elements of the set
			int s1 = rand.nextInt(characteristicSegmentsFromTrajectory.size());
			int s2 = rand.nextInt(characteristicSegmentsFromTrajectory.size());
			
			//Trajectory s1T = listDouglasSegments.get(s1);
			
			//int hashS = dtw(s, )
			
		}
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
		BigDecimal bd = BigDecimal.valueOf(regularTrajectoryCost);
		bd.setScale(5, RoundingMode.HALF_UP);
		
		regularTrajectoryCost = bd.floatValue();
		
		//becuase it needs to be log2.
		//According to paper, MDLnopar is the MDL of the whole trajectory
		//That is L(H) only, cause L(D|H) is 0.
		//L(H) is the sum of the values.
		regularTrajectoryCost = (float) Math.log10(regularTrajectoryCost)/log2Value - precisionRegularizer;
		
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
	

	/**
	 * To calculate DTW distance without the warping path, just as a double
	 * to obtain the neighborhood for density based clustering
	 * @param t first trajectory
	 * @param t2 second trajectory
	 * @return DTW cost of aligning the 2 trajectories using Euclidean distance as a metric
	 */
	public static double calculateDTWDistance(Trajectory t,
			Trajectory t2) {
		// TODO Auto-generated method stub
		  final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance"); 
		TimeSeries ts1 = new TimeSeries(t);
		TimeSeries ts2 = new TimeSeries(t2);
		
		double dtwEuclideanCost = DTW.getWarpDistBetween(ts1, ts2, distFn);
		return dtwEuclideanCost;
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
	
	public String getTrajectoryUser() {
		return trajectoryUser;
	}

	public void setTrajectoryUser(String trajectoryUser) {
		this.trajectoryUser = trajectoryUser;
	}

	@Override
	public String toString() {
		return String.valueOf(getTrajectoryId());
	}

	public String printSummary() {
		return "Trajectory [trajectoryId=" + trajectoryId + 
				" pointsInTrajectory=" + points.size() + "]";
	}
	
	public String printVerbose(){
		return "Trajectory [trajectoryId=" + trajectoryId + 
				" pointsInTrajectory=" + points.size() + ", points="
				+ points + ", MDLPrecision=" + MDLPrecision + "]";
	}
	
	public String printLocation(){
		String toPrint = "";
		
		toPrint = "Trajectory [trajectoryId=" + trajectoryId + 
				" pointsInTrajectory=" + points.size() + ", points=";
		for(Point p: points)
		{
			if(p.isUTM()){
			toPrint = toPrint + "\n"+ p.printToPlotUTMToCoordinates()+ "," + getTrajectoryId();
			}else
			{
			toPrint = toPrint + "\n"+ p.printToPlot() + "," + getTrajectoryId();
			}
		}
		toPrint = toPrint + "]";
		return toPrint;
	}

	public String printTrajectoryToCSV(){
		String toPrint = "";
		
		toPrint = "latitude,longitude,trajectory";
		for(Point p: points)
		{
			if(p.isUTM()){
			toPrint = toPrint + "\n"+ p.printToPlotUTMToCoordinates()+ "," + getTrajectoryId();
			}else
			{
			toPrint = toPrint + "\n"+ p.printToPlot() + "," + getTrajectoryId();
			}
		}
		return toPrint;
	}

	public String printToPlotWithOtherTrajectories(){
		String toPrint = "";

		for(Point p: points)
		{
			if(p.isUTM()){
			toPrint = toPrint + "\n"+ p.printToPlotUTMToCoordinates()+ "," + getTrajectoryId();
			}else
			{
			toPrint = toPrint + "\n"+ p.printToPlot() + "," + getTrajectoryId();
			}
		}
		return toPrint;
	}
	
	public void exportPlotableCoordinates()
	{
		try {
			
			String content = printLocation();
			String filename = "eTrajectory" + getTrajectoryId() + ".txt";
			File file = new File(filename);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
 
			//System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportPlotableCoordinatesCSV()
	{
		try {
			
			String content = printTrajectoryToCSV();
			String filename = "eTrajectory" + getTrajectoryId() + ".csv";
			File file = new File(filename);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
 
			//System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//For clustering over whole trajectories, we need this extra fields, as we did for
	//segments in Traclus
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

	public double isDtwAverage() {
		return dtwAverage;
	}

	public void setDtwAverage(Double dtwAverage2) {
		this.dtwAverage = dtwAverage2;
	}

	@Override
	public float[] getLocation() {
		// TODO Auto-generated method stub
		float[] locations = new float[points.size()*2];
		int i = 0;
		for(Point p:points)
		{
			locations[i] = p.getX();
			i++;
			locations[i] = p.getY();
			i++;
		}
		
		return locations;
	}

	public int getClusterIdPreLabel() {
		return clusterIdPreLabel;
	}

	public void setClusterIdPreLabel(int clusterIdPreLabel) {
		this.clusterIdPreLabel = clusterIdPreLabel;
	}

	
}
