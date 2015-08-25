package cluster.trajectory;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class OutputManagement {

	public OutputManagement() {
		// TODO Auto-generated constructor stub
	}
	
	//In the future add the option to give an export path
	public static void ExportReducedTrajectories(String dataSetName, int numberOfPartitions)
	{
		String CVRRdatasetName = dataSetName;
		ArrayList<Trajectory> trajectoriesFromInput = InputManagement.generateTestTrajectoriesFromDataSetCVRR(CVRRdatasetName);
		
		ArrayList<Trajectory> simplifiedTrajectories = Traclus.simplifyTrajectories(trajectoriesFromInput, true, SegmentationMethod.douglasPeucker, numberOfPartitions);
		
		printSetOfTrajectories(simplifiedTrajectories);
		
	}
	
	public static void printSetOfTrajectories(ArrayList<Trajectory> trajectories)
	{
		for(Trajectory t: trajectories)
		{
			t.exportPlotableCoordinatesCSV();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dataSetName = args[0];
		
		//This parameter must be an Integer
		int numberOfPartitions = 0;
		try {
			numberOfPartitions = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ExportReducedTrajectories(dataSetName, numberOfPartitions);	

	}

}
