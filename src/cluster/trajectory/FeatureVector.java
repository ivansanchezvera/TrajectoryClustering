package cluster.trajectory;

import java.util.ArrayList;

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

	
}
