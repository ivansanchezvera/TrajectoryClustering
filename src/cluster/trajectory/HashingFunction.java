package cluster.trajectory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * This class creates a single instance of a Hashing function.
 * We use an instance of a hashing function to produce hash tables
 * in the DBH method to obtain cluster candidates.
 * It is important to note that each hash function has a pair of
 * trajectories x1 and x2, selected at random by the class that instantiates an 
 * object of HashingFunction, that meand the random selection of x1 and x2 is not done 
 * in this class.
 * For DBH to work, each hashing function must split the data into 2 equal sets of boolean values
 * For that T1 and T2 need to be found based on the set of Trajectories to Hash.
 * Each hash maps to a Double value. That value is map to boolean given the interval T1 & T2.
 * @author IvánA
 *
 */
public class HashingFunction {

	private Trajectory x1;
	private Trajectory x2;
	private double t1;
	private double t2;
	private boolean t1t2Defined = false; 
	private double ds1s2 = 0; //Store this value to save computations.
	
	//Since we have to calculate this anyways, better store them to avoid excesive computations
	private ArrayList<Double> calculatedHashes = new ArrayList<Double>();
	private ArrayList<Boolean> calculatedHashBool = new ArrayList<Boolean>();	
	
	public HashingFunction(Trajectory x1, Trajectory x2) {
		this.x1 = x1;
		this.x2 = x2;
		this.ds1s2 = Trajectory.calculateDTWDistance(x1,x2);
		t1t2Defined = false;
	}
	
	public HashingFunction(Trajectory x1, Trajectory x2,
			double t1, double t2) {
		this.x1 = x1;
		this.x2 = x2;
		this.t1 = t1;
		this.t2 = t2;
		this.ds1s2 = Trajectory.calculateDTWDistance(x1,x2);
		t1t2Defined = true;
	}
	

	/**
	 * This method takes a Trajectory, hashes it to a Double and then
	 * returns a boolean depending wether the hash lies in the interval
	 * defined by t1 - t2, which its supposed to divide the data evenly in 2 halves
	 * @param x : A given Trajectory (to hash)
	 * @return boolean: 1 for trajectory hashes that are inside the interval t1 - t2, 0 for outside the interval
	 * @throws Exception 
	 */
	public boolean hashToBoolean(Trajectory x) throws Exception
	{
		//error that gives projections
		//Why??? what to do with this, how to solve it?
		//Report to RAO
		//ds1s2 = Trajectory.calculateDTWDistance(x1,x2);
		
		if(!t1t2Defined)
		{
			throw new Exception("T1 and T2 values not defined.");
		}
		
		double hash = hash(x);
	
		//this should be 50% chance
		if(hash>=t1 && hash<=t2)
		{
			return false;
		}else{
			return true;
		}
	}

	/**
	 * This method takes a Double, which is the value that had to be precalculated
	 * returns a boolean depending wether the hash lies in the interval
	 * defined by t1 - t2, which its supposed to divide the data evenly in 2 halves
	 * @param x : A given hash of a Trajectory (Precalculated)
	 * @return boolean: 1 for trajectory hashes that are inside the interval t1 - t2, 0 for outside the interval
	 * @throws Exception 
	 */
	public boolean hashToBoolean(Double x)
	{
		//this should be 50% chance
		if(x>=t1 && x<=t2)
		{
			return false;
		}else{
			return true;
		}
	}

	/**
	 * This method takes a trajectory and hashes it.
	 * @param x: A given Trajectory (to hash)
	 * @return Double: value of hash.
	 */
	public double hash(Trajectory x) {
		double dts1 = Trajectory.calculateDTWDistance(x,x1);
		double dts2 = Trajectory.calculateDTWDistance(x,x2);
		
		double hash = (Math.pow(dts1, 2) + Math.pow(ds1s2, 2) - Math.pow(dts2, 2))/(2*ds1s2);
		return hash;
	}

	/**
	 * Here we calculate T1 and T2 over only a subset of all trajectories
	 * Since the process of calculating T1 and T2 is expensive, we try sampling.
	 * We sample a given number (numSamples) of trajectories and then calculate T1 and T2
	 * only based in this subset.
	 * @param trajectories : Complete set of trajectories
	 * @param numSamples : Number of samples to obtain from the set of trajectories.
	 * 
	 * Correct: need to fix this method, cause at the end need to map all trajectories to 
	 * a hash value, for that we need all trajectories.
	 * 
	 * Do this method is worth?
	 * At the end, we still need to calculate all hashes for every trajectory, so 
	 * doing this sampling saves no time at the end. Plus it sacrifices accuracy.
	 */
	public void findT1T2WithSampling(ArrayList<Trajectory> trajectories, int numSamples)
	{
		Random r = new Random();
		ArrayList<Trajectory> samples = new ArrayList<Trajectory>();
		
		Set<Integer> generated = new LinkedHashSet<Integer>();
		
		for(int i=0; i<numSamples;i++)
		{
			int trajectoryIndex = r.nextInt();
			if(generated.contains(trajectoryIndex))
			{
				generated.add(trajectoryIndex);
				samples.add(trajectories.get(trajectoryIndex));
			}
		}
		
		findT1T2(samples);
	}
	
	/**
	 * This method finds T1 and T2 limits for a given hashing function
	 * This is the exhaustive method, makes the overall process very expensive
	 * @param trajectories: a set of trajectories
	 * @param hf: a hashing function
	 */
	public void findT1T2(ArrayList<Trajectory> trajectories)
	{
		//todo: Make it work for samples (need sample size)
		
		t1 = Double.NEGATIVE_INFINITY;
		t2 = 0;
		double minHashValue = Double.POSITIVE_INFINITY;
		double maxHashValue = Double.NEGATIVE_INFINITY;
		
		for(Trajectory t:trajectories)
		{
			Double hashValueOfT = hash(t);
			calculatedHashes.add(hashValueOfT);
			
			
			if(hashValueOfT>maxHashValue)
			{
				maxHashValue = hashValueOfT;
			}
			
			if(hashValueOfT<minHashValue)
			{
				minHashValue = hashValueOfT;
			}
		}

		//Now sort the hashes
		//This has to be in another array
		ArrayList<Double> sortedHashes = new ArrayList<Double>(calculatedHashes);
		Collections.sort(sortedHashes);
		
		t1 = sortedHashes.get(0);
		t2 = sortedHashes.get(sortedHashes.size()/2);
		
		t1t2Defined = true;
		try {
			calculateAllBooleanHashes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
	}

	/**
	 * This method just calculates all the boolean values for the hashes,
	 * This is meant to be run once t1 and t2 have been defined and all calculated hashes are in place.
	 * @throws Exception 
	 */
	private void calculateAllBooleanHashes() throws Exception
	{
		if(t1t2Defined)
		{
			for(int i = 0; i<calculatedHashes.size();i++)
			{
			calculatedHashBool.add(hashToBoolean(calculatedHashes.get(i)));
			}
		}else
		{
			throw new Exception("T1 and T2 need to be defined for this to work");
		}
	}
	
	/**
	 * This method is deprecated now.
	 * This method finds T1 and T2 limits for a given hashing function
	 * @param trajectories: a set of trajectories
	 * @param hf: a hashing function
	 */
	public void oldFindT1T2(ArrayList<Trajectory> trajectories)
	{
		//todo: Make it work for samples (need sample size)
		
		t1 = Double.NEGATIVE_INFINITY;
		t2 = 0;
		double minHashValue = Double.POSITIVE_INFINITY;
		double maxHashValue = Double.NEGATIVE_INFINITY;
		
		for(Trajectory t: trajectories)
		{
			Double hashValueOfT = hash(t);
			
			if(hashValueOfT>maxHashValue)
			{
				maxHashValue = hashValueOfT;
			}
			
			if(hashValueOfT<minHashValue)
			{
				minHashValue = hashValueOfT;
			}
		}

		t2 = (float) ((maxHashValue + minHashValue)/2);
		t1t2Defined = true;
	}

	public double getT1() {
		return t1;
	}

	public void setT1(double t1) {
		this.t1 = t1;
	}

	public double getT2() {
		return t2;
	}

	public void setT2(double t2) {
		this.t2 = t2;
	}

	public boolean isT1t2Defined() {
		return t1t2Defined;
	}

	public void setT1t2Defined(boolean t1t2Defined) {
		this.t1t2Defined = t1t2Defined;
	}

	public ArrayList<Double> getCalculatedHashes() {
		return calculatedHashes;
	}

	public void setCalculatedHashes(ArrayList<Double> calculatedHashes) {
		this.calculatedHashes = calculatedHashes;
	}

	public ArrayList<Boolean> getCalculatedHashBool() {
		return calculatedHashBool;
	}

	public void setCalculatedHashBool(ArrayList<Boolean> calculatedHashBool) {
		this.calculatedHashBool = calculatedHashBool;
	}
}
