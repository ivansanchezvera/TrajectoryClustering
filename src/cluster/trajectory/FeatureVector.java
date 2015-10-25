package cluster.trajectory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Vector;

import com.stromberglabs.cluster.Clusterable;

public class FeatureVector implements Clusterable{

	public int id;
	public ArrayList<Float> features;
	
	public FeatureVector(int id) {
		// TODO Auto-generated constructor stub
		this.id = id;
		features = new ArrayList<Float>();
	}

	@Override
	public float[] getLocation() {
		
		float[] featureVectorAsArray = new float[features.size()];
		int i = 0;

		for (Float f : features) {
			featureVectorAsArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
		}
		return featureVectorAsArray;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Float> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<Float> features) {
		this.features = features;
	}

	@Override
	public String toString() {
		return "FeatureVector [id=" + id + ", features=" + features + "]";
	}

	/**
	 * This function converts a BitSet (a set of bits or BitArray aka Vector of booleans)
	 * to a vector of floats so it can be integrated to the feature Vector class.
	 * This particular function is needed to do Kmeans over a Feature Vector of Booleans with DBH.
	 * @param hashResult : An array of bits (booleans).
	 */
	public void setFeatures(BitSet hashResult) {
		// TODO Auto-generated method stub
		long[] longVector = hashResult.toLongArray();
		
		features = new ArrayList<Float>();
		for(int i = 0; i<longVector.length; i++)
		{
			features.add((float) longVector[i]);
		}
	}

	
}
