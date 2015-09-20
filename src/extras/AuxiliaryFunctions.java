package extras;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.xml.crypto.KeySelector.Purpose;

import org.jfree.io.FileUtilities;

public class AuxiliaryFunctions {

	public AuxiliaryFunctions() {
		// TODO Auto-generated constructor stub
	}

	public static Color generateRandomColor()
	{
		Random rand = new Random();
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);
		
		Color randomColor = new Color(r, g, b);
		return randomColor;
	}
	
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
	
	public static void eraseFolder(String path) throws IOException
	{
		Path p = Paths.get(path);
		Files.delete(p);
	}
	
	public static void eraseFolderContents(String path)
	{
		File f = new File(path);
		purgeDirectory(f);
		
	}
	
	public static void purgeDirectory(File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isDirectory()) purgeDirectory(file);
	        file.delete();
	    }
	}
}
