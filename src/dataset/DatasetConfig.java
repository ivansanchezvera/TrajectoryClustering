package dataset;

public class DatasetConfig {

	private float eNeighborhoodParameter = (float) 27;
	private int minLins = 8;
	private int cardinalityOfClusters = 9;
	private float MLDPrecision = (float) 1;
	private boolean strictSimplification = true;
	private int k = 1;
	private TrajectoryDatasets dataset;
	private int defaultNumPartitions = 7;
	
	public DatasetConfig(TrajectoryDatasets dataset)
	{
		this.dataset = dataset;
		this.minLins = 1;
		this.eNeighborhoodParameter = Float.NEGATIVE_INFINITY;
		this.cardinalityOfClusters = 0;
		this.MLDPrecision = 1;
		this.strictSimplification = false;
		
		switch (dataset) {
		case CROSS:
			this.k = 19;
			this.defaultNumPartitions = 7;
			break;
		case LABOMNI:
			this.k = 19;
			this.defaultNumPartitions = 20;
			break;
		case AUSSIGN:
			this.k = 98;
			this.defaultNumPartitions = 25;
		case GEOLIFE:
			this.k = 19; //TODO Find out what this value should be
			this.defaultNumPartitions = 50;
			break;
		case PENDIGIT:
			this.k = 10;
			this.defaultNumPartitions = 16;
			break;
		case ELK:
			this.k = 13; //TODO Find out what this value should be
			this.defaultNumPartitions = 50;
			this.eNeighborhoodParameter = 27;
			break;
		default:
			break;
		}
	}
}
