package extras;

import java.awt.Color;
import java.util.Random;

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
}
