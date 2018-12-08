package test;

import java.util.ArrayList;
import java.util.List;
import main.Feature;
import mnist.MnistDataReader;
import mnist.MnistMatrix;

public class SimilarityTests {

	public static void main(String[] args) {
		mnistTest3();
//		initialMatrixTest();
	}
	
	public static void mnistTest() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			Feature[] features = new Feature[mnistMatrix.length];
			Feature[] clusters = new Feature[10];
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
//				System.out.println(mnistMatrix[i].getLabel() + "\t" + features[i]);
				
				if (clusters[mnistMatrix[i].getLabel()] == null) {
					clusters[mnistMatrix[i].getLabel()] = features[i];
				} else {
//					clusters[mnistMatrix[i].getLabel()].resonate(features[i], learningRate);
				}
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte", "./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			double accuracy = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
				
				double max = -1;
				int label = -1;
				
				for (int j = 0; j < clusters.length; ++j) {
					double sim = clusters[j].compare(features[i]);
					if (sim > max) {
						max = sim;
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
			Feature[] clusters = new Feature[10];
			
			int count = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
				
				if (clusters[mnistMatrix[i].getLabel()] == null) {
					clusters[mnistMatrix[i].getLabel()] = features[i];
					count++;
				} 
				if (count == 10) {
					break;
				}
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte", "./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			double accuracy = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
				
				double max = -1;
				int label = 0;
				
				for (int j = 0; j < clusters.length; ++j) {
					double sim = clusters[j].compare(features[i]);
					if (sim > max) {
						max = sim;
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
	
	public static void mnistTest3() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			Feature[] features = new Feature[mnistMatrix.length];
			List<Feature> clusters = new ArrayList<Feature>();
			List<Integer> labels = new ArrayList<Integer>();
			
			double learningRate = 0.2;
			double vigilance = 0.80;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
				
				double max = -1;
				int index = -1;;
				int label = -1;
				
				for (int j = 0; j < clusters.size(); ++j) {
					double sim = clusters.get(j).compare(features[i]);
					
					if (sim > max) {
						max = sim;
						index = j;
						label = labels.get(j);
					}
				}
				
				if (max > vigilance && label == mnistMatrix[i].getLabel()) {
					clusters.get(index).resonate(features[i], learningRate);
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
			
//			System.out.println("MNIST Testing Accuracy: " + accuracy / mnistMatrix.length);
//			System.out.println("Cluster count: " + clusters.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
