package rotation;

import adaptiveresonance.AdaptiveResonance;
import mnist.MnistDataReader;
import mnist.MnistMatrix;

public class Test {

	public static void main(String[] args) {
		mnistTest5();
	}
	
	public static void mnistTest4() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			Circle[] features = new Circle[mnistMatrix.length];
			Circle[] clusters = new Circle[10];
			int count = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Circle(mnistMatrix[i].getData());
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
				features[i] = new Circle(mnistMatrix[i].getData());

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
			
			System.out.println("MNIST Training Accuracy: " + (double)accuracy / (double)mnistMatrix.length);
			System.out.println("Average Accuracy: " + avgAccuracy / (mnistMatrix.length * 10));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void mnistTest5() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			double globalVigilance = 0.965;
			
			AdaptiveResonance model = new AdaptiveResonance(globalVigilance);
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				model.trainSupervised(new Circle(mnistMatrix[i].getData()), mnistMatrix[i].getLabel());
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte",
														"./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				model.testSupervised(new Circle(mnistMatrix[i].getData()), mnistMatrix[i].getLabel());
			}
			
			System.out.println("MNIST Training Accuracy: " + (double)(model.getSuccesfullyClassified())/(double)mnistMatrix.length);
			System.out.println("Cluster Count: " + model.getNumberClusters());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
