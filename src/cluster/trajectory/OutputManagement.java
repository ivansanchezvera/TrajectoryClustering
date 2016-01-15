package cluster.trajectory;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class OutputManagement {

	public OutputManagement() {
		// TODO Auto-generated constructor stub
	}
	
	//In the future add the option to give an export path
	/**
	 * This method exports trajectories in its reduced state to a defined path.
	 * @param path
	 * @param dataSetName
	 * @param numberOfPartitions
	 * @return
	 */
	public static ArrayList<Integer> ExportReducedTrajectories(String path, String dataSetName, int numberOfPartitions)
	{
		String CVRRdatasetName = dataSetName;
		ArrayList<Trajectory> trajectoriesFromInput = InputManagement.generateTestTrajectoriesFromDataSetCVRR(CVRRdatasetName, false, null);
		
		ArrayList<Trajectory> simplifiedTrajectories = Trajectory.simplifyTrajectories(trajectoriesFromInput, true, SegmentationMethod.douglasPeucker, numberOfPartitions);
		
		//This is so we can Print the original Trajectories
		ArrayList<Integer> representedTrajectories = new ArrayList<Integer>();
		for(Trajectory t: simplifiedTrajectories)
		{
			representedTrajectories.add(t.getTrajectoryId());
		}
		
		printSetOfTrajectories(path, simplifiedTrajectories, true);
		return representedTrajectories;		
	}
	
	/**
	 * Exports (Creates files) of a given set of trajectories in the given path.
	 * @param path
	 * @param trajectories
	 * @param isSimplifiedTrajectory : Simplified has headers, non simplified normally don't require this (specially for plotting on map).
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
