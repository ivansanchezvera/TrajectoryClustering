package cluster.trajectory;

import java.util.ArrayList;
import java.util.HashSet;

public class HashBucket implements Comparable<HashBucket> {

	public HashSet<Integer> bucketElements; 
	
	public HashBucket() {
		// TODO Auto-generated constructor stub
		this.bucketElements = new HashSet<Integer>();
	}

	public void addElementToBucket(int element)
	{
		bucketElements.add(element);
	}
	
	

	public HashSet<Integer> getBucketElements() {
		return bucketElements;
	}

	public void setBucketElements(HashSet<Integer> bucketElements) {
		this.bucketElements = bucketElements;
	}

	@Override
	public int compareTo(HashBucket otherHB) {
		// TODO Auto-generated method stub
		if(this.getBucketElements().size()==otherHB.getBucketElements().size())
		{
			return 0;
		}else{
			if(this.getBucketElements().size()>otherHB.getBucketElements().size())
			{
				return 1;
			}else{
				return -1;
			}
		}
	}
	
}
