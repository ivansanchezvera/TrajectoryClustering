package cluster.trajectory;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class OutputManagement {

	public OutputManagement() {
		// TODO Auto-generated constructor stub
	}
	
	//In the future add the option to give an export path
	public static void ExportReducedTrajectories(String path, String dataSetName, int numberOfPartitions)
	{
		String CVRRdatasetName = dataSetName;
		ArrayList<Trajectory> trajectoriesFromInput = InputManagement.generateTestTrajectoriesFromDataSetCVRR(CVRRdatasetName, false, null);
		
		ArrayList<Trajectory> simplifiedTrajectories = Traclus.simplifyTrajectories(trajectoriesFromInput, true, SegmentationMethod.douglasPeucker, numberOfPartitions);
		
		printSetOfTrajectories(path, simplifiedTrajectories, true);
		
	}
	
	/**
	 * Exports (Creates files) of a given set of trajectories in the given path.
	 * @param path
	 * @param trajectories
	 * @param isSimplifiedTrajectory : Simplified has headers, non simplified nomrally don't require this (specially for plotting on map).
	 */
	public static void printSetOfTrajectories(String path, ArrayList<Trajectory> trajectories, boolean isSimplifiedTrajectory)
	{
		//First erase folder contents to be sure we only have what we want
		extras.AuxiliaryFunctions.eraseFolderContents(path);
		
		for(Trajectory t: trajectories)
		{
			t.exportPlotableCoordinatesCSV(path, isSimplifiedTrajectory);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dataSetName = args[0];
		String path = args[2];
		
		//This parameter must be an Integer
		int numberOfPartitions = 0;
		try {
			numberOfPartitions = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ExportReducedTrajectories(path, dataSetName, numberOfPartitions);	

	}

}
