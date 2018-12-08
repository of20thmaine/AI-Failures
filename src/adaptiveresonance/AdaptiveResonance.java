package adaptiveresonance;

import java.util.ArrayList;
import java.util.List;
import rotation.Circle;

public class AdaptiveResonance {
	
	private List<Cluster> clusters;
	private int validClassification;
	double globalVigilance;
	
	public AdaptiveResonance(double globalVigilance) {
		validClassification = 0;
		clusters = new ArrayList<Cluster>();
		this.globalVigilance = globalVigilance;
	}
	
	public void trainSupervised(Circle f, int label) {
		boolean accepted = false;
		
		for (int i = 0; i < clusters.size(); ++i) {			
			boolean found = clusters.get(i).train(f, label);
			if (found) {
				accepted = true;
			}
		}
		if (!accepted) {
			clusters.add(new Cluster(f.getRepresentation(), label));
		}
	}
	
	public void testSupervised(Circle f, int label) {
		Cluster mostSimilar = null;
		double similarity = 0;
		
		for (int i = 0; i < clusters.size(); ++i) {			
			double c = clusters.get(i).test(f, label);
			
			if (c > similarity) {
				mostSimilar = clusters.get(i);
				similarity = c;
			}
		}
		
		if (mostSimilar != null) {
			if (mostSimilar.label == label) {
				validClassification += 1;
			}
		}
		
	}
	
	public int getNumberClusters() {
		return clusters.size();
	}
	
	public int getSuccesfullyClassified() {
		return validClassification;
	}
	
	
	private class Cluster {
		
		double[][] cluster;
		double vigilance;
		int label;
		
		public Cluster(double[][] cluster, int label) {
			this.cluster = cluster;
			this.label = label;
			vigilance = 0;
		}
		
		public boolean train(Circle f, int fLabel) {
			double c = f.compare(cluster);
			
			if (label == fLabel) {
				if (c > vigilance && c > globalVigilance) {
					return true;
				}
				return false;
			} else {
				if (c > vigilance) {
					vigilance = c;
				}
				return false;
			}
		}
		
		public double test(Circle f, int fLabel) {
			double c = f.compare(cluster);
			
			if (c > vigilance) {
				return c;
			} else {
				return 0;
			}
		}
		
		public void resonate(Circle f, double error) {
			double[][] bin2 = f.getRepresentation();
			for (int i = 0; i < cluster.length; ++i) {
				for (int j = 0; j < cluster[i].length; ++j) {
					cluster[i][j] += ((cluster[i][j] - bin2[i][j]));
				}
			}
		}
	}
	
}
