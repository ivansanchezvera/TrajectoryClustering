package cluster.trajectory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ApproximatedSetOfCluster {

	ArrayList<HashBucket> possibleClusters = new ArrayList<HashBucket>();
	
	public ApproximatedSetOfCluster() {
		// TODO Auto-generated constructor stub
	}
	
	public ApproximatedSetOfCluster(ApproximatedSetOfCluster approximatedSetOfCluster) {
		// TODO Auto-generated constructor stub
		this.possibleClusters = approximatedSetOfCluster.possibleClusters;
	}
	
	public void pruneApproximatedSetOfClusters(int k)
	{
		Collections.sort(possibleClusters);
		
		if(possibleClusters.size()>k)
		{
			ArrayList<HashBucket> remainingClusters = new ArrayList<HashBucket>(possibleClusters.subList(0, k));
		possibleClusters = remainingClusters;
		}		
	}
	
	public static ApproximatedSetOfCluster mergeApproximatedSetCluster(ApproximatedSetOfCluster a1, ApproximatedSetOfCluster a2, int minElem, float ratio)
	{
		
		ApproximatedSetOfCluster master = a1;
		ApproximatedSetOfCluster slave = a2;
		if(a1.possibleClusters.size()<a2.possibleClusters.size())
		{
			master = a2;
			slave = a1;
		}
		
		ApproximatedSetOfCluster mergedCluster = new ApproximatedSetOfCluster();
		
		
		for(HashBucket hb1:master.possibleClusters)
		{
			if(hb1.bucketElements.size()>0)
			{
				int maxIntersection=-1;
				int indexMaxIntersection=-1;
				for(HashBucket hb2:slave.possibleClusters)
				{
					if(hb2.bucketElements.size()>0)
					{
						HashSet<Integer> intersect = new HashSet<Integer>(hb1.bucketElements); 
						intersect.retainAll(hb2.bucketElements);
						
						//Intersection should be more than half the elements (Set this as parameter ratio)
						HashSet<Integer> union = new HashSet<Integer>(hb1.bucketElements); 
						union.addAll(hb2.bucketElements);
						
						//This values before parametrization, safe to delete now, only here for test
						/*
						float ratio = union.size()/3;
						int minElem = 3;
						*/
						
						if(maxIntersection<intersect.size() && (intersect.size()>ratio && intersect.size()>minElem))
						{
							indexMaxIntersection = slave.possibleClusters.indexOf(hb2);
							maxIntersection = intersect.size();
						}
					}
					
				}
				
				if(maxIntersection>1 && indexMaxIntersection>=0)
				{
					HashBucket mergedBucket = new HashBucket();
					mergedBucket.bucketElements.addAll(hb1.bucketElements);
					mergedBucket.bucketElements.addAll(slave.possibleClusters.get(indexMaxIntersection).bucketElements);
					
					mergedCluster.possibleClusters.add(mergedBucket);
				}else{
					mergedCluster.possibleClusters.add(hb1);
				}
			}
		}
		
		return mergedCluster;
		
	}
}
