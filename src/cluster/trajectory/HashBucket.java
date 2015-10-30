package cluster.trajectory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;

public class HashBucket implements Comparable<HashBucket> {

	public HashSet<Integer> bucketElements; 
	private BitSet bucketAddress = new BitSet();
	
	public BitSet getBucketAddress() {
		return bucketAddress;
	}

	public void setBucketAddress(BitSet bucketAddress) {
		this.bucketAddress = bucketAddress;
	}

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
	
	public String getBucketAddressAsString()
	{
		String bucketAddressAsString = "";
		
		if(bucketAddress.isEmpty())
		{
			bucketAddressAsString = "0";
		}
		
		for(int i=bucketAddress.length()-1; i>=0; i--)
		{
			int booleanToInt = (bucketAddress.get(i)?1:0);
			bucketAddressAsString = bucketAddressAsString + booleanToInt;
		}
		
		//TODO Enable this output for DEBUG Logs only.
		//System.out.println("BucketAddress as String: " + bucketAddressAsString);
		return bucketAddressAsString;
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
