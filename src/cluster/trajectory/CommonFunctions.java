package cluster.trajectory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;


public final class CommonFunctions {

	public static ArrayList<String> listFilesForFolder(String folderPath) {
		ArrayList<String> filesInFolder = new ArrayList<String>();
		final File folder = new File(folderPath);
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry.getPath());
	        } else {
	        	filesInFolder.add(fileEntry.getAbsolutePath());
	            //System.out.println(fileEntry.getAbsolutePath());
	        }
	    }
	    return filesInFolder;
	}
	
	public static HashSet<Integer> getHashSetAllTrajectories(ArrayList<Trajectory> testTrajectories) 
	{
		// TODO Auto-generated method stub
		HashSet<Integer> allTrajectories = new HashSet<Integer>();
		
		for(Trajectory t: testTrajectories)
		{
			allTrajectories.add(t.getTrajectoryId());
		}
		
		return allTrajectories;
	}
	
	public CommonFunctions() {
		// TODO Auto-generated constructor stub
	}

}
