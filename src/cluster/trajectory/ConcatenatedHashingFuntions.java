package cluster.trajectory;

import java.util.ArrayList;
import java.util.BitSet;

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
}
