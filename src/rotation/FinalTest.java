package rotation;

import adaptiveresonance.AdaptiveResonance;
import mnist.MnistDataReader;
import mnist.MnistMatrix;

public class FinalTest {

	public static void main(String[] args) {
		mnistTest2();
	}
	
	public static void mnistTest1() {
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
					if (count == 10) {
						break;
					}
				}
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte",
														"./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			double accuracy = 0;
			double avgAccuracy = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());

				int label = -1;
				double max = 0;
				
				for (int j = 0; j < clusters.length; ++j) {
					double v = clusters[j].compare(features[i]);
					avgAccuracy += v;
					if (v > max) {
						label = j;
						max = v;
					}
				}
				
				if (mnistMatrix[i].getLabel() == label) {
					accuracy++;
				}
			}
			
			System.out.println("MNIST Training Accuracy: " + accuracy / mnistMatrix.length);
			System.out.println("Average Accuracy: " + avgAccuracy / (mnistMatrix.length * 10));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void mnistTest2() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			double vigilance = 0.7; double learningRate = 0;
			
			AdaptiveResonance model = new AdaptiveResonance(vigilance, learningRate);
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				model.trainSupervised(new Feature(mnistMatrix[i].getData()), mnistMatrix[i].getLabel());
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte",
														"./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				model.testSupervised(new Feature(mnistMatrix[i].getData()), mnistMatrix[i].getLabel());
			}
			
			System.out.println("MNIST Training Accuracy: " + (model.getSuccesfullyClassified() / mnistMatrix.length));
			System.out.println("Cluster Count: " + model.getNumberClusters());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
