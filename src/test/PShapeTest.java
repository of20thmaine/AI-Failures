package test;

import main.ProcrustesShape;
import mnist.MnistDataReader;
import mnist.MnistMatrix;

public class PShapeTest {

	public static void main(String[] args) {
		mnistTest();
	}
	
	public static void mnistTest() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			ProcrustesShape[] features = new ProcrustesShape[mnistMatrix.length];
			ProcrustesShape[] clusters = new ProcrustesShape[10];
			
			double learningRate = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new ProcrustesShape(mnistMatrix[i].getData());
				System.out.println(mnistMatrix[i].getLabel() + "\t" + features[i]);
				
				if (clusters[mnistMatrix[i].getLabel()] == null) {
					clusters[mnistMatrix[i].getLabel()] = features[i];
				} else {
//					clusters[mnistMatrix[i].getLabel()].resonate(features[i], learningRate);
				}
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte", "./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			double accuracy = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new ProcrustesShape(mnistMatrix[i].getData());
				
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

}
