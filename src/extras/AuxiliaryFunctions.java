package extras;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.xml.crypto.KeySelector.Purpose;

import org.jfree.io.FileUtilities;

public class AuxiliaryFunctions {

	public AuxiliaryFunctions() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Generates a random color, used to print clusters in different colors in JFreeChart
	 * @return
	 */
	public static Color generateRandomColor()
	{
		Random rand = new Random();
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);
		
		Color randomColor = new Color(r, g, b);
		return randomColor;
	}
	
	/**
	 * Creates a Directory (folder) in a Given Path.
	 * @param path
	 */
	public static void createFolder(String path)
	{
		Path p = Paths.get(path);
		try {
			Files.createDirectory(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Directory could not be created: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Erases a given folder
	 * @param path
	 * @throws IOException
	 */
	public static void eraseFolder(String path) throws IOException
	{
		Path p = Paths.get(path);
		Files.delete(p);
	}
	
	/**
	 * A wrapper to erase all files of a Directory
	 * @param path
	 */
	public static void eraseFolderContents(String path)
	{
		File f = new File(path);
		purgeDirectory(f);
		
	}
	
	/**
	 * Deletes all files of a Directory
	 * @param dir
	 */
	public static void purgeDirectory(File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isDirectory()) purgeDirectory(file);
	        file.delete();
	    }
	}
	
	public static void printStringToFile(String textToPrint, String filename, String path)
	{
		try {
			
			//This is to verify that path exists
			String completeFilename = "";
			if(filename!=null)
			{
				Path p = Paths.get(path);
				if(!Files.exists(p))
				{
					extras.AuxiliaryFunctions.createFolder(path);
				}
				
				completeFilename = path.concat(filename);
			}
			
			File file = new File(completeFilename);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(textToPrint);
			bw.close();
  
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Just to print a Hash Map. Used to print LSH Hashtable representation.
	 * @param mp Any of the class map, in our case a HashMap.
	 */
	public static void printMap(Map mp) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
	}
}
