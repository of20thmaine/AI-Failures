package test;

import java.util.ArrayList;
import java.util.List;

import main.Feature;
import mnist.MnistDataReader;
import mnist.MnistMatrix;

public class SRTest {

	public static void main(String[] args) {
		mnistTest();
	}
	
	public static void mnistTest() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			Feature[] features = new Feature[mnistMatrix.length];
			Feature[] clusters = new Feature[10];
			
			double learningRate = 0.05;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
				
				if (clusters[mnistMatrix[i].getLabel()] == null) {
					clusters[mnistMatrix[i].getLabel()] = features[i];
				} else {
					double nearest = Integer.MAX_VALUE;
					int label = -1;
					
					for (int j = 0; j < clusters.length; ++j) {
						double sim = Math.abs(features[i].getShapeRepresentation() - clusters[j].getShapeRepresentation());
						if (sim < nearest) {
							nearest = sim;
							label = j;
						}
					}
					
					
				}
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte", "./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			double accuracy = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
				
				double nearest = Integer.MAX_VALUE;
				int label = -1;
				
				for (int j = 0; j < clusters.length; ++j) {
					double sim = Math.abs(features[i].getShapeRepresentation() - clusters[j].getShapeRepresentation());
					if (sim < nearest) {
						nearest = sim;
						label = j;
					}
				}
				
				if (mnistMatrix[i].getLabel() == label) {
					accuracy++;
				}
			}
			
			System.out.println("MNIST Training Accuracy: " + accuracy / mnistMatrix.length);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void mnistTest2() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			Feature[] features = new Feature[mnistMatrix.length];
			List<Feature> clusters = new ArrayList<Feature>();
			List<Integer> labels = new ArrayList<Integer>();
			
			double learningRate = 0.01;
			double vigilance = 0.1;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
				
				double max = -1;
				Feature maxF = null;
				int label = -1;
				
				for (int j = 0; j < clusters.size(); ++j) {
					double sim = clusters.get(j).compare(features[i]);
					if (sim > max) {
						max = sim;
						maxF = clusters.get(j);
						label = j;
					}
				}
				
				if (max > vigilance && label == mnistMatrix[i].getLabel()) {
					maxF.resonate(features[i], learningRate);
				} else {
					clusters.add(features[i]);
					labels.add(mnistMatrix[i].getLabel());
				}
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte", "./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			double accuracy = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
				
				double max = -1;
				int label = 0;
				
				for (int j = 0; j < clusters.size(); ++j) {
					double sim = clusters.get(j).compare(features[i]);
					if (sim > max) {
						max = sim;
						label = labels.get(j);
					}
				}
				
				if (mnistMatrix[i].getLabel() == label) {
					accuracy++;
				}
			}
			
			System.out.println("MNIST Training Accuracy: " + accuracy / mnistMatrix.length);
			System.out.println("Cluster count: " + clusters.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
