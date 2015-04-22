import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class testTraclus {

	public testTraclus() {
		// TODO Auto-generated constructor stub
	}

	public static ArrayList<Trajectory> generateTestTrajectories()
	{
		ArrayList<Trajectory> testTrajectories = new ArrayList<Trajectory>();
		
		//****trajectory 0
		ArrayList<Point> listPointsTrajectory0 = new ArrayList<Point>();
		Point tr0p1 = new Point(1, 1, new java.sql.Timestamp(2325345));
		Point tr0p2 = new Point(2, 2, new java.sql.Timestamp(2325345));
		Point tr0p3 = new Point(3, 2, new Timestamp(2353445));
		Point tr0p4 = new Point(4, 2, new Timestamp(2367688));
		Point tr0p5 = new Point(5, 1, new Timestamp(2394524));
		Point tr0p6 = new Point(6, 1, new Timestamp(2394524));
		Point tr0p7 = new Point(7, 1, new Timestamp(2394524));
		Point tr0p8 = new Point(8, 2, new Timestamp(2394524));
		listPointsTrajectory0.add(tr0p1);
		listPointsTrajectory0.add(tr0p2);
		listPointsTrajectory0.add(tr0p3);
		listPointsTrajectory0.add(tr0p4);
		listPointsTrajectory0.add(tr0p5);
		listPointsTrajectory0.add(tr0p6);
		listPointsTrajectory0.add(tr0p7);
		listPointsTrajectory0.add(tr0p8);
		
		Trajectory tr0 = new Trajectory(0, listPointsTrajectory0);
		testTrajectories.add(tr0);
		//****trajectory 1
		
		ArrayList<Point> listPointsTrajectory1 = new ArrayList<Point>();
		Point tr1p1 = new Point(1, 0, new java.sql.Timestamp(2325345));
		Point tr1p2 = new Point(2, 3, new java.sql.Timestamp(2325345));
		Point tr1p3 = new Point(3, 1, new Timestamp(2353445));
		Point tr1p4 = new Point(7, 4, new Timestamp(2367688));
		Point tr1p5 = new Point(8, 2, new Timestamp(2394524));
		listPointsTrajectory1.add(tr1p1);
		listPointsTrajectory1.add(tr1p2);
		listPointsTrajectory1.add(tr1p3);
		listPointsTrajectory1.add(tr1p4);
		listPointsTrajectory1.add(tr1p5);
		
		Trajectory tr1 = new Trajectory(1, listPointsTrajectory1);
		
		testTrajectories.add(tr1);
		
		//************trajectory 2
		ArrayList<Point> listPointsTrajectory2 = new ArrayList<Point>();
		Point tr2p1 = new Point(4, 2, new Timestamp(2521462));
		Point tr2p2 = new Point(7, 1, new Timestamp(2724653));
		Point tr2p3 = new Point(9, 5, new Timestamp(2913445));
		listPointsTrajectory2.add(tr2p1);
		listPointsTrajectory2.add(tr2p2);
		listPointsTrajectory2.add(tr2p3);
		
		Trajectory tr2 = new Trajectory(2, listPointsTrajectory2);
		
		testTrajectories.add(tr2);
		
		//************trajectory 3
		ArrayList<Point> listPointsTrajectory3 = new ArrayList<Point>();
		Point tr3p1 = new Point(125, 152, new Timestamp(2521462));
		Point tr3p2 = new Point(129, 149, new Timestamp(2724653));
		Point tr3p3 = new Point(123, 141, new Timestamp(2913445));
		Point tr3p4 = new Point(101, 133, new Timestamp(2913445));
		listPointsTrajectory3.add(tr3p1);
		listPointsTrajectory3.add(tr3p2);
		listPointsTrajectory3.add(tr3p3);
		listPointsTrajectory3.add(tr3p4);
		
		Trajectory tr3 = new Trajectory(3, listPointsTrajectory3);
		
		testTrajectories.add(tr3);

		//************trajectory 4
		ArrayList<Point> listPointsTrajectory4 = new ArrayList<Point>();
		Point tr4p1 = new Point(75, 110, new Timestamp(2521462));
		Point tr4p2 = new Point(80, 101, new Timestamp(2724653));
		Point tr4p3 = new Point(83, 95, new Timestamp(2913445));
		Point tr4p4 = new Point(92, 109, new Timestamp(2913445));
		Point tr4p5 = new Point(91, 123, new Timestamp(2913445));
		Point tr4p6 = new Point(102, 129, new Timestamp(2913445));
		Point tr4p7 = new Point(111, 145, new Timestamp(2913445));
		
		listPointsTrajectory4.add(tr4p1);
		listPointsTrajectory4.add(tr4p2);
		listPointsTrajectory4.add(tr4p3);
		listPointsTrajectory4.add(tr4p4);
		listPointsTrajectory4.add(tr4p5);
		listPointsTrajectory4.add(tr4p6);
		listPointsTrajectory4.add(tr4p7);
				
		Trajectory tr4 = new Trajectory(4, listPointsTrajectory4);
				
		testTrajectories.add(tr4);
		
		
		//************trajectory 5
		ArrayList<Point> listPointsTrajectory5 = new ArrayList<Point>();
		Point tr5p1 = new Point(25, 3, new Timestamp(2521462));
		Point tr5p2 = new Point(20, 3, new Timestamp(2724653));
		Point tr5p3 = new Point(17, 2, new Timestamp(2913445));
		Point tr5p4 = new Point(13, 5, new Timestamp(2913445));
		Point tr5p5 = new Point(11, 8, new Timestamp(2913445));
		Point tr5p6 = new Point(8, 17, new Timestamp(2913445));
		Point tr5p7 = new Point(5, 23, new Timestamp(2913445));
		Point tr5p8 = new Point(6, 36, new Timestamp(2913445));
				
		listPointsTrajectory5.add(tr5p1);
		listPointsTrajectory5.add(tr5p2);
		listPointsTrajectory5.add(tr5p3);
		listPointsTrajectory5.add(tr5p4);
		listPointsTrajectory5.add(tr5p5);
		listPointsTrajectory5.add(tr5p6);
		listPointsTrajectory5.add(tr5p7);
		listPointsTrajectory5.add(tr5p8);
						
		Trajectory tr5 = new Trajectory(5, listPointsTrajectory5);
						
		testTrajectories.add(tr5);
		
		//************trajectory 6
		ArrayList<Point> listPointsTrajectory6 = new ArrayList<Point>();
		Point tr6p1 = new Point(45, 60, new Timestamp(3321462));
		Point tr6p2 = new Point(48, 52, new Timestamp(3324653));
		Point tr6p3 = new Point(49, 37, new Timestamp(3313445));
		Point tr6p4 = new Point(42, 25, new Timestamp(3313445));
		Point tr6p5 = new Point(56, 22, new Timestamp(3313445));
				
		listPointsTrajectory6.add(tr6p1);
		listPointsTrajectory6.add(tr6p2);
		listPointsTrajectory6.add(tr6p3);
		listPointsTrajectory6.add(tr6p4);
		listPointsTrajectory6.add(tr6p5);
		
		Trajectory tr6 = new Trajectory(6, listPointsTrajectory6);
						
		testTrajectories.add(tr6);
		
		//************trajectory 7
		ArrayList<Point> listPointsTrajectory7 = new ArrayList<Point>();
		Point tr7p1 = new Point(70, 60, new Timestamp(3321654));
		Point tr7p2 = new Point(75, 63, new Timestamp(3324678));
		Point tr7p3 = new Point(72, 68, new Timestamp(3343490));
		Point tr7p4 = new Point(78, 77, new Timestamp(3345445));
						
		listPointsTrajectory7.add(tr7p1);
		listPointsTrajectory7.add(tr7p2);
		listPointsTrajectory7.add(tr7p3);
		listPointsTrajectory7.add(tr7p4);
		
		Trajectory tr7 = new Trajectory(7, listPointsTrajectory7);
							
		testTrajectories.add(tr7);
		
		//************trajectory 8
		ArrayList<Point> listPointsTrajectory8 = new ArrayList<Point>();
		Point tr8p1 = new Point(85, 60, new Timestamp(3321654));
		Point tr8p2 = new Point(89, 52, new Timestamp(3324678));
		Point tr8p3 = new Point(99, 49, new Timestamp(3343490));
		Point tr8p4 = new Point(103,43, new Timestamp(3345445));
		Point tr8p5 = new Point(103,75, new Timestamp(3345445));
		Point tr8p6 = new Point(109,86, new Timestamp(3345445));
							
		listPointsTrajectory8.add(tr8p1);
		listPointsTrajectory8.add(tr8p2);
		listPointsTrajectory8.add(tr8p3);
		listPointsTrajectory8.add(tr8p4);
		listPointsTrajectory8.add(tr8p5);
		listPointsTrajectory8.add(tr8p6);
				
		Trajectory tr8 = new Trajectory(8, listPointsTrajectory8);
		testTrajectories.add(tr8);
		
		//************trajectory 9
		ArrayList<Point> listPointsTrajectory9 = new ArrayList<Point>();
		Point tr9p1 = new Point(30, 20, new Timestamp(3321462));
		Point tr9p2 = new Point(35, 26, new Timestamp(3324653));
		Point tr9p3 = new Point(38, 31, new Timestamp(3313445));		
		Point tr9p4 = new Point(31, 25, new Timestamp(3313445));
		Point tr9p5 = new Point(45, 36, new Timestamp(3313445));
		Point tr9p6 = new Point(51, 46, new Timestamp(3313445));
						
		listPointsTrajectory9.add(tr9p1);
		listPointsTrajectory9.add(tr9p2);
		listPointsTrajectory9.add(tr9p3);
		listPointsTrajectory9.add(tr9p4);
		listPointsTrajectory9.add(tr9p5);
		listPointsTrajectory9.add(tr9p6);
				
		Trajectory tr9 = new Trajectory(9, listPointsTrajectory9);
								
		testTrajectories.add(tr9);
		
		return testTrajectories;
	}
	
	
	public static ArrayList<Trajectory> generateTestTrajectoriesFromDataSet(int userIndex)
	{
		String filePathToDataset = "C:\\Users\\Ivan\\Documents\\Unimelb\\Big Project\\My work\\Datasets\\Geolife Trajectories 1.3\\Data";
		
		float MDLPrecision = (float) 0.00001;
		//Set of trajectories to return
		ArrayList<Trajectory> testTrajectories = new ArrayList<Trajectory>();
		
		for(int i = 0; i<=userIndex; i++)
		{
			String tempUserFolder = String.format("%03d", i);
			String fileToUserFolder = filePathToDataset + "\\" + tempUserFolder + "\\Trajectory";
			
			ArrayList<String> UserTrajectoryFiles = CommonFunctions.listFilesForFolder(fileToUserFolder);
			
			//Set of points to make trajectory
			ArrayList<Point> listPointsTrajectory0 = new ArrayList<Point>();
			for(String userTrajectoryFile: UserTrajectoryFiles)
			{
				BufferedReader reader;
				try {
					reader = new BufferedReader(new InputStreamReader(
			        new FileInputStream(userTrajectoryFile)));
						
			        //Skip first 6 lines cause they mean nothing in the dataset
					for(int j=1;j<=6;j++)
					{
						reader.readLine();
					}
					
					//Reading Lines and creating trajectories
					String line;
					while ((line = reader.readLine()) != null) {
					       // process the line.
						//System.out.println("Trajectory Line: " + line);
						String[] trajectoryPointData = line.split(",");
						//System.out.println("Trajectory decomposed Line: " + trajectoryPointData);
						
						String Latitude = trajectoryPointData[0];
						String Longitude = trajectoryPointData[1];
						String datetimeInNumber = trajectoryPointData[4];
						String dateInHumanFormat = trajectoryPointData[5];
						String timeInHumanFormat = trajectoryPointData[6];
						
						
						int year = Integer.parseInt(dateInHumanFormat.substring(0,4));
						int month = Integer.parseInt(dateInHumanFormat.substring(5,7));
						int day = Integer.parseInt(dateInHumanFormat.substring(8,10));
						SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd"); 
						Date d = new Date();
						try {
							d = ft.parse(dateInHumanFormat);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
								
						//d.parse(dateInHumanFormat);
						d.setHours(Integer.parseInt(timeInHumanFormat.substring(0,2)));
						d.setMinutes(Integer.parseInt(timeInHumanFormat.substring(3,5)));
						d.setSeconds(Integer.parseInt(timeInHumanFormat.substring(6,8)));
						
						float longitudeF = Float.parseFloat(Longitude);
						float latitudeF = Float.parseFloat(Latitude);
						//Float.i					
						
						Point tr0p1 = new Point(longitudeF, latitudeF , new java.sql.Timestamp(d.getTime()));
						listPointsTrajectory0.add(tr0p1);
						
					}
					
					}catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				Trajectory tr0 = new Trajectory(0, listPointsTrajectory0);
				tr0.setMDLPrecision((float) 0.00001);
				testTrajectories.add(tr0);
			}
		}
		return testTrajectories;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		float eNeighborhoodParameter = 25;
		int minLins = 3;
		int cardinalityOfClusters = 1;
		float MLDPrecision = (float) 0.0001;
		
		ArrayList<Trajectory> testTrajectories = testTraclus.generateTestTrajectories();
		ArrayList<Trajectory> testTrajectoriesFromFile = testTraclus.generateTestTrajectoriesFromDataSet(0);	
		
		//To use test trajectories from file
		testTrajectories = testTrajectoriesFromFile;
		
		Traclus traclus = new Traclus(testTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters);
		ArrayList<Cluster> testClusters = traclus.execute();
		
		//To print Clusters - Refactor this
		if(testClusters.isEmpty())
		{
			System.out.println("No clusters meet the parameters criteria");
		}
		else{
			for(Cluster c: testClusters)
			{
				System.out.println(c.toString());
			}
		}
	}
}
