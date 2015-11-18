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
	
	public double distanceFrom(FeatureVector other) throws Exception
	{
		double distance = 0;
		if(this.features.size()!=other.features.size())
		{
			throw new Exception("Fatal Error, cannot calculate norm of Vectors of different dimensions."); 
		}else{
			double squaredDistance = 0;
			for(int i=0; i<features.size(); i++)
			{
				squaredDistance += Math.pow((features.get(i) - other.features.get(i)),2);
			}
			distance = Math.sqrt(squaredDistance);
		}
		
		return distance;
	}
	
	/**
	 * This method is to calculate the L2 distance of two vectors from the stromberglabs kmeans Feature Vectors
	 * I added it here because stromberlabs does not provide such a method and I did not know where else to locate the code
	 * Seems to fits with feature vector class
	 * TODO Move this method to a more appropiated class
	 * @param fv1
	 * @param fv2
	 * @return L2 Distance between 2 Stromberglabs vectors
	 * @throws Exception
	 */
	public static double L2distanceFrom(float[] fv1, float[] fv2) throws Exception
	{
		double distance = 0;
		if(fv1.length!=fv2.length)
		{
			throw new Exception("Fatal Error, cannot calculate norm of Vectors of different dimensions."); 
		}else{
			double squaredDistance = 0;
			for(int i=0; i<fv1.length; i++)
			{
				squaredDistance += Math.pow((fv1[i] - fv2[i]),2);
			}
			distance = Math.sqrt(squaredDistance);
		}
		
		return distance;
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
	public void setFeaturesToVectorSingleRealValue(BitSet hashResult) {
		// TODO Auto-generated method stub
		long[] longVector = hashResult.toLongArray();
		
		if(longVector.length<1)
		{
			//TODO add this to debug only log
			//System.err.println("Hey, feature vector is empty, from hashbit: " + hashResult + " bitlength: " + hashResult.length() + " bitsize: " + hashResult.size());
			longVector = new long[]{0};
		}
		
		features = new ArrayList<Float>();
		for(int i = 0; i<longVector.length; i++)
		{
			features.add((float) longVector[i]);
		}
	}
	
	/**
	 * This function converts a BitSet (a set of bits or BitArray aka Vector of booleans)
	 * to a vector of floats so it can be integrated to the feature Vector class.
	 * This particular function is needed to do Kmeans over a Feature Vector of Booleans with DBH.
	 * @param hashResult : An array of bits (booleans).
	 */
	public void setFeaturesToBinaryVector(BitSet hashResult, int maxNumberOfFeatures) {
		// TODO Auto-generated method stub
		//long[] longVector = hashResult.toLongArray();
		
		for(int i = 0; i<maxNumberOfFeatures; i++)
		{
			//TODO add this to debug only log
			//System.err.println("Hey, feature vector is empty, from hashbit: " + hashResult + " bitlength: " + hashResult.length() + " bitsize: " + hashResult.size());
			float tempValue = (hashResult.get(i)?1:0);
			features.add(tempValue);
		}
	}
	
	public String featuresToString()
	{
		String featuresInString = "";
		for(float f: features)
		{
			int featureInteger = (int) Math.floor(f);
			featuresInString = featuresInString + featureInteger;
		}
		return featuresInString;
	}

	
}
