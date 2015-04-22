import java.io.File;
import java.util.ArrayList;


public final class CommonFunctions {

	public static ArrayList<String> listFilesForFolder(String folderPath) {
		ArrayList<String> filesInFolder = new ArrayList<String>();
		final File folder = new File(folderPath);
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry.getPath());
	        } else {
	        	filesInFolder.add(fileEntry.getAbsolutePath());
	            System.out.println(fileEntry.getAbsolutePath());
	        }
	    }
	    return filesInFolder;
	}
	
	public CommonFunctions() {
		// TODO Auto-generated constructor stub
	}

}
