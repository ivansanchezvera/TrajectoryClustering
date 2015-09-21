package cluster.trajectory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import cluster.trajectory.*;

import com.stromberglabs.cluster.Clusterable;

import fastdtw.com.dtw.DTW;


public class Traclus {

	private ArrayList<Trajectory> trajectories;
	private ArrayList<Segment> segmentsCompleteSet;
	private ArrayList<Cluster> clusterOfTrajectories;
	private float eNeighborhoodParameter;
	private int minLins;
	private int cardinalityOfClusters;
	
	private double epsilonDouglasPeucker;
	private int fixedNumOfTrajectoryPartitionsDouglas;
	
	//To select Segmentation Method
	SegmentationMethod segmentationMethod;
	//Parameters
	//Notion of E Neighbourhood
	//Notion of acceptable time distance
	//
	ArrayList<Cluster> setOfClusters;
	
	//Constructor for Douglas Peucker Partition Traclus
	public Traclus(ArrayList<Trajectory> trajectories, float eNeighborhoodParameter, int minLins, int cardinalityOfClusters, 
			double epsilonDouglas, int fixedNumberTrajPartitionDouglas, SegmentationMethod segmentationMethod) {
		this.trajectories = trajectories;
		this.segmentsCompleteSet = new ArrayList<Segment>();
		this.clusterOfTrajectories = new ArrayList<Cluster>();
		this.eNeighborhoodParameter = eNeighborhoodParameter;
		this.minLins = minLins;
		this.cardinalityOfClusters = cardinalityOfClusters;
		this.fixedNumOfTrajectoryPartitionsDouglas = fixedNumberTrajPartitionDouglas;
		this.epsilonDouglasPeucker = epsilonDouglas;
		this.segmentationMethod = segmentationMethod;
	}
	
	//Constructor for Original Partition Method Traclus
	public Traclus(ArrayList<Trajectory> trajectories, float eNeighborhoodParameter, int minLins, int cardinalityOfClusters, SegmentationMethod segmentationMethod) {
		this.trajectories = trajectories;
		this.segmentsCompleteSet = new ArrayList<Segment>();
		this.clusterOfTrajectories = new ArrayList<Cluster>();
		this.eNeighborhoodParameter = eNeighborhoodParameter;
		this.minLins = minLins;
		this.cardinalityOfClusters = cardinalityOfClusters;
		this.segmentationMethod = segmentationMethod;
	}
	
	public ArrayList<Cluster> executeTraclus()
	{
		segmentsCompleteSet = partition(trajectories);
		
		
		long startTime = System.nanoTime();
		
		//for Traclus clustering approach, cluster over segments
		clusterOfTrajectories = clusterSegments(segmentsCompleteSet, eNeighborhoodParameter, minLins, cardinalityOfClusters);
		

		long stopTime = System.nanoTime();
		long finalTimeInSeconds = (stopTime - startTime)/1000000000;
		System.out.println("Clustering Execution time in seconds: " + (finalTimeInSeconds));
		
		return clusterOfTrajectories;
	}
	
	/**
	 * To do the density base clustering over the whole trajectory
	 * and using DTW as a distance (similarity metric).
	 * @return
	 */
	public ArrayList<Cluster> executeDensityBasedClusterOverTrajectories()
	{
		ArrayList<Trajectory> workingTrajectories = trajectories;
		
		long startTime = System.nanoTime();
		
		//For new Rao Approach, do clustering over trajectories.
		//Form clustering over DTW
		clusterOfTrajectories = clusterTrajectoriesWithDTW(workingTrajectories, eNeighborhoodParameter, minLins, cardinalityOfClusters);
		

		long stopTime = System.nanoTime();
		long finalTimeInSeconds = (stopTime - startTime)/1000000000;
		System.out.println("Clustering Execution time in seconds: " + (finalTimeInSeconds));
		
		return clusterOfTrajectories;
	}
	
	public ArrayList<Cluster> executeKMeansClusterOverTrajectories(int k)
	{
		//ArrayList<Trajectory> simplifiedTrajectories = simplifyTrajectories(trajectories, true, segmentationMethod, fixedNumOfTrajectoryPartitionsDouglas);
		
		ArrayList<Trajectory> workingTrajectories = trajectories;
		long startTime = System.nanoTime();
		
		//Export trajectories to visualize them
		//exportPlotableCoordinatesForAllTrajectories(simplifiedTrajectories);
		
		//For new Rao Approach, do clustering over trajectories.
		//Form clustering over DTW
		clusterOfTrajectories = clusterTrajectoriesKMeans(workingTrajectories, k);		
		
		long stopTime = System.nanoTime();
		long finalTimeInSeconds = (stopTime - startTime)/1000000000;
		System.out.println("Clustering Execution time in seconds: " + (finalTimeInSeconds));
		
		return clusterOfTrajectories;
	}
	
	public ArrayList<Cluster> executeKMedoidsClusterOverTrajectories(int k) {

		ArrayList<Trajectory> workingTrajectories = trajectories;
		
		
		long startTime = System.nanoTime();
		
		//Export trajectories to visualize them
		//exportPlotableCoordinatesForAllTrajectories(simplifiedTrajectories);
		
		//Now we are trying to obtain centroids based in DTW metric and not in Euclideand Distance like K-Means
		clusterOfTrajectories = clusterTrajectoriesKMedoids(workingTrajectories, k);		
		
		long stopTime = System.nanoTime();
		long finalTimeInSeconds = (stopTime - startTime)/1000000000;
		System.out.println("Clustering Execution time in seconds: " + (finalTimeInSeconds));
		
		return clusterOfTrajectories;
	}
	
	public ArrayList<Cluster> executeKmeansDTW(int k) {
	
		ArrayList<Trajectory> workingTrajectories = trajectories;

		long startTime = System.nanoTime();

		//Export trajectories to visualize them
		//exportPlotableCoordinatesForAllTrajectories(simplifiedTrajectories);
		
		//Now we are trying to obtain centroids based in DTW metric and not in Euclideand Distance like K-Means
		clusterOfTrajectories = clusterTrajectoriesKMeansDTW(workingTrajectories, k);		
		
		long stopTime = System.nanoTime();
		long finalTimeInSeconds = (stopTime - startTime)/1000000000;
		System.out.println("Clustering Execution time in seconds: " + (finalTimeInSeconds));
		
		return clusterOfTrajectories;
	}
	

	public ArrayList<Cluster> executeDBHApproximationOfClusterOverTrajectories(int l, int numBits, int minNumElems, boolean merge, float mergeRatio)
	{
		ArrayList<Trajectory> workingTrajectories = trajectories;
				
		long startTime = System.nanoTime();
		
		//Export trajectories to visualize them
		//exportPlotableCoordinatesForAllTrajectories(simplifiedTrajectories);
		
		//For new Rao Approach, do clustering over trajectories.
		//Form clustering over DTW
		try {
			//Old clustering with t1, t2 precalculated for all functions (wrong way but gave results)
			//clusterOfTrajectories = approximateClustersDBH(simplifiedTrajectories, l, numBits, t1, t2, minNumElems, merge, mergeRatio);
			
			//New corrected way as suggested by zay
			clusterOfTrajectories = approximateClustersDBH(workingTrajectories, l, numBits, minNumElems, merge, mergeRatio);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Error, some hash functions have no interval t1-t2 defined.");
			System.err.print(e.getMessage());
			e.printStackTrace();
		}
		
		long stopTime = System.nanoTime();
		long finalTimeInSeconds = (stopTime - startTime)/1000000000;
		System.out.println("Clustering Execution time in seconds: " + (finalTimeInSeconds));
		
		
		return clusterOfTrajectories;
	}
	
	public ArrayList<Cluster> executeLSHEuclidean() {

		ArrayList<Trajectory> workingTrajectories = trajectories;
		
		long startTime = System.nanoTime();
		
		try {

			//clusterOfTrajectories = approximateClustersLSHEuclidean(workingTrajectories, l, numBits);

		} catch (Exception e) {
			System.err.print(e.getMessage());
			e.printStackTrace();
		}
		
		long stopTime = System.nanoTime();
		long finalTimeInSeconds = (stopTime - startTime)/1000000000;
		System.out.println("Clustering Execution time in seconds: " + (finalTimeInSeconds));
	
		return clusterOfTrajectories;
	}
	
	//3 clear stages
	//Partition Phase
	//Input Set of Trajectories
	public ArrayList<Segment> partition(ArrayList<Trajectory> trajectories)
	{
		ArrayList<Segment> setOfSegments = new ArrayList<Segment>();
		
		for(Trajectory t:trajectories)
		{
			
			//For Traclus Processing of Partition of trajectory into segments
			if(segmentationMethod == SegmentationMethod.traclus)
			setOfSegments.addAll(t.divideTrajectoryInSegmentsTraclus());
			
			if(segmentationMethod == SegmentationMethod.douglasPeucker)
			//For Douglas-Peucker Partition of trajectory into segments
			setOfSegments.addAll(t.divideTrajectoryInSegmentsDouglasPeucker(epsilonDouglasPeucker, fixedNumOfTrajectoryPartitionsDouglas));
		}
		
		return setOfSegments;
	}
	
	/**
	 * Method to simplify trajectories, with Douglas-Peucker (DP) or the Traclus approach (MDL)
	 * @param trajectories : A list of trajectories to simplify
	 * @param strict : Determine wether trajectories with less points that the max number of partitions (numberOfPartitions) get removed from the
	 * final set of simplified trajectories. True means that trajectories with less points that the number of partitions provoke an errors, since all the 
	 * trajectories in the resulting set of trajectories in strict mode should have same length. False means trajectories with less points than the specified
	 * max number of partitions are ignored for simplification and simply added to the resulting dataset with no modification (resulting dataset includes
	 * all trajectories, and all dont have the same lenght, although the maximun lenght of any given trajectory is no more that number of partitions).
	 * @param segmentationMethod : Determines the method to partition the trajectories: Douglas-Peucker (DP) or Traclus (MDL)
	 * @param numberOfPartitions : Specifies the maximun number of partitions that any resulting trajectory in the set should have.
	 * @return
	 */
	public static ArrayList<Trajectory> simplifyTrajectories(ArrayList<Trajectory> trajectories,boolean strict, SegmentationMethod segmentationMethod, int numberOfPartitions)
	{
		ArrayList<Trajectory> setOfSimplifiedTrajectories = new ArrayList<Trajectory>();
		int error = 0;
		for(Trajectory t:trajectories)
		{
			//System.out.println(t.printSummary());
			
			if(segmentationMethod == SegmentationMethod.douglasPeucker)
			{
			//For Douglas-Peucker simplification of trajectories
				//Trajectory simplifiedTrajectory = t.simplifyTrajectoryDouglasPeucker(epsilonDouglasPeucker, fixedNumOfTrajectoryPartitionsDouglas);
				//Epsilon is 0 cause we dont care about it, just about the number of points and not an approximate threshold like epsilon.
				Trajectory simplifiedTrajectory = t.simplifyTrajectoryDouglasPeucker(0, numberOfPartitions);
				if(strict)
				{
					//Number of partitions = number of points - 1
					
					//This was here before, for traclust I guess. Now we use number of points instead of number of segments.
					//if(simplifiedTrajectory.getPoints().size()>=numberOfPartitions+1)
					if(simplifiedTrajectory.getPoints().size()>=numberOfPartitions)
					{
					setOfSimplifiedTrajectories.add(simplifiedTrajectory);
					
					//*****************Just to print the simplified trajectories****************
					//System.out.println(simplifiedTrajectory.printLocation());
					//simplifiedTrajectory.exportPlotableCoordinates();
					//simplifiedTrajectory.exportPlotableCoordinatesCSV();
					//Just to print the simplified trajectories
					//interrupt();
					//*****************End of Print trajectories****************
					}else{
						error++;
						System.out.println(error + ". Error could not convert trajectory " + t.getTrajectoryId() + 
								" into the desired number of points. Needed number of points:  " + numberOfPartitions +
								", num points in simplified trajectory: " + simplifiedTrajectory.getPoints().size());
						
						//interrupt();
					}
				}else{
					setOfSimplifiedTrajectories.add(simplifiedTrajectory);
				}
			}
		}
				
		return setOfSimplifiedTrajectories;
	}
	
	//Only to stop the flow of information in console
	private void interrupt()
	{
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void exportPlotableCoordinatesForAllTrajectories(ArrayList<Trajectory> trajectories)
	{
		try {
			String filename = "exportedTrajectoriesToPlot.txt";
			File file = new File(filename);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(Trajectory t:trajectories)
			{
					//*****************Just to print the simplified trajectories****************
					//System.out.println(simplifiedTrajectory.printLocation());
					bw.write(t.printToPlotWithOtherTrajectories());
					//Just to print the simplified trajectories
					//interrupt();
					//*****************End of Print trajectories***************
			}
			bw.close();
			//System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//********************HASHING STAGE*********************************
	
	public double maxDTW(Trajectory t, ArrayList<Trajectory> simplifiedTrajectories)
	{
		
		double maxDTW = 0;
		for(Trajectory t0:simplifiedTrajectories)
		{
			 double tempDist = Trajectory.calculateDTWDistance(t,t0);
			 if(maxDTW<tempDist)
			 {
				 maxDTW = tempDist;
			 }
		}
		
		System.out.println("Maximun DTW distance from 0" + maxDTW);
		return maxDTW;
		
	}
	
	//DBH HASHING
		public ArrayList<Cluster> approximateClustersDBH(ArrayList<Trajectory> simplifiedTrajectories, int l, int k, int minNumElems, boolean merge, float mergeRatio) throws Exception
		{
			

			//Verify the interval???
			
			//Let X be a non metric space and D a distance function defined in X (X,D).
			//Let H be a family of hash functions h:X->Z, where Z is the set of integers
			//First pick integers k and l.
			//Then construct l hash functions g1,g2,...,gl as concatenations of k functions
			//chosen randomly from the family H.
			// gi(x) = (hi1(x),hi2(x),...,hik(x))
			// each db object is stored in each of the l hash tables defined by the functions of gi.
			
			//Given a Query object Q that belongs to space X
			//retrival process first identifies all db objects that fall in the same bucket as Q
			//in at least one of the l hash tables and then EXACT distances are measure between the query and the objects
			
			//If the measure is not assumed to be an Euclidean one, then we have to treat it like a blackbox.
			//We cannot assume anything about the geometrical properties in that space, hence the LSH properties dont hold.
			
			//SO
			//Propose a family of HASH Functions defined only by the distances between the objects
			
			//we need to define a function that maps an arbitrary space (X,D) into real number R.
			//an example is the line projection function like:
			
			/*
			double ds1s2 = Trajectory.calculateDTWDistance(s1,s2);
			double dts1 = Trajectory.calculateDTWDistance(t,s1);
			double dts2 = Trajectory.calculateDTWDistance(t,s2);
			
			double hash = (Math.pow(dts1, 2) + Math.pow(ds1s2, 2) - Math.pow(dts2, 2))/(2*ds1s2);
			*/
			
			//If (X,D) was an euclidean space, the function defined by hash should had computed the projection of point X on the lines dfined by X1 and X2
			//If X is non euclidean space, then there is no geometrical interpretation of function Hash
			
			//But as long as there is a distance measure D, such as DTW, Hash can still be defined and can project the X space into the space Real numbers R
			//The function defined by Hash is a really rich family, any pair of objects can define a different function.
			//With n objects we can define n^2/2 functions
			
			//Since function H provides real numbers and we need discrete numbers, we need to set thresholds t1,t2 that belong to R
			
			//We want binary so:
			//Hash(t1,t2,X) = 0 if hash(x) belongs to interval [t1,t2]
			//Hash(t1,t2,X) = 1 otherwise
			
			//Now the problem is to choose t1 and t2, such that half of the objects are in 0 and half are in 1.
			//We want to have balanced hash tables so that is why we want to map hash values into half and half.
			//Formally, there should be a set V(x1,x2) of intervals [t1,t2], such that every pair of values x1,x2 that belongs to space X
			//so  that hash(t1,t2,X1,X2,X) splits the space in half.
			
			//Almost for every t there exist a t' such that H(X1,X2) maps half of the objects of X to either [t,t'] or to [t',t]
			//For a set of N objects thers are n/2 ways of spliting the set int 2 equal-sized subsets based in the choice of [t1,t2] that belong to V(x1,x2),
			//One alternative is the interval [t1,infinite] such that Hash(x1,x2,X0) is less than t1 for half the objects X that belong to X.
			//The set V(x1,x2) contains all the intervasl to split X into 2 equal subsets.
			
			//Now we an define a family HDBH of hash functions for an arbitrary space (X,D):
			
			//HDBH : {F(x1,x2,t1,t2) for each x1,x2 that belongs to X space, [t1,t2] belong to V(X1,X2)}
			
			//We need to use binary hash functions h form HDBH to define k-bit hash functions gi. gi(X) = (hi1(x),hi2(x),...,hik(x))
			
			//so, to index or retrieve we need to::
			//1.Choosing parameters k and l.
			//2.Constructing l k-bit hash tables, and storing pointers to each database object at the appropriate l buckets.
			//3. Comparing the query object with the database objects found in the l hash table buckets that the query is mapped to.
			
			//Choose randomly 10% of objects to try (so like 29 for LABOMNI)
			//To create the DBH families
			//Pick randonly 29 elements to crate a small subset 
			//For each pair of objects X1,X2 from that subset, create a binary hash function
			//Choosing randomly an interval [t1,t2] of V(X1,X2)
			//Approximately C(n,r) = C(29,2) = 406 functions
			
			//For me K and L are:
			//L 15 cause of the number of clusters in final dataset (cheating??)
			//K is 8+1 cause that is the number of partitions for a given trajectoru
			
			//Create family of functions H
			ArrayList<HashingFunction> hashingFamily = new ArrayList<HashingFunction>();
		

			//to select random trajectory elements
			Random r = new Random();
			
			ArrayList<ConcatenatedHashingFuntions> lkBitFunctions = new ArrayList<ConcatenatedHashingFuntions>(l);
			
			//First obtain the set of V of intervals that make the function split in halt
			//?? T1 and t2 should come from here
			//Select thresholds CORRECTLY, this is shit!

			for(int i=0; i<l; i++)
			{
				ConcatenatedHashingFuntions chf = new ConcatenatedHashingFuntions(k);
				
				while(!chf.isConcatenationComplete())
				{
					//First get 2 random members (trajectories)
					Trajectory s1 = simplifiedTrajectories.get(r.nextInt(simplifiedTrajectories.size()));
					Trajectory s2 = simplifiedTrajectories.get(r.nextInt(simplifiedTrajectories.size()));
					
					//Create the hashing function and calculate the interval t1-t2
					HashingFunction newHF = new HashingFunction(s1,s2);
					newHF.findT1T2(simplifiedTrajectories);
					
					chf.concatenate(newHF);
				}
				
				lkBitFunctions.add(chf); 
			}
			
			//now create hash tables and hash
			ArrayList<HashTable> hashTables = new ArrayList<HashTable>();
			
			//Initialize hash tables
			for(int w=0; w<lkBitFunctions.size();w++)
			{
				HashTable ht = new HashTable(w, k);
				hashTables.add(ht);
			}
			
			//now hash all trajectories
			//Seems like a extremely expensive process
			/*
			 * This code works but needs recalculating clusters
			for(Trajectory t0:simplifiedTrajectories)
			{
				for(int w=0; w<lkBitFunctions.size();w++)
				{
					ConcatenatedHashingFuntions tempCHF = lkBitFunctions.get(w);
					//now hash and index all in once
					
					//This requires recalculating the hashes
					hashTables.get(w).addToBucket(t0.getTrajectoryId(), tempCHF.execute(t0));
				}
			}
			*/
			

			for(int w=0; w<lkBitFunctions.size();w++)
			{
				ConcatenatedHashingFuntions tempCHF = lkBitFunctions.get(w);
				//now hash and index all in once
					
				//This requires recalculating the hashes
				//hashTables.get(w).addToBucket(t0.getTrajectoryId(), tempCHF.execute(t0));
					
				hashTables.get(w).addAllToBucket(simplifiedTrajectories, tempCHF.execute(simplifiedTrajectories));
			}
				
			//Now create the clusters, this seems infeasible
			ArrayList<ApproximatedSetOfCluster> listApproximatedSetClusters = new ArrayList<ApproximatedSetOfCluster>();
			
			//For each hash table bring me only K top buckets with more elements

			for(HashTable ht: hashTables)
			{
				ApproximatedSetOfCluster approxSetCluster = new ApproximatedSetOfCluster();
				
				for(HashBucket hb: ht.buckets)
				{
					if(hb!=null)
					{
						if(hb.bucketElements.size()>=minNumElems)
						{
							approxSetCluster.possibleClusters.add(hb);
						}
					}
				}
				listApproximatedSetClusters.add(approxSetCluster);
			}
			
			//Definitive set of clusters
			ApproximatedSetOfCluster finalCluster = new ApproximatedSetOfCluster();
			
			//First Prune Them
			//Purging the clusters means just getting the top L from each hash table(top = more members).
			for(ApproximatedSetOfCluster approxSetCluster: listApproximatedSetClusters)
			{
				//approxSetCluster.pruneApproximatedSetOfClusters(l);
			}
			
			//Merging clusters between the different hash sets (clusters-bucket) from the
			//different hash tables. If we merge we obtain more members in the clusters
			//But is more expensive and imprecise
			if(merge)
			{
				//Just validate and get the first Set of clusters (first hash table), this is for the merge
				if(listApproximatedSetClusters.size()>0)
				{
					finalCluster = new ApproximatedSetOfCluster(listApproximatedSetClusters.get(0));
				}

				//Now intersect and merge
				for(int w=1; w<listApproximatedSetClusters.size();w++)
				{
					finalCluster = ApproximatedSetOfCluster.mergeApproximatedSetCluster(finalCluster, listApproximatedSetClusters.get(w), minNumElems, mergeRatio);
				}
			}else{
				//Just because its faster, get an Approx Cluster at random with no merge
				//Get a random set of clusters from the hash table
				finalCluster = listApproximatedSetClusters.get(r.nextInt(listApproximatedSetClusters.size()));
			}
				
			//My common representation of set of Clusters
			ArrayList<Cluster> finalListClusterRepresentation = new ArrayList<Cluster>();
			//Now transform to the common representation
			int v = 0;
			for(HashBucket hb:finalCluster.possibleClusters)
			{
				Cluster ct = new Cluster(v, "Cluster"+v);
			
				for(Integer id:hb.bucketElements)
				{
					ct.addElement(simplifiedTrajectories.get(id));
				}
				
				finalListClusterRepresentation.add(ct);
				v++;
			}
			
			//At the end, here, Clustering with DBScan DTW should be done inside each cluster
			//and taking the biggest one (more elements) as the real one. This is only to get rid of noise (false positives)
			
			return finalListClusterRepresentation;
		}
	
	//DBH HASHING
	public ArrayList<Cluster> approximateClustersDBHOld(ArrayList<Trajectory> simplifiedTrajectories, int l, int k, float t1, float t2, int minNumElems, boolean merge, float mergeRatio) throws Exception
	{
		

		//Verify the interval???
		
		//Let X be a non metric space and D a distance function defined in X (X,D).
		//Let H be a family of hash functions h:X->Z, where Z is the set of integers
		//First pick integers k and l.
		//Then construct l hash functions g1,g2,...,gl as concatenations of k functions
		//chosen randomly from the family H.
		// gi(x) = (hi1(x),hi2(x),...,hik(x))
		// each db object is stored in each of the l hash tables defined by the functions of gi.
		
		//Given a Query object Q that belongs to space X
		//retrival process first identifies all db objects that fall in the same bucket as Q
		//in at least one of the l hash tables and then EXACT distances are measure between the query and the objects
		
		//If the measure is not assumed to be an Euclidean one, then we have to treat it like a blackbox.
		//We cannot assume anything about the geometrical properties in that space, hence the LSH properties dont hold.
		
		//SO
		//Propose a family of HASH Functions defined only by the distances between the objects
		
		//we need to define a function that maps an arbitrary space (X,D) into real number R.
		//an example is the line projection function like:
		
		/*
		double ds1s2 = Trajectory.calculateDTWDistance(s1,s2);
		double dts1 = Trajectory.calculateDTWDistance(t,s1);
		double dts2 = Trajectory.calculateDTWDistance(t,s2);
		
		double hash = (Math.pow(dts1, 2) + Math.pow(ds1s2, 2) - Math.pow(dts2, 2))/(2*ds1s2);
		*/
		
		//If (X,D) was an euclidean space, the function defined by hash should had computed the projection of point X on the lines dfined by X1 and X2
		//If X is non euclidean space, then there is no geometrical interpretation of function Hash
		
		//But as long as there is a distance measure D, such as DTW, Hash can still be defined and can project the X space into the space Real numbers R
		//The function defined by Hash is a really rich family, any pair of objects can define a different function.
		//With n objects we can define n^2/2 functions
		
		//Since function H provides real numbers and we need discrete numbers, we need to set thresholds t1,t2 that belong to R
		
		//We want binary so:
		//Hash(t1,t2,X) = 0 if hash(x) belongs to interval [t1,t2]
		//Hash(t1,t2,X) = 1 otherwise
		
		//Now the problem is to choose t1 and t2, such that half of the objects are in 0 and half are in 1.
		//We want to have balanced hash tables so that is why we want to map hash values into half and half.
		//Formally, there should be a set V(x1,x2) of intervals [t1,t2], such that every pair of values x1,x2 that belongs to space X
		//so  that hash(t1,t2,X1,X2,X) splits the space in half.
		
		//Almost for every t there exist a t' such that H(X1,X2) maps half of the objects of X to either [t,t'] or to [t',t]
		//For a set of N objects thers are n/2 ways of spliting the set int 2 equal-sized subsets based in the choice of [t1,t2] that belong to V(x1,x2),
		//One alternative is the interval [t1,infinite] such that Hash(x1,x2,X0) is less than t1 for half the objects X that belong to X.
		//The set V(x1,x2) contains all the intervasl to split X into 2 equal subsets.
		
		//Now we an define a family HDBH of hash functions for an arbitrary space (X,D):
		
		//HDBH : {F(x1,x2,t1,t2) for each x1,x2 that belongs to X space, [t1,t2] belong to V(X1,X2)}
		
		//We need to use binary hash functions h form HDBH to define k-bit hash functions gi. gi(X) = (hi1(x),hi2(x),...,hik(x))
		
		//so, to index or retrieve we need to::
		//1.Choosing parameters k and l.
		//2.Constructing l k-bit hash tables, and storing pointers to each database object at the appropriate l buckets.
		//3. Comparing the query object with the database objects found in the l hash table buckets that the query is mapped to.
		
		//Choose randomly 10% of objects to try (so like 29 for LABOMNI)
		//To create the DBH families
		//Pick randonly 29 elements to crate a small subset 
		//For each pair of objects X1,X2 from that subset, create a binary hash function
		//Choosing randomly an interval [t1,t2] of V(X1,X2)
		//Approximately C(n,r) = C(29,2) = 406 functions
		
		//For me K and L are:
		//L 15 cause of the number of clusters in final dataset (cheating??)
		//K is 8+1 cause that is the number of partitions for a given trajectoru
		
		//Create family of functions H
		ArrayList<HashingFunction> hashingFamily = new ArrayList<HashingFunction>();
	

		//to select random trajectory elements
		Random r = new Random(simplifiedTrajectories.size()-1);
		
		//choose l functions composed of
		Random gFunction = new Random(k);
		
		ArrayList<ConcatenatedHashingFuntions> lkBitFunctions = new ArrayList<ConcatenatedHashingFuntions>(l);
		
		//First obtain the set of V of intervals that make the function split in halt
		//?? T1 and t2 should come from here
		//Select thresholds CORRECTLY, this is shit!

		for(int i=0; i<l; i++)
		{
			ConcatenatedHashingFuntions chf = new ConcatenatedHashingFuntions(k);
			
			while(!chf.isConcatenationComplete())
			{
				//First get 2 random members (trajectories)
				Trajectory s1 = simplifiedTrajectories.get(r.nextInt(simplifiedTrajectories.size()));
				Trajectory s2 = simplifiedTrajectories.get(r.nextInt(simplifiedTrajectories.size()));
				
				HashingFunction newHF = new HashingFunction(s1,s2,t1,t2);
				chf.concatenate(newHF);
			}
			
			lkBitFunctions.add(chf); 
		}
		
		//now create hash tables and hash
		ArrayList<HashTable> hashTables = new ArrayList<HashTable>();
		
		//Initialize hash tables
		for(int w=0; w<lkBitFunctions.size();w++)
		{
			HashTable ht = new HashTable(w, k);
			hashTables.add(ht);
		}
		
		//now hash all trajectories
		//Seems like a extremely expensive process
		for(Trajectory t0:simplifiedTrajectories)
		{
			for(int w=0; w<lkBitFunctions.size();w++)
			{
				ConcatenatedHashingFuntions tempCHF = lkBitFunctions.get(w);
				//now hash and index all in once
				hashTables.get(w).addToBucket(t0.getTrajectoryId(), tempCHF.execute(t0));
			}
		}
		
		
		//Now create the clusters, this seems infeasible
		ArrayList<ApproximatedSetOfCluster> listApproximatedSetClusters = new ArrayList<ApproximatedSetOfCluster>();
		
		//For each hash table bring me only K top buckets with more elements

		for(HashTable ht: hashTables)
		{
			ApproximatedSetOfCluster approxSetCluster = new ApproximatedSetOfCluster();
			
			for(HashBucket hb: ht.buckets)
			{
				if(hb!=null)
				{
					if(hb.bucketElements.size()>=minNumElems)
					{
						approxSetCluster.possibleClusters.add(hb);
					}
				}
			}
			listApproximatedSetClusters.add(approxSetCluster);
		}
		
		//Definitive set of clusters
		ApproximatedSetOfCluster finalCluster = new ApproximatedSetOfCluster();
		
		//First Prune Them
		//Purging the clusters means just getting the top L from each hash table(top = more members).
		for(ApproximatedSetOfCluster approxSetCluster: listApproximatedSetClusters)
		{
			//approxSetCluster.pruneApproximatedSetOfClusters(l);
		}
		
		//Merging clusters between the different hash sets (clusters-bucket) from the
		//different hash tables. If we merge we obtain more members in the clusters
		//But is more expensive and imprecise
		if(merge)
		{
			//Just validate and get the first Set of clusters (first hash table), this is for the merge
			if(listApproximatedSetClusters.size()>0)
			{
				finalCluster = new ApproximatedSetOfCluster(listApproximatedSetClusters.get(0));
			}

			//Now intersect and merge
			for(int w=1; w<listApproximatedSetClusters.size();w++)
			{
				finalCluster = ApproximatedSetOfCluster.mergeApproximatedSetCluster(finalCluster, listApproximatedSetClusters.get(w), minNumElems, mergeRatio);
			}
		}else{
			//Just because its faster, get an Approx Cluster at random with no merge
			//Get a random set of clusters from the hash table
			finalCluster = listApproximatedSetClusters.get(r.nextInt(listApproximatedSetClusters.size()));
		}
			
		//My common representation of set of Clusters
		ArrayList<Cluster> finalListClusterRepresentation = new ArrayList<Cluster>();
		//Now transform to the common representation
		int v = 0;
		for(HashBucket hb:finalCluster.possibleClusters)
		{
			Cluster ct = new Cluster(v, "Cluster"+v);
		
			for(Integer id:hb.bucketElements)
			{
				ct.addElement(simplifiedTrajectories.get(id));
			}
			
			finalListClusterRepresentation.add(ct);
			v++;
		}
		
		//At the end, here, Clustering with DBScan DTW should be done inside each cluster
		//and taking the biggest one (more elements) as the real one. This is only to get rid of noise (false positives)
		
		return finalListClusterRepresentation;
	}
	
	
	
	//Clustering Phase
	public ArrayList<Cluster> clusterSegments(ArrayList<Segment> setOfSegments, float eNeighborhoodParameter, int minLins, int cardinalityOfClusters)
	{
		setOfClusters = new ArrayList<Cluster>();
		
		int clusterId = 0;
		
		for(Segment s: setOfSegments)
		{
			if(!s.isClassified())
			{
				//To compute eNeighboorhood is a hard part
				//Cost n^2 unless we used index like R-Tree which reduces complexity to (n log n)
				//where n is the number of segments in the database.
				//Find Out how to index this.
				//MOre info at the end of page 7 (599) of paper
				ArrayList<Segment> neighborSegments = computeENeighborhoodOfSegment(s, eNeighborhoodParameter, setOfSegments);
				
				//Plus 1 cause my definition of neighborhood does not include core line itself
				if(neighborSegments.size()+1>= minLins)
				{
					ClusterSegments c = new ClusterSegments(clusterId, "Cluster " + clusterId);
					setOfClusters.add(c);
					
					//Before adding to cluster,
					//Should I set all segments of the neighborhood as classified??
					int i = 0;
					for(Segment s1:neighborSegments)
					{
						
						
						s1.setClassified(true);
						s1.setNoise(false);
						//neighborSegments.set(i, s1);
						c.addSegment(s1);
						i++;
					}
					
					//Add neighbours to cluster
					//My definition of neighborhood does not include the core line itself
					//This line is extra since I am already iterating, might be little more efficient?
					//c.setSegments(neighborSegments);
					
					//Add this core line to this cluster
					s.setNoise(false);
					s.setClassified(true);
					c.addSegment(s);
					
					//Insert Neighboors into a Queue (check this part)
					ArrayList<Segment> queue = new ArrayList<Segment>();
					queue.addAll(neighborSegments);
					
					//Now Expand Cluster
					expandClusterSegments(queue, clusterId, eNeighborhoodParameter, minLins, setOfSegments);
					clusterId++;
				}else{
					s.setClassified(true);
					s.setNoise(true);
				}
			}
		}
		
		//Step 3
		//Clusters are already allocated in my code, this differs from paper description of Traclus.
		//Check cardinality

		ArrayList<Cluster> filteredSetOfClusters = filterClustersByCardinality(cardinalityOfClusters, setOfClusters);
		
		//Cluster using Density-based method
		//Need a notion of distance and a notion of time difference
		
		return filteredSetOfClusters;
		
	}

	/**
	 * Can be refactored to use memory in a more efficient way
	 * @param cardinalityOfClusters
	 * @param setOfClusters2 
	 * @return 
	 */
	private ArrayList<Cluster> filterClustersByCardinality(int cardinalityOfClusters, ArrayList<Cluster> setOfClusters2) {
		ArrayList<Cluster> clustersWithDesiredCardinality = new ArrayList<Cluster>();
		for(Cluster c:setOfClusters2)
		{
			if(c.calculateCardinality()>=cardinalityOfClusters)
			{
				clustersWithDesiredCardinality.add(c);
			}
		}
		
		 return clustersWithDesiredCardinality;
	}

	public ArrayList<Cluster> clusterTrajectoriesWithDTW(ArrayList<Trajectory> simplifiedTrajectories, float eNeighborhoodParameter, int minLins, int cardinalityOfClusters)
	{
		setOfClusters = new ArrayList<Cluster>();
		
		int clusterId = 0;
		
		for(Trajectory t: simplifiedTrajectories)
		{
			if(!t.isClassified())
			{
				//This is the Traclus density based algorithm modified to 
				//cluster over whole trajectories, I assume this is similar to DBScan.
				
				ArrayList<Trajectory> neighborTrajectories = computeENeighborhoodOfTrajectory(t, eNeighborhoodParameter, simplifiedTrajectories);
				
				//Plus 1 cause my definition of neighborhood does not include core line itself
				//MinLins is in this case the minimun number of trajectories in the cluster
				if(neighborTrajectories.size()+1>= minLins)
				{
					ClusterTrajectories c = new ClusterTrajectories(clusterId, "Cluster " + clusterId);
					
					setOfClusters.add(c);
					
					//Before adding to cluster,
					//Should I set all segments of the neighborhood as classified??
					for(Trajectory t1:neighborTrajectories)
					{
						if(!t1.isClassified())
						{	
						t1.setClassified(true);
						t1.setNoise(false);
						//neighborSegments.set(i, s1);
						c.addTrajectory(t1);
						}
					}
					
					//Add neighbors to cluster
					//My definition of neighborhood does not include the core line itself
					//This line is extra since I am already iterating, might be little more efficient?
					//c.setSegments(neighborSegments);
					
					//Add this core line to this cluster
					t.setNoise(false);
					t.setClassified(true);
					c.addTrajectory(t);
					
					//Insert Neighboors into a Queue (check this part)
					ArrayList<Trajectory> queue = new ArrayList<Trajectory>();
					queue.addAll(neighborTrajectories);
					
					//Now Expand Cluster
					expandClusterTrajectories(queue, clusterId, eNeighborhoodParameter, minLins, simplifiedTrajectories);
					c.calculateCardinality();
					clusterId++;
				}else{
					t.setClassified(true);
					t.setNoise(true);
				}
			}
		}
		
		//For cluster cardinality
		//not so needed like in segments but I can do it,
		//For now comment it for tests
		ArrayList<Cluster> filteredSetOfClusters = filterClustersByCardinality(cardinalityOfClusters, setOfClusters);
		//ArrayList<Cluster> filteredSetOfClusters = setOfClusters;
		//Cluster using Density-based method
		//Need a notion of distance and a notion of time difference
		
		return filteredSetOfClusters;
	}

	private ArrayList<Segment> computeENeighborhoodOfSegment(Segment s, float eParameter, ArrayList<Segment> allSegments) {
		// TODO Auto-generated method stub'
		ArrayList<Segment> neighborSegments = new ArrayList<Segment>();
		
		for(Segment possibleNeighborSegment:allSegments)
		{
			if(Segment.calculateDistance(s, possibleNeighborSegment)<=eParameter)
			{
				if(s!=possibleNeighborSegment)
				{
				neighborSegments.add(possibleNeighborSegment);
				}
			}
		}
		return neighborSegments;
	}
	
	/**
	 * Could add parameter (int maxNumIterations) to make space for other iterations 
	 * @param simplifiedTrajectories
	 * @param k
	 * @return
	 */
	private  ArrayList<Cluster> clusterTrajectoriesKMeans(ArrayList<Trajectory> simplifiedTrajectories, int k)
	{
		ArrayList<Cluster> kmeansClusters = new ArrayList<Cluster>();
		
		//here call kmeans
		com.stromberglabs.cluster.Cluster[] kmeansCluster = Kmeans.execute(simplifiedTrajectories, k);
		
		for(com.stromberglabs.cluster.Cluster c:kmeansCluster)
		{
			ClusterTrajectories tempMyCluster = new ClusterTrajectories(c.getId(), "kmeans" + c.getId());
			List<Clusterable> items = c.getItems();
			for(Clusterable i: items)
			{
				Trajectory t = (Trajectory) i; 
				tempMyCluster.addTrajectory(t);
			}
			tempMyCluster.calculateCardinality();
			kmeansClusters.add(tempMyCluster);
		}
		
		return kmeansClusters;
	}

	/**
	 * This method uses DTW distance to calculate new point that will be assumed as the center of the cluster
	 * similar to kmeans but we do not need to calculate a centroid, instead we choose points (trajectories) as
	 * the new center of the cluster until it converges 
	 * @param simplifiedTrajectories
	 * @param k
	 * @return
	 */
	private ArrayList<Cluster> clusterTrajectoriesKMedoids(
			ArrayList<Trajectory> simplifiedTrajectories, int k) {

		ArrayList<Cluster> kmedoidsClusters = new ArrayList<Cluster>();
		
		//here call kmedoids
		kmedoidsClusters = Kmedoids.execute(simplifiedTrajectories, k);
		
		return kmedoidsClusters;
	}
	
	/**
	 * This method uses DTW distance to calculate new point that will be assumed as the center of the cluster
	 * in the real Kmeans way 
	 * @param simplifiedTrajectories
	 * @param k
	 * @return
	 */
	private ArrayList<Cluster> clusterTrajectoriesKMeansDTW(
			ArrayList<Trajectory> simplifiedTrajectories, int k) {

		ArrayList<Cluster> kmeansDTWClusters = new ArrayList<Cluster>();
		
		//here call kmedoids
		kmeansDTWClusters = KmeansDTW.execute(simplifiedTrajectories, k);
		
		return kmeansDTWClusters;
	}

	//Step 2 - Clustering phase
	//To compute a density-connected set
	private void expandClusterSegments(ArrayList<Segment> queue, int clusterId,
			float eNeighborhoodParameter, int minLins, ArrayList<Segment> segmentsCompleteSet) {
		// TODO Auto-generated method stub
		while(!queue.isEmpty())
		{
			ArrayList<Segment> neighborhood = computeENeighborhoodOfSegment(queue.get(0), eNeighborhoodParameter, segmentsCompleteSet);
			
			if(Math.abs(neighborhood.size())>= minLins)
			{
				for(Segment s: neighborhood)
				{
					if(!s.isClassified() || s.isNoise())
					{
						//Cluster c = new Cluster(clusterId, "Cluster " + clusterId);
						ClusterSegments cTemp =  (ClusterSegments) setOfClusters.get(clusterId);
						s.setClassified(true);
						
						//This was before refactoring
						cTemp.addSegment(s);
						
						//Now it should be more generalized
						
						//setOfClusters.add(cTemp);
						setOfClusters.set(clusterId, cTemp);
					}
						//Check this part
					if(!s.isClassified())
					{
						//if element not in queue
						if(!queue.contains(s))
						{
						queue.add(s);
						}
					}
				}
			}
			queue.remove(0);
		}
	}
	
	//Step 2 - Clustering phase
		//To compute a density-connected set of trajectories
	//This should be refactored
		private void expandClusterTrajectories(ArrayList<Trajectory> queue, int clusterId,
				float eNeighborhoodParameter, int minLins, ArrayList<Trajectory> trajectoriesCompleteSet) {
			// TODO Auto-generated method stub
			while(!queue.isEmpty())
			{
				ArrayList<Trajectory> neighborhood = computeENeighborhoodOfTrajectory(queue.get(0), eNeighborhoodParameter, trajectoriesCompleteSet);
				
				if(Math.abs(neighborhood.size())>= minLins)
				{
					for(Trajectory t: neighborhood)
					{
						if(t.getTrajectoryId()==182)
						{
							//if element not in queue
							if(!queue.contains(t))
							{
							//queue.add(t);
							}
						}
						
						
						
						if(!t.isClassified() || t.isNoise())
						{
							//Cluster c = new Cluster(clusterId, "Cluster " + clusterId);
							ClusterTrajectories cTemp =  (ClusterTrajectories) setOfClusters.get(clusterId);
							t.setClassified(true);
							
							//This was before refactoring
							cTemp.addTrajectory(t);
							
							//Now it should be more generalized
							
							//setOfClusters.add(cTemp);
							setOfClusters.set(clusterId, cTemp);
						}
						
						if(t.getTrajectoryId()==182)
						{
							//if element not in queue
							if(!queue.contains(t))
							{
							//queue.add(t);
							}
						}
						
						
						//According to my analysis this code is useless
						/*
						//Check this part
						if(!t.isClassified())
						{
							//if element not in queue
							if(!queue.contains(t))
							{
							queue.add(t);
							}
						}
						*/
					}
				}
				queue.remove(0);
			}
		}


		/**
		 * This method should be taking a trajectory and calculating the neighborhood based in dtw
		 * @param trajectory
		 * @param eNeighborhoodParameter2 = the epsilon, in this case given by DTW
		 * @param trajectoriesCompleteSet = this is actually the set of reduced trajectorues
		 * @return
		 */
	private ArrayList<Trajectory> computeENeighborhoodOfTrajectory(Trajectory t, float eParameter, ArrayList<Trajectory> trajectoriesCompleteSet) 
	{

		ArrayList<Double> dtwValues = new ArrayList<Double>();
		ArrayList<Trajectory> neighborTrajectories = new ArrayList<Trajectory>();
		
		for(Trajectory possibleNeighborTrajectory:trajectoriesCompleteSet)
		{
			double tempDtwDistance = Trajectory.calculateDTWDistance(t, possibleNeighborTrajectory);
			dtwValues.add(tempDtwDistance);
			if(tempDtwDistance <=eParameter)
			{
				if(t!=possibleNeighborTrajectory)
				{
				neighborTrajectories.add(possibleNeighborTrajectory);
				}
			}
		}
		
		Double dtwAverage = calculateMeanDTW(dtwValues);
		//System.out.println(dtwAverage);
		t.setDtwAverage(dtwAverage);
		return neighborTrajectories;
	}

	private Double calculateMeanDTW(ArrayList<Double> dtwValues) {
			// TODO Auto-generated method stub
		double tempDTW = 0;
		for(double d: dtwValues)
		{
			tempDTW = tempDTW + d;
		}
		if(dtwValues!=null && dtwValues.size()>0)
		tempDTW = tempDTW/dtwValues.size();
			
		return tempDTW;
		}

	public double getEpsilonDouglasPeucker() {
		return epsilonDouglasPeucker;
	}

	public void setEpsilonDouglasPeucker(double epsilonDouglasPeucker) {
		this.epsilonDouglasPeucker = epsilonDouglasPeucker;
	}

	public int getFixedNumOfTrajectoryPartitionsDouglas() {
		return fixedNumOfTrajectoryPartitionsDouglas;
	}

	public void setFixedNumOfTrajectoryPartitionsDouglas(
			int fixedNumOfTrajectoryPartitionsDouglas) {
		this.fixedNumOfTrajectoryPartitionsDouglas = fixedNumOfTrajectoryPartitionsDouglas;
	}

	


	
	//Calculate Representative Trajectories.
	//This step is done in each individual cluster, so it is in the cluster class.
	
	

}
