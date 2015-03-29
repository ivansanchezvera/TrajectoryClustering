import java.sql.Timestamp;
import java.util.ArrayList;
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
		
		return testTrajectories;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		float eNeighborhoodParameter = 15;
		int minLins = 4;
		int cardinalityOfClusters = 1;
		
		ArrayList<Trajectory> testTrajectories = testTraclus.generateTestTrajectories();
		
		
		
		Traclus traclus = new Traclus(testTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters);
		ArrayList<Cluster> testClusters = traclus.execute();
		
		for(Cluster c: testClusters)
		{
			System.out.println(c.toString());
		}

	}

}
