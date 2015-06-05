CVRR Trajectory Clustering Dataset
http://cvrr.ucsd.edu/bmorris/datasets/dataset_trajectory_clustering.html
------------------------

The CVRR Trajectory Clustering Dataset provides data for benchmarking trajectory clustering algorithms. The full dataset contains 5 different scenes - there are 3 simulated highway scenes, a real highway, a simulated intersection, and an indoor omni directional camera. The datasets are intended for comparison of trajectory similarity/distance measures and clustering algorithms. The provided contain only spatial information (velocity must be inferred), however, the true clustering labels may take into account speed. 


Dataset Description
------------------------
I5: Highway trajectories in both direction of I5 outside of UCSD. Trajectories are obtained by a simple visual tracker. Units are pixels. The true cluster labeling considers only the lane.  8 Clusters

I5_SIM: Simulated free flow highway scene. Noisy trajectory positions as might come from a GPS receiver. Units are meters.  8 Clusters

I5_SIM2: Simulated highway scene with bimodal speed distribution (slow and fast). Noisy trajectory positions as might come from a GPS receiver. Units are meters. The true cluster labeling only considers the lane of travel. 8 Clusters

I5_SIM3: Simulated highway scene with bimodal speed distribution (slow and fast). Noisy trajectory positions as might come from a GPS receiver. Units are meters. The true cluster labeling considers both the lane and speed. Therefore, a the slow and fast trajectories in a single lane are considered as different clusters.  16 Clusters

CROSS: Simulated four way traffic intersection with various through and turn patterns present. Units are pixels.  19 Clusters

LABOMNI: Trajectories of humans walking through a lab captured using an omni-directional camera. Units are pixels.  15 Clusters


File Description
------------------------
The trajectory dataset is given in Matlab .mat files.  Each .mat file contains two variables

tracks - {Nx1} cell array of N trajectories.  Each trajectory is a [2xT] array of T [x,y] tracking points.

truth - [Nx1] array that specifies the truth cluster label for each trajectory. 


Reference
------------------------
Please use the following citation when using the dataset:

B. T. Morris and M. M. Trivedi, "Learning Trajectory Patterns by Clustering: Experimental Studies and Comparative Evaluation," in Proc. IEEE Inter. Conf. on Computer Vision and Pattern Recog., Maimi, Florida, June. 2009