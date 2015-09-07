package cluster.trajectory;

import java.util.ArrayList;

public abstract class Clusterable implements Cloneable{

	public boolean classified = false;
	public boolean isNoise = false;
	public int id = -2;
	
	public ArrayList<Point> elements = new ArrayList<Point>();
	
	public Object clone() throws CloneNotSupportedException {
		return null;
	}
	
	//public void calculateDistance();
}
