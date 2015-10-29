package cluster.trajectory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class ConcatenatedHashingFuntions {

	private int k;
	private ArrayList<HashingFunction> hashingFunctions;
	
	public ConcatenatedHashingFuntions(int k) {
		// TODO Auto-generated constructor stub
		this.k =k;
		this.hashingFunctions = new ArrayList<HashingFunction>();
	}

	public void concatenate(HashingFunction h)
	{
		if(hashingFunctions.size()<k)
		{
			hashingFunctions.add(h);
		}
	}
	
	public boolean isConcatenationComplete()
	{
		if(hashingFunctions.size()==k)
		{
			return true;
		}else{
			return false;
		}
	}
	
	//I should verify that the concatenation is complete before executing this
	public BitSet execute(Trajectory t) throws Exception
	{
		BitSet hashResult = new BitSet(hashingFunctions.size());
		
		int i = 0;
		for(HashingFunction h:hashingFunctions)
		{
			boolean	hash = h.hashToBoolean(t);	
					
			hashResult.set(i, hash);
			i++;
		}
		
		return hashResult;
	}
	
	//I should verify that the concatenation is complete before executing this
	public ArrayList<BitSet> execute(ArrayList<Trajectory> trajectories)
	{
		ArrayList<BitSet> hashedTrajectories = new ArrayList<BitSet>();
	
		
		for(int j=0; j<trajectories.size(); j++)
		{
			BitSet hashResult = new BitSet(hashingFunctions.size());
			int i = 0;
			for(HashingFunction h:hashingFunctions)
			{
				boolean	hash = h.getCalculatedHashBool().get(j);	
						
				hashResult.set(i, hash);
				i++;
			}
			hashedTrajectories.add(hashResult);
		}
		
		return hashedTrajectories;
	}
	
	/**
	 * This function hashes all the trajectories to boolean feature vectors.
	 * It is particularly useful for doing Kmeans over DBH Hashed trajectories.
	 * @param trajectories : A list of trajectories to hash to a list of feature vectors.
	 * @param binaryFeature: Determines wether the resulting feature vector will result in a binary vector or if false in a single real value
	 * @return A List of Feature Vectores obtained from the DBH Hashing of the trajectory list.
	 */
	public ArrayList<FeatureVector> executeHashForFeatureVectors(ArrayList<Trajectory> trajectories, boolean binaryFeature, int maxNumberOfFeatures)
	{
		ArrayList<FeatureVector> hashedTrajectoriesAsFeatureVectors = new ArrayList<FeatureVector>();
		
		for(int j=0; j<trajectories.size(); j++)
		{
			FeatureVector fv = new FeatureVector(j);
			BitSet hashResult = new BitSet(hashingFunctions.size());
			int i = 0;
			for(HashingFunction h:hashingFunctions)
			{
				boolean	hash = h.getCalculatedHashBool().get(j);	
				hashResult.set(i, hash);
				i++;
			}
			
			if(binaryFeature)
			{
				fv.setFeaturesToBinaryVector(hashResult, maxNumberOfFeatures);
			}else{
				fv.setFeaturesToVectorSingleRealValue(hashResult);
			}
			
			hashedTrajectoriesAsFeatureVectors.add(fv);
		}
		return hashedTrajectoriesAsFeatureVectors;
	}
}
